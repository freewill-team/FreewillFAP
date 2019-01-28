package freewill.nextgen.support;

import java.io.Serializable;
import java.util.Collection;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.SupportEntity;

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
public class SupportCrudLogic implements Serializable {

    private SupportCrudView view;

    public SupportCrudLogic(SupportCrudView simpleCrudView) {
        view = simpleCrudView;
    }

    public void init() {
        editRecord(null);
        if(view!=null){
	        try {
	        	Collection<SupportEntity> recs = BltClient.get().getEntities(SupportEntity.class,
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
                    SupportEntity rec = findRecord(pid);
                    view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public SupportEntity findRecord(Long recId) {
    	try {
    		SupportEntity rec = (SupportEntity) BltClient.get().getEntityById(""+recId, 
    			SupportEntity.class, 
				EntryPoint.get().getAccessControl().getTokenKey());
			return rec;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
        return null;
    }

    public void saveRecord(SupportEntity rec, boolean saveAndNext) {
    	try {
    		SupportEntity res = null;
	    	if(rec.getID()==null){
	    		res = (SupportEntity) BltClient.get().createEntity(rec, SupportEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getID() + " created");
	    	}
	    	else{
	    		res = (SupportEntity) BltClient.get().updateEntity(rec, SupportEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null){
	    			view.showSaveNotification(res.getID() + " updated");
	    			//view.clearSelection();
			        //view.editRecord(null);
			        //setFragmentParameter("");
	    		}
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

    public void deleteRecord(SupportEntity rec) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getID(), SupportEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result){
				if(view!=null){
					view.showSaveNotification(rec.getID() + " removed");
					view.removeRecord(rec);
				}
			}
			else{
				if(view!=null)
					view.showError("Error: "+rec.getID() + " cannot be removed");
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

    public void editRecord(SupportEntity rec) {
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
        SupportEntity rec = new SupportEntity();
        rec.setUser(EntryPoint.get().getAccessControl().getUserEntity().getID());
        view.editRecord(rec);
    }

    public void rowSelected(SupportEntity rec) {
    	SupportEntity res = null;
    	if(rec!=null)
    		res = this.findRecord(rec.getID());
        view.editRecord(res);
    }

	public void addComment(SupportEntity rec, String comment) {
		try {
    		SupportEntity res = (SupportEntity) BltClient.get().executeCommand(
    				"addcomment/"+rec.getID()+"/"+comment,
    				SupportEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    	if(view!=null){
	    		view.showSaveNotification(res.getID() + " updated");
			    view.clearSelection();
			    view.refreshRecord(res);
			    view.editRecord(null);
			    System.out.println("RESULT="+res.getID()+" "+res.getComments());
	    	}
	        //setFragmentParameter("");
    	} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
	}

}
