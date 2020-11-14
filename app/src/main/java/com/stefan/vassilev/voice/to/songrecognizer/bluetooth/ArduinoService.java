package com.stefan.vassilev.voice.to.songrecognizer.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ArduinoService {

    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;

    private BluetoothSocket socket;
    private Thread handleReceiveDataThread;
    private final Handler handler;

    public ArduinoService(Handler handler, BluetoothSocket bluetoothSocket) {
        this.handler = handler;
        if (bluetoothSocket == null) {
            throw new IllegalArgumentException("socket cannot be null");
        }
        this.socket = bluetoothSocket;
        this.handleReceiveDataThread = new Thread(this::receiveData);
    }


    public void receiveData() {
        while (true) {
            {
                byte[] buff = new byte[4096];
                try (InputStream input = socket.getInputStream()) {

                    int numBytes = input.read(buff);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MESSAGE_READ, numBytes, -1,
                            buff);
                    readMsg.sendToTarget();

                } catch (IOException e) {
                    Log.e("arduino socket", "failed to receive data", e);
                    break;
                }
            }
        }
    }

    public void sendData(byte[] data) {
        try (OutputStream os = this.socket.getOutputStream()) {
            os.write(data);
        } catch (IOException e) {
            Log.e("arduino socket", "Failed to send data", e);
        }
    }

}
