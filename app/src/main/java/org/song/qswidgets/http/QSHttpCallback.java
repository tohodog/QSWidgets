package org.song.qswidgets.http;


import org.song.http.framework.HttpCallbackEx;
import org.song.http.framework.HttpException;
import org.song.http.framework.ResponseParams;
import org.song.qswidgets.http.model.result.BaseNetM;

import java.io.Serializable;

/*
 * Created by song on 2018/8/29.
 * 根据自己的项目对回调进行再包装
 */
public abstract class QSHttpCallback implements HttpCallbackEx {

    private BaseNetM baseNetModel;

    //联网成功200状态码
    @Override
    public void onSuccess(ResponseParams response) {
        if (response.parserObject() instanceof BaseNetM) {
            baseNetModel = response.parserObject();
            if (!baseNetModel.isSuccess()) {
                checkAuth(baseNetModel);
                onFailure(HttpException.Custom(baseNetModel.getMsg()).responseParams(response));
                return;
            }
        }
        onComplete(response);
    }

    protected <T extends Serializable> T getData() {
        return (T) baseNetModel.getData();
    }

    protected int getStatus() {
        return baseNetModel.getStatus();
    }

    public abstract void onComplete(ResponseParams response);

    @Override
    public void onFailure(HttpException e) {
        //默认弹出错误提示,需要自己处理的覆盖此方法
        e.show();
    }

    //开始联网 可以显示进度框等
    @Override
    public void onStart() {

    }

    //联网结束
    @Override
    public void onEnd() {

    }

    //是否销毁
    @Override
    public boolean isDestroy() {
        return false;
    }

    private void checkAuth(BaseNetM baseNetModel) {
        //鉴权失败 1退出登录
        if (baseNetModel.getStatus() == 4101) {
            //ToastUtil.showToast("登陆已失效,请重新登陆");
        }
    }

}
