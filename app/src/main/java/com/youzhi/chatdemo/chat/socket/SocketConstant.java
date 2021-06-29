package com.youzhi.chatdemo.chat.socket;

/**
 * @author gaoyuheng
 * @description:
 * @date : 5/20/21 11:46 AM.
 */
public interface SocketConstant {

    /*-----------------------------------自定义通道--------------------*/
    //客户端发送消息通道
    String CLIENT_CHANNEL = "client_channel";
    //接受服务端通道
    String SERVER_CHANNEL = "cmd";
    //参数key
    String PARAM = "param";
    String SESSION_ID = "sessionId";
    //获取消息列表消息类型
    String MESSAGE_TYPE_FRIEND_LIST = "31";
    //获取聊天消息列表
    String MESSAGE_TYPE_CHAT_MESSAGE = "33";

    String AUDIO = "3";
    String TEXT = "0";
    String PICTURE = "2";
    String VIDEO="4";
    String AUDIO_TIME = "&time=";
    //    好友列表
    String FRIEND_LIST = "31";
    //    收到消息
    String RECEIVE_MESSAGE = "5";
    //    聊天消息列表
    String CHAT_MESSAGE_LIST = "33";
}
