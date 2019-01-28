package freewill.nextgen.styles;

import java.util.Collection;
import java.util.List;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.Style;
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
public class StylesCrudLogic implements CrudLogicInterface<Style> {

    private CrudViewInterface<Style> view;

	public StylesCrudLogic(CrudViewInterface<Style> simpleCrudView) {
        view = simpleCrudView;
    }

    public void init() {
        editRecord(null);
        if(view!=null){
	        // Hide and disable if not superuser
	        view.setNewRecordEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
	        try {
	        	Collection<Style> recs = BltClient.get().getEntities(Style.class,
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
                    Style rec = findRecord(pid);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public Style findRecord(Long company) {
        try {
        	Style rec = (Style) BltClient.get().getEntityById(""+company, 
        		Style.class, 
				EntryPoint.get().getAccessControl().getTokenKey());
			return rec;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
        return null;
    }

    public void saveRecord(Style rec, boolean saveAndNext) {
        try {
        	Style res = null;
	    	if(rec.getId()==null){
	    		res = (Style) BltClient.get().createEntity(rec, Style.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getName() + " (" + res.getId() + ") created");
	    	}
	    	else{
	    		res = (Style) BltClient.get().updateEntity(rec, Style.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getName() + " (" + res.getId() + ") updated");
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

    public void deleteRecord(Style rec) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getId(), Style.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result){
				if(view!=null){
					view.showSaveNotification(rec.getName() + " (" + rec.getId() + ") removed");
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

    public void editRecord(Style rec) {
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
        view.editRecord(new Style());
    }

    public void rowSelected(Style rec) {
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER)) {
            view.editRecord(rec);
        }
    }

    public void saveStyles(List<Style> styleList) {
		// update or save new styles
		if(styleList==null) return;
		try {
			BltClient.get().executeCommand(
				"/deleteall", Style.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	for(Style rec:styleList){
	       		BltClient.get().createEntity(rec, Style.class,
	    				EntryPoint.get().getAccessControl().getTokenKey());
	       	}
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
	}
	
}
