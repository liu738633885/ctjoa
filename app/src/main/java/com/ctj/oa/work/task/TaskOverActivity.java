package com.ctj.oa.work.task;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.widgets.TitleBar;
import com.hyphenate.chatuidemo.Constant;

public class TaskOverActivity extends BaseActivity {
    private TitleBar titleBar;
    private EditText edt;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_task_over;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task_over();
            }
        });
        edt = (EditText) findViewById(R.id.edt);
    }

    private void task_over() {
        NetBaseRequest request= RequsetFactory.creatBaseRequest(Constants.TASK_OVER);
        //request.add("")
    }
}
