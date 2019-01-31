package freewill.nextgen.dorsal;

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
public class DorsalCrudLogic implements Serializable {

    private DorsalCrudView view;
    private Logger log = null;

    public DorsalCrudLogic(DorsalCrudView simpleCrudView) {
        view = simpleCrudView;
        log = Logger.getLogger(view.getClass());
    }

    public void init(Long competicion) {
    	try{
	        editRecord(null, null);
	        if(view!=null){
	        	view.showRecords(//BltClient.get().getEntities(
	        			BltClient.get().executeQuery("/getInscripciones/"+competicion+"/false",
	        			PatinadorEntity.class,
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
	        view.editRecord(null, null);
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
			view.showError(e.getMessage());
		}
    	return null;
    }

    public ParticipanteEntity saveRecord(ParticipanteEntity rec, PatinadorEntity pat) {
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
		        
		        CategoriaEntity cat = (CategoriaEntity) BltClient.get().getEntityById(
		        		""+rec.getCategoria(), CategoriaEntity.class,
		        		EntryPoint.get().getAccessControl().getTokenKey());
				if(cat!=null){
					switch(cat.getModalidad()){
					case SPEED: pat.setSpeed(true); break;
					case CLASSIC: pat.setClassic(true); break;
					case BATTLE: pat.setBattle(true); break;
					case JAM: pat.setJam(true); break;
					case SLIDE: pat.setDerrapes(true); break;
					case JUMP: pat.setSalto(true); break;
					}
				}
		        view.refreshRecord(pat);
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
    
    public void deleteRecord(ParticipanteEntity rec, PatinadorEntity pat) {
    	try{
	    	if(view!=null){
		    	System.out.println("Deleting = "+rec.toString());
		    	BltClient.get().deleteEntity(""+rec.getId(), ParticipanteEntity.class,
		    			EntryPoint.get().getAccessControl().getTokenKey());
		        view.showSaveNotification(rec.getNombre() + " (" + rec.getId() + ") removed");
		        
		        CategoriaEntity cat = (CategoriaEntity) BltClient.get().getEntityById(
		        		""+rec.getCategoria(), CategoriaEntity.class,
		        		EntryPoint.get().getAccessControl().getTokenKey());
				if(cat!=null){
					switch(cat.getModalidad()){
					case SPEED: pat.setSpeed(false); break;
					case CLASSIC: pat.setClassic(false); break;
					case BATTLE: pat.setBattle(false); break;
					case JAM: pat.setJam(false); break;
					case SLIDE: pat.setDerrapes(false); break;
					case JUMP: pat.setSalto(false); break;
					}
				}
				view.refreshRecord(pat);
	        }
	        //setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
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
			view.showError(e.getMessage());
		}
		return null;
    }

    public void editRecord(PatinadorEntity rec, Long competicion) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(view!=null)
        	view.editRecord(rec, competicion);
    }

    public void rowSelected(PatinadorEntity rec, Long competicion) {
    	view.editRecord(rec, competicion);
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

	public boolean saveDorsal(PatinadorEntity rec, Long competicion) {
		try{
	    	if(view!=null){
	    		PatinadorEntity res = null;
	    		System.out.println("Saving = "+rec.toString());
	    		
	    		res = (PatinadorEntity) BltClient.get().executeCommand(
        				"/updateDorsal/"+rec.getId()+"/"+competicion+"/"+rec.getDorsal(), 
        				PatinadorEntity.class,
        				EntryPoint.get().getAccessControl().getTokenKey());
	        	res.setSalto(rec.getSalto());
	        	res.setSpeed(rec.getSpeed());
	        	res.setJam(rec.getJam());
	        	res.setBattle(rec.getBattle());
	        	res.setClassic(rec.getClassic());
	        	res.setDerrapes(rec.getDerrapes());
	    		
		        view.showSaveNotification(rec.getNombre() + " (" + res.getId() + ") updated");
		        view.clearSelection();
		        view.editRecord(null, null);
		        view.refreshRecord(res);
		        return true;
	    	}
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
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
    
}
