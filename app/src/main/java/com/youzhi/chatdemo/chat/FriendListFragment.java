package com.youzhi.chatdemo.chat;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.youzhi.chatdemo.BaseFragment;
import com.youzhi.chatdemo.MyApplication;
import com.youzhi.chatdemo.R;
import com.youzhi.chatdemo.TitleBar;
import com.youzhi.chatdemo.adapter.RecyclerBaseAdapter;
import com.youzhi.chatdemo.adapter.rv_adapter.OnRecyclerItemListener;
import com.youzhi.chatdemo.bean.BaseBean;
import com.youzhi.chatdemo.bean.UserInfoBean;
import com.youzhi.chatdemo.chat.adapter.FriendListAdapter;
import com.youzhi.chatdemo.chat.bean.FriendInfo;
import com.youzhi.chatdemo.chat.bean.FriendListResult;
import com.youzhi.chatdemo.chat.socket.SocketConstant;
import com.youzhi.chatdemo.chat.socket.SocketManager;
import com.youzhi.chatdemo.chat.utils.popuwindow.CommonPopuUtils;
import com.youzhi.chatdemo.chat.utils.popuwindow.CommonPopupWindow;
import com.youzhi.chatdemo.constant.AppConst;
import com.youzhi.chatdemo.constant.IntentKey;
import com.youzhi.chatdemo.utils.FastJsonUtils;
import com.youzhi.chatdemo.utils.MyOkGo;
import com.youzhi.chatdemo.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.youzhi.chatdemo.chat.socket.SocketConstant.SERVER_CHANNEL;


public class FriendListFragment extends BaseFragment implements OnRecyclerItemListener, CommonPopupWindow.ViewInterface, View.OnClickListener, MyOkGo.NetResultCallback, OnRefreshLoadMoreListener {
    @BindView(R.id.rv_friend_list)
    RecyclerView rvFriendList;
    @BindView(R.id.tb_title)
    TitleBar tbTitle;
    @BindView(R.id.srl_friend_list)
    SmartRefreshLayout srlFriendList;
    private FriendListAdapter friendListAdapter;
    String[] channelName = {Socket.EVENT_CONNECT, Socket.EVENT_DISCONNECT, Socket.EVENT_CONNECT_ERROR, Socket.EVENT_CONNECT_TIMEOUT, SERVER_CHANNEL};
    private ConnectListener connectListener;
    private DisConnectListener disConnectListener;
    private ConnectErrorListener connectErrorListener;
    private ConnectTimeOutListener connectTimeOutListener;
    private ServerMessageListener serverMessageListener;
    private CommonPopupWindow commonPopupWindow;
    private EditText et_content;
    boolean isShowHint; //标识是否需要提示sokcet连接错误

    @Override
    public int getLayout() {
        return R.layout.fragment_firend_list;
    }

