package com.geodata.cups.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.geodata.cups.R;

public class TermsAndConditionsActivity extends AppCompatActivity
{
    private static final String TAG = DataPrivacyPolicyActivity.class.getSimpleName();

    ImageView iv_back;

    TextView tv_privacy, tv_email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        initViews();
    }

    private void initViews()
    {
        iv_back = findViewById(R.id.iv_back);

        tv_privacy = findViewById(R.id.tv_privacy);
        initSetDataPrivacyPolicyText(tv_privacy);
        tv_email   = findViewById(R.id.tv_email);

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

        tv_email.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EmailSent(tv_email.getText().toString());
            }
        });
    }

    private void initSetDataPrivacyPolicyText(TextView view)
    {
        try
        {
            SpannableStringBuilder spanTxt = new SpannableStringBuilder();
            spanTxt.append(getString(R.string.string_privacy));
            spanTxt.append(" ");
            spanTxt.append(getString(R.string.string_data_privacy_policy_link));
            spanTxt.setSpan(new ClickableSpan()
            {
                @Override
                public void onClick(@NonNull View widget)
                {
                    Intent intent = new Intent(TermsAndConditionsActivity.this, DataPrivacyPolicyActivity.class);
                    startActivity(intent);
                }
            }, spanTxt.length() - getString(R.string.string_data_privacy_policy_link).length(), spanTxt.length(), 0);

            spanTxt.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplication(), R.color.blue)), 20, 39, 0);
            //spanTxt.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),20, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spanTxt.append(" ");
            spanTxt.append(getString(R.string.string_privacy_2));

            view.setMovementMethod(LinkMovementMethod.getInstance());
            view.setText(spanTxt, TextView.BufferType.SPANNABLE);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
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