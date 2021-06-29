package com.youzhi.chatdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 */
public class UserSP {
    private static final String DATABASE_NAME = "USER";
    private SharedPreferences sharedPreferences;

    public static final String TAG = "sjx";
    private static final String UID = "UID";//用户ID
    private static final String IS_LOGIN = "IS_LOGIN";//登录状态
    private static final String IS_VPN = "IS_VPN";//VPN状态
    private static final String IS_NIGHT = "IS_NIGHT";//夜间状态
    private static final String IS_FULL = "IS_FULL";//全屏状态
    private static final String IS_NO_IMG = "IS_NO_IMG";//无图状态
    private static final String IS_No_Traceless = "IS_No_Traceless";//无痕状态
    private static final String IS_No_AD = "IS_No_AD";//去除广告状态
    private static final String IS_MOVE = "IS_MOVE";//移动网络提示状态

    public static final String BaiduUrl = "http://www.baidu.com/";
    public static final String SinaUrl = "http://www.sina.com.cn/";
    public static final String GoogleUrl = "http://www.google.com";
    public static final String TencentUrl = "http://news.qq.com/";
    private static final String SE = "SE";//默认的搜索引擎
    private static final String ACCOUNT = "account";//账号
    private static final String STACT_NUM = "STACT_NUM";//存储的多任务创建ID
    private static final String SEARCH_KEY = "SEARCH_KEY";
    private static final String CODE = "code"; //用户登录输入得激活码


    public void init(Context context) {
        sharedPreferences = context.getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 用户ID
     *
     * @param uid
     */

    public void saveUid(String uid) {
        sharedPreferences.edit().putString(UID, uid).commit();
    }

    public String readUid() {
        return sharedPreferences.getString(UID, "");
    }

    /**
     * 是否登录
     *
     * @param isLogin
     */

    public void saveIsLogin(boolean isLogin) {
        sharedPreferences.edit().putBoolean(IS_LOGIN, isLogin).commit();
    }

    public boolean readIsLogin() {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    /**
     * IS_VPN状态
     *
     * @param isVpn
     */

    public void saveIsVpn(boolean isVpn) {
        sharedPreferences.edit().putBoolean(IS_VPN, isVpn).commit();
    }

    public boolean readIsVpn() {
        return sharedPreferences.getBoolean(IS_VPN, false);
    }

    /**
     * 存储的默认搜索引擎
     *
     * @param se
     */

    public void saveSE(String se) {
        sharedPreferences.edit().putString(SE, se).commit();
    }

    public String readSE() {
        return sharedPreferences.getString(SE, BaiduUrl);
    }

    /**
     * 存储的用户账号
     *
     * @param account
     */

    public void saveAccount(String account) {
        sharedPreferences.edit().putString(ACCOUNT, account).commit();
    }

    public String readAccount() {
        return sharedPreferences.getString(ACCOUNT, "");
    }

    /**
     * 存储的用户输入得激活码
     *
     * @param code
     */

    public void saveCode(String code) {
        sharedPreferences.edit().putString(CODE, code).commit();
    }

    public String readCode() {
        return sharedPreferences.getString(CODE, "");
    }

    /**
     * 夜间状态
     *
     * @param isNight
     */

    public void saveIsNight(boolean isNight) {
        sharedPreferences.edit().putBoolean(IS_NIGHT, isNight).commit();
    }

    public boolean readIsNight() {
        return sharedPreferences.getBoolean(IS_NIGHT, false);
    }

    /**
     * 全屏状态
     *
     * @param isFull
     */

    public void saveIsFull(boolean isFull) {
        sharedPreferences.edit().putBoolean(IS_FULL, isFull).commit();
    }

    public boolean readIsFull() {
        return sharedPreferences.getBoolean(IS_FULL, false);
    }

    /**
     * 无图状态
     *
     * @param isNoimg
     */

    public void saveIsNoimg(boolean isNoimg) {
        sharedPreferences.edit().putBoolean(IS_NO_IMG, isNoimg).commit();
    }

    public boolean readIsNoimg() {
        return sharedPreferences.getBoolean(IS_NO_IMG, false);
    }

    /**
     * 无痕状态
     *
     * @param isNoTraceless
     */

    public void saveIsNoTraceless(boolean isNoTraceless) {
        sharedPreferences.edit().putBoolean(IS_No_Traceless, isNoTraceless).commit();
    }

    public boolean readIsNoTraceless() {
        return sharedPreferences.getBoolean(IS_No_Traceless, false);
    }

    /**
     * 去除广告状态
     *
     * @param isNoAd
     */

    public void saveIsNoAd(boolean isNoAd) {
        sharedPreferences.edit().putBoolean(IS_No_AD, isNoAd).commit();
    }

    public boolean readIsNoAd() {
        return sharedPreferences.getBoolean(IS_No_AD, false);
    }

    /**
     * 移动网络状态
     *
     * @param isMove
     */

    public void saveIsMove(boolean isMove) {
        sharedPreferences.edit().putBoolean(IS_MOVE, isMove).commit();
    }

    public boolean readIsMove() {
        return sharedPreferences.getBoolean(IS_MOVE, false);
    }

    /**
     * 添加的多任务数量
     *
     * @param stactNum
     */

    public void saveStactNum(int stactNum) {
        sharedPreferences.edit().putInt(STACT_NUM, stactNum).commit();
    }

    public int readStactNum() {
        return sharedPreferences.getInt(STACT_NUM, 1);
    }

    public void saveSearchHistory(String json) {
        sharedPreferences.edit().putString(SEARCH_KEY, json).commit();

    }

    public String readSearchHistory() {
        return sharedPreferences.getString(SEARCH_KEY, "");
    }
}
