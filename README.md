
# Arduino Spotify LCD


This project contains source code for an android application and Arduino sketches used to create a LCD tracked of the currently playing song on Spotify.



## Algorithm

Spotify application broadcasts events about what's currently happening within the application. One of those events is the `com.spotify.music.metadatachanged` used whenever user changes currently played song.

This event is caught by the android application and the track's name as well as the song's author get transmitted to the Arduino device via BLE.

Track name and Song's author are then displayed on the LCD screen.