    @Override
    public void initView(View view) {
        friendListAdapter = new FriendListAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvFriendList.setLayoutManager(linearLayoutManager);
        rvFriendList.setAdapter(friendListAdapter);
        friendListAdapter.setOnItemClickListener(this);

        tbTitle.setRightTvOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                commonPopupWindow = CommonPopuUtils.showPop(FriendListFragment.this.getActivity(), ViewGroup.LayoutParams.WRAP_CONTENT, R.layout.layout_common_input_popu, Gravity.CENTER, FriendListFragment.this);
            }
        });
        srlFriendList.setOnRefreshLoadMoreListener(this);
    }

    @Override
    public void initData() {
        initSocket();

    }

    @Override
    public void onResume() {
        super.onResume();
        isShowHint = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isShowHint = false;
    }

    private void initSocket() {
        connectListener = new ConnectListener();
        disConnectListener = new DisConnectListener();
        connectErrorListener = new ConnectErrorListener();
        connectTimeOutListener = new ConnectTimeOutListener();
        serverMessageListener = new ServerMessageListener();
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.openChannel(channelName, connectListener, disConnectListener, connectErrorListener, connectTimeOutListener, serverMessageListener);
        socketManager.connect();
    }

    @Override
    public void onItemClick(RecyclerBaseAdapter recyclerBaseAdapter, View v, Object data) {
        if (data instanceof FriendInfo) {
            FriendInfo friendInfo = (FriendInfo) data;
            CommonChatActivity.startMe(this, friendInfo.getToUserId(), friendInfo.getSessionId(), friendInfo.getNick());

        }

    }

    @Override
    public void getChildView(View view, int layoutResId) {
        et_content = view.findViewById(R.id.et_content);
        View iv_close = view.findViewById(R.id.iv_close);
        Button bt_save = view.findViewById(R.id.bt_save);
        iv_close.setOnClickListener(this);
        bt_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                String content = et_content.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showShort("请输入对方账号");
                    return;
                }
                getUserInfo();
                break;
        }
        if (commonPopupWindow != null && commonPopupWindow.isShowing()) {
            commonPopupWindow.dismiss();
        }
    }

    private void getUserInfo() {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("siteId", et_content.getText().toString());
        stringMap.put("mUserId", MyApplication.userSP.readUid());
        stringMap.put("companyId", MyApplication.userSP.readCode());
        MyOkGo.send(MyOkGo.getPostRequest(AppConst.GET_USER_INFO, this).upJson(FastJsonUtils.toJSONString(stringMap))
                , this, new UserInfoBean());
    }

    @Override
    public void onSuccess(BaseBean baseBean) {
        if (baseBean instanceof UserInfoBean) {
            UserInfoBean userInfoBean = (UserInfoBean) baseBean;
            UserInfoBean.UserInfo user = userInfoBean.getUser();
            if (user == null) {
                ToastUtil.showShort("没有该用户信息");
                return;
            }
//            Intent intent = new Intent(getActivity(), CommonChatActivity.class);
//            intent.putExtra(IntentKey.KEY_ID, user.getId());
//            intent.putExtra(IntentKey.KEY_SESSION_ID, userInfoBean.getSessionId());
//            intent.putExtra(IntentKey.KEY_NAME, user.getName());
//            startActivity(intent);
            CommonChatActivity.startMe(this, user.getId(), userInfoBean.getSessionId(), user.getName());
        }
    }


    @Override
    public void onError(int status, String message, String url) {

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (SocketManager.getInstance().isConnected()) {
            SocketManager.getInstance().sendGetFriendListMessage();
        }
    }

    /*=============================Socket回调部分==================================*/
    //服务通道新消息回调
    public class ServerMessageListener implements Emitter.Listener {


        @Override
        public void call(Object... args) {

            FriendListFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FriendListResult messageResult = SocketManager.getInstance().getMessageResult(FriendListResult.class, args);
                    if (messageResult == null) {
                        return;
                    }
                    if (TextUtils.equals(messageResult.getCmd() + "", SocketConstant.FRIEND_LIST)) {

                        FriendListResult.BodyInfo body = messageResult.getBody();
                        if (body == null) {
                            return;
                        }
                        String contacts = body.getContacts();
                        if (!TextUtils.isEmpty(contacts)) {
                            List<FriendInfo> friendInfos = FastJsonUtils.getObjectsList(contacts, FriendInfo.class);
                            if (friendListAdapter != null) {
                                friendListAdapter.refreshData(friendInfos);
                            }

                        }
                        if (srlFriendList != null && srlFriendList.isRefreshing()) {
                            srlFriendList.finishRefresh();
                        }
                    } else if (TextUtils.equals(messageResult.getCmd() + "", SocketConstant.RECEIVE_MESSAGE)) {
                        SocketManager.getInstance().sendGetFriendListMessage();
                    }
                }
            });
        }
    }


    //连接回调
    public class ConnectListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if (srl_friend_list != null && srl_friend_list.isRefreshing()) {
//                        srl_friend_list.finishRefresh();
//                    }
                    Log.e("LOGCAT", "ConnectListener");
                    SocketManager.getInstance().sendGetFriendListMessage();
                    if (srlFriendList != null && srlFriendList.isRefreshing()) {
                        srlFriendList.finishRefresh();
                    }
                }
            });
        }
    }

    //关闭连接回调
    public class DisConnectListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isShowHint) {
                        ToastUtil.showShort("连接已关闭");
                    }
//                    ViewUtils.setListViewShowOrHide(recycler, layout_not_data, adapter.getDatas().size() > 0);
//                    if (srl_friend_list != null && srl_friend_list.isRefreshing()) {
//                        srl_friend_list.finishRefresh();
//                    }
                    if (srlFriendList != null && srlFriendList.isRefreshing()) {
                        srlFriendList.finishRefresh();
                    }
                }
            });

        }
    }

    //连接错误回调
    public class ConnectErrorListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (isShowHint) {
                        ToastUtil.showShort("连接错误");
                    }
//
//                    if (srl_friend_list != null && srl_friend_list.isRefreshing()) {
//                        srl_friend_list.finishRefresh();
//                    }
//                    ViewUtils.setListViewShowOrHide(recycler, layout_not_data, adapter.getDatas().size() > 0);
                    if (srlFriendList != null && srlFriendList.isRefreshing()) {
                        srlFriendList.finishRefresh();
                    }
                }
            });

        }
    }


    //连接超时回调
    public class ConnectTimeOutListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if (srl_friend_list != null && srl_friend_list.isRefreshing()) {
//                        srl_friend_list.finishRefresh();
//                    }
//                    ViewUtils.setListViewShowOrHide(recycler, layout_not_data, adapter.getDatas().size() > 0);
                    ToastUtil.showShort("连接超时");
                    if (srlFriendList != null && srlFriendList.isRefreshing()) {
                        srlFriendList.finishRefresh();
                    }
                }
            });

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketManager.getInstance().closeChannel(channelName, connectListener, disConnectListener, connectErrorListener, connectTimeOutListener, serverMessageListener);
        SocketManager.getInstance().destroySocket();
    }
}
