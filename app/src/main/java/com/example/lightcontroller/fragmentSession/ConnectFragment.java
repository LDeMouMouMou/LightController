package com.example.lightcontroller.fragmentSession;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lightcontroller.BluetoothViewer.BluetoothDeviceItem;
import com.example.lightcontroller.BluetoothViewer.BluetoothListViewAdapter;
import com.example.lightcontroller.BluetoothViewer.BluetoothServer;
import com.example.lightcontroller.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConnectFragment extends Fragment {

    private ListView deviceListView;
    private BluetoothServer bluetoothServer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewByIds();
        getActivity().getApplicationContext()
                .bindService(new Intent(getActivity().getApplicationContext(), BluetoothServer.class),
                        bluetoothServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void findViewByIds() {
        deviceListView = getActivity().findViewById(R.id.setting_listview);
    }

    private ServiceConnection bluetoothServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothServer.BluetoothBinder bluetoothBinder = (BluetoothServer.BluetoothBinder) service;
            bluetoothServer = bluetoothBinder.getService();
            refreshBondedDeviceListView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void refreshBondedDeviceListView() {
        // 从service过来的是map组成的list，但是这里要结构体，要做个转换
        List<BluetoothDeviceItem> bluetoothDeviceItemList = new ArrayList<>();
        List<Map<String, String>> receivedDeviceList = bluetoothServer.getBondedBluetoothDevice();
        for (int i = 0; i < receivedDeviceList.size(); i++) {
            bluetoothDeviceItemList.add(new BluetoothDeviceItem(receivedDeviceList.get(i).get("name"),
                    receivedDeviceList.get(i).get("address")));
        }
        final BluetoothListViewAdapter bluetoothListViewAdapter = new BluetoothListViewAdapter(
                getActivity().getApplicationContext(), R.layout.fragment_setting_listviewitem,
                bluetoothDeviceItemList
        );
        deviceListView.setAdapter(bluetoothListViewAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getApplicationContext().unbindService(bluetoothServiceConnection);
    }
}
