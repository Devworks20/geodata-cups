package com.geodata.cups.Backend.Retrofit.Model.Other;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel
{
    @SerializedName("AccountID")
    @Expose
    private String AccountID;

    @SerializedName("CompleteName")
    @Expose
    private String CompleteName;

    @SerializedName("Rank")
    @Expose
    private String Rank;

    @SerializedName("Suffix")
    @Expose
    private String Suffix;

    @SerializedName("PoliceDistrict")
    @Expose
    private String PoliceDistrict;

    @SerializedName("PoliceStation")
    @Expose
    private String PoliceStation;

    @SerializedName("PolicePrecint")
    @Expose
    private String PolicePrecint;

    @SerializedName("Email")
    @Expose
    private String Email;

    @SerializedName("MobileNo")
    @Expose
    private String MobileNo;

    @SerializedName("DesignationID")
    @Expose
    private String DesignationID;

    @SerializedName("DesignationTitle")
    @Expose
    private String DesignationTitle;

    @SerializedName("PicturePath")
    @Expose
    private String PicturePath;

    @SerializedName("Birthdate")
    @Expose
    private String Birthdate;

    @SerializedName("Shift")
    @Expose
    private String Shift;

    @SerializedName("ShiftLeader")
    @Expose
    private String ShiftLeader;

    @SerializedName("StationCommander")
    @Expose
    private String StationCommander;

    @SerializedName("PCPCommander")
    @Expose
    private String PCPCommander;


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
}
