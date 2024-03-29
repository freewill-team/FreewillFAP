const char INDEX_HTML[] PROGMEM = R"=====(
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>CronoWeb (Time Trial)</title>

<script>
  var connection1 = new WebSocket('ws://192.168.4.115:81/', ['arduino']);
  var connection2 = new WebSocket('ws://192.168.4.118:81/', ['arduino']);
  var starttime = null;
  var timer = null;
  var audioContext = AudioContext && new AudioContext();
  
  function beep(amp, freq, ms){//amp:0..100, freq in Hz, ms
    if (!audioContext) return;
    var osc = audioContext.createOscillator();
    var gain = audioContext.createGain();
    osc.connect(gain);
    osc.value = freq;
    gain.connect(audioContext.destination);
    gain.gain.value = amp/100;
    osc.start(audioContext.currentTime);
    osc.stop(audioContext.currentTime+ms/1000);
  }
  
  function showTime(crono){
    var bio = document.getElementById(crono);
    var now = millis();
    var n = (now - starttime) / 1000;
    bio.innerHTML = parseFloat(n).toFixed(3);
    //console.log('Into showTime()');
  }

  function showCronoTime(){
    showTime('crono');
  }

  function resetTime(crono){
    var bio = document.getElementById(crono);
    bio.innerHTML = '0.000';
    starttime = null;
    console.log('Into resetTime()');
  }
  
  function showGateStatus(element, state){
    if(state.includes('g1KO'))
      empezar();
    else if(state.includes('g2KO'))
      detener('crono');
    var gate = document.getElementById(element);
    if(state.includes('OK'))
      gate.style.background = 'orange';
    else if(state.includes('KO'))
      gate.style.background = 'red';
    else if(state.includes('blue'))
      gate.style.background = 'blue'; 
  }

  function millis(){
    var now = new Date();
    return now.getTime();
  }
  
  function empezar(){
    if(timer==null && starttime==null){
      starttime = millis();
      beep(100, 3000, 300); // 300 msec beep
      timer = window.setInterval(showCronoTime, 50);
      //console.log('Startime0 = '+starttime);
    }
  }
	
	function detener(crono){
    if(timer!=null){
      clearInterval(timer);
      showTime(crono);
      timer = null;
      beep(100, 3000, 300); // 300 msec beep
    }
    console.log('Startime1 = '+starttime);
	}

  function detenerAll(){
    if(timer==null){
      resetTime('crono');
      console.log('All reset');
    }
    else
      detener('crono');
  }

  function start(){
    //connection = new WebSocket('ws://' + location.hostname + ':81/', ['arduino']);
    connection1.onopen = function(){
      console.log('Connect ' + new Date());
      var but = document.getElementById('butStart');
      but.style.display='block';
    };
    connection1.onerror = function(error){
      console.log('WebSocket1 Error ', error);
      showGateStatus('g1status', 'blue');
    };
    connection1.onmessage = function(e){
      showGateStatus('g1status', e.data);
      console.log('WebSocket1: ', e.data);
    };
    connection1.onclose = function(){
      console.log('WebSocket1 connection closed');
      showGateStatus('g1status', 'blue');
    };

    connection2.onopen = function(){
      console.log('Connect ' + new Date());
      var but = document.getElementById('butStart');
      but.style.display='block';
    };
    connection2.onerror = function(error){
      console.log('WebSocket2 Error ', error);
      showGateStatus('g2status', 'blue');
    };
    connection2.onmessage = function(e){
      showGateStatus('g2status', e.data);
      console.log('WebSocket2: ', e.data);
    };
    connection2.onclose = function(){
      console.log('WebSocket2 connection closed');
      showGateStatus('g2status', 'blue');
    };
  }
  
	</script>
	
	<style>
	.crono_wrapper {text-decoration: none; font-size: 32px; margin: 2px; text-align:center; width:100%; min-width: 300px;}
	html {font-family: Helvetica; display: inline-block; margin: 0px auto; text-align: center;}
  .button { background-color: #195B6A; border: none; color: white; padding: 16px 30px; width:100%;
    text-decoration: none; font-size: 22px; margin: 2px; cursor: pointer;}
  .button2 { display: none; background-color: #77878A;} }
  .gate_wrapper { padding:2px; marging:2px; width:100%; background-color: blue; border-radius: 6px 6px 6px 6px;}
  hr { width:100%; height:3px; background: #000 }
  table { width:100%; text-decoration: none; font-size: 16px; text-align:center; }
  th, td {padding: 2px 4px 2px 2px; }
	</style>
            
</head>
<body onload="javascript:start();">

  <h3>CronoWeb (Time Trial)</h3>
  <hr>
	<div class="crono_wrapper">
		<h1 id='crono'>0.000</h1>
	</div>
	<p><button class="button" onclick="detenerAll();">STOP</button></p>
	<p><button id="butStart" class="button" onclick="empezar();">START</button></p>
	<hr>	
	<table>
    <tr>
      <th>Gate 1</th>
      <th>Gate 2</th>
    </tr>
    <tr>
      <td><div id='g1status' class="gate_wrapper">.</div></td>
      <td><div id='g2status' class="gate_wrapper">.</div></td> 
    </tr>
  </table>
  <hr>
  <a href="./ko">Go to KO System</a>

</body>
</html>
)=====";
