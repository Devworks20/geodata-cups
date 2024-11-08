package com.geodata.cups.Backend.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.geodata.cups.R;
import com.geodata.cups.RecyclerViewAdapter.RVOnlineAttachmentListReport;

import java.io.File;
import java.lang.reflect.Method;

public class TempFileViewActivity extends AppCompatActivity
{
    private static final String TAG = TempFileViewActivity.class.getSimpleName();

    String FilePath, FileExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews()
    {
        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            FilePath      = extras.getString("FilePath");
            FileExtension = extras.getString("FileExtension");

            initFileView(FilePath, FileExtension);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void initFileView(String FilePath, String FileExtension)
    {
        try
        {
            File theFile = new File(FilePath);

            if (theFile.exists())
            {
                Log.e(TAG, "THE FILE EXIST...." + "\n\n" + "FullFilePath: " + FilePath + "\n" + "fileExtension: " + FileExtension );

                boolean isVideo = FileExtension.contains("3gp") || FileExtension.contains("mpg") || FileExtension.contains("mpeg") ||
                                  FileExtension.contains("mpe") || FileExtension.contains("mp4") || FileExtension.contains("avi");

                boolean isJPEG = FileExtension.contains("jpg") || FileExtension.contains("jpeg") || FileExtension.contains("png");

                if (Build.VERSION.SDK_INT >= 22)
                {
                    try
                    {
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);

                        if (theFile.exists())
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW);

                            try
                            {
                                Uri ff = Uri.fromFile(theFile);

                                if (FileExtension.contains("doc") || FileExtension.contains("docx"))
                                {
                                    // Word document
                                    intent.setDataAndType(ff, "application/msword");
                                }
                                else if (FileExtension.contains("pdf"))
                                {
                                    // PDF file
                                    intent.setDataAndType(ff, "application/pdf");
                                }
                                else if (FileExtension.contains("ppt") || FileExtension.contains("pptx"))
                                {
                                    // Powerpoint file
                                    intent.setDataAndType(ff, "application/vnd.ms-powerpoint");
                                }
                                else if (FileExtension.contains("xls") || FileExtension.contains("xlsx"))
                                {
                                    // Excel file
                                    intent.setDataAndType(ff, "application/vnd.ms-excel");
                                }
                                else if (FileExtension.contains("zip"))
                                {
                                    // ZIP file
                                    intent.setDataAndType(ff, "application/zip");
                                }
                                else if (FileExtension.contains("rar"))
                                {
                                    // RAR file
                                    intent.setDataAndType(ff, "application/x-rar-compressed");
                                }
                                else if (FileExtension.contains("rtf"))
                                {
                                    // RTF file
                                    intent.setDataAndType(ff, "application/rtf");
                                }
                                else if (FileExtension.contains("wav") || FileExtension.contains("mp3"))
                                {
                                    // WAV audio file
                                    intent.setDataAndType(ff, "audio/x-wav");
                                }
                                else if (FileExtension.contains("gif"))
                                {
                                    // GIF file
                                    intent.setDataAndType(ff, "image/gif");
                                }
                                else if (isJPEG)
                                {
                                    // JPG file
                                    intent.setDataAndType(ff, "image/jpeg");
                                }
                                else if (FileExtension.contains("txt"))
                                {
                                    // Text file
                                    intent.setDataAndType(ff, "text/plain");
                                }
                                else if (isVideo)
                                {
                                    // Video files
                                    intent.setDataAndType(ff, "video/*");
                                }
                                else
                                {
                                    intent.setDataAndType(ff, "*/*");
                                }

                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("Attachment", "Success");
                                setResult(RESULT_OK, intent);
                                startActivity(intent);

                                finish();
                            }
                            catch (ActivityNotFoundException e)
                            {
                                Log.e(TAG, e.toString());

                                initFinish();
                                Toast.makeText(getApplicationContext(), "No application found which can open the file", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            initFinish();

                            Toast.makeText(getApplicationContext(), "File Corrupted", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG,  e.toString());

                        initFinish();
                        Toast.makeText(getApplicationContext(), "No application found which can open the file", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    if (theFile.exists())
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);

                        try
                        {
                            Uri ff = Uri.fromFile(theFile);

                            if (FileExtension.contains("doc") || FileExtension.contains("docx"))
                            {
                                // Word document
                                intent.setDataAndType(ff, "application/msword");
                            }
                            else if (FileExtension.contains("pdf"))
                            {
                                // PDF file
                                intent.setDataAndType(ff, "application/pdf");
                            }
                            else if (FileExtension.contains("ppt") || FileExtension.contains("pptx"))
                            {
                                // Powerpoint file
                                intent.setDataAndType(ff, "application/vnd.ms-powerpoint");
                            }
                            else if (FileExtension.contains("xls") || FileExtension.contains("xlsx"))
                            {
                                // Excel file
                                intent.setDataAndType(ff, "application/vnd.ms-excel");
                            }
                            else if (FileExtension.contains("zip"))
                            {
                                // ZIP file
                                intent.setDataAndType(ff, "application/zip");
                            }
                            else if (FileExtension.contains("rar"))
                            {
                                // RAR file
                                intent.setDataAndType(ff, "application/x-rar-compressed");
                            }
                            else if (FileExtension.contains("rtf"))
                            {
                                // RTF file
                                intent.setDataAndType(ff, "application/rtf");
                            }
                            else if (FileExtension.contains("wav") || FileExtension.contains("mp3"))
                            {
                                // WAV audio file
                                intent.setDataAndType(ff, "audio/x-wav");
                            }
                            else if (FileExtension.contains("gif"))
                            {
                                // GIF file
                                intent.setDataAndType(ff, "image/gif");
                            }
                            else if (isJPEG)
                            {
                                // JPG file
                                intent.setDataAndType(ff, "image/jpeg");
                            }
                            else if (FileExtension.contains("txt"))
                            {
                                // Text file
                                intent.setDataAndType(ff, "text/plain");
                            }
                            else if (isVideo)
                            {
                                // Video files
                                intent.setDataAndType(ff, "video/*");
                            }
                            else
                            {
                                intent.setDataAndType(ff, "*/*");
                            }

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        catch (ActivityNotFoundException e)
                        {
                            Log.e(TAG, e.toString());

                            initFinish();
                            Toast.makeText(getApplicationContext(), "No application found which can open the file", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        initFinish();

                        Toast.makeText(getApplicationContext(), "File Corrupted", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,  e.toString());

            initFinish();
            Toast.makeText(getApplicationContext(), "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed()
    {
        initFinish();
    }

    private void initFinish()
    {
        Intent dataReturn = new Intent();
        dataReturn.putExtra("Attachment", "Success");
        setResult(RESULT_OK, dataReturn);
        finish();
    }

}