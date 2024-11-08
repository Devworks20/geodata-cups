package com.geodata.cups.Backend.Retrofit.Model.OnlineHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttachmentHistoryModel
{
    @SerializedName("AttachmentPath")
    @Expose
    private String AttachmentPath;

    @SerializedName("Type")
    @Expose
    private String Type;

    public AttachmentHistoryModel()
    {
        AttachmentPath = "";
        Type           = "";
    }

    public String getAttachmentPath() {
        return AttachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        AttachmentPath = attachmentPath;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
