package com.dsm.spiralmoon.pbangchajja;

import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MarkerManager extends AppCompatActivity {

    private ArrayList<MarkerOptions> markerOptions = new ArrayList<MarkerOptions>();
    private ArrayList<Marker> marker = new ArrayList<Marker>();

    public void markerSet(Searcher searcher) {

        markerOptions.clear(); // 기존 마커 데이터 삭제

        for(int i = 0; i < searcher.listCount; i++)
        {
            markerOptions.add(new MarkerOptions()
                    .position(searcher.coordinateList[i])
                    .title(searcher.shopName[i])
                    .snippet("현재 위치로부터 " + searcher.distance[i] + "m"));
                    //.snippet(searcher.address[i]);
        }
    }
    public void marking(GoogleMap map) {

        marker.clear(); //기존 마커 삭제
        map.clear();

        for(int i = 0; i < markerOptions.size(); i++) {
            marker.add(map.addMarker(markerOptions.get(i)));
        }
    }
}
