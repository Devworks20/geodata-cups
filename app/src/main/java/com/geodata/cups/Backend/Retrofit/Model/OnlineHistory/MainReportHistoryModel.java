package com.geodata.cups.Backend.Retrofit.Model.OnlineHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainReportHistoryModel
{
    @SerializedName("DateofDuty")
    @Expose
    private String DateofDuty;

    @SerializedName("TimeofDuty")
    @Expose
    private String TimeofDuty;

    @SerializedName("DateSubmission")
    @Expose
    private String DateSubmission;

    @SerializedName("TimeSubmission")
    @Expose
    private String TimeSubmission;

    @SerializedName("_HCUPSReportTask")
    @Expose
    private List<ProgramHistoryModel> programHistoryModelList;

    public MainReportHistoryModel()
    {
        DateofDuty      = "";
        DateSubmission  = "";
        TimeSubmission  = "";
        TimeofDuty      = "";

        programHistoryModelList = null;
    }

    public String getDateofDuty() {
        return DateofDuty;
    }

    public void setDateofDuty(String dateofDuty) {
        DateofDuty = dateofDuty;
    }

    public String getTimeofDuty() {
        return TimeofDuty;
    }

    public void setTimeofDuty(String timeofDuty) {
        TimeofDuty = timeofDuty;
    }

    public String getDateSubmission() {
        return DateSubmission;
    }

    public void setDateSubmission(String dateSubmission) {
        DateSubmission = dateSubmission;
    }

    public String getTimeSubmission() {
        return TimeSubmission;
    }

    public void setTimeSubmission(String timeSubmission) {
        TimeSubmission = timeSubmission;
    }

    public List<ProgramHistoryModel> getProgramHistoryModelList()
    {
        return programHistoryModelList;
    }

    public void setProgramHistoryModelList(List<ProgramHistoryModel> programHistoryModelList) {
        this.programHistoryModelList = programHistoryModelList;
    }
}
