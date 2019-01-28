package freewill.nextgen.deliverable;

import java.util.Collection;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CrudLogicInterface;
import freewill.nextgen.data.CrudViewInterface;
import freewill.nextgen.data.DeliverableEntity;
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
public class DeliverableCrudLogic implements CrudLogicInterface<DeliverableEntity> /*Serializable*/ {

    private /*DeliverableCrudView*/ CrudViewInterface<DeliverableEntity> view;

    public DeliverableCrudLogic(/*DeliverableCrudView*/ CrudViewInterface<DeliverableEntity> simpleCrudView) {
        view = simpleCrudView;
    }
    
    public void setProject(Long prj) {
    	if(view!=null && prj!=null){
	        try {
	        	Collection<DeliverableEntity> recs = BltClient.get().executeQuery("/getbyproject/"+prj, 
	        		DeliverableEntity.class,
					EntryPoint.get().getAccessControl().getTokenKey());
	        	view.showRecords(recs);
			} catch (Exception e) {
				e.printStackTrace();
				view.showRecords(null);
				view.showError("Error: "+e.getMessage());
			}
    	}
    }

    public void init() {
        this.editRecord(null);
        // Hide and disable if not administrator, superuser or coordinator
        view.setNewRecordEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER));
    }

    public void cancelRecord() {
        setFragmentParameter("");
        view.clearSelection();
        view.editRecord(null);
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
        page.setUriFragment("!" + view.getName()/*VIEW_NAME*/ + "/" + fragmentParameter, false);
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
                    DeliverableEntity rec = findRecord(pid);
                    view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public DeliverableEntity findRecord(Long recId) {
    	try {
    		DeliverableEntity rec = (DeliverableEntity) BltClient.get().getEntityById(""+recId, 
    			DeliverableEntity.class, 
 				EntryPoint.get().getAccessControl().getTokenKey());
 			return rec;
 		} catch (Exception e) {
 			e.printStackTrace();
 			if(view!=null)
 				view.showError("Error: "+e.getMessage());
 		}
        return null;
    }

    public void saveRecord(DeliverableEntity rec, boolean saveAndNext) {
    	try {
    		DeliverableEntity res = null;
	    	if(rec.getID()==null){
	    		res = (DeliverableEntity) BltClient.get().createEntity(rec, DeliverableEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getName() + " (" + res.getID() + ") created");
	    	}
	    	else{
	    		res = (DeliverableEntity) BltClient.get().updateEntity(rec, DeliverableEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getName() + " (" + res.getID() + ") updated");
	    	}
	    	if(view!=null){
		        view.clearSelection();
		        view.refreshRecord(res);
		        if(saveAndNext){
		        	this.newRecord();
		        }
		        else{
		            view.editRecord(null);
		            setFragmentParameter("");
		        }
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }

    public void deleteRecord(DeliverableEntity rec) {
    	if(rec.getResolved()==true){
    		if(view!=null)
    			view.showSaveNotification("Sorry. A 'Resolved' Deliverable cannot be deleted"); // i18n
    		return;
    	}
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getID(), DeliverableEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result){
				if(view!=null){
					view.showSaveNotification(rec.getName() + " (" + rec.getID() + ") removed");
					view.removeRecord(rec);
				}
			}
			else{
				if(view!=null)
					view.showError("Error: "+rec.getName() + " cannot be removed");
			}
			if(view!=null){
				view.clearSelection();
		        view.editRecord(null);
			}
	        setFragmentParameter("");
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }

    public void editRecord(DeliverableEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getID() + "");
        }
        view.editRecord(rec);
    }

    public void newRecord() {
        view.clearSelection();
        setFragmentParameter("new");
        DeliverableEntity rec = new DeliverableEntity();
        rec.setProject(((DeliverableCrudView)view).getProject());
        view.editRecord(rec);
    }

    public void rowSelected(DeliverableEntity rec) {
        view.editRecord(rec);
    }
    
    public void showSaveNotification(String msg) {
		view.showSaveNotification(msg);
	}

    public Collection<DeliverableEntity> getDeliverablesByProject(Long prj) {
		try {
        	Collection<DeliverableEntity> recs = BltClient.get().executeQuery("/getbyproject/"+prj, 
        		DeliverableEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
        	return recs;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return null;
	}
    
}
