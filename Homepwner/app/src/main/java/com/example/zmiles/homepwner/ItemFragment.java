package com.example.zmiles.homepwner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ItemFragment extends Fragment {
    private static final String ARG_ITEM_ID = "item_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private Item it;
    private File photoFile;
    private Button date;
    private EditText na;
    private EditText sn;
    private EditText va;
    private Button reportButton;
    private ImageButton photoButton;
    private ImageView photoView;
    private Callbacks callbacks;


    public void onCreate(Bundle savedInstanceState)
    {
         super.onCreate(savedInstanceState);
         UUID itemId = (UUID) getArguments().getSerializable(ARG_ITEM_ID);
         it = ItemList.get(getActivity()).getItem(itemId);
         photoFile = ItemList.get(getActivity()).getPhotoFile(it);
    }

    public interface Callbacks
    {
        void onItemUpdated(Item item);
    }

    public static ItemFragment newInstance(UUID itemId)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_ID, itemId);
        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        callbacks = null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_item, container, false);
        date = (Button) v.findViewById(R.id.date);
        updateDate();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(it.getDate());
                dialog.setTargetFragment(ItemFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        na = (EditText) v.findViewById(R.id.item_name);
        na.setText(it.getName());
        na.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                it.setName(s.toString());
                updateItem();
            }

            public void afterTextChanged(Editable s) {}
        });
        sn = (EditText) v.findViewById(R.id.item_serial);
        sn.setText(it.getSerial());
        sn.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                it.setSerial(s.toString());
                updateItem();
            }

            public void afterTextChanged(Editable s) {}
        });
        va = (EditText) v.findViewById(R.id.item_value);
        String valueString = "" + it.getValue();
        va.setText(valueString);
        va.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                double n = -1;
                try
                {
                    n = Double.parseDouble(s.toString());
                }
                catch(NumberFormatException exception)
                {
                    n = 0;
                }
                it.setValue(n);
                updateItem();
            }

            public void afterTextChanged(Editable s) {}
        });
        reportButton = (Button) v.findViewById(R.id.item_report);
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getItemReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.item_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
        }
    });
        PackageManager packageManager = getActivity().getPackageManager();
        photoButton = (ImageButton) v.findViewById(R.id.item_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = photoFile != null && captureImage.resolveActivity(packageManager) != null;
        photoButton.setEnabled(canTakePhoto);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.zmiles.homepwner.fileprovider", photoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity : cameraActivities)
                {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        photoView = (ImageView) v.findViewById(R.id.item_photo);
        updatePhotoView();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != Activity.RESULT_OK)
        {
            return;
        }
        if(requestCode == REQUEST_DATE)
        {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            it.setDate(date);
            updateItem();
            updateDate();
        }
        else if(requestCode == REQUEST_PHOTO)
        {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.zmiles.homepwner.fileprovider", photoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateItem();
            updatePhotoView();
        }
    }

    private void updateItem()
    {
        callbacks.onItemUpdated(it);
    }

    private void updateDate() {
        String[] d = it.getDate().toString().split(" ");
        String dateString = d[1] + " " + d[2] + ", " + d[5];
        date.setText(dateString);
    }

    private String getItemReport()
    {
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, it.getDate()).toString();
        String iName = it.getName();
        if(iName == null || iName.isEmpty())
        {
            iName = getString(R.string.report_no_name);
        }
        else
        {
            iName = getString(R.string.report_has_name) + iName;
        }
        String iSerial = it.getSerial();
        if(iSerial == null || iSerial.isEmpty())
        {
            iSerial = getString(R.string.report_no_serial);
        }
        else
        {
            iSerial = getString(R.string.report_has_serial) + iSerial;
        }
        String iValue = getString(R.string.report_value) + it.getValue();
        String report = getString(R.string.item_report, iName, dateString, iSerial, iValue);
        return report;
    }

    private void updatePhotoView()
    {
        if(photoFile == null || !photoFile.exists())
        {
            photoView.setImageDrawable(null);
        }
        else
        {
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
            photoView.setImageBitmap(bitmap);
        }
    }
}
