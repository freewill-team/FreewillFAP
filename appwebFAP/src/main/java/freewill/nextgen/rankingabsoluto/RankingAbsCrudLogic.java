package freewill.nextgen.rankingabsoluto;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.data.RankingAbsEntity;

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
public class RankingAbsCrudLogic implements Serializable {

    private RankingAbsActaFinal view;
    private Logger log = null;

    public RankingAbsCrudLogic(RankingAbsActaFinal simpleCrudView) {
        view = simpleCrudView;
        if(view!=null)
        	log = Logger.getLogger(view.getClass());
        else
        	log = Logger.getLogger(this.getClass());
    }

    public void initGrid(ModalidadEnum modalidad) {
    	try{
	        if(view!=null){
	        	List<RankingAbsEntity> records = (List<RankingAbsEntity>) 
	        			BltClient.get().executeQuery(
	        			"/getByModalidad/"+modalidad.name(),
	        			RankingAbsEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        	view.showRecords(records);
	        	this.setFragmentParameter("RankingAbs");
	        }
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
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
                //newRecord();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                	//RankingAbsEntity rec = findRecord(recId);
                    //if(view!=null)
                    //	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public RankingAbsEntity findRecord(String recId) {
    	try{
    		RankingAbsEntity rec = (RankingAbsEntity) BltClient.get().getEntityById(
    				recId, RankingAbsEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		return rec;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
    	return null;
    }
    
    public void cancelRecord() {
        setFragmentParameter("");
        if(view!=null){
	        view.clearSelection();
	        view.editRecord(null);
        }
    }
    
    public void saveRecord(RankingAbsEntity rec) {
    	try {
    		RankingAbsEntity res = null;
	    	if(rec.getId()==null){
	    		res = (RankingAbsEntity) BltClient.get().createEntity(rec, RankingAbsEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
				view.showSaveNotification(res.getNombre() + " (" + res.getId() + ") created");
	    	}
	    	else{
	    		res = (RankingAbsEntity) BltClient.get().updateEntity(rec, RankingAbsEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		view.showSaveNotification(res.getNombre() + " (" + res.getId() + ") updated");
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

    public void deleteRecord(RankingAbsEntity rec) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getId(), RankingAbsEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result){
				if(view!=null){
					view.showSaveNotification(rec.getNombre() + " (" + rec.getId() + ") removed");
					view.removeRecord(rec);
				}
			}
			else{
				if(view!=null)
					view.showError("Error: "+rec.getNombre() + " cannot be removed");
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
    
    public void editRecord(RankingAbsEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(view!=null)
        	view.editRecord(rec);
    }
    
    public void rowSelected(RankingAbsEntity rec) {
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD)){
            view.editRecord(rec);
        }
    }

	public List<PatinadorEntity> getPatinadores() {
		try{
	        return BltClient.get().getEntities(PatinadorEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
	}
	
}
