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
    private int initSoftInputHeight;//???????????????????????????
    private View activityRootView;//???????????????
    private static final String PREFERENCES_NAME = "softInputHeight";
    private static final String KEY_HEIGHT = "height";
    private SharedPreferences sharedPreferences;
    private boolean softInputShow;//???????????????????????????
    private DelayTask delayTask;//????????????
    private ViewTreeObserver viewTreeObserver;
    private int usableHeightPrevious; //???????????????????????????
    private PanelMenuAdapter panelMenuAdapter;//???????????????????????????
    private ChatAdapter chatAdapter;//???????????????????????????
    private final int requestCodePictureSelected = 0x10; //?????????????????????????????????????????????
    private LinearLayoutManager chatMessageLayoutManager;
    private boolean isSendVoice;
    private SendVoiceHelp sendVoiceHelp; //?????????????????????
    private int[] volumeImageIds; //??????????????????
    /* ???????????????????????????*/
    private final int DISCARD = 1;//?????????
    private final int NORMAL = 2;//?????????
    private final int TOO_SHORT = 3;//????????????
    private int voiceStatus = NORMAL;//???????????????????????????
    private long recordAudioTime;//???????????????
    private Map<String, Integer> videoIndexMap;
    private CommonPopupWindow commonPopupWindow;
    private ConnectListener connectListener;
    private ServerMessageListener serverMessageListener;
    //??????socket????????????
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
        //??????????????????????????????????????????????????????????????????????????????View???????????????????????????
        setStatusBarColorWithFitSystem(R.color.color_white, true, true);
        tbTitle.setNotStatusBar();
        //??????????????????????????????,????????????????????????????????????????????????????????????????????????
        initSoftInputHeight = getNativeSoftHeight();
        liveAudioOnItemTouchListener = new LiveAudioOnItemTouchListener(this);
        rvChatList.addOnItemTouchListener(liveAudioOnItemTouchListener);

        //???????????????????????????????????????
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
        //???????????????id
        toId = getIntent().getStringExtra(IntentKey.KEY_ID);
        //????????????
        String name = getIntent().getStringExtra(IntentKey.KEY_NAME);
        tbTitle.settitleContent("???" + name + "????????????");
        //??????id????????????????????????
        sessionId = getIntent().getStringExtra(IntentKey.KEY_SESSION_ID);
        //?????????????????????????????????????????????????????????????????????
        delayTask = new DelayTask(this);
        //??????????????????????????????
        initSoftInputListener();
        //???????????????????????????
        initPanelMenu();
        //???????????????????????????
        initMessageList();
        //????????????????????????????????????
        initVoiceHelp();
        //?????????Socket
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

    //??????????????????
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
                ToastUtil.showShort("??????????????????");
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


    //????????????
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
        menuInfo.setName("??????");
        menuInfo.setId(1);
        menuInfos.add(menuInfo);
        panelMenuAdapter = new PanelMenuAdapter();
        rv_panel.setAdapter(panelMenuAdapter);
        panelMenuAdapter.refreshData(menuInfos);
        panelMenuAdapter.setOnItemClickListener(this);
    }

    /**
     * ?????????????????????
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
                    //???????????????????????????????????????????????????????????????????????????
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
     * ??????????????????????????????????????????????????????
     *
     * @param hasLogic ?????????????????????
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
     * ??????????????????
     */
    private void sendTextMessage() {
        if (TextUtils.isEmpty(etChat.getText().toString().trim())) {
            ToastUtil.showShort("???????????????");
            return;
        }
        SocketManager.getInstance().sendChatMessage(toId, SocketConstant.TEXT, etChat.getText().toString());
    }

    /**
     * ??????????????????
     */
    private void sendImageMessage(String image) {
        SocketManager.getInstance().sendChatMessage(toId, SocketConstant.PICTURE, image);

    }

    /**
     * ??????????????????
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
     * ??????????????????
     *
     * @param videoUrl       ?????????url
     * @param videoThumbPath ?????????????????????
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
        //TODO ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // ??????????????????????????????????????????????????????
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
     * ??????(????????????????????????)???????????????
     *
     * @param imagePath ????????????????????????
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
                        //????????????????????????????????????file??????????????????????????????url???sendImageMessage
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
     * ????????????????????????
     *
     * @param file ?????????????????????
     */
    private void uploadFileToServer(File file) {
        PostRequest<String> params = MyOkGo.getPostRequest(AppConst.FILE_UPLOAD, this)
                .params("file", file);
        MyOkGo.send(params, this, new UploadFileBean());
    }

    /**
     * @param videoPath ?????????????????????
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
     * ??????????????????????????????????????????
     */
    private void notifyScrollToBottom() {
        rvChatList.post(new Runnable() {
            @Override
            public void run() {
                chatMessageLayoutManager.scrollToPositionWithOffset(chatMessageLayoutManager.getItemCount() - 1, Integer.MIN_VALUE);

            }
        });
    }

    //??????view??????????????????
    private boolean isShow(View view) {
        return view.getVisibility() == View.VISIBLE && view.getHeight() > 0;
    }

    /**
     * ??????????????????
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
     * ??????????????????
     */
    public void hidePanel() {
        if (softInputShow) {
            //????????????????????????????????????????????????
            flMenuLayout.postDelayed(delayTask, 300);
        } else {
            //?????????????????????????????????????????????????????????????????????????????????????????????
            InputMethodUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //???????????????????????????????????????????????????????????????????????????????????????????????????
            animationUtil.changeViewHeightAnimatorStart(flMenuLayout, initSoftInputHeight, 0);
        }

    }

    /**
     * ????????????????????????
     *
     * @param view
     * @param isHide true:??????????????????????????????????????????????????????0?????????"?????????"?????????
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
     * TODO ????????????????????????????????????????????????????????????Activity???windowSoftInputMode?????????SOFT_INPUT_ADJUST_NOTHING
     */
    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        activityRootView.getWindowVisibleDisplayFrame(rect);
        //??????????????????????????????
        int displayHeight = rect.bottom - rect.top;
        //???????????????????????????
        if (displayHeight != usableHeightPrevious) {
            int height = activityRootView.getHeight();
            softInputShow = (double) displayHeight / height < 0.8;
            int statusBarHeight = 0;
            try {
                //???????????????????????????
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = getApplicationContext().getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (softInputShow) {
                //?????????????????????EditText?????????????????????????????????????????????toggle????????????
                etChat.requestFocus();
                //??????????????????????????????
                int currentSoftInputHeight = height - displayHeight - statusBarHeight;
                if (currentSoftInputHeight != initSoftInputHeight) {
                    //??????????????????????????????????????????????????????
                    saveSoftHeightToNative(currentSoftInputHeight);
                    //??????initSoftInputHeight????????????????????????????????????
                    initSoftInputHeight = currentSoftInputHeight;
                    if (isShow(flMenuLayout)) {
                        //?????????????????????????????????????????????????????????
                        updatePanelLayout(flMenuLayout, false);
                    }
                }
                //????????????????????????recycleView?????????message????????????????????????????????????
                notifyScrollToBottom();
            } else {
                //???????????????????????????
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

    //??????????????????
    @Override
    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
        if (allGranted) {
            MatisseChooseUtils.startPic(this, requestCodePictureSelected, 1);
        }
    }

    /**
     * ???????????????????????????????????????
     *
     * @param volume ???????????????
     */
    private void changeVoiceHintByStatus(int volume) {
        switch (voiceStatus) {
            case DISCARD:
                tv_voice_hint_txt.setText("???????????????????????????");
                iv_voice_hint_pic.setImageResource(R.mipmap.icon_up_discard);
                break;
            case NORMAL:
                tv_voice_hint_txt.setText("???????????????????????????");
                iv_voice_hint_pic.setImageResource(volumeImageIds[volume]);
                break;
            case TOO_SHORT:
                iv_voice_hint_pic.setImageResource(R.mipmap.icon_record_too_short);
                tv_voice_hint_txt.setText("??????????????????");
                break;
        }
    }

    /**
     * ????????????
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
     * ????????????????????????????????????????????????
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
     * ????????????????????????
     */
    @Override
    public void onMaxTime() {
        if (cl_voice_bg.getVisibility() != View.GONE) {
            cl_voice_bg.setVisibility(View.GONE);
        }
    }

    /**
     * ????????????????????????????????????????????????
     */
    @Override
    public void onMoveToDiscard() {
        voiceStatus = DISCARD;
        changeVoiceHintByStatus(0);
    }

    /**
     * ???????????????????????????????????????????????????????????????
     */
    @Override
    public void onMoveToComplete() {
        voiceStatus = NORMAL;
        changeVoiceHintByStatus(0);
    }

    /**
     * ????????????onMoveToComplete???????????????????????????
     *
     * @param path   ???????????????????????????
     * @param during ??????????????????????????????????????????????????????
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
     * ??????????????????
     *
     * @param path ??????????????????
     */
    private void uploadVoice(String path) {


//        sendAudioMessage(path);
    }

    /**
     * @param volume ??????0-5 ????????????????????????????????????????????????
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
     * ?????????????????????????????????????????????????????????
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
            //?????????????????????????????????????????????????????????????????????????????????????????????
            InputMethodUtils.updateSoftInputMethod(commonChatActivity, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    /*
     *??????????????????????????????????????????????????????
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
     * ???????????????????????????
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
     * ?????????????????????????????????
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
