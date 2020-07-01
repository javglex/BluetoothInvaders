package com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Server;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import static com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Server.BluetoothServer.CHARACTERISTIC_UUID;

public class GattServerCallback extends BluetoothGattServerCallback {

    private final String TAG = "GattServerCallback";
    private BluetoothServer mBTServer;

    public GattServerCallback(BluetoothServer btServer){
        mBTServer = btServer;
    }

    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        super.onConnectionStateChange(device, status, newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            addDeviceToList(device);
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            removeDeviceFromList(device);
        }
    }

    @Override
    public void onCharacteristicWriteRequest(BluetoothDevice device,
                                             int requestId,
                                             BluetoothGattCharacteristic characteristic,
                                             boolean preparedWrite,
                                             boolean responseNeeded,
                                             int offset,
                                             byte[] value) {
        super.onCharacteristicWriteRequest(device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value);

        if (characteristic.getUuid().equals(CHARACTERISTIC_UUID)) {
            mBTServer.sendResponse(characteristic,device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);
        }
    }

    private void addDeviceToList(BluetoothDevice device){
        if (mBTServer != null) {
            Log.d(TAG,"Adding device to list: "+ device.getName());
            mBTServer.addDeviceToList(device);
        }
    }

    private void removeDeviceFromList(BluetoothDevice device){
        if (mBTServer != null){
            Log.d(TAG,"Removing device from list: "+ device.getAddress());
            mBTServer.removeDeviceFromList(device);
        }
    }

}

