package com.youzhi.chatdemo.utils;

import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.youzhi.chatdemo.bean.BaseBean;
import com.youzhi.chatdemo.constant.AppConst;
import com.youzhi.chatdemo.constant.NetResultConstants;


/**
 * @author gaoyuheng
 * @description: 对OkGo框架返回结果进行二次封装
 * @date :2020/9/1 10:05
 */
public class MyOkGo {


    public static PostRequest<String> getPostRequest(String requestUrl, Object tag) {

        PostRequest<String> post = OkGo.<String>post(AppConst.appUrl() + requestUrl).tag(tag);
//        if (MyApplication.userSP != null) {
//            String token = MyApplication.userSP.readUserToken();
//            if (!TextUtils.isEmpty(token)) {
//                post.headers("token", token);
//            }
//
//        }
        return post;

    }

    public static PostRequest<String> getPostRequest(String requestUrl, Object tag, boolean isAllUrl) {

        PostRequest<String> post = OkGo.<String>post(isAllUrl ? requestUrl : AppConst.appUrl() + requestUrl).tag(tag);
//        if (MyApplication.UserPF != null) {
//            String token = MyApplication.userSP.readUserToken();
//            if (!TextUtils.isEmpty(token)) {
//                post.headers("token", token);
//            }
//
//        }
        return post;

    }

    public static void send(PostRequest<String> post, NetResultCallback netResultCallback, BaseBean baseBean) {
        if (post == null) return;
        post.execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {

                if (response == null) {
                    netResultCallback.onError(-1, "response为空", post.getUrl());
                    return;
                }
                if (!response.isSuccessful()) {
                    netResultCallback.onError(response.code(), response.message(), post.getUrl());
                    return;
                }
                String result = response.body();

                if (TextUtils.isEmpty(result)) {
                    netResultCallback.onError(response.code(), "result为null", post.getUrl());
                    return;
                }


                int status = FastJsonUtils.getJsonInt(result, NetResultConstants.CODE);

                String msg = FastJsonUtils.getStr(result, NetResultConstants.MSG);


                switch (status) {
                    case NetResultConstants.STATUS_SUCCESS:
                        //成功
                        BaseBean bean = null;
                        try {
                            bean = FastJsonUtils.parseObject(result, baseBean.getClass());
                        } catch (Exception e) {
                            netResultCallback.onError(status, "数据解析异常", post.getUrl());
                        }
                        if (bean != null) {
                            bean.setRequestUrl(post.getUrl());
                            netResultCallback.onSuccess(bean);
                        }

                        break;
//                    case NetResultConstants.STATUS_LOGIN_STALE_DATED:
//                        netResultCallback.onError(status, "登录过期，请重新登录", post.getUrl());
//                        LoginManager.otherLoginFoToast(ActivitysManager.getInstance().currentActivity(), "登录过期，请重新登录");
//                        break;
//                    case NetResultConstants.STATUS_LOGIN_OTHER:
//                        netResultCallback.onError(status, "您的账号在其他地方登录", post.getUrl());
//                        LoginManager.otherLoginFoToast(ActivitysManager.getInstance().currentActivity(), "您的账号在其他地方登录");
//                        break;
//                    case NetResultConstants.STATUS_JURISDICTION_CHANGE:
//                        netResultCallback.onError(status, "您的权限变更，请重新登录", post.getUrl());
//                        LoginManager.otherLoginFoToast(ActivitysManager.getInstance().currentActivity(), "您的权限变更，请重新登录");
//                        break;
//                    default:
//                        netResultCallback.onError(status, msg, post.getUrl());
//                        LoginManager.forceOutLogToLogIn(ActivitysManager.getInstance().currentActivity(), msg);
//                        break;
                }

            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);

                int status = -1;
                if (response != null) {
                    status = response.code();
                }
                netResultCallback.onError(status, response.message(), post.getUrl());
            }
        });

    }


    public interface NetResultCallback {
        void onSuccess(BaseBean baseBean);

        void onError(int status, String message, String url);
    }
}
