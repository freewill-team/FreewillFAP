package freewill.nextgen.requirement;

import java.util.Collection;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CrudLogicInterface;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.Requirement2Entity;
import freewill.nextgen.data.RequirementEntity;
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
public class RequirementCrudLogic implements CrudLogicInterface<RequirementEntity> /*Serializable*/ {

    private RequirementCrudView view;

    public RequirementCrudLogic(RequirementCrudView simpleCrudView) {
        view = simpleCrudView;
    }
    
    public void setProject(Long prj) {
    	if(view!=null && prj!=null){
	        try {
	        	Collection<Requirement2Entity> recs = BltClient.get().executeQuery("/getbyproject/"+prj,
		        		Requirement2Entity.class,
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
        editRecord(null);
        view.setNewRecordEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER));
    }

    public void cancelRecord() {
        setFragmentParameter("");
        view.clearSelection();
        view.editRecord((RequirementEntity)null, null);
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
                    /*long pid = Long.parseLong(recId);
                    RequirementEntity rec = findRecord(pid);
                    if(view!=null)
                    	view.selectRow(rec);*/
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public RequirementEntity findRecord(Long recId) {
    	try {
    		RequirementEntity rec = (RequirementEntity) BltClient.get().getEntityById(""+recId, 
    			RequirementEntity.class, 
 				EntryPoint.get().getAccessControl().getTokenKey());
 			return rec;
 		} catch (Exception e) {
 			e.printStackTrace();
 			if(view!=null)
 				view.showError("Error: "+e.getMessage());
 		}
        return null;
    }

    public void saveRecord(RequirementEntity rec, Requirement2Entity rec2, boolean saveAndNext) {
    	try {
    		RequirementEntity res = null;
	    	if(rec.getID()==null){
	    		res = (RequirementEntity) BltClient.get().createEntity(rec, RequirementEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getCustomid() + " (" + res.getID() + ") created");
	    	}
	    	else{
	    		res = (RequirementEntity) BltClient.get().updateEntity(rec, RequirementEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getCustomid() + " (" + res.getID() + ") updated");
	    	}
	    	if(view!=null){
	    		if(rec2==null){
	    			rec2 = new Requirement2Entity();
	    			rec2.setId(res.getID());
	    		}
	    		rec2.setCustomid(res.getCustomid());
	    		rec2.setDescription(res.getDescription());
	    		rec2.setProject(res.getProject());
	    		rec2.setCompany(res.getCompany());
	    		rec2.setResolved(res.getResolved());
	    		if(res.getAssignedto()!=null && res.getAssignedto()>0L){
	    			UserEntity user = (UserEntity) BltClient.get().getEntityById(
	    				""+res.getAssignedto(), UserEntity.class, 
						EntryPoint.get().getAccessControl().getTokenKey());
	    			rec2.setUser(user.getName());
	    		}
	    		else
	    			rec2.setUser("");
		        view.clearSelection();
		        view.refreshRecord(rec2);
		        if(saveAndNext){
		        	this.newRecord();
		        }
		        else{
		            view.editRecord((RequirementEntity)null, rec2);
		            setFragmentParameter("");
		        }
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }

    public void deleteRecord(Requirement2Entity rec) {
    	if(rec.getResolved()==true){
    		view.showSaveNotification("Sorry. A 'Resolved' Requirement cannot be deleted");
    		return;
    	}
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getId(), RequirementEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result){
				if(view!=null){
					view.showSaveNotification(rec.getCustomid() + " (" + rec.getId() + ") removed");
					view.removeRecord(rec);
				}
			}
			else{
				if(view!=null)
					view.showError("Error: "+rec.getCustomid() + " cannot be removed");
			}
			if(view!=null){
				view.clearSelection();
		        view.editRecord((RequirementEntity)null, null);
			}
	        setFragmentParameter("");
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }
    
    public void deleteRecord(RequirementEntity rec) {
    	if(rec.getResolved()==true){
    		view.showSaveNotification("Sorry. A 'Resolved' Requirement cannot be deleted");
    		return;
    	}
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getID(), RequirementEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result){
				if(view!=null){
					view.showSaveNotification(rec.getCustomid() + " (" + rec.getID() + ") removed");
					view.removeRecord(rec);
				}
			}
			else{
				if(view!=null)
					view.showError("Error: "+rec.getCustomid() + " cannot be removed");
			}
			if(view!=null){
				view.clearSelection();
		        view.editRecord((RequirementEntity)null, null);
			}
	        setFragmentParameter("");
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }

    public void editRecord(RequirementEntity rec, Requirement2Entity rec2) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getID() + "");
        }
        view.editRecord(rec, rec2);
    }

    public void newRecord() {
        view.clearSelection();
        setFragmentParameter("new");
        RequirementEntity rec = new RequirementEntity();
        rec.setProject(view.getProject());
        view.editRecord(rec, null);
    }

    public void rowSelected(Requirement2Entity rec) {
    	if(rec!=null){
    		RequirementEntity req = this.findRecord(rec.getId());
    		if(req!=null && req.getID()!=null)
    			view.editRecord(req, rec);
    	}
    	else
    		view.editRecord((RequirementEntity)null, rec);
    }

	public void Assignedto(Requirement2Entity rec, UserEntity user, ProjectEntity project) {
		try{
			if(rec==null || user==null || project==null) return;
			RequirementEntity req = this.findRecord(rec.getId());
			
			boolean result = BltClient.get().executeCommand("/assignto/"+user.getID(),
					req,
					RequirementEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result){
				if(view!=null){
					rec.setUser(user.getName());
					view.refreshRecord(rec);
					view.showSaveNotification(rec.getCustomid()+" assigned to "+user.getName());
				}
			}
			else{
				if(view!=null)
					view.showError("Error: "+rec.getCustomid() + " cannot be assigned");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
	}

	public Collection<RequirementEntity> getRequirementsByProject(Long prj) {
	    try {
	        Collection<RequirementEntity> recs = BltClient.get().executeQuery("/getbyproject/"+prj, 
	        	RequirementEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	        return recs;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
	    return null;
    }

	@Override
	public void rowSelected(RequirementEntity rec) {
		// unused
	}

	@Override
	public void saveRecord(RequirementEntity rec, boolean saveAndNext) {
		try {
    		RequirementEntity res = null;
	    	if(rec.getID()==null){
	    		res = (RequirementEntity) BltClient.get().createEntity(rec, RequirementEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getCustomid() + " (" + res.getID() + ") created");
	    	}
	    	else{
	    		res = (RequirementEntity) BltClient.get().updateEntity(rec, RequirementEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getCustomid() + " (" + res.getID() + ") updated");
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
	}

	@Override
	public void editRecord(RequirementEntity rec) {
		// unused
	}
	
}
