package com.stefan.vassilev.voice.to.songrecognizer.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class ArduinoService {

    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;

    private BluetoothSocket socket;
    private Thread handleReceiveDataThread;
    private final Handler handler;

    public ArduinoService(Handler handler) {
        this.handler = handler;
    }


    private static final ArduinoService instance = new ArduinoService();


    public void init(BluetoothSocket bluetoothSocket) {
        if (bluetoothSocket == null) {
            throw new IllegalArgumentException("socket cannot be null");
        }
        this.socket = bluetoothSocket;
        this.handleReceiveDataThread = new Thread(this::receiveData);

    }

    public static ArduinoService getInstance() {
        return instance;
    }

    public void receiveData() {
        try {
            byte[] buff = new byte[4096];
            try (InputStream input = socket.getInputStream()) {

                int numBytes = input.read(buff);
                // Send the obtained bytes to the UI activity.
                Message readMsg = handler.obtainMessage(
                        MESSAGE_READ, numBytes, -1,
                        buff);
                readMsg.sendToTarget();

            }
        } catch (IOException e) {
            Log.e("arduino socket", "failed to receive data", e);
        }
        ;


        if (socket == null) {
            throw new IllegalStateException("Bluetooth socket not initialized");
        }

    }

    public void sendData() {
        if (socket == null) {
            throw new IllegalStateException("Bluetooth socket not initialized");
        }

    }

}
