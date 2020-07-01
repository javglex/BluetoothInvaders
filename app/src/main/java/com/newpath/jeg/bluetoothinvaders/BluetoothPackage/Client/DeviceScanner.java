package com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Server.BluetoothServer.SERVICE_UUID;

public class DeviceScanner {

    private final String TAG = "DeviceScanner";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private HashMap<String,BluetoothDevice> mScanResults;
    private BtleScanCallback mScanCallback;
    private Handler mHandler;
    private boolean mScanning;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;
    private DeviceResultsCallback mCb;

    public DeviceScanner(BluetoothAdapter btAdapter){
        mBluetoothAdapter = btAdapter;
    }

    public void startScan(DeviceResultsCallback cb){
        mCb = cb;
        List<ScanFilter> filters = new ArrayList<>();

        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(SERVICE_UUID))       //filters out any other advertisements that's not ours
                .build();

        filters.add(scanFilter);
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .build();

        mScanResults = new HashMap<>();
        mScanCallback = new BtleScanCallback(mScanResults);

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        mScanning = true;

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, SCAN_PERIOD);

    }

    public void stopScan(){
        if (mScanning && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            scanComplete();
        }

        mScanCallback = null;
        mScanning = false;
        mHandler = null;
    }

    private void scanComplete() {
        if (mScanResults.isEmpty()) {
            mCb.onFail();
            return;
        }

        mCb.onComplete(mScanResults);

    }


    public interface DeviceResultsCallback{
        public void onComplete(HashMap<String,BluetoothDevice> mScanResults);       //completed discovering devices
        public void onFail();       //on fail to discover any devices

    }
}
