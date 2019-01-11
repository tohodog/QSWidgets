package org.song.qswidgets.ui;

import android.graphics.BitmapFactory;
import android.os.Bundle;


import org.song.qswidgets.R;
import org.song.qswidgets.base.BaseActivity;
import org.song.qswidgets.util.ToastUtil;
import org.song.qswidgets.widget.DragImageCaptchaView;

import butterknife.BindView;

/**
 * Created by song on 2018/9/7.
 * 拖曳验证码
 */
public class DragImageCodeActivity extends BaseActivity {


    @BindView(R.id.dragView)
    DragImageCaptchaView dragView;

    @Override
    protected Object getLayout() {
        return R.layout.activity_drag_image_code;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }


    @Override
    protected void initViews() {
        dragView.setUp(BitmapFactory.decodeResource(getResources(), R.drawable.drag_cover),
                BitmapFactory.decodeResource(getResources(), R.drawable.drag_block),
                BitmapFactory.decodeResource(getResources(), R.drawable.drag_cover_c),
                49);
    }

    @Override
    protected void initEvent() {
        dragView.setDragListenner(new DragImageCaptchaView.DragListenner() {
            @Override
            public void onDrag(float proportion, int x) {
                ToastUtil.showToast(proportion + "," + x);
                if (Math.abs(proportion - 0.637) > 0.012)
                    dragView.fail();
                else {
                    dragView.ok();
                    runUIDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dragView.reset();
                        }
                    }, 2000);
                }
            }


        });
    }

}
