package com.geodata.cups.Backend.Retrofit.Model.Registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PoliceDistrictModel
{
    @SerializedName("PoliceDistrictID")
    @Expose
    private Integer PoliceDistrictID;

    @SerializedName("PoliceDistrictName")
    @Expose
    private String PoliceDistrictName;

    public Integer getPoliceDistrictID()
    {
        return PoliceDistrictID;
    }

    public void setPoliceDistrictID(Integer policeDistrictID)
    {
        PoliceDistrictID = policeDistrictID;
    }

    public String getPoliceDistrictName()
    {
        return PoliceDistrictName;
    }

    public void setPoliceDistrictName(String policeDistrictName)
    {
        PoliceDistrictName = policeDistrictName;
    }
}
