package com.geodata.cups.Backend.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.geodata.cups.Backend.SQLite.Repository.AttachmentRepository;
import com.geodata.cups.Backend.SQLite.Repository.ProgramRepository;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RemoveSavedHistory
{
    private static final String TAG = RemoveSavedHistory.class.getSimpleName();

    public void remove(Context context, String AccountID)
    {
        try
        {
            Date now = new Date(System.currentTimeMillis());

            Calendar c = Calendar.getInstance();
            c.setTime(now);
            c.add(Calendar.DATE, -7);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            String DateOfDuty = dateFormat.format(c.getTime());

            Cursor cursor = ProgramRepository.retrieveMainSubmittedReport3(context, AccountID);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    do
                    {
                        String dtAdded = cursor.getString(cursor.getColumnIndex("dtAdded"));

                       // Log.e(TAG,  "Date Added: " + dtAdded + " : Date Validate: " + DateOfDuty);

                        if (stringToDate(dtAdded).compareTo(stringToDate(DateOfDuty)) <= 0)
                        {
                            Log.e(TAG,  "History Deleted!");

                            String ID          =  cursor.getString(cursor.getColumnIndex("ID"));
                            String ActivityID  =  cursor.getString(cursor.getColumnIndex("ActivityID"));

                            ProgramRepository.removeAllProgramSynced(context, ID);

                            AttachmentRepository.removeAllAttachmentSynced(context, ActivityID, dtAdded);
                        }
                    }
                    while (cursor.moveToNext());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    //Convert String to Date
    private Date stringToDate(String aDate)
    {
        if(aDate==null) return null;

        ParsePosition pos = new ParsePosition(0);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpledateformat = new SimpleDateFormat("MMMM dd, yyyy");

        return simpledateformat.parse(aDate, pos);
    }
}
