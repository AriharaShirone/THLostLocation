package com.yukikaze.thlostlocation;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;

public class FirstFragment extends Fragment {

    private MapView mMapView=null;

    public static BaiduMap mBaiduMap=null;

    private static SharedPreferences thSharedPreferences;

    private static final String TAG = "FirstFragment";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView=getView().findViewById(R.id.bmapView);
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.animate=true;
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(latLng!=null){
                    MainActivity.animate=true;
                    MainActivity.setPosition2Center(mBaiduMap,MainActivity.curLoc,true,MainActivity.animate);
                }
            }
        });
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                MainActivity.animate=false;
            }
        });
    }

    public void onStart(){
        thSharedPreferences= getContext().getSharedPreferences("settings",Context.MODE_PRIVATE);
        String data=thSharedPreferences.getString("returned_data","");
        if(data.equals("0")&&(mBaiduMap.getMapType()!=BaiduMap.MAP_TYPE_NORMAL)){
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }
        else if(data.equals("1")&&(mBaiduMap.getMapType()!=BaiduMap.MAP_TYPE_SATELLITE)){
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        }
        super.onStart();
    }
    public void onResume(){
        mBaiduMap.setMyLocationEnabled(false);
        thSharedPreferences= getContext().getSharedPreferences("settings",Context.MODE_PRIVATE);
        String data=thSharedPreferences.getString("returned_data","");
        if(data.equals("0")&&(mBaiduMap.getMapType()!=BaiduMap.MAP_TYPE_NORMAL)){
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }
        else if(data.equals("1")&&(mBaiduMap.getMapType()!=BaiduMap.MAP_TYPE_SATELLITE)){
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        }
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }
}