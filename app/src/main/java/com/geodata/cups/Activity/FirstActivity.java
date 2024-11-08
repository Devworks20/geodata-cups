package com.geodata.cups.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.geodata.cups.Backend.Retrofit.Host.APIClient;
import com.geodata.cups.Backend.SQLite.Repository.UserRepository;

public class FirstActivity extends AppCompatActivity
{
    private static final String TAG = FirstActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews()
    {
        try
        {
            initSetAPIAddressLink();

            Cursor cursor = UserRepository.realAllData(FirstActivity.this);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    String CompleteName =  cursor.getString(cursor.getColumnIndex("CompleteName"));
                    String Username     =  cursor.getString(cursor.getColumnIndex("Username"));

                    Toast.makeText(this, "Welcome back " + CompleteName, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                    intent.putExtra("Username", Username);
                    startActivity(intent);
                }
                cursor.close();
            }
            else
            {
                Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            finish();
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

}