package freewill.nextgen.feature;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Page;
import com.vaadin.data.Item;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CrudLogicInterface;
import freewill.nextgen.data.CrudViewInterface;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.Style.StyleEnum;
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
public class FeatureCrudLogic implements CrudLogicInterface<FeatureEntity> /*Serializable*/ {

    private /*DocumentAdvView*/ CrudViewInterface<FeatureEntity> view;

    public FeatureCrudLogic(/*DocumentAdvView*/ CrudViewInterface<FeatureEntity> simpleCrudView) {
        view = simpleCrudView;
    }

    public void init() {
        editRecord(null);
        view.setNewRecordEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
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
    
    public void showSaveNotification(String msg) {
		view.showSaveNotification(msg);
	}

    public FeatureEntity saveRecord(FeatureEntity rec) {
    	try {
    		FeatureEntity res = null;
	    	if(rec.getID()==null){
	    		res = (FeatureEntity) BltClient.get().createEntity(rec, 
	    			FeatureEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getID() + " created");
	    	}
	    	else{
	    		res = (FeatureEntity) BltClient.get().updateEntity(rec, 
	    			FeatureEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getID() + " updated");
	    	}
	    	if(view!=null){
		        //view.refreshRecord(res);
		        //view.editRecord(null);
		        setFragmentParameter("");
	    	}
	    	return res;
    	} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    	return null;
    }

    public void deleteRecord(FeatureEntity rec) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getID(), FeatureEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result){
				if(view!=null){
					view.showSaveNotification(rec.getID() + " removed");
				}
			}
			else{
				if(view!=null)
					view.showError("Error: "+rec.getID() + " cannot be removed");
			}
			if(view!=null){
				//view.clearSelection();
		        //view.editRecord(null);
			}
	        setFragmentParameter("");
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }

    public void newRecord() {
        //view.clearSelection();
        setFragmentParameter("new");
        //MappingEntity rec = new MappingEntity();
        //view.editRecord(rec);
    }

	public FeatureEntity findRecord(Long recId) {
		try {
			FeatureEntity rec = (FeatureEntity) BltClient.get().getEntityById(""+recId, 
				FeatureEntity.class, 
 				EntryPoint.get().getAccessControl().getTokenKey());
 			return rec;
 		} catch (Exception e) {
 			e.printStackTrace();
 			if(view!=null)
 				view.showError("Error: "+e.getMessage());
 		}
        return null;
	}

	public Collection<FeatureEntity> getByProductFiltered(String text, Long product) {
		try {
        	Collection<FeatureEntity> recs = null;
        	if(product==null || product==0L)
        		recs = BltClient.get().executeQuery(
                		"/getfiltered/"+text, 
                		FeatureEntity.class,
        				EntryPoint.get().getAccessControl().getTokenKey());
        	else
	        	recs = BltClient.get().executeQuery(
		        		"/getbyproductfiltered/"+product+"/"+text, 
		        		FeatureEntity.class,
						EntryPoint.get().getAccessControl().getTokenKey());
        	return recs;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return null;
	}
	
	public Collection<FeatureEntity> getFeaturesFiltered(String text) {
		try {
        	Collection<FeatureEntity> recs = BltClient.get().executeQuery(
        		"/getfiltered/"+text, 
        		FeatureEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
        	return recs;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return null;
	}
	
	public Collection<FeatureEntity> getFeaturesByProduct(Long product) {
		try {
			Collection<FeatureEntity> recs = BltClient.get().executeQuery("/getbyproduct/"+product, 
        		FeatureEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
        	return recs;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return null;
	}
	
	public boolean hasChildDocument(Long recId) {
		try{
			FeatureEntity rec = (FeatureEntity) BltClient.get().executeCommand(
					"/hasChildDocument/"+recId, 
            		FeatureEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
			return (rec.getID()>0);
        }
        catch (Exception e) {
        	e.printStackTrace();
        	if(view!=null)
				view.showError("Error: "+e.getMessage());
        }
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public Container getFeaturesContainer(Long product){
    	IndexedContainer container = new HierarchicalContainer();
    	try{
    		container.addContainerProperty("ID", Long.class, null);
            container.addContainerProperty("Title", String.class, null);
            container.addContainerProperty("Active", Boolean.class, null);
            container.addContainerProperty("Timestamp", Date.class, null);
            //container.addContainerProperty("Level", DocLevelEnum.class, null);
            //container.addContainerProperty("Description", String.class, null);
    		
            List<FeatureEntity> recs = (List<FeatureEntity>) this.getFeaturesByProduct(product);
    		
    		for(FeatureEntity rec:recs){
    			if(rec.getLevel().ordinal()>StyleEnum.H8.ordinal()) continue;
    			Item item = container.addItem(rec.getID());
    			item.getItemProperty("ID").setValue(rec.getID());
    			item.getItemProperty("Title").setValue(rec.getTitle());
    			item.getItemProperty("Active").setValue(rec.getActive());
    			item.getItemProperty("Timestamp").setValue(rec.getTimestamp());
    			//item.getItemProperty("Level").setValue(rec.getLevel());
    			//item.getItemProperty("Description").setValue(rec.getDescription());
    			if(rec.getParent()!=null && rec.getParent()!=0){
    				((Hierarchical) container).setParent(rec.getID(), rec.getParent());
    			}
    		}
    		
    		return container;
        }
        catch (Exception e) {
        	e.printStackTrace();
        	throw new IllegalArgumentException("Fail to retrieve Records from Database");
        }
    }

	@Override
	public void cancelRecord() {
		// unused
	}

	@Override
	public void enter(String recId) {
		// unused
	}
	
	@Override
	public void saveRecord(FeatureEntity rec, boolean saveAndNext) {
		// unused
	}

	@Override
	public void editRecord(FeatureEntity rec) {
		// unused
	}

	@Override
	public void rowSelected(FeatureEntity rec) {
		// unused
	}
	
}
