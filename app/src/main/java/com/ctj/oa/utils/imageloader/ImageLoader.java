package com.ctj.oa.utils.imageloader;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ctj.oa.BuildConfig;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.lewis.utils.DensityUtil;


/**
 * Created by lewis on 16/7/6.
 */
public class ImageLoader {
    private static DrawableRequestBuilder getBuilder(Context context, String url, boolean isHeadIcon, int type, int denWith, int denHeight, int round) {

        if (url.startsWith("http")) {

        } else if (url.startsWith("/storage")) {

        } else if (url.startsWith("android.resource:")) {

        } else {
            if (TextUtils.isEmpty(url) && isHeadIcon) {
                url = "android.resource://" + BuildConfig.APPLICATION_ID + "/mipmap/" + R.mipmap.default_user_icon;
            } else {
                url = Constants.IMG_HEAD + url;
            }
        }

        int placehoder;
        if (isHeadIcon) {
            placehoder = R.mipmap.default_user_icon;
        } else {
            placehoder = R.color.loading;
        }
        DrawableRequestBuilder builder = Glide.with(context).load(url).placeholder(placehoder);
        if (denWith > 0 && denHeight > 0) {
            builder.override(DensityUtil.getWidth(context) / denWith, DensityUtil.getWidth(context) / denHeight);
        }
        if (type == 1) {
            builder.centerCrop();
        } else if (type == 2) {
            builder.fitCenter();
        }
        if (round == 0) {
            builder.transform(new GlideCircleTransform(context));
        } else if (round != -1) {
            builder.transform(new GlideRoundTransform(context, round));
        }
        return builder;
    }

    public static void load(Context context, String url, ImageView imv) {
        try {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            getBuilder(context, url, false, 1, 0, 0, -1).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadFromMipmap(Context context, int urlId, ImageView imv) {
        try {
            String url = "android.resource://" + BuildConfig.APPLICATION_ID + "/mipmap/" + urlId;
            getBuilder(context, url, false, 1, 0, 0, -1).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadHeadImage(Context context, String url, ImageView imv, int round) {
        try {
            getBuilder(context, url, true, 1, 0, 0, round).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadSplashImage(Context context, String url, ImageView imv) {
        Glide.with(context).load(url).placeholder(R.mipmap.splash).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().into(imv);
    }


}
