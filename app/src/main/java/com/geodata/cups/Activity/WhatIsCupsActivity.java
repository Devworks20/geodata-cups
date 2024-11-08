package com.geodata.cups.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.geodata.cups.R;

public class WhatIsCupsActivity extends AppCompatActivity
{
    ImageView iv_back;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_is_cups);

        initViews();
    }

    private void initViews()
    {
        iv_back = findViewById(R.id.iv_back);


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
    }

}