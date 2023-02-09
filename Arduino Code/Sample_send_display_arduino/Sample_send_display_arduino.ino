#include <LiquidCrystal.h>

LiquidCrystal lcd (10, 9, 5, 4, 3 ,2);

int sensorpin = A0;     // analoge volatge reading input pin
int readVolt;           // the read value on the analog pin
double voltval;
byte buf[2];            // byte array used to transmit data


void setup() {
  // Start serial communication with ESP8266 with 115200 baud rate
  Serial.begin(115200);
  lcd.begin(16, 2);
  lcd.setCursor(0,0);
  lcd.print("Wifi status: On");

  lcd.setCursor(0, 1);
  lcd.print("Volt:       ");
}

void loop() {  

  
  // sample and store analog voltage value to json array
  for(int i=0; i<15; i++){

     
    // read volatge at analoge pin and assign to variable
    readVolt = analogRead(sensorpin);
    voltval = (readVolt*5.0)/1024.0;

    lcd.setCursor(6, 1);
    lcd.print(voltval);
    // divide read value into two numbers inorder to transmit to ESP (required since limit to 255 max)
    buf[0] = readVolt % 100;
    buf[1] = (int)(readVolt / 100);

    // send read value at analoge pin as combination of two numbers
    Serial.write(buf[0]);
    delay(10);
    Serial.write(buf[1]);

    delay(100);
  }
  
}
