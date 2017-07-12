package com.ctj.oa.work.task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.activity.WebViewActivity;
import com.ctj.oa.model.event.EventRefresh;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.task.Task;
import com.ctj.oa.model.work.task.TaskDiscuss;
import com.ctj.oa.model.work.task.TaskDiscussList;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.MyViewUtils;
import com.ctj.oa.utils.manager.UserManager;
import com.lewis.utils.DateUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class TaskDetailActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, View.OnClickListener {
    private int id;
    private RecyclerView rv;
    private View headView;
    private int pagerNum;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9;
    private Button btn1, btn_accept, btn_over, btn_cancel, btn_ok, btn_not, btn_recover;
    private Task task;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_task_detail;
    }

    public static void goTo(Context context, int id) {
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        id = intent.getIntExtra("id", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        headView = LayoutInflater.from(this).inflate(R.layout.headview_task_detail, (ViewGroup) rv.getParent(), false);
        tv1 = (TextView) headView.findViewById(R.id.tv1);
        tv2 = (TextView) headView.findViewById(R.id.tv2);
        tv3 = (TextView) headView.findViewById(R.id.tv3);
        tv4 = (TextView) headView.findViewById(R.id.tv4);
        tv5 = (TextView) headView.findViewById(R.id.tv5);
        tv6 = (TextView) headView.findViewById(R.id.tv6);
        tv7 = (TextView) headView.findViewById(R.id.tv7);
        tv8 = (TextView) headView.findViewById(R.id.tv8);
        tv9 = (TextView) headView.findViewById(R.id.tv9);
        //底部按钮
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTaskDiscussActivity.goTo(bContext, task.getMay_discuss_list(), id);
            }
        });
        btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(this);
        btn_over = (Button) findViewById(R.id.btn_over);
        btn_over.setOnClickListener(this);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        btn_not = (Button) findViewById(R.id.btn_not);
        btn_not.setOnClickListener(this);
        btn_recover = (Button) findViewById(R.id.btn_recover);
        btn_recover.setOnClickListener(this);
        adapter.addHeaderView(headView);
        getTaskInfo();
    }


    private void getTaskInfo() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.TASK_INFO);
        request.add("task_id", id);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    task = netBaseBean.parseObject(Task.class);
                    try {
                        tv1.setText(task.getTitle());
                        tv2.setText(task.getUpdate_time());
                        btn1.setVisibility(task.getIs_discuss() == 1 ? View.VISIBLE : View.GONE);
                        btn_accept.setVisibility(task.getIs_receipt() == 1 ? View.VISIBLE : View.GONE);
                        btn_over.setVisibility(task.getIs_over() == 1 ? View.VISIBLE : View.GONE);
                        btn_ok.setVisibility(task.getIs_confirm() == 1 ? View.VISIBLE : View.GONE);
                        btn_not.setVisibility(task.getIs_confirm() == 1 ? View.VISIBLE : View.GONE);
                        btn_cancel.setVisibility(task.getIs_cancel() == 1 ? View.VISIBLE : View.GONE);
                        btn_recover.setVisibility(task.getIs_recover() == 1 ? View.VISIBLE : View.GONE);
                        switch (task.getStatus()) {
                            case 1:
                                tv3.setText("进行中");
                                break;
                            case 2:
                                tv3.setText("待接收");
                                break;
                            case 3:
                                tv3.setText("待审核");
                                break;
                            case 4:
                                tv3.setText("已完成");
                                break;
                            case 5:
                                tv3.setText("已撤销");
                                break;
                        }
                        switch (task.getTight()) {
                            case 1:
                                tv4.setText("普通");
                                break;
                            case 2:
                                tv4.setText("紧急");
                                break;
                            case 3:
                                tv4.setText("重要");
                                break;
                            case 4:
                                tv4.setText("重要且紧急");
                                break;
                        }
                        if (task.getEnd_time() == 0) {
                            tv5.setText("尽快完成");
                        } else {
                            try {
                                tv5.setText(DateUtils.tenLongToString(task.getEnd_time(), DateUtils.DB_DATA_FORMAT2) + "之前完成");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        tv6.setText(task.getCreate_user_name());
                        tv7.setText(task.getPrincipal_user_name());
                        tv8.setText(task.getParticipant_user_name());
                        task_discuss_list(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void task_discuss_list(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.TASK_DISCUSS_LIST);
        request.add("page_no", num);
        request.add("task_id", id);
        CallServer.getRequestInstance().add(this, num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    TaskDiscussList data = netBaseBean.parseObject(TaskDiscussList.class);
                    List<TaskDiscuss> list = data.getList();
                    pagerNum = data.getPageno();
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private BaseQuickAdapter<TaskDiscuss, BaseViewHolder> adapter = new BaseQuickAdapter<TaskDiscuss, BaseViewHolder>(R.layout.item_task_discuss) {
        @Override
        protected void convert(BaseViewHolder helper, final TaskDiscuss item) {
            helper.setText(R.id.tv, "[讨论]   " + item.getTitle());
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewActivity.goTo(bContext, Constants.WEB_TASK_DISCUSS + "?discuss_id=" + item.getId() + "&user_id=" + UserManager.getId() + "&token=" + UserManager.getToken(), "讨论详情");
                }
            });
        }
    };

    @Override
    public void onLoadMoreRequested() {
        task_discuss_list(pagerNum);
    }

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e.getAction().equals(TaskDetailActivity.this.getClass().getSimpleName())) {
            getTaskInfo();
        }
    }

    @Override
    public void onClick(View v) {
        String url = "";
        int status = 0;
        switch (v.getId()) {
            case R.id.btn_cancel:
                url = Constants.TASK_CANCEL;
                break;
            case R.id.btn_accept:
                url = Constants.TASK_ACCEPT;
                break;
            case R.id.btn_over:
                url = Constants.TASK_OVER;
                break;
            case R.id.btn_ok:
                url = Constants.TASK_CONFIRM;
                status = 2;
                break;
            case R.id.btn_not:
                url = Constants.TASK_CONFIRM;
                status = 1;
                break;
            case R.id.btn_recover:
                url = Constants.TASK_RECOVER;
                break;
        }
        if (TextUtils.isEmpty(url)) {
            Toast("非法操作");
            return;
        }
        NetBaseRequest request = RequsetFactory.creatBaseRequest(url);
        request.add("task_id", id);
        if (status != 0) {
            request.add("status", status);
        }
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    getTaskInfo();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }
}
