package com.stefan.vassilev.voice.to.songrecognizer;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.polidea.rxandroidble2.RxBleDevice;
import com.stefan.vassilev.voice.to.songrecognizer.bluetooth.ArduinoBluetoothLeService;
import com.stefan.vassilev.voice.to.songrecognizer.bluetooth.ArduinoGattService;
import com.stefan.vassilev.voice.to.songrecognizer.service.ArduinoService;
import com.stefan.vassilev.voice.to.songrecognizer.service.SpotifyBroadCastReceiver;

import java.util.UUID;

import static com.stefan.vassilev.voice.to.songrecognizer.bluetooth.ArduinoBluetoothLeService.REQUEST_ENABLE_BT;

public class MainActivity extends AppCompatActivity {

    public static final UUID BLUETOOTH_SERVICE_GUID = UUID.fromString("5bcc5572-2783-11eb-adc1-0242ac120002");

    private ArduinoGattService arduinoGattService;

    private EditText editTextMultiline;
    private Button sendTextButton;
    private Button clearTextButton;
    private ArduinoBluetoothLeService bluetoothLeService;
    private ArduinoService arduinoService;
    private SpotifyBroadCastReceiver spotifyBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothLeService = new ArduinoBluetoothLeService(this, this::initalizeArduinoServices);

        IntentFilter filter = new IntentFilter("com.spotify.music.metadatachanged");
        this.registerReceiver(spotifyBroadCastReceiver, filter);

        setContentView(R.layout.activity_main);
        editTextMultiline = findViewById(R.id.editTextTextMultiLine);
        sendTextButton = findViewById(R.id.button_send_text);
        clearTextButton = findViewById(R.id.button_clear_text);
        clearTextButton.setOnClickListener(v -> {
            editTextMultiline.setText("");
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