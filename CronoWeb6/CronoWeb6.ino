#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <WebSocketsServer.h>
#include <EEPROM.h>
//#include <ESP8266mDNS.h>
#include "template.h"
#include "template2.h"
#include "template3.h"
#include "worker_js.h"

#define LED_BUILTIN 2
#define TRIGGERPIN D2
#define ECHOPIN    D3
#define SENSORPIN  D1
#define GATE       1    // 1 o 2 para Gate1 y Gate2 respectivamente
#define ENTRADA    1    // 1 o 2 para entrada Digital o Analogica respectivamente

// Your WiFi credentials
char ssid[] = "CronoAP"; // "dlink-E090";
char pass[] = "wxdai25760";
IPAddress ip1(192, 168, 4, 115);
IPAddress ip2(192, 168, 4, 118);
IPAddress gateway(192, 168, 4, 1);  // IP Address of your WiFi Router (Gateway)
IPAddress subnet(255, 255, 255, 0); // Subnet mask
String gate1 = "CronoGate1";
String gate2 = "CronoGate2";
String hostName = "";

// Chrono Counter variables
int blink = HIGH;
boolean gateOpen = true;
long lastSent = 0;
int threshold = 100;

// Internal Web application
ESP8266WebServer server(80);

// Websockets configuration
WebSocketsServer webSocket = WebSocketsServer(81);

void setup()
{
  // Debug console
  Serial.begin(9600);
  delay(1000);
  
  // Digital pins configuration
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(TRIGGERPIN, OUTPUT);
  pinMode(ECHOPIN, INPUT);
  pinMode(SENSORPIN, INPUT_PULLUP);
  EEPROM.begin(8);
  EEPROM.get(0, threshold);
  if(threshold<1 || threshold>1023) threshold = 100;
  
  if(GATE==1){
    // Fijamos nombre de red
    hostName = gate1;
    WiFi.hostname(hostName);
    
    // Activamos el Wifi Access Point solo en Gate 1
    Serial.print("Creating Access Point ");
    Serial.println(ssid);
    
    WiFi.mode(WIFI_AP);
    WiFi.softAPConfig(ip1, gateway, subnet);
    while(!WiFi.softAP(ssid, pass)){
      delay(600);
      blink = !blink;
      digitalWrite(LED_BUILTIN, blink);
      Serial.print(".");
    }
    Serial.println("Ready");
    
    Serial.println("");
    Serial.print("Access Point IP address: ");
    Serial.println(WiFi.softAPIP());
  }
  else{
    // Fijamos nombre de red
    hostName = gate2;
    WiFi.hostname(hostName);
    
    // Conectamos a la red WiFi
    Serial.print("Connecting to ");
    Serial.println(ssid);
    /* Configuramos el ESP8266 como cliente WiFi. Si no lo hacemos 
       se configurará como cliente y punto de acceso al mismo tiempo */
    WiFi.mode(WIFI_STA); // Modo cliente WiFi
    WiFi.config(ip2, gateway, subnet);
    WiFi.begin(ssid, pass);
    
    // Esperamos a que estemos conectados a la red WiFi
    while (WiFi.status() != WL_CONNECTED) {
      delay(600);
      blink = !blink;
      digitalWrite(LED_BUILTIN, blink);
      Serial.print(".");
    }
    //WiFi.config(ip2, WiFi.gatewayIP(), WiFi.subnetMask());
    WiFi.setAutoReconnect(true);
    Serial.println("");
    Serial.println("WiFi connected"); 
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP()); // Mostramos la IP
  }

  // Activamos web server
  server.on("/", handleRoot);
  server.on("/ko", handleKo);
  server.on("/config", handleConfig);
  server.on("/worker.js", handleWorker);
  server.onNotFound(handleNotFound);
  server.begin();
  Serial.println("HTTP server started");

  // Activamos websockets server
  webSocket.begin();
  webSocket.onEvent(webSocketEvent);
  Serial.println("WebSocket started");

  // configuracion nombre mDNS
  //if(MDNS.begin(hostName))
  //  Serial.println("mDNS started");
  Serial.println("");
  
}

