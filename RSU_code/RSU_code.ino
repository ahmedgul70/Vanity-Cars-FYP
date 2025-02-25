#include "WiFi.h"
#include <esp_now.h>

// REPLACE WITH YOUR RECEIVER MAC Address - 40:22:D8:EA:32:DC (Hospital)
uint8_t broadcastAddress[] = {0x40, 0x22, 0xD8, 0xEA, 0x32, 0xDC};

typedef struct struct_message {
  int value = 0;
} struct_message;

// Create a struct_message called myData
struct_message myData;
struct_message tobeSent;

esp_now_peer_info_t peerInfo;
int fromMaster = 0;

// callback when data is sent
void OnDataSent(const uint8_t *mac_addr, esp_now_send_status_t status) {
  Serial.print("\r\nLast Packet Send Status:\t");
  Serial.println(status == ESP_NOW_SEND_SUCCESS ? "Delivery Success" : "Delivery Fail");
}

bool received = false;
// callback function that will be executed when data is received
void OnDataRecv(const uint8_t * mac, const uint8_t *incomingData, int len) {
  memcpy(&myData, incomingData, sizeof(myData));
  Serial.print("Bytes received: ");
  Serial.println(len);
  Serial.print("From Accidental Car : ");
  Serial.println(myData.value);
  fromMaster = myData.value;
  received = true;
}

void setup() {
  Serial.begin(115200);
  pinMode(2, OUTPUT);
  digitalWrite(2, LOW);

  // Set device as a Wi-Fi Station
  WiFi.mode(WIFI_STA);

  // Init ESP-NOW
  if (esp_now_init() != ESP_OK) {
    Serial.println("Error initializing ESP-NOW");
    return;
  }



  // Once ESPNow is successfully Init, we will register for Send CB to
  // get the status of Trasnmitted packet
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

  // Once ESPNow is successfully Init, we will register for recv CB to
  // get recv packer info
  esp_now_register_recv_cb(OnDataRecv);
}

void loop() {
  if (received) {
    received = false;
    if (fromMaster == 1) {
      tobeSent.value = 1;
      // Send message via ESP-NOW
      esp_err_t result = esp_now_send(broadcastAddress, (uint8_t *) &tobeSent, sizeof(tobeSent));

      if (result == ESP_OK) {
        Serial.println("Sent with success");
        digitalWrite(2, HIGH);
      }
      else {
        Serial.println("Error sending the data");
        digitalWrite(2, LOW);
      }
    }
  }
  delay(250);
}
