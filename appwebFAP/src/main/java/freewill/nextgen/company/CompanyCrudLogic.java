package freewill.nextgen.company;

import java.util.Collection;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.data.CrudLogicInterface;

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
public class CompanyCrudLogic implements CrudLogicInterface /*Serializable*/ {

    private CompanyCrudView view;

    public CompanyCrudLogic(CompanyCrudView simpleCrudView) {
        view = simpleCrudView;
    }

    public void init() {
        editRecord(null);
        if(view!=null){
	        // Hide and disable if not superuser
	        view.setNewRecordEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
	        try {
	        	Collection<CompanyEntity> recs = BltClient.get().getEntities(CompanyEntity.class,
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
    public void setFragmentParameter(String recId) {
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
                    long pid = Long.parseLong(recId);
                    CompanyEntity rec = findRecord(pid);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public CompanyEntity findRecord(Long company) {
        try {
        	CompanyEntity rec = (CompanyEntity) BltClient.get().getEntityById(""+company, 
        		CompanyEntity.class, 
				EntryPoint.get().getAccessControl().getTokenKey());
			return rec;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
        return null;
    }

    public void saveRecord(CompanyEntity rec) {
        try {
        	CompanyEntity res = null;
	    	if(rec.getID()==null){
	    		res = (CompanyEntity) BltClient.get().createEntity(rec, CompanyEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getName() + " (" + res.getID() + ") created");
	    	}
	    	else{
	    		res = (CompanyEntity) BltClient.get().updateEntity(rec, CompanyEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
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

    public void deleteRecord(CompanyEntity rec) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getID(), CompanyEntity.class, 
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

    public void editRecord(CompanyEntity rec) {
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
        view.editRecord(new CompanyEntity());
    }

    public void rowSelected(CompanyEntity rec) {
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER)) {
            view.editRecord(rec);
        }
    }

	public long countActiveUsers() {
		try {
			UserEntity rec = (UserEntity) BltClient.get().executeCommand(
				"/countActiveUsers", UserEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return rec.getID();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
}
