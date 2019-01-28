package freewill.nextgen.mail;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.MailServerEntity;

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
public class MailServerCrudLogic implements Serializable {

    private MailServerCrudView view;
    private Logger log = null;

    public MailServerCrudLogic(MailServerCrudView simpleCrudView) {
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
                    //MailServerEntity rec = findRecord(pid);
                	MailServerEntity rec = findRecord(recId);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private MailServerEntity findRecord(String recId) {
    	try{
    		MailServerEntity rec = (MailServerEntity) BltClient.get().getEntityById(recId, MailServerEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		return rec;
    	}
		catch(Exception e){
			log.error(e.getMessage());
		}
    	return null;
    }

    public void saveRecord(MailServerEntity rec) {
    	try{
	    	if(view!=null){
	    		MailServerEntity res = null;
	    		System.out.println("Saving = "+rec.toString());
	    		
	    		if(rec.getId()==null)
	        		res = (MailServerEntity) BltClient.get().createEntity(rec, MailServerEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	        	else
	        		res = (MailServerEntity) BltClient.get().updateEntity(rec, MailServerEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	    		
		        view.showSaveNotification(rec.getLabel() + " (" + res.getId() + ") updated");
		        view.clearSelection();
		        view.editRecord(null);
		        view.refreshRecord(res);
	    	}
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
		}
    }

    public void deleteRecord(MailServerEntity rec) {
    	try{
	    	if(view!=null){
		    	System.out.println("Deleting = "+rec.toString());
		    	BltClient.get().deleteEntity(""+rec.getId(), MailServerEntity.class,
		    			EntryPoint.get().getAccessControl().getTokenKey());
		        view.showSaveNotification(rec.getLabel() + " (" + rec.getId() + ") removed");
		        view.clearSelection();
		        view.removeRecord(rec);
		        view.editRecord(null);
	        }
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
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
