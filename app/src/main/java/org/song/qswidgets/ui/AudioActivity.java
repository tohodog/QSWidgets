package org.song.qswidgets.ui;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.song.qswidgets.R;
import org.song.qswidgets.base.BaseActivity;
import org.song.qswidgets.manage.VoicePlayManager;
import org.song.qswidgets.widget.audio.AudioRecordButton;

import java.io.File;

import butterknife.BindView;

public class AudioActivity extends BaseActivity {

    @BindView(R.id.audioBtn)
    AudioRecordButton audioBtn;


    @Override
    protected Object getLayout() {
        return R.layout.activity_audio;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        permission(0, Manifest.permission.RECORD_AUDIO);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvent() {

        audioBtn.setRecorderListener(new AudioRecordButton.RecorderListener() {
            @Override
            public void recordOK(long seconds, String filePath) {
                VoicePlayManager.playVoice(filePath, null);
                showToast("时长:" + seconds / 1000 + "s 大小:" + new File(filePath).length() / 1000.0 + "k");
            }

            @Override
            public void recordFail() {
                showToast("是否禁止了权限?");
            }

            @Override
            public void recordCancel() {
                showToast("cacel");
            }

            @Override
            public void recordStart() {
                showToast("start");
            }
        });

    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void onDestroy() {
        super.onDestroy();
        VoicePlayManager.release();
    }
}
