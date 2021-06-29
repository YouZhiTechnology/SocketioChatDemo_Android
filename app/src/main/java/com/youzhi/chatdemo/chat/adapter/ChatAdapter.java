package com.youzhi.chatdemo.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.youzhi.chatdemo.adapter.RecyclerBaseAdapter;
import com.youzhi.chatdemo.adapter.RecyclerBaseHolder;
import com.youzhi.chatdemo.chat.VideoPlayerActivity;
import com.youzhi.chatdemo.chat.audio.AudioFocusManager;
import com.youzhi.chatdemo.chat.audio.MediaPlayerHelp;
import com.youzhi.chatdemo.chat.bean.ChatMessageInfo;
import com.youzhi.chatdemo.R;
import com.youzhi.chatdemo.chat.utils.popuwindow.CommonPopuUtils;
import com.youzhi.chatdemo.chat.utils.popuwindow.CommonPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.shinichi.library.ImagePreview;

import static com.youzhi.chatdemo.chat.socket.SocketConstant.AUDIO_TIME;

/**
 * 聊天adapter
 */
public class ChatAdapter extends RecyclerBaseAdapter<ChatMessageInfo, RecyclerBaseHolder> implements CommonPopupWindow.ViewInterface {


    private RecyclerView mRecycler;
    private MediaPlayer mediaPlay;
    private String chickId;
    private AudioFocusManager audioFocusManager;

    public MediaPlayer getMediaPlay() {
        return mediaPlay;
    }

    public void setMediaPlay(MediaPlayer mediaPlay) {
        this.mediaPlay = mediaPlay;
    }

    public int getCurrentPosition() {
        try {
            if (mediaPlay != null) {
                return mediaPlay.getCurrentPosition();
            }
        } catch (Exception e) {
        }
        return 0;
    }

    private void play() {
        try {
            if (mediaPlay != null) {
                mediaPlay.start();
            }
        } catch (Exception e) {
        }
    }

    public void destroyMediaPlay() {
        if (mediaPlay != null) {
            MediaPlayerHelp.destroyMedia(mediaPlay);
            mediaPlay = null;
        }
    }

    //释放音频焦点
    public void releaseTheAudioFocus() {
        audioFocusManager.releaseTheAudioFocus();
    }

    //获取音频焦点
    public void requestTheAudioFocus(AudioFocusManager.AudioListener audioListener) {
        audioFocusManager.requestTheAudioFocus(audioListener);
    }

    private void showHideVice(boolean playing, boolean isSelf, ImageView view) {
        Log.e("LOGCAT", "playing=" + playing);
        if ((view.getContext() instanceof Activity) && !isDestroy((Activity) view.getContext())) {
            if (isSelf) {
                Glide.with(view.getContext()).load(playing ? R.mipmap.icon_voice_self : R.mipmap.icon_voice_self).into(view);
            } else {
                Glide.with(view.getContext()).load(playing ? R.mipmap.icon_voice_other : R.mipmap.icon_voice_other).into(view);

            }
        }

    }

