package com.geodata.cups.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.geodata.cups.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;

public class ResetPasswordActivity extends AppCompatActivity
{
    private static final String TAG = ResetPasswordActivity.class.getSimpleName();

    ImageView iv_back;

    EditText edt_new_password, edt_re_new_password;
    Button btn_reset_password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();

    }

    private void initViews()
    {
        iv_back = findViewById(R.id.iv_back);

        edt_new_password    = findViewById(R.id.edt_new_password);
        edt_re_new_password = findViewById(R.id.edt_re_new_password);

        btn_reset_password = findViewById(R.id.btn_reset_password);


        initListeners();
    }

    private void initListeners()
    {
        try
        {
            btn_reset_password.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (checkFields())
                    {
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        intent.putExtra("DialogSuccess", "Show");
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private boolean checkFields()
    {
        if (edt_new_password.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter your new password.", Toast.LENGTH_SHORT).show();
        }
        else if (edt_re_new_password.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please re-enter your new password.", Toast.LENGTH_SHORT).show();
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

}