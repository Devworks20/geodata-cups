package com.geodata.cups.Backend.SQLite.Class;

public class MainReportClass
{
    private String programID;
    private String dateOfDuty;
    private String dateSubmitted;

    public MainReportClass()
    {
        programID = "";
        dateOfDuty = "";
        dateSubmitted = "";
    }

    public String getProgramID()
    {
        return programID;
    }

    public void setProgramID(String programID)
    {
        this.programID = programID;
    }

    public String getDateOfDuty()
    {
        return dateOfDuty;
    }

    public void setDateOfDuty(String dateOfDuty)
    {
        this.dateOfDuty = dateOfDuty;
    }

    public String getDateSubmitted()
    {
        return dateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted)
    {
        this.dateSubmitted = dateSubmitted;
    }
}
