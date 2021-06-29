package com.youzhi.chatdemo.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.lzy.okgo.request.PostRequest;
import com.permissionx.guolindev.callback.RequestCallback;
import com.youzhi.chatdemo.BaseActivity;
import com.youzhi.chatdemo.MyApplication;
import com.youzhi.chatdemo.R;
import com.youzhi.chatdemo.TitleBar;
import com.youzhi.chatdemo.bean.BaseBean;
import com.youzhi.chatdemo.bean.UploadFileBean;
import com.youzhi.chatdemo.chat.adapter.ChatAdapter;
import com.youzhi.chatdemo.chat.adapter.PanelMenuAdapter;
import com.youzhi.chatdemo.adapter.RecyclerBaseAdapter;
import com.youzhi.chatdemo.adapter.rv_adapter.OnRecyclerItemListener;
import com.youzhi.chatdemo.chat.audio.AudioRecordManager;
import com.youzhi.chatdemo.chat.audio.SendVoiceHelp;
import com.youzhi.chatdemo.chat.bean.ChatChannelResult;
import com.youzhi.chatdemo.chat.bean.ChatMessageInfo;
import com.youzhi.chatdemo.chat.bean.MenuInfo;
import com.youzhi.chatdemo.chat.socket.SocketConstant;
import com.youzhi.chatdemo.chat.socket.SocketManager;
import com.youzhi.chatdemo.chat.utils.AnimationUtil;
import com.youzhi.chatdemo.chat.utils.MediaFileUtils;
import com.youzhi.chatdemo.chat.utils.popuwindow.CommonPopupWindow;
import com.youzhi.chatdemo.constant.AppConst;
import com.youzhi.chatdemo.constant.IntentKey;
import com.youzhi.chatdemo.utils.FastJsonUtils;
import com.youzhi.chatdemo.utils.FileUtil;
import com.youzhi.chatdemo.chat.utils.InputMethodUtils;
import com.youzhi.chatdemo.utils.MatisseChooseUtils;
import com.youzhi.chatdemo.utils.MyOkGo;
import com.youzhi.chatdemo.utils.PermissionXUtils;
import com.youzhi.chatdemo.utils.QMUIUtils;
import com.youzhi.chatdemo.utils.RxJavaUtils;
import com.youzhi.chatdemo.utils.ToastUtil;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.youzhi.chatdemo.chat.socket.SocketConstant.MESSAGE_TYPE_CHAT_MESSAGE;
import static com.youzhi.chatdemo.chat.socket.SocketConstant.RECEIVE_MESSAGE;
import static com.youzhi.chatdemo.chat.socket.SocketConstant.SERVER_CHANNEL;

/**
 * @author gaoyuheng
 * @description:
 * @date : 5/20/21 11:52 AM.
 */
public class CommonChatActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener, OnRecyclerItemListener, RequestCallback, SendVoiceHelp.OnInteractionListener, AudioRecordManager.OnRecordVolumeListener, MyOkGo.NetResultCallback {

