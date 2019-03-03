package freewill.nextgen.ranking;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.data.RankingEntity;

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
public class RankingCrudLogic implements Serializable {

    private RankingActaFinal view;
    private Logger log = null;

    public RankingCrudLogic(RankingActaFinal simpleCrudView) {
        view = simpleCrudView;
        if(view!=null)
        	log = Logger.getLogger(view.getClass());
        else
        	log = Logger.getLogger(this.getClass());
    }

    public void initGrid(Long circuito, Long categoria) {
    	try{
	        if(view!=null){
	        	List<RankingEntity> records = (List<RankingEntity>) 
	        			BltClient.get().executeQuery(
	        			"/getByCircuitoAndCategoria/"+circuito+"/"+categoria,
	        			RankingEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        	view.showRecords(records);
	        	this.setFragmentParameter("Ranking");
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
                	//RankingEntity rec = findRecord(recId);
                    //if(view!=null)
                    //	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public RankingEntity findRecord(String recId) {
    	try{
    		RankingEntity rec = (RankingEntity) BltClient.get().getEntityById(
    				recId, RankingEntity.class,
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
    
    public void saveRecord(RankingEntity rec) {
    	try {
    		RankingEntity res = null;
	    	if(rec.getId()==null){
	    		res = (RankingEntity) BltClient.get().createEntity(rec, RankingEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
				view.showSaveNotification(res.getNombre() + " (" + res.getId() + ") created");
	    	}
	    	else{
	    		res = (RankingEntity) BltClient.get().updateEntity(rec, RankingEntity.class, 
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

    public void deleteRecord(RankingEntity rec) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getId(), RankingEntity.class, 
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
    
    public void editRecord(RankingEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(view!=null)
        	view.editRecord(rec);
    }
    
    public void rowSelected(RankingEntity rec) {
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN)){
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
    
	public CategoriaEntity findCategoria(String recId) {
    	try{
    		CategoriaEntity rec = (CategoriaEntity) BltClient.get().getEntityById(
    				recId, CategoriaEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		return rec;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
    	return null;
    }
	
}
