const char KO_HTML[] PROGMEM = R"=====(
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>CronoWeb (KO System)</title>

<script>
  var connection1 = '192.168.43.115';
  var connection2 = '192.168.43.118';
  var worker1 = null;
  var worker2 = null;
  var starttime = null;
  var audioContext = AudioContext && new AudioContext();
  
  function beep(amp, freq, ms){//amp:0..100, freq in Hz, ms
    if (!audioContext) return;
    audioContext.resume();
    var osc = audioContext.createOscillator();
    var gain = audioContext.createGain();
    osc.connect(gain);
    osc.value = freq;
    gain.connect(audioContext.destination);
    gain.gain.value = amp/100;
    osc.start(audioContext.currentTime);
    osc.stop(audioContext.currentTime+ms/1000);
  }
  
  function showTime(crono, now){
    var bio = document.getElementById(crono);
    var n = now / 1000;
    bio.innerHTML = parseFloat(n).toFixed(3);
    //console.log('Into showTime()');
  }
  
  function showGateStatus(element, state){
    //console.log('Into showGateStatus() '+state+' '+element);
    var gate = document.getElementById(element);
    if(state.includes('OK'))
      gate.style.background = 'orange';
    else if(state.includes('KO'))
      gate.style.background = 'red';
    else if(state.includes('blue'))
      gate.style.background = 'blue';
    else if(state.includes('beep'))
      beep(100, 3000, 300); // 300 msec beep
  }

  function millis(){
    var now = new Date();
    return now.getTime();
  }
  
  function empezar(){
    console.log('Into empezar() '+starttime);
    if(worker1!=null && worker2!=null && starttime==null){
      beep(100, 800, 1000); // 1,5 sec beep
      setTimeout(function(){
        beep(100, 1200, 500); // 1 sec beep
        }, 1500);
      setTimeout(function(){
        beep(100, 3000, 300); // 300 msec beep
        beep(100, 3000, 300); // 300 msec beep
        starttime = millis();
        worker1.postMessage(starttime);
        worker2.postMessage(starttime);
        //console.log('Startime0 = '+starttime);
        }, 2600+Math.random()*300);
    }
  }

  function detenerAll(){
    console.log('Into detenerAll()');
    if(starttime!=null){
      if(worker1!=null)
        worker1.postMessage('stop');
      if(worker2!=null) 
        worker2.postMessage('stop');
      showTime('crono1', 0);
      showTime('crono2', 0);
      starttime=null;
    }
  }

  function start(){
    worker1 = new Worker("./worker.js");
    worker1.postMessage(connection1);
    worker1.onmessage = function(e){
      if(isNaN(e.data))
        showGateStatus('g1status', e.data);
      else
        showTime('crono1', e.data);
    }
    
    worker2 = new Worker("./worker.js");
    worker2.postMessage(connection2);
    worker2.onmessage = function(e){
      if(isNaN(e.data))
        showGateStatus('g2status', e.data);
      else
        showTime('crono2', e.data);
    }
  }
  
	</script>
	
	<style>
	.crono_wrapper {text-decoration: none; font-size: 28px; margin: 2px; text-align:center; width:100%; min-width: 150px;}
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

  <h3>CronoWeb (KO System)</h3>
  <hr>
  <table>
    <tr>
      <td><div class="crono_wrapper"><h1 id='crono1'>0.000</h1></div></td>
      <td><div class="crono_wrapper"><h1 id='crono2'>0.000</h1></div></td> 
    </tr>
  </table>
	
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
  <a href="./">Go to Time Trial</a>

</body>
</html>
)=====";
