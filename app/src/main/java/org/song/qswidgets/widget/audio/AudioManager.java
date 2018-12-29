package org.song.qswidgets.widget.audio;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 录音管理类
 */
public class AudioManager {

    private MediaRecorder mRecorder;
    private String mDirString;//存放录音文件夹地址  文件名字随机生成
    private String recordFilePathString;

    private boolean isPrepared;// 是否准备好了

    private static AudioManager mInstance;

    private AudioManager() {
    }

    private void setmDirString(String mDirString) {
        this.mDirString = mDirString;
    }

    public static AudioManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager();
                }
            }
        }
        mInstance.setmDirString(dir);
        return mInstance;

    }

    // 准备方法
    public void recordStart() throws IllegalStateException, IOException {
        // 一开始应该是false的
        isPrepared = false;
        release();
        File dir = new File(mDirString);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, generalFileName());
        recordFilePathString = file.getAbsolutePath();

        mRecorder = new MediaRecorder();
        // 设置输出文件
        mRecorder.setOutputFile(recordFilePathString);
        // 设置meidaRecorder的音频源是麦克风
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置文件音频的输出格式为amr
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        // 设置音频的编码格式为amr
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 严格遵守google官方api给出的mediaRecorder的状态流程图
        mRecorder.prepare();
        mRecorder.start();
        // 准备结束
        isPrepared = true;
    }


    // 录制结束
    public String recordStop() {
        release();
        String path = recordFilePathString;
        recordFilePathString = null;
        return path;
    }

    //  cancel方法 删除录音文件，
    public void recordCancel() {
        release();
        if (recordFilePathString != null) {
            File file = new File(recordFilePathString);
            file.delete();
            recordFilePathString = null;
        }
    }


    // 获得声音的level
    public int getVoiceLevel(int maxLevel) {
        // mRecorder.getMaxAmplitude()这个是音频的振幅范围，值域是1-32767
        if (isPrepared) {
            try {
                //设置灵敏一点
                int imaxLevel = (int) (maxLevel * 1.5);
                // 取证+1，否则去不到7
                imaxLevel = imaxLevel * mRecorder.getMaxAmplitude() / 32768 + 1;
                if (imaxLevel > maxLevel)
                    imaxLevel = maxLevel;
                return imaxLevel;
            } catch (Exception e) {
            }
        }

        return 1;
    }

    // 释放资源
    private void release() {
        // 严格按照api流程进行
        if (mRecorder != null) {
            try {
                mRecorder.stop();
                mRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mRecorder = null;
            }
        }
    }

    /**
     * 随机生成文件的名称
     */
    private String generalFileName() {
        return "QSAudio" + System.currentTimeMillis() + ".amr";
    }
}
