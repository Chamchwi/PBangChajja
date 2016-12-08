package com.dsm.spiralmoon.pbangchajja;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsm.spiralmoon.pbangchajja.Searcher;
import com.dsm.spiralmoon.pbangchajja.*;
import com.google.android.gms.maps.model.LatLng;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchResult implements Serializable {

    protected String selectedCategory;
    //from daum DB
    protected String shopName;
    protected String phone;
    protected String newAddress;
    protected String address;
    protected int id;
    protected String subCategoy;

    protected int likeCount = 0;
    protected int hateCount = 0;
    //parsing field
    public String response = null;
    public String str = null;
    public String jsonReviewList = null;
    public boolean isDatabaseExist = false;

    public String serverIP = "smaker.kr:8080";
//    public String serverIP = "esplay.xyz";
//    public String serverPort = "";
    public String directory = "";
    public static String userid = "";

    //생성자
    public SearchResult() {
        Log.e("Error", "Call error occured. SearchResult needs parameter.");
    }
    public SearchResult(Searcher searcher, int index, String selectedCategory) {

        this.selectedCategory = selectedCategory;
        this.shopName = searcher.shopName[index];
        this.phone = searcher.phone[index];
        this.newAddress = searcher.newAddress[index];
        this.address = searcher.address[index];
        this.id = searcher.id[index];
        this.subCategoy = searcher.subCategory[index];
    }

    //통신 메소드
    public String request() {
        return "";
    } //place information request(activity update)
    public void modify(ArrayList<EditText> infoList) {
    } //place information modify
    public void imageModify(ImageView image) {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String address = "";

            if(selectedCategory.equals("PC방")) {
                directory = "/PCroom/image?";
                address = "http://" + serverIP + directory;
            }
            else if (selectedCategory.equals("치킨")) {

            }
            else if(selectedCategory.equals("피자")) {

            }
            else if (selectedCategory.equals("병원")) {

            }

            URL url = new URL(address);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", "UTF-8"); //중요 포인트
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"); //한글 깨짐 방지

            String data = "id=" + Integer.toString(this.id);

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            //Resources res = getResources();

            BitmapDrawable d = (BitmapDrawable) image.getDrawable();
            Bitmap bitmap = d.getBitmap();
            //Bitmap bitmap = BitmapFactory.decodeResource(res,);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

            byte[] imageData = outStream.toByteArray();
            String profileImageBase64 = Base64.encodeToString(imageData, 0);

            data += "&image=" + URLEncoder.encode(profileImageBase64, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(data);

            writer.flush();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            //jsonParse(response);

            response = null;
            str = null;
            //buffer.close();
            writer.close();
        }
        catch(Exception e)
        {
            Log.e("Error", e.getMessage());
        }
    } //places image modify
    public void isLike(boolean isLike) {
        //like = good
        //hate = bad

        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String address = "";

            if(selectedCategory.equals("PC방")) {
                directory = "/PCroom/pccafe_like?";
                address = "http://" + serverIP + directory;
            }
            else if (selectedCategory.equals("치킨")) {

            }
            else if(selectedCategory.equals("피자")) {

            }
            else if (selectedCategory.equals("병원")) {

            }

            URL url = new URL(address);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", "UTF-8"); //중요 포인트
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"); //한글 깨짐 방지
            //connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");

            String data = "id=" + Integer.toString(this.id) +
                    "&userid=" + userid;

            if(isLike)
                data += "&islike=" + URLEncoder.encode("true", "UTF-8");
            else
                data += "&islike=" + URLEncoder.encode("false", "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(data);

            writer.flush();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

            Log.d("추/비추 전송", Integer.toString(this.id));

            //jsonParse(response);

            response = null;
            str = null;
            //buffer.close();
            writer.close();
        }
        catch(Exception e)
        {
            Log.e("Error", e.getMessage());
        }
    } //place like or hate
    public String review(String title, String content) {

        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String address = "";

            if(selectedCategory.equals("PC방")) {
                directory = "/PCroom/pccafe_review?";
                address = "http://" + serverIP + directory;
            }
            else if (selectedCategory.equals("치킨")) {

            }
            else if(selectedCategory.equals("피자")) {

            }
            else if (selectedCategory.equals("병원")) {

            }

            URL url = new URL(address);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", "UTF-8"); //중요 포인트
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"); //한글 깨짐 방지

            String data = "id=" + URLEncoder.encode(Integer.toString(this.id), "UTF-8") +
                    "&userid=" + URLEncoder.encode(userid, "UTF-8") +
                    "&title=" + URLEncoder.encode(title, "UTF-8") +
                    "&content=" + URLEncoder.encode(content, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(data);

            writer.flush();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

            Log.d("전송", Integer.toString(this.id));

            //jsonParse(response);

            response = null;
            str = null;
            //buffer.close();
            writer.close();
        }
        catch(Exception e)
        {
            Log.e("Error", e.getMessage());
        }

        return "";
    } //place review. this is only get

    //그 외 메소드
    public void jsonParse(String json, ArrayList<TextView> textView, TextView like, TextView hate) {

    } //response json parsing
    public boolean dataCheck(ArrayList<EditText> text) {
        return false;
    } //data null check
    public boolean typeCheck(String category, String text) {
        return false;
    } //data type check
}