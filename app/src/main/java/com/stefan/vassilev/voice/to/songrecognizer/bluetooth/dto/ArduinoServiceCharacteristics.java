package com.stefan.vassilev.voice.to.songrecognizer.bluetooth.dto;

import java.util.UUID;

import static java.util.UUID.fromString;

public enum ArduinoServiceCharacteristics {

    TRACK_NAME(fromString("8cf44e98-3c69-11eb-adc1-0242ac120002")),
    SONG_ARTIST(fromString("cbcad3ee-3c69-11eb-adc1-0242ac120002"));


    private final UUID uuid;

    ArduinoServiceCharacteristics(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
