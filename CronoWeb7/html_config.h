const char CONFIG_HTML[] PROGMEM = R"=====(
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>CronoWeb (Config)</title>

<script>
  var connection1 = new WebSocket('ws://192.168.43.115:81/', ['arduino']);
  var connection2 = new WebSocket('ws://192.168.43.118:81/', ['arduino']);
  
  function setConfig(crono, val){
    var bio = document.getElementById(crono);
    bio.innerHTML = val;
    console.log('Into setConfig()');
  }

  function showGateStatus(element, state){
    var gate = document.getElementById(element);
    if(state.includes('OK'))
      gate.style.background = 'orange';
    else if(state.includes('KO'))
      gate.style.background = 'red';
    else if(state.includes('blue'))
      gate.style.background = 'blue'; 
  }

  function salvar(){
    /*if(connection1.readyState==1)
      connection1.send(slider.value);
    if(connection2.readyState==1)
      connection2.send(slider.value);*/
  }
  
  function start(){
    //connection = new WebSocket('ws://' + location.hostname + ':81/', ['arduino']);
    connection1.onopen = function(){
      console.log('Connect ' + new Date());
      //var but = document.getElementById('butSave');
      //but.style.display='block';
      connection1.send("config");
    };
    connection1.onerror = function(error){
      console.log('WebSocket1 Error ', error);
      showGateStatus('g1status', 'blue');
    };
    connection1.onmessage = function(e){
      console.log('WebSocket1: ', e.data);
      if(e.data.includes('g1'))
        showGateStatus('g1status', e.data);
      else
        setConfig('crono1', e.data);
    };
    connection1.onclose = function(){
      console.log('WebSocket1 connection closed');
      showGateStatus('g1status', 'blue');
    };

    connection2.onopen = function(){
      console.log('Connect ' + new Date());
      //var but = document.getElementById('butSave');
      //but.style.display='block';
      connection2.send("config");
    };
    connection2.onerror = function(error){
      console.log('WebSocket2 Error ', error);
      showGateStatus('g2status', 'blue');
    };
    connection2.onmessage = function(e){
      console.log('WebSocket2: ', e.data);
      if(e.data.includes('g2'))
        showGateStatus('g2status', e.data);
      else
        setConfig('crono2', e.data);
    };
    connection2.onclose = function(){
      console.log('WebSocket2 connection closed');
      showGateStatus('g2status', 'blue');
    };
    
  }
  
	</script>
	
	<style>
	.crono_wrapper {text-decoration: none; font-size: 32px; margin: 2px; text-align:center; width:100%; min-width: 150px;}
	html {font-family: Helvetica; display: inline-block; margin: 0px auto; text-align: center;}
  .button { background-color: #195B6A; border: none; color: white; padding: 16px 30px; width:100%;
    text-decoration: none; font-size: 22px; margin: 2px; cursor: pointer;}
  .button2 { display: none; background-color: #77878A;} }
  .gate_wrapper { padding:2px; marging:2px; width:100%; background-color: blue; border-radius: 6px 6px 6px 6px;}
  hr { width:100%; height:3px; background: #000 }
  table { width:100%; text-decoration: none; font-size: 16px; text-align:center; }
  th, td {padding: 2px 4px 2px 2px; }
  .slider {
    -webkit-appearance: none;
    width: 100%;
    height: 15px;
    border-radius: 5px;   
    background: #d3d3d3;
    outline: none;
    opacity: 0.7;
    -webkit-transition: .2s;
    transition: opacity .2s;
  }
  .slider::-webkit-slider-thumb {
    -webkit-appearance: none;
    appearance: none;
    width: 25px;
    height: 25px;
    border-radius: 50%; 
    background: #4CAF50;
    cursor: pointer;
  }
  .slider::-moz-range-thumb {
    width: 25px;
    height: 25px;
    border-radius: 50%;
    background: #4CAF50;
    cursor: pointer;
  }
	</style>
            
</head>
<body onload="javascript:start();">

  <h3>CronoWeb (Config)</h3>
	<!--<p><button id="butSave" class="button" onclick="salvar();">SAVE</button></p>-->
  <hr>  
  <table>
    <tr>
      <th>Gate 1</th>
      <th>Gate 2</th>
    </tr>
    <tr>
      <td><div id='crono1' class="gate_wrapper">.</div></td>
      <td><div id='crono2' class="gate_wrapper">.</div></td> 
    </tr>
    <tr>
      <td><div id='g1status' class="gate_wrapper">.</div></td>
      <td><div id='g2status' class="gate_wrapper">.</div></td> 
    </tr>
  </table>
	<hr>	

  <table>
    <tr>
      <td><a href="./">Go to Time Trial</a></td>
      <td><a href="./ko">Go to KO System</a></td> 
    </tr>
  </table>

</body>
</html>
)=====";
