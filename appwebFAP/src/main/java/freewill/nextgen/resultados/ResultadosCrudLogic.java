package freewill.nextgen.resultados;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ParejaJamEntity;
import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.data.PatinadorEntity;

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
public class ResultadosCrudLogic implements Serializable {

    private ResultadosActaFinal view;
    private Logger log = null;

    public ResultadosCrudLogic(ResultadosActaFinal simpleCrudView) {
        view = simpleCrudView;
        if(view!=null)
        	log = Logger.getLogger(view.getClass());
        else
        	log = Logger.getLogger(this.getClass());
    }

    public void initGrid(Long competicion, Long categoria) {
    	try{
	        if(view!=null){
	        	List<ParticipanteEntity> records = (List<ParticipanteEntity>) 
	        			BltClient.get().executeQuery(
	        			"/getResultados/"+competicion+"/"+categoria,
	        			ParticipanteEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        	view.showRecords(records);
	        	this.setFragmentParameter("Ranking");
	        }
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
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

	public CompeticionEntity findLastCompeticion() {
		try{
			return (CompeticionEntity) BltClient.get().executeCommand(
        			"/getLastCompeticion", CompeticionEntity.class,
        			EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
		return null;
	}
    
	public CompeticionEntity findCompeticion(String recId) {
    	try{
    		CompeticionEntity rec = (CompeticionEntity) BltClient.get().getEntityById(
    				recId, CompeticionEntity.class,
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
	
	public void cancelRecord() {
        setFragmentParameter("");
        if(view!=null){
	        view.clearSelection();
	        view.editRecord(null);
        }
    }
	
	public ParticipanteEntity saveRecord(ParticipanteEntity rec) {
    	try{
	    	if(view!=null){
	    		ParticipanteEntity res = null;
	    		System.out.println("Saving = "+rec.toString());
	    		
	    		if(rec.getId()==null)
	        		res = (ParticipanteEntity) BltClient.get().createEntity(rec, ParticipanteEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	        	else
	        		res = (ParticipanteEntity) BltClient.get().updateEntity(rec, ParticipanteEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	    		
		        view.showSaveNotification(rec.getNombre() + " (" + res.getId() + ") updated");
		        view.clearSelection();
		        view.editRecord(null);
		        view.refreshRecord(res);
		        return res;
	    	}
	        //setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
    	return null;
    }
	
	public void deleteRecord(ParticipanteEntity rec) {
    	try{
	    	if(view!=null){
		    	System.out.println("Deleting = "+rec.toString());
		    	BltClient.get().deleteEntity(""+rec.getId(), ParticipanteEntity.class,
		    			EntryPoint.get().getAccessControl().getTokenKey());
		        view.showSaveNotification(rec.getNombre() + " (" + rec.getId() + ") removed");
		        view.clearSelection();
		        view.editRecord(null);
	        }
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
    }
	
	public void editRecord(ParticipanteEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(view!=null)
        	view.editRecord(rec);
    }
	
    public void rowSelected(ParticipanteEntity rec) {
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN)){
            view.editRecord(rec);
        }
    }

	public List<ParejaJamEntity> getParejasJam() {
		try{
	        return BltClient.get().getEntities(ParejaJamEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
	}
    
}
