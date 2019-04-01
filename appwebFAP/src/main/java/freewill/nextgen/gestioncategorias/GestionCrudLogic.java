package freewill.nextgen.gestioncategorias;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CategoriaEntity.AccionEnum;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.data.SaltoEntity;

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
public class GestionCrudLogic implements Serializable {

    private GestionCrudGrid view;
    private Logger log = null;

    public GestionCrudLogic(GestionCrudGrid simpleCrudView) {
        view = simpleCrudView;
        if(view!=null)
        	log = Logger.getLogger(view.getClass());
        else
        	log = Logger.getLogger(this.getClass());
    }

    public void init(CompeticionEntity competi) {
    	try{
	        if(view!=null){
	        	Date now = new Date();
	        	// Devuelve todos las categorias con datos de inscripciones
	        	view.showRecords(
		        	BltClient.get().executeQuery("/getByCompeticion/"+competi.getId(),
		        	CategoriaEntity.class,
		        	EntryPoint.get().getAccessControl().getTokenKey()));
	        	// Comprueba que la preinscripcion ya esta cerrada
	        	if(competi==null || competi.getFechaFinInscripcion().after(now)){
	        		view.showError("La Inscripción aun está abierta.");
		        	view.setEdicionAbierta(false);
	        	}
	        	else // Comprueba si la competicion ya ha terminadp
		        if(competi.getFechaFin().before(now)){
				    view.showError("La Competición ya ha terminado.");
				    view.setEdicionAbierta(false);
				}
	        	else // Comprueba si la competicion ya ha empezado
	        	if(competi.getFechaInicio().before(now)){
			        view.showError("La Competición ya ha empezado.");
			        view.setEdicionAbierta(false);
			    }
	        }
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null){
				view.showError(e.getMessage());
				view.setEdicionAbierta(false);
			}
		}
    }

    public void cancelRecord() {
        setFragmentParameter("");
        if(view!=null){
	        view.clearSelection();
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
                	CategoriaEntity rec = findRecord(recId);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private CategoriaEntity findRecord(String recId) {
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

	public boolean executeAction(CategoriaEntity selectedRow, 
			AccionEnum accion, Long competicion) {
		if(selectedRow==null) return false;
		try{
			boolean result = BltClient.get().executeCommand(
		        "/executeAction/"+selectedRow.getId()+"/"+accion.name()+"/"+competicion,
		        new CategoriaEntity(), CategoriaEntity.class,
		        EntryPoint.get().getAccessControl().getTokenKey());
			if(result) // Actualiza las categorias mostradas
	        	view.showRecords(
		        	BltClient.get().executeQuery("/getByCompeticion/"+competicion,
		        	CategoriaEntity.class,
		        	EntryPoint.get().getAccessControl().getTokenKey()));
	    }
		catch(Exception e){
			e.printStackTrace();
			if(view!=null)
				view.showError(e.getMessage());
		}
		return false;
	}
	
}
