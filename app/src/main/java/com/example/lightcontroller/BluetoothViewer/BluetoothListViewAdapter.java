package com.example.lightcontroller.BluetoothViewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lightcontroller.R;

import java.util.List;

public class BluetoothListViewAdapter extends ArrayAdapter<BluetoothDeviceItem> {

    private int resourceId;
    private Context context;

    public BluetoothListViewAdapter(Context context, int resource, List<BluetoothDeviceItem> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BluetoothDeviceItem bluetoothDeviceItem = getItem(position);
        View view = LayoutInflater.from(context).inflate(resourceId, parent, false);
        TextView nameText = view.findViewById(R.id.bluetoothListView_name);
        TextView addressText = view.findViewById(R.id.bluetoothListView_address);
        nameText.setText(bluetoothDeviceItem.getBluetoothDeviceName());
        addressText.setText(bluetoothDeviceItem.getBluetoothDeviceAddress());
        return view;
    }
}
