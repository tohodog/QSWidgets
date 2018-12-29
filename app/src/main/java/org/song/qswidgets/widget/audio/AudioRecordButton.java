package org.song.qswidgets.widget.audio;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;


/**
 * 按住实现语音录制的按钮
 */
public class AudioRecordButton extends android.support.v7.widget.AppCompatButton {

    private static final int DISTANCE_Y_CANCEL = 110;

    public static final int STATE_START = 1;//开始
    public static final int STATE_RECORDING = 2;// 正在录音
    public static final int STATE_WANT_TO_CANCEL = 3;// 希望取消
    public static final int STATE_TOO_SHORT = 4;// 太短
    public static final int STATE_STOP = 5;// 默认的状态


    private int nowState = -1; // 当前的状态
    private final int MIN_LEN = 666;//最小录音长度ms
    //private final int MAX_LEN = 1000*60;//最大录音长度ms

    private boolean isRecording = false;// 已经开始录音
    private AudioManager mAudioManager;
    private long recTime;//標記時間
    private int level;//音量
    private ExRecorderListener exRecorderListener;//
    private RecorderListener recorderListener;//
    private AudioDialog audioDialog;

    public AudioRecordButton(Context context) {
        this(context, null);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAudioManager = AudioManager.getInstance(context.getExternalCacheDir().getPath());
        audioDialog = new AudioDialog(context);
        reset();
    }


    /**
     * 屏幕的触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();// 获得x轴坐标
        int y = (int) event.getY();// 获得y轴坐标

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                recordStart();//放在这里
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    // 根据x,y的坐标看是否需要取消
                    if (wantToCancle(x, y))
                        changeUI(STATE_WANT_TO_CANCEL);
                    else
                        changeUI(STATE_RECORDING);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isRecording)
                    finishRecord();
                break;
        }
        return super.onTouchEvent(event);
    }

    //开始
    private void recordStart() {
        try {
            changeUI(STATE_START);
            mAudioManager.recordStart();
            recTime = System.currentTimeMillis();
            if (recorderListener != null)
                recorderListener.recordStart();
            isRecording = true;
            handler.post(run);//开始监听音量
        } catch (Exception e) {
            e.printStackTrace();
            reset();
            if (recorderListener != null)
                recorderListener.recordFail();
        }
    }

    //结束
    private void finishRecord() {
        long len = System.currentTimeMillis() - recTime;
        if (len < MIN_LEN) {
            //Toast.makeText(getContext(), "录音时间过短!", Toast.LENGTH_LONG);
            mAudioManager.recordCancel();
            changeUI(STATE_TOO_SHORT);
            if (recorderListener != null)
                recorderListener.recordCancel();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reset();
                }
            }, 500);
        } else if (nowState == STATE_RECORDING) { // 正在录音的时候，结束
            String path = mAudioManager.recordStop();
            if (recorderListener != null)
                recorderListener.recordOK(len, path);
            reset();
        } else if (nowState == STATE_WANT_TO_CANCEL) { // 想要取消
            mAudioManager.recordCancel();
            if (recorderListener != null)
                recorderListener.recordCancel();
            reset();
        }

    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        isRecording = false;
        changeUI(STATE_STOP);
    }


    /**
     * 改变
     */
    private void changeUI(int state) {
        if (nowState == state)
            return;
        changeDialogListener.changeStatus(state);
        switch (state) {
            case STATE_TOO_SHORT:
            case STATE_STOP:
                setText("按住 说话");
                break;

            case STATE_START:
            case STATE_RECORDING:
                setText("松开 结束");
                break;

            case STATE_WANT_TO_CANCEL:
                setText("松开手指 取消发送");
                break;
        }
        nowState = state;
    }

    /*
     * 获取音量大小的线程
     */
    private Handler handler = new Handler();
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                level = mAudioManager.getVoiceLevel(7);
                changeDialogListener.voiceLevel(level);
                handler.postDelayed(run, 100);
            }
        }
    };

    private boolean wantToCancle(int x, int y) {
        if (x < 0 || x > getWidth()) { // 超过按钮的宽度
            return true;
        }
        // 超过按钮的高度
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }

        return false;
    }

    /**
     * 录音完成监听
     */
    public interface RecorderListener {
        void recordOK(long seconds, String filePath);//顺利完成录音

        void recordFail();//出错 权限问题

        void recordCancel();//

        void recordStart();
    }

    /**
     * 设置录音UI的监听
     */
    public interface ExRecorderListener {
        void voiceLevel(int level);//音量

        void changeStatus(int status);//5种状态
    }

    public void setRecorderListener(RecorderListener recorderOKListener) {
        this.recorderListener = recorderOKListener;
    }

    public void setExRecorderListener(ExRecorderListener exRecorderListener) {
        this.exRecorderListener = exRecorderListener;
    }

    //内部使用的
    private ExRecorderListener changeDialogListener = new ExRecorderListener() {

        @Override
        public void voiceLevel(int level) {
            if (exRecorderListener != null) exRecorderListener.voiceLevel(level);
            audioDialog.updateVoiceLevel(level);
        }

        @Override
        public void changeStatus(int status) {
            if (exRecorderListener != null) exRecorderListener.changeStatus(status);

            switch (status) {
                case STATE_START:
                    audioDialog.showRecordingDialog();

                    break;
                case STATE_RECORDING:
                    audioDialog.recording();

                    break;
                case STATE_WANT_TO_CANCEL:
                    audioDialog.wantToCancel();

                    break;
                case STATE_TOO_SHORT:
                    audioDialog.tooShort();

                    break;
                case STATE_STOP:
                    audioDialog.dimissDialog();

                    break;
            }
        }

    };
}