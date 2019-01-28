package freewill.nextgen.hmi.common;

import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.vaadin.ui.ComboBox;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;

@SuppressWarnings("serial")
public class GenericCombo<T> extends ComboBox {
	
	// This generic ComboBox is only valid for entities with 
	// default "Label" and "Description" fields
	Class<T> entity = null;
	private Logger log = null;

	public GenericCombo(Class<T> entity){
		this.entity = entity;
		log = Logger.getLogger(GenericCombo.class);
		createCombo();
	}
	
	public GenericCombo(String caption, Class<T> entity){
		super(caption);
		this.entity = entity;
		createCombo();
	}
	
	private void createCombo(){
		try{
			Collection<T> list = BltClient.get().getEntities(entity, 
					EntryPoint.get().getAccessControl().getTokenKey());
			boolean first = true;
			
			// Find getName() method
			Method labelMethod = null;
			Method[] methods = entity.getMethods();
			for(Method method : methods){
			    if(isGetter(method)){
			    	if( method.getName().contains("Name") ){
			    		labelMethod = method;
			    		break;
			    	}
			    }   	
			}
			
			// Fulfills data
			for (T s : list) {
		        this.addItem(s);
		        try{
			        String value = (String) labelMethod.invoke(s);
			        this.setItemCaption(s, value);
			        if(first){
			        	this.setValue(s);
			        	first = false;
			        }
		        }
		        catch(Exception e){
		        	System.out.println(e.getMessage());
		        }
		    }
			
	        this.setNullSelectionAllowed(false);
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
	}
	
	public void Refresh(){
		this.removeAllItems();
		createCombo();
	}
	
	private static boolean isGetter(Method method){
		if(method.getName().startsWith("getClass")) return false;
		if(!method.getName().startsWith("get")) return false;
		if(method.getParameterTypes().length != 0) return false;  
		if(void.class.equals(method.getReturnType())) return false;
		return true;
	}

}
