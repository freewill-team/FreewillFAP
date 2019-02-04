package freewill.nextgen.resultados;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ParticipanteEntity;

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
    
    public CategoriaEntity findRecord(String recId) {
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
	
}
