package com.geodata.cups.Backend.Retrofit.Model.Registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuffixModel
{
    @SerializedName("SuffixID")
    @Expose
    private Integer SuffixID;

    @SerializedName("SuffixName")
    @Expose
    private String SuffixName;

    public Integer getSuffixID()
    {
        return SuffixID;
    }

    public void setSuffixID(Integer suffixID)
    {
        SuffixID = suffixID;
    }

    public String getSuffixName()
    {
        return SuffixName;
    }

    public void setSuffixName(String suffixName)
    {
        SuffixName = suffixName;
    }
}
