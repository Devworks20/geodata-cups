package com.geodata.cups.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.geodata.cups.R;

public class DataPrivacyPolicyActivity extends AppCompatActivity
{
    private static final String TAG = DataPrivacyPolicyActivity.class.getSimpleName();

    ImageView iv_back;
    TextView tv_email1, tv_email2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_and_policy);

        initViews();
    }

    private void initViews()
    {
        iv_back   = findViewById(R.id.iv_back);

        tv_email1 = findViewById(R.id.tv_email1);
        tv_email2 = findViewById(R.id.tv_email2);

        initListeners();
    }

    private void initListeners()
    {
        iv_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                finish();
            }
        });

        tv_email1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EmailSent(tv_email1.getText().toString());
            }
        });

        tv_email2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EmailSent(tv_email2.getText().toString());
            }
        });
    }

    private void EmailSent(String email)
    {
        try
        {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text
            startActivity(Intent.createChooser(emailIntent, "Choose Email Sender"));
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
        }
    }
}