void loop()
{
  // Check for object detected
  gateOpen = objectDetected();
  if(gateOpen){
    // Envia Open inmediatamente
    sendStatus(gateOpen);
    //Serial.println("Object detected into Gate");
    digitalWrite(LED_BUILTIN, HIGH);
    delay(500); // To avoid spoureous
  }
  else{
    digitalWrite(LED_BUILTIN, LOW);
    // Envia Close cada 800 millisegundos
    if(millis()-lastSent>800){
      sendStatus(gateOpen);
      lastSent = millis();
    }
  }
  
  // maneja peticiones Http y Websocket
  server.handleClient();
  webSocket.loop();
  
  // actualiza mDNS
  //MDNS.update();
  
}

boolean objectDetected() // Check for object detection
{
  if(ENTRADA==1){
    int status = digitalRead(SENSORPIN);
    if( status == HIGH) return true; // LOW en version anterior
  }
  else{
    if( millis() % 2 != 0 ) // To avoid Wifi contention
       return false;
    int sensorValue = analogRead(A0);
    //delay(3);
    //Serial.printf("sensorValue      = %d\n", sensorValue);
    //Serial.printf("Sensor threshold = %d\n\n", threshold);
    if(sensorValue<threshold)
      return true;
  }
  return false;
}

void handleRoot() {
  Serial.println("Into handleRoot()...");
  // Returns Web Application to manage the chrono - timectrial
  server.send(200, "text/html", INDEX_HTML);
}

void handleKo() {
  Serial.println("Into handleKo()...");
  // Returns Web Application to manage the chrono - KO system
  server.send(200, "text/html", KO_HTML);
}

void handleConfig(){
  Serial.println("Into handleConfig()...");
  // Returns Web Application to manage the chrono - Configuration
  server.send(200, "text/html", CONFIG_HTML);
}

void handleWorker(){
  Serial.println("Into handleWorker()...");
  // Returns Web Application to manage the chrono - Web Worker
  server.send(200, "text/plain", WORKER_JS);
}

void handleNotFound() {
  // Returns "page not found" error message
  String message = "Endpoint Not Found\n\n";
  message += "URI: ";
  message += server.uri();
  message += "\nMethod: ";
  message += (server.method() == HTTP_GET) ? "GET" : "POST";
  message += "\nArguments: ";
  message += server.args();
  message += "\n";
  for (uint8_t i = 0; i < server.args(); i++) {
    message += " " + server.argName(i) + ": " + server.arg(i) + "\n";
  }
  server.send(404, "text/plain", message);
}

void webSocketEvent(uint8_t num, WStype_t type, uint8_t * payload, size_t length) {
    switch(type) {
        case WStype_DISCONNECTED:
          Serial.printf("[%u] Disconnected!\n", num);
          break;
        case WStype_CONNECTED:
        {
          IPAddress ip = webSocket.remoteIP(num);
          Serial.printf("[%u] Connected from %d.%d.%d.%d url: %s\n", num, ip[0], ip[1], ip[2], ip[3], payload);
        }
        break;
        case WStype_TEXT:
        {
          int val = 0;
          Serial.printf("[%u] get Text: %s\n", num, payload);
          // tratar comandos threshold
          if(strcmp("threshold", (const char *)payload) == 0) {
            sendThreshold();
          }
          else if((val=atoi((const char *)payload))>0){
            saveThreshold(val);
          }
          else {
            Serial.println("Unknown command");
          }
        }
        break;
    }
}

void saveThreshold(int val){
   // save num into EEPROM
   threshold = val;
   Serial.printf("New Sensor threshold = %d\n", threshold);
   EEPROM.put(0, threshold);
   EEPROM.commit();
}

void sendThreshold(){
  char cmd[16];
  sprintf(cmd, "%d", threshold);
  webSocket.broadcastTXT(cmd);
  Serial.printf("Sending Sensor threshold = %d\n", threshold);
}

void sendStatus(boolean status){
  if(GATE==1){
    if(status==true)
      webSocket.broadcastTXT("g1KO");
    else
      webSocket.broadcastTXT("g1OK");
  }
  else{
    if(status==true)
      webSocket.broadcastTXT("g2KO");
    else
      webSocket.broadcastTXT("g2OK");
  }
}
