package com.ctj.oa.work.task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddTaskDiscussActivity extends BaseActivity {
    private String TAG = AddTaskDiscussActivity.this.getClass().getSimpleName();
    private TitleBar titleBar;
    private EditText edt1, edt2;
    private Spinner spinner;
    private TextView tv;
    private List<UserInfo> users = new ArrayList<>();
    private int id;
    private int tight = 1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_task_discuss;
    }

    public static void goTo(Context context, List<UserInfo> list, int id) {
        Intent intent = new Intent(context, AddTaskDiscussActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("users", (Serializable) list);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        id = intent.getIntExtra("id", 0);
        users = (List<UserInfo>) intent.getSerializableExtra("users");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task_discuss_save();
            }
        });
        edt1 = (EditText) findViewById(R.id.edt1);
        edt2 = (EditText) findViewById(R.id.edt2);
        tv = (TextView) findViewById(R.id.tv);
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
        ((View) tv.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseUserActivity.goTo(bContext, users, TAG);
            }
        });
        updataUserUI();
    }

    private void task_discuss_save() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.TASK_DISCUSS_SAVE);
        request.add("task_id", id);
        request.add("title", edt1.getText().toString());
        request.add("content", edt2.getText().toString());
        request.add("tight", tight);
        String x = "";
        for (UserInfo u : users) {
            x += u.getId() + ",";
        }
        if (x.length() > 0 && x.endsWith(",")) {
            x = x.substring(0, x.length() - 1);
        }
        request.add("participant_user_id", x);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    EventBus.getDefault().post(new EventRefresh(TaskDetailActivity.class.getSimpleName()));
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void updataUserUI() {
        String x = "";
        for (UserInfo u : users) {
            x += u.getNickname() + ",";
        }
        if (x.length() > 0 && x.endsWith(",")) {
            x = x.substring(0, x.length() - 1);
        }
        tv.setText(x);
    }

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e.getWhere().equals(TAG)) {
            if (e.getData() != null) {
                users = (List<UserInfo>) e.getData();
                updataUserUI();
            }
        }
    }
}
