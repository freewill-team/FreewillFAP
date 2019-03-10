package freewill.nextgen.preinscripcion;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.preinscripcion.PreinscripcionCrudView.InscripcionEnum;

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
public class PreinscripcionCrudLogic implements Serializable {

    private PreinscripcionCrudView view;
    private Logger log = null;

    public PreinscripcionCrudLogic(PreinscripcionCrudView simpleCrudView) {
        view = simpleCrudView;
        if(view!=null)
        	log = Logger.getLogger(view.getClass());
        else
        	log = Logger.getLogger(this.getClass());
    }

    public void init(Long competicion, InscripcionEnum tipoForm) {
    	try{
    		editRecord(null, null, false);
	        if(view!=null){
	        	Date now = new Date();
	        	CompeticionEntity competi = (CompeticionEntity) BltClient.get().getEntityById(
	        			""+competicion, CompeticionEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        	
	        	if(tipoForm == InscripcionEnum.PREINSCRIPCION){
	        		// Solo devuelve los patinadores correspondientes al club del coordinador
	        		// El usuario logueado debe se el coordinador del club
	        		view.showRecords(
	        			BltClient.get().executeQuery("/getInscripcionesbyclub/"+competicion,
	        			PatinadorEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey()));
	        		// Comprueba que la pre-inscripción está abierta
	        		if(competi==null || competi.getFechaFinInscripcion().before(now)){
		        		view.showError("La Pre-Inscripción ya está cerrada.");
		        		view.setPreinscripcionAbierta(false);
		        	}
	        	}
	        	else{
	        		// Devuelve todos los patinadores
	        		view.showRecords(
		        		BltClient.get().executeQuery("/getInscripciones/"+competicion+"/true",
		        		PatinadorEntity.class,
		        		EntryPoint.get().getAccessControl().getTokenKey()));
	        		// Comprueba que la cmpeticion nomha empezado ain
	        		if(competi==null || competi.getFechaInicio().before(now)){
		        		view.showError("La Inscripción ya está cerrada.");
		        		view.setPreinscripcionAbierta(false);
		        	}
	        	}
	        }
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null){
				view.showError(e.getMessage());
				view.setPreinscripcionAbierta(false);
			}
		}
    }

    public void cancelRecord() {
        setFragmentParameter("");
        if(view!=null){
	        view.clearSelection();
	        view.editRecord(null, null, false);
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
                    //int pid = Integer.parseInt(recId);
                    //ParticipanteEntity rec = findRecord(pid);
                	PatinadorEntity rec = findRecord(recId);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private PatinadorEntity findRecord(String recId) {
    	try{
    		PatinadorEntity rec = (PatinadorEntity) BltClient.get().getEntityById(
    				recId, PatinadorEntity.class,
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
    
    public Collection<ParticipanteEntity> getInscripciones(Long patinador, Long competicion) {
    	try{
	        return BltClient.get().executeQuery(
	        		"/getByPatinadorAndCompeticion/"+patinador+"/"+competicion,
	        		ParticipanteEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
		return null;
    }

    public void editRecord(PatinadorEntity rec, CompeticionEntity competicion, boolean preinscripcionAbierta) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(view!=null)
        	view.editRecord(rec, competicion, preinscripcionAbierta);
    }

    public void rowSelected(PatinadorEntity rec, CompeticionEntity competi, boolean preinscripcionAbierta) {
    	view.editRecord(rec, competi, preinscripcionAbierta);
    }
    
    public CategoriaEntity getCategoria(Long id) {
    	try{
    		return (CategoriaEntity) BltClient.get().getEntityById(
    				""+id, CategoriaEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
		return null;
    }
    
    public Collection<CategoriaEntity> getCategorias() {
    	try{
    		return BltClient.get().getEntities(CategoriaEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
		return null;
    }

	public boolean saveRecord(PatinadorEntity rec, CompeticionEntity competicion, boolean preinscripcionAbierta) {
		try{
	    	if(view!=null){
	    		System.out.println("Saving = "+rec.toString());
	    		
	    		BltClient.get().executeCommand(
        				"/updateInscripcion/"+competicion.getId(), rec, PatinadorEntity.class,
        				EntryPoint.get().getAccessControl().getTokenKey());
	    		
	    		PatinadorEntity res = (PatinadorEntity) BltClient.get().executeCommand(
        				"/getInscripcion/"+rec.getId()+"/"+competicion.getId(), PatinadorEntity.class,
        				EntryPoint.get().getAccessControl().getTokenKey());
	    		rec.setCatBattle(res.getCatBattle());
	    		rec.setCatClassic(res.getCatClassic());
	    		rec.setCatDerrapes(res.getCatDerrapes());
	    		rec.setCatJam(res.getCatJam());
	    		rec.setCatSalto(res.getCatSalto());
	    		rec.setCatSpeed(res.getCatSpeed());
	    		
		        view.showSaveNotification(res.getNombre() + " (" + res.getId() + ") updated");
		        //view.clearSelection();
		        //view.editRecord(null, null, false);
		        view.editRecord(res, competicion, preinscripcionAbierta); // refresca datos en el Form, pero no lo cierra
		        view.refreshRecord(res);
		        return true;
	    	}
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
		return false;
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

	public CompeticionEntity getCompeticion(Long recId) {
		try{
	        return (CompeticionEntity) BltClient.get().getEntityById(
	        		""+recId, CompeticionEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
	}
	
}
