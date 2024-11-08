package com.geodata.cups.Backend.Retrofit.Model.Registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PolicePrecintModel
{
    @SerializedName("PolicePrecintID")
    @Expose
    private Integer PolicePrecintID;

    @SerializedName("PolicePrecintName")
    @Expose
    private String PolicePrecintName;

    public Integer getPolicePrecintID()
    {
        return PolicePrecintID;
    }

    public void setPolicePrecintID(Integer policePrecintID)
    {
        PolicePrecintID = policePrecintID;
    }

    public String getPolicePrecintName()
    {
        return PolicePrecintName;
    }

    public void setPolicePrecintName(String policePrecintName)
    {
        PolicePrecintName = policePrecintName;
    }
}
