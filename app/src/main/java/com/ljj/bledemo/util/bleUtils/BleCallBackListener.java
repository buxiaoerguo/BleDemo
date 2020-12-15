package com.ljj.bledemo.util.bleUtils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;

public interface BleCallBackListener {
    void onScan(BluetoothDevice bluetoothDevice, ScanRecord scanRecord,int rssi);
    void onConnectSuccess();
    void onCharacteristicChanged(byte[] data);
    void onConnectFail();
}
