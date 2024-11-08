package com.geodata.cups.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.geodata.cups.Backend.SQLite.Repository.AttachmentRepository;
import com.geodata.cups.R;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import com.geodata.cups.Backend.SQLite.Class.AttachmentClass;

public class RVAttachmentListReport extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = RVAttachmentListReport.class.getSimpleName();

    Context context;
    List<AttachmentClass> attachmentClasses;

    public RVAttachmentListReport(Context context, List<AttachmentClass> attachmentClasses)
    {
        this.context            = context;
        this.attachmentClasses = attachmentClasses;
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

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount()
    {
        return attachmentClasses.size();
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
            ll_close_attachment   = itemView.findViewById(R.id.ll_close_attachment);

            tv_filename_attachment  = itemView.findViewById(R.id.tv_filename_attachment);
            tv_filesize_attachment  = itemView.findViewById(R.id.tv_filesize_attachment);

            iv_image_attachment   = itemView.findViewById(R.id.iv_image_attachment);
        }

        public void bindView(final int position)
        {
            final AttachmentClass current = attachmentClasses.get(position);

            if (!current.getFileExtension().equals(""))
            {
                String sFileName = "FILE NAME " + current.getFileName();
                tv_filename_attachment.setText(sFileName);
                tv_filesize_attachment.setText(current.getFileSize());

                if (current.getAttachmentDisable().equalsIgnoreCase("YES"))
                {
                    ll_close_attachment.setVisibility(View.GONE);
                }

                try
                {
                    switch (current.getFileExtension())
                    {
                        case "pdf":
                            Glide.with(context).load(R.drawable.pdf_icon).into(iv_image_attachment);
                            break;
                        case "docx":
                            Glide.with(context).load(R.drawable.word_png).into(iv_image_attachment);
                            break;
                        case "ppt":
                        case "pptx":
                            Glide.with(context).load(R.drawable.ppt_png).into(iv_image_attachment);
                            break;
                        case "apk":
                            Glide.with(context).load(R.drawable.apk_icon).into(iv_image_attachment);
                            break;
                        case "avi":
                        case "mpe":
                        case "mpeg":
                        case "mpg":
                        case "3gp":
                        case "mp4":
                        case "MP4":
                            Glide.with(context).load(R.drawable.video_play_icon).into(iv_image_attachment);
                            break;
                        case "bmp":
                        case "gif":
                        case "jpg":
                        case "jpeg":
                        case "png":
                        case "webp":
                        case "heic":
                        case "heif":
                            File Image_File = new File(current.getFilePath());

                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

                            Bitmap bitmap = BitmapFactory.decodeFile(Image_File.getAbsolutePath(), bmOptions);
                            bitmap = initImageRotateNormal(Image_File, bitmap);
                            Glide.with(context).load(bitmap).into(iv_image_attachment);
                            break;
                        default:
                            Glide.with(context).load(R.drawable.file_png).into(iv_image_attachment);
                            break;
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());

                    Glide.with(context).load(R.drawable.corrupted_png).into(iv_image_attachment);
                }
            }
            else
            {
                ll_attachment_details.setVisibility(View.GONE);
            }

            iv_image_attachment.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    initFileView(current.getFilePath(), current.getFileExtension(), current.getFileName());
                }
            });

            ll_close_attachment.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case DialogInterface.BUTTON_POSITIVE:

                                        AttachmentRepository.removeAttachment(context, current.getID());

                                        current.setFilePath("");
                                        current.setFileName("");
                                        current.setFileSize("");
                                        current.setFileExtension("");

                                        attachmentClasses.set(getAdapterPosition(), current);

                                        if (current.getFileExtension().equals(""))
                                        {
                                            ll_attachment_details.setVisibility(View.GONE);
                                        }

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Are you sure you want to delete this ?");
                        builder.setCancelable(false);
                        builder.setNegativeButton("NO", onClickListener);
                        builder.setPositiveButton("YES", onClickListener);
                        builder.show();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void initFileView(String FilePath, String FileExtension, String FileName)
    {
        try
        {
            //File theFile = new File(Environment.getExternalStorageDirectory() + FilePath);
            File theFile = new File(FilePath);

            boolean isVideo = FileExtension.contains("3gp") || FileExtension.contains("mpg") || FileExtension.contains("mpeg") ||
                              FileExtension.contains("mpe") || FileExtension.contains("mp4") || FileExtension.contains("avi");

            boolean isJPEG = FileExtension.contains("jpg") || FileExtension.contains("jpeg") || FileExtension.contains("png");

            if (Build.VERSION.SDK_INT >= 22)
            {
                try
                {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);

                    if (theFile.exists())
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);

                        try
                        {
                            Uri ff = Uri.fromFile(theFile);

                            if (FileExtension.contains("doc") || FileExtension.contains("docx"))
                            {
                                // Word document
                                intent.setDataAndType(ff, "application/msword");
                            }
                            else if (FileExtension.contains("pdf"))
                            {
                                // PDF file
                                intent.setDataAndType(ff, "application/pdf");
                            }
                            else if (FileExtension.contains("ppt") || FileExtension.contains("pptx"))
                            {
                                // Powerpoint file
                                intent.setDataAndType(ff, "application/vnd.ms-powerpoint");
                            }
                            else if (FileExtension.contains("xls") || FileExtension.contains("xlsx"))
                            {
                                // Excel file
                                intent.setDataAndType(ff, "application/vnd.ms-excel");
                            }
                            else if (FileExtension.contains("zip"))
                            {
                                // ZIP file
                                intent.setDataAndType(ff, "application/zip");
                            }
                            else if (FileExtension.contains("rar"))
                            {
                                // RAR file
                                intent.setDataAndType(ff, "application/x-rar-compressed");
                            }
                            else if (FileExtension.contains("rtf"))
                            {
                                // RTF file
                                intent.setDataAndType(ff, "application/rtf");
                            }
                            else if (FileExtension.contains("wav") || FileExtension.contains("mp3"))
                            {
                                // WAV audio file
                                intent.setDataAndType(ff, "audio/x-wav");
                            }
                            else if (FileExtension.contains("gif"))
                            {
                                // GIF file
                                intent.setDataAndType(ff, "image/gif");
                            }
                            else if (isJPEG)
                            {
                                // JPG file
                                intent.setDataAndType(ff, "image/jpeg");
                            }
                            else if (FileExtension.contains("txt"))
                            {
                                // Text file
                                intent.setDataAndType(ff, "text/plain");
                            }
                            else if (isVideo)
                            {
                                // Video files
                                intent.setDataAndType(ff, "video/*");
                            }
                            else
                            {
                                intent.setDataAndType(ff, "*/*");
                            }

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        catch (ActivityNotFoundException e)
                        {
                            Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();

                           Log.e(TAG, e.toString());
                        }
                    }
                    else
                        Toast.makeText(context, "File Corrupted", Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    Log.e(TAG,  e.toString());

                    Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                if (theFile.exists())
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    try
                    {
                        Uri ff = Uri.fromFile(theFile);

                        if (FileExtension.contains("doc") || FileExtension.contains("docx"))
                        {
                            // Word document
                            intent.setDataAndType(ff, "application/msword");
                        }
                        else if (FileExtension.contains("pdf"))
                        {
                            // PDF file
                            intent.setDataAndType(ff, "application/pdf");
                        }
                        else if (FileExtension.contains("ppt") || FileExtension.contains("pptx"))
                        {
                            // Powerpoint file
                            intent.setDataAndType(ff, "application/vnd.ms-powerpoint");
                        }
                        else if (FileExtension.contains("xls") || FileExtension.contains("xlsx"))
                        {
                            // Excel file
                            intent.setDataAndType(ff, "application/vnd.ms-excel");
                        }
                        else if (FileExtension.contains("zip"))
                        {
                            // ZIP file
                            intent.setDataAndType(ff, "application/zip");
                        }
                        else if (FileExtension.contains("rar"))
                        {
                            // RAR file
                            intent.setDataAndType(ff, "application/x-rar-compressed");
                        }
                        else if (FileExtension.contains("rtf"))
                        {
                            // RTF file
                            intent.setDataAndType(ff, "application/rtf");
                        }
                        else if (FileExtension.contains("wav") || FileExtension.contains("mp3"))
                        {
                            // WAV audio file
                            intent.setDataAndType(ff, "audio/x-wav");
                        }
                        else if (FileExtension.contains("gif"))
                        {
                            // GIF file
                            intent.setDataAndType(ff, "image/gif");
                        }
                        else if (isJPEG)
                        {
                            // JPG file
                            intent.setDataAndType(ff, "image/jpeg");
                        }
                        else if (FileExtension.contains("txt"))
                        {
                            // Text file
                            intent.setDataAndType(ff, "text/plain");
                        }
                        else if (isVideo)
                        {
                            // Video files
                            intent.setDataAndType(ff, "video/*");
                        }
                        else
                        {
                            intent.setDataAndType(ff, "*/*");
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        context.startActivity(intent);
                    }
                    catch (ActivityNotFoundException e)
                    {
                        Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();

                        Log.e(TAG, e.toString());
                    }
                }
                else
                    Toast.makeText(context, "File Corrupted", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,  e.toString());

            Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }

    //Customize Bitmap to Normal angle of Picture
    private static Bitmap initImageRotateNormal(File imagePath, Bitmap bitmap)
    {
        ExifInterface ei = null;

        try
        {
            ei = new ExifInterface((imagePath.getAbsolutePath()));
        }
        catch (IOException e)
        {
            Log.e(TAG, e.toString());
        }

        int orientation = Objects.requireNonNull(ei).getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap;

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    //Fix Auto rotate in Some Camera
    private static Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0,0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
