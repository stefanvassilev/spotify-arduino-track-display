package com.stefan.vassilev.voice.to.songrecognizer.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static androidx.core.app.ActivityCompat.startActivityForResult;


public final class BluetoothPairer {

    private static final String TAG_NAME = "BLuetoothPairer";

    private static final String ARDUINO_BLUETOOTH_DEVICE_NAME = "arduinoRev2Wifi";
    public final static int REQUEST_ENABLE_BT = 1;

    private final Activity activity;

    private BluetoothDevice arduinoDevice;
    private final Handler mainHandler;

    public BluetoothPairer(Activity activity, Handler mainHandler) {
        this.activity = activity;
        this.mainHandler = mainHandler;
        BluetoothAdapter bluetoothAdapter = getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.i(TAG_NAME, "Device does not support bluetooth");
            throw new UnsupportedOperationException("Bluetooth is mandatory for app");
        }

        if (!bluetoothAdapter.isEnabled()) {
            Log.i(TAG_NAME, "Trying to enable bluetooth on device");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(activity, enableBtIntent, REQUEST_ENABLE_BT, Bundle.EMPTY);
        }


        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        Log.i(TAG_NAME, "Currently paired devices: " + Arrays.toString(pairedDevices.toArray()));

        if (pairedDevices.stream().anyMatch(bluetoothDevice -> bluetoothDevice.getName().equals(ARDUINO_BLUETOOTH_DEVICE_NAME))) {
            Log.i(TAG_NAME, "Arduino device with name: " + ARDUINO_BLUETOOTH_DEVICE_NAME + "is already paired");
            return;
        }


        Log.i(TAG_NAME, "Starting adapter discovery");
        boolean success = getDefaultAdapter().startDiscovery();
        if (!success) {
            throw new IllegalStateException("Failed to start bluetooth discovery");
        }

        BroadcastReceiver receiver = getBroadcastReceiver();
        activity.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

    }


    public Optional<BluetoothDevice> getArduinoDevice() {
        return Optional.of(arduinoDevice);
    }

    public Handler getMainHandler() {
        return mainHandler;
    }

    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG_NAME, "Broadcast receiver: " + intent.getAction());
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG_NAME, "Found device with name: " + foundDevice.getName() + " and address: " + foundDevice.getAddress());
                    if (ARDUINO_BLUETOOTH_DEVICE_NAME.equals(foundDevice.getName())) {
                        Log.i(TAG_NAME, "Found arduino device");
                        arduinoDevice = foundDevice;
                        activity.unregisterReceiver(this);
                        getDefaultAdapter().cancelDiscovery();
                    }

                }

            }
        };
    }
}
