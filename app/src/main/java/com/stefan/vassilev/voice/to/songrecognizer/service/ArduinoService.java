package com.stefan.vassilev.voice.to.songrecognizer.service;

import android.content.Context;

import com.stefan.vassilev.voice.to.songrecognizer.bluetooth.ArduinoGattService;
import com.stefan.vassilev.voice.to.songrecognizer.bluetooth.dto.ArduinoServiceCharacteristics;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ArduinoService {

    private final Context context;
    private final ArduinoGattService arduinoGattService;

    public ArduinoService(Context context, ArduinoGattService arduinoGattService) {
        this.context = context;
        this.arduinoGattService = arduinoGattService;
    }


    public void setCurrentPlayingTrack(String trackName, String songAuthor, int trackLengthInSeconds) {
        sendCharByChar(ArduinoServiceCharacteristics.TRACK_NAME.getUuid(), trackName.getBytes(StandardCharsets.UTF_8));
        sendCharByChar(ArduinoServiceCharacteristics.SONG_ARTIST.getUuid(), songAuthor.getBytes(StandardCharsets.UTF_8));
    }

    public void updateLcdBacklight(int r, int g, int b) {
        arduinoGattService.writeCharacteristic(ArduinoServiceCharacteristics.LCD_RED_VALUE.getUuid(), new byte[]{(byte) r});
        arduinoGattService.writeCharacteristic(ArduinoServiceCharacteristics.LCD_GREEN_VALUE.getUuid(), new byte[]{(byte) g});
        arduinoGattService.writeCharacteristic(ArduinoServiceCharacteristics.LCD_BLUE_VALUE.getUuid(), new byte[]{(byte) b});
    }

    private void sendCharByChar(UUID charUuid, byte[] data) {
        byte[] controlChar = new byte[]{0};
        arduinoGattService.writeCharacteristic(charUuid, controlChar);
        for (byte d : data) {
            arduinoGattService.writeCharacteristic(charUuid, new byte[]{d});
        }
        byte[] endTextChar = new byte[]{1};
        arduinoGattService.writeCharacteristic(charUuid, endTextChar);


    }


}
