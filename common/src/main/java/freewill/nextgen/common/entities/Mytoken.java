package freewill.nextgen.common.entities;

import java.util.Date;

/** 
 * File:   Mytoken.java
 * Date:   03/10/2017
 * Author: Benito Vela
 * Refs:   Used in AuthServiceOauth2
 * 
**/

public class Mytoken {

	private String access_token="";		// The access token
	private String token_type ="";		// Token type
	private String refresh_token="";	// Token for refresh the access token
	private long expires_in=0;			// expires in milliseconds
	private String scope="";			// token scope
	private Date token_date = null; 	// Date when the token was granted
	private String serviceId = "";		// FullId of current service "site:server:service"
	private String serverUrl = "";		// Server and port for current service "server:port"
	
	public Mytoken(){
		// Void
	}
	
	public String toString(){
		return access_token +"\t"+
			token_type +"\t"+
			refresh_token +"\t"+
			expires_in +"\t"+
			scope +"\t"+
			token_date +"\t"+
			serviceId +"\t"+
			serverUrl;
	}
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
		setToken_date(new Date());
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public long getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}

	public boolean needsRenovation() {
		// Indicates whether the token needs to be renewed or not
		if(token_date==null)
			return true;
		
		Long now = new Date().getTime();
		// Calculates expiration date/time in milliseconds - 10 minutes before token expiration
		Long expdate = token_date.getTime() + (this.expires_in - 60*10L)*1000L;
		
		/*System.out.println("Token date = " + token_date.getTime());
		System.out.println("Expires in = " + this.expires_in*1000L);
		System.out.println("Expir.date = " + expdate);
		System.out.println("Now        = " + now);*/
		
		if(now>expdate){
			System.out.println("RENEW NOW !!!");
			return true;
		}
		
		return false;
	}

	public Date getToken_date() {
		return token_date;
	}

	public void setToken_date(Date token_date) {
		this.token_date = token_date;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
}
