package com.geodata.cups.RecyclerViewAdapter;

import android.annotation.SuppressLint;
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

import com.geodata.cups.Backend.SQLite.Class.AttachmentClass;
import com.geodata.cups.Backend.SQLite.Class.ProgramClass;
import com.geodata.cups.Backend.SQLite.Repository.AttachmentRepository;
import com.geodata.cups.R;
import java.util.ArrayList;
import java.util.List;

public class RVMainListReport extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVMainListReport.class.getSimpleName();

    Context context;
    List<ProgramClass> programClass;

    public RVMainListReport(List<ProgramClass> programClass, Context context)
    {
        this.programClass = programClass;
        this.context      = context;
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
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position)
    {
        ((MyHolder) holder).bindView(position);

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount()
    {
        return programClass.size();
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

        public void bindView(final int position)
        {
            final ProgramClass current = programClass.get(position);

            String DisplayID, program, activity, output, remarks;

            DisplayID = current.getDisplayID();
            program   = current.getProgramTitle();
            activity  = current.getActivity();
            output    = current.getOutput();

            tv_displayID.setText(DisplayID);
            tv_program.setText(program);
            tv_activity.setText(activity);
            tv_output.setText(output);

            if (current.getRemarks().isEmpty() && current.getRemarks().equals(""))
            {
                tv_remarks.setText("None");
            }
            else
            {
                tv_remarks.setText(current.getRemarks());
            }

            try
            {
                List<AttachmentClass> attachmentClasses = new ArrayList<>();

                RVAttachmentListReport RVAttachmentListReport = new RVAttachmentListReport(context, attachmentClasses);

                rv_attachment_list.setHasFixedSize(true);
                rv_attachment_list.setLayoutManager(new LinearLayoutManager(context));
                rv_attachment_list.setAdapter(RVAttachmentListReport);

                Cursor cursor = AttachmentRepository.retrieveMainAttachment(context, current.getActivityID());

                if (cursor.getCount()!=0)
                {
                    if (cursor.moveToFirst())
                    {
                        attachmentClasses.clear();

                        do
                        {
                            AttachmentClass cValues = new AttachmentClass();

                            cValues.setID(cursor.getString(cursor.getColumnIndex("ID")));
                            cValues.setFilePath(cursor.getString(cursor.getColumnIndex("FilePath")));
                            cValues.setFileName(cursor.getString(cursor.getColumnIndex("FileName")));
                            cValues.setFileExtension(cursor.getString(cursor.getColumnIndex("FileExtension")));
                            cValues.setFileSize(cursor.getString(cursor.getColumnIndex("FileSize")));
                            cValues.setAttachmentDisable("YES");
                            cValues.setActivityID(cursor.getString(cursor.getColumnIndex("ActivityID")));

                            attachmentClasses.add(cValues);
                        }
                        while (cursor.moveToNext());

                        RVAttachmentListReport.notifyDataSetChanged();
                    }
                }
                else
                {
                    tv_attachment.setVisibility(View.VISIBLE);
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }
}