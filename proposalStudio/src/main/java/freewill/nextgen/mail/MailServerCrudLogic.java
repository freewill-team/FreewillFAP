package freewill.nextgen.mail;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.MailServerEntity;
import freewill.nextgen.data.CrudLogicInterface;
import freewill.nextgen.data.CrudViewInterface;
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
public class MailServerCrudLogic implements CrudLogicInterface<MailServerEntity> /*Serializable*/ {

    private CrudViewInterface<MailServerEntity> view;
    private Logger log = null;

    public MailServerCrudLogic(CrudViewInterface<MailServerEntity> simpleCrudView) {
        view = simpleCrudView;
        log = Logger.getLogger(view.getClass());
    }

    public void init() {
    	try{
	        editRecord(null);
	        if(view!=null){
	        	view.setNewRecordEnabled(true);
	        	view.showRecords(BltClient.get().getEntities(MailServerEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey()));
	        }
    	}
		catch(Exception e){
			log.error(e.getMessage());
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
    public void setFragmentParameter(String recId) {
        String fragmentParameter;
        if (recId == null || recId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = recId;
        }

        Page page = EntryPoint.get().getPage();
        if(view!=null)
        	page.setUriFragment("!" + view.getName() + "/" + fragmentParameter, false);
    }

    public void enter(String recId) {
        if (recId != null && !recId.isEmpty()) {
            if (recId.equals("new")) {
                newRecord();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                	long pid = Long.parseLong(recId);
                	MailServerEntity rec = findRecord(pid);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public MailServerEntity findRecord(Long recId) {
    	try{
    		MailServerEntity rec = (MailServerEntity) BltClient.get().getEntityById(""+recId, 
    				MailServerEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		return rec;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    	return null;
    }

    public void saveRecord(MailServerEntity rec, boolean saveAndNext) {
    	try{
    		MailServerEntity res = null;
	    	if(rec.getId()==null){
	    		res = (MailServerEntity) BltClient.get().createEntity(rec, MailServerEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getHostname() + " (" + res.getId() + ") created");
	    	}
	    	else{
	    		res = (MailServerEntity) BltClient.get().updateEntity(rec, MailServerEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getHostname() + " (" + res.getId() + ") updated");
	    	}
	    	if(view!=null){
		        view.clearSelection();
		        view.editRecord(null);
		        view.refreshRecord(res);
	    	}
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }

    public void deleteRecord(MailServerEntity rec) {
    	try{
    		boolean result = BltClient.get().deleteEntity(""+rec.getId(), MailServerEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result){
				if(view!=null){
					view.showSaveNotification(rec.getHostname() + " (" + rec.getId() + ") removed");
					view.removeRecord(rec);
				}
			}
			else{
				if(view!=null)
					view.showError("Error: "+rec.getHostname() + " cannot be removed");
			}
			if(view!=null){
				view.clearSelection();
		        view.editRecord(null);
			}
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }

    public void editRecord(MailServerEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(view!=null)
        	view.editRecord(rec);
    }

    public void newRecord() {
        view.clearSelection();
        setFragmentParameter("new");
        MailServerEntity rec = new MailServerEntity();
        view.editRecord(rec);
    }

    public void rowSelected(MailServerEntity rec) {
    	view.editRecord(rec);
    }
    
}
