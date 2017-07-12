package com.ctj.oa.work.log;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.log.LogComment;
import com.ctj.oa.model.work.log.LogCommentList;
import com.ctj.oa.model.work.log.LogDetail;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.MyViewUtils;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.lewis.utils.CommonUtils;
import com.lewis.utils.DateUtils;

import java.util.List;

import static com.ctj.oa.R.id.tv;

public class LogDetailActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {
    private int id;
    private RecyclerView rv, rv0;
    private View headView, footerView;
    private TextView h_tv;
    private TextView h_tv2;
    private TextView h_tv3;
    private TextView h_tv4;
    private TextView f_tv;
    private int pagerNum;
    private EditText edt;
    private Button btn_send;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_log_detail;
    }

    public static void goTo(Context context, int id) {
        Intent intent = new Intent(context, LogDetailActivity.class);
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
        headView = LayoutInflater.from(this).inflate(R.layout.headview_log_detail, (ViewGroup) rv.getParent(), false);
        h_tv = (TextView) headView.findViewById(tv);
        h_tv2 = (TextView) headView.findViewById(R.id.tv2);
        h_tv3 = (TextView) headView.findViewById(R.id.tv3);
        h_tv4 = (TextView) headView.findViewById(R.id.tv4);
        adapter.addHeaderView(headView);
        footerView = LayoutInflater.from(this).inflate(R.layout.footerview_log_detail, (ViewGroup) rv.getParent(), false);
        f_tv = (TextView) footerView.findViewById(tv);
        rv0 = (RecyclerView) footerView.findViewById(R.id.rv0);
        rv0.setLayoutManager(new LinearLayoutManager(this));
        rv0.setAdapter(commentAdapter);
        commentAdapter.setOnLoadMoreListener(this, rv0);
        adapter.addFooterView(footerView);
        edt = (EditText) findViewById(R.id.edt);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
        getLogInfo();
    }

    private void sendComment() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ADD_LOG_COMMENT);
        request.add("log_id", id);
        if (TextUtils.isEmpty(edt.getText().toString())) {
            Toast("请输入评论内容");
            return;
        }
        request.add("content", edt.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    getLogInfo();
                    edt.setText("");
                    CommonUtils.hideSoftInput(bContext, edt);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void getLogInfo() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_LOG_INFO);
        request.add("log_id", id);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    LogDetail logDetail = netBaseBean.parseObject(LogDetail.class);
                    try {
                        h_tv.setText(logDetail.getTitle().substring(0, 1));
                        h_tv3.setText(DateUtils.tenLongToString(logDetail.getAdd_time(), DateUtils.yyyyMMddHHmmss));
                        h_tv2.setText(logDetail.getNickname());
                        if (logDetail.getComment_count() > 0) {
                            f_tv.setText("已评  (" + logDetail.getComment_count() + ")");
                            h_tv4.setText("已评");
                        } else {
                            h_tv4.setText("未评");
                            f_tv.setText("未评论");
                        }
                        GradientDrawable myGrad = (GradientDrawable) h_tv.getBackground().mutate();
                        myGrad.setColor(Color.parseColor(logDetail.getBg_color()));
                        getCommentList(0);
                        adapter.setNewData(logDetail.getContent());
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

    private BaseQuickAdapter<LogDetail.Item, BaseViewHolder> adapter = new BaseQuickAdapter<LogDetail.Item, BaseViewHolder>(R.layout.item_log_detail) {
        @Override
        protected void convert(BaseViewHolder helper, LogDetail.Item item) {
            helper.setText(R.id.tv1, item.title_field);
            helper.setText(R.id.tv2, item.value_field);
        }
    };
    private BaseQuickAdapter<LogComment, BaseViewHolder> commentAdapter = new BaseQuickAdapter<LogComment, BaseViewHolder>(R.layout.item_log_detail_comment) {
        @Override
        protected void convert(BaseViewHolder helper, LogComment item) {
            try {
                helper.setText(R.id.tv1, item.getUser_name());
                helper.setText(R.id.tv2, DateUtils.tenLongToString(item.getAdd_time(), DateUtils.yyyyMMddHHmmss));
                helper.setText(R.id.tv3, item.getContent());
                ImageLoader.loadHeadImage(bContext, item.getPortrait(), (ImageView) helper.getView(R.id.imv), 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void getCommentList(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_LOG_COMMENT_LIST);
        request.add("page_no", num);
        request.add("log_id", id);
        CallServer.getRequestInstance().add(this, num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    LogCommentList data = netBaseBean.parseObject(LogCommentList.class);
                    List<LogComment> list = data.getList();
                    pagerNum = data.getPageno();
                    MyViewUtils.bindListWithNum(pagerNum, what, commentAdapter, list);
                } else {
                    Toast(netBaseBean.getMessage());
                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    @Override
    public void onLoadMoreRequested() {
        getCommentList(pagerNum);
    }
}
