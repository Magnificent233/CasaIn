#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>
#include "ACS712.h"


#define FIREBASE_HOST "casain-2020.firebaseio.com" //please remove http and / from firebase url
#define FIREBASE_AUTH "zBSt0VZEfZgYMmhXotEoAaRr67ohO6r4Yt2UfEWY" // paste secret key here
#define WIFI_SSID "NOS-CASA"
#define WIFI_PASSWORD "redecasa"
#define Relay1 D1

FirebaseData firebaseData;
const int sensorIn = A0;
int mVperAmp = 185;
double Voltage=0;
double VRMS=0;
double AmpsRMS=0;
int sumWatt;
float preco;
int Wattage;
int rel;

void setup()
{
  pinMode(Relay1, OUTPUT);
  digitalWrite(Relay1,HIGH);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  //Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    //Serial.print(".");
    delay(300);
  }
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.setInt(firebaseData,"CASA/ELETRICIDADE/STATUS",0); 
  Firebase.reconnectWiFi(true);
  pinMode(A0, INPUT);
  Serial.begin(115200);
  delay(10);
  Firebase.setFloatDigits(2);
}

void loop()
{
  Voltage = getVPP();
  VRMS = (Voltage/2.0) *0.707; // sq root
  AmpsRMS = (VRMS * 1000)/mVperAmp;
  Wattage = (220*AmpsRMS); //Observado 15-20W quando o equipamento não está ligado, devido a induções dos cabos, equipamentos próximos que causam alguma interferência.
  sumWatt = sumWatt + Wattage; 
  //preco = (sumWatt/1000)*0.14*1.23; //preço estimado do consumo elétrico. Este valor não inclui outras taxas aplicáveis(imposto,contador). 
  Firebase.setInt(firebaseData, "CASA/ELETRICIDADE/WATTS",Wattage);
  Firebase.setInt(firebaseData, "CASA/ELETRICIDADE/TOTAL_ENERGIA",sumWatt); //soma da energia consumida.
    
 if(Firebase.getInt(firebaseData,"CASA/ELETRICIDADE/STATUS")){
  //Reading the value of the varialble Status from the firebase
  rel = firebaseData.intData();
 
  Serial.println(rel);
  if(rel==1) 
     {
      digitalWrite(Relay1,HIGH);
      Serial.println("Relay 1 ON");
    }
  if(rel==0)
    {                                      
      digitalWrite(Relay1,LOW);
      Serial.println("Relay 1 OFF");
    }
}
}
  float getVPP(){
    
  float result;
  int readValue; //value read from the sensor
  int maxValue = 0; // store max value here
  int minValue = 1024; // store min value here

  uint32_t start_time = millis();

  while((millis()-start_time) < 1000){
  readValue = analogRead(sensorIn);
// see if you have a new maxValue
  if (readValue > maxValue)
{
/*record the maximum sensor value*/
  maxValue = readValue;
}
  if (readValue < minValue)
{
/*record the maximum sensor value*/
  minValue = readValue;
}
}

// Subtract min from max
  result = ((maxValue - minValue) * 5)/1024.0;
  return result;
}
