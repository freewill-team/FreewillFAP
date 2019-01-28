package freewill.nextgen.common.bltclient;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.entities.Mytoken;

/**
 * Back-end service for retrieving and updating data from a remote service.
 * Implemented as a Singleton, so every process within the same JVM 
 * will share the same instance.
 * 
 */

@SuppressWarnings("serial")
public abstract class BltClient implements Serializable {
	
	public abstract void initializeService(MonitoredProcess service);
	
	public static BltClient get() {
		return BltClientRest.getInstance();
	}
    
    // Functions for generic entities
 	public abstract <T> List<T> getEntities(Class<T> myentity, String key) throws Exception;
 	public abstract <T> Object createEntity(Object rec, Class<T> myentity, String key) throws Exception;
    public abstract <T> Object updateEntity(Object rec, Class<T> myentity, String key) throws Exception;
    public abstract <T> boolean deleteEntity(String recId, Class<T> myentity, String key) throws Exception;
    public abstract <T> Object getEntityById(String recId, Class<T> myentity, String key) throws Exception;
    public abstract <T> Boolean executeCommand(String command, Object rec, Class<T> myentity, String key) throws Exception;
    public abstract <T> Object executeCommand(String command, Class<T> myentity, String key) throws Exception;
    public abstract <T> List<T> executeQuery(String querycommand, Class<T> myentity, String key) throws Exception;
    public abstract <T> List<T> executeQuery(String querycommand, Object rec, Class<T> myentity, String key) throws Exception;

    // Functions for Authentication/Security 
    public abstract String askForToken(String user, String password, String servicetype);
	public abstract String waitUntilToken(String user, String password, String servicetype);
	public abstract Collection<Mytoken> getListOfTokens();
	public abstract boolean resetPassword(String user, String servicetype);
    
}
