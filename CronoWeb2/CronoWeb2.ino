#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <WebSocketsServer.h>
//#include <ESP8266mDNS.h>
#include "template.h"

#define LED_BUILTIN 2
#define TRIGGERPIN D2
#define ECHOPIN    D3
#define SENSORPIN  D1
#define GATE       2    // 1 o 2 para Gate1 y Gate2 respectivamente

// Your WiFi credentials
char ssid[] = "dlink-E090";
char pass[] = "wxdai25760";

// Chrono Counter variables
long counter = 1;
long startTime = 0;
boolean running1 = false;
boolean running2 = false;
int blink = HIGH;
long lastblink = 0;
boolean gate2open = true;
boolean gate1open = true;

// Internal Web application
ESP8266WebServer server(80);
// CronoWeb application
const char* hostName = "137.74.195.144";   // Web(http) server IP
const int httpPort = 8845;                 // Puerto HTTP 

// Udp Commands
typedef enum CommandEnum:long {
  TIMER =  1,
  VOID  =  0,
  START = -1,
  STOP  = -2,
  OPEN  = -3,
  CLOSE = -4
} Command;

// Multicast configuration
IPAddress broadcastIp(238, 0, 0, 1);       // Multicast IP address
unsigned int localPort = 8888;             // local port to listen on multicast traffic
//char packetBuffer[UDP_TX_PACKET_MAX_SIZE]; // buffer to hold incoming packets
WiFiUDP Udp;                               // An EthernetUDP instance to let us send and receive packets

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
  
  // Conectamos a la red WiFi
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
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

  Serial.println("");
  Serial.println("WiFi connected"); 
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP()); // Mostramos la IP

  // Creamos conexion UDP
  Udp.beginMulticast(WiFi.localIP(), broadcastIp, localPort);

  // Activamos web server solo en Gate 1
  if(GATE==1){
    //if(MDNS.begin("esp8266"))
    //  Serial.println("MDNS responder started");
    server.on("/", handleRoot);
    server.on("/start", handleStart);
    server.on("/stop", handleStop);
    server.on("/time", handleTime);
    server.on("/g1status", handleGate1Status);
    server.on("/g2status", handleGate2Status);
    server.on("/savetime", handleSaveTime);
    server.onNotFound(handleNotFound);
    server.begin();
    Serial.println("HTTP server started");
  }

  // Activamos websockets server solo en Gate 1
  if(GATE==1){
    webSocket.begin();
    webSocket.onEvent(webSocketEvent);
  }
}

void loop()
{
  // Bucle principal
  if(GATE==1)
    gate1check(); // Gate 1 logic
  else
    gate2check(); // Gate 2 logic
}

void gate1check() // Gate 1 logic
{
  if(running1){ // Chrono timer is running
    counter = millis() - startTime; // Increment crono time
    
    long message = udpCmdReceived();
    if(message == Command::STOP){ // Check for the Stop command
      running1 = false; // Stop counter
    }
    else if(message>=Command::TIMER){ // A new 'chrono time' is received
      running1 = false; // Stop counter
      Serial.print("Internal Counter = ");
      Serial.println(counter);
      Serial.print("Received Counter = ");
      Serial.println(message);
      counter = (counter+message)/2; // adjust the crono time
    }
    else if(counter>30000){ // 30 seconds
      running1 = false; // Stop counter
      Serial.println("Stop Gate 1 loop por timeout (30 sec)");
    }

    // Blinking LED indicates counter is running
    if(counter-lastblink>333){
      lastblink = counter;
      blink=!blink;
    }
    digitalWrite(LED_BUILTIN, blink);
  }
  else { // Chrono timer is not running - ready to start
    // Check for object detected
    gate1open = objectDetected();
    if(gate1open){
      if(counter==0){ // ready to start, otherwise needs a previous reset
        startTime = millis();
        running1 = true; // Start counter
        sendUdpCmd(Command::START); // Command Gate 2 to start counter
        Serial.println("Object detected into Gate 1 loop");
        lastblink = 0;
        delay(1000); // To avoid spoureous
      }
      digitalWrite(LED_BUILTIN, HIGH);
    }
    else
      digitalWrite(LED_BUILTIN, LOW);
    // In order to clean previous and non-valid STOP messages
    long message = udpCmdReceived();
    // Also Check for Gate 2 status
    if(message == Command::OPEN)
      gate2open = true;
    else if(message == Command::CLOSE)
      gate2open = false;
  }
  
  // maneja peticiones Http
  server.handleClient();
  webSocket.loop();
  // actualiza mDNS
  //MDNS.update();
  // Send data status and counter by WebSockets
  WSsendDataStatus();
}

