package com.ctj.oa.widgets;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.ctj.oa.R;
import com.lewis.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2017/5/3.
 */

public class MyPopView extends PopupWindow {
    private Context context;


    public MyPopView(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_item, null);
        this.setContentView(view);
        this.setWidth(DensityUtil.getWidth(context) / 2);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context, R.color.bg_gray));
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
    }

    public void setData(String[] strings) {
        List<String> list = new ArrayList<>();
        for (String str : strings) {
            list.add(str);
        }
        setData(list);
    }

    public void setData(final List<String> strings) {
        ((ViewGroup) getContentView().findViewById(R.id.ll)).removeAllViews();
        for (int i = 0; i < strings.size(); i++) {
            Button btn = new Button(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(DensityUtil.dip2px(context, 1), 0, DensityUtil.dip2px(context, 1), DensityUtil.dip2px(context, 1));
            btn.setLayoutParams(params);
            btn.setBackgroundResource(R.drawable.ripple_bg);
            btn.setText(strings.get(i));
            ((ViewGroup) getContentView().findViewById(R.id.ll)).addView(btn);
            final int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(strings.get(finalI), finalI);
                    }
                    dismiss();
                }
            });
        }
        update();
    }

    public void showPopupWindow(View parent) {
        if (this.isShowing()) {
            dismiss();
        } else {
            showAsDropDown(parent);
        }
    }

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        public void onItemClick(String item, int position);
    }
}
