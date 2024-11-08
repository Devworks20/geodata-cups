package com.geodata.cups.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.geodata.cups.Backend.Retrofit.Model.OnlineHistory.AttachmentHistoryModel;
import com.geodata.cups.Backend.Retrofit.Model.OnlineHistory.ProgramHistoryModel;
import com.geodata.cups.R;
import java.util.ArrayList;
import java.util.List;

public class RVOnlineMainListReport extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVOnlineMainListReport.class.getSimpleName();

    Context context;
    List<ProgramHistoryModel> programHistoryModelList;

    public RVOnlineMainListReport(Context context, List<ProgramHistoryModel> programHistoryModels)
    {
        this.context                 = context;
        this.programHistoryModelList = programHistoryModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_main_reports,parent, false);

        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ((MyHolder) holder).bindView(position);

        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount()
    {
        return programHistoryModelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView tv_displayID, tv_program, tv_activity, tv_output, tv_remarks, tv_attachment;

        RecyclerView rv_attachment_list;

        public MyHolder(View itemView)
        {
            super(itemView);

            tv_displayID       = itemView.findViewById(R.id.tv_displayID);
            tv_program         = itemView.findViewById(R.id.tv_program);
            tv_activity        = itemView.findViewById(R.id.tv_activity);
            tv_output          = itemView.findViewById(R.id.tv_output);
            tv_remarks         = itemView.findViewById(R.id.tv_remarks);
            tv_attachment      = itemView.findViewById(R.id.tv_attachment);

            rv_attachment_list = itemView.findViewById(R.id.rv_attachment_list);
        }

        @SuppressLint("NotifyDataSetChanged")
        public void bindView(final int position)
        {
            final ProgramHistoryModel current = programHistoryModelList.get(position);

            tv_displayID.setText(current.getProgramDisplayID());
            tv_program.setText(current.getProgramTitle());
            tv_activity.setText(current.getActivity());
            tv_output.setText(current.getOutput());

            if (current.getRemarks() != null && !current.getRemarks().equals(""))
            {
                tv_remarks.setText(current.getRemarks());
            }
            else
            {
                String None = "None";
                tv_remarks.setText(None);
            }

            try
            {
                List<AttachmentHistoryModel> attachmentHistoryModelList = new ArrayList<>();

                RVOnlineAttachmentListReport rvOnlineAttachmentListReport = new RVOnlineAttachmentListReport(context, attachmentHistoryModelList);

                rv_attachment_list.setHasFixedSize(true);
                rv_attachment_list.setLayoutManager(new LinearLayoutManager(context));
                rv_attachment_list.setAdapter(rvOnlineAttachmentListReport);

                attachmentHistoryModelList.clear();

                //this.....
                for (int f = 0; f < programHistoryModelList.get(position).getAttachmentHistoryModelList().size(); f++)
                {
                    AttachmentHistoryModel cValues = new AttachmentHistoryModel();

                    Log.e(TAG, "CATEGORY TITLE : "  + current.getProgramTitle() +
                                    "\nATTACHMENT PATH: " + programHistoryModelList.get(position).getAttachmentHistoryModelList().get(f).getAttachmentPath());

                    cValues.setAttachmentPath(programHistoryModelList.get(position).getAttachmentHistoryModelList().get(f).getAttachmentPath());
                    cValues.setType(programHistoryModelList.get(position).getAttachmentHistoryModelList().get(f).getType());

                    attachmentHistoryModelList.add(cValues);
                }
                rvOnlineAttachmentListReport.notifyDataSetChanged();
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }
}