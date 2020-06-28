


#include <ArduinoJson.h>  
#include <ESP8266WiFi.h>
#include <String>
#include <ESP8266HTTPClient.h>
#include "arduino_secrets.h"       // here are your secret log codes stored for wifi access and the IO Adafruit feed key
                                   // this file must be stored in your library directory in a separate directory.
char ssid[] = "realme X";         // your network SSID (name)
char pass[] = "12345678";         // your network password (use for WPA, or use as key for WEP)

//char ssid[] = "Wifi Free";         // your network SSID (name)
//char pass[] = "bat3glen";         // your network password (use for WPA, or use as key for WEP)

String name = "đèn 1";
String url_server = "http://192.168.43.103:5000/";
int status = WL_IDLE_STATUS;
WiFiEventHandler gotIpEventHandler, disconnectedEventHandler;

// Initialize the client library
WiFiClient client;
int search_Wifi = 2;
int found_Wifi =3;
int state = 0;
int relay_pin = 16;
char statement[10] = "123";
int LED = -1, ON = 1;

void setup() {
  Serial.begin(115200);
  pinMode(relay_pin, OUTPUT);     // Relay
  pinMode(search_Wifi, OUTPUT);    // searching for wifi connection
  pinMode(found_Wifi, OUTPUT);     // wifi connected
  digitalWrite(relay_pin, HIGH);
       
  ConnectToWifi();
  // check disconnect to Wifi
  disconnectedEventHandler = WiFi.onStationModeDisconnected([](const WiFiEventStationModeDisconnected& event)
  {
    Serial.println("Station disconnected");
    status = WL_CONNECTED;
  });
}

void cmdRequest();
void updateStatus(String name, int status);


void loop() {
    if (status == WL_CONNECTED) {ConnectToWifi();} // reconnect to WiFi when disconnected
    cmdRequest();
    Serial.println("_____________________");
    delay(3000);
}

void turnOn()
{
  updateStatus(1);
  digitalWrite(relay_pin, LOW);
}

void turnOff()
{
  updateStatus(0);
  digitalWrite(relay_pin, HIGH);
}

void updateStatus(int status) 
{
  Serial.println("Sending post request...");
  String PostData = "{\"name\": \"" + name;
  if (status){
    PostData += "\",\"status\": \"1\"}"; 
  } else {
    PostData += "\",\"status\": \"0\"}";
  }
  Serial.println(PostData);
  // Make a HTTP request:
  HTTPClient http;
  String url= url_server + "update-status-device";
  http.begin(url); 
  http.addHeader("Content-Type", "application/json"); 
  int httpResponseCode = http.POST(PostData.c_str()); //Send the actual POST request

  if(httpResponseCode>0){
    String response = http.getString();  //Get the response to the request
    Serial.println(httpResponseCode);   //Print return code
    Serial.println(response);           //Print request answer
  } else {
    Serial.print("Error on sending POST: ");
    Serial.println(httpResponseCode);
    http.end();

  }   //Print HTTP return code
  http.writeToStream(&Serial);
}


void cmdRequest() 
{
  Serial.println("Sending post request...");
  String PostData = "{\"name\": \"" + name + "\"}";
  Serial.println(PostData);
  // Make a HTTP request:
  HTTPClient http;
  String url= url_server + "get-command";
  http.begin(url);
  http.addHeader("Content-Type", "application/json"); 
  int httpResponseCode = http.POST(PostData.c_str()); //Send the actual POST request

  if(httpResponseCode>0){
    String response = http.getString();  //Get the response to the request
    Serial.println(httpResponseCode);   //Print return code
    Serial.println(response);           //Print request answer
    if (response != "-1")
      if (response == "0") {
        turnOff();
      } else {
        turnOn();    
      }
  } else {
    Serial.print("Error on sending POST: ");
    Serial.println(httpResponseCode);
    http.end();

  }   //Print HTTP return code
  http.writeToStream(&Serial);
}

void ConnectToWifi()
{
  // attempt to connect to Wifi network:
  while (!status) {;
    Serial.printf("Connecting to %s ...\n", ssid);
    status = WiFi.begin(ssid, pass);

    // wait 10 seconds for connection:
    delay(10000);
  }
  Serial.println("Connected to wifi");
  printWifiStatus();
}

void printWifiStatus() {
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}
