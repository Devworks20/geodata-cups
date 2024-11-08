package com.geodata.cups.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.geodata.cups.Backend.Activity.AttachmentWebViewActivity;
import com.geodata.cups.Backend.Activity.TempFileViewActivity;
import com.geodata.cups.Backend.Retrofit.Model.OnlineHistory.AttachmentHistoryModel;
import com.geodata.cups.Backend.Retrofit.Model.Other.UsernameInfo;
import com.geodata.cups.Backend.SQLite.Repository.AttachmentRepository;
import com.geodata.cups.R;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class RVOnlineAttachmentListReport extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVOnlineAttachmentListReport.class.getSimpleName();

    Context context;
    List<AttachmentHistoryModel> attachmentHistoryModelList;

    public RVOnlineAttachmentListReport(Context context, List<AttachmentHistoryModel> attachmentHistoryModels)
    {
        this.context                     = context;
        this.attachmentHistoryModelList = attachmentHistoryModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_attachment_list_reports,parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position)
    {
        ((MyHolder) holder).bindView(position);

        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount()
    {
        return attachmentHistoryModelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_image_attachment;

        TextView tv_filename_attachment, tv_filesize_attachment;

        LinearLayout  ll_attachment_details, ll_close_attachment;

        public MyHolder(View itemView)
        {
            super(itemView);

            ll_attachment_details  = itemView.findViewById(R.id.ll_attachment_details);

            ll_close_attachment    = itemView.findViewById(R.id.ll_close_attachment);
            ll_close_attachment.setVisibility(View.GONE);

            tv_filename_attachment = itemView.findViewById(R.id.tv_filename_attachment);
            tv_filesize_attachment = itemView.findViewById(R.id.tv_filesize_attachment);

            iv_image_attachment    = itemView.findViewById(R.id.iv_image_attachment);
        }

        public void bindView(final int position)
        {
            final AttachmentHistoryModel current = attachmentHistoryModelList.get(position);

            if (current.getAttachmentPath() != null && !current.getAttachmentPath().equals(""))
            {
                final String AttachmentPath      = current.getAttachmentPath();

                Log.e(TAG, "AttachmentPath: " + AttachmentPath);

                final String AttachmentExtension = AttachmentPath.substring(AttachmentPath.lastIndexOf(".") + 1);

                String sFileName         = AttachmentPath.substring(AttachmentPath.lastIndexOf("/") + 1 );
                String[] splitFileName   = sFileName.split("file-" + UsernameInfo.AccountID + "-");
                String FileName          = splitFileName[1];
                String customizeFileName =  "FILE NAME " + FileName;

                tv_filename_attachment.setText(customizeFileName);

                //Set Image - Icon
                initSetImageOnline(context, AttachmentExtension, iv_image_attachment);

                Cursor cursor = AttachmentRepository.retrieveAttachment2(context, FileName);

                if (cursor.getCount()!=0)
                {
                    if (cursor.moveToFirst())
                    {
                        String Filesize = cursor.getString(cursor.getColumnIndex("FileSize"));
                        tv_filesize_attachment.setText(Filesize);

                        final String FilePath      = cursor.getString(cursor.getColumnIndex("FilePath"));
                        final String FileExtension = cursor.getString(cursor.getColumnIndex("FileName"));

                        iv_image_attachment.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(context, TempFileViewActivity.class);
                                intent.putExtra("FilePath", FilePath);
                                intent.putExtra("FileExtension", FileExtension);
                                ((Activity) context).startActivityForResult(intent, 202);
                            }
                        });
                    }
                }
                else
                {
                    iv_image_attachment.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            initViewImageOnline(context, AttachmentExtension, AttachmentPath);
                        }
                    });
                }
            }
            else
            {
                ll_attachment_details.setVisibility(View.GONE);
            }
        }
    }

    private void initSetImageOnline(Context context, String AttachmentExtension, ImageView imageView)
    {
        try
        {
            switch (AttachmentExtension)
            {
                case "pdf":
                    Glide.with(context).load(R.drawable.pdf_icon).into(imageView);
                    break;
                case "docx":
                case "docs":
                    Glide.with(context).load(R.drawable.word_png).into(imageView);
                    break;
                case "ppt":
                case "pptx":
                    Glide.with(context).load(R.drawable.ppt_png).into(imageView);
                    break;
                case "apk":
                    Glide.with(context).load(R.drawable.apk_icon).into(imageView);
                    break;
                case "avi":
                case "mpe":
                case "mpeg":
                case "mpg":
                case "3gp":
                case "mp4":
                case "MP4":
                case "video":
                    Glide.with(context).load(R.drawable.video_play_icon).into(imageView);
                    break;
                case "bmp":
                case "gif":
                case "jpg":
                case "jpeg":
                case "png":
                case "webp":
                case "heic":
                case "heif":
                case "image":
                    Glide.with(context).load(R.drawable.image_icon_png).into(imageView);
                    break;
                default:
                    Glide.with(context).load(R.drawable.file_png).into(imageView);
                    break;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initViewImageOnline(Context context, String AttachmentExtension, String AttachmentPath)
    {
        try
        {
            Intent intent = new Intent(context, AttachmentWebViewActivity.class);

            switch (AttachmentExtension)
            {
                case "pdf":
                    DownloadFiles("PDF", AttachmentPath, AttachmentExtension);
                    break;
                case "docx":
                case "docs":
                    DownloadFiles("DOCS", AttachmentPath, AttachmentExtension);
                    break;
                case "ppt":
                case "pptx":
                    DownloadFiles("PPT", AttachmentPath, AttachmentExtension);
                    break;
                case "apk":
                    DownloadFiles("APK", AttachmentPath, AttachmentExtension);
                    break;
                case "avi":
                case "mpe":
                case "mpeg":
                case "mpg":
                case "3gp":
                case "mp4":
                case "MP4":
                case "video":
                    intent.putExtra("Type", "Video");
                    intent.putExtra("AttachmentPath", AttachmentPath);
                    //context.startActivity(intent);
                    ((Activity) context).startActivityForResult(intent, 202);
                    break;
                case "bmp":
                case "gif":
                case "jpg":
                case "jpeg":
                case "png":
                case "webp":
                case "heic":
                case "heif":
                case "image":
                    intent.putExtra("Type", "Image");
                    intent.putExtra("AttachmentPath", AttachmentPath);
                    //context.startActivity(intent);
                    ((Activity) context).startActivityForResult(intent, 202);
                    break;
                default:
                    DownloadFiles("FILE", AttachmentPath, AttachmentExtension);
                    break;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public void DownloadFiles(final String fileName, final String fileURL, final String fileExtension)
    {
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        String AttachmentPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/CUPS/" + ".Attachment";

                        File fileAttachment = new File(AttachmentPath);

                        String FullFilePath = AttachmentPath + "/" + fileName + "." + fileExtension;

                        if (!fileAttachment.exists())
                        {
                            File wallpaperDirectory = new File(AttachmentPath);
                            wallpaperDirectory.mkdirs();
                        }

                        URL u = new URL(fileURL);
                        InputStream is = u.openStream();

                        DataInputStream dis = new DataInputStream(is);

                        byte[] buffer = new byte[1024];
                        int length;

                        FileOutputStream fos = new FileOutputStream(new File(AttachmentPath + "/" + fileName + "." + fileExtension));

                        while ((length = dis.read(buffer))>0)
                        {
                            fos.write(buffer, 0, length);
                        }

                        Intent intent = new Intent(context, TempFileViewActivity.class);
                        intent.putExtra("FilePath", FullFilePath);
                        intent.putExtra("FileExtension", fileExtension);
                        ((Activity) context).startActivityForResult(intent, 202);
                    }
                    catch (IOException | SecurityException mue)
                    {
                        Log.e(TAG, mue.toString());
                    }
                }
            });

            thread.start();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
}
