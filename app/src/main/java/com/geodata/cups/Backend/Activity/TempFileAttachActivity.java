package com.geodata.cups.Backend.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.geodata.cups.Backend.SQLite.Class.AttachmentClass;
import com.geodata.cups.Backend.SQLite.Repository.AttachmentRepository;
import com.geodata.cups.Backend.SQLite.Repository.ProgramRepository;
import com.geodata.cups.Backend.Tools.FileUtils;
import com.geodata.cups.R;
import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;

public class TempFileAttachActivity extends AppCompatActivity
{
    private static final String TAG = TempFileAttachActivity.class.getSimpleName();

    private static final int PICK_FILE_RESULT_CODE = 1;

    LinearLayout ll_attach_file;

    public static int MY_PERMISSIONS_REQUEST_CODE_STORAGE = 101;

    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_file_attach);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            ID = extras.getString("ID");
        }

        ll_attach_file = findViewById(R.id.ll_attach_file);

        initValidatePermissionFirst();
    }

    private void initValidatePermissionFirst()
    {
        if (ActivityCompat.checkSelfPermission(TempFileAttachActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(TempFileAttachActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(TempFileAttachActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            }, MY_PERMISSIONS_REQUEST_CODE_STORAGE);
        }
        else
        {
            initFileAttach();
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void initFileAttach()
    {
        try
        {
            String[] mimeTypes = {
                    "image/jpeg",
                    "image/bmp",
                    "image/gif",
                    "image/jpg",
                    "image/png",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                    "application/vnd.ms-powerpoint",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                    "text/plain",
                    "application/pdf",
                    "application/zip",
                    "application/x-rar-compressed",
                    "application/rtf",
                    "audio/x-wav",
                    "video/*"

            };

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
            else
            {
                StringBuilder mimeTypesStr = new StringBuilder();

                for (String mimeType : mimeTypes)
                {
                    mimeTypesStr.append(mimeType).append("|");
                }
                intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
            }
            startActivityForResult(Intent.createChooser(intent,"ChooseFile"), PICK_FILE_RESULT_CODE);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == PICK_FILE_RESULT_CODE)
            {
                try
                {
                    Uri resultUri = data.getData();

                    String FilePath = getFileNameByUri(getApplicationContext(), resultUri);

                    if (FilePath != null)
                    {
                        String activityID = "";


                        Cursor cursor = ProgramRepository.retrieveNewReportActive(getApplicationContext(), ID);

                        if (cursor.getCount()!=0)
                        {
                            if (cursor.moveToFirst())
                            {
                                String sActivityID = cursor.getString(cursor.getColumnIndex("ActivityID"));

                                if (sActivityID.equals("")|| sActivityID.equals("null"))
                                {
                                    activityID =  UUID.randomUUID().toString();

                                    ProgramRepository.updateProgramActivityID(getApplicationContext(), ID, activityID);
                                }
                                else
                                {
                                    activityID = sActivityID;
                                }
                            }
                        }

                        File file            = new File(FilePath);
                        String FileName      = file.getName();
                        String fileExtension = FileName.substring(FileName.lastIndexOf(".") + 1);
                        String FileSize      = getFolderSizeLabel(file);

                        AttachmentClass cValues = new AttachmentClass();
                        cValues.setActivityID(activityID);
                        cValues.setFilePath(FilePath);
                        cValues.setFileName(FileName);
                        cValues.setFileExtension(fileExtension);
                        cValues.setFileSize(FileSize);

                        AttachmentRepository.createAttachment(TempFileAttachActivity.this, cValues);

                        Intent dataReturn = new Intent();
                        dataReturn.putExtra("Result", "Success");
                        setResult(RESULT_OK, dataReturn);
                        finish();

                        Toast.makeText(this, "Attached File Success.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        initFileAttachFailed();
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());

                    finish();

                    Toast.makeText(this, "Your phone is not supported yet.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            finish();
        }
    }

    private void initFileAttachFailed()
    {
        try
        {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TempFileAttachActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_dialog_title, null);
            TextView textView = view.findViewById(R.id.tv_dialog_title);
            String sTitle = "Could not find the file";

            textView.setText(sTitle);
            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.mainColor));
            alertDialog.setCustomTitle(view);
            alertDialog.setMessage("Please locate the exact location of the file.");

            alertDialog.setCancelable(true);
            alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    Intent dataReturn = new Intent();
                    dataReturn.putExtra("Result", "Success");
                    setResult(RESULT_OK, dataReturn);
                    finish();

                    Toast.makeText(TempFileAttachActivity.this, "Attaching file canceled.", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    initFileAttach();
                }
            });
            alertDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public static String getFolderSizeLabel(File file)
    {
        long size = getFolderSize(file) / 1024; // Get size and convert bytes into Kb.

        if (size >= 1024)
        {
            return (size / 1024) + "MB";
        }
        else
        {
            return size + "KB";
        }
    }

    public static long getFolderSize(File file)
    {
        long size = 0;

        if (file.isDirectory())
        {
            for (File child : file.listFiles())
            {
                size += getFolderSize(child);
            }
        }
        else
        {
            size = file.length();
        }
        return size;
    }

    private String getFileNameByUri(Context context, Uri uri)
    {
        try
        {
            if (Objects.requireNonNull(uri.getScheme()).compareTo("content") == 0)
            {
                @SuppressLint("Recycle")
                Cursor cursor = context.getContentResolver().query(uri, new String[]{
                        android.provider.MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.Media.ORIENTATION
                }, null, null, null);

                if (cursor != null)
                {
                    cursor.moveToFirst();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    int column_index = cursor.getColumnIndex(filePathColumn[0]);

                    String FilePath = cursor.getString(column_index);

                    if (FilePath != null)
                    {
                        return FilePath;
                    }
                    else
                    {
                        return FileUtils.getPath(context, uri);
                    }
                }
                else
                {
                    return FileUtils.getPath(context, uri);
                }
            }
            else if (uri.getScheme().compareTo("file") == 0)
            {
                File File = new File(new URI(uri.toString()));

                if (File.exists())
                {
                    return File.getAbsolutePath();
                }
                else
                {
                    return FileUtils.getPath(context, uri);
                }
            }
            else
            {
                return FileUtils.getPath(context, uri);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            return FileUtils.getPath(context, uri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_CODE_STORAGE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                initFileAttach();
            }
            else
            {
                initSettingsPrompt(MY_PERMISSIONS_REQUEST_CODE_STORAGE);
            }
        }
        else
        {
            Log.e(TAG, "EMPTY RESULT");
        }
    }

    private void initSettingsPrompt(final int requestCode)
    {
        try
        {
            final AlertDialog.Builder ADSettings = new AlertDialog.Builder(TempFileAttachActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_dialog_title, null);
            TextView textView = view.findViewById(R.id.tv_dialog_title);
            String sTitle = "Permission Request";
            textView.setText(sTitle);
            ADSettings.setCustomTitle(view);
            ADSettings.setMessage("You need to allow the Permission Requests.");
            ADSettings.setCancelable(true);
            ADSettings.setNeutralButton("SETTINGS", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    final Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    getApplicationContext().startActivity(i);
                }
            });
            ADSettings.setNegativeButton("CLOSE", null);
            ADSettings.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, requestCode);
                }
            });
            ADSettings.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        Intent dataReturn = new Intent();
        dataReturn.putExtra("Result", "Failed");
        setResult(RESULT_OK, dataReturn);
        finish();
    }
}