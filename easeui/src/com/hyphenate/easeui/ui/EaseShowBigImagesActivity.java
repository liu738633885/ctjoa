/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.ImageUtils;
import com.lewis.utils.DensityUtil;
import com.bm.library.PhotoView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * download and show original image
 */
public class EaseShowBigImagesActivity extends EaseBaseActivity {
    private static final String TAG = "ShowBigImage";
    private int default_res = R.drawable.ease_default_image;
    //private Bitmap bitmap;
    private boolean isDownloaded;
    private String toChatUsername;
    private String messageId;
    private List<EMMessage> imgModels;


    //---
    private List<View> guideViewList = new ArrayList<View>();
    private LinearLayout guideGroup;
    private int startPos;
    //private ArrayList<EaseImage> imgUrls;
    //public static final String INTENT_IMGURLS = "imgurls";
    //public static final String INTENT_POSITION = "position";

    /*public static void startImagePagerActivity(Context context, List<EaseImage> imgUrls, int position) {
        Intent intent = new Intent(context, EaseShowBigImagesActivity.class);
        intent.putExtra(INTENT_IMGURLS, new ArrayList<EaseImage>(imgUrls));
        intent.putExtra(INTENT_POSITION, position);
        context.startActivity(intent);
    }*/
    public static void startImagePagerActivity(Context context, String toChatUsername, String messageId) {
        Intent intent = new Intent(context, EaseShowBigImagesActivity.class);
        intent.putExtra("toChatUsername", toChatUsername);
        intent.putExtra("messageId", messageId);
        context.startActivity(intent);
    }

