#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>
#include "FirebaseESP8266.h"
#include <ESP8266HTTPClient.h>
#include <NTPClient.h>
#include <WiFiUdp.h>


FirebaseData firebaseData;

// required libraries
// Set these to run example.
#define FIREBASE_HOST "casain-2020.firebaseio.com" //please remove http and / from firebase url
#define FIREBASE_AUTH "zBSt0VZEfZgYMmhXotEoAaRr67ohO6r4Yt2UfEWY" // paste secret key here
#define WIFI_SSID "NOS-CASA"
#define WIFI_PASSWORD "redecasa"

WiFiUDP ntpUDP;

String myString; // sending integer data as string ,sensor data can also be send as integer
String motion = "Movimento detetado";   // string which stores state - motion/no motion
String nomotion="Não existe movimento";
int sdata = 0; // PIR sensor  value will be stored in sdata.
int Status = 12;  // Digital pin D6
int sensor = 13;  // Digital pin D7
char dat;
const long utcOffsetInSeconds = 0;





NTPClient timeClient(ntpUDP, "pool.ntp.org", utcOffsetInSeconds);

void setup() {
Serial.begin(9600);
  pinMode(sensor, INPUT);   // declare sensor as input
  pinMode(Status, OUTPUT);  // declare LED as output
  int val = 0;  
WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED)
      {
    Serial.print(".");
    delay(500);
      }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());
  
  timeClient.begin();
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
 
}
void loop() {
  timeClient.update();
  sdata = digitalRead(sensor);
  myString = String(sdata); 
  Serial.println(myString);//printing string to verify whether sensor is working or not  
  Firebase.setString(firebaseData ,"CASA/SENSOR_MOV/VALOR",myString);//A string will be sent to real time database under variable /value (user need not create this in advance)
  delay(1000); 
  long state = digitalRead(sensor);
  if(state == HIGH) {
    digitalWrite (Status, HIGH);
    Serial.println("Motion detected!");
    //Serial.println(state);

    
    unsigned long epochTime = timeClient.getEpochTime();
    
    String formattedTime = timeClient.getFormattedTime();
    
    struct tm *ptm = gmtime ((time_t *)&epochTime);
    
    int monthDay = ptm->tm_mday;

    int currentMonth = ptm->tm_mon+1;
  
    int currentYear = ptm->tm_year+1900;
  
    //complete date:
    String Time =formattedTime + " " + String(monthDay) + "/" + String(currentMonth) + "/" + String(currentYear);

    Serial.println(Time);
    
    
    
    Firebase.setString(firebaseData ,"CASA/SENSOR_MOV/ALERTA",motion);//A string will be sent to real time database under state- alert -ex -  motion , no motion 
    
    Firebase.setString(firebaseData , "CASA/SENSOR_MOV/DATA",Time);
    
    delay(1000);
  }
  else{
    digitalWrite (Status, LOW);
    Serial.println("Não existe movimento");
    Firebase.setString(firebaseData ,"CASA/SENSOR_MOV/ALERTA",nomotion);
    delay(1000);
  }
}
