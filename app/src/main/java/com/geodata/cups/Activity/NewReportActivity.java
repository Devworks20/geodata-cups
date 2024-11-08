package com.geodata.cups.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.geodata.cups.Backend.SQLite.Class.ProgramClass;
import com.geodata.cups.Backend.SQLite.Repository.AttachmentRepository;
import com.geodata.cups.Backend.SQLite.Repository.ProgramRepository;
import com.geodata.cups.Backend.Retrofit.Model.Other.TokenModel;
import com.geodata.cups.Backend.Retrofit.Host.APIClient;
import com.geodata.cups.Backend.Retrofit.Interface.APIClientInterface;
import com.geodata.cups.Backend.Tools.VolleyCatch;
import com.geodata.cups.R;
import com.geodata.cups.RecyclerViewAdapter.RVPreviewReport;
import com.geodata.cups.RecyclerViewAdapter.RVNewReport;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewReportActivity extends AppCompatActivity
{
    private static final String TAG = NewReportActivity.class.getSimpleName();

    EditText edt_report_search;
    ImageView iv_back, iv_clear_search;
    Button btn_save_draft, btn_preview_new_cups;
    TextView tv_dateReporting;
    LinearLayout ll_loading;

    List<ProgramClass> programClassList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RVNewReport RVNewReport;

    int request_Code = 100;
    String AccountID, Username, Password, TokenMain, ShiftTimeOfDuty;

    VolleyCatch volleyCatch = new VolleyCatch();
    APIClientInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        initViews();
    }

    private void initViews()
    {
        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            TokenMain   = extras.getString("TokenMain");
            AccountID   = extras.getString("AccountID");
            Username    = extras.getString("Username");
            Password    = extras.getString("Password");
            ShiftTimeOfDuty  = extras.getString("ShiftTimeOfDuty");
        }

        apiInterface = APIClient.getClient().create(APIClientInterface.class);

        edt_report_search    = findViewById(R.id.edt_report_search);
        iv_back              = findViewById(R.id.iv_back);
        iv_clear_search      = findViewById(R.id.iv_clear_search);

        btn_save_draft       = findViewById(R.id.btn_save_draft);
        btn_preview_new_cups = findViewById(R.id.btn_preview_new_cups);

        tv_dateReporting = findViewById(R.id.tv_dateReporting);
        ll_loading       = findViewById(R.id.ll_loading);

        Date now = new Date(System.currentTimeMillis());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String dateTime = dateFormat.format(now);

        tv_dateReporting.setText(dateTime);

        programClassList = new ArrayList<>();
        recyclerView   = findViewById(R.id.rv_reports);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RVNewReport = new RVNewReport(this, programClassList);
        recyclerView.setAdapter(RVNewReport);

        initGetReports();

        initListeners();
    }

    private void initGetReports()
    {
        try
        {
            Cursor cursor = ProgramRepository.retrieveNewReportActive(getApplicationContext());

            if (cursor.getCount() != 0)
            {
                cursor.moveToFirst();

                programClassList.clear();

                do
                {
                   //Log.e(TAG, "TEST FILE NAME: " + cursor.getString(cursor.getColumnIndex("FileName")));

                    ProgramClass cValues = new ProgramClass();

                    cValues.setID(cursor.getString(cursor.getColumnIndex("ID")));
                    cValues.setProgramID(cursor.getString(cursor.getColumnIndex("ProgramID")));
                    cValues.setDisplayID(cursor.getString(cursor.getColumnIndex("DisplayID")));
                    cValues.setProgramTitle(cursor.getString(cursor.getColumnIndex("ProgramTitle")));
                    cValues.setActivity(cursor.getString(cursor.getColumnIndex("Activity")));
                    cValues.setOutput(cursor.getString(cursor.getColumnIndex("Output")));
                    cValues.setRemarks(cursor.getString(cursor.getColumnIndex("Remarks")));
                    cValues.setActivityID(cursor.getString(cursor.getColumnIndex("ActivityID")));
                    cValues.setIsCheckedValue(cursor.getString(cursor.getColumnIndex("isCheckedValue")));
                    cValues.setIsSynced(cursor.getString(cursor.getColumnIndex("isSynced")));

                    programClassList.add(cValues);
                }
                while (cursor.moveToNext());

                RVNewReport.notifyDataSetChanged();
            }
            else
            {
                Toast.makeText(this, "No reports available.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initListeners()
    {
        iv_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initCancelReport();
            }
        });

        btn_preview_new_cups.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initCloseKeyboard(edt_report_search);

               initSubmitConfirmationLayout();
            }
        });

        btn_save_draft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initDraftSave();
            }
        });

        edt_report_search.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                initFilterProgramList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0)
                {
                    if (iv_clear_search.getVisibility() != View.VISIBLE)
                    {
                        iv_clear_search.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    iv_clear_search.setVisibility(View.GONE);
                }
            }
        });

        iv_clear_search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                edt_report_search.setText(null);
            }
        });
    }

    private void initFilterProgramList(String text)
    {
        ArrayList<ProgramClass> filteredList = new ArrayList<>();

        for (ProgramClass item : programClassList)
        {
            if (item.getProgramTitle().toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(item);
            }
            else if (item.getActivity().toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(item);
            }
            else if (item.getOutput().toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(item);
            }
            else if (item.getDisplayID().toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(item);
            }
        }
        RVNewReport.filterCategoryList(filteredList);
    }


    private void initSubmitConfirmationLayout()
    {
        try
        {
            List<ProgramClass> ProgramClasses2;
            ProgramClasses2  = new ArrayList<>();

            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            mBuilder.setCancelable(false);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.item_custom_submission_new_cups_layout, null);

            RecyclerView recyclerView = view.findViewById(R.id.rv_custom_layout);
            ImageView iv_close        = view.findViewById(R.id.iv_close);

            RVPreviewReport RVPreviewReport = new RVPreviewReport(getApplicationContext(), ProgramClasses2);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(RVPreviewReport);
            ProgramClasses2.clear();

            int size = programClassList.size();

            for(int i=0; i < size ; i++)
            {
                ProgramClass programClass = programClassList.get(i);

                if (programClass.getIsCheckedValue().equals("1"))
                {
                    ProgramClass cValues = new ProgramClass();

                    cValues.setID(programClass.getID());
                    cValues.setDisplayID(programClass.getDisplayID());
                    cValues.setProgramID(programClass.getProgramID());
                    cValues.setProgramTitle(programClass.getProgramTitle());
                    cValues.setActivity(programClass.getActivity());
                    cValues.setOutput(programClass.getOutput());
                    cValues.setRemarks(programClass.getRemarks());
                    cValues.setActivityID(programClass.getActivityID());
                    cValues.setIsCheckedValue(programClass.getIsCheckedValue());
                    cValues.setIsSynced(programClass.getIsSynced());

                    ProgramClasses2.add(cValues);
                }
            }
            RVPreviewReport.notifyDataSetChanged();

            mBuilder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    initNotificationSubmit();
                }
            });

            mBuilder.setView(view);

            final AlertDialog dialog = mBuilder.create();

            iv_close.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dialog.dismiss();
                }
            });

            if (ProgramClasses2.size() > 0)
            {
                dialog.show();
            }
            else
            {
                Toast.makeText(this, "Please check any program/activity.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initNotificationSubmit()
    {
        try
        {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewReportActivity.this, R.style.MyAlertDialogStyle);
            alertDialog.setCancelable(false);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_dialog_title, null);
            TextView textView = view.findViewById(R.id.tv_dialog_title);
            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            String sTitle = "Confirm Submission";
            textView.setText(sTitle);

            alertDialog.setCustomTitle(view);
            alertDialog.setMessage("Are you sure you want to submit this report?");

            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });

            alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if (haveNetworkConnection(getApplicationContext()))
                    {
                        if (TokenMain == null || TokenMain.equals(""))
                        {
                            initErrorShow();
                        }
                        else
                        {
                            dialog.dismiss();

                            initSaveData();
                        }
                    }
                    else
                    {
                        dialog.dismiss();

                        Toast.makeText(NewReportActivity.this, "You have no internet connection. try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alertDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initSaveData()
    {
        try
        {
            ll_loading.setVisibility(View.VISIBLE);

            int size = programClassList.size();

            for(int i=0; i < size ; i++)
            {
                ProgramClass programClass = programClassList.get(i);

                if (programClass.getIsCheckedValue().equals("1"))
                {
                    Date now = new Date(System.currentTimeMillis());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, EEEE");
                    String DateOfDuty = dateFormat.format(now);

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMMM dd yyyy hh:mm a");
                    String DateSubmitted  = dateFormat2.format(now);

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat3 = new SimpleDateFormat("MMMM dd, yyyy");
                    String dateNow = dateFormat3.format(now);


                    ProgramClass cValues2 = new ProgramClass();

                    cValues2.setDisplayID(programClass.getDisplayID());
                    cValues2.setProgramID(programClass.getProgramID());
                    cValues2.setProgramTitle(programClass.getProgramTitle());
                    cValues2.setActivity(programClass.getActivity());
                    cValues2.setOutput(programClass.getOutput());
                    cValues2.setAccountID("");
                    cValues2.setActivityID("");
                    cValues2.setRemarks("");
                    cValues2.setActivityID("");
                    cValues2.setDateOfDuty(programClass.getDateOfDuty());
                    cValues2.setDateSubmitted(programClass.getDateSubmitted());
                    cValues2.setIsActive("1");
                    cValues2.setIsCheckedValue("0");
                    cValues2.setIsSynced(programClass.getIsSynced());
                    cValues2.setDtAdded(programClass.getDtAdded());

                    ProgramRepository.updateProgram(getApplicationContext(), cValues2, programClass.getID()); //1st UPDATE


                    ProgramClass cValues = new ProgramClass();

                    cValues.setDisplayID(programClass.getDisplayID());
                    cValues.setProgramID(programClass.getProgramID());
                    cValues.setProgramTitle(programClass.getProgramTitle());
                    cValues.setActivity(programClass.getActivity());
                    cValues.setOutput(programClass.getOutput());
                    cValues.setAccountID(AccountID);
                    cValues.setActivityID(programClass.getActivityID());
                    cValues.setRemarks(programClass.getRemarks());
                    cValues.setDateOfDuty(DateOfDuty);
                    cValues.setDateSubmitted(DateSubmitted);
                    cValues.setIsActive("0");
                    cValues.setIsCheckedValue(programClass.getIsCheckedValue());
                    cValues.setIsSynced(programClass.getIsSynced());
                    cValues.setDtAdded(dateNow);

                    ProgramRepository.createProgram(cValues, getApplicationContext());  //CREATING...
                }
            }

           initPostMainAPI();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initCancelReport()
    {
        final AlertDialog.Builder ADCancel = new AlertDialog.Builder(NewReportActivity.this, R.style.MyAlertDialogStyle);
        ADCancel.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_title, null);
        TextView textView = view.findViewById(R.id.tv_dialog_title);
        String sTitle = "Cancel Report";
        textView.setText(sTitle);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        ADCancel.setCustomTitle(view);
        ADCancel.setMessage("Are you sure you want to cancel this report?");

        ADCancel.setNegativeButton("SAVE DRAFT", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

                initDraftSave();
            }
        });

        ADCancel.setPositiveButton("CANCEL ", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

                initCancelSave();
            }
        });
        ADCancel.show();
    }

    private void initDraftSave()
    {
        try
        {
            int size = programClassList.size();

            for(int i=0; i < size ; i++)
            {
                ProgramClass programClass = programClassList.get(i);

                if (programClass.getIsCheckedValue().equals("1"))
                {
                    ProgramClass cValues = new ProgramClass();

                    cValues.setID(programClass.getID());
                    cValues.setDisplayID(programClass.getDisplayID());
                    cValues.setProgramID(programClass.getProgramID());
                    cValues.setProgramTitle(programClass.getProgramTitle());
                    cValues.setActivity(programClass.getActivity());
                    cValues.setOutput(programClass.getOutput());
                    cValues.setRemarks(programClass.getRemarks());
                    cValues.setActivityID(programClass.getActivityID());
                    cValues.setIsCheckedValue(programClass.getIsCheckedValue());
                    cValues.setIsSynced(programClass.getIsSynced());

                    ProgramRepository.updateProgram(getApplicationContext(), cValues, programClass.getID());
                }
            }
            finish();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initCancelSave()
    {
        try
        {
            int size = programClassList.size();

            for(int i=0; i < size ; i++)
            {
                ProgramClass programClass = programClassList.get(i);

                if (programClass.getIsCheckedValue().equals("1"))
                {
                    ProgramClass cv = new ProgramClass();

                    cv.setDisplayID(programClass.getDisplayID());
                    cv.setProgramID(programClass.getProgramID());
                    cv.setProgramTitle(programClass.getProgramTitle());
                    cv.setActivity(programClass.getActivity());
                    cv.setOutput(programClass.getOutput());
                    cv.setRemarks("");
                    cv.setActivityID("");
                    cv.setDateOfDuty("");
                    cv.setDateSubmitted("");
                    cv.setIsActive(programClass.getIsActive());
                    cv.setIsCheckedValue("0");
                    cv.setIsSynced(programClass.getIsSynced());

                    ProgramRepository.updateProgram(getApplicationContext(), cv, programClass.getID());

                    AttachmentRepository.removeCancelAttachment(getApplicationContext(), programClass.getActivityID());
                }
            }
            finish();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


    private void initPostMainAPI()
    {
        Call<Integer> postMain = apiInterface.PostMain(TokenMain, AccountID);

        postMain.enqueue(new Callback<Integer>()
        {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response)
            {
                ll_loading.setVisibility(View.GONE); // Consolidate visibility change

                if (response.isSuccessful() && response.body() != null)
                {
                    int reportID = response.body();
                    Log.e(TAG, "Post Main Successfully reportID: " + reportID);
                    initPostTaskAPI(reportID); // Continue with the next API call
                }
                else
                {
                    handleResponseError(response.errorBody(), "Post Main");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, Throwable t)
            {
                ll_loading.setVisibility(View.GONE); // Consolidate visibility change
                logError("Post Main Failure: " + t.getMessage());
                initErrorShow(); // Show error UI
            }
        });
    }

    private void handleResponseError(ResponseBody errorBody, String context)
    {
        String errorLog = context + " Failed: " + convertingResponseError(errorBody);
        Log.e(TAG, errorLog);
        volleyCatch.writeToFile(errorLog);
        initErrorShow(); // Show error UI
    }

    private void logError(String errorLog) {
        Log.e(TAG, errorLog);
        volleyCatch.writeToFile(errorLog); // Assuming volleyCatch is a custom logger
    }


    private void initPostTaskAPI(final Integer reportID)
    {
        if (!haveNetworkConnection(this))
        {
            Toast.makeText(this, "No internet connection available.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = ProgramRepository.retrieveNewReportSubmitted(getApplicationContext());

        if (cursor != null && cursor.getCount() > 0)
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    String taskID     = cursor.getString(cursor.getColumnIndex("ID"));
                    String programID  = cursor.getString(cursor.getColumnIndex("ProgramID"));
                    String remarks    = cursor.getString(cursor.getColumnIndex("Remarks"));
                    String activityID = cursor.getString(cursor.getColumnIndex("ActivityID"));

                    try
                    {
                        int parsedProgramID = Integer.parseInt(programID);

                        Call<Integer> postTaskCall = apiInterface.PostTask(TokenMain, reportID, parsedProgramID, remarks);
                        postTaskCall.enqueue(new Callback<Integer>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    int newReportTaskID = response.body();
                                    initUpdateStatusReport("Online", taskID);
                                    processAttachments(activityID, newReportTaskID);
                                }
                                else
                                {
                                    String errorLog = response.isSuccessful() ? "Post Task: Server Response Null" : "Post Task Failed: " +
                                            convertingResponseError(response.errorBody());

                                    Log.e(TAG, errorLog);
                                    volleyCatch.writeToFile(errorLog);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t)
                            {
                                logError("Post Task Failure: " + t.getMessage());
                            }
                        });
                    }
                    catch (NumberFormatException e)
                    {
                        logError("Number format exception: " + e.getMessage());
                    }
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    private void processAttachments(String activityID, int newReportTaskID)
    {
        Cursor attachmentCursor = AttachmentRepository.retrieveAttachment(getApplicationContext(), activityID);

        if (attachmentCursor != null && attachmentCursor.getCount() > 0)
        {
            if (attachmentCursor.moveToFirst())
            {
                do
                {
                    String attachmentID = attachmentCursor.getString(attachmentCursor.getColumnIndex("ID"));
                    String sReportTaskID = attachmentCursor.getString(attachmentCursor.getColumnIndex("ReportTaskID"));
                    String filePath = attachmentCursor.getString(attachmentCursor.getColumnIndex("FilePath"));
                    String fileName = attachmentCursor.getString(attachmentCursor.getColumnIndex("FileName"));
                    String fileExtension = attachmentCursor.getString(attachmentCursor.getColumnIndex("FileExtension"));

                    String fReportTaskID = sReportTaskID.isEmpty() ? String.valueOf(newReportTaskID) : sReportTaskID;

                    initPostAttachmentAPI(attachmentID, fReportTaskID, filePath, fileName, fileExtension);
                }
                while (attachmentCursor.moveToNext());
            }
            attachmentCursor.close();
        }
    }

    private void initPostAttachmentAPI(final String AttachmentID, String fReportTaskID, String FilePath, String FileName, String FileExtension)
    {
        AttachmentRepository.updateAttachment(getApplicationContext(), AttachmentID, fReportTaskID);

        if (!haveNetworkConnection(getApplicationContext()))
        {
            Log.e(TAG, "No internet connection available.");
            return;
        }

        File file = new File(FilePath);
        if (!file.exists())
        {
            Log.e(TAG, "File does not exist: " + FilePath);
            return;
        }

        String CustomizeFileName = "file-" + AccountID + "-" + FileName;
        RequestBody photoContent = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part photo = MultipartBody.Part.createFormData("photo", CustomizeFileName, photoContent);
        RequestBody ReportTaskID = RequestBody.create(MediaType.parse("text/plain"), fReportTaskID);
        RequestBody FileType = RequestBody.create(MediaType.parse("text/plain"), getFileType(FileExtension));

        APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);
        Call<ResponseBody> call = apiInterface.PostAttachment(photo, ReportTaskID, FileType);

        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    updateStatusAttachment(AttachmentID);
                }
                else
                {
                    String errorLog = "Post Attachment Failed: " + (response.isSuccessful() ? "Server Response Null" : convertingResponseError(response.errorBody()));
                    Log.e(TAG, errorLog);
                    volleyCatch.writeToFile(errorLog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t)
            {
                Log.e(TAG, "Post Attachment Failure: " + t.getMessage());
                volleyCatch.writeToFile("Post Attachment Failure: " + t.getMessage());
            }
        });
    }

    private String getFileType(String extension)
    {
        switch (extension.toLowerCase())
        {
            case "pdf":
                return "PDF";
            case "docx":
                return "Docs";
            case "ppt":
            case "pptx":
                return "PPT";
            case "avi":
            case "mpe":
            case "mpeg":
            case "mpg":
            case "3gp":
            case "mp4":
                return "Video";
            case "bmp":
            case "gif":
            case "jpg":
            case "jpeg":
            case "png":
            case "webp":
            case "heic":
            case "heif":
                return "Image";
            default:
                return "File";
        }
    }

    private void updateStatusAttachment(String attachmentID)
    {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, EEEE", Locale.getDefault());
        String DateOfDuty = dateFormat.format(now);

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String dateNow = dateFormat2.format(now);

        AttachmentRepository.updateStatusAttachment(getApplicationContext(), attachmentID, DateOfDuty, dateNow);
    }



    private void initUpdateStatusReport(String condition, String ID)
    {
        try
        {
            if (condition.equals("Online"))
            {
                ProgramRepository.updateStatusReporting(getApplicationContext() , "1", ID);
            }
            else
            {
                ProgramRepository.updateStatusReporting(getApplicationContext() , "0", ID);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    //Network Validation
    private boolean haveNetworkConnection(Context context)
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null)
        {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                haveConnectedWifi = true;
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                haveConnectedMobile = true;
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private String convertingResponseError(ResponseBody responseBody)
    {
        StringBuilder sb = new StringBuilder();

        try
        {
            if (responseBody != null)
            {
                BufferedReader reader;

                reader = new BufferedReader(new InputStreamReader(responseBody.byteStream()));

                String line;
                try
                {
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }
                }
                catch (IOException e)
                {
                    Log.e(TAG, e.toString());
                }
            }
            else
            {
                sb.append("");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            sb.append("");
        }
        return sb.toString();
    }

    @Override
    public void onBackPressed()
    {
        initCancelReport();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_Code)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String sResult = data.getStringExtra("Result");

                if (sResult.equals("Success"))
                {
                    initGetReports();

                    String newText = edt_report_search.getText().toString();
                    initFilterProgramList(newText);
                }
            }
        }
    }

    private void initErrorShow()
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewReportActivity.this, R.style.MyAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();

        alertDialog.setCancelable(false);

        View view = inflater.inflate(R.layout.custom_dialog_title, null);
        TextView textView = view.findViewById(R.id.tv_dialog_title);
        String sTitle = "Oops";

        textView.setText(sTitle);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        alertDialog.setCustomTitle(view);
        alertDialog.setMessage("Sorry, it seems we are experiencing problems. Please check your internet connection or try again in a moment. Thank you.");

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                final Cursor cursor = ProgramRepository.retrieveNewReportSubmitted(getApplicationContext());

                if (cursor.getCount()!=0)
                {
                    cursor.moveToFirst();

                    do
                    {
                        final String ID          = cursor.getString(cursor.getColumnIndex("ID"));
                        final String ActivityID  = cursor.getString(cursor.getColumnIndex("ActivityID"));

                        ProgramRepository.removeProgram(getApplicationContext(), ID);

                        AttachmentRepository.removeCancelAttachment(getApplicationContext(), ActivityID);
                    }
                    while (cursor.moveToNext());
                }

                dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton("Try Again", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

                initRefreshTokenAPI();
            }
        });
        alertDialog.show();
    }

    private void initRefreshTokenAPI()
    {
        try
        {
            if (!haveNetworkConnection(NewReportActivity.this))
            {
                return; // Early exit if no network connection
            }

            Call<TokenModel> callToken = apiInterface.ValidateToken("password", Username, Password);
            callToken.enqueue(new Callback<TokenModel>()
            {
                @Override
                public void onResponse(@NonNull Call<TokenModel> call, @NonNull Response<TokenModel> response)
                {
                    if (!response.isSuccessful())
                    {
                        String errorLog = "Getting Token Failed: " + convertingResponseError(response.errorBody());
                        logAndWriteToFile(errorLog);
                        return;
                    }

                    TokenModel tokenModel = response.body();
                    if (tokenModel == null)
                    {
                        logAndWriteToFile("Getting Token: Server Response Null");
                        return;
                    }

                    TokenMain = "Bearer " + tokenModel.getAccess_token();
                    initPostMainAPI(); // Continue with the main API call
                }

                @Override
                public void onFailure(@NonNull Call<TokenModel> call, @NonNull Throwable t)
                {
                    logAndWriteToFile("Getting Token Failure: " + t.getMessage());
                }
            });
        }
        catch (Exception e)
        {
            logAndWriteToFile("Getting Token: " + e.getMessage());
        }
    }

    private void logAndWriteToFile(String log)
    {
        Log.e(TAG, log);
        volleyCatch.writeToFile(log); // Assuming volleyCatch is a custom logger
    }

    //OTHER FUNCTIONALITY
    private void initCloseKeyboard(EditText editText)
    {
        try
        {
            View view = getCurrentFocus();

            if (view != null)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            editText.clearFocus();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

}