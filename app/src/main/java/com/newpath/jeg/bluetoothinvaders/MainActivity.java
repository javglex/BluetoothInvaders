package com.newpath.jeg.bluetoothinvaders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.newpath.jeg.bluetoothinvaders.BluetoothPackage.BTConstants;
import com.newpath.jeg.bluetoothinvaders.BluetoothPackage.BluetoothService;

public class MainActivity extends AppCompatActivity  {

    BluetoothService mBluetoothService;
    TextView tvReceivedText;
    EditText etSendMessage;
    private String message = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothService = BluetoothService.getBtServiceInstance(this);
        tvReceivedText = findViewById(R.id.tv_received_text);
        etSendMessage = findViewById(R.id.et_send_message);

        etSendMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                message = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    public void onServerClicked(View v){
        onCancel(v);
        mBluetoothService.startServer(new BluetoothService.BluetoothServiceCallback() {
            @Override
            public void onTaskComplete(Enum e) {
                dialogBox("Task completed: " + e.name());
                if (e== BTConstants.ServerTask.CONNECTED)
                    launchGameActivity();

            }

            @Override
            public void onMessageReceived(byte[] message) {
                setText(message);
            }

            @Override
            public void onFail(Error err) {
                dialogBox("error starting server:" + err);
            }
        });
    }

    public void onClientClicked(View v){
        onCancel(v);
        mBluetoothService.startClient(new BluetoothService.BluetoothServiceCallback() {
            @Override
            public void onTaskComplete(Enum e) {
                dialogBox("Task completed: " + e.name());
                if (e == BTConstants.ClientTask.CONNECTED)
                    launchGameActivity();
            }

            @Override
            public void onMessageReceived(byte[] message) {
                setText(message);
            }

            @Override
            public void onFail(Error err) {
                dialogBox("error starting server:" + err);
            }
        });
    }

    public void onCancel(View view){
        mBluetoothService.disconnectClient();
        mBluetoothService.disconnectServer();
    }

    public void onSendMessage(View view){
        mBluetoothService.clientSendMessage(message);
    }

    public void setText(final byte[] message){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("UI thread", "setting tv");
                    tvReceivedText.setText(new String(message, "UTF-8"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void dialogBox(String msg) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                });


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("UI thread", "displaying dialog");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }


    public void launchGameActivity(){
        Intent intent =  new Intent(this, GameActivity.class);
        startActivity(intent);
    }


}
