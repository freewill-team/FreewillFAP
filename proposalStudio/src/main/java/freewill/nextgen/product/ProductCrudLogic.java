package freewill.nextgen.product;

import java.util.Collection;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CrudLogicInterface;
import freewill.nextgen.data.CrudViewInterface;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.ProductEntity;
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
public class ProductCrudLogic implements CrudLogicInterface<ProductEntity> /*Serializable*/ {

    private CrudViewInterface<ProductEntity> view;

    public ProductCrudLogic(CrudViewInterface<ProductEntity> simpleCrudView) {
        view = simpleCrudView;
    }

    public void init() {
        editRecord(null);
        if(view!=null){
       	 	// Hide and disable if not administrator, superuser or coordinator
            view.setNewRecordEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
	        try {
	        	Collection<ProductEntity> recs = BltClient.get().getEntities(ProductEntity.class,
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
                    ProductEntity rec = findRecord(pid);
                    view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public ProductEntity findRecord(Long recId) {
    	try {
    		ProductEntity rec = (ProductEntity) BltClient.get().getEntityById(""+recId, 
    				ProductEntity.class, 
				EntryPoint.get().getAccessControl().getTokenKey());
			return rec;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
        return null;
    }

    public void saveRecord(ProductEntity rec, boolean saveAndNext) {
    	try {
    		ProductEntity res = null;
	    	if(rec.getID()==null){
	    		res = (ProductEntity) BltClient.get().createEntity(rec, ProductEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getName() + " (" + res.getID() + ") created");
	    	}
	    	else{
	    		res = (ProductEntity) BltClient.get().updateEntity(rec, ProductEntity.class, 
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

    public void deleteRecord(ProductEntity rec) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getID(), ProductEntity.class, 
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

    public void editRecord(ProductEntity rec) {
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
        ProductEntity rec = new ProductEntity();
        view.editRecord(rec);
    }

    public void rowSelected(ProductEntity rec) {
    	if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD)){
    		view.editRecord(rec);
        }
    }

	public long countFeaturesPerProduct(long prd) {
		try {
			FeatureEntity rec = (FeatureEntity) BltClient.get().executeCommand(
				"/countFeaturesPerProduct/"+prd, FeatureEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
		    return rec.getID();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
    
}
