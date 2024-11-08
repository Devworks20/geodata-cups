package com.geodata.cups.Backend.Retrofit.Model.Registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RankModel
{
    @SerializedName("RankID")
    @Expose
    private Integer RankID;

    @SerializedName("RankName")
    @Expose
    private String RankName;

    public Integer getRankID()
    {
        return RankID;
    }

    public void setRankID(Integer rankID)
    {
        RankID = rankID;
    }

    public String getRankName()
    {
        return RankName;
    }

    public void setRankName(String rankName)
    {
        RankName = rankName;
    }
}
