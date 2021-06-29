package com.youzhi.chatdemo.chat.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;


import com.youzhi.chatdemo.MyApplication;
import com.youzhi.chatdemo.utils.FileUtil;
import com.youzhi.chatdemo.utils.RxJavaUtils;

import java.io.File;
import java.io.RandomAccessFile;

import io.reactivex.disposables.Disposable;

/**
 * @author gaoyuheng
 * @description: 录音工具类，包含暂停功能，录制的原始文件格式为.pcm 内部已经转成了wav格式
 * @date :2020/10/23 16:57
 */
public class AudioRecordManager implements RxJavaUtils.SimpleListener<String,String> {

    private Disposable mDisposable;
    private AudioRecord mAudioRecord;
    private boolean mWhetherRecord;
    public static final String recordDirectory = "records";
    public static final String saveFileDirectory = FileUtil.getDiskFileDir(MyApplication.getContext(), recordDirectory);
    private OnRecordListener mOnRecordListener;
    private OnRecordVolumeListener mOnRecordVolumeListener;
    private File pcmFile;
    private String wavFilePath;

    public static int STATUS_NO_START = 1;
    public static int STATUS_STARTING = 2;
    public static int STATUS_FINISH = 3;
    public static int STATUS_PAUSE = 4;
    private int status_record = STATUS_NO_START;//记录当前录音的状态
    private long lastVolumeTimes;
    private static final String ERROR_MESSAGE = "麦克风被占用";

    public int getCurrentStatus() {

        return status_record;
    }

    public static synchronized AudioRecordManager getInstance() {

        return new AudioRecordManager();
    }

    private AudioRecordManager() {

    }

    //一帧大小
    private Integer mRecordBufferSize;

    //初始化每一帧大小
    private void initMinBufferSize() {
        /*
         *
         *     只能在4000到192000的范围内取值

         *
         *
         * */
        mRecordBufferSize = AudioRecord.getMinBufferSize(8000
                , AudioFormat.CHANNEL_IN_MONO
                , AudioFormat.ENCODING_PCM_16BIT);
    }

