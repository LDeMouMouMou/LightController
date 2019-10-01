package com.example.lightcontroller.fragmentSession;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.lightcontroller.MainActivity;
import com.example.lightcontroller.R;

public class SwitcherFragment extends Fragment implements View.OnClickListener {

    private boolean isPowerOn = false;
    private Button switcherButton;
    private Button brightButton;
    private Button luxButton;
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
        return inflater.inflate(R.layout.fragment_switcher, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewByIds();
    }

    private void findViewByIds() {
        switcherButton = getActivity().findViewById(R.id.switcher_power);
        switcherButton.setOnClickListener(this);
        brightButton = getActivity().findViewById(R.id.switcher_bright);
        brightButton.setOnClickListener(this);
        luxButton = getActivity().findViewById(R.id.switcher_lux);
        luxButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Message message = new Message();
        switch (v.getId()) {
            case R.id.switcher_power:
                changeButtonState();
                if (isPowerOn) {
                    message.obj = "POWER OFF/";
                }
                else {
                    message.obj = "POWER ON/";
                }
                // 翻转开关状态
                isPowerOn = !isPowerOn;
                break;
            case R.id.switcher_bright:
                message.obj = "CHANGE BRIGHTNESS";
                break;
            case R.id.switcher_lux:
                message.obj = "CHANGE LUX";
                break;
        }
        handler.sendMessage(message);
    }

    private void changeButtonState() {
        if (!isPowerOn) {
            switcherButton.setText(R.string.swicther_poweroff);
            brightButton.setEnabled(true);
            brightButton.setTextColor(getResources().getColor(R.color.colorBlack));
            luxButton.setEnabled(true);
            luxButton.setTextColor(getResources().getColor(R.color.colorBlack));
        }
        else {
            switcherButton.setText(R.string.swicther_poweron);
            brightButton.setEnabled(false);
            brightButton.setTextColor(getResources().getColor(R.color.colorGray));
            luxButton.setEnabled(false);
            luxButton.setTextColor(getResources().getColor(R.color.colorGray));
        }
    }

}
