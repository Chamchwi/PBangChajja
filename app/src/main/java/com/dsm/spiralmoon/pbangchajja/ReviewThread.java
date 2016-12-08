package com.dsm.spiralmoon.pbangchajja;

import android.widget.ListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;

public class ReviewThread extends Thread {

    private String reviewData; //리뷰 데이터
    private ReviewAdapter review; //리스트 뷰

    public ReviewThread(String reviewData, ReviewAdapter review) {
        this.reviewData = reviewData;
        this.review = review;
    }
    public void run() {

        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(reviewData);

            ArrayList<JSONObject> jsonObject = new ArrayList<JSONObject>();

            if(jsonArray.size() != 0)//리뷰가 한개라도 있으면
                for (int i = 0; i < jsonArray.size(); i++) {
                    jsonObject.add((JSONObject) jsonArray.get(i));

                    review.addItem
                            (
                                    jsonObject.get(i).get("userid").toString(), //수정해야할 부분
                                    jsonObject.get(i).get("title").toString(),
                                    jsonObject.get(i).get("content").toString(),
                                    jsonObject.get(i).get("registeredtime").toString()
                            );
                }
//            else //리뷰가 하나도 없을 경우
//                review.addItem
//                        (
//                                "리뷰없음",
//                                "등록된 리뷰가 없습니다.",
//                                "제일 먼저 리뷰를 등록해보세요!",
//                                " "
//                        );
        }
        catch (Exception e) {

        }
    }
}
