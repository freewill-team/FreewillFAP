package freewill.nextgen.authentication;

import java.io.Serializable;

import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;

/**
 * Simple interface for authentication and authorization checks.
 */
public interface AccessControl extends Serializable {

    public boolean signIn(String username, String password, String console);

    public boolean isUserSignedIn();

    public boolean isUserInRole(UserRoleEnum role);

    public String getPrincipalName();
    
    public String getUserLogin();
    
    public String getLocale();
    
    public UserEntity getUserEntity();

	public String getMD5Digest(String str);

	public String getConsole();

	public String getTokenKey();
    
}
