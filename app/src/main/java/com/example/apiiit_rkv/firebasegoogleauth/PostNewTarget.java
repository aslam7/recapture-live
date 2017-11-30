package com.example.apiiit_rkv.firebasegoogleauth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//import com.amazonaws.auth.profile.ProfileCredentialsProvider;

//import com.qualcomm.vuforia.CloudRecognition.utils.SignatureBuilder;


// See the Vuforia Web Services Developer API Specification - https://developer.vuforia.com/resources/dev-guide/adding-target-cloud-database-api

public class
PostNewTarget implements TargetStatusListener, Runnable {

    //Server Keys
    private String accessKey = "c2ca054bdb734c1f8a460b8e8664143ded42e498";
    private String secretKey = "27a2e6c433c493123a3994c62a7edd8052af5342";

    private String url = "https://vws.vuforia.com";
    private String targetName = "Img";
    private String imageLocation ="";

    private Context context2;
    UploadFirst callingClass;

    //	private String staticurl="https://s3.ap-south-1.amazonaws.com/";
    private String staticurl="https://s3.ap-south-1.amazonaws.com/";
    private TargetStatusPoller targetStatusPoller;

    private final float pollingIntervalMinutes = 60;//poll at 1-hour interval

    public void setImageLocation(Context context2, UploadFirst callingClass, String path, String appenedtourl){
        this.context2=context2;
        this.callingClass=callingClass;
        imageLocation=path;
        staticurl=staticurl+appenedtourl;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

        String formattedDate = df.format(c.getTime());
        targetName=targetName+""+formattedDate;
    }

    //    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String postTarget() throws URISyntaxException, ClientProtocolException, IOException, JSONException {
        HttpPost postRequest = new HttpPost();
        HttpClient client = new DefaultHttpClient();
        postRequest.setURI(new URI(url + "/targets"));
        JSONObject requestBody = new JSONObject();

        setRequestBody(requestBody);
        postRequest.setEntity(new StringEntity(requestBody.toString()));
        setHeaders(postRequest); // Must be done after setting the body

        HttpResponse response = client.execute(postRequest);
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println(responseBody);
//        toast1(context2, responseBody);
        JSONObject jobj = new JSONObject(responseBody);

        String uniqueTargetId = jobj.has("target_id") ? jobj.getString("target_id") : "";
        System.out.println("\nCreated target with id: " + uniqueTargetId);
//        toast1(context2, "\nCreated target with id: " + uniqueTargetId);
        return uniqueTargetId;
    }



    //    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setRequestBody(JSONObject requestBody) throws IOException, JSONException {
        File imageFile = new File(imageLocation);
        if(!imageFile.exists()) {
            System.out.println("File location does not exist!");
//            toast1(context2, "No File ");
            System.exit(1);
        }
        byte[] image = FileUtils.readFileToByteArray(imageFile);
        requestBody.put("name", targetName); // Mandatory
        requestBody.put("width", 320.0); // Mandatory
        byte[] b= Base64.encodeBase64(image);
        String s=new String(b);
        requestBody.put("image",s);

        requestBody.put("active_flag", 1); // Optional


        JSONSimpleWritingToFileExample jsonSsample=new JSONSimpleWritingToFileExample();
        File metadatafile=	jsonSsample.createmetadat(staticurl);

        byte[] metainfo = FileUtils.readFileToByteArray(metadatafile);
        byte[] metaencoded= Base64.encodeBase64(metainfo);
        String metastring=new String(metaencoded);
        requestBody.put("application_metadata", metastring); // Optional
    }

    private void setHeaders(HttpUriRequest request) {
        SignatureBuilder sb = new SignatureBuilder();
        request.setHeader(new BasicHeader("Date", DateUtils.formatDate(new Date()).replaceFirst("[+]00:00$", "")));
        request.setHeader(new BasicHeader("Content-Type", "application/json"));
        request.setHeader("Authorization", "VWS " + accessKey + ":" + sb.tmsSignature(request, secretKey));
    }

    /**
     * Posts a new target to the Cloud database;
     * then starts a periodic polling until 'status' of created target is reported as 'success'.
     */
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void postTargetThenPollStatus() {
        String createdTargetId = "";
        try {
            createdTargetId = postTarget();
//            if(createdTargetId!=null) {
//                toast1(context2, "Image Uploaded!");
//                System.out.println("Image1 Uploaded");
//            }
        } catch (URISyntaxException | IOException | JSONException e) {
            e.printStackTrace();
            return;
        }

        // Poll the target status until the 'status' is 'success'
        // The TargetState will be passed to the OnTargetStatusUpdate callback
        if (createdTargetId != null && !createdTargetId.isEmpty()) {
            targetStatusPoller = new TargetStatusPoller(pollingIntervalMinutes, createdTargetId, accessKey, secretKey, this );
            targetStatusPoller.startPolling();
//            toast1(context2, "Image Uploaded!");
                System.out.println("Image1 Uploaded");
//            UploadFirst uf1=new UploadFirst();
//            uf1.stopit();
//            toast1(context2, "Uploaded!");
//            callingClass.stopit();
        }
    }

    // Called with each update of the target status received by the TargetStatusPoller
    @Override
    public void OnTargetStatusUpdate(TargetState target_state) {
        if (target_state.hasState) {

            String status = target_state.getStatus();

            System.out.println("Target status is: " + (status != null ? status : "unknown"));
//            toast1(context2, "Target status is: " + (status != null ? status : "unknown"));
            if (target_state.getActiveFlag() == true && "success".equalsIgnoreCase(status)) {

                targetStatusPoller.stopPolling();

                System.out.println("Target is now in 'success' status");
//                toast1(context2, "Image Uploaded!");
//                UploadFirst stopdialogue=new UploadFirst();
//                stopdialogue.stopit();
            }
        }
    }

    //    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        postTargetThenPollStatus();
    }
    public void toast1(final Context context, final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

	/*public static void main(String[] args) throws URISyntaxException, ClientProtocolException, IOException, JSONException {
		PostNewTarget p = new PostNewTarget();
		p.postTargetThenPollStatus();
	}*/

}