package com.example.lightcontroller.BluetoothViewer;

public class BluetoothDeviceItem {

    private String bluetoothDeviceName;
    private String bluetoothDeviceAddress;

    public BluetoothDeviceItem(String bluetoothDeviceName, String bluetoothDeviceAddress) {
        this.bluetoothDeviceName = bluetoothDeviceName;
        this.bluetoothDeviceAddress = bluetoothDeviceAddress;
    }

    public String getBluetoothDeviceName() {
        return bluetoothDeviceName;
    }

    public void setBluetoothDeviceName(String bluetoothDeviceName) {
        this.bluetoothDeviceName = bluetoothDeviceName;
    }

    public String getBluetoothDeviceAddress() {
        return bluetoothDeviceAddress;
    }

    public void setBluetoothDeviceAddress(String bluetoothDeviceAddress) {
        this.bluetoothDeviceAddress = bluetoothDeviceAddress;
    }
}
