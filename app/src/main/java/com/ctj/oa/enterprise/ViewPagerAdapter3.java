package com.ctj.oa.enterprise;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ctj.oa.model.work.company.Banner2;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 16/7/1.
 */
public class ViewPagerAdapter3 extends LoopPagerAdapter {
    private List<Banner2> data;
    private Context mContext;
    /*private int[] imgs = {
            R.mipmap.test_enterprise2,
            R.mipmap.test_enterprise3,
            R.mipmap.test_enterprise4,
    };*/

    public void updata(List<Banner2> list) {
        if (list != null) {
            data = list;
            notifyDataSetChanged();
        }
    }

    public ViewPagerAdapter3(Context context, RollPagerView viewPager) {
        super(viewPager);
        mContext = context;
        data = new ArrayList<Banner2>();
    }

    @Override
    public View getView(ViewGroup container, final int position) {
        ImageView view = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //view.setImageResource(imgs[position]);
        ImageLoader.load(mContext, data.get(position).original_img, view);
        return view;
    }

    @Override
    public int getRealCount() {
        return data.size();
    }
}
