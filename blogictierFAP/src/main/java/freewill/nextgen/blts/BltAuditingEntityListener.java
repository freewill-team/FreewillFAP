package freewill.nextgen.blts;

import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import freewill.nextgen.common.Messages;
import freewill.nextgen.common.Utils;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.common.entities.LoginEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

public class BltAuditingEntityListener {
	
	@PostPersist
    public void createChangeLog(Object obj) {
		saveEvent(obj, Messages.get().getKey("created", System.getProperty("LOCALE")));
    }
	
	@PostUpdate
    public void updateChangeLog(Object obj) {
		saveEvent(obj, Messages.get().getKey("updated", System.getProperty("LOCALE")));
    }
	
	@PostRemove
    public void deleteChangeLog(Object obj) {
		saveEvent(obj, Messages.get().getKey("deleted", System.getProperty("LOCALE")));
    }
	
	private void saveEvent(Object obj, String action) {
		// Get current authenticated user
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String entityLabel = getLabel(obj);
		// Get console and username
		String console = Utils.NOCONSOLE;
		String username = Utils.SYSTEMUSER;
		LoginEntity user = null;
		if(auth!=null){
			user = RtdbDataService.get().getUserByName(auth.getName());
			if(user!=null){
				console = user.getConsole();
				username = auth.getName();
			}
		}
			
		// It also injects a new event for the audit trail
		RtdbDataService.get().pushEvent(new EventEntity(
				new Date(), 
				String.format(AlarmDic.ALM0031.toString(), entityLabel, action),
				entityLabel, // Entity Label
				ADMSservice.RTS.toString(), // ParentPoint
				obj.getClass().getSimpleName(), // PointType
				AlarmDic.ALM0031.getSeverity(),
				AlarmDic.ALM0031.getCategory(),
				username, // Utils.SYSTEMUSER, // username
				console // Utils.findHostName() // console
				));
	}
	
	private static boolean isGetter(Method method){
		if(method.getName().startsWith("getClass")) return false;
		if(!method.getName().startsWith("get")) return false;
		if(method.getParameterTypes().length != 0) return false;  
		if(void.class.equals(method.getReturnType())) return false;
		return true;
	}
	
	private static String getLabel(Object obj){
		try{
			// Find getLabel() method
			Method labelMethod = null;
			Method[] methods = obj.getClass().getMethods();
			for(Method method : methods){
				if(isGetter(method)){
					if( method.getName().contains("Nombre") 
							|| method.getName().contains("Name") 
							|| method.getName().contains("Customid") 
							|| method.getName().contains("Label") ){
						labelMethod = method;
						break;
					}
				}   	
			}
			// Get Entity Label with reflection
			return (String) labelMethod.invoke(obj);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return obj.toString();
		}
	}
}
