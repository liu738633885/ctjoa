package com.ctj.oa.work.task;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskMainActivity extends BaseActivity {
    private List<Fragment> fragment_list = new ArrayList<Fragment>();
    private int frg_now_num;
    private RadioGroup rg_bottom;
    private FragmentTransaction transaction;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_task_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rg_bottom = (RadioGroup) findViewById(R.id.rg_bottom);
        fragment_list.add(new Task1Fragment());
        fragment_list.add(new TaskListFragment());
        fragment_list.add(new TaskListFragment());
        rg_bottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb1:
                        switchContent(0);
                        break;
                    case R.id.rb2:
                        switchContent(1);
                        break;
                    case R.id.rb3:
                        switchContent(2);
                        break;
                    default:
                }
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_list.get(0)).commit();
    }

    public void switchContent(int wantto) {
        Fragment from = fragment_list.get(frg_now_num);
        Fragment to = fragment_list.get(wantto);
        if (frg_now_num != wantto) {
            frg_now_num = wantto;
            transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(from).add(R.id.fragment_container, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }

    }
}
