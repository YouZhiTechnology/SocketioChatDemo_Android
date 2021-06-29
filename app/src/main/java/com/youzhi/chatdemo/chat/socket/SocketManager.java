package com.youzhi.chatdemo.chat.socket;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.youzhi.chatdemo.MyApplication;
import com.youzhi.chatdemo.constant.AppConst;
import com.youzhi.chatdemo.utils.FastJsonUtils;


import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.youzhi.chatdemo.chat.socket.SocketConstant.RECEIVE_MESSAGE;


/**
 * @author gaoyuheng
 * @description: SocketIo包装类，负责管理socket所有操作，
 * @date :2020/8/31 11:49
 */
public class SocketManager {
    private static SocketManager socketManager;
    private Socket mSocket;


    public static SocketManager getInstance() {
        if (socketManager == null) {
            synchronized (SocketManager.class) {
                if (socketManager == null) {
                    socketManager = new SocketManager();
                }
            }
        }
        return socketManager;
    }

    //初始化socket并包含一些请求连接的参数
    public void initSocketIo(String query) {
        try {
//            Log.e("LOGCAT", "quesry====" + quesry);
            IO.Options options = new IO.Options();

//            OkHttpClient build = new OkHttpClient.Builder().hostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            }).sslSocketFactory(SSLSocket.genSSLSocketFactory().getSocketFactory()).build();
//            IO.setDefaultOkHttpCallFactory(build);
//            IO.setDefaultOkHttpWebSocketFactory(build);
//            options.sslContext = SSLSocket.genSSLSocketFactory();
//            options.hostnameVerifier = new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            };

            options.transports = new String[]{"websocket", "xhr-polling", "jsonp-polling"};
            options.reconnection = true;
            options.forceNew = true;
            options.upgrade = true;
            options.timeout = -1;
            options.reconnectionAttempts = 100;
            options.reconnectionDelay = 3000;
            options.reconnectionDelayMax = 3000;
            options.query = query;
            mSocket = IO.socket(AppConst.getSocketUrl(), options);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //发送获取联系人列表消息
    public void sendGetFriendListMessage() {
        sendMessageNormal(SocketConstant.MESSAGE_TYPE_FRIEND_LIST);
    }

    //发送聊天界面消息列表消息
    public void sendChatListMessage(String sessionId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(SocketConstant.SESSION_ID, sessionId);
        sendMessageForParam(SocketConstant.MESSAGE_TYPE_CHAT_MESSAGE, paramMap);
    }

    public void destroySocket() {
        SocketManager.getInstance().disConnect();
        if (mSocket != null) {
            mSocket = null;
        }
    }

    public boolean isNullResult(Object... args) {
        if (args == null || args.length <= 0) {
            return true;
        }

        Object result = args[0];

        if (result == null) {
            return true;
        }
        return false;
    }

    //判断是否有这个监听
    public boolean hasListener(String event) {
        if (mSocket != null) {
            return mSocket.hasListeners(event);
        }
        return false;
    }

    public void sendChatMessage(String toId, String fileType, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("toId", toId);
        map.put("msg", msg);
        map.put("fileType", fileType);
        map.put("companyId", MyApplication.userSP.readCode());
        map.put("mUserId", MyApplication.userSP.readUid());

//        Api.receiveMessage, map));
        sendMessageForParam(RECEIVE_MESSAGE, map);
    }

    //根据消息接受回调解析消息返回结果
    public <T> T getMessageResult(Class<T> clazz, Object... args) {

        if (args == null || args.length <= 0) {
            return null;
        }

        Object result = args[0];
        if (result == null) {
            return null;
        }
        try {
            String resultString = result.toString();
            if (TextUtils.isEmpty(resultString)) {
                return null;
            }
            Log.e("LOGCAT",resultString);
            return FastJsonUtils.parseObject(resultString, clazz);

        } catch (Exception e) {
            return null;
        }

    }

    //开启通道通道
    public void openChannel(String[] channelName, Emitter.Listener... listeners) {
        if (mSocket == null) {
            String query = "siteId= " + MyApplication.userSP.readUid() + "&companyId=m53vyg4j&mUserId=" + MyApplication.userSP.readUid() + "&EIO=3&transport=websocket&deviceType=android";
            initSocketIo(query);
        }
        if (mSocket == null) {
            return;
        }

        if (channelName == null || listeners == null) {
            return;
        }
        int openCount = Math.min(channelName.length, listeners.length);

        for (int i = 0; i < openCount; i++) {
            mSocket.on(channelName[i], listeners[i]);
        }
    }

    //关闭通道
    public void closeChannel(String[] channelName, Emitter.Listener... listeners) {
        if (mSocket == null) {
            return;
        }
        if (channelName == null || listeners == null) {
            return;
        }

        int openCount = Math.min(channelName.length, listeners.length);

        for (int i = 0; i < openCount; i++) {
            mSocket.off(channelName[i], listeners[i]);
        }
    }

    public void sendMessage(String event, Object... args) {
        if (mSocket != null) {
            mSocket.emit(event, args);
        }
    }

    //发送携带参数的消息
    public void sendMessageForParam(String messageType, Map<String, Object> paramMap) {
        try {
            Map<String, Object> exotheciumMap = new HashMap<>();
            if (paramMap != null && paramMap.size() > 0) {
                exotheciumMap.put(SocketConstant.PARAM, FastJsonUtils.postJson(paramMap));
            }
            exotheciumMap.put(SocketConstant.SERVER_CHANNEL, messageType);
            sendMessage(SocketConstant.SERVER_CHANNEL, FastJsonUtils.postJson(exotheciumMap));
        } catch (Exception e) {

        }

    }

    //发送不带参数的消息
    public void sendMessageNormal(String messageType) {
        sendMessageForParam(messageType, null);
    }

    //发送文本消息
    public void sendTextMessage() {
        sendJsonMessage(null);
    }

    //发送图片
    public void sendPictureMessage() {
        sendJsonMessage(null);
    }

    //发送语音消息
    public void sendAudioMessage() {
        sendJsonMessage(null);
    }

    /*最终发送json消息*/
    public void sendJsonMessage(SocketBaseInfo param) {

        try {
            if (param != null) {

                String textJson = JSONObject.toJSONString(param);
                sendMessage(SocketConstant.CLIENT_CHANNEL, textJson);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(final String event, final Object[] args, final Ack ack) {
        if (mSocket != null) {
            mSocket.emit(event, args, ack);
        }
    }

    //连接
    public void connect() {
        if (mSocket != null) {
            mSocket.connect();
        }
    }

    //判断socket是否在连接
    public boolean isConnected() {
        if (mSocket != null) {
            return mSocket.connected();
        }
        return false;
    }

    //关闭连接
    public void disConnect() {
        if (mSocket != null) {
            mSocket.disconnect();
        }
    }

    public static class SSLSocket {
        public static SSLContext genSSLSocketFactory() {
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        X509Certificate[] x509Certificates = new X509Certificate[0];
                        return x509Certificates;
                    }
                }}, new SecureRandom());
                return sc;
            } catch (Exception localException) {
//            LogHelper.e("SSLSocketFactory -> " + localException.getMessage());
            }
            return null;
        }
    }
}
