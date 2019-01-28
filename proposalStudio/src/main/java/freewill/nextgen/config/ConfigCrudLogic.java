package freewill.nextgen.config;

import java.io.Serializable;
import java.util.Collection;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.ConfigEntity;
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
public class ConfigCrudLogic implements Serializable {

    private ConfigCrudView view;

    public ConfigCrudLogic(ConfigCrudView simpleCrudView) {
        view = simpleCrudView;
    }
    
    public void init() {
        editRecord(null);
        view.setNewRecordEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        try {
        	Collection<ConfigEntity> recs = BltClient.get().getEntities(ConfigEntity.class,
					EntryPoint.get().getAccessControl().getTokenKey());
        	view.showRecords(recs);
		} catch (Exception e) {
			e.printStackTrace();
			view.showRecords(null);
			view.showError("Error: "+e.getMessage());
		}
    }

    public void cancelRecord() {
        setFragmentParameter("");
        view.clearSelection();
        view.editRecord(null);
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
        page.setUriFragment("!" + view.VIEW_NAME + "/" + fragmentParameter, false);
    }

    public void enter(String recId) {
        if (recId != null && !recId.isEmpty()) {
            if (recId.equals("new")) {
                newRecord();
            } else {
                // Ensure this is selected even if coming directly here from login
                try {
                    int pid = Integer.parseInt(recId);
                    ConfigEntity rec = findRecord(pid);
                    view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private ConfigEntity findRecord(int recId) {
		try {
			ConfigEntity rec = (ConfigEntity) BltClient.get().getEntityById(""+recId, 
				ConfigEntity.class, 
				EntryPoint.get().getAccessControl().getTokenKey());
			return rec;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
        return null;
    }

    public void saveRecord(ConfigEntity rec) {
    	try {
    		ConfigEntity res = null;
	    	if(rec.getID()==null){
	    		res = (ConfigEntity) BltClient.get().createEntity(rec, ConfigEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getName() + " (" + res.getID() + ") created");
	    	}
	    	else{
	    		res = (ConfigEntity) BltClient.get().updateEntity(rec, ConfigEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getName() + " (" + res.getID() + ") updated");
	    	}
	        view.clearSelection();
	        view.editRecord(null);
	        view.refreshRecord(res);
	        setFragmentParameter("");
    	} catch (Exception e) {
			e.printStackTrace();
			view.showError("Error: "+e.getMessage());
		}
    }

    public void deleteRecord(ConfigEntity rec) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getID(), ConfigEntity.class, 
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

    public void editRecord(ConfigEntity rec) {
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
        ConfigEntity rec = new ConfigEntity();
        view.editRecord(rec);
    }

    public void rowSelected(ConfigEntity rec) {
        view.editRecord(rec);
    }

	public void showSaveNotification(String msg) {
		view.showSaveNotification(msg);
	}
}
