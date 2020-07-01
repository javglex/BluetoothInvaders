package com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class BluetoothServer {

    private final String TAG = "BluetoothServer";
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private Context mContext;
    private BluetoothGattServer mGattServer;
    public static final UUID SERVICE_UUID = UUID.fromString("0000FFF0-0000-1000-8000-00805F9B34FB");
    public static final UUID CHARACTERISTIC_UUID = UUID.fromString("0000FFF0-0000-1000-8000-00805F9B45AC");
    private AdvertiseCallback mAdvertiseCallback;
    private ArrayList<BluetoothDevice> mDevices;
    private BluetoothServerCallback mCb;

    public BluetoothServer(Context context, BluetoothManager bluetoothManager, BluetoothAdapter mBTAdapter){
        mContext = context;
        mBluetoothLeAdvertiser = mBTAdapter.getBluetoothLeAdvertiser();
        GattServerCallback gattServerCallback = new GattServerCallback(this);
        mGattServer = bluetoothManager.openGattServer(context, gattServerCallback);
        mDevices = new ArrayList<>();
        setupServer();

    }


    public void startAdvertising(final BluetoothServerCallback cb) {

        mCb = cb;

        if (mBluetoothLeAdvertiser == null) {
            cb.onFail(new Error("unable to fetch bluetooth advertiser"));
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)       //allows for two way communication
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .build();

        ParcelUuid parcelUuid = new ParcelUuid(SERVICE_UUID);
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(parcelUuid)
                .build();


        mAdvertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(TAG, "Peripheral advertising started.");
                cb.onAdvertising();
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "Peripheral advertising failed: " + errorCode);
                cb.onFail(new Error("Peripheral advbertising failed with code: "+errorCode));
            }
        };

        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);

    }

    public void sendResponse(BluetoothGattCharacteristic characteristic, BluetoothDevice device, int request, int status, int offset, byte[] value){
        mGattServer.sendResponse(device,request,status,offset,null);

        mCb.onMessageFromClient(value);

        int length = value.length;
        byte[] reversed = new byte[length];

        for (int i = 0; i < length; i++) {
            reversed[i] = value[length - (i + 1)];
        }

        characteristic.setValue(reversed);
        mGattServer.notifyCharacteristicChanged(device, characteristic, false);

    }



    public void stopServer() {
        if (mGattServer != null) {
            mGattServer.close();
        }
    }

    public void stopAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
        }
    }

    private void setupServer() {
        BluetoothGattService service = new BluetoothGattService(SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        //set up characteristics
        BluetoothGattCharacteristic writeCharacteristic = new BluetoothGattCharacteristic(
                CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(writeCharacteristic);

        mGattServer.addService(service);
    }

    public void addDeviceToList(BluetoothDevice device){
        if (device != null) {
            mDevices.add(device);
            mCb.onDeviceConnected();
        }
    }

    public void removeDeviceFromList(BluetoothDevice device){
        if (device != null) {
            mDevices.remove(device);
            mCb.onDeviceDisconnected();
        }
    }

    public interface BluetoothServerCallback{
        public void onAdvertising();
        public void onDeviceConnected();
        public void onDeviceDisconnected();
        public void onMessageFromClient(byte[] msg);
        public void onFail(Error err);
    }

}
