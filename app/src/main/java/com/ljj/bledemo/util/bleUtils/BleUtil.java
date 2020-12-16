package com.ljj.bledemo.util.bleUtils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;


import com.ljj.bledemo.util.BaseUtil;
import com.ljj.bledemo.util.LogUtil;

import java.util.ArrayList;
import java.util.UUID;

import androidx.core.app.ActivityCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 *
 *@author ljj
 *
 *@date 2020/12/14 16:54
 *
 * @description 基础蓝牙工具类
 */
public class BleUtil {
    public Context mContext;
    public Activity activity;
    private BluetoothAdapter mBleAdapter;
    private static BleUtil instance;
    public BleCallBackListener listener;
    public BluetoothManager bluetoothManager;

    public void init(Activity context, BleCallBackListener listener) {
        if (this.mContext == null && context != null) {
            this.mContext = context.getApplicationContext();
            this.activity = context;
            initCallBack(listener);
            bluetoothManager = (BluetoothManager) this.mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBleAdapter = bluetoothManager.getAdapter();
        }
    }
    public void initCallBack(BleCallBackListener listener) {
        this.listener = listener;
    }
    public static BleUtil getInstance() {
        if (instance == null) {
            synchronized (BleUtil.class) {
                if (instance == null) {
                    instance = new BleUtil();
                }
            }
        }
        return instance;
    }
    @SuppressLint("MissingPermission")
    public boolean initBluetooth() {
        boolean onOff = true;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            onOff = false;
        } else if (!requestPermissions()) {
            onOff = true;
        }
        return onOff;
    }
    @SuppressLint("ObsoleteSdkInt")
    public boolean requestPermissions() {
        ArrayList<String> permissionList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (mContext.checkSelfPermission(Manifest.permission.BLUETOOTH) != PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.BLUETOOTH);
            }
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.CAMERA);
            }
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (mContext.checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.GET_ACCOUNTS);
            }
        }

        if (permissionList.size() > 0) {
            LogUtil.e("requestPermissions: permissionList size = " + permissionList.size());
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            LogUtil.e("requestPermissions: permissions length = " + permissions.length);
            for (int i = 0; i < permissions.length; i++) {
                LogUtil.e("requestPermissions: permissions[" + i + "] = " + permissions[i]);
            }

            ActivityCompat.requestPermissions(activity, permissions, 11);
            return true;
        }
        return false;
    }

    public void startScan() {
        mBleAdapter.getBluetoothLeScanner().startScan(callback);
    }

    public void stopScan() {
        mBleAdapter.getBluetoothLeScanner().stopScan(callback);
    }

    private BluetoothGatt mBluetoothGatt;

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void disConnect() {
        if (mBleAdapter == null || mBluetoothGatt == null) {
            LogUtil.e("BluetoothAdapter not initialized.");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * 连接设备
     *
     * @param address 设备mac地址
     */
    public void connect(String address) {

        if (mBleAdapter == null || address == null) {
            LogUtil.e("No device found at this address：" + address);
            return;
        }
        try {
            BluetoothDevice remoteDevice = mBleAdapter.getRemoteDevice(address);
            if (remoteDevice == null) {
                LogUtil.e("Device not found.  Unable to connect.");
                return;
            }
            mBluetoothGatt = remoteDevice.connectGatt(mContext, false, mGattCallBack);
            LogUtil.e("connecting mac-address:" + address);
        } catch (Exception e) {
            LogUtil.e("蓝牙地址错误，请重新绑定");
        }
    }

    BluetoothGattCallback mGattCallBack = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            LogUtil.e("onConnectionStateChange");
            if (newState == 2) {
                LogUtil.e(" Gatt success: " + gatt.getDevice().getAddress());
                    gatt.discoverServices();
                    listener.onConnectSuccess();
            } else {
                LogUtil.e("Gatt fail: " + gatt.getDevice().getAddress());
                listener.onConnectFail();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            LogUtil.e("onServicesDiscovered");
            gatt.requestMtu(158);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            LogUtil.e("onCharacteristicRead");

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            LogUtil.e("onCharacteristicWrite");
            String address = gatt.getDevice().getAddress();
            for (int i = 0; i < characteristic.getValue().length; i++) {
                LogUtil.i("address: " + address + ",Write: " + characteristic.getValue()[i]);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            LogUtil.e("onCharacteristicChanged");
            // 接收回复消息
            byte[] data = characteristic.getValue();
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            LogUtil.e("onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            LogUtil.e("onDescriptorWrite");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            LogUtil.e("onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            LogUtil.e("onMtuChanged" + mtu);
            writeDescriptor(gatt);
            //发送消息
            sendMsg();
        }
    };

    private void sendMsg() {
        byte[] msg = new byte[1];
        msg[0] = 0;
        writeCharacteristic(BaseUtil.serviceUUID, BaseUtil.characteristicUUID, msg);
    }

    private void writeDescriptor(BluetoothGatt gatt) {
        BluetoothGattService linkLossService = gatt.getService(UUID.fromString(BaseUtil.serviceUUID));
        BluetoothGattCharacteristic data = linkLossService.getCharacteristic(UUID.fromString(BaseUtil.characteristicUUIDs));
        mBluetoothGatt.setCharacteristicNotification(data, true);
        BluetoothGattDescriptor descriptor = data.getDescriptor(
                UUID.fromString("00002902-0000-1000-8000-00805F9B34FB"));
        descriptor.setValue(true ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //对结果处理
            BluetoothDevice bluetoothDevice = result.getDevice();
            int rssi = result.getRssi();
            ScanRecord scanRecord = result.getScanRecord();
            listener.onScan(bluetoothDevice, scanRecord, rssi);
        }
    };

    public boolean writeCharacteristic(String serviceUUID, String characteristicUUID, byte[] value) {
        if (mBluetoothGatt != null) {
            BluetoothGattService service =
                    mBluetoothGatt.getService(UUID.fromString(serviceUUID));
            BluetoothGattCharacteristic characteristic =
                    service.getCharacteristic(UUID.fromString(characteristicUUID));
            characteristic.setValue(value);
            return mBluetoothGatt.writeCharacteristic(characteristic);
        }
        return false;
    }
    @SuppressLint("MissingPermission")
    public boolean checkBle() {
        if (!mBleAdapter.isEnabled()) {
            return false;
        } else {
            return true;
        }
    }

    public void readCharacteristic(String serviceUUID, String characteristicUUID) {
        if (mBluetoothGatt != null) {
            BluetoothGattService service =
                    mBluetoothGatt.getService(UUID.fromString(serviceUUID));
            BluetoothGattCharacteristic characteristic =
                    service.getCharacteristic(UUID.fromString(characteristicUUID));
            mBluetoothGatt.readCharacteristic(characteristic);
        }
    }
}
