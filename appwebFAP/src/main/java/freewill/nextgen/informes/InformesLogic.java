package freewill.nextgen.informes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ParticipanteEntity;
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
public class InformesLogic implements Serializable {

    private InformesView view;
    private Logger log = null;

    public InformesLogic(InformesView simpleCrudView) {
        view = simpleCrudView;
        log = Logger.getLogger(view.getClass());
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    @SuppressWarnings("unused")
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
                	//ParticipanteEntity rec = findRecord(recId);
                    //if(view!=null)
                    //	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    public ParticipanteEntity findRecord(String recId) {
    	try{
    		ParticipanteEntity rec = (ParticipanteEntity) BltClient.get().getEntityById(
    				recId, ParticipanteEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		return rec;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
    	return null;
    }
    
    public String[] getCategoriasStr() {
    	try{
    		List<CategoriaEntity> categorias = getCategorias();
    		String[] data = new String[categorias.size()];
    		int i = 0;
    		for(CategoriaEntity cat:categorias){
    			data[i++] = cat.getNombre();
    		}
    		return data;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
    }
    
    public List<CategoriaEntity> getCategorias() {
    	try{
    		return BltClient.get().getEntities(
            		CategoriaEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
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

	public List<ParticipanteEntity>[] getParticipantes(Long competicion) {
		try{
			List<CategoriaEntity> categorias = getCategorias();
			int count = categorias.size();
			@SuppressWarnings("unchecked")
			List<ParticipanteEntity>[] data = new ArrayList[count];
			
			int i = 0;
			for(CategoriaEntity categoria:categorias){
				List<ParticipanteEntity> recs = BltClient.get().executeQuery(
						"/getResultados/"+competicion+"/"+categoria.getId(),
	        			ParticipanteEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
				data[i++] = recs;
			}
			return data;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
	}
	
	public List<ParticipanteEntity> getResultados(Long competicion, Long categoria) {
		try{
			return BltClient.get().executeQuery(
					"/getResultados/"+competicion+"/"+categoria,
        			ParticipanteEntity.class,
        			EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
	}

	public List<CompeticionEntity> getCompeticiones(Long circuito) {
		try{
			return BltClient.get().executeQuery(
					"/getCompeticiones/"+circuito,
					CompeticionEntity.class,
        			EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
	}
	
	public List<CategoriaEntity> getCategorias(ModalidadEnum modalidad) {
    	try{
    		return BltClient.get().executeQuery(
    	     		"/getByModalidad/"+modalidad.name(), CategoriaEntity.class,
    	     		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
    }
	
	public List<ParticipanteEntity> getMejoresMarcas(Long categoria, String sortby) {
		try{
			return BltClient.get().executeQuery(
					"/getMejoresMarcas/"+categoria+"/"+sortby,
        			ParticipanteEntity.class,
        			EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
	}

	public long getCountParticipantes(Long competicion, Long categoria) {
		try{
        	ParticipanteEntity rec = (ParticipanteEntity) BltClient.get().executeCommand(
	        		"/countByCompeticionAndCategoria/"+competicion+"/"+categoria, 
	        		ParticipanteEntity.class, EntryPoint.get().getAccessControl().getTokenKey());
        	if(rec!=null)
        		return rec.getId();
        }
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return 0;
	}
	
	public List<CategoriaEntity> getInscripciones(Long competicion) {
    	try{
    		// Devuelve todos las categorias con datos de inscripciones
    		return BltClient.get().executeQuery("/getByCompeticion/"+competicion,
		        	CategoriaEntity.class,
		        	EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
    }

	public List<RankingEntity> getRankingCircuito(Long circuito, Long categoria) {
		try{
			return BltClient.get().executeQuery(
        			"/getByCircuitoAndCategoria/"+circuito+"/"+categoria,
        			RankingEntity.class,
        			EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
	}
	
	public List<RankingEntity>[] getRankingCircuito(Long circuito) {
		try{
			List<CategoriaEntity> categorias = getCategorias();
			int count = categorias.size();
			@SuppressWarnings("unchecked")
			List<RankingEntity>[] data = new ArrayList[count];
			
			int i = 0;
			for(CategoriaEntity categoria:categorias){
				List<RankingEntity> recs = BltClient.get().executeQuery(
			        	"/getByCircuitoAndCategoria/"+circuito+"/"+categoria.getId(),
			        	RankingEntity.class,
			        	EntryPoint.get().getAccessControl().getTokenKey());
				data[i++] = recs;
			}
			return data;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
	}
	
}
