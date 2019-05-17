/*
  ESP8266 temperature sensor
*/

#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <OneWire.h>
#include <DallasTemperature.h>

// Update these with values suitable for your network.

const char* ssid = "iot-wlan";
const char* password = "iot-wlan";
const char* mqtt_server = "192.168.100.122";

// Temperature sensor
// GPIO des ESP
#define ONE_WIRE_BUS 05

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire); 

//ADC_MODE(ADC_VCC); 

// control snake
char* SNAKE_TOPIC = "snake/0";
char* NORTH = "NORTH";
char* EAST = "EAST";
char* SOUTH = "SOUTH";
char* WEST = "WEST";
int direction = 0;

WiFiClient espClient;
PubSubClient client(espClient);

long lastMsg = 0;
char msg[50];

int value = 0;

float lastTemp;
float lastHum;

// LED pin
#define D0 16
#define LED D0
int state = 0;

void ledBlink() {
  digitalWrite(LED, state > 0);
  state = 1 - state;
}

void ledOff() {
  digitalWrite(LED, HIGH);
  state = 0;
}

void setup_wifi() {
  delay(10);
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    ledBlink();
    delay(50);
    Serial.print(".");
  }

  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  // switch of LED
  ledOff();
}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      ledBlink();
      delay(200);
      ledBlink();
      delay(200);
      ledBlink();
      delay(200);
      ledBlink();
      delay(200);
      ledBlink();
      delay(200);
      ledBlink();
      delay(4000);
    }
  }
}

void setup() {
  // Initialize the BUILTIN_LED pin as an output
  pinMode(BUILTIN_LED, OUTPUT);
  Serial.begin(115200);

  // setup network
  setup_wifi();
  client.setServer(mqtt_server, 1883);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  // send mqtt message every 500ms
  long now = millis();
  if (now - lastMsg > 500) {
    lastMsg = now;

    char* payload;

    // Abfrage Temperatur
    double t;
    sensors.requestTemperatures();
    t = sensors.getTempCByIndex(0);

    Serial.print("Temperature read: ");
    Serial.print(t);
    Serial.print(". Previous temperature: ");
    Serial.println(lastTemp);

    if (!isnan(t) && t - 0.05 > lastTemp) {
      turnLeft();
    } else if (!isnan(t) && t + 0.05 < lastTemp) {
      turnRight();
    } else {
      Serial.println("Keep straight ahead!");
    }
    lastTemp = t;

    if (0 == direction) {
      payload = NORTH;
    } else if (1 == direction) {
      payload = EAST;
    } else if (2 == direction) {
      payload = SOUTH;
    } else if (3 == direction) {
      payload = WEST;
    }

    snprintf(msg, 50, "Send topic %s with payload %s\n", SNAKE_TOPIC, payload);
    Serial.print(msg);

    // send message
    ledBlink();
    client.publish(SNAKE_TOPIC, payload);
    ledBlink();
  }
}

void turnLeft() {
  direction++;
  while (direction > 3) direction -= 4;

  Serial.print("turnLeft: ");
  Serial.println(direction);
}

void turnRight() {
  direction--;
  while (direction < 0) direction += 4;  

  Serial.print("turnRight: ");
  Serial.println(direction);
}
