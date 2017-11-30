package com.example.apiiit_rkv.firebasegoogleauth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//import com.amazonaws.auth.profile.ProfileCredentialsProvider;

//import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by user on 19-11-2016.
 */


public class UploadToAmazonS3{

    String filePath;
    String status;
    Context context1;
    public String TAG = "uploading to amazon";
    String existingBucketName = "recapture2";
    String keyName="";
    UploadFirst callingClass;
//    String staticurl=""
//    ProgressBar progressBar;
    public void setpath(UploadFirst callingClass, String path1) {
        this.callingClass=callingClass;
        filePath = path1;
        Log.d("UploadToAmazonS3", "set path");
    }

    public void fileupload(Context context) {
        Log.d("UploadToAmazonS3", "file upload");
        //   BasicAWSCredentials credentials=new BasicAWSCredentials("AKIAIDV23VPINODVRHLA","LqASDtoUlbBRRDcREyMZruIrZfSpaPzMM0Fo/RXH");
        context1 = context;
       // keyName = "File.";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

        String formattedDate = df.format(c.getTime());
        keyName = formattedDate;

        keyName=keyName+filePath.substring(filePath.lastIndexOf("/"));
        Log.d("Check", keyName);
        File file = new File(filePath);
        if (file == null) {
            Log.d("no file", "existssss");
        }
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setConnectionTimeout(60 * 1000);
        clientConfiguration.setSocketTimeout(60 * 1000);

        final AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJ66OD6MZGB6B6VUA", "tpLXKE+R+EUmx7DOSfwAXi3+phQrvadEHr554DeI");
        AmazonS3 s3client = new AmazonS3Client(awsCredentials, clientConfiguration);
        s3client.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        try {
            Log.d("UploadToAmazonS3.this", "Aws : Uploading  to S3 from a file");
            TransferUtility transferUtility = new TransferUtility(s3client, context);
            TransferObserver transferObserver = null;


            transferObserver = transferUtility.upload(existingBucketName, keyName, file, CannedAccessControlList.PublicRead);
            TransferListener listener = new UploadListener();
            transferObserver.setTransferListener(listener);
            boolean b = true;
            String a = null;
          /*  while(b){
                 a=    transferObserver.getState().toString();
               if(a.equals("COMPLETED"))
               {
                   Log.d("aaa: ",a);
                   Toast.makeText( context1, "a =:  compled", Toast.LENGTH_LONG).show();
                   b=false;
               }
            }*/
            // String a=    transferObserver.getState().toString();

        } catch (AmazonServiceException ase) {
            Log.d("UploadToAmazonS3.this", "AmazonServiceException");
            Log.d(TAG, "Aws : Caught an AmazonServiceException, which " +
                    "means your request made it " +

                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            Log.d(TAG, "Aws : Error Message:    " + ase.getMessage());
            Log.d(TAG, "Aws : HTTP Status Code: " + ase.getStatusCode());
            Log.d(TAG, "Aws : AWS Error Code:   " + ase.getErrorCode());
            Log.d(TAG, "Aws : Error Type:       " + ase.getErrorType());
            Log.d(TAG, "Aws : Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            Log.d("UploadToAmazonS3.this", "AmazonClientException");
            //   Log.d("Aws : Caught an AmazonCl",  "access the network.");
            Log.d(TAG, "Aws : Error Message: " + ace.getMessage());
        } catch (Exception e) {
            Log.d("UploadToAmazonS3.this", "AException");
            Log.d(TAG, "uploadFileToAWS " + e.toString());
        }


        //  startuploading(credentials);
    }




    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.d("UploadToAmazonS3.this", "onError");
            Log.e(TAG, "Error during upload: " + id, e);
            // updateList();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d("UploadToAmazonS3.this", "onProgressChanged");
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));

//            Toast.makeText(context1, "uploading the File ", Toast.LENGTH_LONG).show();
            //  updateList();
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d("UploadToAmazonS3.this", "onStateChanged");
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);
            String a = newState.toString();
            status = a;
            setstate(a);
            if (a.equals("COMPLETED")) {
//                toast1(context2, "Uploaded!");
                Toast.makeText(context1, "Uploaded!", Toast.LENGTH_SHORT).show();
                callingClass.stopit();
//                Toast.makeText(context1, "Video Uploaded", Toast.LENGTH_SHORT).show();
            }

            //  updateList();
        }
    }


    public void setstate(String a) {
        this.status = a;
        Log.d("UploadToAmazonS3.this", "setstate");
    }

    public String state() {
        return this.status;
    }


}
