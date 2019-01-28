package freewill.nextgen.authentication;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;

import freewill.nextgen.common.entities.UserEntity;

/**
 * Class for retrieving and setting the name of the current user of the current
 * session (without using JAAS). All methods of this class require that a
 * {@link VaadinRequest} is bound to the current thread.
 * 
 * 
 * @see com.vaadin.server.VaadinService#getCurrentRequest()
 */
public final class CurrentUser {

    /**
     * The attribute key used to store the username in the session.
     */
    public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = 
    		CurrentUser.class.getCanonicalName();
    // Other properties for current logged user
    public static final String CURRENT_LOCALE_SESSION_ATTRIBUTE_KEY = 
    		CurrentUser.class.getCanonicalName()+"_LOCALE";
    public static final String CURRENT_ENTITY_SESSION_ATTRIBUTE_KEY = 
    		CurrentUser.class.getCanonicalName()+"_ENTITY";
    public static final String CURRENT_CONSOLE_SESSION_ATTRIBUTE_KEY = 
    		CurrentUser.class.getCanonicalName()+"_CONSOLE";
    public static final String CURRENT_TOKEN_SESSION_ATTRIBUTE_KEY = 
    		CurrentUser.class.getCanonicalName()+"_TOKEN";
    
    private CurrentUser() {
    }

    /**
     * Returns the name of the current user stored in the current session, or an
     * empty string if no user name is stored.
     * 
     * @throws IllegalStateException
     *             if the current session cannot be accessed.
     */
    public static String get() {
        String currentUser = (String) getCurrentRequest().getWrappedSession()
                .getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
        if (currentUser == null) {
            return "";
        } else {
            return currentUser;
        }
    }

    /**
     * Sets the name of the current user and stores it in the current session.
     * Using a {@code null} username will remove the username from the session.
     * 
     * @throws IllegalStateException
     *             if the current session cannot be accessed.
     */
    public static void set(String currentUser) {
        if (currentUser == null) {
            getCurrentRequest().getWrappedSession().removeAttribute(
                    CURRENT_USER_SESSION_ATTRIBUTE_KEY);
        } else {
            getCurrentRequest().getWrappedSession().setAttribute(
                    CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);
        }
    }

    private static VaadinRequest getCurrentRequest() {
        VaadinRequest request = VaadinService.getCurrentRequest();
        if (request == null) {
            throw new IllegalStateException("No request bound to current thread");
        }
        return request;
    }
    
    // Get/Set Locale for current logged user
    public static String getLocale() {
        String currentLocale = (String) getCurrentRequest().getWrappedSession()
                .getAttribute(CURRENT_LOCALE_SESSION_ATTRIBUTE_KEY);
        if (currentLocale == null) {
            return "";
        } else {
            return currentLocale;
        }
    }
    
    public static void setLocale(String currentLocale) {
        if (currentLocale == null) {
            getCurrentRequest().getWrappedSession().removeAttribute(
                    CURRENT_LOCALE_SESSION_ATTRIBUTE_KEY);
        } else {
            getCurrentRequest().getWrappedSession().setAttribute(
                    CURRENT_LOCALE_SESSION_ATTRIBUTE_KEY, currentLocale);
        }
    }
    
    // Get/Set User JPA Entity for current logged user
    public static UserEntity getUserEntity() {
    	UserEntity current = (UserEntity) getCurrentRequest().getWrappedSession()
                .getAttribute(CURRENT_ENTITY_SESSION_ATTRIBUTE_KEY);
        return current;
    }
    
    public static void setUserEntity(UserEntity current) {
        if (current == null) {
            getCurrentRequest().getWrappedSession().removeAttribute(
                    CURRENT_ENTITY_SESSION_ATTRIBUTE_KEY);
        } else {
            getCurrentRequest().getWrappedSession().setAttribute(
                    CURRENT_ENTITY_SESSION_ATTRIBUTE_KEY, current);
        }
    }
    
    // Get/Set Console for current logged user
    public static String getConsole() {
        String currentConsole = (String) getCurrentRequest().getWrappedSession()
                .getAttribute(CURRENT_CONSOLE_SESSION_ATTRIBUTE_KEY);
        if (currentConsole == null) {
            return "";
        } else {
            return currentConsole;
        }
    }
    
    public static void setConsole(String currentConsole) {
        if (currentConsole == null) {
            getCurrentRequest().getWrappedSession().removeAttribute(
                    CURRENT_CONSOLE_SESSION_ATTRIBUTE_KEY);
        } else {
            getCurrentRequest().getWrappedSession().setAttribute(
                    CURRENT_CONSOLE_SESSION_ATTRIBUTE_KEY, currentConsole);
        }
    }
    
    // Get/Set Security TokenKey for current logged user
    public static String getTokenKey() {
    	String current = (String) getCurrentRequest().getWrappedSession()
                .getAttribute(CURRENT_TOKEN_SESSION_ATTRIBUTE_KEY);
        return current;
    }
    
    public static void setTokenKey(String current) {
        if (current == null) {
            getCurrentRequest().getWrappedSession().removeAttribute(
                    CURRENT_TOKEN_SESSION_ATTRIBUTE_KEY);
        } else {
            getCurrentRequest().getWrappedSession().setAttribute(
                    CURRENT_TOKEN_SESSION_ATTRIBUTE_KEY, current);
        }
    }
    
}
