package com.stefan.vassilev.voice.to.songrecognizer.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.utils.ConnectionSharingAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class ArduinoGattService {

    private static final String TAG = ArduinoGattService.class.getName();

    private static final String ARDUINO_SERVICE = "5bcc5572-2783-11eb-adc1-0242ac120002";
    private static final String ARDUINO_SERVICE_AUDIO_SUBSCRIBE = "60e45b02-397d-11eb-adc1-0242ac120002";
    private static final long RECORD_TIME = 10000;
    private final MediaPlayer mp = new MediaPlayer();


    int[] sampledAudio = new int[2000];
    boolean fullArray;
    int counter = 0;

    File wavFile = new File(Environment.getExternalStorageDirectory() + "/" + File.separator + "test.wav");
    //TODO: Continue form here https://stackoverflow.com/questions/17192256/recording-wav-with-android-audiorecorder

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


        receiveAudio();

//        while (true) {
//            toggleLEDIndefinetely(1);
//            toggleLEDIndefinetely(0);
//        }

//        Disposable d = rxBleConnectionObservable
//                .flatMap(rxBleConnection -> rxBleConnection.setupNotification(UUID.fromString(ARDUINO_SERVICE_AUDIO_SUBSCRIBE)))
//                .doOnNext(notificationObservable -> {
//                })
//                .flatMap(notificationObservable -> notificationObservable)
//                .subscribe(bytes -> {
//                    Log.e(TAG, "Info" + Arrays.toString(bytes));
//                });
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

    private void playAudio() {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(activity.getApplicationContext().openFileOutput("config.wav", Context.MODE_PRIVATE));
            for (int value : sampledAudio) {
                outputStreamWriter.write(value);
            }
            outputStreamWriter.close();
            FileInputStream fileInputStream = activity.getApplicationContext().openFileInput("config.wav");
//            FileInputStream rawmp3file= new FileInputStream("config.wav");

            mp.setDataSource(fileInputStream.getFD());
            mp.prepare();
            mp.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private InputStream receiveAudio() {
        //TODO: Read on: https://github.com/Polidea/RxAndroidBle#change-notifications
        Disposable subscribe = rxBleConnectionObservable
                .flatMap(rxBleConnection -> rxBleConnection.setupNotification(UUID.fromString(ARDUINO_SERVICE_AUDIO_SUBSCRIBE)))
                .doOnNext(notificationObservable -> {

                    notificationObservable.subscribe(bytes -> {
                        Log.i(TAG, "Received: " + Arrays.toString(bytes));
                    }, throwable -> Log.e(TAG, "Failed", throwable)).dispose();
                })
                .flatMap(notificationObservable -> notificationObservable) // <-- Notification has been set up, now observe value changes.
                .subscribe(
                        bytes -> {
                            if (counter % 100 == 0) {
                                Log.w(TAG, "Receiving data");
                            }

//                            Log.w(TAG, "wrote 20k successfully");
                            if (counter == sampledAudio.length) {
                                Log.w(TAG, "wrote 20k successfully");
                                playAudio();
                                counter = 0;
                            }
                            sampledAudio[counter++] = bytes[0];

                            // Characteristic value confirmed.

                            // Given characteristic has been changes, here is the value.
                        },
                        throwable -> {
                            Log.e(TAG, "failed while receiving bytes: ", throwable);
                            // Handle an error here.
                        }
                );

        return null;
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