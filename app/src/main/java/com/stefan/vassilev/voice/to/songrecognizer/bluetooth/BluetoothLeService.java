package com.stefan.vassilev.voice.to.songrecognizer.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanSettings;

import java.util.function.Consumer;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static androidx.core.app.ActivityCompat.startActivityForResult;


public final class BluetoothLeService {

    public static final String ARDUINO_DEVICE_MAC_ADDRESS = "80:7D:3A:86:DF:DA";

    private static final String TAG_NAME = "BLuetoothPairer";
    private static final long SCAN_PERIOD_IN_MILLIS = 5000;
    public final static int REQUEST_ENABLE_BT = 1;
    private boolean foundArduinoDevice = false;


    private final Handler handler = new Handler();
    private final BluetoothLeScanner bluetoothLeScanner = getDefaultAdapter().getBluetoothLeScanner();

    public BluetoothLeService(Activity activity, Consumer<RxBleDevice> bluetoothDeviceConsumer) {
        enableBluetoothIfNeeded(activity);

        Log.i(TAG_NAME, "Starting LE device scan");

        RxBleClient rxBleClient = RxBleClient.create(activity.getApplicationContext());
        ScanSettings scanSettings = new ScanSettings.Builder().build();

//        Disposable scanSubscription = rxBleClient.scanBleDevices(scanSettings).subscribe(scanResult -> {
//            Log.i(TAG_NAME, "BLE Device: " + scanResult);
//        }, throwable -> Log.e(TAG_NAME, "Failed to scan: " + throwable));

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
