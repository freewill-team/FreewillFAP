package freewill.nextgen.genericCrud;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the record editor form and the data source, including
 * fetching and saving records.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */

@SuppressWarnings("serial")
public class GenericCrudLogic<T> implements Serializable {

    private GenericCrudView<T> view;
    private Class<T> entity;
    private String idfield = null;
    private Logger log = null;

    public GenericCrudLogic(GenericCrudView<T> simpleCrudView, Class<T> entity, String idfield) {
        view = simpleCrudView;
        this.entity = entity;
        this.idfield = idfield;
        if(view!=null)
        	log = Logger.getLogger(view.getClass());
        else
        	log = Logger.getLogger(this.getClass());
    }

    public void init() {
    	try{
	        editRecord(null);
	        if(view!=null){
	        	view.setNewRecordEnabled(true);
	        	view.showRecords(BltClient.get().getEntities(entity, 
	        			EntryPoint.get().getAccessControl().getTokenKey()));
	        }
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
			e.printStackTrace();
		}
    }

    public void cancelRecord() {
        setFragmentParameter("");
        if(view!=null){
	        view.clearSelection();
	        view.editRecord(null);
        }
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    private void setFragmentParameter(String recId) {
        String fragmentParameter;
        if (recId == null || recId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = recId;
        }

        Page page = EntryPoint.get().getPage();
        if(view!=null)
        	page.setUriFragment("!" + view.VIEW_NAME + "/" + fragmentParameter, false);
    }

    public void enter(String recId) {
        if (recId != null && !recId.isEmpty()) {
            if (recId.equals("new")) {
                newRecord();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    //int pid = Integer.parseInt(recId);
                    //User rec = findRecord(pid);
                	T rec = findRecord(recId);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
	private T findRecord(String recId) {
    	try{
    		T rec = (T)BltClient.get().getEntityById(recId, entity,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		return rec;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
    	return null;
    }

    @SuppressWarnings("unchecked")
	public void saveRecord(T rec) {
    	try{
	    	T res = null;
	    	String message = "";
	    	System.out.println("Saving = "+rec.toString());
	    		
	    	if(this.getIdMethod(rec)==null){
	        	res = (T) BltClient.get().createEntity(rec, entity,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        	message = "Record " + this.getIdMethod(res) + " created";
	    	}
	    	else{
	        	res = (T) BltClient.get().updateEntity(rec, entity,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        	message = "Record " + this.getIdMethod(res) + " updated";
	    	}
	    	
	    	if(view!=null){
		        view.showSaveNotification(message);
		        view.refreshRecord(res);
		        view.editRecord(null);
		        view.clearSelection();
	    	}
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			view.showError(e.getMessage());
		}
    }

    public void deleteRecord(T rec) {
    	try{
		    System.out.println("Deleting = "+rec.toString());
		    BltClient.get().deleteEntity(""+this.getIdMethod(rec), entity,
		    		EntryPoint.get().getAccessControl().getTokenKey());
		    if(view!=null){
		        view.showSaveNotification("Record " + this.getIdMethod(rec) + " removed");
		        view.clearSelection();
		        view.removeRecord(rec);
		        view.editRecord(null);
	        }
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
    }

    public void editRecord(T rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(getIdMethod(rec) + "");
            setFragmentParameter("");
        }
        if(view!=null)
        	view.editRecord(rec);
    }

    public void newRecord() {
    	try{
	        view.clearSelection();
	        setFragmentParameter("new");
	        T rec = entity.newInstance();
	        view.editRecord(rec);
    	}
		catch(Exception e){
			log.error(e.getMessage());
		}
    }

    public void rowSelected(T rec) {
    	if(rec==null) return;
    	view.editRecord(rec);
    }
    
    public Long getIdMethod(T rec){
    	// executes the getId() function adapted to the idfield value
    	try{
    		String getidfield = "get"+ idfield.substring(0, 1).toUpperCase()+idfield.substring(1);
	    	Method method = entity.getMethod(getidfield);
			return (Long) method.invoke(rec);
    	}
    	catch(Exception e){
    		log.error(e.getMessage());
    	}
    	return null;
	}
    
    public void setIdMethod(T rec, Long id){
    	// executes the setId() function adapted to the idfield value
    	try{
    		String getidfield = "set"+ idfield.substring(0, 1).toUpperCase()+idfield.substring(1);
	    	Method method = entity.getMethod(getidfield, Long.class);
			method.invoke(rec, id);
    	}
		catch(Exception e){
			log.error(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void cloneRow(T rec) {
		try{
			if(rec==null) return;
			// Clone row
			if(view!=null){
	    		T res = null;
	    		System.out.println("Cloning = "+rec.toString());
	    		// clone row no esta funcionando bien
	    		setIdMethod(rec, null);
	        	res = (T) BltClient.get().createEntity(rec, entity,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	    		
		        view.showSaveNotification("Record " + this.getIdMethod(rec) + " cloned");
		        view.clearSelection();
		        view.refreshRecord(res);
	    	}
		}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
	}
    
	public void saveRecords(List<T> list){
    	try{
	    	for(T rec:list){
	    		Long recId = this.getIdMethod(rec);
	    		T foundrec = findRecord(""+recId);
	    		if(foundrec==null)
	    			// new record
	        		BltClient.get().createEntity(rec, entity,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	        	else
	        		// update existing record
	        		BltClient.get().updateEntity(rec, entity,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	    	}
	    	this.init();
    	}
    	catch(Exception e){
    		log.error(e.getMessage());
    		e.printStackTrace();
    		if(view!=null)
    			view.showError(e.getMessage());
    	}
    }

	public void checkRecords(List<T> list) {
		// checks whether the data can be acually uploaded
		log.error("checkRecords() not implemented yet !");
		if(view!=null)
			view.showError("checkRecords() not implemented yet !");
	}
	
}
