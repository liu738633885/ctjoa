package com.ctj.oa.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.UserInfo;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.BitmapUtils;
import com.ctj.oa.utils.SPUtils;
import com.ctj.oa.utils.UpLoadManager;
import com.ctj.oa.utils.manager.UserManager;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.ctj.oa.widgets.WaveHelper;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.ui.ChatActivity;
import com.hyphenate.easeui.domain.EaseUser;
import com.lewis.utils.T;
import com.lewis.widgets.WaveView;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;


/**
 * Created by lewis on 2017/5/2.
 */

public class UserProfileActivity extends BaseActivity implements View.OnClickListener {
    private String userId;
    private WaveView waveView;
    private WaveHelper mWaveHelper;
    private EditText edt_name, edt_signature, edt_email, edt_tell;
    boolean isMine;
    private ImageView imv_avatar;
    private TextView post_name, dept_name, phone;
    private Button btn_chat;
    private UserInfo userInfo;
    private boolean isFinish;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_user_profile;
    }

    public static void goTo(Context context, String userId) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    public static void goToFinish(Context context, String userId) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isFinish", true);
        context.startActivity(intent);
    }

    //获取Intent
    protected void handleIntent(Intent intent) {
        userId = intent.getStringExtra("userId");
        isFinish = intent.getBooleanExtra("isFinish", false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        isMine = userId.equals(UserManager.getId());
        initWaveView();
        edt_signature = (EditText) findViewById(R.id.edt_signature);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_tell = (EditText) findViewById(R.id.edt_tell);
        edt_name = (EditText) findViewById(R.id.edt_name);
        post_name = (TextView) findViewById(R.id.post_name);
        dept_name = (TextView) findViewById(R.id.dept_name);
        phone = (TextView) findViewById(R.id.phone);
        imv_avatar = (ImageView) findViewById(R.id.imv_avatar);
        btn_chat = (Button) findViewById(R.id.btn_chat);
        setEdit(false);

        if (isMine) {
            btn_chat.setText("修改信息");
        }
        btn_chat.setOnClickListener(this);
        imv_avatar.setOnClickListener(this);

        getUserInfo();
    }

    private void setEdit(boolean isEdit) {
        edt_name.setEnabled(isEdit);
        edt_signature.setEnabled(isEdit);
        edt_email.setEnabled(isEdit);
        edt_tell.setEnabled(isEdit);
        imv_avatar.setEnabled(isEdit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chat:
                if (isMine) {
                    //更新信息
                    if (btn_chat.getText().toString().equals("修改信息")) {
                        btn_chat.setText("提交修改");
                        setEdit(true);
                    } else {
                        updateUserInfo();
                    }
                } else {
                    startActivity(new Intent(this, ChatActivity.class).putExtra("userId", userId));
                    if (isFinish) {
                        finish();
                    }
                }
                break;
            case R.id.imv_avatar:
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(true)
                        .start(UserProfileActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            default:
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Uri sourceUri = Uri.fromFile(new File(photos.get(0)));
                beginCrop(sourceUri);

            }
        }
        if (requestCode == Crop.REQUEST_CROP) {
            UpLoadManager.uploadImg(this, Crop.getOutput(data).getPath(), new UpLoadManager.SingleUpLoadListener() {
                @Override
                public void onComplete(boolean isSuccess, String path_message) {
                    if (isSuccess) {
                        userInfo.setPortrait(path_message);
                        ImageLoader.loadHeadImage(bContext, userInfo.getPortrait(), imv_avatar, 0);
                    }
                }
            }, 500, 500);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(BitmapUtils.getSaveRealPath(), System.currentTimeMillis() + "cropped.jpg"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void updateUserInfo() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.UPDATE_USER_INFO);
        request.add("nickname", edt_name.getText().toString());
        request.add("portrait", userInfo.getPortrait());
        //request.add("portrait", "http://avatarimg.smzdm.com/default/5493411788/57577b32724cd-middle.jpg");
        request.add("signature", edt_signature.getText().toString());
        request.add("tell", edt_tell.getText().toString());
        request.add("mail", edt_email.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    if (isMine) {
                        btn_chat.setText("修改信息");
                        getUserInfo();
                    }
                } else {
                    T.showShort(UserProfileActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            }
        }, true, true);
    }


    private void getUserInfo() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_USER_INFO);
        request.add("get_user_id", userId);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    userInfo = netBaseBean.parseObject(UserInfo.class);
                    if (isMine) {
                        UserManager.saveUserInfo(userInfo);
                    } else {
                        SPUtils.getHuanxinUserInstance().put(userId, userInfo);
                        /*EaseUser easeUser = new EaseUser(userInfo.getId() + "");
                        easeUser.setNickname(userInfo.getNickname());
                        easeUser.setAvatar(userInfo.getPortrait());
                        DemoHelper.getInstance().saveContact(easeUser);*/
                    }
                    updateUI();
                } else {
                    T.showShort(UserProfileActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            }
        }, true, true);
    }

    private void updateUI() {
        ImageLoader.loadHeadImage(this, userInfo.getPortrait(), imv_avatar, 0);
        edt_name.setText(userInfo.getNickname());
        post_name.setText(userInfo.getPost_name());
        dept_name.setText(userInfo.getDept_name());
        phone.setText(userInfo.getPhone());
        edt_signature.setText(userInfo.getSignature());
        edt_email.setText(userInfo.getMail());
        edt_tell.setText(userInfo.getTell());
        setEdit(false);
    }


    private void initWaveView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (waveView == null) {
                    waveView = (WaveView) findViewById(R.id.waveView);
                    waveView.setShapeType(WaveView.ShapeType.SQUARE);
                    waveView.setWaveColor(Color.parseColor("#18FFFFFF"),
                            Color.parseColor("#13FFFFFF"));
                }
                if (mWaveHelper == null) {
                    mWaveHelper = new WaveHelper(waveView);
                    mWaveHelper.start();
                }
            }
        }, 300);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWaveHelper != null) {
            mWaveHelper.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWaveHelper != null) {
            mWaveHelper.start();
        }
    }


}
