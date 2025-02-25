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
bool signupOK = false , ObjectFlag = false, ObjectFlagTemp = false;
volatile bool dataChanged = false;
uint32_t pmillis = 0;

LiquidCrystal_I2C lcd(0x3F, 16, 2);
//Define Ultrasonic Sensors Pins
const int trig1 = 18; // front sensor
const int echo1 = 19;

// **************************************************************************************************************
void setup() {
  Serial.begin(115200);
  initPins();
  initFirebase();

  connectionEstablished();
  send_data("0");
}


void loop() {
  if (millis() - pmillis >= 5000) { // 5 SEC
    pmillis = millis();
    lcdUpdate();
    if (read_ultra(trig1, echo1) < 2.5) {
      //      ObjectFlag = true;
      //      if (ObjectFlagTemp != ObjectFlag) {
      //        ObjectFlagTemp = ObjectFlag;
      Serial.println("Car Detected");
      digitalWrite(2, HIGH);
      send_data("1");
      //      }
    }
    else {
      //      ObjectFlag = false;
      //      if (ObjectFlagTemp != ObjectFlag) {
      //        ObjectFlagTemp = ObjectFlag;
      Serial.println("No Object Detected");
      digitalWrite(2, LOW);
      send_data("0");
      //      }
    }
  }
}
// **************************************************************************************************************
void lcdUpdate() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Front: ");
  lcd.print(read_ultra(trig1, echo1), 2);
  lcd.print("m");
}

void connectionEstablished() {
  for (int i = 0; i < 5; i++) {
    digitalWrite(2, HIGH);
    delay(250);
    digitalWrite(2, LOW);
    delay(250);
  }
}

void send_data(String object)
{
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 2500))
  {
    sendDataPrevMillis = millis();
    Firebase.RTDB.setString(&fbdo, "Security/CarFlag", object);
  }
}

double read_ultra(const int trigPin , const int echoPin) {
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
  distance = (duration * 0.034) / 2; // Speed of sound wave divided by 2 (go and back)
  distance = distance / 100.0;
  return distance;
}

void initPins() {
  pinMode(2, OUTPUT);
  digitalWrite(2, LOW);
  pinMode(echo1, INPUT);
  pinMode(trig1, OUTPUT);

  // Print a message to the LCD.
  lcd.init();
  lcd.backlight();

  lcd.setCursor(0, 0);
  lcd.print("Vanet Group 10");
  delay(3000);
//  lcd.clear();
}

void initFirebase() {
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
  }
  else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  Serial.println("Wifi Connected !!");
  delay(1000);
}
