package com.stefan.vassilev.voice.to.songrecognizer.bluetooth.dto;

import androidx.annotation.NonNull;

public enum Action {
    SEND_TO_SCREEN("send_to_screen");


    Action(String value) {
        this.value = value;
    }

    private final String value;


    @NonNull
    @Override
    public String toString() {
        return value;
    }
}
