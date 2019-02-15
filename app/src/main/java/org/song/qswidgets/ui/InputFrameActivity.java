package org.song.qswidgets.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.song.qswidgets.R;
import org.song.qswidgets.widget.InputFrameView;


public class InputFrameActivity extends Activity {

    InputFrameView inputFrameView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_frame);
        initView();
    }

    public void initView() {
        inputFrameView = findViewById(R.id.number_code_view);
        inputFrameView.setOnNumberInputListener(new InputFrameView.OnInputListener() {
            @Override
            public void onInputFinish() {
                String mInputCode = inputFrameView.getInputCode();
                inputFrameView.hideInputMethod();
                Toast.makeText(InputFrameActivity.this, mInputCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInputIng() {

            }
        });
        inputFrameView.showInputMethod();

        CheckBox checkBox = findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                inputFrameView.setPwdMode(isChecked);
            }
        });
    }
}
