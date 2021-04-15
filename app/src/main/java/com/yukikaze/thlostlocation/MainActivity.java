package com.yukikaze.thlostlocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public LocationClient locationClient=null;
    private THLocationListener thLocationListener=new THLocationListener();
    public static boolean animate=true;
    public static BDLocation curLoc=null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locationClient=new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new THLocationListener());
        FloatingActionButton fab = findViewById(R.id.fab);
        String[] permissions=new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE};
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reqPermission(permissions)&&isLocServiceEnable(getApplicationContext())) {
                    Snackbar.make(view, "Nekomimi Kawaii!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    animate=true;
                    requestLocation();
                }
                else
                    Snackbar.make(view, "E~~~n", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
        });
    }

    public void requestLocation(){
        LocationClientOption locationClientOption=new LocationClientOption();
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationClientOption.setCoorType("bd09ll");
        locationClientOption.setOpenGps(true);
        locationClientOption.setOpenAutoNotifyMode();
        locationClientOption.setIsNeedAddress(true);
        locationClientOption.setIsNeedLocationDescribe(true);
        locationClient.setLocOption(locationClientOption);
        locationClient.registerLocationListener(thLocationListener);
        locationClient.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy(){
        locationClient.stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }

    private boolean reqPermission(String[] p)
    {
        int m=0;
        for(String i:p) {
            m++;
            if (ContextCompat.checkSelfPermission(MainActivity.this, i) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{i},m);
        }
        for(String i:p)
            if(ContextCompat.checkSelfPermission(MainActivity.this, i)!=PackageManager.PERMISSION_GRANTED)
                return false;
            return true;
    }

    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    public class THLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(SecondFragment.textView!=null){
                        SecondFragment.textView.setText(generateLocInfo(location));
                        curLoc=location;
                    }
                    if(FirstFragment.mBaiduMap!=null){
                            setPosition2Center(FirstFragment.mBaiduMap,location,true,animate);
                    }
                }
            });
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            super.onConnectHotSpotMessage(s, i);
        }
    }
    public static void setPosition2Center(BaiduMap map, BDLocation bdLocation, Boolean isShowLoc,Boolean ani) {
        if(bdLocation!=null){
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            if (ani) {
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            if (isShowLoc) {
                FirstFragment.mBaiduMap.clear();
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_position3);
                OverlayOptions option = new MarkerOptions()
                        .position(ll)
                        .icon(bitmap);
                FirstFragment.mBaiduMap.addOverlay(option);
            }
        }

    }
    public String generateLocInfo(BDLocation location){

        String source[][]={{"纬度","经度","精度","定位方式","卫星","网络","还没得到位置信息哟","："},
                {"緯度","経度","精度","位置決めモード","GPS","ネット","位置情報、まだ得られてないよ","："},
                {"Latitude","Longitude","Accuracy","Mode","GPS","Network","Cannot get location information now",":"}};
        if(location!=null){
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append(source[getCurrentlanguage()][0]).append(source[getCurrentlanguage()][7]).append(location.getLatitude()).append("\n");
            currentPosition.append(source[getCurrentlanguage()][1]).append(source[getCurrentlanguage()][7]).append(location.getLongitude()).append("\n");
            currentPosition.append(source[getCurrentlanguage()][2]).append(source[getCurrentlanguage()][7]).append(location.getRadius()).append("m").append("\n");
            currentPosition.append(source[getCurrentlanguage()][3]).append(source[getCurrentlanguage()][7]);
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append(source[getCurrentlanguage()][4]).append("\n");
            } else if (location.getLocType() ==
                    BDLocation.TypeNetWorkLocation) {
                currentPosition.append(source[getCurrentlanguage()][5]).append("\n");
            }
            currentPosition.append(location.getCountry()).append(" ").append(location.getCity()).append(" ").append("\n");
            currentPosition.append(location.getLocationDescribe()).append("\n");
            return String.valueOf((currentPosition));
        }
        else return source[getCurrentlanguage()][6];
    }

    public int getCurrentlanguage() {
        Locale locale = getApplicationContext().getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if(language.equals("cn")){
            return 0;
        }
        else if(language.equals("ja")){
            return 1;
        }
        else if(language.equals("en")){
            return 2;
        }
        else return 0;
    }


}