package org.song.qswidgets.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.song.qswidgets.R;
import org.song.qswidgets.base.BaseActivity;
import org.song.qswidgets.base.BaseRecyAdapter;
import org.song.qswidgets.widget.InputFrameView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    List<ClassModel> info = new ArrayList<>();
    Adpate adpate;


    @Override
    protected Object getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        info.add(new ClassModel("语音录制", AudioActivity.class));
        info.add(new ClassModel("滚动时间", RollTimeActivity.class));
        info.add(new ClassModel("密码/验证码输入框", InputFrameActivity.class));
        info.add(new ClassModel("风格TextView", DiyTextActivity.class));

        info.add(new ClassModel("滑块验证码", DragImageCodeActivity.class));

    }


    @Override
    protected void initViews() {

        GridLayoutManager g = new GridLayoutManager(this, 1);
        recyclerview.setLayoutManager(g);

    }

    @Override
    protected void initEvent() {
        recyclerview.setAdapter(adpate = new Adpate(this, info));
    }


    class Adpate extends BaseRecyAdapter<ClassModel> {


        Adpate(Context context, List<ClassModel> list) {
            super(context, list);
        }

        @Override
        public ViewHolder bindHolder(ViewGroup parent, LayoutInflater inflater, int viewType) {
            return new Hoder(inflater.inflate(R.layout.item_navigation, parent, false));
        }

        class Hoder extends ViewHolder<ClassModel> {


            @BindView(R.id.status_btn)
            AppCompatButton statusBtn;

            Hoder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            public void bindData(int position, final ClassModel o) {
                statusBtn.setText(o.name);
                statusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(context, o._class));
                    }
                });
            }

        }

    }

    class ClassModel {

        ClassModel(String name, Class _class) {
            this.name = name;
            this._class = _class;
        }

        String name;
        Class _class;
    }

}