void gate2check() // Gate 2 logic
{
  counter = millis() - startTime; // Increment crono time
  
  if(running2){ // Chrono timer is running
    
    // Check for object detected
    if(objectDetected()){
      running2 = false; // Stop counter
      sendUdpCmd(counter); // Command Gate 1 to stop counter with measured time
      delay(1000); // To avoid spoureous
    }
    else{
      long message = udpCmdReceived();   
      if(message==Command::STOP){ // Check for the Stop command
        running2 = false; // Stop counter
        delay(1000); // To avoid sporeous
      }
      else if(counter>30000){ // 30 seconds
        running1 = false; // Stop counter
        Serial.println("Stop Gate 2 loop por timeout (30 sec)");
      }
    }
    // Blinking LED indicates counter is running
    if(counter-lastblink>333){
      lastblink = counter;
      blink=!blink;
    }
    digitalWrite(LED_BUILTIN, blink);
  }
  else{ // Chrono timer is not running
    long message = udpCmdReceived();  
    if(message == Command::START){ // Check for the Start command
      running2 = true; // Start counter
      startTime = millis();
      Serial.println("Received START into Gate 2 loop");
      counter = 0;
      lastblink = 0;
    }

    // Also get gate 2 status
    gate2open = objectDetected();
    if(gate2open)
      digitalWrite(LED_BUILTIN, HIGH);
    else
      digitalWrite(LED_BUILTIN, LOW);
    // Periodically sends gate 2 status
    if(counter-lastblink>1200){
      lastblink = counter;
      if(gate2open)
        sendUdpCmd(Command::OPEN);
      else
        sendUdpCmd(Command::CLOSE);
    }
  }
}

boolean objectDetected() // Check for object detection
{
  int status = digitalRead(SENSORPIN);
  if( status == HIGH) return true; // LOW en version anterior
  return false;
}

long udpCmdReceived() // Receive command by Multicast
{
  // Returns a CommandEnum value:
  //  0 if no command received VOID
  // -1 if START
  // -2 if STOP
  // -3 if Gate 2 is OPEN
  // -4 if Gate 2 is CLOSE
  // received 'counter' value if TIMER
  char packetBuffer[UDP_TX_PACKET_MAX_SIZE]; // buffer to hold incoming packets
  
  // if there's data available, read a packet
  int packetSize = Udp.parsePacket();
  if (packetSize) {
    IPAddress remote = Udp.remoteIP();

    if(remote==WiFi.localIP()){
      //Serial.println("This packet is mine, ignore.");
      return Command::VOID;
    }

    // read the packet into packetBufffer
    int len = Udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
    //Serial.print("Received: ");
    //Serial.println(packetBuffer);
    long timer = 0;
    timer = atol(packetBuffer);
    //Serial.println(timer);
    return timer;
  }
  return Command::VOID;
}

void sendUdpCmd(long value) // Send command by Multicast
{
  // Send a command to the broadcast Address
  char cmd[16];
  sprintf(cmd, "%ld", value);
  Serial.print("Sending: ");
  Serial.println(cmd);
  Udp.beginPacketMulticast(broadcastIp, localPort, WiFi.localIP());
  Udp.write(cmd);
  Udp.endPacket();
}

void handleRoot() {
  // Returns Web Application to manage the chrono
  server.send(200, "text/html", INDEX_HTML);
}

void handleStart() {
  // AJAX command to start chrono timer from web app
  if(running1==false){
    counter = 0;
    startTime = millis();
    running1 = true; // Start counter
    sendUdpCmd(Command::START); // Command Gate 2 to start counter
    Serial.println("Start received");
    lastblink = 0;
  }
  handleTime();
}

