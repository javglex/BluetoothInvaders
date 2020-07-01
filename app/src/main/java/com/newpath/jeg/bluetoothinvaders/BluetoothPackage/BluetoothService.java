package com.newpath.jeg.bluetoothinvaders.BluetoothPackage;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Client.BluetoothClient;
import com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Client.DeviceScanner;
import com.newpath.jeg.bluetoothinvaders.BluetoothPackage.Server.BluetoothServer;
import com.newpath.jeg.bluetoothinvaders.PermissionFetcher;

import java.util.HashMap;

import static com.newpath.jeg.bluetoothinvaders.PermissionFetcher.REQUEST_ENABLE_BT;

public class BluetoothService {

    private static final String TAG = "BluetoothService";
    private static BluetoothService btInstance;
    private BluetoothAdapter mBluetoothAdapter;
    private final BluetoothManager mBluetoothManager;
    private Context mContext;
    private BluetoothClient mBluetoothClient;
    private BluetoothServer mBluetoothServer;

    private BluetoothService(Context context){
        this.mContext = context;

        // Initializes Bluetooth adapter.
        mBluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = mBluetoothManager.getAdapter();

        checkBTEnabled();
        checkPermissions();
    }

    public static BluetoothService getBtServiceInstance(Activity context){
        if (btInstance==null){
            btInstance = new BluetoothService(context);
        }
        return btInstance;
    }

    /**
     * checks if device has permisssion, if not attempts to get permission
     * @return false if not authorized. true if device has permission
     */
    public boolean checkPermissions(){
        if (!PermissionFetcher.hasLocationPermissions(mContext)) {
            PermissionFetcher.requestLocationPermission(mContext);
            return false;
        }
        return true;
    }

    /**
     * checks if device has BT enabled, if not attempts to request user to enable
     * @return false if BT disabled. true if BT enabled.
     */
    public boolean checkBTEnabled(){
        if (!isEnabled()) {
            requestBtEnable();
            return false;
        }
        else return true;
    }

    /**
     * starts scanning for devices and then attempts to connect to one
     * @param cb whether connection was succesfull or not
     */
    public void startClient(final BluetoothServiceCallback cb){

        if (!checkBTEnabled()){
            cb.onFail(new Error("Bluetooth must be enabled"));
        }

        if (!checkPermissions()) {
            cb.onFail(new Error("Location permission must be enabled"));
            return;
        }

        mBluetoothClient = new BluetoothClient(mContext);

        DeviceScanner deviceScanner = new DeviceScanner(mBluetoothAdapter);

        //called after scanning completes, triggers when connected/disconnected from device
        final BluetoothClient.BluetoothClientCallback btClientCallback = new BluetoothClient.BluetoothClientCallback() {
            @Override
            public void onConnected() {
                cb.onTaskComplete(BTConstants.ClientTask.CONNECTED);
            }

            @Override
            public void onMessageFromServer(byte[] message) {
                cb.onMessageReceived(message);
            }

            @Override
            public void onDisconnected() {
                cb.onFail(new Error("Unsuccesfull GATT connection"));
            }
        };

        //begins scanning for devices
        deviceScanner.startScan(new DeviceScanner.DeviceResultsCallback() {
            @Override
            public void onComplete(HashMap<String, BluetoothDevice> mScanResults) {
                cb.onTaskComplete(BTConstants.ClientTask.SCAN_COMPLETE);
                for (BluetoothDevice result : mScanResults.values()) {        //connect to first found device for now
                    if (result!=null)
                        mBluetoothClient.connectToDevice(result, btClientCallback);
                    break;
                }
            }

            @Override
            public void onFail() {
                Log.w(TAG, "DeviceScanner failed to find any devices");
                cb.onFail(new Error("DeviceScanner failed to find any devices"));
            }
        });

    }

    public void disconnectClient(){

        if (mBluetoothClient==null)
            return;

        mBluetoothClient.disconnectGattServer();
    }

    public void startServer(final BluetoothServiceCallback cb){
        if (!checkBTEnabled()){
            cb.onFail(new Error("Bluetooth must be enabled"));
        }

        if (!checkPermissions()) {
            cb.onFail(new Error("Location permission must be enabled"));
            return;
        }

        mBluetoothServer = new BluetoothServer(mContext, mBluetoothManager, mBluetoothAdapter);

        mBluetoothServer.startAdvertising(new BluetoothServer.BluetoothServerCallback() {
            @Override
            public void onAdvertising() {
                cb.onTaskComplete(BTConstants.ServerTask.ADVERTISING);
            }

            @Override
            public void onDeviceConnected() {
                cb.onTaskComplete(BTConstants.ServerTask.CONNECTED);
            }

            @Override
            public void onDeviceDisconnected() {

            }

            @Override
            public void onMessageFromClient(byte[] msg) {
                cb.onMessageReceived(msg);
            }

            @Override
            public void onFail(Error err) {
                cb.onFail(err);
            }
        });
    }

    public void clientSendMessage(String msg){
        if (mBluetoothClient!=null)
            mBluetoothClient.sendMessage(msg);
    }

    public void disconnectServer(){
        if (mBluetoothServer!=null){
            mBluetoothServer.stopAdvertising();
            mBluetoothServer.stopServer();
        }
    }

    /**
     * Is the device's bluetooth enabled
     * @return true if enabled, false if disabled
     */
    private boolean isEnabled(){
        // Ensures Bluetooth is available on the device and it is enabled.
        return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled());
    }

    /**
     * requests user to enable device bluetooth
     */
    private void requestBtEnable(){
        try {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public interface BluetoothServiceCallback{
        public void onTaskComplete(Enum task);
        public void onMessageReceived(byte[] message);
        public void onFail(Error err);
    }


}
