package com.atquil.storevideointernal;
/***
 * Paypal for support : paypal.me/atquil
 *
 * Sources :
 * 1. https://stackoverflow.com/questions/9033710/capture-a-video-and-store-it-at-a-specific-location-rather-than-a-default-locati - Storing File Name
 * 2. https://developer.android.com/training/camera/videobasics - Camera
 */

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private static int VIDEO_REQUEST = 101;
    private Uri videoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //4. Getting permission for App for Writing on Internal Storage
        getPermissionforWrite();
    }

    public void CaptureVideo(View view) {

        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(videoIntent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(videoIntent,VIDEO_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
                FileInputStream fis = videoAsset.createInputStream();
                File Atquil=new File(Environment.getExternalStorageDirectory(),"/Atquil/");  //you can replace RecordVideo by the specific folder where you want to save the video
                if (!Atquil.exists()) {
                    System.out.println("Atquil Directory did not found");
                    Atquil.mkdirs();
                }

                File file;
                Date date = new Date();
                SimpleDateFormat dateFormatWithZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                String currentDate = dateFormatWithZone.format(date);
                file=new File(Atquil,"Atquil_"+currentDate+".mp4" );

                FileOutputStream fos = new FileOutputStream(file);

                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
                fis.close();
                fos.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /***
     * Getting the permission from user for writing on android device
     */
    private void getPermissionforWrite() {
    //Getting the permission to create folder
        String TAG = "Permsission : ";
        if (Build.VERSION.SDK_INT >= 23) {
            //Asking for user for permission and Create Folder
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                Log.d("Permission is given","Now create the folder");

            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                Log.d("Permission is given","Now create the folder");

            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is automatically given");

        }
    }


    /***
     * After Getting Permission Creating a default folder Atquil in internal
     *
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String TAG = "Permsission : ";
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            //Define the path you want
           File Atquil = new File(Environment.getExternalStorageDirectory(), "Atquil");
            if (!Atquil.exists()) {
                boolean b = Atquil.mkdirs();
                Log.d("Folder Created", "Trident Folder Created : " + Atquil);

            }
        }
    }
}
