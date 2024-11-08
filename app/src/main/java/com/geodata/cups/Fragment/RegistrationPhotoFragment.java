package com.geodata.cups.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.geodata.cups.Activity.RegistrationActivity;
import com.geodata.cups.Backend.Tools.RealPathUtils;
import com.geodata.cups.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationPhotoFragment extends Fragment
{

    private static final String TAG = RegistrationPhotoFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistrationPhotoFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationPhotoFragment newInstance(String param1, String param2)
    {
        RegistrationPhotoFragment fragment = new RegistrationPhotoFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    CircleImageView registration_image;
    TextView tv_take_photo, tv_upload_photo;

    View mainView;

    public static int MY_PERMISSIONS_REQUEST_CODE_STORAGE_AND_CAMERA_1 = 101;
    public static int MY_PERMISSIONS_REQUEST_CODE_CAMERA = 102;
    public static int MY_PERMISSIONS_REQUEST_CODE_STORAGE = 103;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    private static final int IMAGE_PICK_GALLERY_CODE = 1002;

    String mCameraFileName, sSaveFolderName;
    Uri imageUri;
    File fProfileImage;
    Boolean isProfilePictureAvailable = false;

    String FilePath, FileName, FileExtension, FileSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mainView =   inflater.inflate(R.layout.fragment_registration_photo, container, false);

        initViews();

        return mainView;
    }

    private void initViews()
    {
        registration_image  = mainView.findViewById(R.id.registration_image);
        tv_take_photo       = mainView.findViewById(R.id.tv_take_photo);
        tv_upload_photo     = mainView.findViewById(R.id.tv_upload_photo);

        initListeners();
    }

    private void initListeners()
    {
        registration_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        }, MY_PERMISSIONS_REQUEST_CODE_STORAGE_AND_CAMERA_1);
                    }
                    else
                    {
                        if (ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            requestPermissions(new String[]{
                                    Manifest.permission.CAMERA
                            }, MY_PERMISSIONS_REQUEST_CODE_STORAGE_AND_CAMERA_1);
                        }
                        else
                        {
                            initValidateCreateAndRetrieveImage();

                            if (!fProfileImage.exists())
                            {
                                initTakePhoto();
                            }
                            else
                            {
                                if (isProfilePictureAvailable)
                                {
                                    initLoadPhoto();
                                }
                                else
                                {
                                    initTakePhoto();
                                }
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });

        tv_take_photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        }, MY_PERMISSIONS_REQUEST_CODE_CAMERA);
                    }
                    else
                    {
                        if (ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            requestPermissions(new String[]{
                                    Manifest.permission.CAMERA
                            }, MY_PERMISSIONS_REQUEST_CODE_CAMERA);
                        }
                        else
                        {
                            initGetPictureCamera();
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });

        tv_upload_photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        }, MY_PERMISSIONS_REQUEST_CODE_STORAGE);
                    }
                    else
                    {
                        if (ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            requestPermissions(new String[]{
                                    Manifest.permission.CAMERA
                            }, MY_PERMISSIONS_REQUEST_CODE_STORAGE);
                        }
                        else
                        {
                            initGetPictureGallery();
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    private void initSettingsPrompt(final String requestPermission, final int requestPermissionCode)
    {
        try
        {
            final AlertDialog.Builder ADSettings = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_dialog_title, null);
            TextView textView = view.findViewById(R.id.tv_dialog_title);
            String sTitle = "Permission Request";
            textView.setText(sTitle);
            ADSettings.setCustomTitle(view);
            ADSettings.setMessage("You need to allow the Permission Requests.");
            ADSettings.setCancelable(true);
            ADSettings.setNeutralButton("SETTINGS", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    final Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + getContext().getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    getContext().startActivity(i);
                }
            });
            ADSettings.setNegativeButton("CLOSE", null);
            ADSettings.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    if (requestPermission.equals("CAMERA"))
                    {
                        requestPermissions(new String[]{
                                Manifest.permission.CAMERA
                        }, requestPermissionCode);
                    }

                    else if (requestPermission.equals("GALLERY"))
                    {
                        requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, requestPermissionCode);
                    }

                    else if (requestPermission.equals("CAMERA_GALLERY"))
                    {
                        requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        }, requestPermissionCode);
                    }
                }
            });
            ADSettings.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE_STORAGE_AND_CAMERA_1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    initSettingsPrompt("CAMERA", MY_PERMISSIONS_REQUEST_CODE_STORAGE_AND_CAMERA_1);
                }
                else
                {
                    initValidateCreateAndRetrieveImage(); //Create folder storage for images

                    if (!fProfileImage.exists())
                    {
                        initTakePhoto();
                    }
                    else
                    {
                        if (isProfilePictureAvailable)
                        {
                            initLoadPhoto();
                        }
                        else
                        {
                            initTakePhoto();
                        }
                    }
                }
            }
            else
            {
                initSettingsPrompt("CAMERA_GALLERY", MY_PERMISSIONS_REQUEST_CODE_STORAGE_AND_CAMERA_1);
            }
        }
        else if (requestCode == MY_PERMISSIONS_REQUEST_CODE_CAMERA)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                initGetPictureCamera();
            }
            else
            {
                initSettingsPrompt("CAMERA", MY_PERMISSIONS_REQUEST_CODE_CAMERA);
            }
        }
        else if (requestCode == MY_PERMISSIONS_REQUEST_CODE_STORAGE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                initGetPictureGallery();
            }
            else
            {
                initSettingsPrompt("GALLERY", MY_PERMISSIONS_REQUEST_CODE_STORAGE);
            }
        }
    }

    private void initLoadPhoto()
    {
        try
        {
            TextView title = new TextView(getActivity());
            String sTitle = "Profile Image";
            title.setText(sTitle);
            title.setPadding(10, 20, 10, 0);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.parseColor("#2888ac"));
            title.setTextSize(20);

            final AlertDialog.Builder imageDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.view_image_dialog_layout, (ViewGroup) getActivity().findViewById(R.id.layout_root));

            imageDialog.setView(layout);
            imageDialog.setCustomTitle(title);

            String path = sSaveFolderName + "/" + "Profile" + ".jpg";

            File imageFile = new File(path);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
            bitmap = initImageRotateNormal(imageFile, bitmap);

            ImageView image =  layout.findViewById(R.id.imageView);
            image.setImageBitmap(bitmap);

            imageDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });

            imageDialog.setPositiveButton("CHANGE PICTURE", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    initTakePhoto();

                    dialog.dismiss();
                }
            });

            imageDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initTakePhoto()
    {
        try
        {
            if(Build.VERSION.SDK_INT>=24)
            {
                try
                {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);

                    final CharSequence[] options = { "Take Photo", "Upload Photo"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Change Picture");
                    builder.setItems(options, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int item)
                        {
                            if (options[item].equals("Take Photo"))
                            {
                                initGetPictureCamera();
                            }
                            else if (options[item].equals("Upload Photo"))
                            {
                                initGetPictureGallery();
                            }
                        }
                    });
                    builder.show();
                }
                catch(Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
            else
            {
                final CharSequence[] options = { "Take Photo", "Upload Photo"};

                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Choose method to put picture");
                builder.setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int item)
                    {
                        if (options[item].equals("Take Photo"))
                        {
                            initGetPictureCamera();
                        }
                        else if (options[item].equals("Upload Photo"))
                        {
                            initGetPictureGallery();
                        }
                    }
                });
                builder.show();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetPictureCamera()
    {
        try
        {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            DateFormat df = new SimpleDateFormat("-mm-ss");

            String newPicFile = df.format(date) + ".jpg";
            String outPath = "/sdcard/" + newPicFile;
            File outFile = new File(outPath);

            mCameraFileName = outFile.toString();
            Uri outURI = Uri.fromFile(outFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outURI);
            startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetPictureGallery()
    {
        try
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_GALLERY_CODE);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


    // Image result of the intent - onActivityResult()
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                imageUri = null;

                if (data != null)
                {
                    imageUri = data.getData();
                }

                if (imageUri == null && mCameraFileName != null)
                {
                    imageUri = Uri.fromFile(new File(mCameraFileName));
                }

                try
                {
                    File file = new File(mCameraFileName);

                    if (!file.exists())
                    {
                        file.mkdir();
                    }

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                    Glide.with(this).load(bitMapCustomize).into(registration_image);

                    isProfilePictureAvailable = true;
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
            else if (requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                try
                {
                    imageUri = null;

                    if (data != null)
                    {
                        imageUri = data.getData();

                        FilePath      = getFileNameByUri(getContext(), imageUri);

                        File file     = new File(FilePath);

                        FileName      = file.getName();
                        FileExtension = FileName.substring(FileName.lastIndexOf(".")  + 1);
                        FileSize      = getFolderSizeLabel(file);

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);

                        Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                        //Save to Database
                        //initUpdatePatientImage(bitMapCustomize);
                        createDirectoryAndSaveFile(bitMapCustomize);

                        //bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, false);
                        Glide.with(this).load(bitMapCustomize).into(registration_image);

                        isProfilePictureAvailable = true;
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        }
    }

    private void initValidateCreateAndRetrieveImage()
    {
        try
        {
            sSaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/CUPS/" + ".Profile";

            File fImageDirectory = new File(sSaveFolderName);

            // Create a new folder if no folder name exist
            if (!fImageDirectory.exists())
            {
                fImageDirectory.mkdirs();
            }

            String sProfile = sSaveFolderName + "/Profile.jpg";

            fProfileImage = new File(sProfile);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave)
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                    && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                Log.e(TAG, "Permission is denied");
            }
            else
            {
                try
                {
                    sSaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/CUPS/" + ".Profile";

                    File fileImage = new File(sSaveFolderName);

                    if (!fileImage.exists())
                    {
                        File wallpaperDirectory = new File(sSaveFolderName);
                        wallpaperDirectory.mkdirs();
                    }

                    File file = new File(sSaveFolderName, "Profile");

                    if (file.exists())
                    {
                        file.delete();
                    }

                    try
                    {
                        FileOutputStream out = new FileOutputStream(file + ".jpg");
                        imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Saving Image: " + e.toString());
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Saving Image: " + e.toString());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Saving Image: " + e.toString());
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
            e.printStackTrace();
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

    public static String getFolderSizeLabel(File file)
    {
        long size = getFolderSize(file) / 1024; // Get size and convert bytes into Kb.
        if (size >= 1024)
        {
            return (size / 1024) + " MB";
        }
        else
        {
            return size + " KB";
        }
    }

    public static long getFolderSize(File file)
    {
        long size = 0;

        if (file.isDirectory())
        {
            for (File child : file.listFiles())
            {
                size += getFolderSize(child);
            }
        }
        else
        {
            size = file.length();
        }
        return size;
    }

    private String getFileNameByUri(Context context, Uri uri)
    {
        try
        {
            if (Objects.requireNonNull(uri.getScheme()).compareTo("content") == 0)
            {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{
                        android.provider.MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.Media.ORIENTATION
                }, null, null, null);

                if (cursor != null)
                {
                    Log.e(TAG, "CURSOR - CONTENTS WORKS");

                    cursor.moveToFirst();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    int column_index = cursor.getColumnIndex(filePathColumn[0]);

                    String FilePath = cursor.getString(column_index);

                    Log.e(TAG, "File Path: " + FilePath);

                    if (FilePath != null)
                    {
                        return FilePath;
                    }
                    else
                    {
                        return getPath(uri);
                    }
                }
                else
                {
                    return getPath(uri);
                }
            }
            else if (uri.getScheme().compareTo("file") == 0)
            {
                try
                {
                    Log.e(TAG, "FILE WORKS");

                    File file = new File(new URI(uri.toString()));

                    if (file.exists())
                    {
                        return file.getAbsolutePath();
                    }
                    else
                    {
                        return getPath(uri);
                    }
                }
                catch (URISyntaxException e)
                {
                    return getPath(uri);
                }
            }
            else
            {
                return getPath(uri);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            return getPath(uri);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public String getPath(Uri uri)
    {
        String filePath = null;

        try
        {
            if (Build.VERSION.SDK_INT < 11)
            {
                filePath = RealPathUtils.getRealPathFromURI_BelowAPI11(getContext(), uri);
            }
            else if (Build.VERSION.SDK_INT < 19)
            {
                filePath = RealPathUtils.getRealPathFromURI_API11to18(getContext(), uri);
            }
            else
            {
                filePath = RealPathUtils.getRealPathFromURI_API19(getContext(), uri);
            }

            if (filePath == null)
            {
                filePath = RealPathUtils.elseGetRealPathFromURI(getContext(), uri);
            }

        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
        return filePath;
    }

    public void setPhotoInformation()
    {
        try
        {
            RegistrationActivity registrationActivity = (RegistrationActivity) getActivity();

            Objects.requireNonNull(registrationActivity).setPhotoInfo(FilePath, FileName, FileExtension, FileSize);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public boolean checkFields()
    {
        if(FilePath == null)
        {
            Toast.makeText(getActivity(), "Please take or upload a photo of yourself.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            return  true;
        }
        return false;
    }
}