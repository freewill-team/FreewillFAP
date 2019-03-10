package freewill.nextgen.competicion.derrapes;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.DerrapesRondaEntity;
import freewill.nextgen.data.DerrapesRondaEntity.EliminatoriaEnum;
import freewill.nextgen.data.DerrapesEntity;

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
public class DerrapesCrudLogic implements Serializable {

    private DerrapesPreclasificacion view;
    private DerrapesRonda koView;
    private Logger log = null;

    public DerrapesCrudLogic(DerrapesPreclasificacion simpleCrudView, DerrapesRonda simpleKoView) {
        view = simpleCrudView;
        koView = simpleKoView;
        if(view!=null)
        	log = Logger.getLogger(view.getClass());
        else
        	log = Logger.getLogger(this.getClass());
    }

    public void initGrid(Long competicion, Long categoria) {
    	try{
	        if(view!=null){
	        	List<DerrapesEntity> records = (List<DerrapesEntity>) 
	        			BltClient.get().executeQuery(
	        			"/getByCompeticionAndCategoria/"+competicion+"/"+categoria,
	        			DerrapesEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        	view.showRecords(records);
	        }
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null){
				view.showError(e.getMessage());
				view.setEnabled(false);
			}
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
                    //DerrapesEntity rec = findRecord(pid);
                	DerrapesEntity rec = findRecord(recId);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private DerrapesEntity findRecord(String recId) {
    	try{
    		DerrapesEntity rec = (DerrapesEntity) BltClient.get().getEntityById(
    				recId, DerrapesEntity.class,
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

    public DerrapesEntity saveRecord(DerrapesEntity rec) {
    	try{
	    	if(view!=null && rec!=null){
	    		DerrapesEntity res = null;
	    		System.out.println("Saving = "+rec.toString());
	    		
	    		if(rec.getId()==null)
	        		res = (DerrapesEntity) BltClient.get().createEntity(rec, DerrapesEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	        	else
	        		res = (DerrapesEntity) BltClient.get().updateEntity(rec, DerrapesEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	    		
		        view.showSaveNotification(rec.getNombre() + " (" + res.getId() + ") updated");
		        view.clearSelection();
		        view.editRecord(res);
		        view.refreshRecord(res);
		        return res;
	    	}
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
    	return null;
    }
    
    public void deleteRecord(DerrapesEntity rec) {
    	// Actually Not used
    	try{
	    	if(view!=null){
		    	System.out.println("Deleting = "+rec.toString());
		    	BltClient.get().deleteEntity(""+rec.getId(), DerrapesEntity.class,
		    			EntryPoint.get().getAccessControl().getTokenKey());
		        view.showSaveNotification(rec.getNombre() + " (" + rec.getId() + ") removed");
		        view.clearSelection();
		        view.removeRecord(rec);
		        view.editRecord(null);
	        }
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
    }

    public void editRecord(DerrapesEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(view!=null)
        	view.editRecord(rec);
    }

    public void rowSelected(DerrapesEntity rec) {
    	view.editRecord(rec);
    }

	public List<DerrapesRondaEntity> initKO(Long competicion, Long categoria) {
		try{
		    return BltClient.get().executeQuery(
		    		"/findByCompeticionAndCategoria/"+
		    				competicion+"/"+categoria,
					DerrapesRondaEntity.class,
		    		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			if(koView!=null)
				koView.showError(e.getMessage());
		}
		return null;
	}

	public DerrapesRondaEntity saveRecordKO(DerrapesRondaEntity rec) {
		try{
			DerrapesRondaEntity res = null;
	    	System.out.println("Saving = "+rec.toString());
	    		
	    	if(rec.getId()==null)
	        	res = (DerrapesRondaEntity) BltClient.get().createEntity(rec, DerrapesRondaEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        else
	        	res = (DerrapesRondaEntity) BltClient.get().updateEntity(rec, DerrapesRondaEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());	
		    
	    	if(koView!=null){
	    		koView.editRecord(null);
	    		koView.showRecords(this.initKO(rec.getCompeticion(), rec.getCategoria()));
	    		koView.showSaveNotification("Record (" + res.getId() + ") updated");
	    	}
	    	return res;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			if(koView!=null)
				koView.showError(e.getMessage());
		}
    	return null;
	}

	public DerrapesRondaEntity findRecordKO(String recId) {
		try{
			DerrapesRondaEntity rec = (DerrapesRondaEntity) BltClient.get().getEntityById(
    				recId, DerrapesRondaEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		return rec;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			if(koView!=null)
				koView.showError(e.getMessage());
		}
    	return null;
	}

	public EliminatoriaEnum existeKO(Long competicion, Long categoria) {
		try{
			DerrapesRondaEntity rec = (DerrapesRondaEntity) BltClient.get().executeCommand(
		    		"/existByCompeticionAndCategoria/"+competicion+"/"+categoria,
					DerrapesRondaEntity.class,
		    		EntryPoint.get().getAccessControl().getTokenKey());
			return rec.getEliminatoria();
		}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
		return null;
	}

	public List<DerrapesEntity> initGridResults(Long competicion, Long categoria) {
		try{
	        return BltClient.get().executeQuery(
	        		"/getResultados/"+competicion+"/"+categoria,
	        		DerrapesEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			if(view!=null)
				view.showError(e.getMessage());
		}
		return null;
	}

	public DerrapesEntity moveRecordUp(DerrapesEntity rec) {
		if(rec==null) return null;
		try{
			DerrapesEntity res = (DerrapesEntity) BltClient.get().executeCommand(
	        		"/moveRecordUp/"+rec.getId(), DerrapesEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
	        if(view!=null){
		        view.showSaveNotification(rec.getNombre() + " (" + res.getId() + ") updated");
	        }
	        return res;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			if(view!=null)
				view.showError(e.getMessage());
		}
		return null;
	}
	
	public DerrapesEntity moveRecordDown(DerrapesEntity rec) {
		if(rec==null) return null;
		try{
			DerrapesEntity res = (DerrapesEntity) BltClient.get().executeCommand(
	        		"/moveRecordDown/"+rec.getId(), DerrapesEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
	        if(view!=null){
		        view.showSaveNotification(rec.getNombre() + " (" + res.getId() + ") updated");
	        }
	        return res;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			if(view!=null)
				view.showError(e.getMessage());
		}
		return null;
	}
	
	public boolean deleteAll(Long competicion, Long categoria) {
		try{
	        return BltClient.get().executeCommand(
	        		"/deleteByCompeticionAndCategoria/"+competicion+"/"+categoria,
	        		new DerrapesRondaEntity(), DerrapesRondaEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			e.printStackTrace();
			if(view!=null)
				view.showError(e.getMessage());
		}
		return false;
	}
	
}
