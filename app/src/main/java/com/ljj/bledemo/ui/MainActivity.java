package com.ljj.bledemo.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.ljj.bledemo.R;
import com.ljj.bledemo.bean.ScanDeviceBean;
import com.ljj.bledemo.util.bleUtils.BleCallBackListener;
import com.ljj.bledemo.util.bleUtils.BleUtil;
import com.ljj.bledemo.util.AppExecutors;
import com.ljj.bledemo.util.HexUtil;
import com.ljj.bledemo.util.LogUtil;

import java.util.ArrayList;
/**
 *
 *@author ljj
 *
 *@date 2020/12/15 11:33
 *
 * @description 蓝牙扫描
 */
public class MainActivity extends AppCompatActivity {
    ArrayList<ScanDeviceBean> bleScanList;
    public MainAdapter mainAdapter;
    public BleUtil bleUtil;
    public ListView lvDevice;
    public Button btnScan, btnDisCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initClick();
        initData();

    }

    private void initData() {
        bleUtil = BleUtil.getInstance();
        bleUtil.init(this, listener);
        if (!bleUtil.initBluetooth()) {
            openBle();
        }
        bleScanList = new ArrayList<>();
    }


    private void initClick() {
        btnClick();
        disConClick();
        listClick();
    }

    private void disConClick() {
        btnDisCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bleUtil.disConnect();
            }
        });
    }

    private void listClick() {
        lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                bleUtil.stopScan();
                btnScan.setText("StartScan");
                bleUtil.connect(bleScanList.get(position).getMac());
            }
        });
    }

    private void btnClick() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnScan.getText().equals("StartScan")) {
                    bleUtil.startScan();
                    btnScan.setText("StopScan");
                    bleScanList.clear();
                    if (mainAdapter!=null) {
                        mainAdapter.notifyDataSetChanged();
                    }
                } else if (btnScan.getText().equals("StopScan")) {
                    bleUtil.stopScan();
                    btnScan.setText("StartScan");
                }
            }
        });
    }

    private void initView() {
        lvDevice = findViewById(R.id.lvDevice);
        btnScan = findViewById(R.id.btnScan);
        btnDisCon = findViewById(R.id.btnDisCon);
    }


    public void openBle() {
        if (!bleUtil.checkBle()) {
            //打开蓝牙
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 10);
        }
    }

    BleCallBackListener listener = new BleCallBackListener() {
        @Override
        public void onScan(BluetoothDevice ble, ScanRecord scanRecord, int rssi) {
            /*
            * 扫描出来的设备通过回调返回
            * */
            String test = HexUtil.encodeHexStr(scanRecord.getBytes());
            LogUtil.e("scanRecord:  " + test);
            LogUtil.e("mac:" + ble.getAddress() + "");
            int flag = 0;
            for (int j = 0; j < bleScanList.size(); j++) {
                if (bleScanList.get(j).getMac().equals(ble.getAddress())) {
                    flag = 1;
                }
            }
            if (flag == 1) {
            } else {
                if (ble.getName() != null) {
                    ScanDeviceBean bean = new ScanDeviceBean();
                    bean.setName(ble.getName());
                    bean.setChecked(false);
                    bean.setMac(ble.getAddress());
                    bean.setRssi(rssi);
                    bean.setMode(0);
                    bean.setType(0);
                    bleScanList.add(bean);
                    mainAdapter = new MainAdapter(MainActivity.this, bleScanList);
                    lvDevice.setAdapter(mainAdapter);
                }
            }
        }

        @Override
        public void onConnectSuccess() {
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    btnDisCon.setVisibility(View.VISIBLE);
                }
            });

        }

        @Override
        public void onCharacteristicChanged(byte[] data) {
            /*
            * 设备回复的数据回回调到这里，可以针对回调的参数进行处理
            * */
        }

        @Override
        public void onConnectFail() {
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    btnDisCon.setVisibility(View.GONE);
                }
            });

        }
    };

}