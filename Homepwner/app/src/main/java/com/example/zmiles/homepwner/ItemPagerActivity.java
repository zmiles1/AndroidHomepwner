package com.example.zmiles.homepwner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class ItemPagerActivity extends AppCompatActivity implements ItemFragment.Callbacks
{
    private static final String EXTRA_ITEM_ID = "com.example.zmiles.homepwner.item_id";
    private ViewPager mViewPager;
    private List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_pager);
        UUID itemId = (UUID) getIntent().getSerializableExtra(EXTRA_ITEM_ID);
        mViewPager = (ViewPager) findViewById(R.id.item_view_pager);
        items = ItemList.get(this).getItems();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position)
            {
                Item item = items.get(position);
                return ItemFragment.newInstance(item.getId());
            }

            @Override
            public int getCount()
            {
                return items.size();
            }
        });
        for(int index = 0; index < items.size(); index++)
        {
            if(items.get(index).getId().equals(itemId))
            {
                mViewPager.setCurrentItem(index);
                break;
            }
        }
    }

    public static Intent newIntent(Context packageContext, UUID itemId)
    {
        Intent intent = new Intent(packageContext, ItemPagerActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    public void onItemUpdated(Item item)
    {

    }
}
