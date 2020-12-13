package com.stefan.vassilev.voice.to.songrecognizer.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;

import java.util.function.Consumer;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static androidx.core.app.ActivityCompat.startActivityForResult;


public final class ArduinoBluetoothLeService {

    public static final String ARDUINO_DEVICE_MAC_ADDRESS = "80:7D:3A:86:DF:DA";

    private static final String TAG_NAME = ArduinoBluetoothLeService.class.getName();
    public final static int REQUEST_ENABLE_BT = 1;

    public ArduinoBluetoothLeService(Activity activity, Consumer<RxBleDevice> bluetoothDeviceConsumer) {
        enableBluetoothIfNeeded(activity);
        RxBleClient rxBleClient = RxBleClient.create(activity.getApplicationContext());

        RxBleDevice bleDevice = rxBleClient.getBleDevice(ARDUINO_DEVICE_MAC_ADDRESS);
        bluetoothDeviceConsumer.accept(bleDevice);
    }


    private void enableBluetoothIfNeeded(Activity activity) {
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
    }


}
