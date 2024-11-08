package com.geodata.cups.RecyclerViewAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.geodata.cups.Backend.Activity.TempFileAttachActivity;
import com.geodata.cups.Backend.SQLite.Class.AttachmentClass;
import com.geodata.cups.Backend.SQLite.Class.ProgramClass;
import com.geodata.cups.Backend.SQLite.Repository.AttachmentRepository;
import com.geodata.cups.Backend.SQLite.Repository.ProgramRepository;
import com.geodata.cups.R;
import java.util.ArrayList;
import java.util.List;

public class RVNewReport extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVNewReport.class.getSimpleName();

    Context context;
    List<ProgramClass> programClassList;

    public RVNewReport(Context context, List<ProgramClass> programClasses)
    {
        this.context          = context;
        this.programClassList = programClasses;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_update_reports,parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        holder.setIsRecyclable(false);

        ((MyHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount()
    {
        return programClassList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView tv_displayID, tv_programTitle, tv_activity, tv_output, tv_attach_file, tv_remarks_display;

        EditText edt_remarks;

        LinearLayout ll_background_reports, ll_check_reports;

        ImageView iv_checked_reports;

        RecyclerView rv_attachment_list;

        public MyHolder(View itemView)
        {
            super(itemView);

            tv_displayID            = itemView.findViewById(R.id.tv_displayID);
            tv_programTitle         = itemView.findViewById(R.id.tv_programTitle);
            tv_activity             = itemView.findViewById(R.id.tv_activity);
            tv_output               = itemView.findViewById(R.id.tv_output);

            tv_attach_file          = itemView.findViewById(R.id.tv_attach_file);
            tv_remarks_display      = itemView.findViewById(R.id.tv_remarks_display);

            edt_remarks           = itemView.findViewById(R.id.edt_remarks);
            ll_background_reports = itemView.findViewById(R.id.ll_background_reports);
            ll_check_reports      = itemView.findViewById(R.id.ll_check_reports);

            iv_checked_reports    = itemView.findViewById(R.id.iv_checked_reports);

            rv_attachment_list    = itemView.findViewById(R.id.rv_attachment_list);
        }

        public void bindView(final int position)
        {
            final ProgramClass current = programClassList.get(position);

            tv_displayID.setText(String.valueOf(current.getDisplayID()));
            tv_programTitle.setText(current.getProgramTitle());
            tv_activity.setText(current.getActivity());
            tv_output.setText(current.getOutput());

            if (current.getIsCheckedValue().equals("1"))
            {
                tv_remarks_display.setVisibility(View.VISIBLE);

                edt_remarks.setVisibility(View.VISIBLE);
                edt_remarks.setText(current.getRemarks());

                programClassList.set(getAdapterPosition(), current);

                tv_attach_file.setBackgroundResource(R.drawable.custom_background_active_attachment);
                tv_attach_file.setTextColor(Color.BLACK);
                tv_attach_file.setEnabled(true);

                ll_background_reports.setBackgroundResource(R.color.ProgressColor);
                iv_checked_reports.setVisibility(View.VISIBLE);
                ll_check_reports.setVisibility(View.GONE);
            }
            else
            {
                tv_remarks_display.setVisibility(View.GONE);
                edt_remarks.setVisibility(View.GONE);

                tv_attach_file.setBackgroundResource(R.drawable.custom_background_gray);
                tv_attach_file.setTextColor(ContextCompat.getColor(context, R.color.normalColor));
                tv_attach_file.setEnabled(false);

                ll_background_reports.setBackgroundResource(R.color.darkGray);
                iv_checked_reports.setVisibility(View.GONE);
                ll_check_reports.setVisibility(View.VISIBLE);
            }

            ll_check_reports.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ll_check_reports.setVisibility(View.GONE);
                    ll_background_reports.setBackgroundResource(R.color.ProgressColor);
                    iv_checked_reports.setVisibility(View.VISIBLE);

                    tv_attach_file.setEnabled(true);

                    tv_attach_file.setBackgroundResource(R.drawable.custom_background_active_attachment);
                    tv_attach_file.setTextColor(Color.BLACK);

                    tv_remarks_display.setVisibility(View.VISIBLE);
                    edt_remarks.setVisibility(View.VISIBLE);
                    edt_remarks.requestFocus();
                    edt_remarks.setText("");

                    current.setIsCheckedValue("1");
                    current.setRemarks("");
                    programClassList.set(getAdapterPosition(), current);
                }
            });

            iv_checked_reports.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    iv_checked_reports.setVisibility(View.GONE);
                    ll_background_reports.setBackgroundResource(R.color.darkGray);
                    ll_check_reports.setVisibility(View.VISIBLE);

                    tv_attach_file.setEnabled(false);
                    tv_attach_file.setBackgroundResource(R.drawable.custom_background_gray);
                    tv_attach_file.setTextColor(ContextCompat.getColor(context, R.color.normalColor));

                    tv_remarks_display.setVisibility(View.GONE);
                    edt_remarks.clearFocus();
                    edt_remarks.setVisibility(View.GONE);
                    edt_remarks.setText("");

                    current.setIsCheckedValue("0");
                    current.setRemarks("");
                    programClassList.set(getAdapterPosition(), current);
                }
            });

            tv_attach_file.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    edt_remarks.requestFocus();
                    edt_remarks.clearFocus();
                    edt_remarks.onEditorAction(EditorInfo.IME_ACTION_DONE);

                    Intent intent = new Intent(context, TempFileAttachActivity.class);
                    intent.putExtra("ID", current.getID());
                    intent.putExtra("Remarks", edt_remarks.getText().toString());
                    ((Activity) context).startActivityForResult(intent, 100);
                }
            });

            edt_remarks.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {

                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    if (tv_attach_file.isEnabled())
                    {
                        //Log.e(TAG, "REMARKS IS UPDATE ID: " + current.getID());

                        ProgramRepository.updateProgramRemarks(context, "1", s.toString(), current.getID());

                        current.setIsCheckedValue("1");
                        current.setRemarks(s.toString());
                        programClassList.set(getAdapterPosition(), current);
                    }
                }
            });

            /*edt_remarks.setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if (!hasFocus)
                    {
                        if (tv_attach_file.isEnabled())
                        {
                            //Log.e(TAG, "REMARKS IS UPDATE ID: " + current.getID());

                            EditText editText = (EditText) v;
                            String text = editText.getText().toString();

                            ProgramRepository.updateProgramRemarks(v.getContext(), "1", text, current.getID());

                            current.setIsCheckedValue("1");
                            current.setRemarks(text);
                            programClassList.set(getAdapterPosition(), current);
                        }
                    }
                }
            });*/

            try
            {
                List<AttachmentClass> attachmentClasses = new ArrayList<>();

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
                            cValues.setAttachmentDisable("NO");

                            attachmentClasses.add(cValues);
                        }
                        while (cursor.moveToNext());

                        RVAttachmentListReport.notifyDataSetChanged();

                    }
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }

    public void filterCategoryList(ArrayList<ProgramClass> filteredList)
    {
        programClassList = filteredList;

        notifyDataSetChanged();
    }
}
