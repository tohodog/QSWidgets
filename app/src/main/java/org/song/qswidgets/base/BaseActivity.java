package org.song.qswidgets.base;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

import butterknife.ButterKnife;

/**
 * 所有Activity父类
 *
 * @author song
 */
public abstract class BaseActivity extends AppCompatActivity implements PermissionListener {

    protected Handler handler;
    protected LayoutInflater inflater;
    protected boolean isDestroy;
    //window->decorViewGroup->contentParent->contentView
    protected ViewGroup decorViewGroup;
    protected ViewGroup contentParent;
    protected View contentView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        Object o = getLayout();
        if (o instanceof Integer)
            setContentView((int) o);
        else if (o instanceof View)
            setContentView((View) o);
        decorViewGroup = (ViewGroup) getWindow().getDecorView();
        contentParent = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        contentView = contentParent.getChildAt(0);
        ButterKnife.bind(this);
        initData(savedInstanceState);
        initViews();
        initEvent();
    }


    protected abstract Object getLayout();//设置布局

    protected abstract void initData(Bundle savedInstanceState);//初始化数据

    protected abstract void initViews();//初始化控件

    protected abstract void initEvent();//注册监听设配器等


    public void startActivityForResult(Class<? extends Activity> clz, int code) {
        startActivityForResult(clz, code, null);
    }

    public void startActivityForResult(Class<? extends Activity> clz, int code, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, code);
    }

    public void startActivity(Class<? extends Activity> clz) {
        startActivity(clz, null);
    }

    public void startActivity(Class<? extends Activity> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    public void runUIDelayed(Runnable run, int de) {
        if (handler == null)
            handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(run, de);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }

    @Override
    public void finish() {
        super.finish();
    }

    //=====================================权限==========================================


    protected void permission(int code, String... permission) {
        AndPermission.with(this)
                .requestCode(code)
                .permission(permission)
                .rationale(new RationaleListener() {
                               @Override
                               public void showRequestPermissionRationale(int i, Rationale rationale) {
                                   AndPermission.rationaleDialog(BaseActivity.this, rationale).show();
                               }
                           }
                )
                .send();
    }

    //申请权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[]
                                                   grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onSucceed(int i, List<String> list) {

    }

    @Override
    public void onFailed(int i, List<String> list) {
        if (AndPermission.hasAlwaysDeniedPermission(this, list))
            AndPermission.defaultSettingDialog(this, i).show();
    }

}
