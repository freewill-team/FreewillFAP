package freewill.nextgen.common.bltclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.Utils.*;
import freewill.nextgen.common.entities.Mytoken;
import freewill.nextgen.common.entities.ServiceEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/**
 * Back-end service for retrieving and updating data from a remote service.
 * Implemented as a Singleton, so every process within the same JVM 
 * will share the same instance.
 * 
 * Specific implementation with Rest services.
 * 
 */

@SuppressWarnings({ "serial", "deprecation" })
public class BltClientRest extends BltClient {
	
	// Internal variables
    private static BltClientRest INSTANCE = null;			// Instance to itself
    private MonitoredProcess parentService = null;			// Parent Service/Process Class
    private RestTemplate restTemplate = null;				// Used for HTTPS calls
    private String P12PASSWORD = "sirenados";				// KeyStore password
    private String P12CERTFILE = "c:/keystore.p12";			// KeyStore Certificate file
    private String BLTPREFIX = "https://";					// Prefix for service URL
    // Deprecated private String BLTSERVER = "localhost:8445";			// BLT server for service URL
    private String RESETPWD = "/UserEntity/resetPassword/"; // BTL suffix for user password reset
    private String BLTTOKEN = "?access_token="; 			// BTL suffix for regular requests 
    private String BLTGRANT = "/oauth/token?grant_type=password&username=%s&password=%s";
    														// BLT suffix for access token 
    private static HashMap<String,Mytoken> tokenList = 		// Contains a HashMap with registered
			new HashMap<String,Mytoken>(); 					// tokens for requests authorization
    														// The key is "username:password"
    
