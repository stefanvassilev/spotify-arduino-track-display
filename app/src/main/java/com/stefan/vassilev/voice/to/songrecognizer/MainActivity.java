package com.stefan.vassilev.voice.to.songrecognizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.stefan.vassilev.voice.to.songrecognizer.bluetooth.BluetoothPairer;

import java.util.UUID;

import static com.stefan.vassilev.voice.to.songrecognizer.bluetooth.BluetoothPairer.REQUEST_ENABLE_BT;

public class MainActivity extends AppCompatActivity {

    public static final String BLUETOOTH_SERVICE_NAME = "songrecognizer";
    public static final String BLUETOOTH_SERVICE_GUID = UUID.fromString(BLUETOOTH_SERVICE_NAME).toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        BluetoothPairer bluetoothPairer = new BluetoothPairer(this);
        setContentView(R.layout.activity_main);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode != RESULT_OK) {
            throw new IllegalStateException("Failed to enable bluetooth app");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}