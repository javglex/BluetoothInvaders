package com.newpath.jeg.bluetoothinvaders.BluetoothPackage;

public class BTConstants {

    public enum ClientTask {
        SCAN_COMPLETE,
        CONNECTED,
        CLIENT_RECEIVED_MESSAGE
    }

    public enum ServerTask {
        ADVERTISING,
        CONNECTED,
        SERVER_RECEIVED_MESSAGE
    }

}
