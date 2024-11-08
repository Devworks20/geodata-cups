package com.geodata.cups.Backend.SQLite.Class;

public class UserClass
{
    public  String ID;
    private String UserID;
    private String Username;
    private String Password;
    private String AccountID;
    private String CompleteName;
    private String Rank;
    private String Suffix;
    private String PoliceDistrict;
    private String PoliceStation;
    private String PolicePrecint;
    private String Email;
    private String MobileNo;
    private String DesignationID;
    private String DesignationTitle;
    private String PicturePath;
    private String Birthdate;
    private String Shift;
    private String ShiftLeader;
    private String StationCommander;
    private String PCPCommander;
    private String PicturePathOffline;
    private String dtAdded;
    private String isActive;

    public UserClass()
    {
        ID                 = "";
        UserID             = "";
        Username           = "";
        Password           = "";
        AccountID          = "";
        CompleteName       = "";
        Rank               = "";
        Suffix             = "";
        PoliceDistrict     = "";
        PoliceStation      = "";
        PolicePrecint      = "";
        Email              = "";
        MobileNo           = "";
        DesignationID      = "";
        DesignationTitle   = "";
        PicturePath        = "";
        Birthdate          = "";
        Shift              = "";
        ShiftLeader        = "";
        StationCommander   = "";
        PCPCommander       = "";
        PicturePathOffline = "";
        dtAdded            = "";
        isActive           = "1";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getAccountID() {
        return AccountID;
    }

    public void setAccountID(String accountID) {
        AccountID = accountID;
    }

    public String getCompleteName() {
        return CompleteName;
    }

    public void setCompleteName(String completeName) {
        CompleteName = completeName;
    }

    public String getRank() {
        return Rank;
    }

    public void setRank(String rank) {
        Rank = rank;
    }

    public String getSuffix() {
        return Suffix;
    }

    public void setSuffix(String suffix) {
        Suffix = suffix;
    }

    public String getPoliceDistrict() {
        return PoliceDistrict;
    }

    public void setPoliceDistrict(String policeDistrict) {
        PoliceDistrict = policeDistrict;
    }

    public String getPoliceStation() {
        return PoliceStation;
    }

    public void setPoliceStation(String policeStation) {
        PoliceStation = policeStation;
    }

    public String getPolicePrecint() {
        return PolicePrecint;
    }

    public void setPolicePrecint(String policePrecint) {
        PolicePrecint = policePrecint;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getDesignationID() {
        return DesignationID;
    }

    public void setDesignationID(String designationID) {
        DesignationID = designationID;
    }

    public String getDesignationTitle() {
        return DesignationTitle;
    }

    public void setDesignationTitle(String designationTitle) {
        DesignationTitle = designationTitle;
    }

    public String getPicturePath() {
        return PicturePath;
    }

    public void setPicturePath(String picturePath) {
        PicturePath = picturePath;
    }

    public String getBirthdate() {
        return Birthdate;
    }

    public void setBirthdate(String birthdate) {
        Birthdate = birthdate;
    }

    public String getShift() {
        return Shift;
    }

    public void setShift(String shift) {
        Shift = shift;
    }

    public String getShiftLeader() {
        return ShiftLeader;
    }

    public void setShiftLeader(String shiftLeader) {
        ShiftLeader = shiftLeader;
    }

    public String getStationCommander() {
        return StationCommander;
    }

    public void setStationCommander(String stationCommander) {
        StationCommander = stationCommander;
    }

    public String getPCPCommander() {
        return PCPCommander;
    }

    public void setPCPCommander(String PCPCommander) {
        this.PCPCommander = PCPCommander;
    }

    public String getPicturePathOffline() {
        return PicturePathOffline;
    }

    public void setPicturePathOffline(String picturePathOffline) {
        PicturePathOffline = picturePathOffline;
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
}
