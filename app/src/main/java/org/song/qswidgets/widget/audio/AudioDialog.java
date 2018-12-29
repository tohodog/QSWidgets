package org.song.qswidgets.widget.audio;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.song.qswidgets.R;


/**
 *
 */
public class AudioDialog {

    /**
     * 以下为dialog的初始化控件，包括其中的布局文件
     */
    private Dialog mDialog;

    private ImageView mIcon;
    private ImageView mVoice;

    private TextView mLable;

    private Context mContext;

    public AudioDialog(Context context) {
        mContext = context;
    }

    public void showRecordingDialog() {
        dimissDialog();
        if (!(mContext instanceof Activity))
            return;
        mDialog = new Dialog(mContext, R.style.Theme_Dialog_Transparent);
        // 用layoutinflater来引用布局
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_audio, null);
        mDialog.setContentView(view);

        mIcon = (ImageView) mDialog.findViewById(R.id.dialog_icon);
        mVoice = (ImageView) mDialog.findViewById(R.id.dialog_voice);
        mLable = (TextView) mDialog.findViewById(R.id.recorder_dialogtext);
        mDialog.show();
        recording();
    }

    /**
     * 设置正在录音时的dialog界面
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.mipmap.audio_mic);
            mLable.setTextColor(Color.WHITE);
            mLable.setText("手指上滑，取消发送");
        }
    }

    /**
     * 取消界面
     */
    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mIcon.setImageResource(R.mipmap.audio_cancel);

            mLable.setTextColor(Color.parseColor("#FF6666"));
            mLable.setText("松开手指，取消发送");
        }
    }

    // 时间过短
    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mIcon.setImageResource(R.mipmap.audio_to_short);

            mLable.setTextColor(Color.parseColor("#FF6666"));
            mLable.setText("录音时间过短");
        }
    }

    // 隐藏dialog
    public void dimissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
            int resId = mContext.getResources().getIdentifier("audio_v" + level,
                    "mipmap", mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }

}
