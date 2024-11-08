package com.geodata.cups.Backend.Retrofit.Model.Registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegistrationResponseModel
{
    @SerializedName("AccountID")
    @Expose
    private String AccountID;

    @SerializedName("Username")
    @Expose
    private String Username;

    @SerializedName("Password")
    @Expose
    private String Password;

    @SerializedName("Firstname")
    @Expose
    private String Firstname;

    @SerializedName("Middlename")
    @Expose
    private String Middlename;

    @SerializedName("Completename")
    @Expose
    private String Completename;

    @SerializedName("Lastname")
    @Expose
    private String Lastname;

    @SerializedName("Email")
    @Expose
    private String Email;

    @SerializedName("Mobileno")
    @Expose
    private String Mobileno;

    public String getAccountID() {
        return AccountID;
    }

    public void setAccountID(String accountID) {
        AccountID = accountID;
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

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getMiddlename() {
        return Middlename;
    }

    public void setMiddlename(String middlename) {
        Middlename = middlename;
    }

    public String getCompletename() {
        return Completename;
    }

    public void setCompletename(String completename) {
        Completename = completename;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobileno() {
        return Mobileno;
    }

    public void setMobileno(String mobileno) {
        Mobileno = mobileno;
    }
}
