package com.youzhi.chatdemo.bean;

public class UserInfoBean extends BaseBean {


    /**
     * code : 200
     * user : {"_id":"60d2e117b5016f172b99a0fb","siteId":"13512672048","companyId":"m53vyg4j","name":"用户-72e1635456","ip":null,"avatar":"https://customer-service-bucket.oss-cn-qingdao.aliyuncs.com/hly_setting/personalIcon/2blue.png","intime":null,"pushType":null,"cltype":1,"pushId":null,"lastLogin":"2021-06-23T07:21:59.921+0000","lastUpdate":"2021-06-23T07:21:59.921+0000","enablePush":true}
     */


    private UserInfo user;
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public static class UserInfo {
        /**
         * _id : 60d2e117b5016f172b99a0fb
         * siteId : 13512672048
         * companyId : m53vyg4j
         * name : 用户-72e1635456
         * ip : null
         * avatar : https://customer-service-bucket.oss-cn-qingdao.aliyuncs.com/hly_setting/personalIcon/2blue.png
         * intime : null
         * pushType : null
         * cltype : 1
         * pushId : null
         * lastLogin : 2021-06-23T07:21:59.921+0000
         * lastUpdate : 2021-06-23T07:21:59.921+0000
         * enablePush : true
         */

        private String id;
        private String siteId;
        private String companyId;
        private String name;
        private Object ip;
        private String avatar;
        private Object intime;
        private Object pushType;
        private int cltype;
        private Object pushId;
        private String lastLogin;
        private String lastUpdate;
        private boolean enablePush;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSiteId() {
            return siteId;
        }

        public void setSiteId(String siteId) {
            this.siteId = siteId;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getIp() {
            return ip;
        }

        public void setIp(Object ip) {
            this.ip = ip;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Object getIntime() {
            return intime;
        }

        public void setIntime(Object intime) {
            this.intime = intime;
        }

        public Object getPushType() {
            return pushType;
        }

        public void setPushType(Object pushType) {
            this.pushType = pushType;
        }

        public int getCltype() {
            return cltype;
        }

        public void setCltype(int cltype) {
            this.cltype = cltype;
        }

        public Object getPushId() {
            return pushId;
        }

        public void setPushId(Object pushId) {
            this.pushId = pushId;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }

        public String getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(String lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public boolean isEnablePush() {
            return enablePush;
        }

        public void setEnablePush(boolean enablePush) {
            this.enablePush = enablePush;
        }
    }
}
