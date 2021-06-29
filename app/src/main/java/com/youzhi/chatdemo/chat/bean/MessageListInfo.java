package com.youzhi.chatdemo.chat.bean;

public class MessageListInfo {


    /**
     * cmd : 31
     * time : 109693355531
     * scope : id
     * to :
     * mid : IYNOEv1FHAqpqyO_VVc8X1
     * exclude :
     * body : {"count":"0","contacts":"[]"}
     */

    private int cmd;
    private long time;
    private String scope;
    private String to;
    private String mid;
    private String exclude;
    private BodyBean body;

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
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

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean {
        /**
         * count : 0
         * contacts : []
         */

        private String count;
        private Object contacts;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public Object getContacts() {
            return contacts;
        }

        public void setContacts(Object contacts) {
            this.contacts = contacts;
        }
    }
}
