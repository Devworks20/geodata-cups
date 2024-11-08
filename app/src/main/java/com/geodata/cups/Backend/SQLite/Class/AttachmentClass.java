package com.geodata.cups.Backend.SQLite.Class;

public class AttachmentClass
{
    private String ID;
    private String ActivityID;
    private String FilePath;
    private String FileName;
    private String FileExtension;
    private String FileSize;
    private String DateOfDuty;
    private String dtAdded;
    private String isActive;
    private String ReportTaskID;
    private String isSynced;
    private String AttachmentDisable;

    public AttachmentClass()
    {
        ID                = "";
        ActivityID        = "";
        FilePath          = "";
        FileName          = "";
        FileExtension     = "";
        FileSize          = "";
        DateOfDuty        = "";
        dtAdded           = "";
        isActive          = "1";
        ReportTaskID      = "";
        isSynced          = "0";
        AttachmentDisable = "NO";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String activityID) {
        ActivityID = activityID;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileExtension() {
        return FileExtension;
    }

    public void setFileExtension(String fileExtension) {
        FileExtension = fileExtension;
    }

    public String getFileSize() {
        return FileSize;
    }

    public void setFileSize(String fileSize) {
        FileSize = fileSize;
    }

    public String getDateOfDuty() {
        return DateOfDuty;
    }

    public void setDateOfDuty(String dateOfDuty) {
        DateOfDuty = dateOfDuty;
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

    public String getReportTaskID() {
        return ReportTaskID;
    }

    public void setReportTaskID(String reportTaskID) {
        ReportTaskID = reportTaskID;
    }

    public String getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(String isSynced) {
        this.isSynced = isSynced;
    }

    public String getAttachmentDisable() {
        return AttachmentDisable;
    }

    public void setAttachmentDisable(String attachmentDisable) {
        AttachmentDisable = attachmentDisable;
    }
}
