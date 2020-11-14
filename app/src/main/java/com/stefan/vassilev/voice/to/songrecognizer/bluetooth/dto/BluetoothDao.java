package com.stefan.vassilev.voice.to.songrecognizer.bluetooth.dto;

import com.stefan.vassilev.voice.to.songrecognizer.bluetooth.dto.Action;

public class BluetoothDao {

    private Action action;

    public Action getAction() {
        return action;
    }

    public BluetoothDao(Action action) {
        this.action = action;
    }
}
