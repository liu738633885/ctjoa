package com.ctj.oa.work.memo;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.UserInfo;
import com.ctj.oa.model.event.EventRefresh;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.ctj.oa.widgets.TitleBar;
import com.ctj.oa.work.ChooseUserActivity;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lewis.utils.DateUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/5/3.
 */

public class AddMemoActivity extends BaseActivity {
    private String TAG = AddMemoActivity.this.getClass().getSimpleName();
    private EditText edt;
    private TimePickerDialog pickerDialog;
    private LinearLayout ll1, ll_tip, ll_choose_start_time, ll_choose_tip_time, ll_chongfu, ll_week;
    private ToggleButton toggleButton;
    private List<UserInfo> users = new ArrayList<UserInfo>();
    private RecyclerView rv;
    private TitleBar titleBar;
    private Spinner spinner, sp_month, sp_day,sp_tips_range_type;
    private int tips_type = 1;
    private int tips_month = 1;
    private int tips_day = 1;
    private int tips_range_type = 1;
    private String tips_start_time, tip_time_hour, tip_time_minute;
    private List<Integer> weeks = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_memo;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoSave();
            }
        });
        edt = (EditText) findViewById(R.id.edt);
        ll1 = (LinearLayout) findViewById(R.id.ll1);
        ll_choose_start_time = (LinearLayout) findViewById(R.id.ll_choose_start_time);
        ll_choose_tip_time = (LinearLayout) findViewById(R.id.ll_choose_tip_time);
        ll_chongfu = (LinearLayout) findViewById(R.id.ll_chongfu);
        ll_week = (LinearLayout) findViewById(R.id.ll_week);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        ll_tip = (LinearLayout) findViewById(R.id.ll_tip);
        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(userAdapter);
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseUserActivity.goTo(bContext, users, TAG);
            }
        });
        ll_choose_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatTimeDialog(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        try {
                            tips_start_time = DateUtils.tenLongToString(millseconds / 1000, DateUtils.yyyyMMDD);
                            ((TextView) ll_choose_start_time.getChildAt(1)).setText(tips_start_time);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, Type.YEAR_MONTH_DAY);
            }
        });
        ll_choose_tip_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatTimeDialog(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        try {
                            String tip_time = "";
                            tip_time = DateUtils.tenLongToString(millseconds / 1000, DateUtils.hhmm);
                            ((TextView) ll_choose_tip_time.getChildAt(1)).setText(tip_time);
                            String[] x = tip_time.split(":");
                            tip_time_hour = x[0];
                            tip_time_minute = x[1];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, Type.HOURS_MINS);
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ll_tip.setVisibility(View.VISIBLE);
                } else {
                    ll_tip.setVisibility(View.GONE);
                }
            }
        });
        spinner = (Spinner) findViewById(R.id.spinner);
        sp_month = (Spinner) findViewById(R.id.sp_month);
        sp_day = (Spinner) findViewById(R.id.sp_day);
        sp_tips_range_type = (Spinner) findViewById(R.id.sp_tips_range_type);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tips_type = position + 1;
                switch (tips_type) {
                    case 1:
                        ll_chongfu.setVisibility(View.GONE);
                        ll_week.setVisibility(View.GONE);
                        break;
                    case 2:
                        ll_chongfu.setVisibility(View.GONE);
                        ll_week.setVisibility(View.GONE);
                        break;
                    case 3:
                        ll_chongfu.setVisibility(View.GONE);
                        ll_week.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        ll_chongfu.setVisibility(View.VISIBLE);
                        sp_month.setVisibility(View.INVISIBLE);
                        sp_day.setVisibility(View.VISIBLE);
                        ll_week.setVisibility(View.GONE);
                        break;
                    case 5:
                        ll_chongfu.setVisibility(View.VISIBLE);
                        sp_month.setVisibility(View.VISIBLE);
                        sp_day.setVisibility(View.VISIBLE);
                        ll_week.setVisibility(View.GONE);
                        break;
                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tips_month = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tips_day = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_tips_range_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tips_range_type=position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void memoSave() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.MEMO_SAVE);
        if (TextUtils.isEmpty(edt.getText().toString())) {
            Toast("请输入备忘内容");
            return;
        }
        request.add("content", edt.getText().toString());
        if (users != null && users.size() > 0) {
            String ids = "";
            for (UserInfo info : users) {
                ids = ids + info.getId() + ",";
            }
            if (ids.endsWith(",")) {
                ids = ids.substring(0, ids.length() - 1);
            }
            request.add("share_user_id", ids);
        } else {
            request.add("share_user_id", 0);
        }
        if (toggleButton.isChecked()) {
            request.add("tips_type", tips_type);
            switch (tips_type) {
                case 3:
                    String strs = "";
                    for (int i = 0; i < ll_week.getChildCount(); i++) {
                        CheckBox cb = (CheckBox) ll_week.getChildAt(i);
                        if (cb.isChecked()) {
                            strs = strs + i + ",";
                        }
                    }
                    if (strs.endsWith(",")) {
                        strs = strs.substring(0, strs.length() - 1);
                    }
                    if (TextUtils.isEmpty(strs)) {
                        Toast("最少要选择一个星期数");
                        return;
                    }
                    request.add("tips_week_day", strs);
                    break;
                case 4:
                    request.add("tips_day", tips_day);
                    break;
                case 5:
                    request.add("tips_month", tips_month);
                    request.add("tips_day", tips_day);
                    break;
                default:
            }
            if (TextUtils.isEmpty(tips_start_time)) {
                Toast("请选择开始日期");
                return;
            }
            request.add("tips_start_time", tips_start_time);
            if (TextUtils.isEmpty(tip_time_hour) || TextUtils.isEmpty(tip_time_minute)) {
                Toast("请选择提醒时间");
                return;
            }
            request.add("tips_hours", tip_time_hour);
            request.add("tips_minute", tip_time_minute);
            request.add("tips_range_type", tips_range_type);
        }
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                Toast(netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private BaseQuickAdapter<UserInfo, BaseViewHolder> userAdapter = new BaseQuickAdapter<UserInfo, BaseViewHolder>(R.layout.item_user) {
        @Override
        protected void convert(BaseViewHolder helper, UserInfo item) {
            helper.setText(R.id.tv, item.getNickname());
            if (TextUtils.isEmpty(item.getPortrait())) {
                helper.setVisible(R.id.imv, false);
                helper.setVisible(R.id.tv0, true);
                try {
                    helper.setText(R.id.tv0, item.getNickname().substring(0, 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ImageLoader.loadHeadImage(bContext, item.getPortrait(), ((ImageView) helper.getView(R.id.imv)), 2);
                helper.setVisible(R.id.imv, true);
                helper.setVisible(R.id.tv0, false);
            }
        }

    };

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e.getWhere().equals(TAG)) {
            if (e.getData() != null) {
                users = (List<UserInfo>) e.getData();
                userAdapter.setNewData(users);
            }
        }
    }

    private void creatTimeDialog(OnDateSetListener listener, Type type) {
        if (listener == null) {
            return;
        }
        long twoYears = 2L * 365 * 1000 * 60 * 60 * 24L;
        TimePickerDialog.Builder build = new TimePickerDialog.Builder()
                .setCallBack(listener)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + twoYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(ContextCompat.getColor(bContext, R.color.super_blue2))
                .setWheelItemTextNormalColor(ContextCompat.getColor(bContext, R.color.gray01))
                .setWheelItemTextSelectorColor(ContextCompat.getColor(bContext, R.color.super_blue))
                .setWheelItemTextSize(12);
        build.setType(type);
        pickerDialog = build.build();
        pickerDialog.show(getSupportFragmentManager(), type + "");
    }
}
