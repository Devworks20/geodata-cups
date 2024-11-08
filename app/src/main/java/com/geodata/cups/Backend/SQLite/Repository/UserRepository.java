package com.geodata.cups.Backend.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.cups.Backend.SQLite.Class.UserClass;
import com.geodata.cups.Backend.SQLite.Main.SQLiteDbContext;

public class UserRepository
{
    private static final String TAG = UserRepository.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "UserID",
                    "Username",
                    "Password",
                    "AccountID",
                    "CompleteName",
                    "Rank",
                    "Suffix",
                    "PoliceDistrict",
                    "PoliceStation",
                    "PolicePrecint",
                    "Email",
                    "MobileNo",
                    "DesignationID",
                    "DesignationTitle",
                    "PicturePath",
                    "Birthdate",
                    "Shift",
                    "ShiftLeader",
                    "StationCommander",
                    "PCPCommander",
                    "PicturePathOffline",
                    "dtAdded",
                    "isActive"

            };

    public static ContentValues setUserValues(UserClass userClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("UserID",              userClass.getUserID());
        cValues.put("Username",            userClass.getUsername());
        cValues.put("Password",            userClass.getPassword());
        cValues.put("AccountID",           userClass.getAccountID());
        cValues.put("CompleteName",        userClass.getCompleteName());
        cValues.put("Rank",                userClass.getRank());
        cValues.put("Suffix",              userClass.getSuffix());
        cValues.put("PoliceDistrict",      userClass.getPoliceDistrict());
        cValues.put("PoliceStation",       userClass.getPoliceStation());
        cValues.put("PolicePrecint",       userClass.getPolicePrecint());
        cValues.put("Email",               userClass.getEmail());
        cValues.put("MobileNo",            userClass.getMobileNo());
        cValues.put("DesignationID",       userClass.getDesignationID());
        cValues.put("DesignationTitle",    userClass.getDesignationTitle());
        cValues.put("PicturePath",         userClass.getPicturePath());
        cValues.put("PicturePathOffline",  userClass.getPicturePathOffline());
        cValues.put("Birthdate",           userClass.getBirthdate());
        cValues.put("Shift",               userClass.getShift());
        cValues.put("ShiftLeader",         userClass.getShiftLeader());
        cValues.put("StationCommander",    userClass.getStationCommander());
        cValues.put("PCPCommander",        userClass.getPCPCommander());
        cValues.put("dtAdded",             userClass.getDtAdded());
        cValues.put("isActive",            userClass.getIsActive());

        return  cValues;
    }

    public static ContentValues setUserUpdateValues(UserClass userClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("AccountID",         userClass.getAccountID());
        cValues.put("CompleteName",      userClass.getCompleteName());
        cValues.put("Rank",              userClass.getRank());
        cValues.put("Suffix",            userClass.getSuffix());
        cValues.put("PoliceDistrict",    userClass.getPoliceDistrict());
        cValues.put("PoliceStation",     userClass.getPoliceStation());
        cValues.put("PolicePrecint",     userClass.getPolicePrecint());
        cValues.put("Email",             userClass.getEmail());
        cValues.put("MobileNo",          userClass.getMobileNo());
        cValues.put("DesignationID",     userClass.getDesignationID());
        cValues.put("DesignationTitle",  userClass.getDesignationTitle());
        cValues.put("PicturePath",       userClass.getPicturePath());
        cValues.put("Birthdate",         userClass.getBirthdate());
        cValues.put("Shift",             userClass.getShift());
        cValues.put("ShiftLeader",       userClass.getShiftLeader());
        cValues.put("StationCommander",  userClass.getStationCommander());
        cValues.put("PCPCommander",      userClass.getPCPCommander());

        return  cValues;
    }

    public static Cursor realAllData(Context context)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_USER, allColumns, null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String Username)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_USER, allColumns, "Username=?", new String[]{Username}, null, null, null);
    }

    public static void createUser(UserClass userClass, Context context)
    {
        ContentValues cValues = setUserValues(userClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_NAME_CUPS_USER, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateUser(Context context, UserClass userClass, String username)
    {
        ContentValues cv = setUserUpdateValues(userClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NAME_CUPS_USER, cv, "username=?",new String[]{username});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateUserPicturePathOffline(Context context, String PicturePathOffline, String Username)
    {
        ContentValues cv = new ContentValues();
        cv.put("PicturePathOffline", PicturePathOffline);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NAME_CUPS_USER, cv, "Username=?",new String[]{Username});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateUserNewPassword(Context context, String NewPassword, String Username)
    {
        ContentValues cv = new ContentValues();
        cv.put("Password", NewPassword);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NAME_CUPS_USER, cv, "Username=?",new String[]{Username});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void removeUser(Context context)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_NAME_CUPS_USER, null, null);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}
