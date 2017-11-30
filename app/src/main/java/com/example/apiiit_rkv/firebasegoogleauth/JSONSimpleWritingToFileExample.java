package com.example.apiiit_rkv.firebasegoogleauth;

import android.os.Environment;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by user on 18-11-2016.
 */

//@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class JSONSimpleWritingToFileExample {

//    File pathff  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//    File pathff  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
File pathff = new File(Environment.getExternalStorageDirectory() + "/Documents");

//    File path = Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_PICTURES);

    public File createmetadat(String staticurl) {

        JSONObject countryObj = new JSONObject();



        JSONObject media = new JSONObject();
        media.put("type","2");
        //String u= new String("https://s3.ap-south-1.amazonaws.com/boggle.co.in/Malgudi+Days+Title+Track.mp4");
       String u = staticurl;
        media.put("url",u);
        media.put("height","1.0");
        media.put("width","1.0");
        media.put("x","0.0");
        media.put("y","0.0");
        media.put("z","0.0");

        countryObj.put("maincontent",media);

        JSONArray elements = new JSONArray();

        JSONObject elementslist = new JSONObject();

        elementslist.put("type","1");
        elementslist.put("url","http://s3-eu-west-1.amazonaws.com/studio-live/473370/augImages/2266eee1-8843-4cca-bb1a-15f921ad443e/hindu_britannia_butter.png");
        elementslist.put("maximgversion","4");
        elementslist.put("x","-0.65");
        elementslist.put("y","-0.71");
        elementslist.put("z","1.0");

        JSONObject elementdata = new JSONObject();
        elementdata.put("type","1");

        elementdata.put("data1","http://britannia.co.in/products/good-day/rich-butter");
        elementdata.put("data2","http://britannia.co.in/products/good-day/rich-cashew");

        elementslist.put("action",elementdata);

        elements.add(elementslist);

        countryObj.put("elements",elements);


       // countryObj.put("States", listOfStates);
        File file=null;
        try {

            // Writing to a file
            boolean isPresent = true;
            if (!pathff.exists()) {
                isPresent = pathff.mkdir();
            }
            if(isPresent)
                file =new File(pathff.getAbsoluteFile(),"CountryJSONFile.txt");
            try {
//                pathff.mkdirs();
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            FileWriter fileWriter = new FileWriter(file);
            System.out.println("Writing JSON object to file");
            System.out.println("-----------------------");
            System.out.print(countryObj);

            fileWriter.write(countryObj.toJSONString());
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return  file;
    }



}
