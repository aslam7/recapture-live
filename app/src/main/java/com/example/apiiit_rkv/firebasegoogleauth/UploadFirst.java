package com.example.apiiit_rkv.firebasegoogleauth;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.apiiit_rkv.R;

import java.io.FileDescriptor;
import java.io.IOException;

public class UploadFirst extends BaseActivity {
   private static final int SELECT_PICTURE = 100;
    private static final int SELECT_VIDEO = 200;
    static String appendUrl="";
    private static final String TAG = "UploadFirst";
    public Uri selectedVideouri;
    public Uri selectedImageuri;
//    ImageView img;
    public String videoUri;
    public String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("UploadFirst", "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_first);
//        img = (ImageView) findViewById(R.id.img);

        findViewById(R.id.select_image).setVisibility(View.VISIBLE);
        findViewById(R.id.select_video).setVisibility(View.GONE);
        findViewById(R.id.upload_aws).setVisibility(View.GONE);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F0F8FF")));
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setIcon(R.drawable.recapturew);

    }
    //File imageFile = new File("/storage/sdcard0/WhatsApp/Media/WhatsApp Profile Photos/D.jpg");
    // Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    //img.setImageBitmap(bitmap);
    public void handlerImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }
    public void handlerVideo(View view) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), SELECT_VIDEO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                // Get the url from data
                selectedImageuri = data.getData();
                if (null != selectedImageuri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageuri);
                    Log.i(TAG, "Image Path : " + path);
                    // Set the image in ImageView
//                    img.setImageURI(selectedVideouri);
                    setImageuri(path);
                    findViewById(R.id.select_video).setVisibility(View.VISIBLE);
//                    findViewById(R.id.upload_aws).setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Image Path: "+path, Toast.LENGTH_SHORT).show();
                    Bitmap bmImg = BitmapFactory.decodeFile(path);
                    ImageView img= (ImageView) findViewById(R.id.img);
                    img.setImageBitmap(bmImg);
                }
            }
            else if (requestCode == SELECT_VIDEO) {

                // Get the url from data
                selectedVideouri = data.getData();
                if (null != selectedVideouri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedVideouri);
                    Log.i(TAG, "Image Path : " + path);
                    // Set the image in ImageView
//                    img.setImageURI(selectedVideouri);
                    setVideouri(path);
                    findViewById(R.id.select_video).setVisibility(View.VISIBLE);
                    findViewById(R.id.upload_aws).setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Video Path: "+path, Toast.LENGTH_SHORT).show();
                    VideoView video_player_view = (VideoView) findViewById(R.id.vid);
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path,
                            MediaStore.Images.Thumbnails.MINI_KIND);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                    video_player_view.setBackgroundDrawable(bitmapDrawable);
                    MediaController media_Controller = new MediaController(this);
                    DisplayMetrics dm = new DisplayMetrics();
                    this.getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int height = dm.heightPixels;
                    int width = dm.widthPixels;
                    video_player_view.setMinimumWidth(width);
                    video_player_view.setMinimumHeight(height);
                    video_player_view.setMediaController(media_Controller);
                    video_player_view.setVideoPath(path);
//                    video_player_view.start();
                }
            }

        }
    }

    private void setVideouri(String imageuri) {
        this.videoUri = imageuri;
    }

    private String getVideouri() {
        return videoUri;
    }
    private void setImageuri(String imageuri) {
        this.imageUri = imageuri;
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
    private String getImageuri() {
        return imageUri;
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void handlerAWS(View view) {
        showProgressDialog();
        mProgressDialog.setMessage("Uploading...");
        Log.d("UploadFirst", "handlerAWS");
        UploadToAmazonS3 uploadToAmazonS3= new UploadToAmazonS3();
        uploadToAmazonS3.setpath(UploadFirst.this,getVideouri());
        uploadToAmazonS3.fileupload(getApplicationContext());
        appendUrl=uploadToAmazonS3.existingBucketName+"/"+uploadToAmazonS3.keyName;

//        Intent myIntent = new Intent(UploadFirst.this, Upload2VWS.class);
//        //myIntent.putExtra("append_path",); //Optional parameters
//        UploadFirst.this.startActivity(myIntent);

        /*Intent intent1 = new Intent();
        intent1.setType("Image/*");
        intent1.setAction(Intent.ACTION_GET_CONTENT);
        */
        PostNewTarget postNewTarget=new PostNewTarget();
        postNewTarget.setImageLocation(getApplicationContext(), UploadFirst.this,getImageuri(), UploadFirst.appendUrl);
        Thread thread=new Thread(postNewTarget);
        thread.start();

    }

    public void stopit() {
//        Log.d("IsOkay","Okay");

        System.out.println("All uploaded");
        hideProgressDialog();
//        Toast.makeText(getApplicationContext(), "All Uploaded", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent( UploadFirst.this, MainActivityOld.class);
//        findViewById(R.id.sign_out_and_AR).setVisibility(View.VISIBLE);
        startActivity(intent);
    }
}
