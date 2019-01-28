package freewill.nextgen.file;

import java.util.Collection;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CrudLogicInterface;
import freewill.nextgen.data.CrudViewInterface;
import freewill.nextgen.data.FileEntity;
import freewill.nextgen.data.ReportInfo;
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
public class FileCrudLogic implements CrudLogicInterface<FileEntity> /*Serializable*/ {

    private /*FileCrudView*/ CrudViewInterface<FileEntity> view;

    public FileCrudLogic(/*FileCrudView*/ CrudViewInterface<FileEntity> simpleCrudView) {
        view = simpleCrudView;
    }
    
    public void setProject(Long prj) {
    	if(view!=null){
	        try {
	        	Collection<FileEntity> recs = BltClient.get().executeQuery("/getbyproject/"+prj, 
	        		FileEntity.class,
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
                    FileEntity rec = findRecord(pid);
                    view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public FileEntity findRecord(Long recId) {
    	try {
    		FileEntity rec = (FileEntity) BltClient.get().getEntityById(""+recId, 
    			FileEntity.class, 
 				EntryPoint.get().getAccessControl().getTokenKey());
 			return rec;
 		} catch (Exception e) {
 			e.printStackTrace();
 			if(view!=null)
 				view.showError("Error: "+e.getMessage());
 		}
        return null;
    }

    public void saveRecord(FileEntity rec, boolean saveAndNext) {
    	try {
    		FileEntity res = null;
	    	if(rec.getID()==null){
	    		res = (FileEntity) BltClient.get().createEntity(rec, FileEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getName() + " (" + res.getID() + ") created");
	    	}
	    	else{
	    		res = (FileEntity) BltClient.get().updateEntity(rec, FileEntity.class, 
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

    public void deleteRecord(FileEntity rec) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getID(), FileEntity.class, 
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

    public void editRecord(FileEntity rec) {
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
        FileEntity rec = new FileEntity();
        rec.setProject(((FileCrudView)view).getProject());
        view.editRecord(rec);
    }

    public void rowSelected(FileEntity rec) {
        view.editRecord(rec);
    }

	public void showSaveNotification(String msg) {
		view.showSaveNotification(msg);
	}

	public void saveRecord(ReportInfo rec) {
		try {
			ReportInfo rep = (ReportInfo) BltClient.get().createEntity(rec, ReportInfo.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			
			// returned record "rep" contains new FileEntity object
			FileEntity res = findRecord(rep.getId());
			
	    	if(view!=null){
	    		view.showSaveNotification(res.getName() + " (" + res.getID() + ") created");
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
	
}
