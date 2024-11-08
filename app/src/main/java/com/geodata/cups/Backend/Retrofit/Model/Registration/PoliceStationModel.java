package com.geodata.cups.Backend.Retrofit.Model.Registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PoliceStationModel
{
    @SerializedName("PoliceStationID")
    @Expose
    private Integer PoliceStationID;

    @SerializedName("PoliceStationName")
    @Expose
    private String PoliceStationName;

    public Integer getPoliceStationID()
    {
        return PoliceStationID;
    }

    public void setPoliceStationID(Integer policeStationID)
    {
        PoliceStationID = policeStationID;
    }

    public String getPoliceStationName()
    {
        return PoliceStationName;
    }

    public void setPoliceStationName(String policeStationName)
    {
        PoliceStationName = policeStationName;
    }
}
