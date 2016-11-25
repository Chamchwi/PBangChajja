package com.dsm.spiralmoon.pbangchajja;

import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.lang.String;

public class Searcher extends Thread {

    private String apikey;

    protected short listCount = 0;

    public String[] shopName;
    public String[] phone;
    public String[] placeUrl;
    public String[] imageUrl;
    public String[] newAddress;
    public String[] address;
    public int[] zipcode;
    public long[] addressCode;
    public int[] id;
    public int[] distance;
    public LatLng[] coordinateList; //is Latitude and Longitude
    public String[] subCategory; //검색어를 통한 검색일 경우 선택해주는 카테고리

    public Searcher() {
        Log.e("Error", "Call error occured. Searcher class need parameter(api key)");
    }

    public Searcher(String apikey) {
        this.apikey = apikey;
    }

    public String search(String category, int radius) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String response = null, str = null;
            String address = "https://apis.daum.net/local/v1/search/keyword.xml?";
            address += "apikey=" + apikey;
            address += "&query=" + URLEncoder.encode(category, "UTF-8");
            address += "&location=" + MainActivity.myLoc.getLatitude() + "," + MainActivity.myLoc.getLongitude();

            if (radius != 0) {
                address += "&radius=" + "" + radius;
            }
            URL url = new URL(address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            Log.d("URL", address);

            BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));

            do {
                str = buffer.readLine();

                if (str == null)
                    break;

                //Log.d("Response Message", str);
                response += str;

            } while (str != null);

            xmlParse(response);

            return response;
        } catch (Exception e) {
            String error = e.getMessage();

            if (error != null) {
                Log.e("Error", error);
            }
        }
        return null;
    }

    public void xmlParse(String xml) {

        String temp[];
        String temp2[];
        double temp3[];

        if (xml == null)
            Log.e("XML not find", "request xml is null");
        else {

            temp = xml.split("</info>");
            temp = temp[1].split("<title>");

            listCount = (short) (temp.length - 1);

            shopName = new String[temp.length - 1];
            phone = new String[temp.length - 1];
            placeUrl = new String[temp.length - 1];
            addressCode = new long[temp.length - 1];
            id = new int[temp.length - 1];
            distance = new int[temp.length - 1];
            imageUrl = new String[temp.length - 1];
            newAddress = new String[temp.length - 1];
            address = new String[temp.length - 1];
            zipcode = new int[temp.length - 1];
            phone = new String[temp.length - 1];
            coordinateList = new LatLng[temp.length - 1];

            temp3 = new double[temp.length - 1];

            for (int i = 0; i < temp.length - 1; i++)
                temp[i] = temp[i + 1];

            temp[temp.length - 1] = null;

            for (int i = 0; i < temp.length - 1; i++) {
                Log.d("List", temp[i]);

                //shop name
                temp2 = temp[i].split("</title>");
                this.shopName[i] = temp2[0]; //shop name parse

                //address code
                if (temp[i].indexOf("<addressBCode>") != -1) {
                    temp2 = temp[i].split("<addressBCode>");
                    temp2 = temp2[1].split("</addressBCode>");

                    //this.addressCode[i] = Integer.parseInt(temp2[0]);
                    this.addressCode[i] = Long.parseLong(temp2[0]);
                } else {
                    this.addressCode[i] = 0;
                }

                //place url
                if (temp[i].indexOf("<placeUrl>") != -1) {
                    temp2 = temp[i].split("<placeUrl>");
                    temp2 = temp2[1].split("</placeUrl>");

                    this.placeUrl[i] = temp2[0];
                } else {
                    this.placeUrl[i] = "";
                }

                //id
                if (temp[i].indexOf("<id>") != -1) {
                    temp2 = temp[i].split("<id>");
                    temp2 = temp2[1].split("</id>");

                    this.id[i] = Integer.parseInt(temp2[0]);
                } else {
                    this.id[i] = 0;
                }

                //distance
                if (temp[i].indexOf("<distance>") != -1) {
                    temp2 = temp[i].split("<distance>");
                    temp2 = temp2[1].split("</distance>");

                    this.distance[i] = Integer.parseInt(temp2[0]);
                } else {
                    this.distance[i] = 0;
                }

                //image url
                if (temp[i].indexOf("<imageUrl>") != -1) {
                    temp2 = temp[i].split("<imageUrl>");
                    temp2 = temp2[1].split("</imageUrl>");

                    this.imageUrl[i] = temp2[0];
                } else {
                    this.imageUrl[i] = "";
                }

                //new address
                if (temp[i].indexOf("<newAddress>") != -1) {
                    temp2 = temp[i].split("<newAddress>");
                    temp2 = temp2[1].split("</newAddress>");

                    this.newAddress[i] = temp2[0];
                } else {
                    this.newAddress[i] = "";
                }

                //address
                if (temp[i].indexOf("<address>") != -1) {
                    temp2 = temp[i].split("<address>");
                    temp2 = temp2[1].split("</address>");

                    this.address[i] = temp2[0];
                } else {
                    this.address[i] = "";
                }

                //zip code
                if (temp[i].indexOf("<zipcode>") != -1) {
                    temp2 = temp[i].split("<zipcode>");
                    temp2 = temp2[1].split("</zipcode>");

                    this.zipcode[i] = Integer.parseInt(temp2[0]);
                } else {
                    this.zipcode[i] = 0;
                }

                //phone
                if (temp[i].indexOf("<phone>") != -1) {
                    temp2 = temp[i].split("<phone>");
                    temp2 = temp2[1].split("</phone>");

                    this.phone[i] = temp2[0];
                } else {
                    this.phone[i] = "";
                }

                //latitude
                if (temp[i].indexOf("<latitude>") != -1) {
                    temp2 = temp[i].split("<latitude>");
                    temp2 = temp2[1].split("</latitude>");

                    temp3[i] = Double.parseDouble(temp2[0]);
                } else {
                    temp3[i] = 0;
                }

                //logitude and coordinateList
                if (temp[i].indexOf("<longitude>") != -1) {
                    temp2 = temp[i].split("<longitude>");
                    temp2 = temp2[1].split("</longitude>");

                    this.coordinateList[i] = new LatLng(temp3[i], Double.parseDouble(temp2[0]));
                } else {
                    this.coordinateList[i] = new LatLng(temp3[i], 0);
                }

                //검색어를 통한 검색일 경우의 카테고리 추출
                if (temp[i].indexOf("<category>") != -1) {
                    temp2 = temp[i].split("<category>");
                    temp2 = temp2[1].split("</category>");

                    //current temp2 ex)
                }
            }
        }
    }
}