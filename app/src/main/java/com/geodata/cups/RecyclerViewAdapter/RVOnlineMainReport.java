package com.geodata.cups.RecyclerViewAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.geodata.cups.Backend.Retrofit.Model.OnlineHistory.MainReportHistoryModel;
import com.geodata.cups.Backend.Retrofit.Model.OnlineHistory.ProgramHistoryModel;
import com.geodata.cups.R;
import java.util.ArrayList;
import java.util.List;

public class RVOnlineMainReport extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVOnlineMainReport.class.getSimpleName();

    Context context;
    List<MainReportHistoryModel> mainReportHistoryModelList;

    public RVOnlineMainReport(Context context, List<MainReportHistoryModel> mainReportClasses)
    {
        this.context                    = context;
        this.mainReportHistoryModelList = mainReportClasses;
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

        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount()
    {
        return mainReportHistoryModelList.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder
    {
        TextView tv_date_of_duty, tv_timeOfDuty, tv_date_time_submission;

        RecyclerView rv_history;

        public MyHolder(View view)
        {
            super(view);

            tv_date_of_duty         = itemView.findViewById(R.id.tv_date_of_duty);
            tv_timeOfDuty           = itemView.findViewById(R.id.tv_timeOfDuty);
            tv_date_time_submission = itemView.findViewById(R.id.tv_date_time_submission);

            rv_history               = itemView.findViewById(R.id.rv_history);
        }

        public void bindView(final int position)
        {
            final MainReportHistoryModel current = mainReportHistoryModelList.get(position);

            tv_date_of_duty.setText(current.getDateofDuty());

            if (current.getTimeofDuty()!= null && !current.getTimeofDuty().equals(""))
            {
                tv_timeOfDuty.setText(current.getTimeofDuty());
            }
            String dateTimeSubmission =current.getDateSubmission() + " " + current.getTimeSubmission();

            tv_date_time_submission.setText(dateTimeSubmission);

            try
            {
                List<ProgramHistoryModel> programHistoryModelList = new ArrayList<>();

                RVOnlineMainListReport recyclerViewMainReportList = new RVOnlineMainListReport(context, programHistoryModelList);

                rv_history.setHasFixedSize(true);
                rv_history.setLayoutManager(new LinearLayoutManager(context));
                rv_history.setAdapter(recyclerViewMainReportList);

                programHistoryModelList.clear();

                for (int f = 0; f < mainReportHistoryModelList.get(position).getProgramHistoryModelList().size(); f++)
                {
                    ProgramHistoryModel cValues = new ProgramHistoryModel();

                    cValues.setProgramDisplayID(mainReportHistoryModelList.get(position).getProgramHistoryModelList().get(f).getProgramDisplayID());
                    cValues.setProgramTitle(mainReportHistoryModelList.get(position).getProgramHistoryModelList().get(f).getProgramTitle());
                    cValues.setActivity(mainReportHistoryModelList.get(position).getProgramHistoryModelList().get(f).getActivity());
                    cValues.setOutput(mainReportHistoryModelList.get(position).getProgramHistoryModelList().get(f).getOutput());
                    cValues.setRemarks(mainReportHistoryModelList.get(position).getProgramHistoryModelList().get(f).getRemarks());

                    cValues.setAttachmentHistoryModelList(mainReportHistoryModelList.get(position).getProgramHistoryModelList().get(f).getAttachmentHistoryModelList());

                    programHistoryModelList.add(cValues);
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }
}
