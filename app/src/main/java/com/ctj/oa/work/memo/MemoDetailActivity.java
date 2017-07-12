package com.ctj.oa.work.memo;

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
import com.ctj.oa.model.work.memo.Memo;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.MyViewUtils;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.lewis.utils.CommonUtils;
import com.lewis.utils.DateUtils;

import java.util.List;

public class MemoDetailActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {
    private int id;
    private RecyclerView rv;
    private int pagerNum;
    private EditText edt;
    private Button btn_send;
    private View headView;
    private ImageView imv, imv2;
    private TextView tv2, tv3, tv_content, tv4;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_memo_detail;
    }

    public static void goTo(Context context, int id) {
        Intent intent = new Intent(context, MemoDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        id = intent.getIntExtra("id", id);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(commentAdapter);
        commentAdapter.setOnLoadMoreListener(this, rv);
        headView = LayoutInflater.from(this).inflate(R.layout.headview_memo_detail, (ViewGroup) rv.getParent(), false);
        tv2 = (TextView) headView.findViewById(R.id.tv2);
        tv3 = (TextView) headView.findViewById(R.id.tv3);
        tv_content = (TextView) headView.findViewById(R.id.tv_content);
        tv4 = (TextView) headView.findViewById(R.id.tv4);
        imv = (ImageView) headView.findViewById(R.id.imv);
        imv2 = (ImageView) headView.findViewById(R.id.imv2);
        commentAdapter.addHeaderView(headView);
        edt = (EditText) findViewById(R.id.edt);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
        getMemoInfo();
    }

    private void sendComment() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ADD_MEMO_COMMENT);
        request.add("memo_id", id);
        if (TextUtils.isEmpty(edt.getText().toString())) {
            Toast("请输入评论内容");
            return;
        }
        request.add("content", edt.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    getMemoInfo();
                    edt.setText("");
                    CommonUtils.hideSoftInput(bContext, edt);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void getMemoInfo() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_MEMO_INFO);
        request.add("memo_id", id);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    Memo memo = netBaseBean.parseObject(Memo.class);
                    try {
                        tv2.setText(memo.getNickname());
                        tv3.setText(DateUtils.tenLongToString(memo.getAdd_time(), DateUtils.yyyyMMddHHmmss));
                        tv_content.setText(memo.getContent());
                        if (memo.getComment_count() > 0) {
                            tv4.setText("已评  (" + memo.getComment_count() + ")");
                        } else {
                            tv4.setText("未评论");
                        }
                        ImageLoader.loadHeadImage(bContext, memo.getPortrait(), imv, 2);
                        if (memo.getIs_tips() == 1) {
                            imv2.setVisibility(View.VISIBLE);
                        } else {
                            imv2.setVisibility(View.INVISIBLE);
                        }
                        getCommentList(0);
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
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_MEMO_COMMENT_LIST);
        request.add("page_no", num);
        request.add("memo_id", id);
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
