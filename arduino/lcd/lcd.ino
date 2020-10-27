 /*!
  * file SetColor.ino
  * brief SetColor.
  *
  * Copyright   [DFRobot](https://www.dfrobot.com), 2016
  * Copyright   GNU Lesser General Public License
  *
  * version  V1.0
  * date  2017-2-10
  */

#include <Wire.h>
#include "DFRobot_LCD.h"


int r,g,b;
int t=0;



DFRobot_LCD lcd(16,2);  //16 characters and 2 lines of show


void clearRow(int row, DFRobot_LCD& lcd) {
  lcd.setCursor(0, row);
  for (int i = 0; i < 16; i++) {
    lcd.print(" ");
  }
  lcd.setCursor(0, row);
}

void scrollText(const String s, int row, DFRobot_LCD& lcd) {
  auto s_len = s.length();

  String stringToPrint;
  if (s_len > 16) {
    for (int i = 0; i < s_len; i += 16) {
      stringToPrint = s.substring(i, min(s_len, 16 + i));
      lcd.print(stringToPrint);
      lcd.setCursor(0, row);
      delay(1000);
      clearRow(row, lcd);
    }
    
  } else {
    lcd.print(s);
  }
  

  
}


void setup() {
  Serial.begin(9600);
  // initialize
  lcd.init();

  r = 255;
  g = 255;
  b = 255;
  t=t *3;
  lcd.setRGB(r, g, b);                  //Set R,G,B Value
  lcd.setCursor(0,0); 

}

void loop() {
  scrollText("zdr kpr n6 ti,smislen tekst, kojto moje da se prosledi dosta po-dobre", 0, lcd);
  delay(300);
}
