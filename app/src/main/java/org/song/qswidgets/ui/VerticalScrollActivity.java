package org.song.qswidgets.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.song.qswidgets.R;
import org.song.qswidgets.base.BaseActivity;
import org.song.qswidgets.util.ToastUtil;
import org.song.qswidgets.widget.VerticalPageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerticalScrollActivity extends BaseActivity {


    //todo  * Future:support adater and cycle
    @BindView(R.id.vertical_page_view)
    VerticalPageView verticalPageView;

    @Override
    protected Object getLayout() {
        return R.layout.activity_vertical_scroll;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvent() {
        verticalPageView.setPageListenner(new VerticalPageView.PageListenner() {
            @Override
            public void onPageChange(int nowPage) {
                ToastUtil.showToast(nowPage + "");
            }

            @Override
            public void onPageScrolled(int nextPage, float rate) {
                Log.e("onPageScrolled", nextPage + "/" + rate);
            }
        });
    }

}
