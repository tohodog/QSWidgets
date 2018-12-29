package org.song.qswidgets.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;


import org.song.qswidgets.R;
import org.song.qswidgets.base.BaseActivity;
import org.song.qswidgets.util.PixelUtil;
import org.song.qswidgets.util.TimeUtil;
import org.song.qswidgets.widget.roll.RollView;

import java.util.List;

import butterknife.BindViews;

public class RollTimeActivity extends BaseActivity {


    @BindViews({R.id.rollview1, R.id.rollview2, R.id.rollview3, R.id.rollview4, R.id.rollview5, R.id.rollview6})
    List<RollView> rollviews;

    @Override
    protected Object getLayout() {
        return R.layout.activity_roll_time;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void initViews() {

        for (RollView r : rollviews) {
            r.setSize(PixelUtil.dp2px(40));
            r.setGap(PixelUtil.dp2px(8));
        }

        rollviews.get(0).setContentArr(new String[]{"0", "1", "2"});
        rollviews.get(1).setContentArr(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
        rollviews.get(2).setContentArr(new String[]{"0", "1", "2", "3", "4", "5"});
        rollviews.get(3).setContentArr(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
        rollviews.get(4).setContentArr(new String[]{"0", "1", "2", "3", "4", "5",});
        rollviews.get(5).setContentArr(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
    }

    @Override
    protected void initEvent() {
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(run, 1000);
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            String s = TimeUtil.getNowFormatTime("HHmmss");
            char[] chars = s.toCharArray();

            int index = 0;
            for (char c : chars) {
                rollviews.get(index).onScroll(c - 48);
                index++;
            }
            //半秒时刷新,保证不会跳秒
            handler.postDelayed(run, 1500 - System.currentTimeMillis() % 1000);
        }
    };

}
