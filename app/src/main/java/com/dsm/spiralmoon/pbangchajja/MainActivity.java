package com.dsm.spiralmoon.pbangchajja;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dsm.spiralmoon.pbangchajja.chicken.SearchResult_chicken;
import com.dsm.spiralmoon.pbangchajja.pccafe.PlacesInfoActivity_pccafe;
import com.dsm.spiralmoon.pbangchajja.chicken.PlacesInfoActivity_chicken;
import com.dsm.spiralmoon.pbangchajja.pccafe.SearchResult_pccafe;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    //category
    private Spinner categorySpinner;
    private CustomAdapter adapter;
    private String[] category = {"PC방", "치킨", "피자", "병원"};
    private int[] icon = {R.drawable.icon_pc, R.drawable.icon_chicken, R.drawable.icon_pizza, R.drawable.icon_hospital};

    //navigation
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private boolean isDrawerOpen = false;

    private String selectedCategory;
    public static boolean autoSearch = true;
    public static boolean autoCamera = true;

    //Shortest place
    private Button shortestButton;
    private TextView shortestShopname;
    private TextView shortestTel;
    private TextView shortestCategory;
    private TextView shortestAddress;
    int shortestPlaceIndex = 0;

    //Gps location
    public static Location myLoc = new Location(LocationManager.GPS_PROVIDER);
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if (autoSearch) {
                myLoc = location;

                //last location save
                pref.setLastLocation(myLoc.getLatitude(), myLoc.getLongitude());
                //
                searcher.search(selectedCategory, radius); //1000 == 1km
                markerManager.markerSet(searcher);
                markerManager.marking(map); //marking

                if (autoCamera) { //자동 카메라 조준(위치가 바뀔 때마다 업데이트)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom
                            (new LatLng(myLoc.getLatitude(), myLoc.getLongitude()), zoomLevel));

                    //움직일 때마다 가장 가까운 곳의 이름 출력
                    ArrayList<Double> distance = new ArrayList<Double>();

                    for (int i = 0; i < searcher.listCount; i++) //모든 마커와의 거리 탐색
                        distance.add(distanceCalculate(
                                new LatLng(myLoc.getLatitude(), myLoc.getLongitude()), searcher.coordinateList[i]));

                    if (distance.size() > 0) { //마커가 하나 이상 있으면
                        shortestPlaceIndex = shortestPlace(distance);
                        Log.d("distance", searcher.shopName[shortestPlaceIndex] + "이 제일 가깝습니다.");
                        Log.d("distance", searcher.shopName[shortestPlaceIndex] + "은 "
                                + searcher.distance[shortestPlaceIndex] + "m 거리에 있습니다.");

                        shortestShopname.setText(searcher.shopName[shortestPlaceIndex]);
                        shortestAddress.setText(searcher.address[shortestPlaceIndex]);
                        shortestTel.setText(searcher.phone[shortestPlaceIndex]);
                        shortestCategory.setText(selectedCategory);
                    }
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    private LocationManager locationManager = null;
    private UserPref pref = null;

    //Map, marker
    private GoogleMap map = null;
    private MarkerManager markerManager = null;
    private MapFragment mapFragment = null;
    private int radius = 500;
    private short zoomLevel = 17;

    //Searcher
    private Searcher searcher = null;
    private ImageView drawerSearchButton = null;
    private EditText drawerSearchEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_main);

        try { //delete before activitys
            Activity introActivity = IntroActivity.introActivity;
            introActivity.finish();
            Activity setActivity = SetActivity.setActivity;
            setActivity.finish();
        } catch (Exception e) {
        }

        //category spinner set
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        categorySpinner.setOnItemSelectedListener(this);
        adapter = new CustomAdapter(getApplicationContext(), icon, category);
        categorySpinner.setAdapter(adapter);

        //navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawerOpen = false;
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerOpen = true;
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerSearchButton = (ImageView) findViewById(R.id.button_search);
        drawerSearchButton.setOnClickListener(drawerSearchButtonListener);
        drawerSearchEditText = (EditText) findViewById(R.id.drawer_search);

        //GPS security exception
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            else
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        //map layout set
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //shortest place button layout set
        shortestButton = (Button) findViewById(R.id.shortest_button);
        shortestShopname = (TextView) findViewById(R.id.shortest_shopname);
        shortestCategory = (TextView) findViewById(R.id.shortest_category);
        shortestTel = (TextView) findViewById(R.id.shortest_tel);
        shortestAddress = (TextView) findViewById(R.id.shortest_address);


        //GPS update set
        pref = new UserPref(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 7, locationListener);

        //place searcher and marker set
        searcher = new Searcher(getString(R.string.search_api_key));
        markerManager = new MarkerManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_bar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        Log.d("integer", id + ", " + R.id.search_button);

        if (id == R.id.search_button) {
            if (!isDrawerOpen)
                drawerLayout.openDrawer(Gravity.RIGHT);
            else
                drawerLayout.closeDrawers();
            return true;
        }

        return super.onOptionsItemSelected(item);
    } //actionBar item select

    //Listener and callback method
    @Override
    public void onMapReady(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            else
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        this.map = map;
        this.map.setMyLocationEnabled(true);
        this.map.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        this.map.setOnInfoWindowClickListener(onInfoWindowClickListener);
        this.map.setOnMapClickListener(onMapClickListener);
        this.map.setOnCameraMoveListener(onCameraMoveListener);

        if (pref.getLastLatitude() != 0)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom //앱 실행 시, 마지막으로 사용했던 위치를 기준으로 보여줌
                    (new LatLng(pref.getLastLatitude(), pref.getLastLongitude()), zoomLevel));
    } //google maps set

    public GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {

            map.moveCamera(CameraUpdateFactory.newLatLngZoom
                    (new LatLng(myLoc.getLatitude(), myLoc.getLongitude()), zoomLevel));

            searcher.search(selectedCategory, radius); //1000 == 1km
            markerManager.markerSet(searcher);
            markerManager.marking(map); //marking

            if (!autoCamera)
                Toast.makeText(MainActivity.this, "자동 화면이동 기능이 활성화되었습니다.", Toast.LENGTH_SHORT).show();

            autoSearch = true;
            autoCamera = true;

            return false;
        }
    };
    public GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {

            if (searcher != null) {
                for (int i = 0; i < searcher.listCount; i++)
                    if (searcher.shopName[i].equals(marker.getTitle())) {

                        //각 카테고리에 따라 다른 액티비티로 연결함
                        if (selectedCategory.equals("PC방")) {
                            SearchResult_pccafe result = new SearchResult_pccafe(searcher, i, selectedCategory);

                            Intent intent = new Intent(MainActivity.this, PlacesInfoActivity_pccafe.class);
                            intent.putExtra("SearchResult_pccafe", result);
                            startActivity(intent);
                        } else if (selectedCategory.equals("치킨")) {
                            SearchResult_chicken result = new SearchResult_chicken(searcher, i, selectedCategory);

                            Intent intent = new Intent(MainActivity.this, PlacesInfoActivity_chicken.class);
                            intent.putExtra("SearchResult_chicken", result);
                            startActivity(intent);
                        } else if (selectedCategory.equals("피자")) {
//                            SearchResult_pizza result = new SearchResult_pizza(searcher, i);
//
//                            Intent intent = new Intent(MainActivity.this, PlacesInfoActivity_pizza.class);
//                            intent.putExtra("SearchResult_pizza", result);
//                            startActivity(intent);
                        } else if (selectedCategory.equals("병원")) {
//                            SearchResult_hospital result = new SearchResult_hospital(searcher, i);
//
//                            Intent intent = new Intent(MainActivity.this, PlacesInfoActivity_hospital.class);
//                            intent.putExtra("SearchResult_hospital", result);
//                            startActivity(intent);
                        }
                    }
            }
        }
    };


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

        this.selectedCategory = category[position];
        searcher.search(this.selectedCategory, radius); //1000 == 1km
        markerManager.markerSet(searcher);
        markerManager.marking(map); //marking

        autoSearch = true;
        autoCamera = true;
    } //category select in spinner

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public View.OnClickListener drawerSearchButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            searcher.search(drawerSearchEditText.getText().toString(), 0);
            markerManager.markerSet(searcher);
            markerManager.marking(map); //marking
            autoSearch = false;
            autoCamera = false;

            map.moveCamera(CameraUpdateFactory.newLatLngZoom
                    (new LatLng(myLoc.getLatitude(), myLoc.getLongitude()), 10)); //7은 국가 크기 줌 레벨임

            isDrawerOpen = false;
            drawerLayout.closeDrawers();
        }
    };
    public Button.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {

            } catch (Exception e) {

            }
        }
    };
    public GoogleMap.OnMapClickListener onMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {

            if (autoCamera)
                Toast.makeText(MainActivity.this, "자동 화면이동 기능이 비활성화되었습니다.", Toast.LENGTH_SHORT).show();

            autoCamera = false;
        }
    };
    public GoogleMap.OnCameraMoveListener onCameraMoveListener = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            if (!autoCamera) {
                //현재 화면 중앙에서 가장 가까운 곳의 이름 출력
                ArrayList<Double> distance = new ArrayList<Double>();

                LatLng center = map.getCameraPosition().target;

                for (int i = 0; i < searcher.listCount; i++) //모든 마커와의 거리 탐색
                    distance.add(distanceCalculate(
                            new LatLng(center.latitude, center.longitude), searcher.coordinateList[i]));

                if (distance.size() > 0) { //마커가 하나 이상 있으면
                    shortestPlaceIndex = shortestPlace(distance);
                    Log.d("distance", searcher.shopName[shortestPlaceIndex] + "이 제일 가깝습니다.");
                    Log.d("distance", searcher.shopName[shortestPlaceIndex] + "은 "
                            + searcher.distance[shortestPlaceIndex] + "m 거리에 있습니다.");

                    shortestShopname.setText(searcher.shopName[shortestPlaceIndex]);
                    shortestAddress.setText(searcher.address[shortestPlaceIndex]);
                    shortestTel.setText(searcher.phone[shortestPlaceIndex]);
                    shortestCategory.setText(selectedCategory);
                }
            }
        }
    };

    public double distanceCalculate(LatLng x1, LatLng x2) { //두 점 사이의 거리 구하는 공식, 현재 위치와 모든 마커와의 거리를 계산함
        return Math.sqrt(Math.pow((x1.latitude - x2.latitude), 2) + Math.pow((x1.longitude - x2.longitude), 2));
    }

    public int shortestPlace(ArrayList<Double> distance) { //거리가 가장 짧은 장소를 추출하여 인덱스 반환
        try {
            int shortestPlaceIndex = 0;
            double shortestDistance = distance.get(0);

            for (int i = 0; i < distance.size(); i++)
                if (shortestDistance >= distance.get(i)) {
                    shortestDistance = distance.get(i);
                    shortestPlaceIndex = i;
                }

            return shortestPlaceIndex;
        } catch (Exception e) {
            return 0;
        }
    }
}