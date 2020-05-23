package com.example.zmiles.homepwner;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemList
{
    public static ItemList sItemList;
    private List<Item> items;
    private Context mContext;

    private ItemList(Context context)
    {
          mContext = context.getApplicationContext();
          items = new ArrayList<Item>();
          Item i = new Item();
          i.setName("Shiny Bear");
          i.setSerial("B933B15A");
          i.setValue(78.0);
          items.add(i);
          i = new Item();
          i.setName("Fluffy Mac");
          i.setSerial("178D1C4A");
          i.setValue(16.0);
          items.add(i);
          i = new Item();
          i.setName("Fluffy Spork");
          i.setSerial("308C9990");
          i.setValue(80.0);
          items.add(i);
    }

    public static ItemList get(Context context)
    {
        if(sItemList == null)
        {
            sItemList = new ItemList(context);
        }
        return sItemList;
    }

    public void addItem(Item i)
    {
        items.add(i);
    }

    public List<Item> getItems()
    {
        return items;
    }

    public Item getItem(UUID id)
    {
        for(Item i : items)
        {
            if(i.getId().equals(id))
            {
                return i;
            }
        }
        return null;
    }

    public File getPhotoFile(Item item)
    {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, item.getPhotoFilename());
    }
}