    /*
     * 初始化record
     * */
    private void initAudioRecord() {
        initMinBufferSize();
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC
                , 8000
                , AudioFormat.CHANNEL_IN_MONO
                , AudioFormat.ENCODING_PCM_16BIT
                , mRecordBufferSize);
    }

    public String getAudioPath() {
        return saveFileDirectory + File.separator + "record_head.wav";

    }


    @Override
    public String buildData(String... param) {
        if (mAudioRecord != null) {
            mAudioRecord.startRecording();//开始录制
        }
        try {
            RandomAccessFile mRandomAccessFile = new RandomAccessFile(pcmFile, "rw");
            byte[] b = new byte[mRecordBufferSize / 4];
            //开始录制音频
            if (mAudioRecord != null) {
                mAudioRecord.startRecording();
                if (mAudioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                    return ERROR_MESSAGE;
                }
            }

            //判断是否正在录制
            while (mWhetherRecord) {
                if (mAudioRecord != null) {
                    mAudioRecord.read(b, 0, b.length);

                }
                //获取音频音量
                if (mOnRecordVolumeListener != null) {
                    double volume = calculateVolume(b);
                    int adoptVolume = 0;
                    if (volume <= 0) {
                        adoptVolume = 0;
                    } else if (volume > 12) {
                        adoptVolume = (int) ((volume - 12) / 2);

                    } else {
                        adoptVolume = 1;
                    }
                    Log.e("LOGCAT", "adoptVolume==" + adoptVolume);
                    if (System.currentTimeMillis() - lastVolumeTimes >= 100) {
                        mOnRecordVolumeListener.onRecordVolume(adoptVolume);
                        lastVolumeTimes = System.currentTimeMillis();
                    }

                }

                //始终向文件末尾处追加内容
                mRandomAccessFile.seek(mRandomAccessFile.length());
                mRandomAccessFile.write(b, 0, b.length);
            }
            //停止录制
            if (mAudioRecord != null) {
                mAudioRecord.stop();
            }
            mRandomAccessFile.close();

            if (status_record != STATUS_PAUSE) {
                release();
                addHeadData(pcmFile.getAbsolutePath(), wavFilePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return wavFilePath;

    }

    @Override
    public void rxSuccess(String data) {
        if (mOnRecordListener != null) {
            Log.e("OkGo", "录音完成");
            if (TextUtils.equals(data, ERROR_MESSAGE)) {
                mOnRecordListener.onRecordFailure(data);
            } else if (new File(data).exists()) {
                mOnRecordListener.onRecordFinish(data);
            } else {
                mOnRecordListener.onRecordFailure("录音文件不存在");
            }


        }
    }

    private double calculateVolume(byte[] buffer) {


        double sumVolume = 0.0;

        double avgVolume = 0.0;

        double volume = 0.0;

        for (int i = 0; i < buffer.length; i += 2) {

            int v1 = buffer[i] & 0xFF;

            int v2 = buffer[i + 1] & 0xFF;

            int temp = v1 + (v2 << 8);// 小端

            if (temp >= 0x8000) {

                temp = 0xffff - temp;

            }

            sumVolume += Math.abs(temp);

        }

        avgVolume = sumVolume / buffer.length / 2;

        volume = Math.log10(1 + avgVolume) * 10;


        return volume;

    }

    @Override
    public void rxError(Throwable e) {
        if (mOnRecordListener != null) {
            mOnRecordListener.onRecordFailure(e.getMessage());

        }
    }

    @Override
    public void rxDoOnSubscribe(Disposable disposable) {
        mDisposable = disposable;
    }

    public interface OnRecordListener {

        void onRecordFinish(String path);

        void onRecordFailure(String message);
    }

    public interface OnRecordVolumeListener {
        /**
         * @param volume 范围0-5 异步调用需要自行回到主线程去处理
         */
        void onRecordVolume(int volume);
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        mOnRecordListener = onRecordListener;
    }

    public void setOnRecordVolumeListener(OnRecordVolumeListener onRecordVolumeListener) {
        this.mOnRecordVolumeListener = onRecordVolumeListener;
    }

    /*
     *
     *  开始录音
     *
     * */
    public void startRecord() {
        if (mAudioRecord == null) {
            initAudioRecord();
        }

        File file = new File(saveFileDirectory);
        if (!file.exists()) {
            file.mkdirs();
        }
        final String pcmFilePath = "record.pcm";
        wavFilePath = saveFileDirectory + File.separator + "record_head.wav";
        //xxx.pcm
        pcmFile = new File(saveFileDirectory, pcmFilePath);
        if (status_record != STATUS_PAUSE) {
            if (pcmFile.exists()) {
                pcmFile.delete();
            }
        }

        mWhetherRecord = true;
        status_record = STATUS_STARTING;
        RxJavaUtils.executeAsync(this);

    }

    public void pauseRecord() {
        status_record = STATUS_PAUSE;
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mWhetherRecord = false;
    }

    public void stopRecord() {
        lastVolumeTimes = 0;
        if (status_record == STATUS_PAUSE) {
            endToWavFile();
        } else {
            mWhetherRecord = false;
        }
        status_record = STATUS_FINISH;
    }

    /*结束并转换文件为wav*/
    private void endToWavFile() {
        release();
        //添加音频头部信息并且转成wav格式
        RxJavaUtils.executeAsync(new RxJavaUtils.SimpleListener<File,String>() {
            @Override
            public File buildData(String... param) {

                return addHeadData(pcmFile.getAbsolutePath(), wavFilePath);
            }

            @Override
            public void rxSuccess(File data) {
                if (mOnRecordListener != null) {
                    Log.e("OkGo", "录音完成");
                    if (data.exists()) {
                        mOnRecordListener.onRecordFinish(data.getAbsolutePath());
                    } else {
                        mOnRecordListener.onRecordFailure("录音文件不存在");
                    }

                }
            }

            @Override
            public void rxError(Throwable e) {

            }

            @Override
            public void rxDoOnSubscribe(Disposable disposable) {

            }
        });

    }

    private void release() {
        if (mAudioRecord != null) {
            mAudioRecord.release();
            mAudioRecord = null;
        }

    }

    private File addHeadData(String filePath, String outputWavFIlePath) {
        File pcmFile = new File(filePath);
        //xxx.wav
        File handlerWavFile = new File(outputWavFIlePath);
        PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        pcmToWavUtil.pcmToWav(pcmFile.toString(), handlerWavFile.toString());
        return pcmFile;
    }

    public boolean isRecording() {
        return mWhetherRecord;
    }
}
