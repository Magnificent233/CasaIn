#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>
#include <DHT.h>   
 
#define DHTTYPE DHT11
  
#define FIREBASE_HOST "casain-2020.firebaseio.com" //please remove http and / from firebase url
#define FIREBASE_AUTH "zBSt0VZEfZgYMmhXotEoAaRr67ohO6r4Yt2UfEWY" // paste secret key here
#define WIFI_SSID "NOS-CASA"
#define WIFI_PASSWORD "redecasa"

#define DHTPIN 14
DHT dht(DHTPIN, DHTTYPE); 

//Define FirebaseESP8266 data object
FirebaseData firebaseData;
FirebaseJson json;
int ledi = 13;
int relay = 12; 
int button =0;
int a=0;

void setup()
{
  pinMode(relay,OUTPUT);
  pinMode(ledi,OUTPUT);
  pinMode(button,INPUT_PULLUP);
  //Serial.begin(9600);
  digitalWrite(relay,LOW);
  dht.begin();
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  //Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    //Serial.print(".");
    delay(300);
  }

  digitalWrite(ledi, LOW);
  //Serial.println();
  //Serial.print("Connected with IP: ");
  //Serial.println(WiFi.localIP());
  //Serial.println();

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);

}


void loop() {
  float h = dht.readHumidity();
  
  float t = dht.readTemperature();
  
  Firebase.setFloat(firebaseData, "CASA/DHT/temperatura", t);
  Firebase.setFloat(firebaseData, "CASA/DHT/humidade", h);

}
