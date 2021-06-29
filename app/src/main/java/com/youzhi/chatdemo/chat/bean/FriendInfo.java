package com.youzhi.chatdemo.chat.bean;

public class FriendInfo {

    /**
     * lastMsg : 22
     * lastTime : 1591783502746
     * lastTimestr : 18:05
     * me : 5ee03be1e7012e50c04ca84c
     * nick : 2321
     * num : 0
     * online : false
     * read : 1
     * sessionId : 5ee03be1e7012e50c04ca84c5ee0a9001bdec82c5047ad67
     * toId : 3
     * toUserId : 5ee0a9001bdec82c5047ad67
     * type : 0
     */
    private String avatar;
    private String lastMsg;
    private long lastTime;
    private String lastTimestr;
    private String me;
    private String nick;
    private int num;
    private boolean online;
    private int read;
    private String sessionId;
    private String toId;
    private String toUserId;
    private int type;
    private String fileType;
    private String id;
    private int isTop;

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastTimestr() {
        return lastTimestr;
    }

    public void setLastTimestr(String lastTimestr) {
        this.lastTimestr = lastTimestr;
    }

    public String getMe() {
        return me;
    }

    public void setMe(String me) {
        this.me = me;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
