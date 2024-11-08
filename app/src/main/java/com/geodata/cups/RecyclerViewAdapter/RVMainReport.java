package com.geodata.cups.RecyclerViewAdapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.geodata.cups.Backend.SQLite.Class.MainReportClass;
import com.geodata.cups.Backend.SQLite.Class.ProgramClass;
import com.geodata.cups.Backend.SQLite.Repository.ProgramRepository;
import com.geodata.cups.R;
import java.util.ArrayList;
import java.util.List;

public class RVMainReport extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVMainReport.class.getSimpleName();

    Context context;
    List<MainReportClass> mainReportClasses;

    public RVMainReport(Context context, List<MainReportClass> mainReportClasses)
    {
        this.context          = context;
        this.mainReportClasses = mainReportClasses;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_main_layout_report, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ((MyHolder) holder).bindView(position);

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount()
    {
        return mainReportClasses.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder
    {
        TextView tv_date_of_duty, tv_date_time_submission;

        RecyclerView rv_history;

        public MyHolder(View view)
        {
            super(view);

            tv_date_of_duty         = itemView.findViewById(R.id.tv_date_of_duty);
            tv_date_time_submission = itemView.findViewById(R.id.tv_date_time_submission);

            rv_history               = itemView.findViewById(R.id.rv_history);
        }

        public void bindView(final int position)
        {

            tv_date_of_duty.setText(mainReportClasses.get(position).getDateOfDuty());

            tv_date_time_submission.setText(mainReportClasses.get(position).getDateSubmitted());

            try
            {
                List<ProgramClass> programClasses = new ArrayList<>();

                RVMainListReport RVMainListReport = new RVMainListReport(programClasses, context);

                rv_history.setHasFixedSize(true);
                rv_history.setLayoutManager(new LinearLayoutManager(context));
                rv_history.setAdapter(RVMainListReport);

                Cursor cursor = ProgramRepository.retrieveMainSubmittedReport2(context, mainReportClasses.get(position).getDateSubmitted());

                if (cursor.getCount()!=0)
                {
                    if (cursor.moveToFirst())
                    {
                        programClasses.clear();

                        do
                        {
                            ProgramClass cValues = new ProgramClass();

                            cValues.setID(cursor.getString(cursor.getColumnIndex("ID")));
                            cValues.setDisplayID(cursor.getString(cursor.getColumnIndex("DisplayID")));
                            cValues.setProgramID(cursor.getString(cursor.getColumnIndex("ProgramID")));
                            cValues.setProgramTitle(cursor.getString(cursor.getColumnIndex("ProgramTitle")));
                            cValues.setOutput(cursor.getString(cursor.getColumnIndex("Activity")));
                            cValues.setActivity(cursor.getString(cursor.getColumnIndex("Output")));
                            cValues.setRemarks(cursor.getString(cursor.getColumnIndex("Remarks")));
                            cValues.setActivityID(cursor.getString(cursor.getColumnIndex("ActivityID")));

                            programClasses.add(cValues);
                        }
                        while (cursor.moveToNext());

                        RVMainListReport.notifyDataSetChanged();
                    }
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }
}
