package com.ctj.oa.work.log;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.widgets.dialog.BottomDialog;

import java.util.ArrayList;
import java.util.List;

public class AddLogTemplateActivity extends BaseActivity {
    private RecyclerView rv;
    private BottomDialog bottomDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_log_template;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        adapter.setNewData(makeData());
        bottomDialog = new BottomDialog(this);

    }

    private BaseQuickAdapter<Object, BaseViewHolder> adapter = new BaseQuickAdapter<Object, BaseViewHolder>(new ArrayList<Object>()) {
        @Override
        protected void convert(final BaseViewHolder helper, Object item) {
            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (helper.getItemViewType() == -1) {
                        int index = getData().size() - 2;
                        if (index < 0) {
                            index = 0;
                        }
                        getData().add(index, 0);
                        notifyDataSetChanged();
                    } else {
                        showTemplateDialog();
                    }
                }
            });

        }

        @Override
        protected int getDefItemViewType(int position) {
            if (position == getData().size() - 1) {
                return -1;
            } else {
                return 0;
            }
        }

        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
            if (viewType == -1) {
                return new BaseViewHolder(this.getItemView(R.layout.item_log_template_add, parent));
            } else {
                return new BaseViewHolder(this.getItemView(R.layout.item_log_template, parent));
            }
        }
    };

    private void showTemplateDialog() {
        bottomDialog.setContentView(R.layout.dialog_edit_log_template, true);
        bottomDialog.show();

    }

    private List<Object> makeData() {
        List<Object> list = new ArrayList<>();
        list.add(-1);
        return list;
    }
}
