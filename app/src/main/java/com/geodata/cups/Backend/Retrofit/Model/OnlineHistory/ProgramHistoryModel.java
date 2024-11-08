package com.geodata.cups.Backend.Retrofit.Model.OnlineHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProgramHistoryModel
{
    @SerializedName("ProgramDisplayID")
    @Expose
    private String ProgramDisplayID;

    @SerializedName("ProgramTitle")
    @Expose
    private String ProgramTitle;

    @SerializedName("Activity")
    @Expose
    private String Activity;

    @SerializedName("Output")
    @Expose
    private String Output;

    @SerializedName("Remarks")
    @Expose
    private String Remarks;

    @SerializedName("_HCUPSAttachment")
    @Expose
    private List<AttachmentHistoryModel> attachmentHistoryModelList;

    public ProgramHistoryModel()
    {
        ProgramDisplayID = "";
        ProgramTitle     = "";
        Activity         = "";
        Output           = "";
        Remarks          = "";
    }

    public String getProgramDisplayID()
    {
        return ProgramDisplayID;
    }

    public void setProgramDisplayID(String programDisplayID)
    {
        ProgramDisplayID = programDisplayID;
    }

    public String getProgramTitle()
    {
        return ProgramTitle;
    }

    public void setProgramTitle(String programTitle)
    {
        ProgramTitle = programTitle;
    }

    public String getActivity()
    {
        return Activity;
    }

    public void setActivity(String activity)
    {
        Activity = activity;
    }

    public String getOutput()
    {
        return Output;
    }

    public void setOutput(String output)
    {
        Output = output;
    }

    public String getRemarks()
    {
        return Remarks;
    }

    public void setRemarks(String remarks)
    {
        Remarks = remarks;
    }

    public List<AttachmentHistoryModel> getAttachmentHistoryModelList()
    {
        return attachmentHistoryModelList;
    }

    public void setAttachmentHistoryModelList(List<AttachmentHistoryModel> attachmentHistoryModelList)
    {
        this.attachmentHistoryModelList = attachmentHistoryModelList;
    }
}
