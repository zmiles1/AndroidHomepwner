package com.example.zmiles.homepwner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ItemListFragment extends Fragment
{
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView itemRecyclerView;
    private ItemAdapter iAdapter;
    private boolean subtitleVisible;
    private Callbacks callbacks;

    public interface Callbacks
    {
        void onItemSelected(Item item);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Item i;
        private TextView nameTextView;
        private TextView valueTextView;

        public ItemHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.list_item_item, parent, false));
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            valueTextView = (TextView) itemView.findViewById(R.id.value);
            itemView.setOnClickListener(this);
        }

        public void bind(Item item)
        {
            i = item;
            nameTextView.setText(i.getName());
            String vString = "$" + i.getValue();
            valueTextView.setText(vString);
        }

        public void onClick(View view)
        {
            callbacks.onItemSelected(i);
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder>
    {
        private List<Item> mItems;

        public ItemAdapter(List<Item> items)
        {
            mItems = items;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder itemHolder, int i)
        {
            Item item = mItems.get(i);
            itemHolder.bind(item);
        }

        @Override
        public int getItemCount()
        {
            return mItems.size();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        itemRecyclerView = (RecyclerView) view.findViewById(R.id.item_recycler_view);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(savedInstanceState != null)
        {
            subtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, subtitleVisible);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        callbacks = null;
    }

    public void updateUI()
    {
        ItemList itemList = ItemList.get(getActivity());
        List<Item> items = itemList.getItems();
        if(iAdapter == null)
        {
            iAdapter = new ItemAdapter(items);
            itemRecyclerView.setAdapter(iAdapter);
        }
        else
        {
            iAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_item_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(subtitleVisible)
        {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else
        {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.new_item:
                Item it = new Item();
                ItemList.get(getActivity()).addItem(it);
                updateUI();
                callbacks.onItemSelected(it);
                return true;
            case R.id.show_subtitle:
                subtitleVisible = !subtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle()
    {
        ItemList itemList = ItemList.get(getActivity());
        int itemCount = itemList.getItems().size();
        String subtitle = getString(R.string.subtitle_format, itemCount);
        if(!subtitleVisible)
        {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
}
