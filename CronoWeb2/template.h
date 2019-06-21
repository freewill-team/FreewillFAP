const char INDEX_HTML[] PROGMEM = R"=====(
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>CronoWeb</title>

<script>
	var timer = 0;
  var canSaveTime = false;
  var connection = new WebSocket('ws://' + location.hostname + ':81/', ['arduino']);
	
	var request = new XMLHttpRequest(); 
	request.onreadystatechange = function() {
	  if(request.readyState == 4) {
	    if(request.status == 200){
        var bio = document.getElementById('crono');
	      bio.innerHTML = request.responseText;
	    }
      else if(request.status == 201){
        var obj = document.getElementById('g1status');
        obj.style.background = request.responseText;
      }
      else if(request.status == 202){
        var obj = document.getElementById('g2status');
        obj.style.background = request.responseText;
      }
	  }
	}
  
  function salvar(){
    if(canSaveTime){
      request.open('Get', './savetime');
      request.send();
    }
  }
  
	function empezar(){
		//request.open('Get', './start');
		//request.send();
    connection.send("start");
	}
	
	function detener(){
		//request.open('Get', './stop');
		//request.send();
    connection.send("stop");
	}
	
	function lecturaTiempo(){
    request.open('Get', './time');
    request.send();
    var but = document.getElementById('butStart');
    but.style.display='block';
	}
	//setInterval(lecturaTiempo, 433);

  function lecturaG1Status(){
    request.open('Get', './g1status');
    request.send();
  }
  //setInterval(lecturaG1Status, 1045);
  
  function lecturaG2Status(){
    request.open('Get', './g2status');
    request.send();
  }
  //setInterval(lecturaG2Status, 955);

  function showWeb(){
    var bio = document.getElementById('webDiv');
    bio.style.height="560px";
    var but2 = document.getElementById('butSave');
    but2.style.display='block';
    canSaveTime = true;
  }

  function start(){
    //connection = new WebSocket('ws://' + location.hostname + ':81/', ['arduino']);
    connection.onopen = function(){
      console.log('Connect ' + new Date());
      var but = document.getElementById('butStart');
      but.style.display='block';
    };
    connection.onerror = function(error){
      console.log('WebSocket Error ', error);
    };
    connection.onmessage = function(e){
      //console.log('Server: ', e.data);
      var bio = document.getElementById('crono');
      var g1s = document.getElementById('g1status');
      var g2s = document.getElementById('g2status');
      if(e.data.includes('g1statusOK'))
        g1s.style.background = 'orange';
      else if(e.data.includes('g1statusKO'))
        g1s.style.background = 'red';
      else if(e.data.includes('g2statusOK'))
        g2s.style.background = 'orange';
      else if(e.data.includes('g2statusKO'))
        g2s.style.background = 'red';
      else 
        bio.innerHTML = e.data;
    };
    connection.onclose = function(){
      console.log('WebSocket connection closed');
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
 
	<div class="crono_wrapper">
		<h1 id='crono'>0.000</h1>
	</div>
	<p><button class="button" onclick="detener();">STOP</button></p>
	<p><button id="butStart" class="button" onclick="empezar();">START</button></p>
  <p><button id="butSave" class="button button2" onclick="salvar();">SAVE TIME</button></p>
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
  <div id="web"> 
    <object id="webDiv" onload="showWeb();" type="text/html" data="http://137.74.195.144:8846/" width="100%" height="0px" style="overflow:auto;border:2px ridge blue">
    </object>
  </div>

</body>
</html>
)=====";
