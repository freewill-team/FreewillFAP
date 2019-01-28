package freewill.nextgen.user;

import java.io.Serializable;
import java.util.Collection;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;

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
public class UserCrudLogic implements Serializable {

    private UserCrudView view;

    public UserCrudLogic(UserCrudView simpleCrudView) {
        view = simpleCrudView;
    }

    public void init() {
        editRecord(null);
        if(view!=null){
        	 // Hide and disable if not administrator or superuser
            view.setNewRecordEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
	        try {
	        	Collection<UserEntity> recs = BltClient.get().getEntities(UserEntity.class,
						EntryPoint.get().getAccessControl().getTokenKey());
	        	view.showRecords(recs);
			} catch (Exception e) {
				e.printStackTrace();
				view.showRecords(null);
				view.showError("Error: "+e.getMessage());
			}
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
                    int pid = Integer.parseInt(recId);
                    UserEntity rec = findRecord(pid);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private UserEntity findRecord(int recId) {
    	try {
        	UserEntity rec = (UserEntity) BltClient.get().getEntityById(""+recId, 
        			UserEntity.class, 
				EntryPoint.get().getAccessControl().getTokenKey());
			return rec;
		} catch (Exception e) {
			e.printStackTrace();
			view.showError("Error: "+e.getMessage());
		}
        return null;
    }

    public void saveRecord(UserEntity rec) {
    	try {
    		UserEntity res = null;
	    	if(rec.getID()==null){
	    		res = (UserEntity) BltClient.get().createEntity(rec, UserEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
				view.showSaveNotification(res.getName() + " (" + res.getID() + ") created");
	    	}
	    	else{
	    		res = (UserEntity) BltClient.get().updateEntity(rec, UserEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		view.showSaveNotification(res.getName() + " (" + res.getID() + ") updated");
	    	}
	    	if(view!=null){
		        view.clearSelection();
		        view.editRecord(null);
		        view.refreshRecord(res);
	    	}
	        setFragmentParameter("");
    	} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }

    public void deleteRecord(UserEntity rec) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getID(), UserEntity.class, 
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

    public void editRecord(UserEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getID() + "");
        }
        if(view!=null)
        	view.editRecord(rec);
    }

    public void newRecord() {
        view.clearSelection();
        setFragmentParameter("new");
        UserEntity rec = new UserEntity();
        view.editRecord(rec);
    }

    public void rowSelected(UserEntity rec) {
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN)){
            view.editRecord(rec);
        }
    }
}
