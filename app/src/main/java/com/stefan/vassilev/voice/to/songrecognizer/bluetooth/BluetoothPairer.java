package com.stefan.vassilev.voice.to.songrecognizer.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.Optional;
import java.util.Set;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static androidx.core.app.ActivityCompat.startActivityForResult;


public final class BluetoothPairer {

    private static final String ARDUINO_BLUETOOTH_DEVICE_NAME = "arduinoRev2Wifi";
    public final static int REQUEST_ENABLE_BT = 1;

    private final Activity activity;

    private BluetoothDevice arduinoDevice;

    public BluetoothPairer(Activity activity) {
        this.activity = activity;
        BluetoothAdapter bluetoothAdapter = getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            throw new UnsupportedOperationException("Bluetooth is mandatory for app");
        }

        if (!bluetoothAdapter.isEnabled()) {
            // try to enable bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(activity, enableBtIntent, REQUEST_ENABLE_BT, Bundle.EMPTY);
        }


        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.stream().anyMatch(bluetoothDevice -> bluetoothDevice.getName().equals(ARDUINO_BLUETOOTH_DEVICE_NAME))) {
            // already paired
            return;
        }


        getDefaultAdapter().startDiscovery();
        BroadcastReceiver receiver = getBroadcastReceiver();
        try {
            activity.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        } finally {
            activity.unregisterReceiver(receiver);
            getDefaultAdapter().cancelDiscovery();
        }

    }


    public Optional<BluetoothDevice> getArduinoDevice() {
        return Optional.of(arduinoDevice);
    }

    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    arduinoDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    activity.unregisterReceiver(this);
                    getDefaultAdapter().cancelDiscovery();
                }

            }
        };
    }
}
