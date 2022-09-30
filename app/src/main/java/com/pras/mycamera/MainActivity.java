package com.pras.mycamera;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView fileName;
    private ImageView imResult;
    private Uri photoUri;
    ActivityResultLauncher<Uri> takePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result) {
                    fileName.setText(photoUri.getPath());
                    Bitmap bitmap;
                    ContentResolver contentResolver = getContentResolver();
                    try {
                        if (Build.VERSION.SDK_INT < 28) {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri);
                        } else {
                            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, photoUri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        }
                        if (bitmap != null) {
                            imResult.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTakePicture = findViewById(R.id.btnTakePicture);
        fileName = findViewById(R.id.txtFile);
        imResult = findViewById(R.id.imgResult);

        btnTakePicture.setOnClickListener(view -> {
            try {
                dispatchTakePictureIntent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri mPhotoUri = FileProvider.getUriForFile(this,
                        "com.prasetya.mycamera.fileprovider",
                        photoFile);
                this.photoUri = mPhotoUri;
                takePicture.launch(mPhotoUri);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_image_confirmation, null))
                .setPositiveButton("Ya", (dialogInterface, i) -> {

                })
                .setNegativeButton("Tidak", (dialogInterface, i) -> {

                });
        builder.show();
    }
}
