package freewill.nextgen.common.rtdbclient;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.Utils;
import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.Utils.ServiceStatusEnum;
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
 * Specific implementation for REDIS.
 * 
 */

@SuppressWarnings("serial")
public class RtdbDataServiceRedis extends RtdbDataService {
	
	// Internal variables
    private static RtdbDataServiceRedis INSTANCE;			// Instance to itself
    private RedissonClient redisson = null;					// connection to the Redis In-memory database
    private String REDISCLUSTER = "No";						// Is Redis in cluster?
    private String REDISSERVER = "127.0.0.1:6379";			// Redis server ip:port
    private String REDISAUTH = "";							// Redis authorization password
    private MonitoredProcess parentService = null;			// Parent Service/Process Class
    private RList<ProcessEntity> processesList = null;		// Internal cache for Processes
    private RList<AlarmEntity> alarmList = null;			// Internal cache for Alarms
    private RQueue<EventEntity> eventQueue = null; 			// Internal temporary queue for Events
    private RQueue<EmailEntity> emailQueue = null; 			// Internal temporary queue for eMails
    private static HashMap<String, RList<Object>> entitiesList = 
			new HashMap<String, RList<Object>>(); // Contains a HashMap with the different Entity Lists
    
    public synchronized static RtdbDataService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RtdbDataServiceRedis();
        }
        return INSTANCE;
    }
    
    private RtdbDataServiceRedis() {
    	// initializeService() needs to be called before first use of this service
    	// already done in MonitoredProcess
    	echo("Creating RtdbDataServiceRedis instance");
    }
    
    @Override
    public void initializeService(MonitoredProcess service){
    	parentService = service;
    	try {
    		// It reads config properties first
    		if(parentService!=null){
    			REDISCLUSTER = parentService.readConfigPropString("REDISCLUSTER", "No");
    			REDISSERVER = parentService.readConfigPropString("REDISSERVER", "127.0.0.1:6379");
    			REDISAUTH = parentService.readConfigPropString("REDISAUTH", "");
    		}
    		// It creates distributed cache
    		redisson = createDbconnection();
    	} 
    	catch (Exception e) {
			error(e.getMessage());
			e.printStackTrace();
			redisson = null;
			throw new IllegalArgumentException("Fail to invoke RtdbDataServiceRedis manager");
		}
    }
    
    private RedissonClient createDbconnection() throws Exception {
    	
    	Config config = new Config();
    	if(REDISCLUSTER.equals("Yes")){
    		config.useClusterServers().addNodeAddress("redis://"+REDISSERVER);
    		if(!REDISAUTH.isEmpty())
    			config.useClusterServers().setPassword(REDISAUTH);
    	}
    	else{
    		config.useSingleServer().setAddress("redis://"+REDISSERVER);
    		if(!REDISAUTH.isEmpty())
    			config.useSingleServer().setPassword(REDISAUTH);
    	}
    	
    	RedissonClient conn = Redisson.create(config);
		return conn;
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
    
	// Functions for processes
	
	@Override
	public List<ProcessEntity> getProcesses() {
		// Retrieves the entire list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateProcessesList();
			
			List<ProcessEntity> list = new ArrayList<>();
			for(ProcessEntity rec:processesList){
				list.add(rec.clone());
			}
			
			return list;
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail retrieving processesList");
		}
	}
	
	@Override
	public void createProcess(ProcessEntity rec) {
		// Adds a new record to the list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateProcessesList();
			
			processesList.add(rec);
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail creating new record in processesList - "+rec);
		}
	}
	
	@Override
	public void updateProcess(ProcessEntity rec) {
		// Updates the record in the list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateProcessesList();
			
			ProcessEntity recold = getProcessById(rec.getID());
			if(recold!=null){
				int i = processesList.indexOf(recold);
				processesList.set(i, rec);
			}
			else
				error("Record "+rec.getID()+" cannot be found");
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail updating record in processesList - "+rec);
		}
	}

	@Override
	public void deleteProcess(ProcessEntity rec) {
		// Removes the record from the list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateProcessesList();
			
			processesList.remove(rec);
			
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail deleting record in processesList - "+rec);
		}
	}
	
	@Override
	public boolean deleteProcess(String recId, String user, String console){
		// Removes the record from the list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateProcessesList();
			
			ProcessEntity rec = getProcessById(recId);
			if(rec!=null){
				processesList.remove(rec);
				
				// It also injects a new event reporting the new Process is removed
				RtdbDataService.get().pushEvent(new EventEntity(
					     new Date(), 
					     String.format(AlarmDic.ALM0008.toString(), rec.getID()),
					     rec.getID(),
					     rec.getServer(),
					     Utils.PROCESSES,
					     AlarmDic.ALM0008.getSeverity(),
					     AlarmDic.ALM0008.getCategory(),
					     user,
					     console
					     ));
				
				return true;
			}
			else
				error("Record "+recId+" cannot be found");

			return false; // Not Found
		}
		catch (Exception e)
		{
			error(e.getMessage());
		}
		return false;
	}

	@Override
	public ProcessEntity getProcessById(String recId) {
		// Find Object by its FullId
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateProcessesList();
			
			for(ProcessEntity rec:processesList){
				if(recId.equals(rec.getID()))
					return rec.clone();
			}
			return null; // Not Found
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail searching record in processesList - "+recId);
		}
	}
	
	@Override
	public boolean stopProcess(String recId, String user, String console){
		// Find Object by its FullId
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
					
			checkandcreateProcessesList();
					
			ProcessEntity rec = getProcessById(recId);
			if(rec!=null){
				if(rec.getStatus()==ServiceStatusEnum.STOP 
						|| rec.getStatus()==ServiceStatusEnum.FAILED)
						return true;
				int i = processesList.indexOf(rec);
				rec.setStopProcess(true);
				processesList.set(i, rec);
				// It also injects a new event reporting the new Process is requested to be stopped
				RtdbDataService.get().pushEvent(new EventEntity(
					     new Date(), 
					     String.format(AlarmDic.ALM0005.toString(), recId),
					     recId,
					     rec.getServer(),
					     Utils.PROCESSES,
					     AlarmDic.ALM0005.getSeverity(),
					     AlarmDic.ALM0005.getCategory(),
					     user,
					     console
					     ));
				
				return true;
			}
			else
				error("Record "+recId+" cannot be found");

			return false; // Not Found
		}
		catch (Exception e)
		{
			error(e.getMessage());
		}
		return false;
	}
	
	@Override
	public boolean startProcess(String recId, String user, String console){
		// Find Object by its FullId
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
					
			checkandcreateProcessesList();
					
			ProcessEntity rec = getProcessById(recId);
			if(rec!=null){
				if(rec.getStatus()==ServiceStatusEnum.GOOD 
						|| rec.getStatus()==ServiceStatusEnum.STARTING)
						return true;
				int i = processesList.indexOf(rec);
				rec.setStartProcess(true);
				processesList.set(i, rec);
				// It also injects a new event reporting the new Process is requested to be started
				RtdbDataService.get().pushEvent(new EventEntity(
					     new Date(), 
					     String.format(AlarmDic.ALM0006.toString(), recId),
					     recId,
					     rec.getServer(),
					     Utils.PROCESSES,
					     AlarmDic.ALM0006.getSeverity(),
					     AlarmDic.ALM0006.getCategory(),
					     user,
					     console
					     ));
				
				return true;
			}
			else
				error("Record "+recId+" cannot be found");

			return false; // Not Found
		}
		catch (Exception e)
		{
			error(e.getMessage());
		}
		return false;
	}
	
	/*public List<ProcessEntity> getProcessesByServer(String server){
		// Retrieves processes by server
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateProcessesList();
			
			List<ProcessEntity> list = new ArrayList<>();
			for(ProcessEntity rec:processesList){
				if(server.equals(rec.getServer()))
					list.add(rec.clone());
			}
			return list;
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail retrieving processesList by Server "+server);
		}
	}*/
	
    /*public List<ProcessEntity> getProcessesByService(String server, String service){
		// Retrieves processes by service
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
					
			checkandcreateProcessesList();
					
			List<ProcessEntity> list = new ArrayList<>();
			for(ProcessEntity rec:processesList){
				if(server.equals(rec.getServer()) 
				&& service.equals(rec.getService()))
					list.add(rec.clone());
			}
			
			return list;
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail retrieving processesList by Service "+service+":"+server);
		}
    }*/
	
	@Override
	public List<ProcessEntity> getProcessesByServer(ServerEntity server){
		// Retrieves processes by server
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateProcessesList();
			
			List<ProcessEntity> list = new ArrayList<>();
			for(ProcessEntity rec:processesList){
				if(rec.getID().startsWith(server.getID()))
					list.add(rec.clone());
			}
			return list;
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail retrieving processesList by Server "+server.getID());
		}
	}
	
	@Override
    public List<ProcessEntity> getProcessesByService(ServiceEntity service){
		// Retrieves processes by service
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
					
			checkandcreateProcessesList();
					
			List<ProcessEntity> list = new ArrayList<>();
			for(ProcessEntity rec:processesList){
				if(rec.getID().startsWith(service.getID()))
					list.add(rec.clone());
			}
			
			return list;
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail retrieving processesList by Service "+service.getID());
		}
    }

	private void checkandcreateProcessesList() {
		// checks whether the processesList has been already created
		if(processesList==null && redisson!=null){
			processesList = redisson.getList("processesList");
		}
	}

	// Functions for Alarms
	
	@Override
	public List<AlarmEntity> getAlarms() {
		// Retrieves the entire list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
					
			checkandcreateAlarmList();
			
			List<AlarmEntity> list = new ArrayList<>();
			for(AlarmEntity rec:alarmList){
				// Area filters will be implemented here
				list.add(rec.clone());
			}
			
			return list;
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail retrieving alarmList");
		}
	}

	@Override
	public void createAlarm(AlarmEntity rec) {
		// Adds a new record to the list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
					
			checkandcreateAlarmList();
			
			// Get next Id
			long nextId = 0;
			for(AlarmEntity obj:alarmList){
				if( obj.getId()>nextId )
					nextId = obj.getId();
				if( obj.getPoint().equals(rec.getPoint()) 
						&& obj.getPointType().equals(rec.getPointType())
						&& obj.getParentPoint().equals(rec.getParentPoint()))
					return; // To avoid duplicates
			}
			
			// Finally, saves the record
			rec.setID(nextId+1);
			alarmList.add(rec);
			
			// It also injects a new event
			RtdbDataService.get().pushEvent(new EventEntity(
					rec.getTimestamp(), 
					rec.getMessage(),
					rec.getPoint(),
					rec.getParentPoint(),
					rec.getPointType(),
					rec.getSeverity(),
					rec.getCategory(),
					Utils.SYSTEMUSER,
					Utils.findHostName()
					));
			
			// Also injects the alarm in the internal queue for email forwarding
			RtdbDataService.get().pushEmail(new EmailEntity(
					"", // recipient will be retrieved from MailServerEntity
					"New Alarm for: "+rec.getPoint(),
					"Timestamp: "+rec.getTimestamp()+"\n"+
				    		"Message:   "+rec.getMessage()+"\n"+
				    		"Point:     "+rec.getPoint()+"\n"+	
				    		"PointType: "+rec.getPointType()+"\n"+
				    		"Category:  "+rec.getCategory()+"\n"+
				    		"Severity:  "+rec.getSeverity()+"\n"+
				    		"Parent Pt: "+rec.getParentPoint()+"\n"+
				    		"\n",
		    		0L // Company=0 as it is a system alarm
		    		));
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail creating new record in alarmList - "+rec);
		}
	}

	@Override
	public void updateAlarm(AlarmEntity rec) {
		// Updates the record in the list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateAlarmList();
			
			AlarmEntity recold = getAlarmById(rec.getId());
			if(recold!=null){
				int i = alarmList.indexOf(recold);
				alarmList.set(i, rec);
			}
			else
				error("Record "+rec.getId()+" cannot be found");
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail updating record in alarmList - "+rec);
		}
	}

	@Override
	public void deleteAlarm(AlarmEntity rec) {
		// Removes the record from the list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateAlarmList();
			
			alarmList.remove(rec);
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail deleting record in alarmList - "+rec);
		}
	}

	@Override
	public AlarmEntity getAlarmById(Long recId) {
		// Find Object by its Id
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateAlarmList();
			
			for(AlarmEntity rec:alarmList){
				if(recId.equals(rec.getId()))
					return rec.clone();
			}
			return null; // Not Found
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail searching record in alarmList - "+recId);
		}
	}
	
	@Override
	public List<AlarmEntity> getAlarmsByPoint(String point, String pointType) {
		// Find Object by its Point and Parent
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			checkandcreateAlarmList();
			
			List<AlarmEntity> result = new ArrayList<AlarmEntity>();
			
			for(AlarmEntity rec:alarmList){
				if( point.equals(rec.getPoint()) 
						&& pointType.equals(rec.getPointType())){
					result.add(rec.clone());
				}
			}
			return result;
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail searching record in alarmList - "+point);
		}
	}
	
	private void checkandcreateAlarmList() {
		// checks whether the alarmList has been already created
		if(alarmList==null && redisson!=null){
			alarmList = redisson.getList("alarmList");
		}
	}
	
	// Functions for Events temporal Queue
	// Developers are encouraged to use this function to save new events,
	// for best performance and reliability
	
	public void pushEvent(EventEntity rec){
		// Adds a new record to the queue
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
							
			checkandcreateEventQueue();
					
			// Finally, saves the record
			eventQueue.add(rec);
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail creating new record in eventQueue - "+rec);
		}
	}
	
	public EventEntity retrieveEvent()
	{
		// Retrieves the last record from the queue
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
									
			checkandcreateEventQueue();
							
			// Retrieves the record, null if empty
			return eventQueue.poll();	
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail retrieving record from eventQueue");
		}
	}
	
	private void checkandcreateEventQueue() {
		// checks whether the eventQueue has been already created
		if(eventQueue==null && redisson!=null){
			eventQueue = redisson.getQueue("eventQueue");
		}
	}
	
	// Functions for generic entities
	
	public <T> List<T> getEntities(Class<T> myentity){
		// Retrieves the entire list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			
			List<T> list = new ArrayList<>();
			for(T rec:checkandcreateEntityList(myentity)){
				// Area filters will be implemented here
				list.add(rec);
				// clonar rec siempre que sea posible
			}
			
			return list;
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail retrieving "+myentity.getSimpleName());
		}
	}
	
	public <T> void createEntity(Object rec, Class<T> myentity){
		// Adds a new record to the list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
									
			RList<T> list = checkandcreateEntityList(myentity);
							
			// Finally, saves the record
			list.add(myentity.cast(rec));
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail creating new record in "+myentity.getSimpleName()+ ">"+rec);
		}
	}
	
	public <T> void updateEntity(Object rec, Class<T> myentity){
		// Updates the record in the list
		// The object must have a getID() method 
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
					
			RList<T> list = checkandcreateEntityList(myentity);
			
			// Gets the record ID
			Object recId = null;
			Method[] methods = rec.getClass().getMethods();
			for(Method method : methods){
				String methodName = method.getName().toUpperCase();
		    	if(methodName.equals("GETID")){
    		    	recId = method.invoke(rec);
		    	}
			}
			
			// Gets the old record
			Object recOld = getEntityById(recId, myentity);
			if(recOld!=null){
				int i = list.indexOf(recOld);
				list.set(i, myentity.cast(rec));
			}
			else
				error("Record "+recId+" cannot be found in "+myentity.getSimpleName());
		}
		catch (Exception e)
		{
			error(e.getMessage());
			//e.printStackTrace(); // solo para pruebas
			throw new IllegalArgumentException("Fail updating new record in "+myentity.getSimpleName()+ ">"+rec);
		}
	}
	
	public <T> void deleteEntity(Object rec, Class<T> myentity){
		// Removes the record from the list
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
					
			RList<T> list = checkandcreateEntityList(myentity);
					
			// Finally, removes the record
			list.remove(myentity.cast(rec));
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail deleting record in "+myentity.getSimpleName()+ ">"+rec);
		}
	}
	
	public Object getEntityById(Object recId, Class<?> myentity){
		// Find Object by its Id
		// The object must have a getID() or getId() method 
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
			RList<?> list = checkandcreateEntityList(myentity);
			
			// Find getId() method
			Method method = null;
			try{
				method = myentity.getMethod("getID");
			}
			catch(Exception e2){
				try{
					method = myentity.getMethod("getId");
				}
				catch(Exception e3){
					throw new IllegalArgumentException(
							"Fail searching record in "+myentity.getSimpleName()+
							". No getId()/getID() method found.");
				}
			}
			
			for(Object rec:list){
				Object value = method.invoke(rec);
				//System.out.println("GETID = "+value);
				if(value.equals(recId))
			    	return rec;
				// clonar rec 
			}
			return null; // Not Found
		}
		catch (Exception e)
		{
			error(e.getMessage());
	    	throw new IllegalArgumentException("Fail searching record in "+myentity.getSimpleName()+ ">"+recId);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> RList<T> checkandcreateEntityList(Class<T> entity) {
		// checks whether the entityList has been already created
		RList<Object> list = entitiesList.get(entity.getSimpleName());
		if(list==null && redisson!=null){
			// it doesn't exist, so create a new one
			list = redisson.getList(entity.getSimpleName());
			entitiesList.put(entity.getSimpleName(), list);
		}
		return (RList<T>) list;
	}

	// Functions for Email temporal Queue
		
	public void pushEmail(EmailEntity rec){
		// Adds a new record to the queue
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
								
			checkandcreateEmailQueue();
						
			// Finally, saves the record
			emailQueue.add(rec);
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail creating new record in emailQueue - "+rec);
		}
	}
		
	public EmailEntity retrieveEmail()
	{
		// Retrieves the last record from the queue
		try{
			// First check whether a valid connection already exists
			if( redisson==null || redisson.isShutdown())
				redisson = createDbconnection();
										
			checkandcreateEmailQueue();
								
			// Retrieves the record, null if empty
			return emailQueue.poll();	
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail retrieving record from emailQueue");
		}
	}
		
	private void checkandcreateEmailQueue() {
		// checks whether the emailQueue has been already created
		if(emailQueue==null && redisson!=null){
			emailQueue = redisson.getQueue("emailQueue");
		}
	}
	
	// Functions for User/Login control
	
	public void userCheckin(String user, String console, String app){
		// Add/update user in Login list
		try{
			String loginId = console+":"+user+":"+app;
			LoginEntity login = (LoginEntity) getEntityById(loginId, LoginEntity.class);
			if(login==null){
				login = new LoginEntity();
				login.setID(loginId);
				login.setName(user);
				login.setConsole(console);
				login.setLastCheckin(new Date());
				login.setAorId(0L);
				login.setAorLabel("");
				login.setApplication(app);
				createEntity(login, LoginEntity.class);
			}
			else{
				login.setLastCheckin(new Date());
				updateEntity(login, LoginEntity.class);
			}
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail retrieving record from LoginEntity");
		}
	}
	
	public void userCheckout(String user, String console, String app){
		// Removes user from Login list
		try{
			String loginId = console+":"+user+":"+app;
			LoginEntity login = (LoginEntity) getEntityById(loginId, LoginEntity.class);
			if(login!=null)
				deleteEntity(login, LoginEntity.class);
			else
				throw new IllegalArgumentException("Fail removing record from LoginEntity");
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail removing record from LoginEntity");
		}
	}
	
	public LoginEntity getUserByName(String user){
		// Add/update user in Login list
		try{
			List<LoginEntity> logins = getEntities(LoginEntity.class);
			for(LoginEntity rec:logins){
				if(user.equals(rec.getName()))
					return rec;
			}
			return null;
		}
		catch (Exception e)
		{
			error(e.getMessage());
			throw new IllegalArgumentException("Fail retrieving record from LoginEntity");
		}
	}

	// Functions for stop/start servers/services
	
	@Override
	public boolean startServer(String id, String user, String console) {
		try{
			ServerEntity srv = (ServerEntity) this.getEntityById(id, ServerEntity.class);
			if(srv==null) return false;
			// First injects a new event reporting the server is requested to be started
			RtdbDataService.get().pushEvent(new EventEntity(
				     new Date(), 
				     String.format(AlarmDic.ALM0010.toString(), id),
				     id,
				     srv.getName(),
				     Utils.SERVERS,
				     AlarmDic.ALM0010.getSeverity(),
				     AlarmDic.ALM0010.getCategory(),
				     user,
				     console
				     ));
			
			// Then Starts all processes in server
			List<ProcessEntity> list = this.getProcessesByServer(srv); //id);
			for(ProcessEntity rec:list){
				startProcess(rec.getID(), user, console);	
			}
					
			return true;
		}
		catch (Exception e)
		{
			error(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean stopServer(String id, String user, String console) {
		try{
			ServerEntity srv = (ServerEntity) this.getEntityById(id, ServerEntity.class);
			if(srv==null) return false;
			// First injects a new event reporting the server is requested to be stopped
			RtdbDataService.get().pushEvent(new EventEntity(
				     new Date(), 
				     String.format(AlarmDic.ALM0009.toString(), id),
				     id,
				     srv.getName(),
				     Utils.SERVERS,
				     AlarmDic.ALM0009.getSeverity(),
				     AlarmDic.ALM0009.getCategory(),
				     user,
				     console
				     ));
			
			// Then Stops all processes in server
			List<ProcessEntity> list = this.getProcessesByServer(srv); //id);
			for(ProcessEntity rec:list){
				stopProcess(rec.getID(), user, console);	
			}
					
			return true;
		}
		catch (Exception e)
		{
			error(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean startService(String id, String user, String console) {
		try{
			ServiceEntity srv = (ServiceEntity) this.getEntityById(id, ServiceEntity.class);
			if(srv==null) return false;
			// First injects a new event reporting the service is requested to be started
			RtdbDataService.get().pushEvent(new EventEntity(
				     new Date(), 
				     String.format(AlarmDic.ALM0010.toString(), id),
				     id,
				     srv.getServer(),
				     Utils.SERVICES,
				     AlarmDic.ALM0010.getSeverity(),
				     AlarmDic.ALM0010.getCategory(),
				     user,
				     console
				     ));
			
			// Then Start all processes in service
			List<ProcessEntity> list = this.getProcessesByService(srv); //.getServer(), srv.getName());
			for(ProcessEntity rec:list){
				startProcess(rec.getID(), user, console);	
			}
			
			return true;
		}
		catch (Exception e)
		{
			error(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean stopService(String id, String user, String console) {
		try{
			ServiceEntity srv = (ServiceEntity) this.getEntityById(id, ServiceEntity.class);
			if(srv==null) return false;
			// First injects a new event reporting the service is requested to be stopped
			RtdbDataService.get().pushEvent(new EventEntity(
				     new Date(), 
				     String.format(AlarmDic.ALM0009.toString(), id),
				     id,
				     srv.getServer(),
				     Utils.SERVERS,
				     AlarmDic.ALM0009.getSeverity(),
				     AlarmDic.ALM0009.getCategory(),
				     user,
				     console
				     ));
			
			// Then Stops all processes in service
			List<ProcessEntity> list = this.getProcessesByService(srv); //.getServer(), srv.getName());
			for(ProcessEntity rec:list){
				stopProcess(rec.getID(), user, console);	
			}
					
			return true;
		}
		catch (Exception e)
		{
			error(e.getMessage());
		}
		return false;
	}
	
}
