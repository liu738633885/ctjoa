package com.ctj.oa.utils;

import android.content.Context;
import android.content.Intent;

import com.ctj.oa.Constants;
import com.ctj.oa.activity.WebViewActivity;
import com.ctj.oa.utils.manager.UserManager;
import com.ctj.oa.work.approval.ApprovalDetailActivity;
import com.ctj.oa.work.log.LogDetailActivity;
import com.ctj.oa.work.memo.MemoDetailActivity;
import com.ctj.oa.work.task.TaskDetailActivity;

/**
 * Created by lewis on 2017/6/13.
 */

public class GoToUtils {
    public static void goToFormMessage(Context context, int id, int type) {
        switch (type) {
            case 0:
                break;
            case 1:
                ApprovalDetailActivity.goTo(context, id, ApprovalDetailActivity.MODE_APPROVAL, type);
                break;
            case 2:
                ApprovalDetailActivity.goTo(context, id, ApprovalDetailActivity.MODE_CT, type);
                break;
            case 3:
                ApprovalDetailActivity.goTo(context, id, ApprovalDetailActivity.MODE_APPLY, type);
                break;
            case 4:
                WebViewActivity.goTo(context, Constants.WEB_NOTICE_DETAIL + "?id=" + id + "&user_id=" + UserManager.getId() + "&token=" + UserManager.getToken(), "公告详情");
                break;
            case 5:
                LogDetailActivity.goTo(context, id);
                break;
            case 6:
                MemoDetailActivity.goTo(context, id);
                break;
            case 7:
                TaskDetailActivity.goTo(context, id);
                break;
            case 8:
                WebViewActivity.goTo(context, Constants.WEB_FILE_LIST + "?user_id=" + UserManager.getId() + "&token=" + UserManager.getToken(), "文件列表");
                break;
        }
    }

    public static Intent messageToIntent(Context context, int id, int type) {
        Intent intent = null;
        switch (type) {
            case 0:
                break;
            case 1:
                intent = new Intent(context, ApprovalDetailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("type", type);
                intent.putExtra("mode", ApprovalDetailActivity.MODE_APPROVAL);
                break;
            case 2:
                intent = new Intent(context, ApprovalDetailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("type", type);
                intent.putExtra("mode", ApprovalDetailActivity.MODE_CT);
                break;
            case 3:
                intent = new Intent(context, ApprovalDetailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("type", type);
                intent.putExtra("mode", ApprovalDetailActivity.MODE_APPLY);
                break;
            case 4:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", Constants.WEB_NOTICE_DETAIL + "?id=" + id + "&user_id=" + UserManager.getId() + "&token=" + UserManager.getToken());
                intent.putExtra("title", "公告详情");
                break;
            case 5:
                intent = new Intent(context, LogDetailActivity.class);
                intent.putExtra("id", id);
                break;
            case 6:
                intent = new Intent(context, MemoDetailActivity.class);
                intent.putExtra("id", id);
                break;
            case 7:
                intent = new Intent(context, TaskDetailActivity.class);
                intent.putExtra("id", id);
                break;
            case 8:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", Constants.WEB_FILE_LIST + "?user_id=" + UserManager.getId() + "&token=" + UserManager.getToken());
                intent.putExtra("title", "文件列表");
                break;
            default:
        }
        return intent;
    }
}
