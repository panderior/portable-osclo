#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ArduinoJson.h>

int transVolt;                          // variable to store the reconstructed value of the data recieved
byte buf[2];                            // byte array used to recieve data
const char *ssid = "MyESP8266AP";       // wifi name
const char *password = "12345678";      // wifi password
String jsonString;                      // a string to store the content of json array

ESP8266WebServer server(80);
StaticJsonBuffer<500> jsonBuffer;       


void handleRoot() {
  server.send(200, "text/json", jsonString);
}

void setup() {
  // Start serial communication with arduino at 115200 baud rate
  Serial.begin(115200);

  // Start wifi with specified name and password
  WiFi.softAP(ssid, password);
  server.on("/", handleRoot);
  server.begin();
}

void loop() {
  
  jsonString = "";
  // clear json buffer
  jsonBuffer.clear();                  

   
  // create json array to store recieved values
  JsonArray &values = jsonBuffer.createArray();
     
    
  // accept voltage reading from Arduino
  for(int j=0; j<15; j++){
    // wait for the first data segmnet
    while(!Serial.available()) {   } 
    buf[0] = Serial.read();
    delay(10);
    buf[1] = Serial.read();
    transVolt = (buf[1]*100) + buf[0];

    // store value on json array
    values.add(transVolt);
    delay(100);
  }

  // convert the json array to string
  values.prettyPrintTo(jsonString);
  
  // handle client request
  server.handleClient();
  
}
