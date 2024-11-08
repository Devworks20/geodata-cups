package com.geodata.cups.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.geodata.cups.Backend.SQLite.Class.UserClass;
import com.geodata.cups.Backend.SQLite.Repository.UserRepository;
import com.geodata.cups.Backend.Retrofit.Host.APIClient;
import com.geodata.cups.Backend.Retrofit.Interface.APIClientInterface;
import com.geodata.cups.Backend.Tools.EncodeDecodeAES;
import com.geodata.cups.Backend.Tools.VolleyCatch;
import com.geodata.cups.Backend.Retrofit.Model.Other.LoginModel;
import com.geodata.cups.Backend.Retrofit.Model.Other.TokenModel;
import com.geodata.cups.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = LoginActivity.class.getSimpleName();

    TextView tv_forgotPassword, tv_whatIsCups, tv_terms_and_conditions, tv_data_privacy_policy;

    EditText edt_username, edt_password;

    Button btn_login, btn_register;

    LinearLayout ll_loading;

    int request_Code = 101;

    VolleyCatch volleyCatch = new VolleyCatch();

    String sUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    private void initViews()
    {
        initSetAPIAddressLink();

        ActivityCompat.requestPermissions(this,new String[]
                {
                        Manifest.permission.INTERNET,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                },1);

        tv_forgotPassword       = findViewById(R.id.tv_forgotPassword);
        tv_whatIsCups           = findViewById(R.id.tv_whatIsCups);
        tv_terms_and_conditions = findViewById(R.id.tv_terms_and_conditions);
        tv_data_privacy_policy  = findViewById(R.id.tv_data_privacy_policy);

        edt_username       = findViewById(R.id.edt_username);
        edt_password       = findViewById(R.id.edt_password);

        btn_login          = findViewById(R.id.btn_login);
        btn_register       = findViewById(R.id.btn_register);

        ll_loading         = findViewById(R.id.ll_loading);

        initListeners();
    }

    private void initListeners()
    {
        tv_forgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        tv_whatIsCups.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(LoginActivity.this, WhatIsCupsActivity.class);
                startActivity(intent);
            }
        });

        tv_terms_and_conditions.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(LoginActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);
            }
        });

        tv_data_privacy_policy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(LoginActivity.this, DataPrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (initCheckFields())
                {
                    if (haveNetworkConnection(getApplicationContext()))
                    {
                        sUsername       = edt_username.getText().toString();
                        String Password = edt_password.getText().toString();

                        edt_password.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        ll_loading.setVisibility(View.VISIBLE);

                        initOnlineLogin(sUsername, Password);

                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "You have no internet connection. try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (haveNetworkConnection(getApplicationContext()))
                {
                    Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                    startActivityForResult(intent, request_Code);
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "You have to internet connection. please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_Code && resultCode == Activity.RESULT_OK)
        {
            initSuccessRegistration();
        }
    }

    private void initSuccessRegistration()
    {
        try
        {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle);
            alertDialog.setCancelable(true);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_dialog_title, null);
            TextView textView = view.findViewById(R.id.tv_dialog_title);
            String sTitle = "Successful CUPS Registration";

            textView.setText(sTitle);
            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.ProgressColor));
            alertDialog.setCustomTitle(view);
            alertDialog.setMessage("You have successfully registered to CUPS mobile application. Your account details is now under review." +
                                   "\n\nWe will send you an email regarding your account status as soon as possible." +
                                   "\n\nThank you!");

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


    private void initPrivacyText()
    {
        String title = "Privacy Policy";
        String description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

        initDisplayText(title, description);
    }

    private void initTermsText()
    {
        String title = "Terms";
        String description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

        initDisplayText(title, description);
    }

    private void initDisplayText(String title, String description)
    {
        androidx.appcompat.app.AlertDialog.Builder sync = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this);
        sync.setTitle(title);
        sync.setMessage(description);
        sync.setCancelable(false);
        sync.setPositiveButton("CLOSE", null);
        sync.show();
    }


    public boolean initCheckFields()
    {
        if(Objects.requireNonNull(edt_username.getText()).toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Please enter your username.", Toast.LENGTH_SHORT).show();

            edt_username.requestFocus();
        }
        else if(Objects.requireNonNull(edt_password.getText()).toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Please enter your password.", Toast.LENGTH_SHORT).show();

            edt_password.requestFocus();
        }
        else
        {
            return  true;
        }
        return false;
    }

    private void initOnlineLogin(final String Username, final String Password)
    {
        try
        {
            APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);
            Call<TokenModel> callToken = apiInterface.ValidateToken("password", Username, Password);

            callToken.enqueue(new Callback<TokenModel>()
            {
                @Override
                public void onResponse(@NonNull Call<TokenModel> call, @NonNull Response<TokenModel> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        TokenModel tokenModel = response.body();

                        final String TokenMain = "Bearer " + tokenModel.getAccess_token();
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=UTF-8");
                        headers.put("Authorization", TokenMain);

                        //PUT SUBS KEY HERE..
                        Call<LoginModel> callLogin = apiInterface.LoginUser(headers, Username, Password, "CUPS");
                        callLogin.enqueue(new Callback<LoginModel>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    final LoginModel loginModel = response.body();

                                    if (loginModel.getAccountID().equals("0"))
                                    {
                                        handleLoginFailure("Login Access Failed.");
                                    }
                                    else
                                    {
                                        // Continue your logic here
                                        Call<String> checkAccountPassword = apiInterface.CheckAccountPassword(headers, loginModel.getAccountID());
                                        checkAccountPassword.enqueue(new Callback<String>()
                                        {
                                            @Override
                                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                                            {
                                                if (response.isSuccessful() && response.body() != null)
                                                {
                                                    String userID = UUID.randomUUID().toString();

                                                    UserClass userClass = new UserClass();
                                                    userClass.setUserID(userID);
                                                    userClass.setUsername(Username);

                                                    try
                                                    {
                                                        EncodeDecodeAES aes = new EncodeDecodeAES();
                                                        String encryptedPassword = EncodeDecodeAES.bytesToHex(aes.encrypt(Password));
                                                        userClass.setPassword(encryptedPassword);
                                                    }
                                                    catch (Exception e)
                                                    {
                                                        Log.e(TAG, e.toString());
                                                        userClass.setPassword(Password);
                                                    }

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

                                                    userClass.setStationCommander(loginModel.getStationCommander() != null ? loginModel.getStationCommander() : "");
                                                    userClass.setPCPCommander(loginModel.getPCPCommander() != null ? loginModel.getPCPCommander() : "");

                                                    Cursor cursor = UserRepository.realAllData2(getApplicationContext(), Username);

                                                    if (cursor.getCount() == 0)
                                                    {
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
                                                        Date now = new Date(System.currentTimeMillis());
                                                        String dateNow = dateFormat.format(now);
                                                        userClass.setDtAdded(dateNow);
                                                        userClass.setIsActive("1");
                                                        UserRepository.createUser(userClass, getApplicationContext());
                                                    }
                                                    else if (cursor.moveToFirst())
                                                    {
                                                        UserRepository.updateUser(getApplicationContext(), userClass, Username);
                                                    }

                                                    final Intent intent;
                                                    if (response.body().equals("UnChanged"))
                                                    {
                                                        intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                                                        intent.putExtra("Status", "First Change");
                                                        intent.putExtra("TokenMain", TokenMain);
                                                        intent.putExtra("AccountID", loginModel.getAccountID());
                                                        intent.putExtra("Username", Username);
                                                        intent.putExtra("Password", Password);
                                                    }
                                                    else
                                                    {
                                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        intent.putExtra("Username", Username);
                                                    }

                                                    try
                                                    {
                                                        if (loginModel.getPicturePath() != null && !loginModel.getPicturePath().equals(""))
                                                        {
                                                            Picasso.get()
                                                                    .load(loginModel.getPicturePath())
                                                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                                                    .into(target);
                                                        }

                                                        new Handler().postDelayed(new Runnable()
                                                        {
                                                            @Override
                                                            public void run()
                                                            {
                                                                Log.e(TAG, "DIRECT TO MAIN");
                                                                startActivity(intent);
                                                                finish();
                                                                ll_loading.setVisibility(View.GONE);
                                                            }
                                                        }, 1000);
                                                    }
                                                    catch (Exception e)
                                                    {
                                                        Log.e(TAG, e.toString());
                                                    }
                                                }
                                                else
                                                {
                                                    String Logs = response.isSuccessful() ?
                                                            "Check Account Password Failed: Server Response Null" :
                                                            "Check Account Password Failed: " + convertingResponseError(response.errorBody());
                                                    Log.e(TAG, Logs);
                                                    volleyCatch.writeToFile(Logs);
                                                    handleLoginFailure("Login Access Failed.");
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                                            {
                                                String Logs = "Check Account Password Failure: " + t.getMessage();
                                                Log.e(TAG, Logs);
                                                volleyCatch.writeToFile(Logs);
                                                handleLoginFailure("Login Access Failed.");
                                            }
                                        });
                                    }
                                }
                                else
                                {
                                    String errorMessage = response.isSuccessful() ? "Login: Server Response Null" : "Login Failed: "
                                            + convertingResponseError(response.errorBody());

                                    handleLoginFailure(errorMessage);
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t)
                            {
                                String Logs = "Login Failure: " + t.getMessage();
                                handleLoginFailure(Logs);
                            }
                        });
                    }
                    else
                    {
                        handleLoginFailure(response.isSuccessful() ? "Login Token: Server Response Null" : convertingResponseError(response.errorBody()));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<TokenModel> call, @NonNull Throwable t)
                {
                    handleLoginFailure(t.getMessage());

                    if (Objects.requireNonNull(t.getMessage()).contains("failed to connect"))
                    {
                        handleLoginFailure("FAILED TO ACCESS API LINK");

                        initPopUpChangeAPIAddressLink();
                    }
                }
            });
        }
        catch (Exception e)
        {
            handleLoginFailure("Getting Login: " + e.getMessage());
        }
    }

    private void handleLoginFailure(String errorMessage)
    {
        Log.e(TAG, errorMessage);
        volleyCatch.writeToFile(errorMessage);
        ll_loading.setVisibility(View.GONE);
        Toast.makeText(LoginActivity.this, "Login Access Failed.", Toast.LENGTH_SHORT).show();
    }


    private void initPopUpChangeAPIAddressLink()
    {
        try
        {
            AlertDialog.Builder alertDialogNew = new AlertDialog.Builder(this);

            final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout_edit_text, null);
            final EditText editText = customLayout.findViewById(R.id.edt_custom);

            alertDialogNew.setView(customLayout);

            alertDialogNew.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // Handle cancel button click
                    dialog.cancel();
                }
            });

            alertDialogNew.setPositiveButton("Proceed", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // Handle proceed button click
                    String enteredText = editText.getText().toString();
                    // Do something with the entered text

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("IPAddress", enteredText);
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "Entered API Address: " + enteredText, Toast.LENGTH_SHORT).show();

                    initSetAPIAddressLink();
                }
            });
            AlertDialog alert = alertDialogNew.create();
            alert.setCancelable(false);
            alert.setTitle("Change API Address");
            alert.setMessage("Please provide the new API Address you'd like to use.");
            alert.show();

        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

    }

    private void initSetAPIAddressLink()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (prefs.contains("IPAddress") && prefs.getString("IPAddress", null) != null)
        {
            String IPAddress = prefs.getString("IPAddress", "");

            if (!IPAddress.equals(""))
            {
                if (!IPAddress.contains("https://"))
                {
                    //public static String URL_API_TEST = "https://demo.geosolutions.com.ph:8002/";

                    APIClient.URL_API = "https://" + IPAddress + "/";
                }
                else
                {
                    APIClient.URL_API = IPAddress;
                }
            }
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

                    //UPDATING - USER PATH PICTURE OFFLINE
                    UserRepository.updateUserPicturePathOffline(getApplicationContext(), PicturePathOffline, sUsername);

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

    @Override
    public void onBackPressed()
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure to exit the application?");
        builder.setTitle("Confirm exit");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                finishAffinity();
                System.exit(0);
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        initResetPasswordSuccessful();
    }

    private void initResetPasswordSuccessful()
    {
        try
        {
            Bundle extras = getIntent().getExtras();

            if (extras != null)
            {
                if (extras.getString("DialogSuccess") != null)
                {
                   String  DialogSuccess = extras.getString("DialogSuccess");

                    if (DialogSuccess != null && DialogSuccess.equalsIgnoreCase("Show"))
                    {
                        String message = "Reset password successfully!\nYou can now login to your account.";
                        initSuccessDialog(message);
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
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
}