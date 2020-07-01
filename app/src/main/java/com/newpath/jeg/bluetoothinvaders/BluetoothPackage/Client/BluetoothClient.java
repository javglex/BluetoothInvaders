package com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import static com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Server.BluetoothServer.CHARACTERISTIC_UUID;
import static com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Server.BluetoothServer.SERVICE_UUID;

public class BluetoothClient {

    private static final String TAG = "BluetoothClient";
    private BluetoothGatt mGatt;
    private Context mContext;
    private boolean mConnected = false;
    private BluetoothClientCallback mCb;
    private boolean mInitialized;


    public BluetoothClient(Context context){
        mContext = context;
    }

    public void connectToDevice(BluetoothDevice device, final BluetoothClientCallback cb){
        mCb = cb;
        GattClientCallback gattClientCallback = new GattClientCallback(this);
        mGatt = device.connectGatt(mContext, false, gattClientCallback);

    }

    public void sendMessage(String message) {
        if (!mConnected || !mInitialized) {
            return;
        }
        BluetoothGattService service = mGatt.getService(SERVICE_UUID);
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);

        byte[] messageBytes = new byte[0];

        try {
            messageBytes = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Failed to convert message string to byte array");
        }

        characteristic.setValue(messageBytes);
        boolean success = mGatt.writeCharacteristic(characteristic);
    }

    public void initCharacteristic(BluetoothGattCharacteristic characteristic){
        try {
            mInitialized = mGatt.setCharacteristicNotification(characteristic, true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void disconnectCharacteristic(BluetoothGattCharacteristic characteristic){
        try{
            mInitialized = mGatt.setCharacteristicNotification(characteristic, false);
        }catch(Exception e){
            e.printStackTrace();
        }

        mInitialized = false;

    }

    public void onMessageFromServer(byte[] msg){
        mCb.onMessageFromServer(msg);
    }

    public void disconnectGattServer() {
        mConnected = false;
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }

    /**
     * called by gattclientcallback to set state of connection
     * @param connected
     */
    public void setConnected(boolean connected){
        this.mConnected = connected;
        if (connected)
            mCb.onConnected();
        else mCb.onDisconnected();
    }



    public interface BluetoothClientCallback{
        public void onConnected();
        public void onMessageFromServer(byte[] message);
        public void onDisconnected();
    }
}
