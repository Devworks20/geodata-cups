package com.geodata.cups.Backend.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.geodata.cups.Backend.SQLite.Class.ProgramClass;
import com.geodata.cups.Backend.SQLite.Repository.ProgramRepository;
import com.geodata.cups.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.Objects;

public class NewProgramsActivity extends AppCompatActivity
{
    private static final String TAG = NewProgramsActivity.class.getSimpleName();

    ImageView iv_back;

    MaterialEditText edt_program, edt_activity, edt_output;

    Button btn_submit_programs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_programs);

        initViews();
    }

    private void initViews()
    {
        iv_back = findViewById(R.id.iv_back);

        edt_program = findViewById(R.id.edt_program);
        edt_activity = findViewById(R.id.edt_activity);
        edt_output = findViewById(R.id.edt_output);

        btn_submit_programs = findViewById(R.id.btn_submit_programs);

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

        btn_submit_programs.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String Programs = Objects.requireNonNull(edt_program.getText()).toString();

                Cursor cursor = ProgramRepository.validateInsertProgram(NewProgramsActivity.this, Programs);

                if (cursor.getCount() != 0)
                {
                    initSavePrograms("update");

                    Toast.makeText(getApplicationContext(),  "Program Updated!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    initSavePrograms("add");

                    Toast.makeText(getApplicationContext(), " Program Added", Toast.LENGTH_SHORT).show();
                }

                edt_program.setText("");
                edt_activity.setText("");
                edt_output.setText("");
            }
        });
    }

    public void initSavePrograms(String action)
    {
        try
        {
            ProgramClass programClass = new ProgramClass();

            programClass.setProgramTitle(Objects.requireNonNull(edt_program.getText()).toString());
            programClass.setActivity(Objects.requireNonNull(edt_activity.getText()).toString());
            programClass.setOutput(Objects.requireNonNull(edt_output.getText()).toString());

            switch (action)
            {
                case "add":
                    ProgramRepository.createProgram(programClass, this);
                    break;
                case "update":
                    ProgramRepository.validateUpdatePrograms(programClass, this, "");
                    break;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
}