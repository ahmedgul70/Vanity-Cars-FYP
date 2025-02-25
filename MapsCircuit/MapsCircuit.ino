#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>

//Provide the token generation process info.
#include "addons/TokenHelper.h"
//Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

// Insert your network credentials
#define WIFI_SSID "VANET"
#define WIFI_PASSWORD "12345678"

// Insert Firebase project API Key
#define API_KEY "AIzaSyAfNlYRhXMwtIhnCa7VBmWm-uV0JUOGErA"

// Insert RTDB URLefine the RTDB URL */
#define DATABASE_URL "https://vanitycars-37b73-default-rtdb.firebaseio.com/"

//Define Firebase Data object
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

uint32_t sendDataPrevMillis = 0;
bool signupOK = false;
volatile bool dataChanged = false;
uint32_t pmillis = 0;

//Define Ultrasonic Sensors Pins
const int trig1 = 26;  // Point 1
const int echo1 = 25;
const int trig2 = 14;  // Point 2
const int echo2 = 27;
const int trig3 = 19;  // Point 3
const int echo3 = 18;

// **************************************************************************************************************
void setup() {
  Serial.begin(115200);
  initPins();
  initFirebase();
  connectionEstablished();
  delay(2000);
  send_data("1");
  pmillis = millis();
}

void loop() {
  if (millis() - pmillis >= 3000) { // 3SEC
    pmillis = millis();
//    Serial.print(read_ultra(trig1, echo1));
//    Serial.print("\t");
//    Serial.print(read_ultra(trig2, echo2));
//    Serial.print("\t");
//    Serial.print(read_ultra(trig3, echo3));
//    Serial.println(" ");

    //    if (read_ultra(trig1, echo1) < 50.0 && read_ultra(trig1, echo1) > 0.0) {
    //      Serial.println("Point A");
    //      digitalWrite(2, HIGH);
    //      send_data("0");
    //    }
    if (read_ultra(trig2, echo2) < 50.0 && read_ultra(trig2, echo2) > 0.0) {
      Serial.println("Point B");
      digitalWrite(2, HIGH);
      send_data("3");
    }
    else if (read_ultra(trig3, echo3) < 50.0 && read_ultra(trig3, echo3) > 0.0) {
      Serial.println("Point C");
      digitalWrite(2, HIGH);
      send_data("0");
    }
  }
}
// **************************************************************************************************************

void connectionEstablished() {
  for (int i = 0; i < 5; i++) {
    digitalWrite(2, HIGH);
    delay(250);
    digitalWrite(2, LOW);
    delay(250);
  }
}

void send_data(String object) {
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 2500)) {
    sendDataPrevMillis = millis();
    Firebase.RTDB.setString(&fbdo, "V2I/points", object);
  }
}

double read_ultra(const int trigPin, const int echoPin) {
  double distance = 0;
  unsigned long duration = 0;
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  // Reads the echoPin, returns the sound wave travel time in microseconds
  duration = pulseIn(echoPin, HIGH);

  // Calculating the distance
  distance = (duration * 0.034) / 2;  // Speed of sound wave divided by 2 (go and back)
  return distance;
}

void initPins() {
  pinMode(2, OUTPUT);
  digitalWrite(2, LOW);
  pinMode(echo1, INPUT);
  pinMode(trig1, OUTPUT);
  pinMode(echo2, INPUT);
  pinMode(trig2, OUTPUT);
  pinMode(echo3, INPUT);
  pinMode(trig3, OUTPUT);
}

void initFirebase() {
  //  WiFi.mode(WIFI_STA);
  // Firebase Initialization
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  /* Assign the api key (required) */
  config.api_key = API_KEY;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;

  /* Sign up */
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("ok");
    signupOK = true;
  } else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback;  //see addons/TokenHelper.h

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  Serial.println("Wifi Connected !!");
  delay(1000);
}
