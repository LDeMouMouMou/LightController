package com.example.lightcontroller.BluetoothViewer;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BluetoothServer extends Service {

    public final IBinder binder = new BluetoothBinder();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String connectedDeviceAddress = null;
    private List<Map<String, String>> bondedBluetoothDevice = new ArrayList<>();

    public class BluetoothBinder extends Binder {
        public BluetoothServer getService() {
            return BluetoothServer.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return;
        }
        if (!bluetoothAdapter.isEnabled())
        {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBluetoothIntent);
        }
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent startDiscoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startDiscoverable.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startDiscoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(startDiscoverable);
        }
        startDiscovery();
        super.onCreate();
    }

    public List<Map<String, String>> getBondedBluetoothDevice() {
        bondedBluetoothDevice = new ArrayList<>();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                Map<String, String> deviceInfo = new HashMap<>();
                deviceInfo.put("name", device.getName());
                deviceInfo.put("address", device.getAddress());
                bondedBluetoothDevice.add(deviceInfo);
            }
        }
        return bondedBluetoothDevice;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    private void startDiscovery() {
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
    }

    private void stopDiscovery() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

}
