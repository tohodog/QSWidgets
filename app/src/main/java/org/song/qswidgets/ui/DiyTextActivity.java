package org.song.qswidgets.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

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


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.d_doge);
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap,PixelUtil.dp2px(28), PixelUtil.dp2px(28), true);
        diytextview.addColorRegex("[\\d]+", 0xff2CA298)
                .addColorRegex("[a-zA-Z]+", 0xffff6666)
                .addColorRegex("[QS]+", 0xffFF4081)
                .addImageRegex("\\[d_doge\\]", scaleBitmap);


        diytextview.setText("[d_doge]QSWidgets-666");
        diytextview.append("\n[d_doge]QSWidgets-666");
        diytextview.append("\n[d_doge]QSWidgets-666");
        diytextview.append("\n[d_doge]QSWidgets-666");
        diytextview.append("\n[d_doge]QSWidgets-666");
        diytextview.append("\n[d_doge]QSWidgets-666");

    }

    @Override
    protected void initEvent() {

    }

}
