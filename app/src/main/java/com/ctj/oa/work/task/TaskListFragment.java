package com.ctj.oa.work.task;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.fragment.BaseFragment;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.task.Task;
import com.ctj.oa.model.work.task.TaskList;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.MyViewUtils;
import com.lewis.utils.DateUtils;
import com.lewis.widgets.LewisSwipeRefreshLayout;

import java.util.List;


/**
 * Created by lewis on 2017/6/27.
 */

public class TaskListFragment extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private RecyclerView rv;
    private LewisSwipeRefreshLayout swl;
    private int status;
    private int role;
    private String q;
    private int pagerNum;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_task_list;
    }

    public static TaskListFragment newInstance(int status, int role) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        bundle.putInt("role", role);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        status = getArguments().getInt("status");
        role = getArguments().getInt("role");
        swl = (LewisSwipeRefreshLayout) view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view);
        onRefresh();
    }

    public void setQ(String q) {
        this.q = q;
        onRefresh();
    }

    @Override
    public void onRefresh() {
        getTaskList(0);
    }

    @Override
    public void onLoadMoreRequested() {
        getTaskList(pagerNum);
    }

    private void getTaskList(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.TASK_LIST);
        if (!TextUtils.isEmpty(q)) {
            request.add("q", q);
        }
        request.add("pageno", num);
        request.add("status", status);
        request.add("role", role);
        CallServer.getRequestInstance().add(getActivity(), num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    TaskList taskList = netBaseBean.parseObject(TaskList.class);
                    pagerNum = taskList.getPageno();
                    List<Task> list = taskList.getList();
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    private BaseQuickAdapter<Task, BaseViewHolder> adapter = new BaseQuickAdapter<Task, BaseViewHolder>(R.layout.item_task) {


        @Override
        protected void convert(BaseViewHolder helper, final Task item) {
            ImageView imv = helper.getView(R.id.imv);
            switch (item.getTask_item()) {
                case 1:
                    imv.setVisibility(View.VISIBLE);
                    imv.setImageResource(R.mipmap.ic_new);
                    break;
                case 2:
                    imv.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    imv.setVisibility(View.VISIBLE);
                    imv.setImageResource(R.mipmap.ic_yan);
                    break;
                default:
                    imv.setVisibility(View.INVISIBLE);

            }
            helper.setText(R.id.tv1, item.getTitle());
            //1进行中 2待接收 3待审核 4已完成 5已撤销
            switch (item.getStatus()) {
                case 1:
                    helper.setText(R.id.tv2, "进行中");
                    break;
                case 2:
                    helper.setText(R.id.tv2, "待接收");
                    break;
                case 3:
                    helper.setText(R.id.tv2, "待审核");
                    break;
                case 4:
                    helper.setText(R.id.tv2, "已完成");
                    break;
                case 5:
                    helper.setText(R.id.tv2, "已撤销");
                    break;
            }
            helper.setText(R.id.tv3, "负责人: " + item.getPrincipal_user_nickname());
            String text1 = "";
            if (item.getEnd_time() == 0) {
                text1 = "尽快完成";
            } else {
                try {
                    text1 = DateUtils.tenLongToString(item.getEnd_time(), DateUtils.DB_DATA_FORMAT2) + " 之前完成";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String text2 = "";
            switch (item.getTight()) {
                case 1:
                    text2 = " [普通]";
                    break;
                case 2:
                    text2 = " [紧急]";
                    break;
                case 3:
                    text2 = " [重要]";
                    break;
                case 4:
                    text2 = " [重要且紧急]";
                    break;
            }
            helper.setText(R.id.tv4, text1 + text2);
            helper.setText(R.id.tv5, "更新于  " + item.getUpdate_time());
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskDetailActivity.goTo(getActivity(), item.getId());
                }
            });
        }
    };
}
