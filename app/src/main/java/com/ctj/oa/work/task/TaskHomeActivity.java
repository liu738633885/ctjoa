package com.ctj.oa.work.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;

public class TaskHomeActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll1, ll2, ll3, ll_add;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_task_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ll1 = (LinearLayout) findViewById(R.id.ll1);
        ll2 = (LinearLayout) findViewById(R.id.ll2);
        ll3 = (LinearLayout) findViewById(R.id.ll3);
        ll_add = (LinearLayout) findViewById(R.id.ll_add);
        ll1.setOnClickListener(this);
        ll2.setOnClickListener(this);
        ll3.setOnClickListener(this);
        ll_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll1:
//                startActivity(new Intent(this, TaskMainActivity.class));
                TaskListActivity.goTo(this, 2);
                break;
            case R.id.ll2:
                TaskListActivity.goTo(this, 1);
                break;
            case R.id.ll3:
                TaskListActivity.goTo(this, 3);
                break;
            case R.id.ll_add:
                startActivity(new Intent(bContext, AddTaskActivity.class));
                break;
        }
    }
}
