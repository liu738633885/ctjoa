package com.ctj.oa.enterprise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctj.oa.R;
import com.ctj.oa.model.work.CircleClass;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 16/7/1.
 */
public class ViewPagerAdapter2 extends LoopPagerAdapter {
    private List<CircleClass> data;
    private Context mContext;

    public void updata(List<CircleClass> list) {
        if (list != null) {
            data = list;
            notifyDataSetChanged();
        }
    }

    public ViewPagerAdapter2(Context context, RollPagerView viewPager) {
        super(viewPager);
        mContext = context;
        data = new ArrayList<CircleClass>();
        mContext = context;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        LinearLayout view = new LinearLayout(container.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < 4; i++) {
            final int x = (position) * 4 + i;
            if (x <= (data.size() - 1)) {
                View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_enterprise_application, view, false);
                ImageLoader.load(mContext, data.get(x).getClass_icon(), (ImageView) itemView.findViewById(R.id.imv));
                ((TextView) itemView.findViewById(R.id.tv)).setText(data.get(x).getClass_name());
                view.addView(itemView);
                if (listener != null) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //T.showShort(mContext, data.get(ic_new).getClass_name());
                            listener.onClick(data.get(x), x);
                        }
                    });
                }
            }
        }

        return view;
    }

    @Override
    public int getRealCount() {
        int x = (int) Math.ceil((double) data.size() / (double) 4);
        return x;
    }

    private OnClickViewPagerListener listener;

    public void setListener(OnClickViewPagerListener listener) {
        this.listener = listener;
    }

    public interface OnClickViewPagerListener {
        void onClick(CircleClass circle, int position);
    }
}
