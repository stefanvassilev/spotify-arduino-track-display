#include <ArduinoBLE.h>
#include <Wire.h>
#include "DFRobot_LCD.h"


#include <Thread.h>

Thread bleThread = Thread();
Thread lcdThread = Thread();

// Bluetooth LE services and characteristics
BLEService songService("5bcc5572-2783-11eb-adc1-0242ac120002");

// create switch characteristic and allow remote device to read and write
BLEByteCharacteristic switchCharacteristic("5bcc5572-2783-11eb-adc1-0242ac120002", BLERead | BLEWrite);

BLEByteCharacteristic trackNameChar("8cf44e98-3c69-11eb-adc1-0242ac120002", BLERead | BLEWrite);
BLEByteCharacteristic songAuthorChar("cbcad3ee-3c69-11eb-adc1-0242ac120002", BLERead | BLEWrite);


// LED initialization
DFRobot_LCD lcd(16,2);  //16 characters and 2 lines of show
int r,g,b;
int t=0;

String trackName = "";
String songAuthor = "";
bool showSong = false;

void setup() {
    Serial.begin(9600);
    while (!Serial);

    r = 255;
    g = 0;
    b = 255;
    t=t *3;
    lcd.init();

    lcd.setRGB(r, g, b);                  //Set R,G,B Value
    lcd.setCursor(0,0);


    // begin initialization
    if (!BLE.begin()) {
      Serial.println("starting BLE failed!");
        while (1){ }
    }

    Serial.print("Setting up BLE service");
    BLE.setLocalName("track-display");
    BLE.setAdvertisedService(songService);

    songService.addCharacteristic(switchCharacteristic);
    songService.addCharacteristic(trackNameChar);
    songService.addCharacteristic(songAuthorChar);

    BLE.addService(songService);

    // assign event handlers for connected, disconnected to peripheral
    BLE.setEventHandler(BLEConnected, blePeripheralConnectHandler);
    BLE.setEventHandler(BLEDisconnected, blePeripheralDisconnectHandler);

   String address = BLE.address();
   Serial.print("Local address is: ");
   Serial.println(address);

  // assign event handlers for characteristics
  trackNameChar.setEventHandler(BLEWritten, trackNameCharWritten);
  songAuthorChar.setEventHandler(BLEWritten, authorCharWritten);

  // set an initial value for the characteristic
  switchCharacteristic.setValue(0);

  // start advertising
  BLE.advertise();
  Serial.println(("Bluetooth device active, waiting for connections..."));

  lcdThread.onRun(printToLcd);
  lcdThread.setInterval(1);

  bleThread.onRun(pollBle);
  bleThread.setInterval(1);
}


void loop() {
    if (lcdThread.shouldRun()) {
      lcdThread.run();
    }

    if (bleThread.shouldRun()) {
      bleThread.run();
    }
}

void pollBle() {
    BLE.poll();
}


unsigned long previousMillis = 0;
int track_index_0 = 0;
int track_index_1 = 0;

int author_index_0 = 0;
int author_index_1 = 0;


void printToLcd() {
    if (!showSong) {
      return;
    }

    unsigned long currentMillis  = millis();
    if (currentMillis - previousMillis >= 3000) {
      // release
      Serial.println("Releasing thread printing track name..");
      previousMillis = currentMillis;
    } else {
      //block
      return;
    }


   if (trackName) {
    writeToScreen(track_index_0, track_index_1, 0, trackName);
  }

  if (songAuthor) {
    writeToScreen(author_index_0, author_index_1, 1, "By "+ songAuthor );
  }

}

void writeToScreen(int& index1,int& index2, int row, String s) {
  auto s_len = s.length();
  String stringToPrint;
  if (s_len > 16) {
    if (index2 == 0) {
      index2 = min(s_len, index2 + 16);
    } else {
      index1 = index2;
      index2 = min(s_len, index2 + 16);
    }
  stringToPrint = s.substring(index1, index2);
  clearRow(row, lcd);
  lcd.setCursor(0, row);
  lcd.print(stringToPrint);

} else {
  lcd.setCursor(0, row);
  lcd.print(s);

}

if (index2 == s_len) {
  index1 = 0;
  index2 = 0;
}


}


void trackNameCharWritten(BLEDevice central, BLECharacteristic characteristic) {
  if (trackNameChar.value() == byte(0)) {
    // clear cached buffer, start writing new one
    trackName = "";
    track_index_0 = 0;
    track_index_1 = 0;
    Serial.println("New Song recieved");
    showSong = false;
    clearRow(0, lcd);
    clearRow(1, lcd);
    return;
  }

  if (trackNameChar.value()  == byte(1)) {
    Serial.print("Printing to screen: ");
    Serial.println("name: " + trackName);
    return;
    }

  trackName += char(trackNameChar.value());
 }

void authorCharWritten(BLEDevice central, BLECharacteristic characteristic) {
  if (songAuthorChar.value()  == byte(0)) {
    // clear cached buffer, start writing new one
    songAuthor = "";
    return;
  }

  if (songAuthorChar.value() == byte(1)) {
      Serial.println("author: " + songAuthor);
      showSong = true;
      return;
    }

  songAuthor += char(songAuthorChar.value());
  Serial.println(char(songAuthorChar.value()));

}

void blePeripheralConnectHandler(BLEDevice central) {
  // central connected event handler
  Serial.print("Connected event, central: ");
  Serial.println(central.address());
}

void blePeripheralDisconnectHandler(BLEDevice central) {
  // central disconnected event handler
  Serial.print("Disconnected event, central: ");
  Serial.println(central.address());
}


void clearRow(int row, DFRobot_LCD& lcd) {
  lcd.setCursor(0, row);
  for (int i = 0; i < 16; i++) {
    lcd.print(" ");
  }
  lcd.setCursor(0, row);
}
