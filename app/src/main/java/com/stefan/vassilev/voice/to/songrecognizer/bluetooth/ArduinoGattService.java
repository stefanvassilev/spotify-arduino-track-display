package com.stefan.vassilev.voice.to.songrecognizer.bluetooth;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

    private final RxBleDevice bluetoothDevice;

    private final PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();
    private final Observable<RxBleConnection> rxBleConnectionObservable;
    private final Context context;


    public ArduinoGattService(Context context, RxBleDevice bluetoothDevice) {
        Log.i(TAG, "Creating arduino service");
        this.bluetoothDevice = bluetoothDevice;

        rxBleConnectionObservable = prepareConnectionObservable();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.context = context;
    }

    public void writeCharacteristic(UUID charId, byte[] data) {
        Disposable subscribe = rxBleConnectionObservable
                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(charId, data))
                .subscribe(
                        characteristicValue -> {
                            Log.i(TAG, "success" + Arrays.toString(characteristicValue));
                        },
                        throwable -> {
                            Log.e(TAG, "error", throwable);
                            Toast.makeText(context, "error" + throwable, Toast.LENGTH_LONG).show();
                        }
                );
    }


    private Observable<RxBleConnection> prepareConnectionObservable() {
        return bluetoothDevice
                .establishConnection(false)
                .takeUntil(disconnectTriggerSubject)
                .compose(new ConnectionSharingAdapter());
    }

}