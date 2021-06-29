package com.youzhi.chatdemo.constant;

/**
 * @author gaoyuheng
 * @description:
 * @date : 5/20/21 11:41 AM.
 */
public interface AppConst {
    String KEY_FRAGMENT_STATUS_SAVE = "fragment_status_save";
    boolean isDebug = true;
//    String DEBUG_URL = "https://hcim.zaoha.net/hcapi";
    String DEBUG_URL = "https://opim.zaoha.net/opapi";

    String UPLOAD_IMG_SERVER_URL = "https://hecheng.yy2080.xyz";

    static String appUrl() {
        if (isDebug)
            return DEBUG_URL;
        else
            return DEBUG_URL;

    }

    static String getSocketUrl() {
        if (isDebug) {
           // return "https://hcim.zaoha.net";   //测试socket
            return "https://opim.zaoha.net";   //测试socket
        } else {
            return ""; //正式socket
        }
    }

    String LOGIN_CHECK = "/openSource/loginCheck"; //登录接口
    String GET_USER_INFO = "/openSource/getUserInfo";//获取用户信息接口
    String UP_LOAD = "/action/ac_house/uploadpic";//上传图片
    String CHAT_VOICE = "/action/ac_user/chatVoice"; //上传聊天语音
    String FILE_UPLOAD = "/openSource/fileUpload"; //文件上传


}
