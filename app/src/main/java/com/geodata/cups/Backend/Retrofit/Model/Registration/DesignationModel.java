package com.geodata.cups.Backend.Retrofit.Model.Registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DesignationModel
{
    @SerializedName("DesignationID")
    @Expose
    private Integer DesignationID;

    @SerializedName("DesignationName")
    @Expose
    private String DesignationName;

    public Integer getDesignationID()
    {
        return DesignationID;
    }

    public void setDesignationID(Integer designationID)
    {
        DesignationID = designationID;
    }

    public String getDesignationName()
    {
        return DesignationName;
    }

    public void setDesignationName(String designationName)
    {
        DesignationName = designationName;
    }
}
