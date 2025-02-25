#include <esp_now.h>
#include <WiFi.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <Firebase_ESP_Client.h>

LiquidCrystal_I2C lcd(0x27, 16, 2); // set the LCD address to 0x27 for a 16 chars and 2 line display

// Must match the sender structure
typedef struct struct_message {
  int value = 0;
} struct_message;

volatile bool dataChanged = false;
// Create a struct_message called myData
struct_message myData;

// callback function that will be executed when data is received
void OnDataRecv(const uint8_t * mac, const uint8_t *incomingData, int len) {
  memcpy(&myData, incomingData, sizeof(myData));
  Serial.print("Bytes received: ");
  Serial.println(len);
  Serial.print("From RSU : ");
  Serial.println(myData.value);
  if (myData.value == 1) {
    lcd.clear();
    lcd.setCursor(3, 0);
    lcd.print("Help Me !");
    dataChanged = true;
  }
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

uint32_t pmillis = 0;


void setup() {
  // Initialize Serial Monitor
  Serial.begin(115200);
  lcd.init();
  // Print a message to the LCD.
  lcd.backlight();
  lcd.setCursor(0, 0);
  lcd.print("Vanet Group 10");

  // Set device as a Wi-Fi Station
  WiFi.mode(WIFI_STA);

  // Init ESP-NOW
  if (esp_now_init() != ESP_OK) {
    Serial.println("Error initializing ESP-NOW");
    return;
  }

  // Once ESPNow is successfully Init, we will register for recv CB to
  // get recv packer info
  esp_now_register_recv_cb(OnDataRecv);
}

String points = " ", text = " ";
void loop() {
  if (dataChanged) {
    initFirebase();
    //    connectionEstablished();
    delay(2000);
    read_data();
    if (points == "1") {
      text = "Point A";
    }
    else if (points == "2") {
      text = "Point B";
    }
    else if (points == "3") {
      text = "Point C";
    }

    lcd.setCursor(3, 1);
    lcd.print(text);
  }
}

void read_data()
{
  if (Firebase.ready() && signupOK && (millis() - readDataPrevMillis > 2500 || readDataPrevMillis == 0))
  {
    readDataPrevMillis = millis();
    if (Firebase.RTDB.getString(&fbdo, "/V2I/points")) { // Mode Selection
      if (fbdo.dataType() == "string") {
        points  = fbdo.stringData();
//        Serial.println("Point : " + str);
      }
    }
  }
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
