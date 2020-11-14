package com.stefan.vassilev.voice.to.songrecognizer.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

import static com.stefan.vassilev.voice.to.songrecognizer.MainActivity.BLUETOOTH_SERVICE_GUID;
import static com.stefan.vassilev.voice.to.songrecognizer.MainActivity.BLUETOOTH_SERVICE_NAME;

public class BluetoothServer implements Runnable {

    public static final String TAG = "bluetooth socket";
    private final BluetoothServerSocket mmServerSocket;
    private final BluetoothPairer bluetoothPairer;
    private ArduinoService arduinoService;

    public BluetoothServer(BluetoothPairer bluetoothPairer) {
        this.bluetoothPairer = bluetoothPairer;
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(BLUETOOTH_SERVICE_NAME, BLUETOOTH_SERVICE_GUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }


    @Override
    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                arduinoService = new ArduinoService(bluetoothPairer.getMainHandler(), socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close bluetooth server socket", e);
                }
                break;
            }
        }


    }
}
