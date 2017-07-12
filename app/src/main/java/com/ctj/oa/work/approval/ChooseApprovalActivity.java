package com.ctj.oa.work.approval;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.approve.Approve;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.lewis.utils.SearchBarUtils;
import com.lewis.utils.T;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class ChooseApprovalActivity extends BaseActivity {
    private RecyclerView rv;
    private EditText searchView;
    private MyAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_approval;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        adapter = new MyAdapter();
        searchView = SearchBarUtils.init(this);
        if (searchView != null) {
            searchView.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //过滤操作
                    adapter.getFilter().filter(s);
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void afterTextChanged(Editable s) {
                }
            });
        }
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        getApproveTemplate();
    }

    private void getApproveTemplate() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_APPROVE_TEMPLATE);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Approve> list = netBaseBean.parseList(Approve.class);
                    adapter.setNewData(list);

                } else {
                    T.showShort(ChooseApprovalActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    class MyAdapter extends BaseQuickAdapter<Approve, BaseViewHolder> implements Filterable {
        List<Approve> mCopyInviteMessages;


        public MyAdapter() {
            super(new ArrayList<Approve>());
        }

        @Override
        protected void convert(BaseViewHolder helper, final Approve item) {
            TextView tv_title = helper.getView(R.id.tv_title);
            if (tv_title != null) {
                tv_title.setText(item.getClass_name());
            }
            TextView tv = helper.getView(R.id.tv);
            tv.setText(item.getApprove_title().substring(0, 1));
            GradientDrawable myGrad = (GradientDrawable) tv.getBackground().mutate();
            myGrad.setColor(Color.parseColor(item.getBg_color()));
            helper.setText(R.id.tv2, item.getApprove_title());
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddApprovalActivity.goTo(ChooseApprovalActivity.this, item.getId());
                }
            });
        }

        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new BaseViewHolder(this.getItemView(R.layout.item_approva_class_choose, parent));
                case 1:
                    return new BaseViewHolder(this.getItemView(R.layout.item_approva_class_choose_title, parent));
                default:
                    return null;
            }
        }

        @Override
        protected int getDefItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            if (getItem(position).getClass_name().equals(getItem(position - 1).getClass_name())) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public void setNewData(List<Approve> list) {
            super.setNewData(list);
            mCopyInviteMessages = new ArrayList<>();
            this.mCopyInviteMessages.addAll(list);
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    Logger.e("哈哈哈哈performFiltering");
                    //初始化过滤结果对象
                    FilterResults results = new FilterResults();
                    //假如搜索为空的时候，将复制的数据添加到原始数据，用于继续过滤操作
                    if (results.values == null) {
                        mData.clear();
                        mData.addAll(mCopyInviteMessages);
                    }
                    //关键字为空的时候，搜索结果为复制的结果
                    if (TextUtils.isEmpty(constraint)) {
                        results.values = mCopyInviteMessages;
                        results.count = mCopyInviteMessages.size();
                    } else {
                        String prefixString = constraint.toString();
                        final int count = getData().size();
                        //用于存放暂时的过滤结果
                        final ArrayList<Approve> newValues = new ArrayList<Approve>();
                        for (int i = 0; i < count; i++) {
                            // First match against the whole ,non-splitted value，假如含有关键字的时候，添加
                            String x1 = getData().get(i).getApprove_title();
                            String x2 = getData().get(i).getClass_name();
                            if (x1.contains(prefixString) || x2.contains(prefixString)) {
                                newValues.add(getData().get(i));
                            }
                        }
                        results.values = newValues;
                        results.count = newValues.size();
                    }
                    return results;//过滤结果
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    Logger.e("哈哈哈哈publishResults");
                    mData.clear();
                    mData.addAll((List<Approve>) results.values);
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        //关键字不为零但是过滤结果为空刷新数据
                        if (constraint.length() != 0) {
                            adapter.notifyDataSetChanged();
                            return;
                        }
                        //加载复制的数据，即为最初的数据
                        adapter.setNewData(mCopyInviteMessages);
                    }
                }
            };
        }
    }


    /*private BaseQuickAdapter<Approve, BaseViewHolder> adapter = new BaseQuickAdapter<Approve, BaseViewHolder>(new ArrayList<Approve>()) {

    };
*/
}
