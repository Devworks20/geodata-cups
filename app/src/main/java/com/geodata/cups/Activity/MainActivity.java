package com.geodata.cups.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.geodata.cups.Backend.Retrofit.Model.Other.UsernameInfo;
import com.geodata.cups.Backend.SQLite.Class.ProgramClass;
import com.geodata.cups.Backend.SQLite.Class.UserClass;
import com.geodata.cups.Backend.SQLite.Repository.UserRepository;
import com.geodata.cups.Backend.Retrofit.Model.OnlineHistory.MainReportHistoryModel;
import com.geodata.cups.Backend.Retrofit.Model.Other.CUPS_FMModel;
import com.geodata.cups.Backend.Retrofit.Model.Other.LoginModel;
import com.geodata.cups.Backend.SQLite.Class.MainReportClass;
import com.geodata.cups.Backend.SQLite.Repository.ProgramRepository;
import com.geodata.cups.Backend.Retrofit.Model.Other.TokenModel;
import com.geodata.cups.Backend.Retrofit.Host.APIClient;
import com.geodata.cups.Backend.Retrofit.Interface.APIClientInterface;
import com.geodata.cups.Backend.Tools.ClearApplicationCache;
import com.geodata.cups.Backend.Tools.EncodeDecodeAES;
import com.geodata.cups.Backend.Tools.RemoveSavedHistory;
import com.geodata.cups.Backend.Tools.VolleyCatch;
import com.geodata.cups.R;
import com.geodata.cups.RecyclerViewAdapter.RVOnlineMainReport;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    Boolean doubleBackToExitPressedOnce = false;
    Boolean enableRefreshMain = true;
    Boolean enableRefreshUser = true;

    CircleImageView profile_image;

    FloatingActionButton fab_add_programs;
    ImageView iv_settings;

    int request_Code = 101;
    int request_Code2 = 202;

    LinearLayout ll_calendar_history;

    TextView tv_calendar_history, tv_no_history, tv_completeName, tv_designationTitle, tv_PoliceStation;
    Calendar iCalendar;

    List<MainReportHistoryModel> mainReportHistoryModelList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RVOnlineMainReport rvOnlineMainReport;

    VolleyCatch volleyCatch = new VolleyCatch();
    RemoveSavedHistory removeSavedHistory = new RemoveSavedHistory();

    String sDateNow, AccountID="", MobileNo, Username, Password, TokenMain, DialogSuccess,
           sPicturePath = "", mCameraFileName, OnlinePicturePath, PicturePathOffline, ShiftTimeOfDuty;

    APIClientInterface apiInterface;

    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    private static final int IMAGE_PICK_GALLERY_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews()
    {
        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            DialogSuccess = extras.getString("DialogSuccess");

            if (DialogSuccess != null && DialogSuccess.equalsIgnoreCase("Show"))
            {
                String message = "Password changed successfully!";
                initSuccessDialog(message);
            }

            //AccountID  = extras.getString("AccountID");
            //TokenMain = extras.getString("TokenMain");
            Username  = extras.getString("Username");
        }


        apiInterface = APIClient.getClient().create(APIClientInterface.class);

        profile_image = findViewById(R.id.profile_image);

        fab_add_programs = findViewById(R.id.fab_add_programs);
        iv_settings   = findViewById(R.id.iv_settings);

        ll_calendar_history = findViewById(R.id.ll_calendar_history);
        tv_calendar_history = findViewById(R.id.tv_calendar_history);
        tv_no_history       = findViewById(R.id.tv_no_history);
        tv_completeName     = findViewById(R.id.tv_completeName);
        tv_designationTitle = findViewById(R.id.tv_designationTitle);
        tv_PoliceStation    = findViewById(R.id.tv_PoliceStation);

        mainReportHistoryModelList = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_history);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        initListeners();

        initSetDates();
    }

    private void initListeners()
    {
        fab_add_programs.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (AccountID != null && !AccountID.equals(""))
                {
                    Intent intent = new Intent(MainActivity.this, NewReportActivity.class);
                    intent.putExtra("TokenMain", TokenMain);
                    intent.putExtra("AccountID", AccountID);
                    intent.putExtra("Username", Username);
                    intent.putExtra("Password", Password);
                    intent.putExtra("ShiftTimeOfDuty", ShiftTimeOfDuty);
                    startActivityForResult(intent, request_Code);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No reports available.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_settings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("TokenMain", TokenMain);
                intent.putExtra("AccountID", AccountID);
                intent.putExtra("Username", Username);
                intent.putExtra("Password", Password);
                startActivity(intent);
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Log.e(TAG, "Day of Month: " + dayOfMonth);

                iCalendar.set(Calendar.YEAR, year);
                iCalendar.set(Calendar.MONTH, monthOfYear);
                iCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MMMM dd, yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                String myFormat2 = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.US);
                String DateReport = sdf2.format(iCalendar.getTime());

                tv_calendar_history.setText(sdf.format(iCalendar.getTime()));

                initOnlineHistoryReport(DateReport);
            }
        };

        ll_calendar_history.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.date_picker_dialog_theme, date,
                        iCalendar.get(Calendar.YEAR),
                        iCalendar.get(Calendar.MONTH),
                        iCalendar.get(Calendar.DATE));

               //  datePickerDialog.getDatePicker().setMaxDate(iCalendar.getTimeInMillis());

                if(!datePickerDialog.isShowing())
                {
                    datePickerDialog.show();
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if (!sPicturePath.equals(""))
                    {
                        String FullPathProfile = sPicturePath + "/Profile.jpg";

                        File file = new File(FullPathProfile);

                        if (file.exists())
                        {
                            initLoadPhoto(FullPathProfile);
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }



    //GETTING REPORTS
    private void initSetDates()
    {
        try
        {
            iCalendar = Calendar.getInstance();

            Date now = new Date(System.currentTimeMillis());

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            String dateTime = dateFormat.format(now);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMMM dd, yyyy, EEEE");
            sDateNow = dateFormat2.format(now);

            tv_calendar_history.setText(dateTime);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
            String DateReport = dateFormat3.format(now);

            initOnlineHistoryReport(DateReport);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    //Optimized
    private void initOnlineHistoryReport(final String DateReport)
    {
        if (!haveNetworkConnection(MainActivity.this))
        {
            return;
        }

        Cursor cursor = UserRepository.realAllData2(getApplicationContext(), Username);

        if (cursor.getCount() == 0 || !cursor.moveToFirst())
        {
            return;
        }

        String Password;

        try
        {
            EncodeDecodeAES aes = new EncodeDecodeAES();
            Password = new String(aes.decrypt(cursor.getString(cursor.getColumnIndex("Password")))).trim();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            Password = cursor.getString(cursor.getColumnIndex("Password")).trim();
        }

        Call<TokenModel> callToken = apiInterface.ValidateToken("password", Username, Password);
        callToken.enqueue(new Callback<TokenModel>()
        {
            @Override
            public void onResponse(@NonNull Call<TokenModel> call, @NonNull Response<TokenModel> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    TokenModel tokenModel = response.body();
                    String TokenMain = "Bearer " + tokenModel.getAccess_token();

                    // Authorization
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Authorization", TokenMain);

                    Call<List<MainReportHistoryModel>> callReportHistory = apiInterface.ReportHistory(headers, AccountID, DateReport);
                    callReportHistory.enqueue(new Callback<List<MainReportHistoryModel>>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<List<MainReportHistoryModel>> call, @NonNull Response<List<MainReportHistoryModel>> response)
                        {
                            if (response.isSuccessful() && response.body() != null)
                            {
                                List<MainReportHistoryModel> historyModels = response.body();

                                if (!historyModels.isEmpty())
                                {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    tv_no_history.setVisibility(View.GONE);

                                    rvOnlineMainReport = new RVOnlineMainReport(MainActivity.this, historyModels);
                                    recyclerView.setAdapter(rvOnlineMainReport);
                                }
                                else
                                {
                                    recyclerView.setVisibility(View.GONE);
                                    tv_no_history.setVisibility(View.VISIBLE);
                                    Toast.makeText(MainActivity.this, "No reports available.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                String Logs = response.isSuccessful() ? "Getting Report History: Server Response Null" :
                                        "Getting Report History Failed: " + convertingResponseError(response.errorBody());

                                Log.e(TAG, Logs);
                                volleyCatch.writeToFile(Logs);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<MainReportHistoryModel>> call, @NonNull Throwable t)
                        {
                            String Logs = "Getting Report History Failure: " + t.getMessage();

                            Log.e(TAG, Logs);
                            volleyCatch.writeToFile(Logs);
                        }
                    });
                }
                else
                {
                    String Logs = response.isSuccessful() ? "Getting Token: Server Response Null" :
                            "Getting Token Failed: " + convertingResponseError(response.errorBody());

                    Log.e(TAG, Logs);
                    volleyCatch.writeToFile(Logs);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TokenModel> call, @NonNull Throwable t)
            {
                String Logs = "Getting Token Failure: " + t.getMessage();
                Log.e(TAG, Logs);
                volleyCatch.writeToFile(Logs);
            }
        });
    }

    private void initGetMainReports(String dateNow)
    {
        try
        {
            Cursor cursor = ProgramRepository.retrieveMainSubmittedReport(getApplicationContext(), dateNow, AccountID);

            if (cursor.getCount()!=0)
            {
                cursor.moveToFirst();

                recyclerView.setVisibility(View.VISIBLE);
                tv_no_history.setVisibility(View.GONE);

                //mainReportClasses.clear();

                do
                {
                    MainReportClass cValues = new MainReportClass();

                    cValues.setProgramID(cursor.getString(cursor.getColumnIndex("ProgramID")));
                    cValues.setDateOfDuty(cursor.getString(cursor.getColumnIndex("DateOfDuty")));
                    cValues.setDateSubmitted(cursor.getString(cursor.getColumnIndex("DateSubmitted")));

                    //mainReportClasses.add(cValues);
                }
                while (cursor.moveToNext());

                //recyclerViewMainReport.notifyDataSetChanged();
            }
            else
            {
                tv_no_history.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            tv_no_history.setVisibility(View.VISIBLE);

            Log.e(TAG,  "ERROR HERE...." + e.toString());
        }
    }



    //UPDATE PROFILE
    @Override
    protected void onResume()
    {
        super.onResume();

        if (enableRefreshMain)
        {
            initSetUIDataOffline();
        }
        else
        {
            enableRefreshMain = true;
        }
    }

    private void initSetUIDataOffline()
    {
        try
        {
            Cursor cursor = UserRepository.realAllData2(getApplicationContext(), Username);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    try
                    {
                        EncodeDecodeAES aes = new EncodeDecodeAES();
                        Password = new String(aes.decrypt(cursor.getString(cursor.getColumnIndex("Password")))).trim();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                        Password =  cursor.getString(cursor.getColumnIndex("Password")).trim();
                    }

                    AccountID                  =  cursor.getString(cursor.getColumnIndex("AccountID"));
                    UsernameInfo.AccountID     = AccountID;
                    String Rank                =  cursor.getString(cursor.getColumnIndex("Rank"));
                    String CompleteName        =  cursor.getString(cursor.getColumnIndex("CompleteName"));
                    String DesignationTitle    =  cursor.getString(cursor.getColumnIndex("DesignationTitle"));
                    String PoliceStation       =  cursor.getString(cursor.getColumnIndex("PoliceStation"));
                    String PolicePrecint       =  cursor.getString(cursor.getColumnIndex("PolicePrecint"));
                    MobileNo                   =  cursor.getString(cursor.getColumnIndex("MobileNo"));
                    OnlinePicturePath          =  cursor.getString(cursor.getColumnIndex("PicturePath"));
                    PicturePathOffline         =  cursor.getString(cursor.getColumnIndex("PicturePathOffline"));
                    ShiftTimeOfDuty            =  cursor.getString(cursor.getColumnIndex("Shift"));

                    String customizeCompleteName  = Rank + " "  + CompleteName;
                    String customizePoliceStation = PoliceStation + "/ " + PolicePrecint;

                    tv_completeName.setText(customizeCompleteName);
                    tv_designationTitle.setText(DesignationTitle);
                    tv_PoliceStation.setText(customizePoliceStation);

                    if (PicturePathOffline!= null && !PicturePathOffline.equals(""))
                    {
                        sPicturePath = PicturePathOffline;
                        initRetrieveProfilePictureOffline(PicturePathOffline);
                    }

                    //REMOVE HISTORY...
                    removeSavedHistory.remove(getApplicationContext(), AccountID);

                    if (enableRefreshUser)
                    {
                        initUpdateUserInfoAPI();
                    }
                }
            }
            cursor.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initRetrieveProfilePictureOffline(String PicturePathOffline)
    {
        try
        {
            if (!PicturePathOffline.equals(""))
            {
                File FileImageDirectory = new File(PicturePathOffline);

                // Create a new folder if no folder name exist
                if (!FileImageDirectory.exists())
                {
                    FileImageDirectory.mkdirs();
                }

                String sProfile = PicturePathOffline + "/Profile.jpg";

                File ProfileImage = new File(sProfile);

                if (ProfileImage.exists())
                {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(ProfileImage.getAbsolutePath(), bmOptions);
                    Glide.with(MainActivity.this).load(bitmap).into(profile_image);
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initUpdateUserInfoAPI()
    {
        try
        {
            if (haveNetworkConnection(MainActivity.this))
            {
                Call<TokenModel> callToken = apiInterface.ValidateToken("password", Username, Password);
                callToken.enqueue(new Callback<TokenModel>()
                {
                    @Override
                    public void onResponse(@NonNull Call<TokenModel> call, @NonNull Response<TokenModel> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            TokenModel tokenModel = response.body();
                            TokenMain = "Bearer " + tokenModel.getAccess_token();

                            // Authorization
                            final  Map<String, String> headers  = new HashMap<>();
                            headers.put("Content-Type", "application/json; charset=UTF-8");
                            headers.put("Authorization", TokenMain);

                            Call<LoginModel> callLogin = apiInterface.LoginUser(headers, Username, Password,"CUPS");
                            callLogin.enqueue(new Callback<LoginModel>()
                            {
                                @Override
                                public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response)
                                {
                                    if (!response.isSuccessful())
                                    {
                                        String errorLog = "Updating User Failed: " + convertingResponseError(response.errorBody());
                                        Log.e(TAG, errorLog);
                                        return;
                                    }
                                    if (response.body() == null)
                                    {
                                        String nullResponseLog = "Updating User Failure: Server Response Null";
                                        Log.e(TAG, nullResponseLog);
                                        volleyCatch.writeToFile(nullResponseLog); // Assuming volleyCatch is a custom logger
                                        return;
                                    }

                                    final LoginModel loginModel = response.body();
                                    if ("0".equals(loginModel.getAccountID()))
                                    {
                                        Log.e(TAG, "Login Access Failed.");
                                        return;
                                    }

                                    Log.e(TAG, "User Account Update: Success");

                                    UserClass userClass = new UserClass();
                                    userClass.setAccountID(loginModel.getAccountID());
                                    userClass.setCompleteName(loginModel.getCompleteName());
                                    userClass.setRank(loginModel.getRank());
                                    userClass.setSuffix(loginModel.getSuffix());
                                    userClass.setPoliceDistrict(loginModel.getPoliceDistrict());
                                    userClass.setPoliceStation(loginModel.getPoliceStation());
                                    userClass.setPolicePrecint(loginModel.getPolicePrecint());
                                    userClass.setEmail(loginModel.getEmail());
                                    userClass.setMobileNo(loginModel.getMobileNo());
                                    userClass.setDesignationID(loginModel.getDesignationID());
                                    userClass.setDesignationTitle(loginModel.getDesignationTitle());
                                    userClass.setPicturePath(loginModel.getPicturePath());
                                    userClass.setBirthdate(loginModel.getBirthdate());
                                    userClass.setShift(loginModel.getShift());
                                    userClass.setShiftLeader(loginModel.getShiftLeader());
                                    userClass.setStationCommander(loginModel.getStationCommander());
                                    userClass.setPCPCommander(loginModel.getPCPCommander());

                                    //Updating to local db
                                    UserRepository.updateUser(getApplicationContext(), userClass, Username);

                                    // UI updates should be run on the main thread if this is a background thread
                                    updateUI(loginModel);

                                    // Load user image
                                    if (loginModel.getPicturePath() != null && !loginModel.getPicturePath().equals(""))
                                    {
                                        if (OnlinePicturePath != null && !OnlinePicturePath.equals(loginModel.getPicturePath()))
                                        {
                                            Log.e(TAG, "IMAGE SET 2");

                                            try
                                            {
                                                Picasso.get()
                                                        .load(loginModel.getPicturePath())
                                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                                        .resize(100, 100)
                                                        .centerCrop()
                                                        .into(profile_image);

                                                Picasso.get()
                                                        .load(loginModel.getPicturePath())
                                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                                        .into(target);
                                            }
                                            catch (Exception e)
                                            {
                                                Log.e(TAG, e.toString());
                                            }
                                        }
                                        else
                                        {
                                            if (sPicturePath.equals(""))
                                            {
                                                Log.e(TAG, "IMAGE SET 3");

                                                try
                                                {
                                                    Picasso.get()
                                                            .load(loginModel.getPicturePath())
                                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                                            .resize(100, 100)
                                                            .centerCrop()
                                                            .placeholder(R.drawable.image_user)
                                                            .error(R.drawable.image_user)
                                                            .into(profile_image);

                                                    Picasso.get()
                                                            .load(loginModel.getPicturePath())
                                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                                            .resize(100, 100)
                                                            .into(target);
                                                }
                                                catch (Exception e)
                                                {
                                                    Log.e(TAG, e.toString());
                                                }
                                            }
                                        }
                                    }

                                    initGETCupsFMAPI(loginModel.getDesignationID());
                                }

                                @Override
                                public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t)
                                {
                                    String failureLog = "Updating User Failure: " + t.getMessage();
                                    handleFailureOrError(failureLog);
                                }

                                private void updateUI(LoginModel loginModel)
                                {
                                    String customizeCompleteName = loginModel.getRank() + " " + loginModel.getCompleteName();
                                    String customizePoliceStation = loginModel.getPoliceStation() + "/ " + loginModel.getPolicePrecint();

                                    tv_completeName.setText(customizeCompleteName);
                                    tv_designationTitle.setText(loginModel.getDesignationTitle());
                                    tv_PoliceStation.setText(customizePoliceStation);
                                }
                            });
                        }
                        else
                        {
                            String error = "Getting Token Failed: " + convertingResponseError(response.errorBody());
                            handleFailureOrError(error);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<TokenModel> call, @NonNull Throwable t)
                    {
                        String error = "Getting Token Failure: " + t.getMessage();
                        handleFailureOrError(error);
                    }
                });
            }
        }
        catch (Exception e)
        {
            String error = "Update Existing User: " + e.getMessage();
            handleFailureOrError(error);
        }
    }

    private void handleFailureOrError(String errorMessage)
    {
        Log.e(TAG, errorMessage);
        volleyCatch.writeToFile(errorMessage);
    }


    private final Target target = new Target()
    {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {
            // Bitmap is loaded, use image here
            createDirectoryAndSaveFile(bitmap);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable)
        {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable)
        {
            Log.e(TAG, "onPrepareLoad");
        }
    };

    //NOT-SURE
    private void initGETCupsFMAPI(String DesignationID)
    {
        if (!haveNetworkConnection(getApplicationContext()))
        {
            // Handle no network connection scenario here if needed
            return;
        }

        // Authorization
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Authorization", TokenMain);

        apiInterface.GetCupsFMList(headers, DesignationID).enqueue(new Callback<List<CUPS_FMModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<CUPS_FMModel>> call, @NonNull Response<List<CUPS_FMModel>> response)
            {
                if (!response.isSuccessful())
                {
                    logError("CUPS FM Failed: " + convertingResponseError(response.errorBody()));
                    return;
                }

                List<CUPS_FMModel> cups_fmModels = response.body();
                if (cups_fmModels == null)
                {
                    logError("CUPS FM: Server Response Null");
                    return;
                }

                for (CUPS_FMModel model : cups_fmModels)
                {
                    try (Cursor cursor = ProgramRepository.retrieveValidateMainNewReport(getApplicationContext(), model.getDisplayID(), model.getProgramID()))
                    {
                        if (cursor != null && cursor.getCount() == 0)
                        {
                            ProgramClass programClass = new ProgramClass();
                            programClass.setDisplayID(model.getDisplayID());
                            programClass.setProgramID(model.getProgramID());
                            programClass.setProgramTitle(model.getProgramTitle());
                            programClass.setActivity(model.getActivity());
                            programClass.setOutput(model.getOutput());

                            ProgramRepository.createProgram(programClass, MainActivity.this);
                        }
                    } // Cursor will be closed automatically here, no need for finally block
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CUPS_FMModel>> call, @NonNull Throwable t)
            {
                logError("CUPS FM Failure: " + t.getMessage());
            }
        });
    }

    private void logError(String errorLog)
    {
        Log.e(TAG, errorLog);
        volleyCatch.writeToFile(errorLog);
    }







    //OTHER FUNCTIONALITY
    private void createDirectoryAndSaveFile(Bitmap imageToSave)
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                    && ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                Log.e(TAG, "Permission is denied");
            }
            else
            {
                try
                {
                    String PicturePathOffline = Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/CUPS/" + ".Profile";

                    sPicturePath = PicturePathOffline;

                    //UPDATING - USER PATH PICTURE OFFLINE
                    UserRepository.updateUserPicturePathOffline(getApplicationContext(), PicturePathOffline, Username);

                    Log.e(TAG, "PROFILE PICTURE CHANGED!");

                    File fileImage = new File(PicturePathOffline);

                    if (!fileImage.exists())
                    {
                        File wallpaperDirectory = new File(PicturePathOffline);
                        wallpaperDirectory.mkdirs();
                    }

                    File file = new File(PicturePathOffline, "Profile");

                    if (file.exists())
                    {
                        file.delete();
                    }

                    try
                    {
                        FileOutputStream out = new FileOutputStream(file + ".jpg");
                        imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Saving Image: " + e.toString());
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Saving Image: " + e.toString());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Saving Image: " + e.toString());
        }
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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_Code && resultCode == Activity.RESULT_OK)
        {
            String Result = data.getStringExtra("Result");

            if (Result != null && Result.equalsIgnoreCase("Draft"))
            {
                Toast.makeText(this, "Draft Report Successfully.", Toast.LENGTH_LONG).show();
            }
            else if (Result != null && Result.equalsIgnoreCase("Send"))
            {
                String message = "Thank you for submitting your\nCUPS Report today!";
                initSuccessDialog(message);

                Date now = new Date(System.currentTimeMillis());
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String DateReport = dateFormat.format(now);
                initOnlineHistoryReport(DateReport);
            }
        }

        if (requestCode == request_Code2 && resultCode == Activity.RESULT_OK)
        {
            String Attachment = data.getStringExtra("Attachment");

            if (Attachment != null && Attachment.equalsIgnoreCase("Success"))
            {
                enableRefreshMain = false;
            }
        }

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                Uri imageUri = null;

                if (data != null)
                {
                    imageUri = data.getData();
                }

                if (imageUri == null && mCameraFileName != null)
                {
                    imageUri = Uri.fromFile(new File(mCameraFileName));

                    try
                    {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getApplicationContext()).getContentResolver(), imageUri);
                        createDirectoryAndSaveFile(bitmap);

                        Glide.with(MainActivity.this).load(bitmap).into(profile_image);
                        Toast.makeText(this, "Profile Picture Changed!", Toast.LENGTH_SHORT).show();

                        initPostRegistrationAttachmentAPI();
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }

                try
                {
                    File file = new File(mCameraFileName);

                    if (!file.exists())
                    {
                        file.mkdir();
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
            else if (requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                Uri imageUri;

                if (data != null)
                {
                    imageUri = data.getData();

                    try
                    {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getApplicationContext()).getContentResolver(), imageUri);
                        createDirectoryAndSaveFile(bitmap);

                        Glide.with(MainActivity.this).load(bitmap).into(profile_image);
                        Toast.makeText(this, "Profile Picture Changed!", Toast.LENGTH_SHORT).show();

                        initPostRegistrationAttachmentAPI();
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        }
    }

    private void initSuccessDialog(String message)
    {
        try
        {
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this, R.style.CustomDialogTransparent);
            mBuilder.setCancelable(false);
            LayoutInflater inflater = getLayoutInflater();

            View view = inflater.inflate(R.layout.custom_dialog_change_passsword_success, null);

            Button btn_okay     = view.findViewById(R.id.btn_okay);
            TextView tv_status  = view.findViewById(R.id.tv_status);

            tv_status.setText(message);
            mBuilder.setView(view);

            final AlertDialog dialog = mBuilder.create();

            dialog.show();

            btn_okay.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dialog.dismiss();
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(this, "Please tap the back button again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }




    private void initLoadPhoto(String FullPathProfile)
    {
        try
        {
            TextView title = new TextView(this);
            String sTitle = "Profile Image";
            title.setText(sTitle);
            title.setPadding(10, 20, 10, 0);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.parseColor("#198754"));
            title.setTextSize(20);

            final AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);

            final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.view_image_dialog_layout, (ViewGroup) this.findViewById(R.id.layout_root));

            imageDialog.setView(layout);
            imageDialog.setCustomTitle(title);

            File imageFile = new File(FullPathProfile);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
            bitmap = initImageRotateNormal(imageFile, bitmap);
            Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

            ImageView image =  layout.findViewById(R.id.imageView);
            image.setImageBitmap(bitMapCustomize);

            imageDialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });

          /*  imageDialog.setPositiveButton("CHANGE PICTURE", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    initTakePhoto();
                }
            });*/

            imageDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initTakePhoto()
    {
        try
        {
            if(Build.VERSION.SDK_INT>=24)
            {
                try
                {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);

                    final CharSequence[] options = {"Take Photo", "Upload from Gallery"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Change Picture");
                    builder.setItems(options, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int item)
                        {
                            if (options[item].equals("Take Photo"))
                            {
                                initGetPictureCamera();
                            }
                            else if (options[item].equals("Upload from Gallery"))
                            {
                                initGetPictureGallery();
                            }
                        }
                    });
                    builder.show();
                }
                catch(Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
            else
            {
                final CharSequence[] options = {"Take Photo", "Upload from Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Change Picture");
                builder.setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int item)
                    {
                        if (options[item].equals("Take Photo"))
                        {
                            initGetPictureCamera();
                        }
                        else if (options[item].equals("Upload from Gallery"))
                        {
                            initGetPictureGallery();
                        }
                    }
                });
                builder.show();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetPictureCamera()
    {
        try
        {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            String FullPathProfile = sPicturePath + "/Profile.jpg";
            File outFile = new File(FullPathProfile);

            mCameraFileName = outFile.toString();
            Uri outURI = Uri.fromFile(outFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outURI);
            startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetPictureGallery()
    {
        try
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_GALLERY_CODE);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    //Customize Bitmap to Normal angle of Picture
    private static Bitmap initImageRotateNormal(File imagePath, Bitmap bitmap)
    {
        ExifInterface ei = null;

        try
        {
            ei = new ExifInterface((imagePath.getAbsolutePath()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        int orientation = Objects.requireNonNull(ei).getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap;

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    //Fix Auto rotate in Some Camera
    private static Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0,0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void initPostRegistrationAttachmentAPI()
    {
        try
        {
            String FullPathProfile = sPicturePath + "/Profile.jpg";

            File file = new File(FullPathProfile);

            if (file.exists())
            {
                enableRefreshUser = false;

                String CustomizeFileName = "img-" + AccountID +  ".jpg";

                RequestBody photoContent  = RequestBody.create(MediaType.parse("multipart/form-data") , file);
                MultipartBody.Part photo  = MultipartBody.Part.createFormData("photo", CustomizeFileName, photoContent);
                RequestBody sAccountID    = RequestBody.create(MediaType.parse("text/plain"), AccountID);

                final APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);

                Call<ResponseBody> call = apiInterface.PostRegistrationAttachment(photo, sAccountID);
                call.enqueue(new Callback<ResponseBody>()
                {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        if(response.isSuccessful())
                        {
                            if(response.body() != null)
                            {
                                Log.e(TAG, "Success Uploading Registration Attachment \n\n"+ response.toString());

                                enableRefreshUser = true;
                            }
                            else
                            {
                                String Logs = "Post Registration Attachment : Server Response Null";

                                Log.e(TAG, Logs);
                                volleyCatch.writeToFile(Logs);
                            }
                        }
                        else
                        {
                            String Logs   = convertingResponseError(response.errorBody());
                            String rLogs  = "Post Registration Attachment  Failed: " + Logs;

                            Log.e(TAG, rLogs);
                            volleyCatch.writeToFile(rLogs);

                            try
                            {
                                String[] out = Logs.split(":");
                                String result = out[1].replaceAll("[\\[\\](){}]","");
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, e.toString());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
                    {
                        String Logs = "Post Registration Attachment Failure: " + t.getMessage();

                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);
                    }
                });
            }

        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            File fileCache = getApplicationContext().getCacheDir();
            ClearApplicationCache.clearCache(fileCache);

            Log.e(TAG, "CLEAR CACHE");
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        super.onDestroy();
    }

}