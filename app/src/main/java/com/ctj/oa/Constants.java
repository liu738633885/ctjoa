package com.ctj.oa;

import com.lewis.utils.FileUtil;

import java.io.File;

/**
 * Created by lewis on 16/6/22.
 */
public class Constants {
    public static String getApiConfig() {
        return getConfig() + "/v1_0.";
    }

    public static String getConfig() {
        int httpConfig = MainApplication.getInstance().getResources().getInteger(R.integer.HTTP_CONFIG);
        if (httpConfig == 1) {
            return API_URL_RELEASE;
        } else if (httpConfig == 2) {
            return API_URL_DEBUG;
        } else {
            return API_URL_RELEASE;
        }
    }

    //public static final String API_URL_RELEASE = "http://oaapi.weihainan.com";
    public static final String API_URL_RELEASE = "http://api.rockbrain.net";
    public static final String API_URL_DEBUG = "http://oaapi.weihainan.com";
    public static final String IMG_HEAD = API_URL_RELEASE;
    public static final String ADMIN_ID = "admin";

    /**
     * ====login=======
     */
    public static final String _LOGIN = getApiConfig() + "login/";
    public static final String REGISTER = _LOGIN + "register";
    public static final String LOGIN = _LOGIN + "auth";
    public static final String GET_SMS_CODE = _LOGIN + "get_sms_code";
    public static final String GET_START_PAGE = _LOGIN + "get_startpage";
    public static final String GET_EDIT_PASSWORD_CODE = _LOGIN + "get_editpassword_code";
    public static final String FIND_PASSWORD = _LOGIN + "find_password";
    /**
     * ====Company========
     */

    public static final String _COMPANY = getApiConfig() + "Company/";
    public static final String GET_COMPANY_DEPT = _COMPANY + "getCompanyDept";
    public static final String GET_COMPANY_DEPT_USER = _COMPANY + "getCompanyDeptUser";
    public static final String ADD_COMPANY_DEPT = _COMPANY + "addCompanyDept";
    public static final String DEL_COMPANY_DEPT = _COMPANY + "delCompanyDept";
    public static final String GET_CIRCLE_CLASS_LIST = _COMPANY + "getCircleClassList";
    public static final String GET_DEPT_USER = _COMPANY + "getDeptUser";


