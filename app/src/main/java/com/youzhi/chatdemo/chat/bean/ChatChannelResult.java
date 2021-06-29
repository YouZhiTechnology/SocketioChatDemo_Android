package com.youzhi.chatdemo.chat.bean;

public class ChatChannelResult {

    /**
     * cmd : 5
     * time : 109778502568
     * scope : id
     * to : 60d41fbdb5016f172b99a12e,60d2afc6b5016f03c899fca7
     * mid : n_3roR4THiiXXTj_tZfkB1
     * exclude :
     * body : {"msg":"健康","toId":"60d41fbdb5016f172b99a12e","tNick":"","ctimestr":"2021-06-24 14:01","ctime":"1624514502575","siteId":"15046497472","from":"60d2afc6b5016f03c899fca7","sessionId":"60d2afc6b5016f03c899fca760d41fbdb5016f172b99a12e","fNick":"用户-e5c0a5dcef","fAvatar":"https://customer-service-bucket.oss-cn-qingdao.aliyuncs.com/hly_setting/personalIcon/2blue.png","fileType":"0"}
     */

    private String cmd;
    private long time;
    private String scope;
    private String to;
    private String mid;
    private String exclude;
    private BodyInfo body;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    public BodyInfo getBody() {
        return body;
    }

    public void setBody(BodyInfo body) {
        this.body = body;
    }

    public static class BodyInfo {
        /**
         * msg : 健康
         * toId : 60d41fbdb5016f172b99a12e
         * tNick :
         * ctimestr : 2021-06-24 14:01
         * ctime : 1624514502575
         * siteId : 15046497472
         * from : 60d2afc6b5016f03c899fca7
         * sessionId : 60d2afc6b5016f03c899fca760d41fbdb5016f172b99a12e
         * fNick : 用户-e5c0a5dcef
         * fAvatar : https://customer-service-bucket.oss-cn-qingdao.aliyuncs.com/hly_setting/personalIcon/2blue.png
         * fileType : 0
         */

        private String msg;
        private String toId;
        private String tNick;
        private String ctimestr;
        private String ctime;
        private String siteId;
        private String from;
        private String sessionId;
        private String fNick;
        private String fAvatar;
        private int fileType;
        private String list;

        public String getList() {
            return list;
        }

        public void setList(String list) {
            this.list = list;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getToId() {
            return toId;
        }

        public void setToId(String toId) {
            this.toId = toId;
        }

        public String getTNick() {
            return tNick;
        }

        public void setTNick(String tNick) {
            this.tNick = tNick;
        }

        public String getCtimestr() {
            return ctimestr;
        }

        public void setCtimestr(String ctimestr) {
            this.ctimestr = ctimestr;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getSiteId() {
            return siteId;
        }

        public void setSiteId(String siteId) {
            this.siteId = siteId;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getFNick() {
            return fNick;
        }

        public void setFNick(String fNick) {
            this.fNick = fNick;
        }

        public String getFAvatar() {
            return fAvatar;
        }

        public void setFAvatar(String fAvatar) {
            this.fAvatar = fAvatar;
        }

        public int getFileType() {
            return fileType;
        }

        public void setFileType(int fileType) {
            this.fileType = fileType;
        }
    }
}
