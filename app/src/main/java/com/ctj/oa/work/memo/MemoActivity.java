package com.ctj.oa.work.memo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
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

public class MemoActivity extends BaseActivity {
    private TabLayout tabLayout;
    private List<BaseFragment> list_fragment = new ArrayList<>();
    private ViewPager viewPager;
    private TitleBar titleBar;
    private EditText edt;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_memo;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MemoActivity.this, AddMemoActivity.class));
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
        tabLayout.addTab(tabLayout.newTab().setText("全部备忘"));
        tabLayout.addTab(tabLayout.newTab().setText("我的备忘"));
        tabLayout.addTab(tabLayout.newTab().setText("共享给我"));
        list_fragment.add(MemoListFragment.newInstance(2));
        list_fragment.add(MemoListFragment.newInstance(1));
        list_fragment.add(MemoListFragment.newInstance(3));
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public MemoListFragment currentFragment;
    private FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        private String tabTitles[] = new String[]{"全部备忘", "我的备忘", "共享给我"};

        @Override
        public Fragment getItem(int position) {
            return list_fragment.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }


        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            currentFragment = (MemoListFragment) object;
            super.setPrimaryItem(container, position, object);
        }

    };
}
