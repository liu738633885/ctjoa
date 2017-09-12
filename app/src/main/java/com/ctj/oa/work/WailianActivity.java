package com.ctj.oa.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.activity.WebViewActivity;
import com.ctj.oa.enterprise.JoinEntranceActivity;
import com.ctj.oa.enterprise.business.BusinessRegisterActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.CircleClass;
import com.ctj.oa.model.work.MineView;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.ctj.oa.utils.manager.UserManager;
import com.lewis.utils.T;
import com.lewis.widgets.LewisSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class WailianActivity extends BaseActivity implements LewisSwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rv;
    private LewisSwipeRefreshLayout swl;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_wailian;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        swl = (LewisSwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(adapter);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        getCircleClassList();
    }

    private void getCircleClassList() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_CIRCLE_CLASS_LIST);
        CallServer.getRequestInstance().add(bContext, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<CircleClass> list_class = netBaseBean.parseList(CircleClass.class);
                    makeDate(list_class);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }

    private void makeDate(List<CircleClass> list_class) {
        List<Object> dates = new ArrayList<>();
        if (list_class != null && list_class.size() > 0) {
            dates.addAll(list_class);
            if (UserManager.isAdmin()) {
                dates.add(new MineView(R.mipmap.work_icon_shenqingjiaru, "申请加入", JoinEntranceActivity.class));
            }
            int b = (dates.size()) % 4;

            if (b != 0) {
                for (int i = 0; i < 4 - b; i++) {
                    dates.add(new MineView(0, ""));//空白
                }
            }
        }

        adapter.setNewData(dates);
    }

    BaseQuickAdapter adapter = new BaseQuickAdapter<Object, BaseViewHolder>(new ArrayList<>()) {

        @Override
        protected void convert(final BaseViewHolder helper, final Object item) {
            switch (helper.getItemViewType()) {
                case 1:
                    final MineView mineView = (MineView) item;
                    ((ImageView) helper.getView(R.id.imv)).setImageResource(mineView.getId());
                    helper.setText(R.id.tv, mineView.getTittle());
                    helper.getConvertView().setEnabled(!TextUtils.isEmpty(mineView.getTittle()));

                    helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mineView.getActivity() != null) {
                                mContext.startActivity(new Intent(mContext, mineView.getActivity()));
                            } else if (!TextUtils.isEmpty(mineView.getInfo()) && mineView.getInfo().startsWith("http")) {
                                WebViewActivity.goTo(bContext, mineView.getInfo(), "文件列表");
                            } else {
                                T.showShort(bContext, "功能暂未开放");
                            }
                        }
                    });

                    break;
                case 2:
                    final CircleClass aClass = (CircleClass) item;
                    ImageLoader.load(mContext, aClass.getClass_icon(), (ImageView) helper.getView(R.id.imv));
                    helper.setText(R.id.tv, aClass.getClass_name());
                    helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BusinessRegisterActivity.goTo(bContext, aClass.getClass_id(), aClass.getClass_name());
                        }
                    });
                    break;
                case 3:
                    break;
                default:
                    break;
            }

        }

        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
               /* case 0:
                    return new FullViewHolder(this.getItemView(R.layout.item_work_head, parent));*/
                case 1:
                    return new GridViewHolder(this.getItemView(R.layout.item_work_application, parent));
                case 2:
                    return new GridViewHolder(this.getItemView(R.layout.item_work_application, parent));
                case 3:
                    return new FullViewHolder(this.getItemView(R.layout.item_work_wailianfuwu, parent));
                default:
                    return null;
            }
        }

        @Override
        protected int getDefItemViewType(int position) {
            if (getItem(position) instanceof MineView) {
                return 1;
            }
            if (getItem(position) instanceof CircleClass) {
                return 2;
            }
            if (getItem(position) instanceof Integer) {
                return (int) getItem(position);
            }
            return 1;
        }

        class FullViewHolder extends BaseViewHolder {
            FullViewHolder(View view) {
                super(view);
                setFullSpan(this);
            }
        }

        class GridViewHolder extends BaseViewHolder {
            GridViewHolder(View view) {
                super(view);
            }
        }
    };


}
