const char WORKER_JS[] PROGMEM = R"=====(

var connection = null;
var timer = null;
var starttime = 0;

function init(ipaddr){
  //connection = new WebSocket('ws://' + location.hostname + ':81/', ['arduino']);
  connection = new WebSocket('ws://'+ipaddr+':81/', ['arduino']);
  
  connection.onopen = function(){
    console.log('Connect webSocket ' + ipaddr);
    connection.send('stop');
  };
  
  connection.onerror = function(error){
    console.log('WebSocket Error ', ipaddr, error);
    postMessage('blue');
  };
  
  connection.onmessage = function(e){
    if(e.data.includes('KO') && timer!=null){
      clearInterval(timer);
      timer = null;
      postMessage('beep');
    }
    // Actualizaremos estado Gates o Timer
    postMessage(e.data);
    //console.log('WebSocket ', ipaddr, e.data);
  };
  
  connection.onclose = function(){
    console.log('WebSocket connection closed ', ipaddr);
    postMessage('blue');
  };
}

self.onmessage = function(e){
  if(e.data.includes('stop')){
    if(timer!=null){
      clearInterval(timer);
      timer = null;
    }
    connection.send(e.data);
  }
  else if(e.data.includes('start')){
    if(timer==null){
      starttime = new Date();
      timer = setInterval(showCronoTime, 50);
      connection.send(e.data);
    }
  }
  else
    init(e.data);
}

function showCronoTime(){
  var now = new Date();
  var n = (now - starttime);
  postMessage(n); 
}

)=====";
