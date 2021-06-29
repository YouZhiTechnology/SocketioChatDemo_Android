package com.youzhi.chatdemo.chat.bean;

import android.text.TextUtils;

import com.youzhi.chatdemo.MyApplication;

public class ChatMessageInfo {

    public static final int TYPE_ITEM_TEXT_OTHER = 0;//对方文本消息类型
    public static final int TYPE_ITEM_PHOTO_OTHER = 2;//对方图片消息类型
    public static final int TYPE_ITEM_VOICE_OTHER = 3;//对方语音消息类型
    public static final int TYPE_ITEM_VIDEO_OTHER = 4;//对方视频消息类型
    public static final int TYPE_ITEM_TEXT_SELF = 200; //自己文本消息类型
    public static final int TYPE_ITEM_PHOTO_SELF = 202;//自己图片消息类型
    public static final int TYPE_ITEM_VOICE_SELF = 203;//自己语音消息类型
    public static final int TYPE_ITEM_VIDEO_SELF = 204;//自己视频消息类型

    public static final int STATUS_LOADING = 1;
    public static final int STATUS_COMPLETE = 2;
    public static final int STATUS_ERROR = 3;
    private int fileStatus = STATUS_LOADING;
    private boolean isNeedScrollToBottom;
    private String identification;//唯一标识
    private boolean playing;
    private String thumbUrl;
    private String time;
    private String from;
    private String toId;
    private int fileType;

    public boolean isFirstLoadScrollToBottom() {
        return isFirstLoadScrollToBottom;
    }

    public void setFirstLoadScrollToBottom(boolean firstLoadScrollToBottom) {
        isFirstLoadScrollToBottom = firstLoadScrollToBottom;
    }

    private boolean isFirstLoadScrollToBottom;
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public int getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(int fileStatus) {
        this.fileStatus = fileStatus;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    private String mid;//messageId

    public boolean isNeedScrollToBottom() {
        return isNeedScrollToBottom;
    }


    public void setNeedScrollToBottom(boolean needScrollToBottom) {
        isNeedScrollToBottom = needScrollToBottom;
    }

    private int itemType;
    private String fAvatar;
    private String tAvatar;

    public String getfAvatar() {
        return fAvatar;
    }

    public void setfAvatar(String fAvatar) {
        this.fAvatar = fAvatar;
    }

    public String gettAvatar() {
        return tAvatar;
    }

    public void settAvatar(String tAvatar) {
        this.tAvatar = tAvatar;
    }



    public int getItemType() {
        String mUserId = MyApplication.userSP.readUid();
        if (!TextUtils.isEmpty(mUserId) && mUserId.equals(from)) {
            itemType = fileType+ 200;
        } else {
            itemType = fileType ;
        }
        return itemType;
    }


    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
