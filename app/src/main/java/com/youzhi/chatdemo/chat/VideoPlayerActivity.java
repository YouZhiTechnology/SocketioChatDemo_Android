package com.youzhi.chatdemo.chat;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.youzhi.chatdemo.BaseActivity;
import com.youzhi.chatdemo.R;


import butterknife.BindView;

public class VideoPlayerActivity extends BaseActivity {
    @BindView(R.id.video_player)
    StandardGSYVideoPlayer videoPlayer;
    OrientationUtils orientationUtils;
    public static String VIDEO_KEY = "video_url";

    @Override
    protected int getLayout() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initView() {
        super.initView();
        setStatusBarColorWithFitSystem(android.R.color.black, false, true);
        String source = getIntent().getStringExtra(VIDEO_KEY);
        videoPlayer.setUp(source, true, "");

        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        videoPlayer.startPlayLogic();
    }

    public static void startMine(Activity activity, String videoUrl) {
        Intent intent = new Intent(activity, VideoPlayerActivity.class);
        intent.putExtra(VIDEO_KEY, videoUrl);
        activity.startActivity(intent);
    }

    @Override
    protected boolean isNeedImmerse() {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }
}
