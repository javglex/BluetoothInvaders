package com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Client;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Client.BluetoothClient;

import java.io.UnsupportedEncodingException;

import static com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Server.BluetoothServer.CHARACTERISTIC_UUID;
import static com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Server.BluetoothServer.SERVICE_UUID;

class GattClientCallback extends BluetoothGattCallback {

    private final String TAG = "GattClientCallback";
    private BluetoothClient mBTClient;
    private BluetoothGattCharacteristic mCharacteristic;

    public GattClientCallback(BluetoothClient bt){
        mBTClient = bt;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (status == BluetoothGatt.GATT_FAILURE) {
            Log.e(TAG, "GATT_FAILURE");
            disconnected();
            disconnectGattServer();
            return;
        } else if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.e(TAG, "!GATT_SUCCESS");
            disconnected();
            disconnectGattServer();
            return;
        }
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "STATE_CONNECTED");
            gatt.discoverServices();
            connected();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i(TAG, "STATE_DISCONNECTED");
            disconnectGattServer();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status != BluetoothGatt.GATT_SUCCESS) {
            return;
        }

        BluetoothGattService service = gatt.getService(SERVICE_UUID);
        mCharacteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
        mCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        mBTClient.initCharacteristic(mCharacteristic);

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG,"characteristic write: "+ characteristic.getStringValue(0) );
    }


    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);

        byte[] messageBytes = characteristic.getValue();
        String messageString = null;
        try {
            messageString = new String(messageBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Unable to convert message bytes to string");
        }
        Log.d(TAG,"Received message: " + messageString);

        mBTClient.onMessageFromServer(messageBytes);
    }



    private void disconnectGattServer(){
        if (mBTClient!=null) {
            mBTClient.disconnectCharacteristic(mCharacteristic);
            mBTClient.disconnectGattServer();
        }
    }

    private void connected(){
        if (mBTClient!=null)
            mBTClient.setConnected(true);
    }

    private void disconnected(){
        if (mBTClient!=null)
            mBTClient.setConnected(false);
    }

}
