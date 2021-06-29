package com.youzhi.chatdemo.chat.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.File;

/**
 * @author gaoyuheng
 * @description:
 * @date :2020/12/11 17:23
 */
public class MediaPlayerHelp {

    public static MediaPlayer initMediaPlay(MediaListener mediaListener, File file) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(mediaListener);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mediaListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mediaPlayer;
    }

    public static MediaPlayer initMediaPlay(MediaListener mediaListener, String uri) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(mediaListener);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mediaListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mediaPlayer;
    }

    public static MediaPlayer pause(MediaPlayer mediaPlayer) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        } catch (Exception e) {

        }

        return mediaPlayer;
    }

    public static MediaPlayer reStart(MediaPlayer mediaPlayer) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        } catch (Exception e) {

        }

        return mediaPlayer;
    }

    public interface MediaListener extends MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {


    }

    public static void destroyMedia(MediaPlayer mediaPlayer) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            }
        } catch (Exception e) {

        }


    }
}