void handleStop() {
   // AJAX command to stop chrono timer from web app
  if(running1==true){
    running1 = false; // Stop counter
    counter = millis() - startTime; // Increment crono time
    sendUdpCmd(Command::STOP); // Command Gate 2 to stop counter
    Serial.println("Stop received");
    Serial.print("Internal Counter = ");
    Serial.println(counter);
  }
  else{
    counter = 0;
    sendUdpCmd(Command::STOP); // Command Gate 2 to start counter
    Serial.println("Stop (reset) received");
    Serial.print("Internal Counter = ");
    Serial.println(counter);
  }
  handleTime();
}

void handleGate1Status() {
  //Serial.println("Gate1Status received");
  if(gate1open)
    server.send(201, "text/plain", "red");
  else
    server.send(201, "text/plain", "orange");
}

void handleGate2Status() {
  //Serial.println("Gate2Status received");
  if(gate2open)
    server.send(202, "text/plain", "red");
  else
    server.send(202, "text/plain", "orange");
}

void handleSaveTime() {
  Serial.println("SaveTime received");
  if(counter>3000)
    writeHttpEvent(counter); // Send the crono time by Http
}

void handleTime() {
   // AJAX command to get chrono timer from web app
  //Serial.println("Get Time received");
  char cmd[16];
  printfcomma(cmd, counter);
  server.send(200, "text/plain", cmd);
}

void printfcomma(char *s, long n) {
  if (n < 1000) {
    sprintf (s, "0.%03ld", n);
    return;
  }
  sprintf(s, "%ld.%03ld", n/1000, n%1000);
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

void writeHttpEvent(long time) // Send Chrono time to Web Server
{
  Serial.print("connecting to ");
  Serial.println(hostName);
 
  // Creamos el cliente Wifi
  WiFiClient client;
  if (!client.connect(hostName, httpPort)) {
    // ¿hay algún error al conectar?
    Serial.println("Ha fallado la conexión");
    return;
  }
 
  // Creamos la URL para la petición
  String url = "/crono/new/" + String(time);
 
  Serial.print("URL de la petición: http://");
  Serial.print(hostName);
  Serial.print(":");
  Serial.print(httpPort);
  Serial.println(url);
 
  // Enviamos la petición
  client.print(String("GET ") + url + " HTTP/1.1\r\n" +
         "Host: " + hostName + "\r\n" + 
         "Connection: close\r\n\r\n");
  unsigned long timeout = millis();
  while (client.available() == 0) {
    if (millis() - timeout > 5000) {
      Serial.println(">>> Superado el tiempo de espera !");
      client.stop();
      return;
    }
  }

  // Leemos la respuesta y la enviamos al monitor serie
  while(client.available()){
    String line = client.readStringUntil('\r');
    Serial.print(line);
  }
 
  Serial.println();
  Serial.println("Cerrando la conexión");
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
          // send counter to client
          WSsendDataStatus();
          /*char cmd[16];
          printfcomma(cmd, counter);
          webSocket.sendTXT(num, cmd);*/
          }
          break;
        case WStype_TEXT:
          {
          Serial.printf("[%u] get Text: %s\n", num, payload);
          // tratar comandos start/stop
          if(strcmp("start", (const char *)payload) == 0) {
            handleStart();
          }
          else if(strcmp("stop", (const char *)payload) == 0) {
            handleStop();
          }
          else {
            Serial.println("Unknown command");
          }
          // send data to all connected clients
          WSsendDataStatus();
          }
          break;
    }
}

void WSsendDataStatus(){
  if(gate1open)
    webSocket.broadcastTXT("g1statusKO");
  else
    webSocket.broadcastTXT("g1statusOK");
  if(gate2open)
    webSocket.broadcastTXT("g2statusKO");
  else
    webSocket.broadcastTXT("g2statusOK");
  char cmd[16];
  printfcomma(cmd, counter);
  webSocket.broadcastTXT(cmd);
}
