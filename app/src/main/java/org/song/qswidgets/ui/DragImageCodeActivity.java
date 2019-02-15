package org.song.qswidgets.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Base64;


import com.alibaba.fastjson.JSONObject;

import org.song.http.QSHttp;
import org.song.http.framework.HttpException;
import org.song.http.framework.ResponseParams;
import org.song.qswidgets.R;
import org.song.qswidgets.base.BaseActivity;
import org.song.qswidgets.http.QSHttpCallback;
import org.song.qswidgets.http.URL;
import org.song.qswidgets.http.model.result.JSONNetM;
import org.song.qswidgets.util.ToastUtil;
import org.song.qswidgets.widget.DragImageCaptchaView;

import butterknife.BindView;

/**
 * Created by song on 2018/9/7.
 * 拖曳验证码
 */
public class DragImageCodeActivity extends BaseActivity {


    @BindView(R.id.dragView)
    DragImageCaptchaView dragViewLocal;


    @BindView(R.id.dragView2)
    DragImageCaptchaView dragViewNet;
    String captcha_id;

    @Override
    protected Object getLayout() {
        return R.layout.activity_drag_image_code;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        QSHttp.get(URL.GET_CAPTCHA)
                .jsonModel(JSONNetM.class)
                .buildAndExecute(new QSHttpCallback() {

                    @Override
                    public void onComplete(ResponseParams response) {
                        JSONObject result = getData();
                        int y = result.getIntValue("y");
                        int height = result.getIntValue("height");
                        captcha_id = result.getString("captcha_id");
                        byte[] bytes_cover_complete = Base64.decode(result.getString("base64_cover_complete"), Base64.NO_PADDING | Base64.NO_WRAP);
                        byte[] bytes_cover = Base64.decode(result.getString("base64_cover"), Base64.NO_PADDING | Base64.NO_WRAP);
                        byte[] bytes_block = Base64.decode(result.getString("base64_block"), Base64.NO_PADDING | Base64.NO_WRAP);

                        dragViewNet.setUp(BitmapFactory.decodeByteArray(bytes_cover, 0, bytes_cover.length),
                                STROKE(BitmapFactory.decodeByteArray(bytes_block, 0, bytes_block.length)),
                                BitmapFactory.decodeByteArray(bytes_cover_complete, 0, bytes_cover_complete.length),
                                y);
                    }

                    @Override
                    public void onFailure(HttpException e) {
                        ToastUtil.showToast("加载本地验证码");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inScaled = false;
                        dragViewNet.setUp(BitmapFactory.decodeResource(getResources(), R.drawable.drag_cover, options),
                                BitmapFactory.decodeResource(getResources(), R.drawable.drag_block, options),
                                BitmapFactory.decodeResource(getResources(), R.drawable.drag_cover_c, options),
                                44);
                    }

                    //开始联网 可以显示进度框等
                    @Override
                    public void onStart() {
                    }

                    //联网结束
                    @Override
                    public void onEnd() {
                    }

                });

    }


    @Override
    protected void initViews() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        dragViewLocal.setUp(BitmapFactory.decodeResource(getResources(), R.drawable.drag_cover, options),
                BitmapFactory.decodeResource(getResources(), R.drawable.drag_block, options),
                BitmapFactory.decodeResource(getResources(), R.drawable.drag_cover_c, options),
                44);


    }

    @Override
    protected void initEvent() {
        dragViewLocal.setDragListenner(new DragImageCaptchaView.DragListenner() {
            @Override
            public void onDrag(float proportion, int x) {
                ToastUtil.showToast(proportion + "," + x);
                if (Math.abs(proportion - 0.637) > 0.012)
                    dragViewLocal.fail();
                else {
                    dragViewLocal.ok();
                    runUIDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dragViewLocal.reset();
                        }
                    }, 2000);
                }
            }


        });


        dragViewNet.setDragListenner(new DragImageCaptchaView.DragListenner() {
            @Override
            public void onDrag(float proportion, int x) {
                QSHttp.get(URL.GET_CAPTCHA_CHECK)
                        .param("captcha_id", captcha_id)
                        .param("x", x)
                        .jsonModel(JSONNetM.class)
                        .buildAndExecute(new QSHttpCallback() {

                            @Override
                            public void onComplete(ResponseParams response) {
                                dragViewNet.ok();
                                runUIDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        initData(null);
                                    }
                                }, 2000);
                            }

                            @Override
                            public void onFailure(HttpException e) {
                                e.show();
                                dragViewNet.fail();
                                if ("验证码已失效".equals(e.getPrompt()))
                                    initData(null);
                            }
                        });
            }
        });
    }


    public static Bitmap STROKE(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);

        Rect r = new Rect(0, 0, w, h);
        Paint p = new Paint();
        p.setColor(0xff888888);
        p.setAntiAlias(true);//设置画笔颜色
        p.setStrokeWidth(2);//线宽
        p.setStyle(Paint.Style.STROKE);
        canvas.drawRect(r, p);
        return newBitmap;
    }
}
