package org.song.qswidgets.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;

import org.song.qswidgets.R;
import org.song.qswidgets.base.BaseActivity;
import org.song.qswidgets.util.PixelUtil;
import org.song.qswidgets.widget.DiyStyleTextView;

import butterknife.BindView;

public class DiyTextActivity extends BaseActivity {


    @BindView(R.id.diytextview)
    DiyStyleTextView diytextview;

    @Override
    protected Object getLayout() {
        return R.layout.activity_diy_text;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initViews() {


        diytextview.setText("[d_doge]QSWidgets-666");
        diytextview.append("\n[d_doge]QSWidgets-666");
        diytextview.append("\n[d_doge]QSWidgets-666");
        diytextview.append("\n[d_doge]QSWidgets-666");
        diytextview.append("\n[d_doge]QSWidgets-666");
        diytextview.append("\n[d_doge]QSWidgets-666");

    }

    Handler handler = new Handler();
    boolean flag;

    @Override
    protected void initEvent() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.d_doge);
        final Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, PixelUtil.dp2px(28), PixelUtil.dp2px(28), true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                flag = !flag;
                if (flag)
                    set(scaleBitmap, 0xff2CA298, 0xffff6666, 0xffFF4081);
                else
                    set(scaleBitmap, 0xffff6666, 0xffFF4081, 0xff2CA298);

                handler.postDelayed(this, 500);
            }
        }, 500);
    }

    private void set(Bitmap scaleBitmap, int a, int b, int c) {
        diytextview.reset();
        diytextview.addColorRegex("[\\d]+", a)
                .addColorRegex("[a-zA-Z]+", b)
                .addColorRegex("QS", c)
                .addImageRegex("\\[d_doge\\]", scaleBitmap);
        diytextview.setText(diytextview.getText().toString());//需要tostring,不然一直叠加效果,boom
    }

}
