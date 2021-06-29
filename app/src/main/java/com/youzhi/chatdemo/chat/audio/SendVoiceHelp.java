package com.youzhi.chatdemo.chat.audio;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.callback.RequestCallback;
import com.youzhi.chatdemo.utils.PermissionXUtils;


import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author gaoyuheng
 * @description: 针对View 长按录音点击上滑取消帮助类
 * @date : 3/19/21 1:20 PM.
 */
public class SendVoiceHelp implements View.OnLongClickListener, View.OnTouchListener, RequestCallback, AudioRecordManager.OnRecordListener {
    private AudioRecordManager audioRecordManager;//音频录制的管理类
    public static final long INTERVAL = 1000; //录音计时间隔单位毫秒
    private int currentSecondNum;//计时的数
    private Handler mTimeHandler;
    private TimerTask timerTask;//定时任务
    private OnInteractionListener mInteractionListener;
    private boolean isUserForceOverRecord;//标识用户自己取消录音
    private float touchTargetViewY;//触摸目标View Y坐标
    private long startRecordTime; //记录录制的开始时间戳
    private int maxSeconds = 60;//限制语音录制最大妙数

    public void setMaxSeconds(int maxSeconds) {
        this.maxSeconds = maxSeconds;
    }

    public synchronized static SendVoiceHelp getInstance() {
        return new SendVoiceHelp();
    }

    private SendVoiceHelp() {
        audioRecordManager = AudioRecordManager.getInstance();
        audioRecordManager.setOnRecordListener(this);

    }

    /**
     * 提供外部的方法调用配置参数
     *
     * @param targetView            长按录音的View
     * @param onInteractionListener 各种相应交互的回调
     */
    public void takeOnLongTouchToCancel(View targetView, OnInteractionListener onInteractionListener) {
        mInteractionListener = onInteractionListener;
        targetView.setOnLongClickListener(this);
        targetView.setOnTouchListener(this);

    }

    public static boolean hasPermission(Context context, String permission) {
        int perm = context.checkCallingOrSelfPermission(permission);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onLongClick(View v) {
        Context context = v.getContext();
        if (context instanceof FragmentActivity) {
            if (hasPermission(context, Manifest.permission.RECORD_AUDIO)) {
                startRecord();
            } else {
                PermissionXUtils.request((FragmentActivity) context, this, Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }


        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //只有在录音过程中我们再对其事件进行处理
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchTargetViewY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //如果是正在录制的状态
                if (audioRecordManager.getCurrentStatus() != AudioRecordManager.STATUS_STARTING) {
                    return false;
                }
                if (audioRecordManager.isRecording()) {
                    audioRecordManager.stopRecord();
                }
                startOrStopTimer(false);


                break;
            case MotionEvent.ACTION_MOVE:
                if (audioRecordManager.getCurrentStatus() != AudioRecordManager.STATUS_STARTING) {
                    return false;
                }
                float moveY = event.getY();
                if (Math.abs(touchTargetViewY - moveY) > 100 && !isUserForceOverRecord) {
                    isUserForceOverRecord = true;
                    if (mInteractionListener != null) {
                        mInteractionListener.onMoveToDiscard();
                    }
                }
                //移动到view的范围
                if (isTouchPointInView(v, (int) event.getRawX(), (int) event.getRawY()) && isUserForceOverRecord) {
                    isUserForceOverRecord = false;
                    if (mInteractionListener != null) {
                        mInteractionListener.onMoveToComplete();

                    }
                }
                break;
        }
        return false;
    }

    /**
     * 重置一些属性方法
     */
    private void resetSomeProperty() {
        currentSecondNum = 0;//当前计时的数

    }

    @Override
    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
        if (allGranted) {

        }

    }

    @Override
    public void onRecordFinish(String path) {
        if (!TextUtils.isEmpty(path) && mInteractionListener != null) {
            if (!isUserForceOverRecord) {
                mInteractionListener.onRecordSuccess(path, System.currentTimeMillis() - startRecordTime);
            } else {
                mInteractionListener.onRecordCancel("");
            }
            isUserForceOverRecord = false;
        }
        resetSomeProperty();
    }

    public void setOnVolumeListener(AudioRecordManager.OnRecordVolumeListener onRecordVolumeListener) {
        if (audioRecordManager != null) {
            audioRecordManager.setOnRecordVolumeListener(onRecordVolumeListener);
        }
    }

    /**
     * 判断是否触摸到了View所在的范围
     *
     * @param view 目标的View
     * @param x    event.getRawX
     * @param y    event.getRawY
     * @return
     */
    private boolean isTouchPointInView(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }

    @Override
    public void onRecordFailure(String message) {
        Log.e("LOGCAT", "onRecordFailure==" + message);
        if (mInteractionListener != null) {
            mInteractionListener.onRecordCancel(message);
        }
    }

    /**
     * 提供外部的交互回调接口
     */
    public interface OnInteractionListener {
        void onStartRecord();//开始录音

        void onRecordCancel(String message);//取消录音

        void onMaxTime();//达到最大时间

        void onMoveToDiscard();//移动到丢弃的范围回调方法

        void onMoveToComplete();//移动到用户非丢弃范围回调方法

        void onRecordSuccess(String path, long during);//录音成功
    }


    public static class TimerTask implements Runnable {

        private final Reference<SendVoiceHelp> reference;

        public TimerTask(SendVoiceHelp help) {
            reference = new WeakReference<>(help);

        }

        @Override
        public void run() {
            SendVoiceHelp help = reference.get();
            if (help == null) return;
            help.currentSecondNum += 1;
            //限制录音最大时长60秒
            if (help.currentSecondNum < help.maxSeconds) {
                help.mTimeHandler.postDelayed(help.timerTask, help.INTERVAL);
            } else {
                if (help.mInteractionListener != null) {
                    help.mInteractionListener.onMaxTime();
                }
                help.stopRecordByMaxTime();
            }

        }

    }

    //停止录音
    private void stopRecordByMaxTime() {
        isUserForceOverRecord = false;
        if (audioRecordManager.isRecording()) {
            audioRecordManager.stopRecord();
        }
        startOrStopTimer(false);
        // tvRecordStatus.setText("录音结束");

    }

    //开始录音方法
    private void startRecord() {
        audioRecordManager.startRecord();
        startOrStopTimer(true);
        startRecordTime = System.currentTimeMillis();
        if (mInteractionListener != null) {
            mInteractionListener.onStartRecord();
        }
    }

    private void startOrStopTimer(boolean isStart) {
        if (mTimeHandler == null) {
            mTimeHandler = new Handler();
        }
        if (timerTask == null) {
            timerTask = new TimerTask(this);
        }

        if (isStart) {
            mTimeHandler.postDelayed(timerTask, INTERVAL);
        } else {
            mTimeHandler.removeCallbacks(timerTask);
        }
    }
}
