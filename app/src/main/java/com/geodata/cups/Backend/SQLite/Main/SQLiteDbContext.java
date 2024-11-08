package com.geodata.cups.Backend.SQLite.Main;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteDbContext extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "CUPS.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME_CUPS_USER                    = "CUPS_User";
    public static final String TABLE_NAME_CUPS_PROGRAM                 = "CUPS_Program";
    public static final String TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST = "CUPS_Program_Attachment_List";
    public static final String TABLE_NAME_CUPS_RANK_LIST               = "CUPS_Rank_List";
    public static final String TABLE_NAME_CUPS_DESIGNATION_LIST        = "CUPS_Designation_List";
    public static final String TABLE_NAME_CUPS_SUFFIX_LIST             = "CUPS_Suffix_List";
    public static final String TABLE_NAME_POLICE_DISTRICT_LIST         = "CUPS_District_List";
    public static final String TABLE_NAME_POLICE_STATION_LIST          = "CUPS_Police_Station_List";
    public static final String TABLE_NAME_POLICE_PRECINT_LIST          = "CUPS_Police_Precint_List";

    public SQLiteDbContext(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteDbContext(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_CUPS_USER);
        db.execSQL(CREATE_TABLE_CUPS_PROGRAM);
        db.execSQL(CREATE_TABLE_CUPS_PROGRAM_ATTACHMENT_LIST);
        db.execSQL(CREATE_TABLE_RANK_LIST);
        db.execSQL(CREATE_TABLE_DESIGNATION_LIST);
        db.execSQL(CREATE_TABLE_SUFFIX_LIST);
        db.execSQL(CREATE_TABLE_POLICE_DISTRICT_LIST);
        db.execSQL(CREATE_TABLE_POLICE_STATION_LIST);
        db.execSQL(CREATE_TABLE_POLICE_PRECINT_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CUPS_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CUPS_PROGRAM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CUPS_RANK_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CUPS_DESIGNATION_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CUPS_SUFFIX_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_POLICE_DISTRICT_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_POLICE_STATION_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_POLICE_PRECINT_LIST);

        onCreate(db);
    }

    private static final String CREATE_TABLE_CUPS_USER =
            "CREATE TABLE " + TABLE_NAME_CUPS_USER +
            "("                                             +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, "        +
            "UserID VARCHAR, "                              +
            "Username VARCHAR, "                            +
            "Password VARCHAR, "                            +
            "AccountID VARCHAR, "                           +
            "CompleteName VARCHAR, "                        +
            "Rank VARCHAR, "                                +
            "Suffix VARCHAR, "                              +
            "PoliceDistrict VARCHAR, "                      +
            "PoliceStation VARCHAR, "                       +
            "PolicePrecint VARCHAR, "                       +
            "Email VARCHAR, "                               +
            "MobileNo VARCHAR, "                            +
            "DesignationID VARCHAR, "                       +
            "DesignationTitle VARCHAR, "                    +
            "PicturePath VARCHAR, "                         +
            "Birthdate VARCHAR, "                           +
            "Shift VARCHAR, "                               +
            "ShiftLeader VARCHAR, "                         +
            "StationCommander VARCHAR, "                    +
            "PCPCommander VARCHAR, "                        +
            "PicturePathOffline VARCHAR, "                  +
            "dtAdded VARCHAR, "                             +
            "isActive VARCHAR "                             +
            ")";

    private static final String CREATE_TABLE_CUPS_PROGRAM =
            "CREATE TABLE " + TABLE_NAME_CUPS_PROGRAM       +
            "("                                             +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, "        +
            "DisplayID VARCHAR, "                           +
            "ProgramID VARCHAR, "                           +
            "ProgramTitle VARCHAR, "                        +
            "Activity VARCHAR, "                            +
            "Output VARCHAR, "                              +
            "AccountID VARCHAR, "                           +
            "ActivityID VARCHAR, "                          +
            "Remarks VARCHAR, "                             +
            "DateOfDuty VARCHAR, "                          +
            "DateSubmitted VARCHAR, "                       +
            "dtAdded VARCHAR, "                             +
            "isActive VARCHAR, "                            +
            "isCheckedValue VARCHAR, "                      +
            "isSynced VARCHAR "                             +
            ")";

    private static final String CREATE_TABLE_CUPS_PROGRAM_ATTACHMENT_LIST =
            "CREATE TABLE " + TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST +
                    "("                                               +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, "          +
                    "ActivityID VARCHAR, "                            +
                    "FilePath VARCHAR, "                              +
                    "FileName VARCHAR, "                              +
                    "FileExtension VARCHAR, "                         +
                    "FileSize VARCHAR, "                              +
                    "DateOfDuty VARCHAR, "                            +
                    "dtAdded VARCHAR, "                               +
                    "isActive VARCHAR, "                              +
                    "ReportTaskID VARCHAR, "                          +
                    "isSynced VARCHAR "                               +
                    ")";

    private static final String CREATE_TABLE_RANK_LIST =
            "CREATE TABLE " + TABLE_NAME_CUPS_RANK_LIST      +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "RankID VARCHAR, "                       +
                    "RankName VARCHAR "                      +
                    ")";

    private static final String CREATE_TABLE_DESIGNATION_LIST =
            "CREATE TABLE " + TABLE_NAME_CUPS_DESIGNATION_LIST +
                    "("                                        +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, "   +
                    "DesignationID VARCHAR, "                  +
                    "DesignationName VARCHAR "                 +
                    ")";


    private static final String CREATE_TABLE_SUFFIX_LIST =
            "CREATE TABLE " + TABLE_NAME_CUPS_SUFFIX_LIST     +
                    "("                                       +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                    "SuffixID VARCHAR, "                      +
                    "SuffixName VARCHAR "                     +
                    ")";

    private static final String CREATE_TABLE_POLICE_DISTRICT_LIST =
            "CREATE TABLE " + TABLE_NAME_POLICE_DISTRICT_LIST +
                    "("                                       +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                    "PoliceDistrictID VARCHAR, "              +
                    "PoliceDistrictName VARCHAR "             +
                    ")";

    private static final String CREATE_TABLE_POLICE_STATION_LIST =
            "CREATE TABLE " + TABLE_NAME_POLICE_STATION_LIST +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "PoliceStationID VARCHAR, "              +
                    "PoliceStationName VARCHAR "             +
                    ")";

    private static final String CREATE_TABLE_POLICE_PRECINT_LIST =
            "CREATE TABLE " + TABLE_NAME_POLICE_PRECINT_LIST +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "PolicePrecintID VARCHAR, "              +
                    "PolicePrecintName VARCHAR "             +
                    ")";

}
