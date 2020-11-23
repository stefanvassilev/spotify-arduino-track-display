package com.stefan.vassilev.voice.to.songrecognizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.stefan.vassilev.voice.to.songrecognizer.bluetooth.ArduinoGattService;
import com.stefan.vassilev.voice.to.songrecognizer.bluetooth.BluetoothLeService;

import java.util.UUID;

import static com.stefan.vassilev.voice.to.songrecognizer.bluetooth.BluetoothLeService.REQUEST_ENABLE_BT;

public class MainActivity extends AppCompatActivity {

    public static final UUID BLUETOOTH_SERVICE_GUID = UUID.fromString("5bcc5572-2783-11eb-adc1-0242ac120002");

    private ArduinoGattService arduinoGattService;

    private EditText editTextMultiline;
    private Button sendTextButton;
    private Button clearTextButton;
    private BluetoothLeService bluetoothLeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothLeService = new BluetoothLeService(this, bluetoothDevice -> {
            this.arduinoGattService = new ArduinoGattService(this, bluetoothDevice);
        });
        setContentView(R.layout.activity_main);
        editTextMultiline = findViewById(R.id.editTextTextMultiLine);
        sendTextButton = findViewById(R.id.button_send_text);
        clearTextButton = findViewById(R.id.button_clear_text);
        clearTextButton.setOnClickListener(v -> {
            editTextMultiline.setText("");
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode != RESULT_OK) {
            throw new IllegalStateException("Failed to enable bluetooth app");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}