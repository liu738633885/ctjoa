package com.ctj.oa.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.xiaomi.push.thrift.a.R;


/**
 * Created by AINANA-RD-X on 2016/8/11.
 */
public class ShareUtils {

    //发送到聊天界面
    public static final int SHARE_TO_SESSION = SendMessageToWX.Req.WXSceneSession;
    //发送到朋友圈
    public static final int SHARE_TO_TIMELINE = SendMessageToWX.Req.WXSceneTimeline;
    //添加到微信收藏
    public static final int SHARE_TO_FAVORITE = SendMessageToWX.Req.WXSceneFavorite;

    public static void ShareWX(Context context, String shareUrl, String title, String description, Bitmap bitmap, int shareStyle) {
      /*  IWXAPI msgApi = WXAPIFactory.createWXAPI(context, "wx5bb1152c4f7a21d4");
        //初始化一个WXWebpageObject对象，填写Url
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = shareUrl;
        //
        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = title;
        msg.description = description;
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        msg.thumbData = Util.bmpToByteArrayMy(bitmap, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webPage" + System.currentTimeMillis();
        req.message = msg;
        req.scene = shareStyle;
        msgApi.sendReq(req);*/
    }

    public static void ShareWX(final Context context, final String shareUrl, final String title, final String description, String imgurl, final int shareStyle) {
        /*if (TextUtils.isEmpty(imgurl)) {
            ShareWX(context, shareUrl, title, description, BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_icon), shareStyle);
            return;
        }
        Glide.with(context).load(imgurl).asBitmap().into(new SimpleTarget<Bitmap>(200, 200) {
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                ShareWX(context, shareUrl, title, description, BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_icon), shareStyle);
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                ShareWX(context, shareUrl, title, description, resource, shareStyle);
            }
        });*/
    }

}
