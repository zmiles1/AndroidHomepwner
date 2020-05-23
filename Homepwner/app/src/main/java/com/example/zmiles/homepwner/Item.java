package com.example.zmiles.homepwner;

import java.util.Date;
import java.util.UUID;

public class Item
{
    private UUID id;
    private String name;
    private double value;
    private String serial;
    private Date date;

    public Item()
    {
        id = UUID.randomUUID();
        date = new Date();

    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPhotoFilename()
    {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
