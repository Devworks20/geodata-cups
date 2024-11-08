package com.geodata.cups.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.geodata.cups.Backend.Retrofit.Model.Registration.RegistrationResponseModel;
import com.geodata.cups.Backend.Retrofit.Host.APIClient;
import com.geodata.cups.Backend.Retrofit.Interface.APIClientInterface;
import com.geodata.cups.Backend.Tools.VolleyCatch;
import com.geodata.cups.R;
import com.geodata.cups.Fragment.RegistrationAccountInfoFragment;
import com.geodata.cups.Fragment.RegistrationPhotoFragment;
import com.geodata.cups.Fragment.RegistrationPrimaryFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity
{
    private static final String TAG = RegistrationActivity.class.getSimpleName();

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    RegistrationPrimaryFragment registrationPrimaryFragment;
    RegistrationAccountInfoFragment registrationAccountInfoFragment;
    RegistrationPhotoFragment registrationPhotoFragment;

    ImageView iv_back;
    LinearLayout ll_prev, ll_next, ll_done, ll_loading;
    FloatingActionButton fab_previousPage, fab_nextPage, fab_done;
    View view_progress1, view_progress2, view_progress3;

    HashMap<String,String> primaryInfo = new HashMap<>();
    HashMap<String,String> accountInfo = new HashMap<>();
    HashMap<String,String> photoInfo = new HashMap<>();

    VolleyCatch volleyCatch = new VolleyCatch();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
    }

    private void initViews()
    {
        fragmentManager = getSupportFragmentManager();
        registrationPrimaryFragment     = new RegistrationPrimaryFragment();
        registrationAccountInfoFragment = new RegistrationAccountInfoFragment();
        registrationPhotoFragment       = new RegistrationPhotoFragment();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout, registrationPrimaryFragment);
        fragmentTransaction.add(R.id.frameLayout, registrationAccountInfoFragment);
        fragmentTransaction.add(R.id.frameLayout, registrationPhotoFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        showHideFragment(registrationAccountInfoFragment, registrationPrimaryFragment);
        showHideFragment(registrationPhotoFragment, registrationPrimaryFragment);

        iv_back = findViewById(R.id.iv_back);

        ll_prev    = findViewById(R.id.ll_prev);
        ll_next    = findViewById(R.id.ll_next);
        ll_done    = findViewById(R.id.ll_done);
        ll_loading = findViewById(R.id.ll_loading);

        fab_previousPage = findViewById(R.id.fab_previousPage);
        fab_nextPage    = findViewById(R.id.fab_nextPage);
        fab_done        = findViewById(R.id.fab_done);

        view_progress1 = findViewById(R.id.view_progress1);
        view_progress2 = findViewById(R.id.view_progress2);
        view_progress3 = findViewById(R.id.view_progress3);

        initListeners();
    }

    private void initListeners()
    {
        iv_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initCancelRegistration();
            }
        });

        fab_nextPage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(registrationPrimaryFragment.isVisible())
                {
                    if(registrationPrimaryFragment.checkFields())
                    {
                        showHideFragment(registrationPrimaryFragment, registrationAccountInfoFragment);
                        ll_prev.setVisibility(View.VISIBLE);
                        ll_next.setVisibility(View.VISIBLE);
                        ll_done.setVisibility(View.GONE);
                        view_progress2.setBackgroundResource(R.color.ProgressColor);
                    }
                }
                else if (registrationAccountInfoFragment.isVisible())
                {
                    if (registrationAccountInfoFragment.checkFields())
                    {
                        registrationAccountInfoFragment.setAccountInformation();

                        initUsernameCheckerAPI(accountInfo.get("Username"));
                    }
                }
            }
        });

        fab_previousPage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(registrationAccountInfoFragment.isVisible())
                {
                    showHideFragment(registrationAccountInfoFragment, registrationPrimaryFragment);
                    ll_prev.setVisibility(View.GONE);
                    ll_next.setVisibility(View.VISIBLE);
                    ll_done.setVisibility(View.GONE);

                    view_progress2.setBackgroundResource(R.color.mainGray);
                }
                else if (registrationPhotoFragment.isVisible())
                {
                    showHideFragment(registrationPhotoFragment, registrationAccountInfoFragment);
                    ll_prev.setVisibility(View.VISIBLE);
                    ll_next.setVisibility(View.VISIBLE);
                    ll_done.setVisibility(View.GONE);

                    view_progress3.setBackgroundResource(R.color.mainGray);
                }
            }
        });

        fab_done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (registrationPhotoFragment.checkFields())
                {
                    final AlertDialog.Builder ADCancel = new AlertDialog.Builder(RegistrationActivity.this, R.style.MyAlertDialogStyle);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.custom_dialog_title, null);
                    TextView textView = view.findViewById(R.id.tv_dialog_title);
                    String sTitle = "Confirm Submission";
                    textView.setText(sTitle);
                    ADCancel.setCustomTitle(view);
                    ADCancel.setMessage("Are you sure you want to submit this form?");
                    ADCancel.setCancelable(true);
                    ADCancel.setNegativeButton("CANCEL ", null);
                    ADCancel.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            registrationPrimaryFragment.setPrimaryInformation();
                            registrationAccountInfoFragment.setAccountInformation();
                            registrationPhotoFragment.setPhotoInformation();

                            dialog.dismiss();
                            initPostRegistrationAPI();
                        }
                    });
                    ADCancel.show();
                }
            }
        });
    }

    private void showHideFragment(Fragment fragment1, Fragment fragment2)
    {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTransaction.hide(fragment1);
        fragTransaction.show(fragment2);
        fragTransaction.commit();
    }

    private void initUsernameCheckerAPI(String Username)
    {
        if (!haveNetworkConnection(getApplicationContext()))
        {
            // Handle no network connection scenario here if needed
            return;
        }

        APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);
        Call<String> callLogin = apiInterface.GetUsernameChecker(Username);

        callLogin.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (!response.isSuccessful())
                {
                    logError("Username Checker Failed: " + convertingResponseError(response.errorBody()));
                    return;
                }

                String responseBody = response.body();

                if (responseBody == null)
                {
                    logError("Username Checker: Server Response Null");
                    return;
                }

                Log.e(TAG, responseBody);

                if ("NotExist".equals(responseBody))
                {
                    showHideFragment(registrationAccountInfoFragment, registrationPhotoFragment);
                    ll_prev.setVisibility(View.VISIBLE);
                    ll_next.setVisibility(View.GONE);
                    ll_done.setVisibility(View.VISIBLE);

                    view_progress3.setBackgroundResource(R.color.ProgressColor);
                }
                else
                {
                    Toast.makeText(RegistrationActivity.this, "Username has already been taken.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                logError("Username Checker Failure: " + t.getMessage());
            }
        });
    }

    private void logError(String errorLog)
    {
        Log.e(TAG, errorLog);
        volleyCatch.writeToFile(errorLog); // Assuming volleyCatch is a custom logger
    }


    private void initPostRegistrationAPI()
    {
        if (!haveNetworkConnection(this))
        {
            Toast.makeText(this, "You have no internet, please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        ll_loading.setVisibility(View.VISIBLE);
        APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);

        Call<RegistrationResponseModel> postRegistration = apiInterface.PostRegistration(
                accountInfo.get("Username"),
                accountInfo.get("Password"),
                primaryInfo.get("Firstname"),
                primaryInfo.get("Middlename"),
                primaryInfo.get("Lastname"),
                primaryInfo.get("Email"),
                primaryInfo.get("Mobileno"),
                Integer.parseInt(Objects.requireNonNull(primaryInfo.get("RankID"))),
                Integer.parseInt(Objects.requireNonNull(primaryInfo.get("DesignationID"))),
                primaryInfo.get("SuffixID"),
                Integer.parseInt(Objects.requireNonNull(primaryInfo.get("PoliceDistrictID"))),
                Integer.parseInt(Objects.requireNonNull(primaryInfo.get("PoliceStationID"))),
                Integer.parseInt(Objects.requireNonNull(primaryInfo.get("PolicePrecintID")))
        );

        postRegistration.enqueue(new Callback<RegistrationResponseModel>()
        {
            @Override
            public void onResponse(@NonNull Call<RegistrationResponseModel> call, @NonNull Response<RegistrationResponseModel> response)
            {
                ll_loading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null)
                {
                    RegistrationResponseModel registrationResponseModel = response.body();
                    PostRegistrationAttachmentAPI(registrationResponseModel.getAccountID());
                    Log.e(TAG, "Account ID: " + registrationResponseModel.getAccountID());
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
                else
                {
                    String errorLog = "Post Registration: " + convertingResponseError(response.errorBody());
                    logError(errorLog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegistrationResponseModel> call, @NonNull Throwable t)
            {
                ll_loading.setVisibility(View.GONE);
                logError("Post Registration Failure: " + t.getMessage());
            }
        });

    }


    private void PostRegistrationAttachmentAPI(String AccountID)
    {
        File file = new File(Objects.requireNonNull(photoInfo.get("FilePath")));

        if (!file.exists())
        {
            Log.e(TAG, "File does not exist: " + file.getAbsolutePath());
            return;
        }

        String CustomizeFileName = "img-" + AccountID + "." + photoInfo.get("FileExtension");

        RequestBody photoContent = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part photo = MultipartBody.Part.createFormData("photo", CustomizeFileName, photoContent);
        RequestBody sAccountID = RequestBody.create(MediaType.parse("text/plain"), AccountID);

        APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);
        Call<ResponseBody> call = apiInterface.PostRegistrationAttachment(photo, sAccountID);
        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
            {
                if (response.isSuccessful())
                {
                    Log.e(TAG, "Success Uploading Registration Attachment \n\n" + response.message());
                }
                else
                {
                    String errorLog = "Post Registration Attachment Failed: " + convertingResponseError(response.errorBody());
                    logError(errorLog);
                    Toast.makeText(RegistrationActivity.this, errorLog, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t)
            {
                logError("Post Registration Attachment Failure: " + t.getMessage());
            }
        });
    }

    public void setPrimaryInfo(String RankID, String Firstname, String Middlename, String Lastname,
                               String SuffixID, String Email, String Mobileno, String DesignationID,
                               String PoliceDistrictID, String PoliceStationID, String PolicePrecintID)
    {
        primaryInfo.clear();

        primaryInfo.put("RankID", RankID);
        primaryInfo.put("Firstname", Firstname);
        primaryInfo.put("Middlename", Middlename);
        primaryInfo.put("Lastname", Lastname);
        primaryInfo.put("SuffixID", SuffixID);
        primaryInfo.put("Email", Email);
        primaryInfo.put("Mobileno", Mobileno);
        primaryInfo.put("DesignationID", DesignationID);
        primaryInfo.put("PoliceDistrictID", PoliceDistrictID);
        primaryInfo.put("PoliceStationID", PoliceStationID);
        primaryInfo.put("PolicePrecintID", PolicePrecintID);
    }

    public void setAccountInfo(String Username, String Password)
    {
        accountInfo.clear();

        accountInfo.put("Username", Username);
        accountInfo.put("Password", Password);
    }

    public void setPhotoInfo(String FilePath, String FileName, String FileExtension, String FileSize)
    {
        photoInfo.clear();

        photoInfo.put("FilePath", FilePath);
        photoInfo.put("FileName", FileName);
        photoInfo.put("FileExtension", FileExtension);
        photoInfo.put("FileSize", FileSize);
    }



    private void initCancelRegistration()
    {
        if (registrationPrimaryFragment.isVisible())
        {
            AlertDialog.Builder ADCancel = new AlertDialog.Builder(RegistrationActivity.this, R.style.MyAlertDialogStyle);
            View customTitleView = getLayoutInflater().inflate(R.layout.custom_dialog_title, null);
            TextView textView = customTitleView.findViewById(R.id.tv_dialog_title);
            String text = "Cancel Registration";
            textView.setText(text);
            ADCancel.setCustomTitle(customTitleView);
            ADCancel.setMessage("Are you sure you want to cancel?");
            ADCancel.setCancelable(true);
            ADCancel.setNegativeButton("NO", null);
            ADCancel.setPositiveButton("YES", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    finish();
                }
            });
            ADCancel.show();
        }
        else if (registrationAccountInfoFragment.isVisible() || registrationPhotoFragment.isVisible())
        {
            fab_previousPage.callOnClick();
        }
    }


    @Override
    public void onBackPressed()
    {
        initCancelRegistration();
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

}