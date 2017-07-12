package com.ctj.oa.work;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.chat.organization.Node;
import com.ctj.oa.chat.organization.TreeRecyclerAdapter;
import com.ctj.oa.model.UserInfo;
import com.ctj.oa.model.event.EventRefresh;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.ctj.oa.widgets.TitleBar;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.ctj.oa.R.id.imv;

public class ChooseUserActivity extends BaseActivity {
    private RecyclerView rv, rv0;
    private TreeRecyclerAdapter mAdapter;
    private TitleBar titleBar;
    private int mode = 1;
    private List<UserInfo> users = new ArrayList<UserInfo>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_user;
    }

    //单个
    public static void goTo(Context context, String where) {
        Intent intent = new Intent(context, ChooseUserActivity.class);
        intent.putExtra("mode", 1);
        intent.putExtra("where", where);
        context.startActivity(intent);
    }

    //多个
    public static void goTo(Context context, List<UserInfo> list, String where) {
        Intent intent = new Intent(context, ChooseUserActivity.class);
        intent.putExtra("mode", 2);
        intent.putExtra("where", where);
        intent.putExtra("users", (Serializable) list);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        mode = intent.getIntExtra("mode", 0);
        where = intent.getStringExtra("where");
        users = (List<UserInfo>) intent.getSerializableExtra("users");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        if (mode == 1) {
            titleBar.getRight_tv().setVisibility(View.INVISIBLE);
        } else {
            titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventRefresh(users, where));
                    finish();
                }
            });
        }
        rv = (RecyclerView) findViewById(R.id.rv);
        rv0 = (RecyclerView) findViewById(R.id.rv0);
        rv.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv0.setLayoutManager(linearLayoutManager);
        mAdapter = new SimpleTreeRecyclerAdapter(rv, this,
                new ArrayList<Node>(), 1, R.drawable.ic_down2_blue, R.drawable.ic_right2);
        rv.setAdapter(mAdapter);
        rv0.setAdapter(userAdapter);
        if (users != null && users.size() > 0) {

        } else {
            users = new ArrayList<UserInfo>();
        }
        getCompanyDept();
    }

    private void getCompanyDept() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_COMPANY_DEPT_USER);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Node> list = netBaseBean.parseList(Node.class);
                    if (mode == 2) {
                        for (Node node : list) {
                            if (node.getType() == 2) {
                                for (UserInfo info : users) {
                                    if (info.getId() == node.getDept_id()) {
                                        node.setClick(true);
                                        info.setNickname(node.getNickname());
                                        info.setPortrait(node.getPortrait());
                                    }
                                }
                            }
                        }
                        userAdapter.setNewData(users);

                    }
                   /* list.add(new Node(50, 49, "刘冠志", 2));
                    list.add(new Node(41, 50, "潘泓翰", 2));
                    list.add(new Node(49, 49, "黄龙", 2));*/
                    mAdapter = new SimpleTreeRecyclerAdapter(rv, bContext,
                            list, 1, R.drawable.ic_down2_blue, R.drawable.ic_right2);
                    Logger.d("个数" + list.size());
                    rv.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
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
                if (mode == 1) {
                    return new MemberHoder(LayoutInflater.from(mContext).inflate(R.layout.item_organization_member, parent, false));
                } else {
                    return new MemberHoder(LayoutInflater.from(mContext).inflate(R.layout.item_organization_member_check, parent, false));
                }
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
                memberHoder.imv.setImageResource(R.mipmap.default_user_icon);
                ImageLoader.loadHeadImage(bContext, node.getPortrait(), memberHoder.imv, 2);
                if (memberHoder.cb != null) {
                    memberHoder.cb.setOnCheckedChangeListener(null);
                    memberHoder.cb.setChecked(node.isClick());
                    memberHoder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            node.setClick(isChecked);
                            UserInfo userInfo = new UserInfo();
                            userInfo.setId(node.getDept_id());
                            userInfo.setNickname(node.getNickname());
                            userInfo.setPortrait(node.getPortrait());
                            for (UserInfo user : users) {
                                if (user.getId() == userInfo.getId()) {
                                    users.remove(user);
                                    break;
                                }
                            }
                            if (isChecked) {
                                users.add(userInfo);
                            } else {

                            }


                            userAdapter.setNewData(users);
                            titleBar.getRight_tv().setText("提交(" + users.size() + ")");
                        }
                    });
                }
                ((View) memberHoder.tv1.getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mode == 1) {
                            UserInfo userInfo = new UserInfo();
                            userInfo.setId(node.getDept_id());
                            userInfo.setNickname(node.getNickname());
                            userInfo.setPortrait(node.getPortrait());

                            EventBus.getDefault().post(new EventRefresh(userInfo, where));
                            finish();
                        }
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
            public CheckBox cb;

            public ImageView imv;

            public MemberHoder(View itemView) {
                super(itemView);

                tv1 = (TextView) itemView
                        .findViewById(R.id.tv1);
                tv2 = (TextView) itemView
                        .findViewById(R.id.tv2);
                tv3 = (TextView) itemView
                        .findViewById(R.id.tv3);
                try {
                    cb = (CheckBox) itemView
                            .findViewById(R.id.cb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imv = (ImageView) itemView.findViewById(R.id.imv);

            }

        }
    }

    private BaseQuickAdapter<UserInfo, BaseViewHolder> userAdapter = new BaseQuickAdapter<UserInfo, BaseViewHolder>(R.layout.item_user) {
        @Override
        protected void convert(BaseViewHolder helper, UserInfo item) {
            helper.setText(R.id.tv, item.getNickname());
            if (TextUtils.isEmpty(item.getPortrait())) {
                helper.setVisible(imv, false);
                helper.setVisible(R.id.tv0, true);
                try {
                    helper.setText(R.id.tv0, item.getNickname().substring(0, 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ImageLoader.loadHeadImage(bContext, item.getPortrait(), ((ImageView) helper.getView(imv)), 2);
                helper.setVisible(imv, true);
                helper.setVisible(R.id.tv0, false);
            }
        }

    };
}
