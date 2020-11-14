package com.stefan.vassilev.voice.to.songrecognizer.service;

public class SongDetectorException extends RuntimeException {
    public SongDetectorException(String message) {
        super(message);
    }

    public SongDetectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
