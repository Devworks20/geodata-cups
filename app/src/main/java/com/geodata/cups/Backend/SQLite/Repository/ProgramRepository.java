package com.geodata.cups.Backend.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.cups.Backend.SQLite.Class.ProgramClass;
import com.geodata.cups.Backend.SQLite.Main.SQLiteDbContext;

import java.security.acl.LastOwnerException;

public class ProgramRepository
{
    private static final String TAG = ProgramRepository.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "DisplayID",
                    "ProgramID",
                    "ProgramTitle",
                    "Activity",
                    "Output",
                    "AccountID",
                    "ActivityID",
                    "Remarks",
                    "DateOfDuty",
                    "DateSubmitted",
                    "dtAdded",
                    "isActive",
                    "isCheckedValue",
                    "isSynced"
            };

    public static ContentValues setContentValues(ProgramClass programClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("DisplayID",      programClass.getDisplayID());
        cValues.put("ProgramID",      programClass.getProgramID());
        cValues.put("ProgramTitle",   programClass.getProgramTitle());
        cValues.put("Activity",       programClass.getActivity());
        cValues.put("Output",         programClass.getOutput());
        cValues.put("AccountID",      programClass.getAccountID());
        cValues.put("ActivityID",     programClass.getActivityID());
        cValues.put("Remarks",        programClass.getRemarks());
        cValues.put("DateOfDuty",     programClass.getDateOfDuty());
        cValues.put("DateSubmitted",  programClass.getDateSubmitted());
        cValues.put("dtAdded",        programClass.getDtAdded());
        cValues.put("isActive",       programClass.getIsActive());
        cValues.put("isCheckedValue", programClass.getIsCheckedValue());
        cValues.put("isSynced",       programClass.getIsSynced());

        return  cValues;
    }

    public static void createProgram(ProgramClass programClass, Context context)
    {
        ContentValues contentValues = setContentValues(programClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, null, contentValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateProgram(Context context, ProgramClass programClass, String ID)
    {
        ContentValues cv = setContentValues(programClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);

        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, cv, "ID=?",new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void removeProgram(Context context, String ID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, "ID=?",new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void removeAllProgramSynced(Context context, String ID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, "isSynced=? AND ID=?",new String[]{"1", ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }


    //MAIN REPORTS
    public static Cursor retrieveValidateMainNewReport(Context context, String DisplayID, String ProgramID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, allColumns,
                "isActive=? AND DisplayID=? AND ProgramID=?", new String[]{"1", DisplayID, ProgramID}, null, null, null);
    }

    public static Cursor retrieveMainSubmittedReport(Context context, String dateNow, String AccountID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, allColumns ,
                "isActive=? AND DateOfDuty=? AND AccountID=?", new String[]{"0", dateNow, AccountID}, "DateOfDuty, DateSubmitted", null, null);
    }

    public static Cursor retrieveMainSubmittedReport2(Context context, String dateSubmitted)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, allColumns,
                "isActive=? AND DateSubmitted=?", new String[]{"0", dateSubmitted}, null, null, "ID ASC");
    }

    public static Cursor retrieveMainSubmittedReport3(Context context, String AccountID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, allColumns ,
                "isSynced=? AND AccountID=?", new String[]{"1", AccountID}, null, null, null);
    }


    //NEW REPORTS
    public static Cursor retrieveNewReportActive(Context context)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, allColumns ,
                "isActive=?", new String[]{"1"}, null, null, "ID ASC");
    }

    public static Cursor retrieveNewReportActive(Context context, String ID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, allColumns ,
                "ID=?", new String[]{ID}, null, null, null);
    }

    public static Cursor retrieveNewReportSubmitted(Context context)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, allColumns,
                "isActive=? AND isSynced=? AND isCheckedValue=?", new String[]{"0", "0", "1"}, null, null, null);
    }


    //REMARKS UPDATE
    public static void updateProgramRemarks(Context context, String IsCheckedValue, String Remarks, String ID)
    {
        ContentValues cv = new ContentValues();

        cv.put("isCheckedValue", IsCheckedValue);
        cv.put("Remarks",        Remarks);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, cv, "ID=?",new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    //ActivityID
    public static void updateProgramActivityID(Context context, String ID, String ActivityID)
    {
        ContentValues cv = new ContentValues();

        cv.put("ActivityID", ActivityID);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, cv, "ID=?",new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }


        db.close();
    }

    //ONLINE REPORTING
    public static void updateStatusReporting(Context context, String isSynced,  String ID)
    {
        ContentValues cv = new ContentValues();

        cv.put("isSynced", isSynced);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, cv, "ID=?",new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }


    //BACK-END
    public static Cursor validateInsertProgram(Context context, String ProgramTitle)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, allColumns ,
                "ProgramTitle=? ", new String[]{ProgramTitle}, null, null, null);
    }

    public static void validateUpdatePrograms(ProgramClass programClass, Context context, String ProgramTitle)
    {
        ContentValues cv = setContentValues(programClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM, cv, "ProgramTitle=?",new String[]{ProgramTitle});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}
