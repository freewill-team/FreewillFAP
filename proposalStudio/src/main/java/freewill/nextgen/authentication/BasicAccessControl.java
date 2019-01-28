package freewill.nextgen.authentication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import freewill.nextgen.authentication.CurrentUser;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import freewill.nextgen.data.CompanyEntity;

/**
 * Implementation of {@link AccessControl}.
 */
@SuppressWarnings("serial")
public class BasicAccessControl implements AccessControl {
	
	private UserEntity currentUser = null; // keeps a copy of current logged user

    @Override
    public boolean signIn(String username, String password, String console) {
    	
        if (username == null || username.isEmpty() 
         || password == null || password.isEmpty()
         || console == null || console.isEmpty())
            return false;
        
        String digestedPassword = getMD5Digest(password);
        
        try{
	        // Checks whether the user is valid
	        System.out.println("Getting Token...");
	    	String tokenKey = BltClient.get().askForToken(username, digestedPassword, ADMSservice.RTS.toString());
	    	
	    	currentUser = new UserEntity();
	        currentUser = (UserEntity) BltClient.get().executeCommand(
	        		"login/"+username, UserEntity.class, tokenKey);
	        
	        // Injects successful login event
	        RtdbDataService.get().pushEvent(new EventEntity(
    				new Date(), 
    				String.format(AlarmDic.ALM0021.toString(), username, "ProposalStudio"),
    				username,
    				ADMSservice.HMI.toString(), // ParentPoint
    				"ProposalStudio", // PointType
    				AlarmDic.ALM0021.getSeverity(),
    				AlarmDic.ALM0021.getCategory(),
    				username,
    				console
    				));
	    
	        // User is valid, then save session data for later retrieval
	        CurrentUser.set(currentUser.getLoginname());
	        CurrentUser.setLocale(currentUser.getLanguage().toLocale());
	        CurrentUser.setUserEntity(currentUser);
	        CurrentUser.setConsole(console);
	        CurrentUser.setTokenKey(tokenKey);
	        
	        return true;
        }
        catch(Exception e){
        	System.out.println("Something went wrong during authentication.");
        	// Injects unsuccessful login event
    		RtdbDataService.get().pushEvent(new EventEntity(
    				new Date(), 
    				String.format(AlarmDic.ALM0022.toString(), username, "ProposalStudio"),
    				username,
    				ADMSservice.HMI.toString(), // ParentPoint
    				"ProposalStudio", // PointType
    				AlarmDic.ALM0022.getSeverity(),
    				AlarmDic.ALM0022.getCategory(),
    				username,
    				console
    				));
        	return false;
        }
    }

    @Override
    public boolean isUserSignedIn() {
        return !CurrentUser.get().isEmpty();
    }
    
    @Override
    public String getUserLogin() {
    	if(isUserSignedIn() && currentUser==null)
			currentUser = CurrentUser.getUserEntity();
    	if(currentUser==null)
    		return "";
        return currentUser.getLoginname();
    }

    @Override
    public boolean isUserInRole(UserRoleEnum role){
    	if(isUserSignedIn() && currentUser==null)
			currentUser = CurrentUser.getUserEntity();
    	if(currentUser==null)
    		return false;
    	
    	return currentUser.getRole().ordinal()<=role.ordinal();
    }

    @Override
    public String getPrincipalName() {
    	if(isUserSignedIn() && currentUser==null)
			currentUser = CurrentUser.getUserEntity();
        return CurrentUser.get();
    }

    @Override
	public String getLocale() {
		if(isUserSignedIn() && currentUser==null)
			currentUser = CurrentUser.getUserEntity();
		return 
			CurrentUser.getLocale();
	}
	
	@Override
	public String getConsole() {
		if(isUserSignedIn() && currentUser==null)
			currentUser = CurrentUser.getUserEntity();
		return 
			CurrentUser.getConsole();
	}
	
	@Override
	public String getTokenKey() {
		if(isUserSignedIn() && currentUser==null)
			currentUser = CurrentUser.getUserEntity();
		return 
			CurrentUser.getTokenKey();
	}

	@Override
    public UserEntity getUserEntity() {
    	if(isUserSignedIn() && currentUser==null)
			currentUser = CurrentUser.getUserEntity();
        return currentUser;
    }
	
	@Override
	public String getMD5Digest(String str) { 
		try { 
			byte[] buffer = str.getBytes(); 
			byte[] result = null; 
			StringBuffer buf = null; 
			MessageDigest md5 = MessageDigest.getInstance("MD5"); 
			// allocate room for the hash 
			result = new byte[md5.getDigestLength()]; 
			// calculate hash 
			md5.reset(); 
			md5.update(buffer); 
			result = md5.digest(); 
			// System.out.println(result); 
			// create hex string from the 16-byte hash 
			buf = new StringBuffer(result.length * 2); 
			for (int i = 0; i < result.length; i++) { 
				int intVal = result[i] & 0xff; 
				if (intVal < 0x10) { 
					buf.append("0"); 
				} 
				buf.append(Integer.toHexString(intVal).toUpperCase()); 
			} 
			return buf.toString();
		} 
		catch (NoSuchAlgorithmException e) { 
			System.err.println("Exception caught: " + e); 
			e.printStackTrace();
		} 
		return null;
	}

	@Override
	public CompanyEntity getCompany() {
		try{
			if(isUserSignedIn() && currentUser==null)
				currentUser = CurrentUser.getUserEntity();
			Long recId = currentUser.getCompany();
			CompanyEntity compRec = (CompanyEntity) BltClient.get().getEntityById(""+recId, 
					CompanyEntity.class,
					CurrentUser.getTokenKey());
			return compRec;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
}
