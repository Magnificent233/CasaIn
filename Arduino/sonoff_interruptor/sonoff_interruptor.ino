#include <Arduino.h>
#include <ESP8266HTTPClient.h>
#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>

FirebaseData firebaseData;

// Set these to run example.
#define FIREBASE_HOST "casain-2020.firebaseio.com"              // the project name address from firebase id
#define FIREBASE_AUTH "zBSt0VZEfZgYMmhXotEoAaRr67ohO6r4Yt2UfEWY"       // the secret key generated from firebase
#define WIFI_SSID "NOS-CASA"                                          
#define WIFI_PASSWORD "redecasa"   

int but=0;
int onoff=0;
int led=0;
int relay=12;
int ledi=13;

void setup() {
  Serial.begin(115200); 
  pinMode(relay, OUTPUT);
  pinMode(but, INPUT);
  pinMode(ledi, OUTPUT);
  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  //Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    //Serial.print(".");
    delay(500);
  }
  digitalWrite(ledi,LOW);
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
}

void loop() {
  if(Firebase.getInt(firebaseData, "CASA/LED"))
  {
    //Success
    led = firebaseData.intData();
    digitalWrite(relay, led);
  }
  if (digitalRead(but)==0){
    onoff=!onoff;
    Firebase.setInt(firebaseData, "CASA/LED", onoff);
    delay(100);
  }
}
