package com.ctj.oa.work.log;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.log.Log;
import com.ctj.oa.model.work.log.LogList;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.MyViewUtils;
import com.ctj.oa.widgets.MyPopView;
import com.ctj.oa.widgets.TitleBar;
import com.lewis.utils.SearchBarUtils;
import com.lewis.widgets.LewisSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class LogListActivity extends BaseActivity implements View.OnClickListener, LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private Button btn_choose_1, btn_choose_2;
    private MyPopView myPopView1, myPopView2;
    private RecyclerView rv;
    private LewisSwipeRefreshLayout swl;
    private TitleBar titleBar;
    private int type;
    private int pagerNum;
    private int is_comment;
    private List<Log> tempList;
    private int example_id;
    private String q;
    private EditText editText;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_log_list;
    }

    public static void goTo(Context context, int type) {
        Intent intent = new Intent(context, LogListActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        type = intent.getIntExtra("type", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogListActivity.this, AddLogActivity.class));
            }
        });
        switch (type) {
            case 1:
                titleBar.setCenterText("我的日志");
                break;
            case 2:
                titleBar.setCenterText("全部日志");
                break;
            case 3:
                titleBar.setCenterText("共享给我");
                break;
            default:
                titleBar.setCenterText("日志列表");
        }
        editText = SearchBarUtils.init(this, new SearchBarUtils.OnSearchListener() {
            @Override
            public void onSearch(String string) {
                q = string;
                onRefresh();
            }

            @Override
            public void cleanSearch() {
                q = "";
                onRefresh();
            }
        });
        btn_choose_1 = (Button) findViewById(R.id.btn_choose_1);
        btn_choose_1.setOnClickListener(this);
        btn_choose_2 = (Button) findViewById(R.id.btn_choose_2);
        btn_choose_2.setOnClickListener(this);
        myPopView1 = new MyPopView(this);
        myPopView1.setData(new String[]{"全部", "已评", "未评"});
        myPopView1.setListener(new MyPopView.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                if (item.equals("全部")) {
                    item = "状态";
                }
                btn_choose_1.setText(item);
                is_comment = position;
                getLogList(0);
            }
        });
        myPopView2 = new MyPopView(this);
        myPopView2.setData(new String[]{"全部"});
        myPopView2.setListener(new MyPopView.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                if (item.equals("全部")) {
                    item = "类型";
                    example_id = -1;
                } else {
                    example_id = tempList.get(position - 1).getId();
                }
                btn_choose_2.setText(item);
                getLogList(0);
            }
        });
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view);
        swl.setRefreshing(true);
        getLogTemplate();
        onRefresh();
    }

    private void getLogList(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_LOG_LIST);
        request.add("type", type);
        request.add("is_comment", is_comment);
        request.add("pageno", num);
        if (example_id != -1) {
            request.add("example_id", example_id);
        }
        if (!TextUtils.isEmpty(q)) {
            request.add("q", q);
        }
        CallServer.getRequestInstance().add(this, num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    LogList logList = netBaseBean.parseObject(LogList.class);
                    pagerNum = logList.getPageno();
                    List<Log> list = logList.getList();
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    private void getLogTemplate() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_LOG_TEMPLATE);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    tempList = netBaseBean.parseList(Log.class);
                    List<String> tempNames = new ArrayList<String>();
                    tempNames.add("全部");
                    for (Log log : tempList) {
                        tempNames.add(log.getName());
                    }
                    myPopView2.setData(tempNames);
                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, false, "");
    }

    private BaseQuickAdapter<Log, BaseViewHolder> adapter = new BaseQuickAdapter<Log, BaseViewHolder>(R.layout.item_log) {
        @Override
        protected void convert(BaseViewHolder helper, final Log item) {
            try {
                TextView tv = helper.getView(R.id.tv);
                tv.setText(item.getTitle().substring(0, 1));
                GradientDrawable myGrad = (GradientDrawable) tv.getBackground().mutate();
                myGrad.setColor(Color.parseColor(item.getBg_color()));
                helper.setText(R.id.tv2, item.getTitle());
                helper.setText(R.id.tv3, item.getAdd_time());
                helper.setText(R.id.tv4, item.getComment_count() > 0 ? "已评" : "未评");
            } catch (Exception e) {
                e.printStackTrace();
            }
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogDetailActivity.goTo(bContext, item.getId());
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_1:
                myPopView1.showPopupWindow(v);
                break;
            case R.id.btn_choose_2:
                myPopView2.showPopupWindow(v);
                break;
            default:
        }
    }

    @Override
    public void onRefresh() {
        getLogList(0);
    }

    @Override
    public void onLoadMoreRequested() {
        getLogList(pagerNum);
    }

}
