#include <esp_now.h>
#include <WiFi.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <Firebase_ESP_Client.h>

// REPLACE WITH YOUR RECEIVER MAC Address -- B0:A7:32:DB:9E:98 (RSU Unit)
uint8_t broadcastAddress[] = {0xB0, 0xA7, 0x32, 0xDB, 0x9E, 0x98};

typedef struct struct_message {
  int value=0;
} struct_message;

// Create a struct_message called myData
struct_message myData;

esp_now_peer_info_t peerInfo;

// callback when data is sent
void OnDataSent(const uint8_t *mac_addr, esp_now_send_status_t status) {
  Serial.print("\r\nLast Packet Send Status:\t");
  Serial.println(status == ESP_NOW_SEND_SUCCESS ? "Delivery Success" : "Delivery Fail");
}

//Provide the token generation process info.
#include "addons/TokenHelper.h"
//Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

// Insert your network credentials
const char* WIFI_SSID = "VANET";
const char* WIFI_PASSWORD = "12345678";

// Insert Firebase project API Key
#define API_KEY "AIzaSyAfNlYRhXMwtIhnCa7VBmWm-uV0JUOGErA"

// Insert RTDB URLefine the RTDB URL */
#define DATABASE_URL "https://vanitycars-37b73-default-rtdb.firebaseio.com/"

//Define Firebase Data object
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

uint32_t sendDataPrevMillis = 0, readDataPrevMillis = 0;
bool signupOK = false;
volatile bool dataChanged = false;
uint32_t pmillis = 0;

LiquidCrystal_I2C lcd(0x3F, 16, 2);
//Define Ultrasonic Sensors Pins
const int trig1 = 18; // front sensor
const int echo1 = 19;

bool accidentFlag = false;
String accident = " ", timerStatus = " ";

void setup() {
  Serial.begin(115200);

  initFirebase();
  connectionEstablished();

  initPins();
  delay(2000);
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 2500))
  {
    sendDataPrevMillis = millis();
    Firebase.RTDB.setString(&fbdo, "V2I/accident", "0");
    Firebase.RTDB.setString(&fbdo, "V2I/TimerCompleted", "0");
  }
}

int count = 0;
void loop() {
  if (millis() - pmillis >= 2000) {
    pmillis = millis();
    lcdUpdate();
    if (read_ultra(trig1, echo1) < 0.1) {
      digitalWrite(2, HIGH);
      accidentFlag = true;
    } else {
      accidentFlag = false;
      digitalWrite(2, LOW);
    }
  }

  if (accidentFlag) {
    send_data("1");
    while (accidentFlag) {
      delay(1000);
      read_data();
      if (timerStatus == "1" && accident == "1") {
        WiFi.disconnect();
        sendRSU();
        break;
      }
      else if (accident == "0") {
        break;
      }      
    }
    accidentFlag = false;
  }
}

void sendRSU() {
  WiFi.mode(WIFI_AP_STA);
  // Init ESP-NOW
  if (esp_now_init() != ESP_OK) {
    Serial.println("Error initializing ESP-NOW");
    return;
  }

  esp_now_register_send_cb(OnDataSent);

  // Register peer
  memcpy(peerInfo.peer_addr, broadcastAddress, 6);
  peerInfo.channel = 0;
  peerInfo.encrypt = false;

  // Add peer
  if (esp_now_add_peer(&peerInfo) != ESP_OK) {
    Serial.println("Failed to add peer");
    return;
  }
  delay(1000);
  // Set values to send
  myData.value = 1;

  // Send message via ESP-NOW
  esp_err_t result = esp_now_send(broadcastAddress, (uint8_t *) &myData, sizeof(myData));

  if (result == ESP_OK) {
    Serial.println("Sent with success");
  }
  else {
    Serial.println("Error sending the data");
  }
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
}

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

void read_data()
{
  if (Firebase.ready() && signupOK && (millis() - readDataPrevMillis > 2500 || readDataPrevMillis == 0))
  {
    readDataPrevMillis = millis();
    if (Firebase.RTDB.getString(&fbdo, "/V2I/TimerCompleted")) { // Mode Selection
      if (fbdo.dataType() == "string") {
        timerStatus  = fbdo.stringData();
        //        Serial.println("Timer Status : " + str);
      }
    }
    if (Firebase.RTDB.getString(&fbdo, "/V2I/accident")) { // Mode Selection
      if (fbdo.dataType() == "string") {
        accident  = fbdo.stringData();
        //        Serial.println("Timer Status : " + str);
      }
    }
  }
}

void send_data(String value)
{
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 2500))
  {
    sendDataPrevMillis = millis();
    Firebase.RTDB.setString(&fbdo, "V2I/accident", value);
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
  //  Firebase.reconnectWiFi(true);

  Serial.println("Wifi Connected !!");
  delay(1000);
}
