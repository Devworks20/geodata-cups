package com.geodata.cups.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.geodata.cups.Backend.SQLite.Repository.UserRepository;
import com.geodata.cups.Backend.Retrofit.Host.APIClient;
import com.geodata.cups.Backend.Retrofit.Interface.APIClientInterface;
import com.geodata.cups.Backend.Tools.EncodeDecodeAES;
import com.geodata.cups.Backend.Tools.VolleyCatch;
import com.geodata.cups.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity
{
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    LinearLayout ll_loading, ll_current_password;
    ImageView iv_back;
    TextView tv_display;

    EditText edt_current_password, edt_new_password, edt_re_new_password;

    Button btn_change_password;

    String Status, TokenMain, AccountID, Username, Password;

    VolleyCatch volleyCatch = new VolleyCatch();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);

        initViews();
    }

    private void initViews()
    {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            Status    = extras.getString("Status");
            TokenMain = extras.getString("TokenMain");
            AccountID = extras.getString("AccountID");
            Username  = extras.getString("Username");
            Password  = extras.getString("Password");
        }

        ll_loading          = findViewById(R.id.ll_loading);
        ll_current_password = findViewById(R.id.ll_current_password);
        iv_back             = findViewById(R.id.iv_back);
        tv_display          = findViewById(R.id.tv_display);

        edt_current_password  = findViewById(R.id.edt_current_password);
        edt_new_password      = findViewById(R.id.edt_new_password);
        edt_re_new_password   = findViewById(R.id.edt_re_new_password);

        btn_change_password   = findViewById(R.id.btn_reset_password);

        if (Status.equalsIgnoreCase("Second Change"))
        {
            tv_display.setVisibility(View.GONE);
            ll_current_password.setVisibility(View.VISIBLE);
        }

        initListeners();
    }

    private boolean checkFields()
    {
        if (Status.equalsIgnoreCase("Second Change") && edt_current_password.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter your current password.", Toast.LENGTH_SHORT).show();
        }
        else if (Status.equalsIgnoreCase("Second Change") &&  (!Password.equals(edt_current_password.getText().toString())))
        {
            Toast.makeText(this, "Your old password was entered incorrectly. Please enter it again.", Toast.LENGTH_SHORT).show();
        }
        else if (edt_new_password.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter your new password.", Toast.LENGTH_SHORT).show();
        }
        else if (edt_new_password.getText().toString().startsWith(" ") || edt_new_password.getText().toString().endsWith(" "))
        {
            Toast.makeText(this, "Passwords cannot have leading or trailing whitespace.", Toast.LENGTH_SHORT).show();
        }
        else if (edt_re_new_password.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please re-enter your new password.", Toast.LENGTH_SHORT).show();
        }
        else if (edt_re_new_password.getText().toString().startsWith(" ") || edt_re_new_password.getText().toString().endsWith(" "))
        {
            Toast.makeText(this, "Passwords cannot have leading or trailing whitespace.", Toast.LENGTH_SHORT).show();
        }

        else if (!edt_new_password.getText().toString().equals(edt_re_new_password.getText().toString()))
        {
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
        }
        else
        {
            return true;
        }
        return false;
    }

    private void initListeners()
    {
        btn_change_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkFields())
                {
                    initCloseKeyboard();

                    if (haveNetworkConnection(getApplicationContext()))
                    {
                        edt_re_new_password.onEditorAction(EditorInfo.IME_ACTION_DONE);

                        ll_loading.setVisibility(View.VISIBLE);
                        String NewPassword = edt_re_new_password.getText().toString();
                        initPostChangePasswordAPI(NewPassword);
                    }
                    else
                    {
                        Toast.makeText(ChangePasswordActivity.this, "You have no internet connection. try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initCancelChangePassword();
            }
        });

        edt_new_password.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (count > 0)
                {
                    if (s.toString().startsWith(" ") || s.toString().endsWith(" "))
                    {
                        edt_new_password.setError("Passwords cannot have leading or trailing whitespace.");

                        edt_new_password.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
                    }
                }
                else
                {
                    edt_new_password.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        edt_re_new_password.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (count > 0)
                {
                    if (s.toString().startsWith(" ") || s.toString().endsWith(" "))
                    {
                        edt_re_new_password.setError("Passwords cannot have leading or trailing whitespace.");

                        edt_re_new_password.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
                    }
                }
                else
                {
                    edt_re_new_password.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    private void initPostChangePasswordAPI(final String NewPassword)
    {
        try
        {
            if (!haveNetworkConnection(this))
            {
                Toast.makeText(ChangePasswordActivity.this, "No network connection.", Toast.LENGTH_SHORT).show();
                return; // Early exit if no network connection
            }

            final APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);

            // Authorization
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json; charset=UTF-8");
            headers.put("Authorization", TokenMain);

            Call<String> postChangePassword = apiInterface.PostChangePassword(headers, AccountID, NewPassword);
            postChangePassword.enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                {
                    ll_loading.setVisibility(View.GONE); // Hide loading in both success and failure
                    if (!response.isSuccessful())
                    {
                        Log.e(TAG, "Change Password Failed: " + convertingResponseError(response.errorBody()));
                        volleyCatch.writeToFile("Change Password Failed: " + convertingResponseError(response.errorBody()));
                        Toast.makeText(ChangePasswordActivity.this, "Change Password Failed.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (response.body() == null)
                    {
                        Log.e(TAG, "Change Password: Server Response Null");
                        volleyCatch.writeToFile("Change Password: Server Response Null");
                        Toast.makeText(ChangePasswordActivity.this, "Change Password Failed.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.e(TAG, "postChangePassword: " + response.body());

                    try
                    {
                        EncodeDecodeAES aes = new EncodeDecodeAES();
                        String encryptedNewPassword = EncodeDecodeAES.bytesToHex(aes.encrypt(NewPassword));
                        UserRepository.updateUserNewPassword(getApplicationContext(), encryptedNewPassword, Username);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                        UserRepository.updateUserNewPassword(getApplicationContext(), NewPassword, Username);
                    }

                    // Handle navigation based on the status
                    if ("First Change".equalsIgnoreCase(Status))
                    {
                        Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                        intent.putExtra("DialogSuccess", "Show");
                        intent.putExtra("Username", Username);
                        startActivity(intent);
                    }
                    else if ("Second Change".equalsIgnoreCase(Status))
                    {
                        Intent data = new Intent();
                        data.putExtra("Result", "Success");
                        data.putExtra("Password", NewPassword);
                        setResult(RESULT_OK, data);
                    }
                    finish();
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                {
                    ll_loading.setVisibility(View.GONE);
                    Log.e(TAG, "Change Password Failure: " + t.getMessage());
                    volleyCatch.writeToFile("Change Password Failure: " + t.getMessage());
                    Toast.makeText(ChangePasswordActivity.this, "Change Password Failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e)
        {
            ll_loading.setVisibility(View.GONE);
            Log.e(TAG, "Getting Login: " + e);
            volleyCatch.writeToFile("Getting Login: " + e.toString());
            Toast.makeText(ChangePasswordActivity.this, "Change Password Failed.", Toast.LENGTH_SHORT).show();
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

    private void initCancelChangePassword()
    {
        try
        {
            final AlertDialog.Builder ADialog = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.MyAlertDialogStyle);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_dialog_title, null);
            TextView textView = view.findViewById(R.id.tv_dialog_title);
            String sTitle = "Cancel Change Password";
            textView.setText(sTitle);
            ADialog.setCustomTitle(view);
            ADialog.setMessage("Are you sure you want to cancel?");
            ADialog.setCancelable(true);
            ADialog.setNegativeButton("CLOSE ", null);
            ADialog.setPositiveButton("YES", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if (Status.equalsIgnoreCase("First Change"))
                    {
                        UserRepository.removeUser(getApplication());

                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }
            });
            ADialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        initCancelChangePassword();
    }

    private void initCloseKeyboard()
    {
        View view = getCurrentFocus();

        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}