package freewill.nextgen.mejoresmarcas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.data.CategoriaEntity;
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
public class MarcasCrudLogic implements Serializable {

    private MarcasCrudGrid view;
    private Logger log = null;

    public MarcasCrudLogic(MarcasCrudGrid simpleCrudView) {
        view = simpleCrudView;
        if(view!=null)
        	log = Logger.getLogger(view.getClass());
        else
        	log = Logger.getLogger(this.getClass());
    }

    public void init(ModalidadEnum modalidad, String sortby) {
    	try{
    		Long i = 1L;
    		List<ParticipanteEntity> output = new ArrayList<ParticipanteEntity>();
    		List<CategoriaEntity> categorias = getCategorias(modalidad);
			for(CategoriaEntity cat:categorias){
				List<ParticipanteEntity> collection = BltClient.get().executeQuery(
						"/getMejoresMarcas/"+cat.getId()+"/"+sortby,
			        	ParticipanteEntity.class,
			        	EntryPoint.get().getAccessControl().getTokenKey());
				//output.addAll(collection);
				for(ParticipanteEntity rec:collection){
					rec.setId(i++);
					output.add(rec);
				}
			}
	        if(view!=null)
	        	view.showRecords(output);
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null){
				view.showError(e.getMessage());
			}
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
                	ParticipanteEntity rec = findRecord(recId);
                    
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private ParticipanteEntity findRecord(String recId) {
    	try{
    		ParticipanteEntity rec = (ParticipanteEntity) BltClient.get().getEntityById(
    				recId, ParticipanteEntity.class,
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
    
}
