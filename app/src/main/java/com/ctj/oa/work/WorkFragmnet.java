package com.ctj.oa.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.enterprise.JoinEntranceActivity;
import com.ctj.oa.enterprise.business.BusinessRegisterActivity;
import com.ctj.oa.fragment.BaseFragment;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.CircleClass;
import com.ctj.oa.model.work.MineView;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.ctj.oa.utils.manager.UserManager;
import com.ctj.oa.work.approval.ApprovalHomeActivity;
import com.ctj.oa.work.approval.ApprovalListActivity;
import com.ctj.oa.work.log.LogHomeActivity;
import com.ctj.oa.work.log.LogListActivity;
import com.ctj.oa.work.memo.MemoActivity;
import com.ctj.oa.work.notice.NoticeListActivity;
import com.ctj.oa.work.sign.SignActivity;
import com.ctj.oa.work.task.TaskHomeActivity;
import com.lewis.utils.T;
import com.lewis.widgets.LewisSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/4/24.
 */

public class WorkFragmnet extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rv;
    private List<Object> dates;
    private LewisSwipeRefreshLayout swl;
    //private String banner;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_work;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        swl = (LewisSwipeRefreshLayout) view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(adapter);
        makeDate(null);
        onRefresh();

    }

    /*private void company_banner() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.COMPANY_BANNER);
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Banner> list_class = netBaseBean.parseList(Banner.class);
                    banner = list_class.get(0).getBanner_image();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, false, "");
    }
*/

    private void getCircleClassList() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_CIRCLE_CLASS_LIST);
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
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

    BaseQuickAdapter adapter = new BaseQuickAdapter<Object, BaseViewHolder>(new ArrayList<>()) {

        @Override
        protected void convert(final BaseViewHolder helper, final Object item) {
            switch (helper.getItemViewType()) {
                case 0:
                    helper.setText(R.id.tv1, UserManager.getCompanyName());
                    helper.setText(R.id.tv2, UserManager.getPostName());
                    TextView tv3 = helper.getView(R.id.tv3);
                    TextView tv4 = helper.getView(R.id.tv4);
                    TextView tv5 = helper.getView(R.id.tv5);
                    TextView tv6 = helper.getView(R.id.tv6);
                    ((View) tv3.getParent()).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), ApprovalListActivity.class));
                        }
                    });
                    ((View) tv4.getParent()).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LogListActivity.goTo(getActivity(), 2);
                        }
                    });
                    ((View) tv5.getParent()).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    ((View) tv6.getParent()).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    //if (!TextUtils.isEmpty(banner)) {
                    ImageLoader.loadFromMipmap(getActivity(), R.mipmap.work_head, (ImageView) helper.getView(R.id.imv0));
                    //}
                    break;
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
                                } else {
                                    T.showShort(getActivity(), "暂未开放");
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
                            BusinessRegisterActivity.goTo(getActivity(), aClass.getClass_id(), aClass.getClass_name());
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
                case 0:
                    return new FullViewHolder(this.getItemView(R.layout.item_work_head, parent));
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
            return position;
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

    private void makeDate(List<CircleClass> list) {
        dates = new ArrayList<>();
        dates.add(0);//头部
        dates.add(new MineView(R.mipmap.work_icon_qiandao, "签到", SignActivity.class));
        dates.add(new MineView(R.mipmap.work_icon_shenpi, "审批", ApprovalHomeActivity.class));
        dates.add(new MineView(R.mipmap.work_icon_beiwanglu, "备忘录", MemoActivity.class));
        dates.add(new MineView(R.mipmap.work_icon_rizhi, "日志", LogHomeActivity.class));
        dates.add(new MineView(R.mipmap.work_icon_gonggao, "公告", NoticeListActivity.class));
        dates.add(new MineView(R.mipmap.work_icon_rencaizhaopin, "任务", TaskHomeActivity.class));
        //dates.add(new MineView(R.drawable.ic_add2, "添加"));//加号
        dates.add(new MineView(R.mipmap.work_icon_kehu, "客户"));
        dates.add(new MineView(R.mipmap.work_icon_xiaoshou, "销售"));
        //dates.add(new MineView(0, ""));//空白
        //dates.add(new MineView(0, ""));//空白
        //dates.add(new MineView(0, ""));//空白
        dates.add(3);//文本:"外联服务"
        //dates.add(new MineView(R.mipmap.work_icon_rizhi, " 文档签名", SealActivity2.class));
        if (list != null && list.size() > 0) {
            dates.addAll(list);
            dates.add(new MineView(R.mipmap.work_icon_shenqingjiaru, "申请加入", JoinEntranceActivity.class));
            int b = (list.size() + 1) % 4;

            if (b != 0) {
                for (int i = 0; i < 4 - b; i++) {
                    dates.add(new MineView(0, ""));//空白
                }
            }
        }

        adapter.setNewData(dates);
       /*
        dates.add(new MineView(R.mipmap.work_icon_jihua, "计划"));
        dates.add(new MineView(R.mipmap.work_icon_gongshangzhuce, "工商注册", BusinessRegisterActivity.class));
        dates.add(new MineView(R.mipmap.work_icon_fawucaishui, "法务财税"));
        dates.add(new MineView(R.mipmap.work_icon_rencaizhaopin, "人才招聘"));
        dates.add(new MineView(R.mipmap.work_icon_chanpingsheji, "产品设计"));
        dates.add(new MineView(R.mipmap.work_icon_wenancehua, "文案策划"));
        dates.add(new MineView(R.mipmap.work_icon_pingpaituiguang, "品牌推广"));
        dates.add(new MineView(R.mipmap.work_icon_guquanrongzi, "股权融资"));
        dates.add(new MineView(R.mipmap.work_icon_binggoushangshi, "并购上市"));
        dates.add(new MineView(R.mipmap.work_icon_qiyepeixun, "企业陪训"));
        dates.add(new MineView(R.mipmap.work_icon_guanlizixun, "管理咨询", SealActivity2.class));
        dates.add(new MineView(R.mipmap.work_icon_shenqingjiaru, "申请加入", JoinEntranceActivity.class));
        */
    }

    @Override
    public void onRefresh() {
        getCircleClassList();
        //company_banner();
    }
}
