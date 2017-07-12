package com.ctj.oa.work.task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.fragment.BaseFragment;
import com.ctj.oa.widgets.TitleBar;
import com.lewis.utils.SearchBarUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends BaseActivity {
    private TabLayout tabLayout;
    private List<BaseFragment> list_fragment = new ArrayList<>();
    private ViewPager viewPager;
    private TitleBar titleBar;
    private EditText edt;
    private int role;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_task_list;
    }

    public static void goTo(Context context, int role) {
        Intent intent = new Intent(context, TaskListActivity.class);
        intent.putExtra("role", role);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        role = intent.getIntExtra("role", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(bContext, AddTaskActivity.class));
            }
        });
        edt = SearchBarUtils.init(this, new SearchBarUtils.OnSearchListener() {
            @Override
            public void onSearch(String string) {
                try {
                    currentFragment.setQ(string);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void cleanSearch() {
                try {
                    currentFragment.setQ("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.addTab(tabLayout.newTab().setText("待接收"));
        tabLayout.addTab(tabLayout.newTab().setText("进行中"));
        tabLayout.addTab(tabLayout.newTab().setText("待审核"));
        tabLayout.addTab(tabLayout.newTab().setText("已完成"));
        tabLayout.addTab(tabLayout.newTab().setText("已撤销"));
        list_fragment.add(TaskListFragment.newInstance(0, role));
        list_fragment.add(TaskListFragment.newInstance(2, role));
        list_fragment.add(TaskListFragment.newInstance(1, role));
        list_fragment.add(TaskListFragment.newInstance(3, role));
        list_fragment.add(TaskListFragment.newInstance(4, role));
        list_fragment.add(TaskListFragment.newInstance(5, role));
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //viewPager.setOffscreenPageLimit(6);
    }

    public TaskListFragment currentFragment;
    private FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        private String tabTitles[] = new String[]{"全部", "待接收", "进行中", "待审核", "已完成", "已撤销"};

        @Override
        public Fragment getItem(int position) {
            return list_fragment.get(position);
        }

        @Override
        public int getCount() {
            return list_fragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }


        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            currentFragment = (TaskListFragment) object;
            super.setPrimaryItem(container, position, object);
        }

    };
}
