package com.example.imageloader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button galerie, photo;
    private GridView GridV;
    private ImageAdapter adapter;
    ImageView imageView;
    int actual, max, min;
    String imageEncoded;

    ArrayList<Uri> mArrayUri ;

    private static final int PICK_IMAGE_MULTIPLE = 1;
    private static final int PICK_IMAGE = 100;
    private static final int CAPTURE = 0;
    Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initListener();
    }

    public void initView(){
        galerie = findViewById(R.id.galerie);
        photo = findViewById(R.id.photo);
        imageView= findViewById(R.id.image);
    }

    public void initData(){
        actual = 0;
        min = 3;
        max = 15;
        mArrayUri = new ArrayList<Uri>();
    }

    public void initListener(){
        galerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent galerie = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(galerie, PICK_IMAGE);*/
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequestGETCamera();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /*super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            image = data.getData();
            imageView.setImageURI(image);
        }*/
        try {
            if( requestCode == PICK_IMAGE_MULTIPLE && data != null){
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                if(data.getData()!=null && mArrayUri.size()<15){
                    Log.e("passData", "pass");
                    Uri mImageUri=data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();
                    if(!mArrayUri.contains(mImageUri)&& mArrayUri.size()<15){
                        mArrayUri.add(mImageUri);
                    }

                } else if (data.getClipData() != null && mArrayUri.size()<15) {
                    ClipData mClipData = data.getClipData();

                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        if(!mArrayUri.contains(uri) &&  mArrayUri.size()<15){
                            mArrayUri.add(uri);
                        }
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        Log.d("ListeImage",mArrayUri.toString());

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded  = cursor.getString(columnIndex);
                        cursor.close();

                    }
                    Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                } else if (mArrayUri.size()==15){
                    Toast.makeText(this, mArrayUri.size()+"/"+max+", limite max atteint", Toast.LENGTH_LONG).show();
                }
            }else if (requestCode==CAPTURE){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                mArrayUri.add(getImageUri(this, bitmap));
                Log.i("Capture", "add capture");
            }

            Log.d("ListeImage",mArrayUri.toString());
            Log.d("ListeImage",mArrayUri.size()+"");
            if (resultCode == RESULT_OK) {
                if (mArrayUri.size()==0){
                    Toast.makeText(this, "aucune image n'a été selectionné", Toast.LENGTH_LONG).show();
                }else if(mArrayUri.size()<min){
                    Toast.makeText(this, mArrayUri.size()+"/"+max+", vous devez selectionner au moins "+min+" images", Toast.LENGTH_LONG).show();
                }else if (mArrayUri.size()>=min && mArrayUri.size()<=15){
                    Toast.makeText(this, mArrayUri.size() +"/"+ max, Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void sendRequestGETCamera(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED  ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CAPTURE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {// Permission has already been granted
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAPTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAPTURE);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
