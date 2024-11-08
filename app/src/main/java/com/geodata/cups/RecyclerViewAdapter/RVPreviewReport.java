package com.geodata.cups.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geodata.cups.Backend.SQLite.Class.AttachmentClass;
import com.geodata.cups.Backend.SQLite.Class.ProgramClass;
import com.geodata.cups.Backend.SQLite.Repository.AttachmentRepository;
import com.geodata.cups.R;
import java.util.ArrayList;
import java.util.List;

public class RVPreviewReport extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVPreviewReport.class.getSimpleName();

    Context context;
    List<ProgramClass> ProgramClasses;

    public RVPreviewReport(Context context, List<ProgramClass> programClasses)
    {
        this.ProgramClasses = programClasses;
        this.context        = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_new_cups_submit_confirmation_layout,parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, @SuppressLint("RecyclerView") final int position)
    {
        ((MyHolder) holder).bindView(position);

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount()
    {
        return ProgramClasses.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        View view_id_1;

        TextView tv_displayID, tv_programTitle, tv_activity, tv_output, tv_remarks, tv_remarks_display, tv_attachment;

        RecyclerView rv_attachment_list;

        public MyHolder(View itemView)
        {
            super(itemView);

            view_id_1               = itemView.findViewById(R.id.view_id_1);
            tv_displayID            = itemView.findViewById(R.id.tv_displayID);
            tv_programTitle         = itemView.findViewById(R.id.tv_programTitle);
            tv_activity             = itemView.findViewById(R.id.tv_activity);
            tv_output               = itemView.findViewById(R.id.tv_output);
            tv_remarks              = itemView.findViewById(R.id.tv_remarks);
            tv_remarks_display      = itemView.findViewById(R.id.tv_remarks_display);
            rv_attachment_list      = itemView.findViewById(R.id.rv_attachment_list);
            tv_attachment           = itemView.findViewById(R.id.tv_attachment);
        }

        public void bindView(final int position)
        {
            if (position == 0)
            {
              //  view_id_1.setVisibility(View.GONE);
            }

            final ProgramClass current = ProgramClasses.get(position);

            tv_displayID.setText(current.getDisplayID());
            tv_programTitle.setText(current.getProgramTitle());
            tv_activity.setText(current.getActivity());
            tv_output.setText(current.getOutput());

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
                List<AttachmentClass> attachmentClasses  = new ArrayList<>();

                RVAttachmentListReport RVAttachmentListReport = new RVAttachmentListReport(context, attachmentClasses);

                rv_attachment_list.setHasFixedSize(true);
                rv_attachment_list.setLayoutManager(new LinearLayoutManager(context));
                rv_attachment_list.setAdapter(RVAttachmentListReport);

                Cursor cursor = AttachmentRepository.retrieveAttachment(context, current.getActivityID());

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

                            attachmentClasses.add(cValues);
                        }
                        while (cursor.moveToNext());

                        RVAttachmentListReport.notifyDataSetChanged();
                    }
                }
                else
                {
                    tv_attachment.setVisibility(View.VISIBLE);
                    tv_attachment.setText("None");
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }
}