    public synchronized static BltClientRest getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BltClientRest();
        }
        return INSTANCE;
    }
    
	private BltClientRest() {
		// initializeService() needs to be called before first use of this service
		// already done in MonitoredProcess
	    echo("Creating BltClientRest instance");
    }
    
	 @Override
	 public void initializeService(MonitoredProcess service){
	    parentService = service;
	    try {
	    	// It reads configuration properties first
	    	if(parentService!=null){
	    		//token = null;
	    		P12PASSWORD = parentService.readConfigPropString("P12PASSWORD", "sirenados");
	    		P12CERTFILE = parentService.readConfigPropString("P12CERTFILE", "c:/keystore.p12");
	    		// Deprecated, now it uses auto-discovery:
	    		// BLTSERVER = parentService.readConfigPropString("BLTSERVER", "localhost:8445"); 
		    	
	    		// It creates distributed cache
		    	KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			    keyStore.load(new FileInputStream(new File(P12CERTFILE)), P12PASSWORD.toCharArray());
			
			    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
			            new SSLContextBuilder()
			                   .loadTrustMaterial(null, new TrustSelfSignedStrategy())
			                   .loadKeyMaterial(keyStore, P12PASSWORD.toCharArray())
			                   .build(),
			            NoopHostnameVerifier.INSTANCE);
			
			    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
			
			    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			    restTemplate = new RestTemplate(requestFactory);
		    
	    	}
	    } 
	    catch (Exception e) {
			error(e.getMessage());
			restTemplate = null;
			throw new IllegalArgumentException("Fail to invoke BltClientRest manager");
		}
	}
	
	// Debug auxiliary functions
	    
	private void echo(String message){
		if(parentService!=null)
			parentService.getLogger().debug(message);
		else
			System.out.println(message);
	}
		
	private void error(String message){
		if(parentService!=null)
			parentService.getLogger().error(message);
		else
			System.out.println(message);
	}
	
    // Functions for generic entities
    
    @Override
	public <T> List<T> getEntities(Class<T> myentity, String key) throws Exception {
 		try{
 			Mytoken token = findToken(key);
 			if(token==null)
 				throw new IllegalArgumentException("Fail into getEntities - Missing token");
 			
 			//String url = BLTPREFIX+BLTSERVER+"/"+myentity.getSimpleName()+"/getlist"+BLTTOKEN+token.getAccess_token();
 			String url = BLTPREFIX+token.getServerUrl()+"/"+myentity.getSimpleName()+"/getlist"+BLTTOKEN+token.getAccess_token();
 			
 			String json  = restTemplate.getForObject(url, String.class);
 			List<T> list = jsonToObjectList(json, myentity);
	    	return list;
 		}
		catch (HttpStatusCodeException e) {
			String msg = getErrorMessage(e);
			throw new Exception(msg);
	    }
		catch (Exception e)
		{
			e.printStackTrace();
			error(e.getMessage());
			throw new IllegalArgumentException("Fail retrieving list of "+myentity.getSimpleName());
		}
 	}
 	
	private <T> List<T> jsonToObjectList(String json, Class<T> tClass) throws Exception {
        ObjectMapper MAPPER = new ObjectMapper();
        List<T> ts = MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, tClass));
        return ts;
    }

	@Override
	public <T> Object createEntity(Object rec, Class<T> myentity, String key) throws Exception {
		try{
			Mytoken token = findToken(key);
			if(token==null)
				throw new IllegalArgumentException("Fail into createEntities - Missing token");
			
			HttpEntity<Object> request = new HttpEntity<>(rec);
			//String url = BLTPREFIX+BLTSERVER+"/"+myentity.getSimpleName()+"/create"+BLTTOKEN+token.getAccess_token();
			String url = BLTPREFIX+token.getServerUrl()+"/"+myentity.getSimpleName()+"/create"+BLTTOKEN+token.getAccess_token();
			
			Object result = restTemplate.postForObject(url, request, myentity);
			return result;
		}
		catch (HttpStatusCodeException e) {
			String msg = getErrorMessage(e);
			throw new Exception(msg);
	    }
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail creating new record in "+myentity.getSimpleName()+ ">"+rec);
		}
	}

	private String getErrorMessage(HttpStatusCodeException e)
			throws IOException, JsonParseException, JsonMappingException {
		String msg = e.getMessage();
		String json = e.getResponseBodyAsString();
		ObjectNode node = new ObjectMapper().readValue(json, ObjectNode.class);
		if(node.has("message")) {
			msg = node.get("message").textValue();
		}    
		error(msg);
		return msg;
	}

	@Override
	public <T> Object updateEntity(Object rec, Class<T> myentity, String key) throws Exception {
		try{
			Mytoken token = findToken(key);
			if(token==null)
				throw new IllegalArgumentException("Fail into updateEntities - Missing token");
			
			HttpEntity<Object> request = new HttpEntity<>(rec);
			//String url = BLTPREFIX+BLTSERVER+"/"+myentity.getSimpleName()+"/update"+BLTTOKEN+token.getAccess_token();
			String url = BLTPREFIX+token.getServerUrl()+"/"+myentity.getSimpleName()+"/update"+BLTTOKEN+token.getAccess_token();
			
			Object result = restTemplate.postForObject(url, request, myentity);
			return result;
		}
		catch (HttpStatusCodeException e) {
			String msg = getErrorMessage(e);
			throw new Exception(msg);
	    }
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail updating new record in "+myentity.getSimpleName()+ ">"+rec);
		}
	}

	@Override
	public <T> boolean deleteEntity(String recId, Class<T> myentity, String key) throws Exception {
		try{
			Mytoken token = findToken(key);
			if(token==null)
				throw new IllegalArgumentException("Fail into deleteEntities - Missing token");
			
			//String url = BLTPREFIX+BLTSERVER+"/"+myentity.getSimpleName()+"/delete/"+recId+BLTTOKEN+token.getAccess_token();	
			String url = BLTPREFIX+token.getServerUrl()+"/"+myentity.getSimpleName()+"/delete/"+recId+BLTTOKEN+token.getAccess_token();	
			
			boolean result = restTemplate.getForObject(url, Boolean.class);
			return result;
		}
		catch (HttpStatusCodeException e) {
			String msg = getErrorMessage(e);
			throw new Exception(msg);
	    }
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail deleting record in "+myentity.getSimpleName()+ ">"+recId);
		}
	}

	@Override
	public <T> Object getEntityById(String recId, Class<T> myentity, String key) throws Exception {
		try{
			Mytoken token = findToken(key);
			if(token==null)
				throw new IllegalArgumentException("Fail into getEntityById - Missing token");
			
			//String url = BLTPREFIX+BLTSERVER+"/"+myentity.getSimpleName()+"/get/"+recId+BLTTOKEN+token.getAccess_token();
			String url = BLTPREFIX+token.getServerUrl()+"/"+myentity.getSimpleName()+"/get/"+recId+BLTTOKEN+token.getAccess_token();
			
			Object result = restTemplate.getForObject(url, myentity);
			return result;
		}
		catch (HttpStatusCodeException e) {
			String msg = getErrorMessage(e);
			throw new Exception(msg);
	    }
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail searching record in "+myentity.getSimpleName()+ ">"+recId);
		}
	}
	
	@Override
	public <T> Boolean executeCommand(String command, Object rec, Class<T> myentity, String key) throws Exception {
		try{
			if( command == null || command.equals(""))
				throw new IllegalArgumentException("Fail into executeCommand - Missing command");
			
			Mytoken token = findToken(key);
			if(token==null)
				throw new IllegalArgumentException("Fail into executeCommand - Missing token");
			
			HttpEntity<Object> request = new HttpEntity<>(rec);
			//String url = BLTPREFIX+BLTSERVER+"/"+myentity.getSimpleName()+"/"+command;
			String url = BLTPREFIX+token.getServerUrl()+"/"+myentity.getSimpleName()+"/"+command;
			if(url.contains("?"))
				url=url+"&access_token="+token.getAccess_token();
			else
				url=url+BLTTOKEN+token.getAccess_token();
			
			Boolean result = restTemplate.postForObject(url, request, Boolean.class); //myentity);
			return result;
		}
		catch (HttpStatusCodeException e) {
			String msg = getErrorMessage(e);
			throw new Exception(msg);
	    }
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail executing command " +
					command + " for " + myentity.getSimpleName() + ">" + rec);
		}
	}
	
	@Override
	public <T> Object executeCommand(String command, Class<T> myentity, String key) throws Exception {
		try{
			if( command == null || command.equals(""))
				throw new IllegalArgumentException("Fail into executeCommand2 - Missing command");
			
			Mytoken token = findToken(key);
			if(token==null)
				throw new IllegalArgumentException("Fail into executeCommand2 - Missing token");
			
			//HttpEntity<Object> request = new HttpEntity<>(rec);
			//String url = BLTPREFIX+BLTSERVER+"/"+myentity.getSimpleName()+"/"+command;
			String url = BLTPREFIX+token.getServerUrl()+"/"+myentity.getSimpleName()+"/"+command;
			if(url.contains("?"))
				url=url+"&access_token="+token.getAccess_token();
			else
				url=url+BLTTOKEN+token.getAccess_token();
			
			//Object result = restTemplate.postForObject(url, request, myentity);
			Object result = restTemplate.getForObject(url, myentity);
			return result;
		}
		catch (HttpStatusCodeException e) {
			String msg = getErrorMessage(e);
			throw new Exception(msg);
	    }
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail executing command2 " +
					command + " for " + myentity.getSimpleName());
		}
	}

	public <T> List<T> executeQuery(String querycommand, Class<T> myentity, String key) throws Exception {
		try{
			if( querycommand == null || querycommand.equals(""))
				throw new IllegalArgumentException("Fail into executeQuery - Missing query command");
			
			Mytoken token = findToken(key);
			if(token==null)
				throw new IllegalArgumentException("Fail into executeQuery - Missing token");
			
			//String url = BLTPREFIX+BLTSERVER+"/"+myentity.getSimpleName()+"/"+querycommand;
			String url = BLTPREFIX+token.getServerUrl()+"/"+myentity.getSimpleName()+"/"+querycommand;
			if(url.contains("?"))
				url=url+"&access_token="+token.getAccess_token();
			else
				url=url+BLTTOKEN+token.getAccess_token();
			
 			String json = restTemplate.getForObject(url, String.class);
 			List<T> list = jsonToObjectList(json, myentity);
	    	return list;
		}
		catch (HttpStatusCodeException e) {
			String msg = getErrorMessage(e);
			throw new Exception(msg);
	    }
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail in executeQuery retrieving list of "+myentity.getSimpleName());
		}
	}
	
	public <T> List<T> executeQuery(String querycommand, Object rec, Class<T> myentity, String key) throws Exception {
		try{
			if( querycommand == null || querycommand.equals(""))
				throw new IllegalArgumentException("Fail into executeQuery2 - Missing query command");
			
			Mytoken token = findToken(key);
			if(token==null)
				throw new IllegalArgumentException("Fail into executeQuery2 - Missing token");
			
			HttpEntity<Object> request = new HttpEntity<>(rec);
			//String url = BLTPREFIX+BLTSERVER+"/"+myentity.getSimpleName()+"/"+querycommand;
			String url = BLTPREFIX+token.getServerUrl()+"/"+myentity.getSimpleName()+"/"+querycommand;
			if(url.contains("?"))
				url=url+"&access_token="+token.getAccess_token();
			else
				url=url+BLTTOKEN+token.getAccess_token();
 			
 			String json  = restTemplate.postForObject(url, request, String.class);
 			List<T> list = jsonToObjectList(json, myentity);
	    	return list;
		}
		catch (HttpStatusCodeException e) {
			String msg = getErrorMessage(e);
			throw new Exception(msg);
	    }
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail in executeQuery2 retrieving list of "+myentity.getSimpleName());
		}
	}
	
	// Functions for Authentication/Security 
	
	@Override
	public String askForToken(String user, String password, String servicetype) {
		try{
			// Builds key credentials
			String key = user+":"+password+":"+servicetype;
			
			// Ask for an available service
			ServiceEntity service = getAvailableService(servicetype); // ADMSservice.RTS.toString());
			String serviceUrl = service.getServer()+":"+service.getPort();
					
			// get access token
			//String plainClientCredentials=user+":"+password;
	        String plainClientCredentials="foo:foo";
			String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
			Mytoken token = new Mytoken();
	        //String url = String.format(BLTPREFIX+BLTSERVER+BLTGRANT, user, password);
			String url = String.format(BLTPREFIX+serviceUrl+BLTGRANT, user, password);
	        
	        HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.add("Authorization", "Basic " + base64ClientCredentials);
			HttpEntity<String> request = new HttpEntity<String>(headers);
	        
			ResponseEntity<Mytoken> result = restTemplate.postForEntity(url, request, Mytoken.class);
	        if(result.getStatusCode()==HttpStatus.OK){
	        	token = (Mytoken) result.getBody();
	        	token.setServerUrl(serviceUrl);
	        	token.setServiceId(service.getID());
	        	echo("Got Token = " + token.getAccess_token());
	        	tokenList.put(key, token);
	        	return key;
	        }
	        else {
	        	throw new Exception("Error Retrieving Security Token");
	        }
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail retrieving security token");
		}
	}
	
	private String renewToken(Mytoken token, String key){
		try{
			if(token==null)
				throw new IllegalArgumentException("Fail into renewToken - Missing token");
			
			// renew access token
	        //String url = BLTPREFIX+BLTSERVER+"/oauth/token?grant_type=refresh_token&refresh_token="+token.getRefresh_token();
			String url = BLTPREFIX+token.getServerUrl()+"/oauth/token?grant_type=refresh_token&refresh_token="+token.getRefresh_token();
	        
	        //String plainClientCredentials=user+":"+password;
			//String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			//headers.add("Authorization", "Basic " + base64ClientCredentials);
			HttpEntity<String> request = new HttpEntity<String>(headers);
	        
			ResponseEntity<Mytoken> result = restTemplate.postForEntity(url, request, Mytoken.class);
	        if(result.getStatusCode()==HttpStatus.OK){
	        	Mytoken newtoken = (Mytoken) result.getBody();
	        	newtoken.setServerUrl(token.getServerUrl());
	        	newtoken.setServiceId(token.getServiceId());
	        	echo("Got New Token = " + token.getAccess_token());
	        	tokenList.put(key, newtoken);
	        }
	        else {
	        	throw new Exception("Error renewing Security Token");
	        }
	        return key;
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail renewing security token");
		}
	}
	
	private Mytoken findToken(String key){
		try{
			Mytoken token = tokenList.get(key);
			if(token==null){
				// Not found, so ask for a token
				this.askForNewToken(key);
				return tokenList.get(key);
			}
			if(token.needsRenovation()){
				// Token close to expiration, so renew it
				this.renewToken(token, key);
				return tokenList.get(key);
			}
			//this.forDebug(); // remove in official release
			return token;
		}
		catch(Exception e){
			error("Error - Cannot find or renew a token for "+key);
			tokenList.put(key, null);
		}
		return null;
	}
	
	private void askForNewToken(String key) {
		String[] credentials = key.split(":");
		String user = credentials[0];
		String pass = credentials[1];
		String service = credentials[2];
		askForToken(user, pass, service);
	}
	
	public String waitUntilToken(String user, String password, String servicetype){
		// Use with careful as it uses an infinite loop
		String key="";
		while(true){
			try{
				Thread.sleep(6000);
				key = this.askForToken(user, password, servicetype);
				echo("Got token for key "+key);
				return key;
			}
			catch(Exception e){
				echo("No token for key '"+user+"'. It will be retried.");
			}
		}
	}
	
	@Override
	public boolean resetPassword(String user, String servicetype) {
		try{
			// Ask for an available service
			ServiceEntity service = getAvailableService(servicetype); // ADMSservice.RTS.toString());
			String serviceUrl = service.getServer()+":"+service.getPort();
			String url = BLTPREFIX+serviceUrl+RESETPWD+user;
			
			boolean result = restTemplate.getForObject(url, Boolean.class);
			return result;
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail executing resetPassword for " +user);
		}
	}
	
	// Functions for Service discovery
	
	private List<ServiceEntity> getServices(String servicetype){
		List<ServiceEntity> res = new ArrayList<ServiceEntity>();
		List<ServiceEntity> list = RtdbDataService.get().getEntities(ServiceEntity.class);
		for(ServiceEntity rec:list){
			if(servicetype.equals(rec.getName())){
				res.add(rec);
			}
		}
		return res;
	}
	
	private ServiceEntity getAvailableService(String servicetype){
		List<ServiceEntity> list = this.getServices(servicetype);
		// First try to find a service in GOOD status
		for(ServiceEntity rec:list){
			if(rec.getStatus()==ServiceStatusEnum.GOOD){
				return rec;
			}
		}
		// Second, return any service randomly
		Random r = new Random();
		int i = r.nextInt(list.size());
		return list.get(i);
	}
	
	/*private void forDebug(){
		echo("Tokens =");
		for(Mytoken rec:tokenList.values()){
			echo(rec.toString());
		}
	}*/
	
	public Collection<Mytoken> getListOfTokens(){
		return (Collection<Mytoken>) tokenList.values();
	}
	
}
