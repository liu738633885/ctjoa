package com.ctj.oa.enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.WebViewActivity;
import com.ctj.oa.fragment.BaseFragment;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.CircleClass;
import com.ctj.oa.model.work.company.Artice;
import com.ctj.oa.model.work.company.ArticeList;
import com.ctj.oa.model.work.company.Banner;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.MyViewUtils;
import com.ctj.oa.utils.manager.UserManager;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.IconHintView;
import com.lewis.widgets.LewisSwipeRefreshLayout;

import java.util.List;

/**
 * Created by lewis on 2017/4/24.
 */

public class EnterpriseFragment extends BaseFragment implements LewisSwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener, View.OnClickListener {
    private LewisSwipeRefreshLayout swl;
    private RecyclerView rv;
    private View headView;
    private RollPagerView rollPagerView, rollPagerView2;
    private ViewPagerAdapter2 pagerAdapter2;
    private ViewPagerAdapter pagerAdapter;
    private ImageButton btn_add;
    private int pagerNum;
    private int type = 1;
    private int class_id = -1;
    private RadioButton btn1, btn2;
    private CheckBox btn3;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_enterprise;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        swl = (LewisSwipeRefreshLayout) view.findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.headview_enterprise, (ViewGroup) rv.getParent(), false);
        btn1 = (RadioButton) headView.findViewById(R.id.btn1);
        btn2 = (RadioButton) headView.findViewById(R.id.btn2);
        btn3 = (CheckBox) headView.findViewById(R.id.btn3);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn_add = (ImageButton) headView.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddPostActivity.class));
            }
        });
        rollPagerView = (RollPagerView) headView.findViewById(R.id.roll_view_pager);
        rollPagerView2 = (RollPagerView) headView.findViewById(R.id.roll_view_pager2);
        rv.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, rv);
        adapter.addHeaderView(headView);
        pagerAdapter = new ViewPagerAdapter(getActivity(), rollPagerView);
        rollPagerView.setAdapter(pagerAdapter);
        rollPagerView.setHintView(new IconHintView(getActivity(), R.drawable.shape_viewpager_point_focus, R.drawable.shape_viewpager_point_normal, 0));
        pagerAdapter2 = new ViewPagerAdapter2(getActivity(), rollPagerView2);
        rollPagerView2.setAdapter(pagerAdapter2);
        rollPagerView2.setHintView(new IconHintView(getActivity(), R.drawable.shape_viewpager_point_focus, R.drawable.shape_viewpager_point_normal_gray, 0));
        pagerAdapter2.setListener(new ViewPagerAdapter2.OnClickViewPagerListener() {
            @Override
            public void onClick(CircleClass cirle, int position) {
                class_id = cirle.getClass_id();
                getArticeList(0);
                btn3.setChecked(false);
            }
        });
        adapter.setEmptyView(R.layout.empty_view_wrap);
        adapter.setHeaderAndEmpty(true);
        onRefresh();
    }

    private void company_banner() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.COMPANY_BANNER);
        CallServer.getRequestInstance().add(getActivity(), 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Banner> list_class = netBaseBean.parseList(Banner.class);
                    pagerAdapter.updata(list_class);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, false, "");
    }

    private void getCircleClassList() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_CIRCLE_CLASS_LIST);
        CallServer.getRequestInstance().add(getActivity(), 0x02, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<CircleClass> list_class = netBaseBean.parseList(CircleClass.class);
                    pagerAdapter2.updata(list_class);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, false, "");
    }

    private void getArticeList(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_ARTICLE_LIST);
        request.add("type", type);
        request.add("pageno", num);
        if (class_id > 0) {
            request.add("class_id", class_id);
        }
        CallServer.getRequestInstance().add(getActivity(), num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    ArticeList articeList = netBaseBean.parseObject(ArticeList.class);
                    pagerNum = articeList.getPageno();
                    List<Artice> list = articeList.getList();
                    MyViewUtils.bindListWithNum(pagerNum, what, adapter, list);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, swl, "");
    }


    /* private List<MineView> makeDate() {
         List<MineView> dates = new ArrayList<MineView>();
         dates.add(new MineView(R.mipmap.work_icon_gongshangzhuce, "工商注册", BusinessRegisterActivity.class));
         dates.add(new MineView(R.mipmap.work_icon_fawucaishui, "法务财税"));
         dates.add(new MineView(R.mipmap.work_icon_rencaizhaopin, "人才招聘"));
         dates.add(new MineView(R.mipmap.work_icon_chanpingsheji, "产品设计"));
         dates.add(new MineView(R.mipmap.work_icon_jihua, "计划"));
         dates.add(new MineView(R.mipmap.work_icon_beiwanglu, "备忘录", MemoActivity.class));
         dates.add(new MineView(R.mipmap.work_icon_rizhi, "日志", JournalHomeActivity.class));
         dates.add(new MineView(R.mipmap.work_icon_wenancehua, "文案策划"));
         dates.add(new MineView(R.mipmap.work_icon_pingpaituiguang, "品牌推广"));
         dates.add(new MineView(R.mipmap.work_icon_guquanrongzi, "股权融资"));
         dates.add(new MineView(R.mipmap.work_icon_binggoushangshi, "并购上市"));
         dates.add(new MineView(R.mipmap.work_icon_qiyepeixun, "企业陪训"));
         dates.add(new MineView(R.mipmap.work_icon_guanlizixun, "管理咨询"));
         dates.add(new MineView(R.mipmap.work_icon_shenqingjiaru, "申请加入"));
         return dates;
     }*/
    private BaseQuickAdapter<Artice, BaseViewHolder> adapter = new BaseQuickAdapter<Artice, BaseViewHolder>(R.layout.item_enterprise) {
        @Override
        protected void convert(BaseViewHolder helper, final Artice item) {
            helper.setText(R.id.tv1, item.getArticle_title());
            helper.setText(R.id.tv2, item.getArticle_content());
            helper.setText(R.id.tv3, item.getCompany_name());
            helper.setText(R.id.tv4, item.getComment_num() + "");
            ImageView imv1 = helper.getView(R.id.imv1);
            ImageView imv2 = helper.getView(R.id.imv2);
            ImageLoader.load(getActivity(), item.getArticle_original_img(), imv1);
            ImageLoader.loadHeadImage(getActivity(), item.getCompany_logo(), imv2, -1);
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewActivity.goTo(getActivity(), Constants.WEB_ARTICLE_DETAIL + "?article_id=" + item.getArticle_id() + "&user_id=" + UserManager.getId() + "&token=" + UserManager.getToken(), "帖子详情");
                }
            });
        }
    };

    @Override
    public void onRefresh() {
        getCircleClassList();
        company_banner();
        getArticeList(0);
    }

    @Override
    public void onLoadMoreRequested() {
        getArticeList(pagerNum);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                type = 1;
                getArticeList(0);
                break;
            case R.id.btn2:
                type = 2;
                getArticeList(0);
                break;
            case R.id.btn3:
                class_id = -1;
                getArticeList(0);
                btn3.setChecked(true);
                break;

        }
    }
}
