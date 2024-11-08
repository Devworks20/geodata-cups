package com.geodata.cups.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geodata.cups.Backend.SQLite.Repository.UserRepository;
import com.geodata.cups.Backend.Tools.ClearApplicationCache;
import com.geodata.cups.R;

import java.io.File;

public class SettingsActivity extends AppCompatActivity
{
    private static final String TAG = SettingsActivity.class.getSimpleName();

    ImageView iv_back;

    LinearLayout ll_what_is_cups, ll_change_password, ll_terms_and_conditions, ll_data_privacy_policy, ll_logout;

    String TokenMain, AccountID, Username, Password;
    int request_Code = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
    }

    private void initViews()
    {
        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            TokenMain = extras.getString("TokenMain");
            AccountID = extras.getString("AccountID");
            Username  = extras.getString("Username");
            Password  = extras.getString("Password");
        }

        iv_back              = findViewById(R.id.iv_back);

        ll_what_is_cups         = findViewById(R.id.ll_what_is_cups);
        ll_change_password      = findViewById(R.id.ll_change_password);
        ll_terms_and_conditions = findViewById(R.id.ll_terms_and_conditions);
        ll_data_privacy_policy  = findViewById(R.id.ll_data_privacy_policy);
        ll_logout               = findViewById(R.id.ll_logout);

        initListeners();
    }

    private void initListeners()
    {
        iv_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        ll_what_is_cups.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SettingsActivity.this, WhatIsCupsActivity.class);
                startActivity(intent);
            }
        });

        ll_change_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                intent.putExtra("Status", "Second Change");
                intent.putExtra("TokenMain", TokenMain);
                intent.putExtra("AccountID", AccountID);
                intent.putExtra("Username", Username);
                intent.putExtra("Password", Password);
                startActivityForResult(intent, request_Code);

            }
        });


        ll_terms_and_conditions.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SettingsActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);
            }
        });


        ll_data_privacy_policy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SettingsActivity.this, DataPrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });

        ll_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_dialog_title, null);
                TextView textView = view.findViewById(R.id.tv_dialog_title);
                String sTitle = "Logout Confirmation";
                textView.setText(sTitle);
                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.mainColor));

                final AlertDialog.Builder ADialog = new AlertDialog.Builder(SettingsActivity.this, R.style.MyAlertDialogStyle);
                ADialog.setCustomTitle(view);
                ADialog.setMessage("Are you sure you want to logout?");
                ADialog.setCancelable(true);
                ADialog.setNegativeButton("Cancel ", null);
                ADialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            //Remove User
                            UserRepository.removeUser(getApplication());

                            //Remove Cache
                            try
                            {
                                File fileCache = getApplicationContext().getCacheDir();
                                ClearApplicationCache.clearCache(fileCache);
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, e.toString());
                            }

                            dialog.dismiss();

                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(SettingsActivity.this, "Logout Success", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
                ADialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_Code && resultCode == Activity.RESULT_OK)
        {
            String Result = data.getStringExtra("Result");

            if (Result != null && Result.equalsIgnoreCase("Success"))
            {
                Password = data.getStringExtra("Password");

                String message = "Password changed successfully!";
                initSuccessDialog(message);
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

}