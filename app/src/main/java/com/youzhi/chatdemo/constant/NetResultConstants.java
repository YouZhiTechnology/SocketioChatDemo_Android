package com.youzhi.chatdemo.constant;

/**
 * @author gaoyuheng
 * @description:
 * @date :2020/9/1 10:42
 */
public interface NetResultConstants {
    //登录成功
    int STATUS_SUCCESS = 200;
    //登录过期
    int STATUS_LOGIN_STALE_DATED = 605;
    //其他地方登录
    int STATUS_LOGIN_OTHER = 606;
    //权限更改
    int STATUS_JURISDICTION_CHANGE = 608;
    String DATA = "data";
    String MSG = "msg";
    String CODE = "code";
}
