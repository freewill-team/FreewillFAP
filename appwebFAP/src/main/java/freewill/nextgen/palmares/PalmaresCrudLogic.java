package freewill.nextgen.palmares;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CategoriaEntity;
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
public class PalmaresCrudLogic implements Serializable {

    private PalmaresCrudView view;
    private Logger log = null;

    public PalmaresCrudLogic(PalmaresCrudView simpleCrudView) {
        view = simpleCrudView;
        log = Logger.getLogger(view.getClass());
    }

    public void init(Long competicion) {
    	try{
	        editRecord(null);
	        if(view!=null){
	        	view.showRecords(//BltClient.get().getEntities(
	        			BltClient.get().executeQuery("/getByCompeticion/"+competicion,
	        			ParticipanteEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey()));
	        }
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
    }

    public void cancelRecord() {
        setFragmentParameter("");
        if(view!=null){
	        view.clearSelection();
	        view.editRecord(null);
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
                    if(view!=null)
                    	view.selectRow(rec);
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
			view.showError(e.getMessage());
		}
    	return null;
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
    	view.editRecord(rec);
    }
    
    public Collection<CategoriaEntity> getCategorias() {
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
	
	public void newRecord(Long competicion) {
        view.clearSelection();
        setFragmentParameter("new");
        ParticipanteEntity rec = new ParticipanteEntity();
        rec.setCompeticion(competicion);
        view.editRecord(rec);
    }

	public void findRecords(Long id) {
	    try{
		    if(view!=null){
		        view.showRecords(
		        		BltClient.get().executeQuery("/getByPatinador/"+id,
		        		ParticipanteEntity.class,
		        		EntryPoint.get().getAccessControl().getTokenKey()));
		    }
	    }
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
	}
	
}
