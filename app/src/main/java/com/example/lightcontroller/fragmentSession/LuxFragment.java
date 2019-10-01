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

public class LuxFragment extends Fragment {

    private Button luxCurrent;
    private SeekBar luxSeekBar;
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
        return inflater.inflate(R.layout.fragment_lux, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewByIds();
        initLuxSeekBar();
        initCurrentButtonClick();
    }

    private void findViewByIds() {
        luxCurrent = getActivity().findViewById(R.id.lux_current);
        luxSeekBar = getActivity().findViewById(R.id.lux_seekbar);
    }

    private void initLuxSeekBar() {
        luxSeekBar.setMax(500);
        luxSeekBar.setProgress(200);
        luxSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateValueToDevice(3000+progress*6);
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
        luxCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "长按当前值来输入色温值",
                        Toast.LENGTH_SHORT).show();
            }
        });
        luxCurrent.setOnLongClickListener(new View.OnLongClickListener() {
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
        View view = View.inflate(getActivity(), R.layout.dialog_valueinput_lux, null);
        final EditText valueInput = view.findViewById(R.id.dialog_value_lux_input);
        view.findViewById(R.id.dialog_value_lux_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valueDialog.dismiss();
            }
        });
        view.findViewById(R.id.dialog_value_lux_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueInputString = valueInput.getText().toString();
                // 检查是否输入为空
                if (!isNullEmptyBlank(valueInputString)) {
                    int luxCurrentValue = -1;
                    // 尝试转化为整数
                    try {
                        luxCurrentValue = Integer.valueOf(valueInputString);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), "输入格式有误，请检查！", Toast.LENGTH_SHORT).show();
                    }
                    // 检查是否转化成功
                    if (luxCurrentValue != -1) {
                        // 检查是否在正确的取值范围内
                        if (luxCurrentValue >= 3000 && luxCurrentValue <= 6000) {
                            luxSeekBar.setProgress((int)((luxCurrentValue-3000)/6));
                            updateValueToDevice(luxCurrentValue);
                            valueDialog.dismiss();
                        }
                        else {
                            Toast.makeText(getActivity().getApplicationContext(), "请输入3000到6000内的正整数！", Toast.LENGTH_SHORT).show();
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
    private void updateValueToDevice(int luxCurrentValue) {
        String luxCurrentText = "当前值："+luxCurrentValue+"K";
        luxCurrent.setText(luxCurrentText);
        Message message = new Message();
        message.obj = "LUX UPDATE/" + luxCurrentValue;
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
