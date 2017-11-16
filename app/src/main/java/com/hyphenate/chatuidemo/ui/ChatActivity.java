package com.hyphenate.chatuidemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.chat.message.MessageListActivity;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsManager;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.util.EasyUtils;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;

/**
 */
public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    public String toChatUsername;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        //get user id or group id
        toChatUsername = getIntent().getExtras().getString("userId");
        if (toChatUsername.equals(Constants.ADMIN_ID)) {
            startActivity(new Intent(this, MessageListActivity.class));
            finish();
        }
        setContentView(R.layout.em_activity_chat);
        activityInstance = this;

        //use EaseChatFratFragment
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
          /*  Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);*/
        }
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
                if (data != null) {
                    ArrayList<String> photos =
                            data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    //Uri selectedImage = data.getData();
                   /* Uri selectedImage = Uri.parse(photos.get(0));
                    if (selectedImage != null) {
                        chatFragment.sendPicByUri(selectedImage);
                    }*/
                    if (photos.size() > 0) {
                        for (String s : photos) {
                            Uri selectedImage = Uri.parse(s);
                            if (selectedImage != null) {
                                chatFragment.sendPicByUri(selectedImage);
                            }
                        }
                    }
                }
        }
    }
}
