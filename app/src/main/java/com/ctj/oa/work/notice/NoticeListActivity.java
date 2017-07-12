package com.ctj.oa.work.notice;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.activity.WebViewActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.notice.Notice;
import com.ctj.oa.model.work.notice.NoticeList;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.MyViewUtils;
import com.ctj.oa.utils.manager.UserManager;
import com.ctj.oa.widgets.MyPopView;
import com.lewis.utils.CommonUtils;
import com.lewis.utils.DateUtils;
import com.lewis.widgets.LewisSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class NoticeListActivity extends BaseActivity implements View.OnClickListener, LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private RecyclerView rv;
    private LewisSwipeRefreshLayout swl;
    private Button btn_choose_1;
    private MyPopView myPopView1;
    private List<Notice> classList = new ArrayList<Notice>();
    private int class_id;
    private int pagerNum;
    private String keyword;
    private EditText edt;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_notice_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        btn_choose_1 = (Button) findViewById(R.id.btn_choose_1);
        btn_choose_1.setOnClickListener(this);
        myPopView1 = new MyPopView(this);
        myPopView1.setData(new String[]{"全部"});
        myPopView1.setListener(new MyPopView.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                if (item.equals("全部")) {
                    item = "栏目";
                    class_id=0;
                }else {
                    class_id = classList.get(position-1).getClass_id();
                }
                btn_choose_1.setText(item);
                getNoticeList(0);
            }
        });
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.setEmptyView(R.layout.empty_view);
        edt = (EditText) findViewById(R.id.edt);
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    keyword = "";
                    getNoticeList(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    CommonUtils.hideSoftInput(bContext, edt);
                    keyword = edt.getText().toString();
                    getNoticeList(0);
                    return true;
                }
                return false;
            }
        });
        getNoticeClass();
        onRefresh();
    }

    private void getNoticeList(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.NOTICE_LIST);
        if (class_id != 0) {
            request.add("class_id", class_id);
        }
        if (!TextUtils.isEmpty(keyword)) {
            request.add("keyword", keyword);
        }
        request.add("pageno", num);
        CallServer.getRequestInstance().add(this, num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    NoticeList noticeList = netBaseBean.parseObject(NoticeList.class);
                    pagerNum = noticeList.getPageno();
                    List<Notice> list = noticeList.getList();
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                } else {
                    Toast(netBaseBean.getMessage());
                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl);
    }

    private void getNoticeClass() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.NOTICE_CLASS_LIST);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    classList = netBaseBean.parseList(Notice.class);
                    List<String> classNames = new ArrayList<String>();
                    classNames.add("全部");
                    for (Notice notice : classList) {
                        classNames.add(notice.getNotice_class_name());
                    }
                    myPopView1.setData(classNames);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, false);
    }

    private BaseQuickAdapter<Notice, BaseViewHolder> adapter = new BaseQuickAdapter<Notice, BaseViewHolder>(R.layout.item_notice) {
        @Override
        protected void convert(BaseViewHolder helper, final Notice item) {
            helper.setText(R.id.tv1, item.getNotice_title());
            helper.setText(R.id.tv2, Html.fromHtml(item.getNotice_content()));
            try {
                helper.setText(R.id.tv3, DateUtils.tenLongToString(item.getSend_time(), DateUtils.DB_DATA_FORMAT2));
            } catch (Exception e) {
                e.printStackTrace();
            }
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewActivity.goTo(bContext, Constants.WEB_NOTICE_DETAIL + "?id=" + item.getId() + "&user_id=" + UserManager.getId()+ "&token=" + UserManager.getToken(), "公告详情");
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
            default:
        }
    }

    @Override
    public void onRefresh() {
        getNoticeList(0);
    }

    @Override
    public void onLoadMoreRequested() {
        getNoticeList(pagerNum);
    }
}