    private void getIntentData() {
        toChatUsername = getIntent().getStringExtra("toChatUsername");
        messageId = getIntent().getStringExtra("messageId");
        /*startPos = getIntent().getIntExtra(INTENT_POSITION, 0);
        imgUrls = (ArrayList<EaseImage>) getIntent().getSerializableExtra(INTENT_IMGURLS);*/
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.ease_activity_show_big_images);
        super.onCreate(savedInstanceState);
        ViewPager viewPager = findViewById(R.id.pager);
        guideGroup = findViewById(R.id.guideGroup);
        getIntentData();
        ImageAdapter mAdapter = new ImageAdapter(this);
        //初始化数据
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername);
        List<EMMessage> messages = conversation.getAllMessages();
        imgModels = new ArrayList<EMMessage>();
        //Collections.reverse(messages);
        boolean find = false;
        for (int i = 0; i < messages.size(); i++) {
            //Log.e(TAG,"类型"+ e.getType()+"id"+e.getMsgId());
            if (messages.get(i).getType() == EMMessage.Type.IMAGE) {
                imgModels.add(messages.get(i));
                if (messages.get(i).getMsgId().equals(messageId)) {
                    find = true;
                }
                if (!find) {
                    startPos++;
                }
            }
        }
        mAdapter.setDatas(imgModels);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < guideViewList.size(); i++) {
                    guideViewList.get(i).setSelected(i == position ? true : false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(startPos);

        addGuideView(guideGroup, startPos);

        default_res = getIntent().getIntExtra("default_image", R.drawable.ease_default_image);
    }

    @SuppressLint("NewApi")
    private class ImageAdapter extends PagerAdapter {

        private List<EMMessage> datas = new ArrayList<EMMessage>();
        private LayoutInflater inflater;
        private Context context;
        private ImageView smallImageView = null;

        public void setDatas(List<EMMessage> datas) {
            if (datas != null)
                this.datas = datas;
        }


        public ImageAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (datas == null) return 0;
            return datas.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.ease_item_pager_image, container, false);
            if (view != null) {
                final PhotoView imageView =  view.findViewById(R.id.image);
                imageView.enable();
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EaseShowBigImagesActivity.this.finish();
                    }
                });
                //loading
                final ProgressBar loading = new ProgressBar(context);
                FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                loadingLayoutParams.gravity = Gravity.CENTER;
                loading.setLayoutParams(loadingLayoutParams);
                ((FrameLayout) view).addView(loading);
                final EMMessage imageModelFather = datas.get(position);
                final EMImageMessageBody imageModel = (EMImageMessageBody) imageModelFather.getBody();
                container.addView(view, 0);

                //渲染图片
                File file = new File(imageModel.getLocalUrl());
                if (file.exists()) {
                    EMLog.d(TAG, "showbigimage file exists. directly show it");
                    Glide.with(context)
                            .load(imageModel.getLocalUrl())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存多个尺寸
                            .thumbnail(0.1f)//先显示缩略图  缩略图为原图的1/10
                            .error(default_res)
                            .into(new GlideDrawableImageViewTarget(imageView) {
                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                    loading.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                    loading.setVisibility(View.GONE);
                                }

                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                    super.onResourceReady(resource, animation);
                                    loading.setVisibility(View.GONE);
                                }
                            });
                } else if (datas.get(position).getMsgId() != null) {
                    EMLog.e(TAG, "download with messageId: " + imageModelFather.getMsgId());
                    String str1 = getResources().getString(R.string.Download_the_pictures);

                    File temp = new File(imageModel.getLocalUrl());
                    final String tempPath = temp.getParent() + "/temp_" + temp.getName();
                    final EMCallBack callback = new EMCallBack() {
                        public void onSuccess() {
                            EMLog.e(TAG, "onSuccess");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new File(tempPath).renameTo(new File(imageModel.getLocalUrl()));

                                    DisplayMetrics metrics = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                                    int screenWidth = metrics.widthPixels;
                                    int screenHeight = metrics.heightPixels;

                                    Bitmap bitmap = ImageUtils.decodeScaleImage(imageModel.getLocalUrl(), screenWidth, screenHeight);
                                    if (bitmap == null) {
                                        imageView.setImageResource(default_res);
                                    } else {
                                        imageView.setImageBitmap(bitmap);
                                        EaseImageCache.getInstance().put(imageModel.getLocalUrl(), bitmap);
                                        isDownloaded = true;
                                    }
                                    if (isFinishing() || isDestroyed()) {
                                        return;
                                    }
                                    if (loading != null) {
                                        loading.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }

                        public void onError(int error, String msg) {
                            EMLog.e(TAG, "offline file transfer error:" + msg);
                            File file = new File(tempPath);
                            if (file.exists() && file.isFile()) {
                                file.delete();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (EaseShowBigImagesActivity.this.isFinishing() || EaseShowBigImagesActivity.this.isDestroyed()) {
                                        return;
                                    }
                                    imageView.setImageResource(default_res);
                                    if (loading != null) {
                                        loading.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }

                        public void onProgress(final int progress, String status) {
                            EMLog.d(TAG, "Progress: " + progress);
                            final String str2 = getResources().getString(R.string.Download_the_pictures_new);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (EaseShowBigImagesActivity.this.isFinishing() || EaseShowBigImagesActivity.this.isDestroyed()) {
                                        return;
                                    }
                                    //pd.setMessage(str2 + progress + "%");
                                    if (loading != null) {
                                        loading.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    };

                    EMMessage msg = EMClient.getInstance().chatManager().getMessage(imageModelFather.getMsgId());
                    msg.setMessageStatusCallback(callback);

                    EMLog.e(TAG, "downloadAttachement");
                    EMClient.getInstance().chatManager().downloadAttachment(msg);

                } else {
                    imageView.setImageResource(default_res);
                }

            }

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }

    private void addGuideView(LinearLayout guideGroup, int startPos) {
        if (imgModels != null && imgModels.size() > 0) {
            guideViewList.clear();
            for (int i = 0; i < imgModels.size(); i++) {
                View view = new View(this);
                view.setBackgroundResource(R.drawable.selector_guide_bg);
                view.setSelected(i == startPos ? true : false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(this, 6),
                        DensityUtil.dip2px(this, 6));
                layoutParams.setMargins(10, 0, 0, 0);
                guideGroup.addView(view, layoutParams);
                guideViewList.add(view);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isDownloaded)
            setResult(RESULT_OK);
        finish();
    }
}