    public static boolean isDestroy(Activity mActivity) {
        if (mActivity == null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }

    /*更改播放暂停的各种情况通过播放状态*/
    private void changePlayingByStatus(boolean isPlay, String dataId, String voiceUrl, ImageView imageView) {
        Log.e("LOGCAT", "点击播放的语音id==" + dataId);
        destroyMediaPlay();
        ChatMessageInfo dataById = getDataById(dataId);
        if (dataById != null) {
            showHideVice(isPlay, dataById.getItemType() == ChatMessageInfo.TYPE_ITEM_VOICE_SELF, imageView);
            dataById.setPlaying(isPlay);
        }
        if (isPlay) {
            if (!TextUtils.isEmpty(getChickId()) && !TextUtils.equals(dataId, getChickId())) {
                //修改上一个播放语音的状态并刷新
                notifyChildAudioClose();

            }
            setChickId(dataId);
            setMediaPlay(MediaPlayerHelp.initMediaPlay(new MediaPlayerHelp.MediaListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    releaseTheAudioFocus();
                    changePlayingByStatus(false, getChickId(), voiceUrl, imageView);
//                    liveAudioTeacherMessageAdapter.setTaskCallBack(null, "");
                    ChatMessageInfo chatMessageInfo = getDataById(getChickId());
                    if (chatMessageInfo != null) {
                        notifyItemChanged(getDatas().indexOf(chatMessageInfo));
                    }
                }

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }

                @Override
                public void onPrepared(MediaPlayer mp) {
                    play();

                }
            }, voiceUrl));

        }
    }

    public void notifyChildAudioClose() {
        ChatMessageInfo chatMessageInfo = getDataById(getChickId());
        if (chatMessageInfo != null) {
            chatMessageInfo.setPlaying(false);
//            socketMessageResult.setCurrentVoiceTime(0);
//            socketMessageResult.setPause(false);
            notifyItemChanged(getDatas().indexOf(chatMessageInfo));
        }
        setChickId("");
    }

    public String getChickId() {
        return chickId;
    }

    public void setChickId(String chickId) {
        this.chickId = chickId;
    }

    public ChatMessageInfo getDataById(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        for (int i = 0; i < getDatas().size(); i++) {
            ChatMessageInfo chatMessageInfo = getDatas().get(i);
            if (TextUtils.equals(id, chatMessageInfo.getMid())) {
                return chatMessageInfo;
            }
        }
        return null;
    }

    @Override
    public int getLayoutId(int viewType) {

        int layoutId;
        switch (viewType) {
            case ChatMessageInfo.TYPE_ITEM_TEXT_OTHER:
                layoutId = R.layout.item_chat_txt_other;
                break;
            case ChatMessageInfo.TYPE_ITEM_PHOTO_OTHER:
                layoutId = R.layout.item_chat_pic_other;
                break;
            case ChatMessageInfo.TYPE_ITEM_VOICE_OTHER:
                layoutId = R.layout.item_chat_voice_other;
                break;
            case ChatMessageInfo.TYPE_ITEM_TEXT_SELF:
                layoutId = R.layout.item_chat_txt_self;
                break;
            case ChatMessageInfo.TYPE_ITEM_PHOTO_SELF:
                layoutId = R.layout.item_chat_pic_self;
                break;
            case ChatMessageInfo.TYPE_ITEM_VOICE_SELF:
                layoutId = R.layout.item_chat_voice_self;
                break;
            case ChatMessageInfo.TYPE_ITEM_VIDEO_OTHER:
                layoutId = R.layout.item_chat_video_other;
                break;
            case ChatMessageInfo.TYPE_ITEM_VIDEO_SELF:
                layoutId = R.layout.item_chat_video_self;
                break;
            default:
                layoutId = -100;
                break;
        }


        return layoutId;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecycler = recyclerView;
        audioFocusManager = new AudioFocusManager();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position) || isFooterView(position)) {
            return super.getItemViewType(position);
        } else {
            return getDatas().get(haveHeaderView() ? position - 1 : position).getItemType();
        }
    }

    @Override
    public RecyclerBaseHolder createViewHolder(View itemView, Context context, int viewType) {

        switch (viewType) {
            case ChatMessageInfo.TYPE_ITEM_TEXT_OTHER:
            case ChatMessageInfo.TYPE_ITEM_TEXT_SELF:
                return new TextMessageHolder(itemView, context, this);
            case ChatMessageInfo.TYPE_ITEM_PHOTO_OTHER:
            case ChatMessageInfo.TYPE_ITEM_PHOTO_SELF:
                return new PhotoMessageHolder(itemView, context, this);
            case ChatMessageInfo.TYPE_ITEM_VOICE_OTHER:
            case ChatMessageInfo.TYPE_ITEM_VOICE_SELF:
                return new VoiceMessageHolder(itemView, context, this);

            case ChatMessageInfo.TYPE_ITEM_VIDEO_OTHER:
            case ChatMessageInfo.TYPE_ITEM_VIDEO_SELF:
                return new VideoMessageHolder(itemView, context, this);
        }

        return new HeadOrFootHolder(itemView, context, this);
    }

    @Override
    public void getChildView(View view, int layoutResId) {

    }

    /**
     * 文本消息holder
     */
    private static class TextMessageHolder extends RecyclerBaseHolder<ChatMessageInfo> implements View.OnLongClickListener {

        private final TextView tv_message;
        private final ChatAdapter mChatAdapter;

        public TextMessageHolder(View itemView, Context context, RecyclerView.Adapter adapter) {
            super(itemView, context, adapter);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_message.setOnLongClickListener(this);
            mChatAdapter = (ChatAdapter) adapter;
        }

        @Override
        public void bindHolder(int position) {
            tv_message.setText(mData.getMsg());

        }

        @Override
        public boolean onLongClick(View v) {
            CommonPopuUtils.showPopForViewOnItem((Activity) mContext, R.layout.layout_chat_popu, v, mChatAdapter);

            return false;
        }
    }


    /**
     * 图片消息holder
     */
    private static class PhotoMessageHolder extends RecyclerBaseHolder<ChatMessageInfo> implements View.OnClickListener, View.OnLongClickListener {
        private static final float IMAGE_MAX_HEIGHT_TO_WIDTH = 0.3f;
        private final ImageView iv_picture;
        private int screenHeight;
        private final ChatAdapter mChatAdapter;

        public PhotoMessageHolder(View itemView, Context context, RecyclerView.Adapter adapter) {
            super(itemView, context, adapter);
            iv_picture = itemView.findViewById(R.id.iv_picture);
            screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
            mChatAdapter = (ChatAdapter) adapter;
            iv_picture.setOnClickListener(this);
            iv_picture.setOnLongClickListener(this);
        }

        @Override
        public void bindHolder(int position) {

            Glide.with(mContext).load(mData.getMsg()).into(iv_picture);
//            Glide.with(mContext).asBitmap().load(mData.getMsg())
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(new CustomTarget<Bitmap>() {
//
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            Log.e("LOGCAT", "onResourceReady");
//                            ViewGroup.LayoutParams layoutParams = iv_picture.getLayoutParams();
//                            boolean canScrollVertically = mChatAdapter.mRecycler.canScrollVertically(-1);
//                            int bw = resource.getWidth();
//                            int bh = resource.getHeight();
//                            float maxwh = screenHeight * IMAGE_MAX_HEIGHT_TO_WIDTH;
//                            if (bw >= bh) {
//                                //优化限制，如果尺寸相同复用就不需要再进行计算
//                                if (layoutParams.width != (int) maxwh || layoutParams.height != (int) (bh * maxwh / bw)) {
//                                    layoutParams.width = (int) maxwh;
//                                    layoutParams.height = (int) (bh * maxwh / bw);
//                                    //正确的刷新LayoutParams得方式直接调用setLayoutParams不推荐
//                                    iv_picture.requestLayout();
//                                }
//
//                            } else {
//                                //优化限制，如果尺寸相同复用就不需要再进行计算
//                                if (layoutParams.width != (int) (bw * maxwh / bh) || layoutParams.height != (int) maxwh) {
//                                    layoutParams.height = (int) maxwh;
//                                    layoutParams.width = (int) (bw * maxwh / bh);
//                                    iv_picture.setLayoutParams(layoutParams);
//                                    iv_picture.requestLayout();
//                                }
//
//                            }
//                            iv_picture.setImageBitmap(resource);
//                            /**
//                             *  TODO 由于图片是异步显示，并且是通过拿到真实图片后获取得宽高比才计算得大小
//                             *  TODO ，添加到适配器后直接scrollToBottom无效，需要glide回调测量重绘图片后去通知列表滚动到底部
//                             *  添加isNeedScrollToBottom进行标识，用完一次需要将其状态设置为false
//                             *
//                             */
//                            int lastCompletelyVisibleItemPosition = ((LinearLayoutManager) mChatAdapter.mRecycler.getLayoutManager()).findLastCompletelyVisibleItemPosition();
//                            if (mData.isNeedScrollToBottom() && lastCompletelyVisibleItemPosition==position) {
//                                mChatAdapter.mRecycler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        ((LinearLayoutManager) mChatAdapter.mRecycler.getLayoutManager()).scrollToPositionWithOffset(mChatAdapter.mRecycler.getLayoutManager().getItemCount() - 1, Integer.MIN_VALUE);
//
//                                    }
//                                });
//                                mData.setNeedScrollToBottom(false);
//                            }
//
//                        }
//
//
//                        @Override
//                        public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                        }
//                    });

        }


        @Override
        public void onClick(View v) {
            List<String> imageList = new ArrayList<>();
            List<ChatMessageInfo> datas = mChatAdapter.getDatas();
            for (int i = 0; i < datas.size(); i++) {
                ChatMessageInfo chatMessageInfo = datas.get(i);
                if (chatMessageInfo.getItemType() == ChatMessageInfo.TYPE_ITEM_PHOTO_SELF || chatMessageInfo.getItemType() == ChatMessageInfo.TYPE_ITEM_PHOTO_OTHER) {
                    imageList.add(chatMessageInfo.getMsg());
                }
            }
            ImagePreview
                    .getInstance()
                    // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好；
                    .setContext(mContext)
                    // 设置从第几张开始看（索引从0开始）
                    .setIndex(imageList.indexOf(mData.getMsg()))
                    // 2：直接传url List
                    .setImageList(imageList)
                    // 缩放动画时长，单位ms
                    .setZoomTransitionDuration(300)
                    // 是否启用点击图片关闭。默认启用
                    .setEnableClickClose(true)
//                    .setDownIconResId(R.mipmap.icon_down_pic)
                    // 是否启用上拉/下拉关闭。默认不启用
                    .setEnableDragClose(false)
                    .setShowDownButton(false)
                    .setShowCenterSavePic(true)//自定义的按钮样式
//                                    .setDownIconResId(R.mipmap.icon_chat_voice)
                    // 开启预览
                    .start();
        }

        @Override
        public boolean onLongClick(View v) {
            CommonPopuUtils.showPopForViewOnItem((Activity) mContext, R.layout.layout_chat_popu, v, mChatAdapter);
            return true;
        }


    }


    /**
     * 语音消息holder
     */
    private static class VoiceMessageHolder extends RecyclerBaseHolder<ChatMessageInfo> implements View.OnClickListener, AudioFocusManager.AudioListener, View.OnLongClickListener {

        private final TextView tv_audio_time;
        private final ChatAdapter chatAdapter;
        private final View cl_voice;
        private final ImageView iv_voice;

        public VoiceMessageHolder(View itemView, Context context, RecyclerView.Adapter adapter) {
            super(itemView, context, adapter);
            tv_audio_time = itemView.findViewById(R.id.tv_audio_time);
            cl_voice = itemView.findViewById(R.id.cl_voice);
            iv_voice = itemView.findViewById(R.id.iv_voice);
            cl_voice.setOnClickListener(this);
            cl_voice.setOnLongClickListener(this);
            chatAdapter = (ChatAdapter) adapter;

        }

        @Override
        public void bindHolder(int position) {

            tv_audio_time.setText(Integer.valueOf(getStringAfter(mData.getMsg(), AUDIO_TIME)) / 1000 + "");
            chatAdapter.showHideVice(mData.isPlaying(), mData.getItemType() == ChatMessageInfo.TYPE_ITEM_VOICE_SELF, iv_voice);

        }

        @Override
        public void onClick(View v) {
            chatAdapter.requestTheAudioFocus(this);
            chatAdapter.changePlayingByStatus(true, mData.getMid(), getStringBefore(mData.getMsg(), AUDIO_TIME), iv_voice);

        }

        public static String getStringAfter(String source, String target) {
            if (!TextUtils.isEmpty(source) && !TextUtils.isEmpty(target) && source.contains(target)) {
                return source.substring(source.indexOf(target) + target.length());
            }
            return "";
        }

        public static String getStringBefore(String source, String target) {
            if (!TextUtils.isEmpty(source) && !TextUtils.isEmpty(target) && source.contains(target)) {
                return source.substring(0, source.indexOf(target));
            }
            return "";
        }

        @Override
        public void start() {

        }

        @Override
        public void pause() {

        }

        @Override
        public boolean onLongClick(View v) {
//            CommonPopuUtils.showPopForViewOnItem((Activity) mContext, R.layout.layout_chat_popu, v, chatAdapter);
            return true;
        }
    }


    /**
     * 视频消息Holder
     */
    public static class VideoMessageHolder extends RecyclerBaseHolder<ChatMessageInfo> implements View.OnClickListener, View.OnLongClickListener {

        private final ImageView iv_video_picture;
        private final View iv_video_play;
        private final ProgressBar pb_video;
        private final ChatAdapter chatAdapter;

        public VideoMessageHolder(View itemView, Context context, RecyclerView.Adapter adapter) {
            super(itemView, context, adapter);
            chatAdapter = (ChatAdapter) adapter;
            iv_video_picture = itemView.findViewById(R.id.iv_video_picture);
            iv_video_play = itemView.findViewById(R.id.iv_video_play);
            pb_video = itemView.findViewById(R.id.pb_video);
            iv_video_play.setOnClickListener(this);
            iv_video_picture.setOnClickListener(this);
            iv_video_play.setOnLongClickListener(this);
            iv_video_picture.setOnLongClickListener(this);
        }

        @Override
        public void bindHolder(int position) {
            Glide.with(mContext).load(new File(mData.getThumbUrl()))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_video_picture);
            switch (mData.getFileStatus()) {
                case ChatMessageInfo.STATUS_COMPLETE:
                    if (pb_video.getVisibility() != View.GONE) {
                        pb_video.setVisibility(View.GONE);
                    }
                    if (iv_video_play.getVisibility() != View.VISIBLE) {
                        iv_video_play.setVisibility(View.VISIBLE);
                    }
                    break;
                case ChatMessageInfo.STATUS_LOADING:
                    if (pb_video.getVisibility() != View.VISIBLE) {
                        pb_video.setVisibility(View.VISIBLE);
                    }
                    if (iv_video_play.getVisibility() != View.GONE) {
                        iv_video_play.setVisibility(View.GONE);
                    }
                    break;

            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_video_play:
                    if (!TextUtils.isEmpty(mData.getMsg())) {
                        VideoPlayerActivity.startMine((Activity) mContext, mData.getMsg());
                    }
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            CommonPopuUtils.showPopForViewOnItem((Activity) mContext, R.layout.layout_chat_popu, v, chatAdapter);
            return true;
        }


    }


}
