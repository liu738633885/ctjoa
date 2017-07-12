package com.ctj.oa.chat.organization;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.widgets.TitleBar;
import com.ctj.oa.widgets.dialog.BottomDialog;
import com.ctj.oa.widgets.dialog.CenterDialog;
import com.lewis.utils.T;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class OrganizationManageActivity extends BaseActivity {
    private RecyclerView rv;
    private TreeRecyclerAdapter mAdapter;
    private BottomDialog bottomDialog;
    private CenterDialog centerDialog;
    private int rootId;
    private int showLevel = 1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_organization_manage;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        //initDatas();
        bottomDialog = new BottomDialog(this);
        centerDialog = new CenterDialog(this);
        findViewById(R.id.ll1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rootId != 0) {
                    showAddnewBumen(rootId, 1);
                }
            }
        });
        mAdapter = new SimpleTreeRecyclerAdapter2(rv, OrganizationManageActivity.this,
                new ArrayList<Node>(), 1, R.drawable.ic_down2_blue, R.drawable.ic_right2);
        mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
            @Override
            public void onClick(Node node, int position) {
                if (node.isLeaf()) {
                    showCenterDialog(node);
                }
            }
        });
        getCompanyDept();
    }

    private void getCompanyDept() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_COMPANY_DEPT);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<Node> list = netBaseBean.parseList(Node.class);
                    mAdapter = new SimpleTreeRecyclerAdapter2(rv, OrganizationManageActivity.this,
                            list, showLevel, R.drawable.ic_down2_blue, R.drawable.ic_right2);
                    Logger.d("个数" + list.size());
                    rv.setAdapter(mAdapter);
                    if (mAdapter.getAllNodes().size() > 0) {
                        rootId = mAdapter.getAllNodes().get(0).getDept_id();
                    }
                    Logger.d("rootId" + mAdapter.getAllNodes().get(0).getDept_id());
                    if (showLevel < 1) {
                        showLevel = 1;
                    }

                } else {
                    T.showShort(OrganizationManageActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void addCompanyDept(String dept_name, final int pid, final int level) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ADD_COMPANY_DEPT);
        request.add("dept_parent_id", pid);
        request.add("dept_name", dept_name);
        request.add("dept_desc", "");
        CallServer.getRequestInstance().add(this, 0x02, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(OrganizationManageActivity.this, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    showLevel = level;
                    getCompanyDept();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void delCompanyDept(final int level, int id) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.DEL_COMPANY_DEPT);
        request.add("dept_id", id);
        CallServer.getRequestInstance().add(this, 0x03, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(OrganizationManageActivity.this, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    showLevel = level;
                    getCompanyDept();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void showAddnewBumen(final int pid, final int level) {
        bottomDialog.setContentView(R.layout.dialog_edit_text, true);
        final EditText edt = (EditText) bottomDialog.findViewById(R.id.edt);
        TitleBar title_bar = (TitleBar) bottomDialog.findViewById(R.id.titleBar);
        title_bar.setCenterText("添加部门");
        title_bar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt.getText().length() > 0) {
                    addCompanyDept(edt.getText().toString(), pid, level);
                    T.showShort(OrganizationManageActivity.this, "创建成功");
                    bottomDialog.dismiss();
                } else {
                    T.showShort(OrganizationManageActivity.this, "请输入文字");
                }
            }
        });
        ((TextView) bottomDialog.findViewById(R.id.tv1)).setText("请输入你要创建的部门名称:");
        ((TextView) bottomDialog.findViewById(R.id.tv2)).setText("部门详情只能在网页添加");
        bottomDialog.show();
    }

    private void showCenterDialog(final Node node) {
        centerDialog.setContentView(R.layout.dialog_organization_menu);
        centerDialog.findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddnewBumen(node.getDept_id(), node.getLevel());
                centerDialog.dismiss();
            }
        });
        centerDialog.findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        centerDialog.findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delCompanyDept(node.getDept_parent_id(), node.getDept_id());
                centerDialog.dismiss();
            }
        });
        centerDialog.show();
    }

    class SimpleTreeRecyclerAdapter2 extends TreeRecyclerAdapter {

        public SimpleTreeRecyclerAdapter2(RecyclerView mTree, Context context, List<Node> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
            super(mTree, context, datas, defaultExpandLevel, iconExpand, iconNoExpand);
        }

        public SimpleTreeRecyclerAdapter2(RecyclerView mTree, Context context, List<Node> datas, int defaultExpandLevel) {
            super(mTree, context, datas, defaultExpandLevel);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GroupHoder(LayoutInflater.from(mContext).inflate(R.layout.item_organization_group2, parent, false));
        }

        @Override
        public void onBindViewHolder(final Node node, RecyclerView.ViewHolder holder, int position) {
            final GroupHoder groupHoder = (GroupHoder) holder;
            if (node.getIcon() == -1) {
                groupHoder.imv.setVisibility(View.INVISIBLE);
            } else {
                groupHoder.imv.setVisibility(View.VISIBLE);
                groupHoder.imv.setImageResource(node.getIcon());
            }
            groupHoder.tv1.setText(node.getDept_name());
            groupHoder.imv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCenterDialog(node);
                }
            });
        }

        class GroupHoder extends RecyclerView.ViewHolder {
            public TextView tv1;

            public ImageView imv, imv2;

            public GroupHoder(View itemView) {
                super(itemView);

                tv1 = (TextView) itemView
                        .findViewById(R.id.tv1);
                imv = (ImageView) itemView.findViewById(R.id.imv);
                imv2 = (ImageView) itemView.findViewById(R.id.imv2);

            }

        }
    }

}
