#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <WebSocketsServer.h>
//#include <ESP8266mDNS.h>
#include "template.h"
#include "template2.h"

#define LED_BUILTIN 2
#define TRIGGERPIN D2
#define ECHOPIN    D3
#define SENSORPIN  D1
#define GATE       1    // 1 o 2 para Gate1 y Gate2 respectivamente

// Your WiFi credentials
char ssid[] = "dlink-E090";
char pass[] = "wxdai25760";
IPAddress ip1(192, 168, 1, 115);
IPAddress ip2(192, 168, 1, 118);
//IPAddress gateway(192, 168, 1, 39);  // IP Address of your WiFi Router (Gateway)
//IPAddress subnet(255, 255, 255, 0); // Subnet mask
String gate1 = "CronoGate1";
String gate2 = "CronoGate2";
String hostName = "";

// Chrono Counter variables
int blink = HIGH;
boolean gateOpen = true;
long lastSent = 0;

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
  
  // Fijamos nombre de red
  if(GATE==1)
    hostName = gate1;
  else
    hostName = gate2;
  WiFi.hostname(hostName);  
   
  // Conectamos a la red WiFi
  Serial.println();
  Serial.print(hostName);
  Serial.print(" connecting to ");
  Serial.println(ssid);
 
  /* Configuramos el ESP8266 como cliente WiFi. Si no lo hacemos 
     se configurará como cliente y punto de acceso al mismo tiempo */
  WiFi.mode(WIFI_STA); // Modo cliente WiFi
  WiFi.begin(ssid, pass);

  // Esperamos a que estemos conectados a la red WiFi
  while (WiFi.status() != WL_CONNECTED) {
    delay(600);
    blink = !blink;
    digitalWrite(LED_BUILTIN, blink);
    Serial.print(".");
  }

  /*/ Fijamos direccion IP estatica
  if(GATE==1)
    WiFi.config(ip1, WiFi.subnetMask(), WiFi.gatewayIP());
  else
    WiFi.config(ip2, WiFi.subnetMask(), WiFi.gatewayIP());*/

  Serial.println("");
  Serial.println("WiFi connected"); 
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP()); // Mostramos la IP

  // Activamos web server
  server.on("/", handleRoot);
  server.on("/ko", handleKo);
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
    Serial.println("Object detected into Gate");
    delay(500); // To avoid spoureous
    digitalWrite(LED_BUILTIN, HIGH);
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
  int status = digitalRead(SENSORPIN);
  if( status == HIGH) return true; // LOW en version anterior
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
          Serial.printf("[%u] get Text: %s\n", num, payload);
          // tratar comandos start/stop (deprecated)
          if(strcmp("start", (const char *)payload) == 0) {
            //handleStart();
          }
          else if(strcmp("stop", (const char *)payload) == 0) {
            //handleStop();
          }
          else {
            Serial.println("Unknown command");
          }
          }
          break;
    }
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
