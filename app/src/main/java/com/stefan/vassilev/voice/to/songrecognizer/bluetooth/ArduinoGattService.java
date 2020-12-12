package com.stefan.vassilev.voice.to.songrecognizer.bluetooth;

import android.app.Activity;
import android.util.Log;

import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.utils.ConnectionSharingAdapter;

import java.util.Arrays;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class ArduinoGattService {

    private static final String TAG = ArduinoGattService.class.getName();

    private static final String ARDUINO_SERVICE = "5bcc5572-2783-11eb-adc1-0242ac120002";

    private final Activity activity;
    private final RxBleDevice bluetoothDevice;

    private PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();
    private final Observable<RxBleConnection> rxBleConnectionObservable;


    public ArduinoGattService(Activity activity, RxBleDevice bluetoothDevice) {
        Log.i(TAG, "Creating arduino service");
        this.activity = activity;
        this.bluetoothDevice = bluetoothDevice;

        rxBleConnectionObservable = prepareConnectionObservable();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        while (true) {
            toggleLEDIndefinetely(1);
            toggleLEDIndefinetely(0);
        }

    }

    private void toggleLEDIndefinetely(int val) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Disposable subscribe = rxBleConnectionObservable
                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(UUID.fromString(ARDUINO_SERVICE), new byte[]{(byte) val}))
                .subscribe(
                        characteristicValue -> {
                            Log.w(TAG, "success" + Arrays.toString(characteristicValue));
                            // Characteristic value confirmed.
                        },
                        throwable -> {
                            Log.e(TAG, "error", throwable);
                            // Handle an error here.
                        }
                );

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private boolean isConnected() {
        return bluetoothDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }


    private Observable<RxBleConnection> prepareConnectionObservable() {
        return bluetoothDevice
                .establishConnection(false)
                .takeUntil(disconnectTriggerSubject)
                .compose(new ConnectionSharingAdapter());
    }


    private void triggerDisconnect() {
        disconnectTriggerSubject.onNext(true);
    }
}