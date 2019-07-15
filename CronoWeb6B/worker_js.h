const char WORKER_JS[] PROGMEM = R"=====(

var connection = null;
var timer = null;
var starttime = 0;

function showCronoTime(){
  var now = millis();
  var n = (now - starttime);
  postMessage(n); 
}

function detener(){
  if(timer!=null){
    showCronoTime();
    clearInterval(timer);
    timer = null;
    postMessage('beep');
  }
  else
    postMessage(0);
}

function init(ipaddr){
  //connection = new WebSocket('ws://' + location.hostname + ':81/', ['arduino']);
  connection = new WebSocket('ws://'+ipaddr+':81/', ['arduino']);
  
  connection.onopen = function(){
    console.log('Connect webSocket ' + ipaddr);
  };
  
  connection.onerror = function(error){
    console.log('WebSocket Error ', ipaddr, error);
    postMessage('blue');
  };
  
  connection.onmessage = function(e){
    if(e.data.includes('KO') && timer!=null)
      detener();
    else if(timer==null) // no actualizaremos estado Gates si el timer esta corriendo
      postMessage(e.data);
    //console.log('WebSocket ', ipaddr, e.data);
  };
  
  connection.onclose = function(){
    console.log('WebSocket connection closed ', ipaddr);
    postMessage('blue');
  };
}

self.onmessage = function(e){
  if(isNaN(e.data)){
    if(e.data.includes('stop'))
      detener();
    else
      init(e.data);
  }
  else{
    if(timer==null){
      starttime = e.data; // in milliseconds
      timer = setInterval(showCronoTime, 75);
    }
  }
}

function millis(){
  var now = new Date();
  return now.getTime();
}

)=====";
