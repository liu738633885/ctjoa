package com.ctj.oa.work.task;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.ctj.oa.widgets.TitleBar;
import com.ctj.oa.work.ChooseUserActivity;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lewis.utils.DateUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AddTaskActivity extends BaseActivity {
    private String TAG = AddTaskActivity.this.getClass().getSimpleName();
    private TitleBar titleBar;
    private EditText edt1, edt2;
    private Spinner spinner, spinner2;
    private TextView tv1, tv2, tv_time;
    private List<UserInfo> users = new ArrayList<>();
    private UserInfo fuzeUser = new UserInfo();
    private int chooseUserType = 1;//1单个,2多个
    private int tight = 1;
    private long end_time;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_task;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task_save();
            }
        });
        edt1 = (EditText) findViewById(R.id.edt1);
        edt2 = (EditText) findViewById(R.id.edt2);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv_time = (TextView) findViewById(R.id.tv_time);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tight = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    end_time = 0;
                    tv_time.setVisibility(View.GONE);
                } else {
                    end_time = System.currentTimeMillis() / 1000;
                    tv_time.setVisibility(View.VISIBLE);
                    try {
                        tv_time.setText(DateUtils.tenLongToString(end_time, DateUtils.DB_DATA_FORMAT2));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((View) tv1.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseUserType = 1;
                ChooseUserActivity.goTo(bContext, TAG);
            }
        });
        ((View) tv2.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseUserType = 2;
                ChooseUserActivity.goTo(bContext, users, TAG);
            }
        });
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatTimeDialog(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        end_time = millseconds / 1000;
                        try {
                            tv_time.setText(DateUtils.tenLongToString(end_time, DateUtils.DB_DATA_FORMAT2));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void task_save() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.TASK_SAVE);
        request.add("title", edt1.getText().toString());
        request.add("desc", edt2.getText().toString());
        request.add("tight", tight);
        if (fuzeUser != null && fuzeUser.getId() != 0) {
            request.add("principal_user_id", fuzeUser.getId());
        }
        String x = "";
        for (UserInfo u : users) {
            x += u.getId() + ",";
        }
        if (x.length() > 0 && x.endsWith(",")) {
            x = x.substring(0, x.length() - 1);
        }
        request.add("participant_user_id", x);
        request.add("end_time", end_time);
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

    private TimePickerDialog pickerDialog;

    private void creatTimeDialog(OnDateSetListener listener) {
        if (listener == null) {
            return;
        }
        long Years = 100L * 365 * 1000 * 60 * 60 * 24L;
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
                .setMinMillseconds(System.currentTimeMillis() - Years)
                .setMaxMillseconds(System.currentTimeMillis() + Years)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(ContextCompat.getColor(bContext, R.color.super_blue2))
                .setWheelItemTextNormalColor(ContextCompat.getColor(bContext, R.color.gray01))
                .setWheelItemTextSelectorColor(ContextCompat.getColor(bContext, R.color.super_blue))
                .setWheelItemTextSize(12);
        build.setType(Type.ALL);
        pickerDialog = build.build();
        pickerDialog.show(getSupportFragmentManager(), "all");
    }

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e.getWhere().equals(TAG)) {
            if (e.getData() != null) {
                if (chooseUserType == 1) {
                    fuzeUser = (UserInfo) e.getData();
                    tv1.setText(fuzeUser.getNickname());
                } else {
                    users = (List<UserInfo>) e.getData();
                    String x = "";
                    for (UserInfo u : users) {
                        x += u.getNickname() + ",";
                    }
                    if (x.length() > 0 && x.endsWith(",")) {
                        x = x.substring(0, x.length() - 1);
                    }
                    tv2.setText(x);
                }
            }
        }
    }
}
