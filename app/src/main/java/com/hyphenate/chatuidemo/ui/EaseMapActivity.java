/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.chatuidemo.ui;


import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.ctj.oa.R;
import com.ctj.oa.widgets.TitleBar;
import com.lewis.utils.T;
import com.orhanobut.logger.Logger;

public class EaseMapActivity extends com.ctj.oa.activity.BaseActivity {
    private MapView mMapView = null;
    private AMap mAMap;
    private MarkerOptions markerOption;
    private Marker marker;
    private String address;
    private Location mlocation;
    private TitleBar titleBar;
    double lat, lng;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_ease_map;
    }

    //获取Intent
    protected void handleIntent(Intent intent) {
        lat = intent.getDoubleExtra("latitude", 0);
        lng = intent.getDoubleExtra("longitude", 0);
        address = intent.getStringExtra("address");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLocation();
            }
        });
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(
                BitmapDescriptorFactory.fromResource(R.mipmap.gps_point));
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        //myLocationStyle.interval(5000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        mAMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        if (!TextUtils.isEmpty(address)) {
            titleBar.getRightGroup().setVisibility(View.GONE);
            mAMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lat, lng), 15, 0, 0)));
            mAMap.clear();
            markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(new LatLng(lat, lng))
                    .snippet(address);
            marker =mAMap.addMarker(markerOption);
            marker.showInfoWindow();
                    /*.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));*/
            return;
        }
        mAMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.moveCamera(CameraUpdateFactory.zoomBy(15));
        mAMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                mlocation = location;
                //T.showLong(SignActivity.this, ((AMapLocation)mAMap.getMyLocation()().toString());
                String str = location.toString();
                Logger.e("定位信息" + str);
                address = str.substring(str.indexOf("#address=") + 9, str.lastIndexOf("#country="));

                Logger.e("地址" + address);
                //
                mAMap.clear();
                if (!TextUtils.isEmpty(address)) {
                    markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .snippet(address);
                    // .snippet("内容");
                    //.snippet(locaion.toString());
                    marker = mAMap.addMarker(markerOption);
                    marker.showInfoWindow();
                }
            }
        });
    }


    public void sendLocation() {
        if (mlocation == null || mlocation.getLatitude() == 0 || mlocation.getLongitude() == 0) {
            T.showShort(this, "还没有定位成功,定位不准确");
            return;
        }
        Intent intent = this.getIntent();
        intent.putExtra("latitude", mlocation.getLatitude());
        intent.putExtra("longitude", mlocation.getLongitude());
        intent.putExtra("address", address);
        this.setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
