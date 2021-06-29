package com.youzhi.chatdemo.chat.socket;

/**
 * @author gaoyuheng
 * @description:
 * @date :2020/12/18 13:41
 */
public class SocketBaseInfo {

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
    //消息类型
    private int  messageType;

}
