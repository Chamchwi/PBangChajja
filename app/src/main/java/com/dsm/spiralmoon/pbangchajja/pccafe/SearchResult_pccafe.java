package com.dsm.spiralmoon.pbangchajja.pccafe;

import android.os.StrictMode;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.dsm.spiralmoon.pbangchajja.SearchResult;
import com.dsm.spiralmoon.pbangchajja.Searcher;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.spec.ECField;
import java.util.ArrayList;

public class SearchResult_pccafe extends SearchResult {
    protected String selectedCategory;

    //from xenoaron DB
    protected String shopIntroduction;
    protected String cpu;
    protected String ram;
    protected String grahpic_card;
    protected String monitor_size;
    protected String cost;

    //생성자
    public SearchResult_pccafe() {
        Log.e("Error", "Call error occured. SearchResult needs parameter.");
    }

    public SearchResult_pccafe(Searcher searcher, int index, String selectedCategory) {

        this.selectedCategory = selectedCategory;
        super.selectedCategory = selectedCategory;
        this.shopName = searcher.shopName[index];
        this.phone = searcher.phone[index];
        this.newAddress = searcher.newAddress[index];
        this.address = searcher.address[index];
        this.id = searcher.id[index];
    }

    //통신 메소드
    @Override
    public String request() {

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String address = "";

            directory = "/pcroom/pccafe_load?";
            address = "http://" + serverIP + directory;

            address += "id=" + Integer.toString(this.id);
            URL url = new URL(address);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            do {
                str = buffer.readLine();

                if (str == null)
                    break;

                response += str;

            } while (str != null);

            Log.d("id", Integer.toString(this.id));
            Log.d("결과", response);

            //jsonParse(response);

            //response = null;
            str = null;

            return response;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return null;
    }

    @Override
    public void modify(ArrayList<EditText> infoList) {

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String address = "";

            if (!super.isDatabaseExist)
                directory = "/pcroom/pccafe_register?";
            else
                directory = "/pcroom/pccafe_update?";

            address = "http://" + serverIP + directory;


            URL url = new URL(address);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", "UTF-8"); //중요 포인트
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"); //한글 깨짐 방지
            //connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");

            String data = "id=" + URLEncoder.encode(Integer.toString(this.id), "UTF-8") +
                    "&userid=" + URLEncoder.encode(userid, "UTF-8") +
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
        } catch (Exception e) {
            Log.e("Server error", e.getMessage());
        }
    }

    public void modifyService(EditText gameName) {

    }

    //그 외 메소드
    @Override
    public void jsonParse(String json, ArrayList<TextView> infoData, TextView like, TextView hate) {

        try {

            JSONParser jsonParser = new JSONParser();

            if (json.contains("null{}")) //해당 장소의 DB가 없는 경우
                super.isDatabaseExist = false;
            else
                super.isDatabaseExist = true;

            if (json.contains("null{"))
                json = json.replace("null{", "{");

            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);

            infoData.get(0).setText(this.shopName);

            try {
                this.shopIntroduction = jsonObject.get("text").toString();
                infoData.get(1).setText("PC방 소개 : " + this.shopIntroduction);
            } catch (Exception e) {
                infoData.get(1).setText("PC방 소개 : " + "소개글이 없습니다.");
            }

            try {
                this.cpu = jsonObject.get("cpu").toString();
                infoData.get(2).setText("CPU : " + this.cpu);
            } catch (Exception e) {
                infoData.get(2).setText("CPU : " + "정보없음");
            }

            try {
                this.ram = jsonObject.get("ram").toString();
                infoData.get(3).setText("RAM : " + this.ram + "G");
            } catch (Exception e) {
                infoData.get(3).setText("RAM : " + "정보없음");
            }

            try {
                this.grahpic_card = jsonObject.get("graphic_card").toString();
                infoData.get(4).setText("Graphic card : " + this.grahpic_card);
            } catch (Exception e) {
                infoData.get(4).setText("Graphic card : " + "정보없음");
            }

            try {
                this.monitor_size = jsonObject.get("monitor_size").toString();
                infoData.get(5).setText("Monitor : " + this.monitor_size + "인치");
            } catch (Exception e) {
                infoData.get(5).setText("Monitor : " + "정보없음");
            }

            try {
                this.cost = jsonObject.get("cost").toString();
                infoData.get(6).setText("요금 : " + this.cost + "원" + " (1시간 기준)");
            } catch (Exception e) {
                infoData.get(6).setText("요금 : " + "정보없음" + " (1시간 기준)");
            }

            try {
                this.likeCount = Integer.parseInt(jsonObject.get("good").toString());
                like.setText(Integer.toString(this.likeCount));
            } catch (Exception e) {
                like.setText("0");
            }

            try {
                this.hateCount = Integer.parseInt(jsonObject.get("bad").toString());
                hate.setText(Integer.toString(this.hateCount));
            } catch (Exception e) {
                hate.setText("0");
            }

            try { //리뷰 목록 검출 (파싱 전)
                this.jsonReviewList = jsonObject.get("review").toString();
            } catch (Exception e) {
                //
            }

        } catch (Exception e) {
        }
    }

    @Override
    public boolean dataCheck(ArrayList<EditText> text) {

        for (int i = 0; i < text.size(); i++) {
            if (text.get(i) == null)
                return false;
        }

        return true;
    }

    @Override
    public boolean typeCheck(String category, String text) {
        //category : ex)PC방 각 부품
        //text : editText 내용


        if (category.equals("monitorSize")) {
            try {
                Integer.parseInt(text);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else if (category.equals("price")) {
            try {
                Integer.parseInt(text);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }
}