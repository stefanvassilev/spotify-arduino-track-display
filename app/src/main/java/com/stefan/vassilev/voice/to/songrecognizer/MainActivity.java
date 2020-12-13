package com.stefan.vassilev.voice.to.songrecognizer;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.polidea.rxandroidble2.RxBleDevice;
import com.stefan.vassilev.voice.to.songrecognizer.bluetooth.ArduinoBluetoothLeService;
import com.stefan.vassilev.voice.to.songrecognizer.bluetooth.ArduinoGattService;
import com.stefan.vassilev.voice.to.songrecognizer.service.ArduinoService;
import com.stefan.vassilev.voice.to.songrecognizer.service.SpotifyBroadCastReceiver;

import static com.stefan.vassilev.voice.to.songrecognizer.bluetooth.ArduinoBluetoothLeService.REQUEST_ENABLE_BT;

public class MainActivity extends AppCompatActivity {

    private ArduinoGattService arduinoGattService;

    private ArduinoService arduinoService;
    private SpotifyBroadCastReceiver spotifyBroadCastReceiver;
    private ArduinoBluetoothLeService bluetoothLeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothLeService = new ArduinoBluetoothLeService(this, this::initalizeArduinoServices);
        IntentFilter filter = new IntentFilter("com.spotify.music.metadatachanged");
        this.registerReceiver(spotifyBroadCastReceiver, filter);

        setContentView(R.layout.activity_main);
        Button updateRgb = findViewById(R.id.button_send_text);

        updateRgb.setOnClickListener((e) -> {
            SeekBar redSeekBar = findViewById(R.id.RedValueSeekBar);
            SeekBar greenSeekBar = findViewById(R.id.GreenValueSeekBar);
            SeekBar blueSeekBar = findViewById(R.id.BlueValueSeekBar);
            arduinoService.updateLcdBacklight(redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress());

            Toast.makeText(this, "Updating r,g,b with: " + "(" + redSeekBar.getProgress() + ", " + greenSeekBar.getProgress() + "," + blueSeekBar.getProgress() + ")",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void initalizeArduinoServices(RxBleDevice bluetoothDevice) {
        this.arduinoGattService = new ArduinoGattService(this, bluetoothDevice);
        this.arduinoService = new ArduinoService(this, arduinoGattService);
        this.spotifyBroadCastReceiver = new SpotifyBroadCastReceiver(arduinoService);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode != RESULT_OK) {
            throw new IllegalStateException("Failed to enable bluetooth app");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}