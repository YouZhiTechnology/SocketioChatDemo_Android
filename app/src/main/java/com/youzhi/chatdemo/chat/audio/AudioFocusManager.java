package com.youzhi.chatdemo.chat.audio;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.youzhi.chatdemo.MyApplication;


/**
 * @author gaoyuheng
 * @description:
 * @date :2020/12/22 18:11
 */
public class AudioFocusManager {
    /**
     * 用AudioManager获取音频焦点避免音视频声音并发问题
     */
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;

    //zxzhong 请求音频焦点 设置监听
    public void requestTheAudioFocus(final AudioListener audioListener) {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        }
        if (mAudioFocusChangeListener == null) {
            mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {//监听器
                @Override
                public void onAudioFocusChange(int focusChange) {
                    Log.e("LOGCAT", "focusChange");
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                            //播放操作
                            audioListener.start();
                            break;

                        case AudioManager.AUDIOFOCUS_LOSS:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            //暂停操作
                            audioListener.pause();
                            break;
                        default:
                            break;
                     }
                }
            };
        }
        //下面两个常量参数试过很多 都无效，最终反编译了其他app才搞定，汗~
        int requestFocusResult = mAudioManager.requestAudioFocus(mAudioFocusChangeListener,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

    }

    //zxzhong 暂停、播放完成或退到后台释放音频焦点
    public void releaseTheAudioFocus() {
        if (mAudioManager != null && mAudioFocusChangeListener != null) {
            mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
        }
    }

    public interface AudioListener {
        void start();
        void pause();
    }


}
