package com.geodata.cups.Backend.Retrofit.Model.Other;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CUPS_FMModel
{
    @SerializedName("DisplayID")
    @Expose
    private String DisplayID;

    @SerializedName("ProgramID")
    @Expose
    private String ProgramID;

    @SerializedName("ProgramTitle")
    @Expose
    private String ProgramTitle;

    @SerializedName("Activity")
    @Expose
    private String Activity;

    @SerializedName("Output")
    @Expose
    private String Output;

    public String getDisplayID() {
        return DisplayID;
    }

    public void setDisplayID(String displayID) {
        DisplayID = displayID;
    }

    public String getProgramID() {
        return ProgramID;
    }

    public void setProgramID(String programID) {
        ProgramID = programID;
    }

    public String getProgramTitle() {
        return ProgramTitle;
    }

    public void setProgramTitle(String programTitle) {
        ProgramTitle = programTitle;
    }

    public String getActivity() {
        return Activity;
    }

    public void setActivity(String activity) {
        Activity = activity;
    }

    public String getOutput() {
        return Output;
    }

    public void setOutput(String output) {
        Output = output;
    }
}
