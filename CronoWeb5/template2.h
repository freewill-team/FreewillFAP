const char KO_HTML[] PROGMEM = R"=====(
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>CronoWeb</title>

<script>
  var connection1 = new WebSocket('ws://192.168.1.115:81/', ['arduino']);
  var connection2 = new WebSocket('ws://192.168.1.118:81/', ['arduino']);
  var starttime = null;
  var timer1 = null;
  var timer2 = null;
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
    /*if(now.getMilliseconds()>=starttime.getMilliseconds())
      bio.innerHTML = (now.getSeconds()-starttime.getSeconds())+'.'+(now.getMilliseconds()-starttime.getMilliseconds());
    else
      bio.innerHTML = (now.getSeconds()-starttime.getSeconds())+'.'+(1000-starttime.getMilliseconds()+now.getMilliseconds());*/
    //console.log('Into showTime()');
  }

  function showCrono1Time(){
    showTime('crono1');
  }

  function showCrono2Time(){
    showTime('crono2');
  }
  
  function resetTime(crono){
    var bio = document.getElementById(crono);
    bio.innerHTML = '0.000';
    starttime = null;
    //console.log('Into resetTime()');
  }
  
  function showGateStatus(element, state){
    if(state.includes('g1KO'))
      detener('crono1', timer1);
    else if(state.includes('g2KO'))
      detener('crono2', timer2);
    var gate = document.getElementById(element);
    if(state.includes('OK'))
      gate.style.background = 'orange';
    else if(state.includes('KO'))
      gate.style.background = 'red';
  }

  function millis(){
    var now = new Date();
    return now.getTime();
  }
  
  function empezar(){
    if(timer1==null && timer2==null && starttime==null){
      beep(100, 800, 1000); // 1,5 sec beep
      setTimeout(function(){
        beep(100, 1200, 500); // 1 sec beep
        }, 1500);
      setTimeout(function(){
        starttime = millis();
        beep(100, 3000, 300); // 300 msec beep
        beep(100, 3000, 300); // 300 msec beep
        timer1 = window.setInterval(showCrono1Time, 50);
        timer2 = window.setInterval(showCrono2Time, 50);
        console.log('Startime0 = '+starttime);
        }, 2600+Math.random()*300);
    }
  }
  
  function detener(crono, timer){
    if(timer!=null){
      if(crono.includes('crono1')){
        clearInterval(timer1);
        showTime(crono);
        timer1 = null;
      }
      else{
        clearInterval(timer2);
        showTime(crono);
        timer2 = null;
      }
      beep(100, 3000, 300); // 300 msec beep
      console.log('Detenido timer para '+crono);
    }
    console.log('Startime1 = '+starttime);
  }

  function detenerAll(){
    console.log('Into detenerAll()');
    if(timer1==null && timer2==null){
      resetTime('crono1');
      resetTime('crono2');
      console.log('All reset');
    }
    else{
      if(timer1!=null)
        detener('crono1', timer1);
      if(timer2!=null) 
        detener('crono2', timer2);
    }
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
      console.log('WebSocket1: ', e.data);
      showGateStatus('g1status', e.data);
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
      console.log('WebSocket2: ', e.data);
      showGateStatus('g2status', e.data);
    };
    connection2.onclose = function(){
      console.log('WebSocket connection closed');
      showGateStatus('g2status', 'blue');
    };
  }
  
	</script>
	
	<style>
	.crono_wrapper {text-decoration: none; font-size: 28px; margin: 2px; text-align:center; width:100%; min-width: 300px;}
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
 
	<div class="crono_wrapper">
		<h1 id='crono1'>0.000</h1>
    <h1 id='crono2'>0.000</h1>
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

</body>
</html>
)=====";
