package freewill.nextgen.common.rtdbclient;

import java.io.Serializable;
import java.util.List;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.entities.AlarmEntity;
import freewill.nextgen.common.entities.EmailEntity;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.common.entities.LoginEntity;
import freewill.nextgen.common.entities.ProcessEntity;
import freewill.nextgen.common.entities.ServerEntity;
import freewill.nextgen.common.entities.ServiceEntity;

/**
 * Back-end service for retrieving and updating data from a In-memory repository.
 * Implemented as a Singleton, so every process within the same JVM 
 * will share the same instance.
 * 
 */

@SuppressWarnings("serial")
public abstract class RtdbDataService implements Serializable {

	public abstract void initializeService(MonitoredProcess service);
	
	public static RtdbDataService get() {
		return RtdbDataServiceRedis.getInstance();
	}

	// Functions for processes
    public abstract List<ProcessEntity> getProcesses();
    public abstract void createProcess(ProcessEntity rec);
    public abstract void updateProcess(ProcessEntity rec);
    public abstract void deleteProcess(ProcessEntity rec);
    public abstract ProcessEntity getProcessById(String recId);
    public abstract boolean stopProcess(String recId, String user, String console);
    public abstract boolean startProcess(String recId, String user, String console);
    public abstract boolean deleteProcess(String recId, String user, String console);
    //public abstract List<ProcessEntity> getProcessesByServer(String server);
    //public abstract List<ProcessEntity> getProcessesByService(String server, String service);
    public abstract List<ProcessEntity> getProcessesByServer(ServerEntity server);
    public abstract List<ProcessEntity> getProcessesByService(ServiceEntity service);
    
    // Functions for Alarms
    public abstract List<AlarmEntity> getAlarms();
    public abstract void createAlarm(AlarmEntity rec);
    public abstract void updateAlarm(AlarmEntity rec);
    public abstract void deleteAlarm(AlarmEntity rec);
    public abstract AlarmEntity getAlarmById(Long recId);
    public abstract List<AlarmEntity> getAlarmsByPoint(String point, String pointType);
    
	// Functions for Events temporal Queue
	public abstract void pushEvent(EventEntity rec);
	public abstract EventEntity retrieveEvent();
    
    // Functions for generic entities
	public abstract <T> List<T> getEntities(Class<T> myentity);
    public abstract <T> void createEntity(Object rec, Class<T> myentity);
    public abstract <T> void updateEntity(Object rec, Class<T> myentity);
    public abstract <T> void deleteEntity(Object rec, Class<T> myentity);
    public abstract Object getEntityById(Object recId, Class<?> myentity);

    // Functions for Alarms temporal Queue
 	public abstract void pushEmail(EmailEntity rec);
 	public abstract EmailEntity retrieveEmail();
  	
  	// Functions for User/Login control
  	public abstract void userCheckin(String user, String console, String app);
  	public abstract void userCheckout(String user, String console, String app);
  	public abstract LoginEntity getUserByName(String user);

  	// Functions for stop/start servers/services
	public abstract boolean startServer(String id, String userLogin, String console);
	public abstract boolean stopServer(String id, String userLogin, String console);
	public abstract boolean startService(String id, String userLogin, String console);
	public abstract boolean stopService(String id, String userLogin, String console);
	
}
