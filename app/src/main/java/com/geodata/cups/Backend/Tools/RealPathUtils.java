package com.geodata.cups.Backend.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import androidx.loader.content.CursorLoader;

import java.io.File;
import java.net.URI;
import java.util.Objects;

public class RealPathUtils
{
    private static final String TAG = RealPathUtils.class.getSimpleName();

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri)
    {
        try
        {
            String[] strings = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(contentUri, strings, null, null, null);

            if (cursor != null)
            {
                cursor.moveToFirst();

                int column_index = Objects.requireNonNull(cursor).getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                return cursor.getString(column_index);
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            return null;
        }
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri)
    {
        try
        {
            String[] strings = {MediaStore.Images.Media.DATA};

            CursorLoader cursorLoader = new CursorLoader(context, contentUri, strings, null, null, null);

            Cursor cursor = cursorLoader.loadInBackground();

            if(cursor != null)
            {
                cursor.moveToFirst();

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                return cursor.getString(column_index);
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            return null;
        }
    }

    //This is not working...
    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri)
    {
        String wholeID = DocumentsContract.getDocumentId(uri);

        String id;
        try
        {
             id = wholeID.split(":")[1];  // Split at colon, use second item in the array
        }
        catch (Exception e)
        {
            id = wholeID;
        }

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        try
        {
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            if (cursor != null)
            {
                int column_index = Objects.requireNonNull(cursor).getColumnIndex(column[0]);

                if (column_index != 0)
                {
                    return cursor.getString(column_index);
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            return null;
        }
    }

    public static String elseGetRealPathFromURI(Context context, Uri uri)
    {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor!= null)
        {
            String document_id = null;

            if (cursor.moveToFirst())
            {
                try
                {
                    document_id = cursor.getString(0);
                    document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                }
                catch (Exception e)
                {
                    document_id = cursor.getString(0);
                }
            }
            cursor.close();

            try
            {
                Log.e(TAG, "document_id: " + document_id);

                cursor = context.getContentResolver().query(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);

                if (cursor != null)
                {
                    if (cursor.moveToFirst() && cursor.getCount() >= 1)
                    {
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                        Log.e(TAG, "filePath: " + filePath);

                        cursor.close();

                        return filePath;
                    }
                    else
                    {
                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());

                if (document_id == null || document_id.length() <= 10)
                {
                    return null;
                }
                else
                {
                    return document_id;
                }
            }
        }
        else
        {
            return null;
        }
    }

}