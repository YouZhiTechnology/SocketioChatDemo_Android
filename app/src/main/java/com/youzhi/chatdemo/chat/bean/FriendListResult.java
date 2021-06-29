package com.youzhi.chatdemo.chat.bean;

public class FriendListResult  {


    /**
     * cmd : 31
     * time : 109856546828
     * scope : id
     * to :
     * mid : Rot9VcalFePWL8zejJpfU2
     * exclude :
     * body : {"count":"2","contacts":"[{\"avatar\":\"https://customer-service-bucket.oss-cn-qingdao.aliyuncs.com/hly_setting/personalIcon/2blue.png\",\"fileType\":0,\"id\":\"60d424edb5016f172b99a133\",\"isTop\":0,\"lastMsg\":\"图\",\"lastTime\":1624591149406,\"lastTimestr\":\"11:19\",\"me\":\"60d2afc6b5016f03c899fca7\",\"nick\":\"用户-72e1635456\",\"num\":0,\"online\":true,\"read\":1,\"sessionId\":\"60d2afc6b5016f03c899fca760d2e117b5016f172b99a0fb\",\"toId\":\"13512672048\",\"toUserId\":\"60d2e117b5016f172b99a0fb\",\"type\":0},{\"fileType\":0,\"id\":\"60d41fc6b5016f172b99a130\",\"isTop\":0,\"lastMsg\":\"健康\",\"lastTime\":1624514502585,\"lastTimestr\":\"06-24 14:01\",\"me\":\"60d2afc6b5016f03c899fca7\",\"num\":0,\"online\":false,\"read\":1,\"sessionId\":\"60d2afc6b5016f03c899fca760d41fbdb5016f172b99a12e\",\"toId\":\"太累了\",\"toUserId\":\"60d41fbdb5016f172b99a12e\",\"type\":0}]"}
     */

    private int cmd;
    private long time;
    private String scope;
    private String to;
    private String mid;
    private String exclude;
    private BodyInfo body;

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

    public BodyInfo getBody() {
        return body;
    }

    public void setBody(BodyInfo body) {
        this.body = body;
    }

    public static class BodyInfo {
        /**
         * count : 2
         * contacts : [{"avatar":"https://customer-service-bucket.oss-cn-qingdao.aliyuncs.com/hly_setting/personalIcon/2blue.png","fileType":0,"id":"60d424edb5016f172b99a133","isTop":0,"lastMsg":"图","lastTime":1624591149406,"lastTimestr":"11:19","me":"60d2afc6b5016f03c899fca7","nick":"用户-72e1635456","num":0,"online":true,"read":1,"sessionId":"60d2afc6b5016f03c899fca760d2e117b5016f172b99a0fb","toId":"13512672048","toUserId":"60d2e117b5016f172b99a0fb","type":0},{"fileType":0,"id":"60d41fc6b5016f172b99a130","isTop":0,"lastMsg":"健康","lastTime":1624514502585,"lastTimestr":"06-24 14:01","me":"60d2afc6b5016f03c899fca7","num":0,"online":false,"read":1,"sessionId":"60d2afc6b5016f03c899fca760d41fbdb5016f172b99a12e","toId":"太累了","toUserId":"60d41fbdb5016f172b99a12e","type":0}]
         */

        private String count;
        private String contacts;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getContacts() {
            return contacts;
        }

        public void setContacts(String contacts) {
            this.contacts = contacts;
        }
    }
}
