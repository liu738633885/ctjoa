package com.ctj.oa.work.sign;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.attend.Shift;
import com.ctj.oa.model.attend.SignRecord;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.lewis.utils.DateUtils;
import com.lewis.utils.T;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class SignActivity extends BaseActivity {
    private MapView mMapView = null;
    private AMap mAMap;
    private MarkerOptions markerOption;
    private Marker marker;
    private RecyclerView rv;
    private View headView, footerView, headViewLine;
    private TextView tv_sign, tv_time;
    private LinearLayout ll_sign;
    private String address;
    private Location mlocation;
    private Shift shift;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_sign;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
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
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        headView = LayoutInflater.from(this).inflate(R.layout.item_sign1, rv, false);
        headViewLine = headView.findViewById(R.id.line);
        adapter.addHeaderView(headView);
        footerView = LayoutInflater.from(this).inflate(R.layout.footerview_sign, rv, false);
        adapter.addFooterView(footerView);
        tv_sign = (TextView) footerView.findViewById(R.id.tv_sign);
        tv_time = (TextView) footerView.findViewById(R.id.tv_time);
        ll_sign = (LinearLayout) footerView.findViewById(R.id.ll_sign);
        ll_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSign();
            }
        });
        setTime();
        getTodayShift();
    }

    private void addSign() {
        if (mlocation == null || mlocation.getLatitude() == 0 || mlocation.getLongitude() == 0) {
            T.showShort(this, "还没有定位成功,定位不准确");
            //return;
        }
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.SIGN);
        request.add("address", address);
        request.add("longitude", mlocation.getLongitude());
        request.add("latitude", mlocation.getLatitude());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    getToadySignRecord();
                } else {
                    T.showShort(SignActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void getToadySignRecord() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.TODAY_SIGN_RECORD);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    SignRecord signRecord = netBaseBean.parseObject(SignRecord.class);
                    updateRv(signRecord);
                } else {
                    T.showShort(SignActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void getTodayShift() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.TODAY_SHIFT);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    shift = netBaseBean.parseObject(Shift.class);
                    updateHeadview(shift);
                    getToadySignRecord();
                } else {
                    T.showShort(SignActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void updateHeadview(Shift shift) {
        String nowdate = DateUtils.getNowDate(DateUtils.yyyyMMDD) + "   星期" + DateUtils.getWeek(System.currentTimeMillis());
        ((TextView) headView.findViewById(R.id.tv1)).setText(nowdate);
        try {
            String shiftText = DateUtils.tenLongToString(shift.getSign_in(), DateUtils.hhmm) + "-" + DateUtils.tenLongToString(shift.getSign_out(), DateUtils.hhmm);
            ((TextView) headView.findViewById(R.id.tv2)).setText("默认班次: " + shiftText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRv(SignRecord signRecord) {
        List<Sign> list = new ArrayList<>();
        GradientDrawable myGrad = (GradientDrawable) ll_sign.getBackground().mutate();
        if (signRecord == null || signRecord.getAdd_time() == 0) {
            //没有签到
            myGrad.setColor(0xff4fda7a);
            tv_sign.setText("签到");
            headViewLine.setVisibility(View.INVISIBLE);
        } else {
            myGrad.setColor(0xff2c99eb);
            tv_sign.setText("签退");
            headViewLine.setVisibility(View.VISIBLE);
            list.add(new Sign(0, signRecord.getAdd_time(), signRecord.getAddress()));
            if (signRecord.getCheck_time() != 0) {
                list.add(new Sign(1, signRecord.getCheck_time(), signRecord.getCheck_address()));
            }
        }
        adapter.setNewData(list);
    }

    class Sign {
        //0 签到  1签退
        public int type;
        public long time;
        public String address;

        public Sign(int type, long time, String address) {
            this.type = type;
            this.time = time;
            this.address = address;
        }
    }

    private void setTime() {
        tv_time.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_time.setText(DateUtils.getNowTime(DateUtils.HHmmss));
                setTime();
            }
        }, 1000);
    }

    private BaseQuickAdapter<Sign, BaseViewHolder> adapter = new BaseQuickAdapter<Sign, BaseViewHolder>(new ArrayList<Sign>()) {
        @Override
        protected void convert(BaseViewHolder helper, Sign item) {
            try {
                helper.setText(R.id.tv1, DateUtils.tenLongToString(item.time, DateUtils.hhmm));
                helper.setText(R.id.tv2, item.type == 0 ? "签到" : "签退");
                helper.setText(R.id.tv4, item.address);
                helper.setVisible(R.id.tv3, false);
                if (item.type == 0 && shift != null && shift.getSign_in() != null && item.time > shift.getSign_in()) {
                    helper.setVisible(R.id.tv3, true);
                    helper.setText(R.id.tv3, "迟到");
                }
                if (item.type == 1 && shift != null && shift.getSign_out() != null && item.time < shift.getSign_out()) {
                    helper.setVisible(R.id.tv3, true);
                    helper.setText(R.id.tv3, "早退");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (helper.getView(R.id.line) != null) {
                helper.setVisible(R.id.line, helper.getItemViewType() != getData().size() - 1);
            }

        }

        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
            return new BaseViewHolder(this.getItemView(R.layout.item_sign2, parent));
        }

        @Override
        protected int getDefItemViewType(int position) {
            return position;
        }
    };

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
