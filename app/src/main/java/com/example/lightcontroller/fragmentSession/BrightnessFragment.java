package com.example.lightcontroller.fragmentSession;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.lightcontroller.MainActivity;
import com.example.lightcontroller.R;

import org.jetbrains.annotations.Contract;

public class BrightnessFragment extends Fragment {

    private Button brightCurrent;
    private SeekBar brightSeekBar;
    private Handler handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        handler = mainActivity.handler;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brightness, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewByIds();
        initBrightnessSeekBar();
        initCurrentButtonClick();
    }

    private void findViewByIds() {
        brightCurrent = getActivity().findViewById(R.id.bright_current);
        brightSeekBar = getActivity().findViewById(R.id.bright_seekbar);
    }

    private void initBrightnessSeekBar() {
        brightSeekBar.setMax(500);
        // 默认值暂时定为200
        brightSeekBar.setProgress(200);
        brightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateValueToDevice(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initCurrentButtonClick() {
        brightCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "长按当前值来输入亮度值",
                        Toast.LENGTH_SHORT).show();
            }
        });
        brightCurrent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showValueChangeDialog();
                return true;
            }
        });
    }

    private void showValueChangeDialog() {
        final Dialog valueDialog = new Dialog(getActivity(), R.style.centerDialog);
        valueDialog.setCancelable(false);
        valueDialog.setCanceledOnTouchOutside(true);
        Window window = valueDialog.getWindow();
        View view = View.inflate(getActivity(), R.layout.dialog_valueinput_bright, null);
        final EditText valueInput = view.findViewById(R.id.dialog_value_bright_input);
        view.findViewById(R.id.dialog_value_bright_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valueDialog.dismiss();
            }
        });
        view.findViewById(R.id.dialog_value_bright_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueInputString = valueInput.getText().toString();
                // 检查是否输入为空
                if (!isNullEmptyBlank(valueInputString)) {
                    int brightCurrentValue = -1;
                    // 尝试转化为整数
                    try {
                        brightCurrentValue = Integer.valueOf(valueInputString);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), "输入格式有误，请检查！", Toast.LENGTH_SHORT).show();
                    }
                    // 检查是否转化成功
                    if (brightCurrentValue != -1) {
                        // 检查是否在正确的取值范围内
                        if (brightCurrentValue >= 0 && brightCurrentValue <= 500) {
                            brightSeekBar.setProgress(brightCurrentValue);
                            updateValueToDevice(brightCurrentValue);
                            valueDialog.dismiss();
                        }
                        else {
                            Toast.makeText(getActivity().getApplicationContext(), "请输入500及以内的正整数！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setContentView(view);
        valueDialog.show();
    }

    // 更新当前值的显示并发送给Activity
    private void updateValueToDevice(int brightCurrentValue) {
        String brightCurrentText = "当前值："+brightCurrentValue+"lx";
        brightCurrent.setText(brightCurrentText);
        Message message = new Message();
        message.obj = "BRIGHTNESS UPDATE/" + brightCurrentValue;
        handler.sendMessage(message);
    }

    @Contract("null -> true")
    private boolean isNullEmptyBlank(String str){
        if (str == null || "".equals(str) || "".equals(str.trim())){
            return true;
        }
        return false;
    }

}
