package com.geodata.cups.Backend.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.cups.Backend.SQLite.Class.AttachmentClass;
import com.geodata.cups.Backend.SQLite.Main.SQLiteDbContext;

public class AttachmentRepository
{
    private static final String TAG = AttachmentRepository.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "ActivityID",
                    "FilePath",
                    "FileName",
                    "FileExtension",
                    "FileSize",
                    "DateOfDuty",
                    "dtAdded",
                    "isActive",
                    "ReportTaskID",
                    "isSynced"
            };

    public static ContentValues setContentValues(AttachmentClass attachmentClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("ActivityID",    attachmentClass.getActivityID());
        cValues.put("FilePath",      attachmentClass.getFilePath());
        cValues.put("FileName",      attachmentClass.getFileName());
        cValues.put("FileExtension", attachmentClass.getFileExtension());
        cValues.put("FileSize",      attachmentClass.getFileSize());
        cValues.put("DateOfDuty",    attachmentClass.getFileSize());
        cValues.put("dtAdded",      attachmentClass.getDtAdded());
        cValues.put("isActive",      attachmentClass.getIsActive());
        cValues.put("ReportTaskID",  attachmentClass.getReportTaskID());
        cValues.put("isSynced",      attachmentClass.getIsSynced());

        return cValues;
    }

    public static Cursor retrieveMainAttachment(Context context, String ActivityID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST, allColumns,
                "ActivityID=?", new String[]{ActivityID}, null, null, null);
    }

    public static Cursor retrieveAttachment(Context context, String ActivityID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST, allColumns,
                "ActivityID=? AND isSynced=?", new String[]{ActivityID, "0"}, null, null, null);
    }

    public static Cursor retrieveAttachment2(Context context, String FileName)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST, allColumns,
                "FileName=? AND isSynced=?", new String[]{FileName, "1"}, null, null, null);
    }

    public static void createAttachment(Context context, AttachmentClass attachmentClass)
    {
        ContentValues contentValues = setContentValues(attachmentClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST, null, contentValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateAttachment(Context context, String ID, String ReportTaskID)
    {
        ContentValues cv = new ContentValues();
        cv.put("ReportTaskID",  ReportTaskID);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST, cv, "ID=?", new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateStatusAttachment(Context context, String ID, String DateOfDuty, String dtAdded)
    {
        ContentValues cv = new ContentValues();
        cv.put("isSynced", "1");
        cv.put("DateOfDuty", DateOfDuty);
        cv.put("dtAdded", dtAdded);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST, cv, "ID=?", new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void removeAttachment(Context context, String ID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST, "ID=?", new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void removeAllAttachmentSynced(Context context, String ActivityID, String dtAdded)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST,
                    "isSynced=? AND ActivityID=? AND dtAdded=?", new String[]{"1", ActivityID, dtAdded});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void removeCancelAttachment(Context context, String ActivityID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_NAME_CUPS_PROGRAM_ATTACHMENT_LIST, "ActivityID=? AND isSynced=?", new String[]{ActivityID, "0"});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }
}
