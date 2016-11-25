package com.dsm.spiralmoon.pbangchajja.chicken;

import android.os.StrictMode;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.dsm.spiralmoon.pbangchajja.SearchResult;
import com.dsm.spiralmoon.pbangchajja.Searcher;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.messages.EddystoneUid;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by dsm_029 on 2016-10-14.
 */
public class SearchResult_chicken extends SearchResult {
    protected String selectedCategory;

    //from xenoaron DB
    protected String shopIntroduction;
    protected String fried;
    protected String spicy;
    protected String soya;
    protected String cola;

    //생성자
    public SearchResult_chicken() {
        Log.e("Error", "Call error occured. SearchResult needs parameter.");
    }
    public SearchResult_chicken(Searcher searcher, int index, String selectedCategory) {

        this.selectedCategory = selectedCategory;
        this.shopName = searcher.shopName[index];
        this.phone = searcher.phone[index];
        this.newAddress = searcher.newAddress[index];
        this.address = searcher.address[index];
        this.id = searcher.id[index];
    }

    //통신 메소드
    @Override
    public String request() {

        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String address = "";

            if(this.selectedCategory.equals("치킨")) {
                directory = "/pcroom/pccafe_load?";
                address = "http://" + serverIP + directory;
            }

            address += "id=" + Integer.toString(this.id);
            URL url = new URL(address);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            do {
                str = buffer.readLine();

                if (str == null)
                    break;

                response += str;

            } while(str != null);

            Log.d("id", Integer.toString(this.id));
            Log.d("결과", response);

            //jsonParse(response);

            //response = null;
            str = null;

            return response;
        }
        catch(Exception e)
        {
            Log.e("Error", e.getMessage());
        }

        return null;
    } //place information request(activity update)
    @Override
    public void modify(ArrayList<EditText> infoList) {

        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String address = "";

            if(this.selectedCategory.equals("치킨")) {
                directory = "/pcroom/pccafe_register?";
                address = "http://" + serverIP + directory;
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
                    "&userid=" + "testUser" +
                    "&text=" + URLEncoder.encode(infoList.get(0).getText().toString(), "UTF-8") +
                    "&cpu=" + URLEncoder.encode(infoList.get(1).getText().toString(), "UTF-8") +
                    "&ram=" + URLEncoder.encode(infoList.get(2).getText().toString(), "UTF-8") +
                    "&graphic_card=" + URLEncoder.encode(infoList.get(3).getText().toString(), "UTF-8") +
                    "&monitor_size=" + URLEncoder.encode(infoList.get(4).getText().toString(), "UTF-8") +
                    "&cost=" + URLEncoder.encode(infoList.get(5).getText().toString(), "UTF-8");

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
            Log.e("Server error", e.getMessage());
        }
    } //place information modify

    //그 외 메소드
    @Override
    public void jsonParse(String json, ArrayList<TextView> textView, TextView like, TextView hate) {

        try {

            JSONParser jsonParser = new JSONParser();

            if(json.contains("null{"))
                json = json.replace("null{", "{");

            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);

            this.shopIntroduction = jsonObject.get("text").toString();
//            this.cpu = jsonObject.get("cpu").toString();
//            this.ram = jsonObject.get("ram").toString();
//            this.cost = jsonObject.get("cost").toString();
//            this.grahpic_card = jsonObject.get("graphic_card").toString();
//            this.monitor_size = jsonObject.get("monitor_size").toString();
//
//            textView.get(0).setText(this.shopName);
//            textView.get(1).setText("PC방 소개 : " + this.shopIntroduction);
//            textView.get(2).setText("CPU : " + this.cpu);
//            textView.get(3).setText("RAM : " + this.ram + "G");
//            textView.get(4).setText("Graphic card : " + this.grahpic_card);
//            textView.get(5).setText("Monitor : " + this.monitor_size + "인치");
//            textView.get(6).setText("요금 : " + this.cost + " (1시간 기준)");
        }
        catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    } //response json parsing
    @Override
    public boolean dataCheck(ArrayList<EditText> text) {

        for (int i = 0; i < text.size(); i++) {
            if(text.get(i) == null)
                return false;
        }

        return true;
    } //data type check
    @Override
    public boolean typeCheck(String category, String text) {

        if(category.equals("fried") || category.equals("spicy") || category.equals("soya")) {

            try {
                Integer.parseInt(text);
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }

        return false;
    }
}
