package com.ctj.oa.chat.organization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.mine.UserProfileActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.manager.UserManager;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.ctj.oa.widgets.TitleBar;
import com.lewis.utils.T;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/4/25.
 */

public class OrganizationActivity extends BaseActivity {
    private RecyclerView rv;
    private TreeRecyclerAdapter mAdapter;
    private TitleBar titleBar;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_organization;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.isAdmin()) {
                    startActivity(new Intent(OrganizationActivity.this, OrganizationManageActivity.class));
                } else {
                    Toast("你不是当前公司管理者");
                }
            }
        });
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SimpleTreeRecyclerAdapter(rv, this,
                new ArrayList<Node>(), 1, R.drawable.ic_down2_blue, R.drawable.ic_right2);
        rv.setAdapter(mAdapter);
        getCompanyDept();
    }

    private void getCompanyDept() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_COMPANY_DEPT_USER);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Node> list = netBaseBean.parseList(Node.class);
                   /* list.add(new Node(50,49,"刘冠志",2));
                    list.add(new Node(41,50,"潘泓翰",2));
                    list.add(new Node(49,49,"黄龙",2));*/
                    mAdapter = new SimpleTreeRecyclerAdapter(rv, OrganizationActivity.this,
                            list, 1, R.drawable.ic_down2_blue, R.drawable.ic_right2);
                    Logger.d("个数" + list.size());
                    rv.setAdapter(mAdapter);

                } else {
                    T.showShort(OrganizationActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    public class SimpleTreeRecyclerAdapter extends TreeRecyclerAdapter {

        public SimpleTreeRecyclerAdapter(RecyclerView mTree, Context context, List<Node> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
            super(mTree, context, datas, defaultExpandLevel, iconExpand, iconNoExpand);
        }

        public SimpleTreeRecyclerAdapter(RecyclerView mTree, Context context, List<Node> datas, int defaultExpandLevel) {
            super(mTree, context, datas, defaultExpandLevel);
        }

        @Override
        public int getItemViewType(int position) {
            return mNodes.get(position).getType();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 2) {
                return new MemberHoder(LayoutInflater.from(mContext).inflate(R.layout.item_organization_member, parent, false));
            } else {
                return new GroupHoder(LayoutInflater.from(mContext).inflate(R.layout.item_organization_group, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(final Node node, RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 2) {
                final MemberHoder memberHoder = (MemberHoder) holder;
                memberHoder.tv1.setText(node.getNickname());
                memberHoder.tv2.setText(node.getDept_name());
                memberHoder.tv3.setText(node.getSignature());
                ImageLoader.loadHeadImage(bContext, node.getPortrait(), memberHoder.imv, 2);
                ((View) memberHoder.tv1.getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserProfileActivity.goTo(bContext, node.getDept_id() + "");
                    }
                });
            } else {
                final GroupHoder groupHoder = (GroupHoder) holder;
                if (node.getIcon() == -1) {
                    groupHoder.imv.setVisibility(View.INVISIBLE);
                } else {
                    groupHoder.imv.setVisibility(View.VISIBLE);
                    groupHoder.imv.setImageResource(node.getIcon());
                }
                groupHoder.tv1.setText(node.getDept_name());
            }


        }

        class GroupHoder extends RecyclerView.ViewHolder {
            public TextView tv1;
            public TextView tv2;

            public ImageView imv;

            public GroupHoder(View itemView) {
                super(itemView);

                tv1 = (TextView) itemView
                        .findViewById(R.id.tv1);
                tv2 = (TextView) itemView
                        .findViewById(R.id.tv2);
                imv = (ImageView) itemView.findViewById(R.id.imv);

            }

        }

        class MemberHoder extends RecyclerView.ViewHolder {
            public TextView tv1;
            public TextView tv2;
            public TextView tv3;

            public ImageView imv;

            public MemberHoder(View itemView) {
                super(itemView);

                tv1 = (TextView) itemView
                        .findViewById(R.id.tv1);
                tv2 = (TextView) itemView
                        .findViewById(R.id.tv2);
                tv3 = (TextView) itemView
                        .findViewById(R.id.tv3);
                imv = (ImageView) itemView.findViewById(R.id.imv);

            }

        }
    }


}
