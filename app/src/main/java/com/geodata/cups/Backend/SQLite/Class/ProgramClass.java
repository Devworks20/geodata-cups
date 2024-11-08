package com.geodata.cups.Backend.SQLite.Class;

public class ProgramClass
{
    private String ID;
    private String DisplayID;
    private String ProgramID;

    private String ProgramTitle;
    private String Activity;
    private String Output;

    private Boolean isChecked;

    private String AccountID;
    private String ActivityID;
    private String Remarks;

    private String DateOfDuty;
    private String DateSubmitted;

    private String dtAdded;
    private String isActive;
    private String isCheckedValue;
    private String isSynced;

    public ProgramClass()
    {
        ID             = "";
        DisplayID      = "";
        ProgramID      = "";

        ProgramTitle   = "";
        Activity       = "";
        Output         = "";

        isChecked      = false;

        AccountID      = "";
        ActivityID     = "";
        Remarks        = "";

        DateOfDuty     = "";
        DateSubmitted  = "";

        dtAdded        = "";
        isActive       = "1";
        isCheckedValue = "0";
        isSynced       = "0";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

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

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getAccountID() {
        return AccountID;
    }

    public void setAccountID(String accountID) {
        AccountID = accountID;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String activityID) {
        ActivityID = activityID;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getDateOfDuty() {
        return DateOfDuty;
    }

    public void setDateOfDuty(String dateOfDuty) {
        DateOfDuty = dateOfDuty;
    }

    public String getDateSubmitted() {
        return DateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted) {
        DateSubmitted = dateSubmitted;
    }

    public String getDtAdded() {
        return dtAdded;
    }

    public void setDtAdded(String dtAdded) {
        this.dtAdded = dtAdded;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getIsCheckedValue() {
        return isCheckedValue;
    }

    public void setIsCheckedValue(String isCheckedValue) {
        this.isCheckedValue = isCheckedValue;
    }

    public String getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(String isSynced) {
        this.isSynced = isSynced;
    }
}
