package com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Client;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

class BtleScanCallback extends ScanCallback {

    private final String TAG = "BtleScanCallback";
    private HashMap<String,BluetoothDevice> mScanResults;

    public BtleScanCallback(HashMap<String,BluetoothDevice> scanResults){
        mScanResults = scanResults;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        addScanResult(result);
    }
    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        for (ScanResult result : results) {
            addScanResult(result);
        }
    }
    @Override
    public void onScanFailed(int errorCode) {
        Log.e(TAG, "BLE Scan Failed with code " + errorCode);
    }

    private void addScanResult(ScanResult result) {
        BluetoothDevice device = result.getDevice();
        String deviceAddress = device.getAddress();
        mScanResults.put(deviceAddress, device);
    }
};