    private static final int TYPE_UPLOAD_VOICE = 3;
    private static final int TYPE_UPLOAD_IMAGE = 2;
    @BindView(R.id.tb_title)
    TitleBar tbTitle;
    @BindView(R.id.rv_chat_list)
    RecyclerView rvChatList;
    @BindView(R.id.iv_voice_keyboard)
    ImageView ivVoiceKeyboard;
    @BindView(R.id.et_chat)
    EditText etChat;
    @BindView(R.id.tv_long_click)
    TextView tvLongClick;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.iv_send)
    ImageView ivSend;
    @BindView(R.id.fl_menu_layout)
    FrameLayout flMenuLayout;
    @BindView(R.id.rv_panel)
    RecyclerView rv_panel;
    @BindView(R.id.cl_voice_bg)
    View cl_voice_bg;
    @BindView(R.id.iv_voice_hint_pic)
    ImageView iv_voice_hint_pic;
    @BindView(R.id.tv_voice_hint_txt)
    TextView tv_voice_hint_txt;
    private LiveAudioOnItemTouchListener liveAudioOnItemTouchListener;
    private AnimationUtil animationUtil;
    private int initSoftInputHeight;//初始化得软键盘高度
    private View activityRootView;//界面根布局
    private static final String PREFERENCES_NAME = "softInputHeight";
    private static final String KEY_HEIGHT = "height";
    private SharedPreferences sharedPreferences;
    private boolean softInputShow;//表示软键盘是否弹出
    private DelayTask delayTask;//延迟任务
    private ViewTreeObserver viewTreeObserver;
    private int usableHeightPrevious; //记录得屏幕可见高度
    private PanelMenuAdapter panelMenuAdapter;//底部菜单面板适配器
    private ChatAdapter chatAdapter;//历史消息列表适配器
    private final int requestCodePictureSelected = 0x10; //发送图片进入图片选择器得请求码
    private LinearLayoutManager chatMessageLayoutManager;
    private boolean isSendVoice;
    private SendVoiceHelp sendVoiceHelp; //发送语音帮助类
    private int[] volumeImageIds; //音量图标数组
    /* 录音手势的三种状态*/
    private final int DISCARD = 1;//丢弃的
    private final int NORMAL = 2;//正常的
    private final int TOO_SHORT = 3;//短时间的
    private int voiceStatus = NORMAL;//页面出事的音频状态
    private long recordAudioTime;//音频的时间
    private Map<String, Integer> videoIndexMap;
    private CommonPopupWindow commonPopupWindow;
    private ConnectListener connectListener;
    private ServerMessageListener serverMessageListener;
    //定义socket通道名称
    String[] channelName = {Socket.EVENT_CONNECT, SERVER_CHANNEL};
    private String toId;
    private String sessionId;
    private int uploadType;


    public static void startMe(Activity context, String toId, String sessionId, String toName) {
        Intent intent = new Intent(context, CommonChatActivity.class);
        intent.putExtra(IntentKey.KEY_ID, toId);
        intent.putExtra(IntentKey.KEY_SESSION_ID, sessionId);
        intent.putExtra(IntentKey.KEY_NAME, toName);
        context.startActivity(intent);
    }

    public static void startMe(Fragment fragment, String toId, String sessionId, String toName) {
        Intent intent = new Intent(fragment.getActivity(), CommonChatActivity.class);
        intent.putExtra(IntentKey.KEY_ID, toId);
        intent.putExtra(IntentKey.KEY_SESSION_ID, sessionId);
        intent.putExtra(IntentKey.KEY_NAME, toName);
        fragment.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_common_chat;
    }

    @Override
    protected boolean isNeedImmerse() {
        return false;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        //这句话必须改为这种方式，不然即便是中间滚动视图下面的View也不会随键盘顶上去
        setStatusBarColorWithFitSystem(R.color.color_white, true, true);
        tbTitle.setNotStatusBar();
        //先从本地取软键盘高度,没有给定默认高度值，后面会获取软键盘高度进行存储
        initSoftInputHeight = getNativeSoftHeight();
        liveAudioOnItemTouchListener = new LiveAudioOnItemTouchListener(this);
        rvChatList.addOnItemTouchListener(liveAudioOnItemTouchListener);

        //获取聊天界面动画工具类实例
        animationUtil = AnimationUtil.getInstance();
        animationUtil.bindRecyclerViewLinkage(rvChatList);
        etChat.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (isShow(flMenuLayout)) {
                        softInputShow = true;
                        hidePanel();
                    }
                }
                return false;
            }
        });
        //聊天得对方id
        toId = getIntent().getStringExtra(IntentKey.KEY_ID);
        //对方姓名
        String name = getIntent().getStringExtra(IntentKey.KEY_NAME);
        tbTitle.settitleContent("与" + name + "进行聊天");
        //回话id用来获取历史聊天
        sessionId = getIntent().getStringExtra(IntentKey.KEY_SESSION_ID);
        //构建面板隐藏延时任务，为了更优化软键盘弹出体验
        delayTask = new DelayTask(this);
        //初始化软键盘改变监听
        initSoftInputListener();
        //初始化功能面板菜单
        initPanelMenu();
        //初始化历史消息列表
        initMessageList();
        //初始化长按语音发送帮助类
        initVoiceHelp();
        //初始化Socket
        initSocket();
        rvChatList.setItemAnimator(null);
    }


    private void initSocket() {
        connectListener = new ConnectListener();
        serverMessageListener = new ServerMessageListener();
        SocketManager.getInstance().openChannel(channelName, connectListener, serverMessageListener);
        if (!SocketManager.getInstance().isConnected()) {
            SocketManager.getInstance().connect();
        } else {
            initMessageData();
        }
    }

    //获取消息列表
    private void initMessageData() {
        SocketManager.getInstance().sendChatListMessage(sessionId);
    }

    @Override
    public void onSuccess(BaseBean baseBean) {
        dismissLoadingView();
        if (baseBean instanceof UploadFileBean) {
            UploadFileBean uploadFileBean = (UploadFileBean) baseBean;
            String fileUrl = uploadFileBean.getUrl();
            if (TextUtils.isEmpty(fileUrl)) {
                ToastUtil.showShort("发送文件失败");
                return;
            }
            String AllFileUrl = AppConst.getSocketUrl() + fileUrl;
            switch (uploadType) {
                case TYPE_UPLOAD_IMAGE:
                    sendImageMessage(AllFileUrl);
                    break;
                case TYPE_UPLOAD_VOICE:
                    sendAudioMessage(AllFileUrl);
                    break;
            }
        }


    }

    @Override
    public void onError(int status, String message, String url) {
        dismissLoadingView();
    }


    //连接回调
    public class ConnectListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            CommonChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initMessageData();

                }
            });
        }
    }

    public class ServerMessageListener implements Emitter.Listener {


        @Override
        public void call(Object... args) {
            ChatChannelResult messageResult = SocketManager.getInstance().getMessageResult(ChatChannelResult.class, args);

            if (messageResult == null) {
                return;
            }
            CommonChatActivity.this.runOnUiThread(new Runnable() {


                @Override
                public void run() {
                    ChatChannelResult.BodyInfo body = messageResult.getBody();
                    if (body == null) {
                        return;
                    }
                    switch (messageResult.getCmd()) {
                        case RECEIVE_MESSAGE:
                            ChatMessageInfo chatMessageInfo = new ChatMessageInfo();
                            chatMessageInfo.setFileType(body.getFileType());
                            chatMessageInfo.setFrom(body.getFrom());
                            chatMessageInfo.setToId(body.getToId());
                            chatMessageInfo.setMsg(body.getMsg());
                            chatMessageInfo.setNeedScrollToBottom(true);
                            chatAdapter.add(chatMessageInfo);
                            etChat.setText("");
                            notifyScrollToBottom();
                            break;
                        case MESSAGE_TYPE_CHAT_MESSAGE:
                            String list = body.getList();
                            if (TextUtils.isEmpty(list)) {
                                return;
                            }
                            List<ChatMessageInfo> chatMessageInfos = FastJsonUtils.getObjectsList(list, ChatMessageInfo.class);
                            if (chatMessageInfos != null && chatMessageInfos.size() > 0) {
                                ChatMessageInfo lastMessageInfo = chatMessageInfos.get(chatMessageInfos.size() - 1);
                                lastMessageInfo.setNeedScrollToBottom(true);
                                chatAdapter.refreshData(chatMessageInfos);
                                notifyScrollToBottom();
                            }
                            break;

                        default:
                            break;
                    }
                }
            });


        }
    }

    private void initVoiceHelp() {
        volumeImageIds = new int[]{R.mipmap.icon_recording_1, R.mipmap.icon_recording_2, R.mipmap.icon_recording_3,
                R.mipmap.icon_recording_4, R.mipmap.icon_recording_5, R.mipmap.icon_recording_6, R.mipmap.icon_recording_7,
                R.mipmap.icon_recording_8, R.mipmap.icon_recording_9, R.mipmap.icon_recording_10
        };
        sendVoiceHelp = SendVoiceHelp.getInstance();
        sendVoiceHelp.takeOnLongTouchToCancel(tvLongClick, this);
        sendVoiceHelp.setOnVolumeListener(this);
    }

    private void initMessageList() {
        chatAdapter = new ChatAdapter();
        chatMessageLayoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = rvChatList.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.setChangeDuration(0);
        }

        rvChatList.setLayoutManager(chatMessageLayoutManager);
        rvChatList.setAdapter(chatAdapter);
    }

    private void initPanelMenu() {
        rv_panel.setLayoutManager(new GridLayoutManager(this, 4));
        List<MenuInfo> menuInfos = new ArrayList<>();
        MenuInfo menuInfo = new MenuInfo();
        menuInfo.setIcon(R.mipmap.icon_chat_menu_pic);
        menuInfo.setName("相册");
        menuInfo.setId(1);
        menuInfos.add(menuInfo);
        panelMenuAdapter = new PanelMenuAdapter();
        rv_panel.setAdapter(panelMenuAdapter);
        panelMenuAdapter.refreshData(menuInfos);
        panelMenuAdapter.setOnItemClickListener(this);
    }

    /**
     * 软键盘变化监听
     */
    private void initSoftInputListener() {
        activityRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        if (activityRootView != null) {
            viewTreeObserver = activityRootView.getViewTreeObserver();
            if (viewTreeObserver != null) {
                viewTreeObserver.addOnGlobalLayoutListener(this);
            }
        }
    }


    @OnClick({R.id.iv_menu, R.id.iv_send, R.id.iv_voice_keyboard})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                if (isShow(flMenuLayout)) {
                    InputMethodUtils.toggleSoftInput(etChat);
                    softInputShow = true;
                    hidePanel();
                } else {
                    //弹出面板发送语音切换为为输入框显示模式，并没有焦点
                    isSendVoice = false;
                    changeClickVoiceStyle(false);
                    showPanel();
                }

                break;
            case R.id.iv_send:
                sendTextMessage();
                break;
            case R.id.iv_voice_keyboard:
                isSendVoice = !isSendVoice;
                changeClickVoiceStyle(true);
                break;
        }
    }

    /**
     * 点击发送语音图标样式修改及相关小逻辑
     *
     * @param hasLogic 是否有判断逻辑
     */
    private void changeClickVoiceStyle(boolean hasLogic) {
        ivVoiceKeyboard.setImageResource(isSendVoice ? R.mipmap.icon_keybord : R.mipmap.icon_chat_voice);
        etChat.setVisibility(isSendVoice ? View.GONE : View.VISIBLE);
        tvLongClick.setVisibility(isSendVoice ? View.VISIBLE : View.GONE);
        if (!hasLogic) {
            return;
        }
        if (!isSendVoice) {
            etChat.requestFocus();
            softInputShow = true;
            InputMethodUtils.toggleSoftInput(etChat);
        } else {
            if (isShow(flMenuLayout)) {
                hidePanel();
            }
            if (softInputShow) {
                InputMethodUtils.hideKeyboard(etChat);
            }
        }

    }

    /**
     * 发送文本消息
     */
    private void sendTextMessage() {
        if (TextUtils.isEmpty(etChat.getText().toString().trim())) {
            ToastUtil.showShort("请输入内容");
            return;
        }
        SocketManager.getInstance().sendChatMessage(toId, SocketConstant.TEXT, etChat.getText().toString());
    }

    /**
     * 发送图片消息
     */
    private void sendImageMessage(String image) {
        SocketManager.getInstance().sendChatMessage(toId, SocketConstant.PICTURE, image);

    }

    /**
     * 发送音频消息
     *
     * @param url
     */
    private void sendAudioMessage(String url) {
        if (!TextUtils.isEmpty(url)) {
            Log.e("TAG", "success:  url== " + url + SocketConstant.AUDIO_TIME + recordAudioTime);
            SocketManager.getInstance().sendChatMessage(toId, SocketConstant.AUDIO, url + SocketConstant.AUDIO_TIME + recordAudioTime);
        }
    }

    /**
     * 发送视频消息
     *
     * @param videoUrl       视频得url
     * @param videoThumbPath 视频第一帧地址
     */
    private void sendVideoMessage(String videoUrl, String videoThumbPath) {
        if (videoIndexMap == null) {
            videoIndexMap = new HashMap<>();
        }
        ChatMessageInfo chatMessageInfo = new ChatMessageInfo();
        chatMessageInfo.setFileType(Integer.valueOf(SocketConstant.VIDEO));
        chatMessageInfo.setMsg(videoUrl);
        chatMessageInfo.setThumbUrl(videoThumbPath);
        long timeMillis = System.currentTimeMillis();
        chatMessageInfo.setIdentification(timeMillis + "");
        chatAdapter.add(chatMessageInfo);
        notifyScrollToBottom();
//            chatMessageInfo.setMid(System.currentTimeMillis() + "");
        //TODO 因为要先在本地消息里加一条视频消息占位后上传后台服务器，这里需要引入一个时间戳概念，一条视频消息对应一个唯一得时间戳
        // 发送视频成功要刷新列表对应视频得信息
        uploadVideo(videoUrl, videoThumbPath, timeMillis + "");

    }

    private void uploadVideo(String videoUrl, String videoThumbPath, final String timeMillis) {
        rvChatList.postDelayed(new Runnable() {
            @Override
            public void run() {
                ChatMessageInfo dataByIdentification = getDataByIdentification(timeMillis);
                dataByIdentification.setMsg(videoUrl);
                dataByIdentification.setFileStatus(ChatMessageInfo.STATUS_COMPLETE);
                chatAdapter.notifyItemChanged(chatAdapter.getDatas().indexOf(dataByIdentification));
            }
        }, 6000);
    }

    private ChatMessageInfo getDataByIdentification(String identification) {
        List<ChatMessageInfo> datas = chatAdapter.getDatas();
        for (int i = 0; i < datas.size(); i++) {
            ChatMessageInfo chatMessageInfo = datas.get(i);
            if (chatMessageInfo.getItemType() == ChatMessageInfo.TYPE_ITEM_VIDEO_SELF && TextUtils.equals(chatMessageInfo.getIdentification(), identification)) {
                return chatMessageInfo;
            }

        }
        return null;
    }

    /**
     * 压缩(必须要进行这一步)并上传图片
     *
     * @param imagePath 本地原始图片路径
     */
    private void uploadPic(String imagePath) {
        Luban.with(this)
                .load(imagePath)
                .ignoreBy(100)
                .setTargetDir(FileUtil.getDiskFileDir(this, getString(R.string.luban_compress)))
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        showLoadingView(R.string.uploading, QMUIUtils.ICON_TYPE_LOADING);
                    }

                    @Override
                    public void onSuccess(File file) {
                        //这里正常情况需要先拿到讲file上传到服务器拿到对应url后sendImageMessage
//                        dismissLoadingView();
                        uploadType = TYPE_UPLOAD_IMAGE;
                        uploadFileToServer(file);

                    }


                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();
    }

    /**
     * 上传图片到服务器
     *
     * @param file 图片对应得文件
     */
    private void uploadFileToServer(File file) {
        PostRequest<String> params = MyOkGo.getPostRequest(AppConst.FILE_UPLOAD, this)
                .params("file", file);
        MyOkGo.send(params, this, new UploadFileBean());
    }

    /**
     * @param videoPath 异步创建缩略图
     */
    private void createThumb(String videoPath) {
        RxJavaUtils.executeAsync(new RxJavaUtils.SimpleListener<String, String>() {
            @Override
            public String buildData(String... param) {

                return MediaFileUtils.getVideoThumb(param[0]);
            }

            @Override
            public void rxSuccess(String data) {
                sendVideoMessage(videoPath, data);
            }

            @Override
            public void rxError(Throwable e) {

            }

            @Override
            public void rxDoOnSubscribe(Disposable disposable) {

            }
        }, videoPath);
    }

    /**
     * 通知消息列表滚动到底部得方法
     */
    private void notifyScrollToBottom() {
        rvChatList.post(new Runnable() {
            @Override
            public void run() {
                chatMessageLayoutManager.scrollToPositionWithOffset(chatMessageLayoutManager.getItemCount() - 1, Integer.MIN_VALUE);

            }
        });
    }

    //判断view是否显示方法
    private boolean isShow(View view) {
        return view.getVisibility() == View.VISIBLE && view.getHeight() > 0;
    }

    /**
     * 显示功能面板
     */
    public void showPanel() {
        InputMethodUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        flMenuLayout.removeCallbacks(delayTask);
        if (!isShow(flMenuLayout)) {
            flMenuLayout.setVisibility(View.VISIBLE);
            if (!softInputShow) {
                animationUtil.changeViewHeightAnimatorStart(flMenuLayout, 0, initSoftInputHeight);
            } else {
                updatePanelLayout(flMenuLayout, false);
            }
            InputMethodUtils.hideKeyboard(getCurrentFocus());

        }

    }

    /**
     * 隐藏功能面板
     */
    public void hidePanel() {
        if (softInputShow) {
            //软键盘需要显示时发送延迟隐藏任务
            flMenuLayout.postDelayed(delayTask, 300);
        } else {
            //面板布局隐藏后设置软件软件盘输入方式为底部布局岁软件盘上顶方式
            InputMethodUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //当前只有面板显示且用户单独点击外部等原因意图关闭面板时设置动画隐藏
            animationUtil.changeViewHeightAnimatorStart(flMenuLayout, initSoftInputHeight, 0);
        }

    }

    /**
     * 更新面板布局尺寸
     *
     * @param view
     * @param isHide true:代表隐藏方式，将给面板布局高度设置为0否则为"软键盘"得高度
     */
    private void updatePanelLayout(View view, boolean isHide) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (isHide) {
            layoutParams.height = 0;
        } else {
            layoutParams.height = initSoftInputHeight;
        }
        view.requestLayout();
    }

    /**
     * TODO 动态监听软键盘状态，该方法被调用的前提是Activity的windowSoftInputMode不能为SOFT_INPUT_ADJUST_NOTHING
     */
    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        activityRootView.getWindowVisibleDisplayFrame(rect);
        //计算出可见屏幕的高度
        int displayHeight = rect.bottom - rect.top;
        //获得屏幕整体的高度
        if (displayHeight != usableHeightPrevious) {
            int height = activityRootView.getHeight();
            softInputShow = (double) displayHeight / height < 0.8;
            int statusBarHeight = 0;
            try {
                //反射获取状态栏高度
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = getApplicationContext().getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (softInputShow) {
                //软键盘弹出后让EditText获取焦点，否则软键盘弹出隐藏得toggle方法无效
                etChat.requestFocus();
                //获得当前弹出键盘高度
                int currentSoftInputHeight = height - displayHeight - statusBarHeight;
                if (currentSoftInputHeight != initSoftInputHeight) {
                    //如果不相等讲最新的软键盘高度存在本地
                    saveSoftHeightToNative(currentSoftInputHeight);
                    //保持initSoftInputHeight为最新，真实的软键盘高度
                    initSoftInputHeight = currentSoftInputHeight;
                    if (isShow(flMenuLayout)) {
                        //如果当前面板正在显示，直接更新布局尺寸
                        updatePanelLayout(flMenuLayout, false);
                    }
                }
                //每次软键盘弹出让recycleView最新的message可见，也是就是滚动到底部
                notifyScrollToBottom();
            } else {
                //软件盘隐藏释放焦点
                etChat.clearFocus();
            }
            usableHeightPrevious = displayHeight;
        }
    }

    @Override
    public void onItemClick(RecyclerBaseAdapter recyclerBaseAdapter, View v, Object data) {
        if (recyclerBaseAdapter instanceof PanelMenuAdapter) {
            MenuInfo menuInfo = (MenuInfo) data;
            switch (menuInfo.getId()) {
                case 1:
                    PermissionXUtils.request(this, this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                    break;
            }

        }
    }

    //权限回调方法
    @Override
    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
        if (allGranted) {
            MatisseChooseUtils.startPic(this, requestCodePictureSelected, 1);
        }
    }

    /**
     * 改变录音手势提示状态并显示
     *
     * @param volume 录音的音量
     */
    private void changeVoiceHintByStatus(int volume) {
        switch (voiceStatus) {
            case DISCARD:
                tv_voice_hint_txt.setText("松开手指，取消发送");
                iv_voice_hint_pic.setImageResource(R.mipmap.icon_up_discard);
                break;
            case NORMAL:
                tv_voice_hint_txt.setText("手指上滑，取消发送");
                iv_voice_hint_pic.setImageResource(volumeImageIds[volume]);
                break;
            case TOO_SHORT:
                iv_voice_hint_pic.setImageResource(R.mipmap.icon_record_too_short);
                tv_voice_hint_txt.setText("说话时间太短");
                break;
        }
    }

    /**
     * 开始录制
     */
    @Override
    public void onStartRecord() {
        voiceStatus = NORMAL;
        if (cl_voice_bg.getVisibility() != View.VISIBLE) {
            cl_voice_bg.setVisibility(View.VISIBLE);
        }
        changeVoiceHintByStatus(0);
    }

    /**
     * 录制失败或者用户强制取消回调方法
     *
     * @param message
     */
    @Override
    public void onRecordCancel(String message) {
        if (!TextUtils.isEmpty(message)) {
            ToastUtil.showShort(message);
        }
        if (cl_voice_bg.getVisibility() != View.GONE) {
            cl_voice_bg.setVisibility(View.GONE);
        }
    }

    /**
     * 达到最大录制时间
     */
    @Override
    public void onMaxTime() {
        if (cl_voice_bg.getVisibility() != View.GONE) {
            cl_voice_bg.setVisibility(View.GONE);
        }
    }

    /**
     * 当手势移动到可以松手取消发送区域
     */
    @Override
    public void onMoveToDiscard() {
        voiceStatus = DISCARD;
        changeVoiceHintByStatus(0);
    }

    /**
     * 当手势从取消发送区域移动回松手完成发送区域
     */
    @Override
    public void onMoveToComplete() {
        voiceStatus = NORMAL;
        changeVoiceHintByStatus(0);
    }

    /**
     * 录制成功onMoveToComplete调用该方法必定触发
     *
     * @param path   返回录制得文件路径
     * @param during 文件录制得时长（会有极其细微得误差）
     */
    @Override
    public void onRecordSuccess(String path, long during) {
        if (during < 1100) {
            voiceStatus = TOO_SHORT;
            changeVoiceHintByStatus(0);
            cl_voice_bg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (cl_voice_bg.getVisibility() != View.GONE) {
                        cl_voice_bg.setVisibility(View.GONE);
                    }
                }
            }, 800);

        } else {
            if (cl_voice_bg.getVisibility() != View.GONE) {
                cl_voice_bg.setVisibility(View.GONE);
            }
            recordAudioTime = during;
//            uploadVoice(path);
            uploadType = TYPE_UPLOAD_VOICE;
            uploadFileToServer(new File(path));
        }
    }

    /**
     * 上传语音方法
     *
     * @param path 语音本地路径
     */
    private void uploadVoice(String path) {


//        sendAudioMessage(path);
    }

    /**
     * @param volume 范围0-5 异步调用需要自行回到主线程去处理
     */
    @Override
    public void onRecordVolume(int volume) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (volumeImageIds != null && volume < volumeImageIds.length && cl_voice_bg.getVisibility() == View.VISIBLE && voiceStatus == NORMAL) {
                    changeVoiceHintByStatus(volume);
                }
            }
        });
    }


    /**
     * 延迟任务，用于功能面板与软键盘柔和切换
     */
    private static class DelayTask implements Runnable {

        private final Reference<CommonChatActivity> reference;

        public DelayTask(CommonChatActivity commonChatActivity) {
            reference = new WeakReference<>(commonChatActivity);
        }

        @Override
        public void run() {
            CommonChatActivity commonChatActivity = reference.get();
            if (commonChatActivity == null) {
                return;
            }
            commonChatActivity.flMenuLayout.setVisibility(View.GONE);
            commonChatActivity.updatePanelLayout(commonChatActivity.flMenuLayout, true);
            //面板布局隐藏后设置软件软件盘输入方式为底部布局岁软件盘上顶方式
            InputMethodUtils.updateSoftInputMethod(commonChatActivity, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    /*
     *点击列表空白处隐藏软键盘，功能面板等
     * */
    public static class LiveAudioOnItemTouchListener implements RecyclerView.OnItemTouchListener {

        private final Reference<CommonChatActivity> reference;

        public LiveAudioOnItemTouchListener(CommonChatActivity chatActivity) {
            reference = new WeakReference<>(chatActivity);
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            CommonChatActivity chatActivity = reference.get();

            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                if (chatActivity == null) {
                    return false;
                }
                if (chatActivity.isShow(chatActivity.flMenuLayout)) {
                    chatActivity.hidePanel();
                }
                InputMethodUtils.hideKeyboard(chatActivity.etChat);

            }

            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

    /**
     * 获取本地软键盘高度
     *
     * @return
     */
    private int getNativeSoftHeight() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getInt(KEY_HEIGHT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 337.5f, getResources().getDisplayMetrics()));
    }

    /**
     * 讲软键盘高度存储到本地
     *
     * @param height
     */
    private void saveSoftHeightToNative(int height) {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putInt(KEY_HEIGHT, height).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rvChatList != null) {
            rvChatList.removeOnItemTouchListener(liveAudioOnItemTouchListener);
        }
        if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnGlobalLayoutListener(this);
        }

        SocketManager.getInstance().closeChannel(channelName, connectListener, serverMessageListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodePictureSelected && resultCode == RESULT_OK) {
            List<String> paths = Matisse.obtainPathResult(data);
            if (paths != null && paths.size() > 0) {
                String mediaPath = paths.get(0);
                if (MediaFileUtils.isVideoFileType(mediaPath)) {
                    createThumb(mediaPath);
                } else {
                    uploadPic(mediaPath);
                }

            }

        }
    }


}
