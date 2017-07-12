package com.ctj.oa.work.memo;

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
import com.ctj.oa.model.work.memo.Memo;
import com.ctj.oa.model.work.memo.MemoList;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.MyViewUtils;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.lewis.utils.DateUtils;
import com.lewis.widgets.LewisSwipeRefreshLayout;

import java.util.List;


/**
 * Created by lewis on 2017/5/2.
 */

public class MemoListFragment extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private RecyclerView rv;
    private LewisSwipeRefreshLayout swl;
    private int type;
    private String q;
    private int pagerNum;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_memo;
    }

    public static MemoListFragment newInstance(int type) {
        MemoListFragment fragment = new MemoListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        type = getArguments().getInt("type");
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
        getMemoList(0);
    }

    @Override
    public void onLoadMoreRequested() {
        getMemoList(pagerNum);
    }

    private void getMemoList(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_MEMO_LIST);
        if (!TextUtils.isEmpty(q)) {
            request.add("q", q);
        }
        request.add("pageno", num);
        request.add("type", type);
        CallServer.getRequestInstance().add(getActivity(), num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    MemoList memoList = netBaseBean.parseObject(MemoList.class);
                    pagerNum = memoList.getPageno();
                    List<Memo> list = memoList.getList();
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    private BaseQuickAdapter<Memo, BaseViewHolder> adapter = new BaseQuickAdapter<Memo, BaseViewHolder>(R.layout.item_memo) {


        @Override
        protected void convert(BaseViewHolder helper, final Memo item) {
            helper.setText(R.id.tv_name, item.getNickname());
            helper.setText(R.id.tv_title, item.getContent());
            try {
                helper.setText(R.id.tv_time, DateUtils.tenLongToString(item.getAdd_time(), DateUtils.DB_DATA_FORMAT2));
            } catch (Exception e) {
                e.printStackTrace();
            }
            helper.setText(R.id.comment_num, item.getComment_count() + "");
            helper.setVisible(R.id.imv_notice, item.getIs_tips() == 1);
            ImageLoader.loadHeadImage(getActivity(), item.getPortrait(), (ImageView) helper.getView(R.id.imv), 2);
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MemoDetailActivity.goTo(getActivity(), item.getId());
                }
            });
        }
    };
}
