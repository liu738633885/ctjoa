package com.ctj.oa.chat.message;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.message.Message;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.GoToUtils;
import com.ctj.oa.utils.MyViewUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.lewis.utils.DateUtils;
import com.lewis.widgets.LewisSwipeRefreshLayout;

import java.util.List;

public class MessageListActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rv;
    private LewisSwipeRefreshLayout swl;
    protected EMConversation conversation;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_message_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        adapter.bindToRecyclerView(rv);
        adapter.setEmptyView(R.layout.empty_view);
        conversation = EMClient.getInstance().chatManager().getConversation(Constants.ADMIN_ID, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true);
        conversation.markAllMessagesAsRead();
    }


    private void get_message_list() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_MESSAGE_LIST);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Message> list = netBaseBean.parseList(Message.class);
                    MyViewUtils.bindList(adapter, list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    private BaseQuickAdapter<Message, BaseViewHolder> adapter = new BaseQuickAdapter<Message, BaseViewHolder>(R.layout.item_message) {
        @Override
        protected void convert(BaseViewHolder helper, final Message item) {
            try {
                String text = "消息类型";
                switch (item.getType()) {
                    case 0:
                        text = "默认消息";
                        break;
                    case 1:
                        text = "审批消息";
                        break;
                    case 2:
                        text = "抄送消息";
                        break;
                    case 3:
                        text = "审批结果";
                        break;
                    case 4:
                        text = "公告消息";
                        break;
                    case 5:
                        text = "日志消息";
                        break;
                    case 6:
                        text = "备忘消息";
                        break;
                    case 7:
                        text = "任务消息";
                        break;
                    case 8:
                        text = "文件共享";
                        break;
                }
                helper.setText(R.id.tv1, text);
                helper.setText(R.id.tv2, item.getMessage_title());
                helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GoToUtils.goToFormMessage(bContext, item.getType_id(), item.getType());
                    }
                });
                helper.setText(R.id.tv3, DateUtils.tenLongToString(item.getCreate_time(), DateUtils.DB_DATA_FORMAT2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onRefresh() {
        get_message_list();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onRefresh();
    }

}
