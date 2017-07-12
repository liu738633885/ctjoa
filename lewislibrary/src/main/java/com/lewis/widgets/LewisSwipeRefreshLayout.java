package com.lewis.widgets;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import com.example.ainanalibrary.R;
import com.lewis.utils.DensityUtil;
import com.lewis.widgets.swl.CustomProgressDrawable;
import com.lewis.widgets.swl.SwipeRefreshLayout;

/**
 * Created by lewis on 2017/1/23.
 */

public class LewisSwipeRefreshLayout extends SwipeRefreshLayout {

    public LewisSwipeRefreshLayout(Context context) {
        super(context);
        init();
    }

    public LewisSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        CustomProgressDrawable drawable = new CustomProgressDrawable(getContext(), this);
        drawable.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.tong));
        setProgressView(drawable);

        setProgressViewOffset(false, 0, DensityUtil.dip2px(getContext(), 64));
        //swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }
}
