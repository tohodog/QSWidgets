package org.song.qswidgets.manage;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

import java.io.IOException;

/**
 * 播放语音
 */
public class VoicePlayManager {

    private static MediaPlayer mediaPlayer;
    private static VoiceListener preVoiceListener;

    /**
     * @param filePath 音频文件
     */
    public static void playVoice(String filePath, VoiceListener voiceListener) {
        init(voiceListener);
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    /**
     * @param raw 资源文件
     */
    public static void playVoice(Context context, int raw, VoiceListener voiceListener) {
        init(voiceListener);
        try {
            AssetFileDescriptor file = context.getResources().openRawResourceFd(raw);
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    private static void init(VoiceListener voiceListener) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnErrorListener(new OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mediaPlayer.reset();
                    return false;
                }
            });
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (preVoiceListener != null) preVoiceListener.Completion();
                }
            });
        } else {
            if (preVoiceListener != null) preVoiceListener.cancel();
            mediaPlayer.reset();
        }
        preVoiceListener = voiceListener;
    }

    public interface VoiceListener {

        void Completion();

        void cancel();

        void pause();

        void start();

    }

    /**
     * 是否正在播放音频
     */
    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    /**
     * 播放
     */
    public static void start() {
        if (!isPlaying()) {
            if (mediaPlayer != null)
                mediaPlayer.start();
            if (preVoiceListener != null) preVoiceListener.start();
        }
    }

    /**
     * 重新播放
     */
    public static void reStart() {
        seekTo(0);
        start();
    }

    /**
     * 暂停
     */
    public static void pause() {
        if (isPlaying()) {
            mediaPlayer.pause();
            if (preVoiceListener != null) preVoiceListener.pause();
        }
    }

    public static void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }


    /**
     * 释放mediaplayer占用的资源
     */
    public static void release() {
        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = null;
        if (preVoiceListener != null) preVoiceListener.cancel();
        preVoiceListener = null;
    }
}