    public static final String COMPANY_BANNER = _COMPANY + "company_banner";
    public static final String GET_COMPANY_BANNER_LIST = _COMPANY + "get_company_banner_list";
    public static final String GET_COMPANY_LIST = _COMPANY + "getCompanyList";
    public static final String APPLY_ADD_CIRCLE = _COMPANY + "applyAddCircle";
    public static final String GET_CIRCLE_INFO = _COMPANY + "getCircleInfo";
    public static final String ADD_ARTICLE = _COMPANY + "add_article";
    public static final String COMPANY_FOLLOW = _COMPANY + "company_follow";
    public static final String GET_ARTICLE_LIST = _COMPANY + "get_article_list";
    public static final String GET_COMPANY_DETAIL = _COMPANY + "get_company_detail";
    /**
     * ====User========
     */
    public static final String _USER = getApiConfig() + "User/";
    public static final String GET_USER_INFO = _USER + "getUserInfo";
    public static final String UPDATE_USER_INFO = _USER + "updateUserInfo";
    public static final String EDIT_PASSWORD = _USER + "editPassword";
    public static final String USER_FEEDBACK = _USER + "user_feedback";
    public static final String GET_FIELD_INFO = _USER + "getFieldInfo";
    public static final String GET_GROUP_USER = _USER + "get_group_user";
    /**
     * ====Attend========
     */
    public static final String _ATTEND = getApiConfig() + "Attend/";
    public static final String TODAY_SHIFT = _ATTEND + "todayShift";
    public static final String TODAY_SIGN_RECORD = _ATTEND + "toadySignRecord";
    public static final String SIGN = _ATTEND + "sign";
    /**
     * ====Approve=======
     */
    public static final String _APPROVE = getApiConfig() + "Approve/";
    public static final String GET_APPROVE_TEMPLATE = _APPROVE + "getApproveTemplate";
    public static final String GET_APPROVE_TEMPLATE_DETAIL = _APPROVE + "getApproveTemplateDetail";
    public static final String ADD_APPLY_APPROVE = _APPROVE + "AddApplyApprove";
    public static final String GET_APPLY_LIST = _APPROVE + "getApplyList";
    public static final String MY_APPROVE_LIST = _APPROVE + "MyApproveList";
    public static final String CC_APPROVE_LIST = _APPROVE + "cc_approve_list";
    public static final String GET_APPLY_INFO = _APPROVE + "getApplyInfo";
    public static final String CT_INFO = _APPROVE + "ct_info";
    public static final String GET_CHECK_APPROVE_DETAIL = _APPROVE + "getCheckApproveDetail";
    public static final String GET_APPROVE_CLASS_LIST = _APPROVE + "get_class_list";
    public static final String CC_APPROVE = _APPROVE + "cc_approve";
    public static final String REVOKE_APPROVE = _APPROVE + "revoke_approve";
    public static final String CHECK_APPLY_APPROVE = _APPROVE + "check_apply_approve";
    public static final String GET_APPLY_APPROVE_COUNT = _APPROVE + "get_apply_approve_count";
    /**
     * ====log=========
     */
    public static final String _LOG = getApiConfig() + "log/";
    public static final String GET_LOG_LIST = _LOG + "getLogList";
    public static final String GET_LOG_TEMPLATE = _LOG + "getLogTemplate";
    public static final String GET_LOG_TEMPLATE_INFO = _LOG + "getLogTemplateInfo";
    public static final String ADD_LOG = _LOG + "addLog";
    public static final String GET_LOG_INFO = _LOG + "getLogInfo";
    public static final String GET_LOG_COMMENT_LIST = _LOG + "getCommentList";
    public static final String ADD_LOG_COMMENT = _LOG + "addLogComment";
    /**
     * ====notice=========
     */
    public static final String _NOTICE = getApiConfig() + "Notice/";
    public static final String NOTICE_CLASS_LIST = _NOTICE + "notice_class_list";
    public static final String NOTICE_LIST = _NOTICE + "notice_list";
    /**
     * ====message=========
     */
    public static final String _MESSAGE = getApiConfig() + "message/";
    public static final String GET_MESSAGE_LIST = _MESSAGE + "get_message_list";
    /**
     * ====memo=========
     */
    public static final String _MEMO = getApiConfig() + "memo/";
    public static final String GET_MEMO_COMMENT_LIST = _MEMO + "getMemoCommentList";
    public static final String ADD_MEMO_COMMENT = _MEMO + "addMemoComment";
    public static final String GET_MEMO_INFO = _MEMO + "getMemoInfo";
    public static final String MEMO_SAVE = _MEMO + "memoSave";
    public static final String GET_MEMO_LIST = _MEMO + "getMemoList";
    public static final String CLEAN_TIPS = _MEMO + "clear_tips";
    /**
     * ====task=========
     */
    public static final String _Task = getApiConfig() + "Task/";
    public static final String TASK_LIST = _Task + "task_list";
    public static final String TASK_INFO = _Task + "task_info";
    public static final String TASK_DISCUSS_LIST = _Task + "task_discuss_list";
    public static final String TASK_DISCUSS_SAVE = _Task + "task_discuss_save";
    public static final String TASK_SAVE = _Task + "task_save";
    public static final String TASK_ACCEPT = _Task + "task_accept";
    public static final String TASK_OVER = _Task + "task_over";
    public static final String TASK_CONFIRM = _Task + "task_confirm";
    public static final String TASK_CANCEL = _Task + "task_cancel";
    public static final String TASK_RECOVER = _Task + "task_recover";

    /**
     * ====Upload=========
     */
    public static final String _Upload = getApiConfig() + "Upload/";
    public static final String UPLOAD_IMGES = _Upload + "upload_images";
    /**
     * ====WEB==========
     */
    //public static final String _WEB = "http://oaweb.weihainan.com/";
    public static final String _WEB = "http://web.rockbrain.net/";
    public static final String WEB_NOTICE_DETAIL = _WEB + "mycenter/notice_detail.html";
    public static final String WEB_ARTICLE_DETAIL = _WEB + "mycenter/article_detail.html";
    public static final String WEB_CREATE_COMPANY = _WEB + "open/create_company.html";
    public static final String WEB_HELP = _WEB + "open/content_page/alias/user_help.html";
    public static final String WEB_SHARE = _WEB + "mycenter/share.html";
    public static final String WEB_TASK_DISCUSS = _WEB + "open/task_discuss_info.html";
    public static final String WEB_ABOUT = _WEB + "open/content_page/alias/about.html";
    public static final String WEB_AGREEMENT = _WEB + "open/content_page/alias/user_agreement.html";
    public static final String WEB_FILE_LIST = _WEB + "mycenter/myFileList.html";


    /**
     * ====图片地址=======
     */
    public static final String APP_PATH_ROOT = FileUtil.getRootPath().getAbsolutePath() + File.separator + "OA_splash";
}