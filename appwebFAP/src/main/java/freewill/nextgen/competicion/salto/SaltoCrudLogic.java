package freewill.nextgen.competicion.salto;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
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
public class SaltoCrudLogic implements Serializable {

    private SaltoTrial view;
    private Logger log = null;

    public SaltoCrudLogic(SaltoTrial simpleCrudView) {
        view = simpleCrudView;
        if(view!=null)
        	log = Logger.getLogger(view.getClass());
        else
        	log = Logger.getLogger(this.getClass());
    }

    public void initGrid(Long competicion, Long categoria, int ronda) {
    	try{
	        editRecord(null);
	        if(view!=null && ronda==0){
	        	List<SaltoEntity> records = (List<SaltoEntity>) 
	        			BltClient.get().executeQuery(
	        			"/getByCompeticionAndCategoria/"+competicion+"/"+categoria,
	        			SaltoEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        	view.showRecords(records);
	        }
	        else if(view!=null){
	        	List<SaltoEntity> records = (List<SaltoEntity>) 
	        			BltClient.get().executeQuery(
	        			"/getByCompeticionAndCategoriaAndRonda/"+competicion+"/"+categoria+"/"+ronda,
	        			SaltoEntity.class,
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
                    //SaltoEntity rec = findRecord(pid);
                	SaltoEntity rec = findRecord(recId);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private SaltoEntity findRecord(String recId) {
    	try{
    		SaltoEntity rec = (SaltoEntity) BltClient.get().getEntityById(
    				recId, SaltoEntity.class,
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

    public SaltoEntity saveRecord(SaltoEntity rec) {
    	try{
	    	if(view!=null && rec!=null){
	    		SaltoEntity res = null;
	    		System.out.println("Saving = "+rec.toString());
	    		
	    		if(rec.getId()==null)
	        		res = (SaltoEntity) BltClient.get().createEntity(rec, SaltoEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	        	else
	        		res = (SaltoEntity) BltClient.get().updateEntity(rec, SaltoEntity.class,
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
    
    public void deleteRecord(SaltoEntity rec) {
    	// Actually Not used
    	try{
	    	if(view!=null){
		    	System.out.println("Deleting = "+rec.toString());
		    	BltClient.get().deleteEntity(""+rec.getId(), SaltoEntity.class,
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

    public void editRecord(SaltoEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(view!=null)
        	view.editRecord(rec);
    }

    public void rowSelected(SaltoEntity rec) {
    	if(view!=null)
    		view.editRecord(rec);
    }

	public int existenDatosRonda(Long competicion, Long categoria, int ronda) {
		try{
			SaltoEntity rec = (SaltoEntity) BltClient.get().executeCommand(
		    		"/checkRondaByCompeticionAndCategoria/"+competicion+"/"+categoria+"/"+ronda,
		    		SaltoEntity.class, EntryPoint.get().getAccessControl().getTokenKey());
			return rec.getAltura();
		}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
		return 0;
	}
	
	public boolean createNewAltura(Long competicion, Long categoria, int ronda, int altura) {
		// Create nuevos SaltoIntentoEntity para newAltura 
		try{
			// return True if it is last run
			return BltClient.get().executeCommand(
		    	"/createByCompeticionAndCategoriaAndRonda/"+competicion+"/"+categoria+"/"+ronda+"/"+altura,
		    	new SaltoEntity(), SaltoEntity.class, EntryPoint.get().getAccessControl().getTokenKey());
		}
		catch(Exception e){
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
		return false;
	}

	public List<SaltoEntity> initGridResults(Long competicion, Long categoria, int ronda) {
		try{
	        return BltClient.get().executeQuery(
	        		"/getResultados/"+competicion+"/"+categoria,
	        		SaltoEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
			if(view!=null)
				view.showError(e.getMessage());
		}
		return null;
	}

	public boolean deleteAll(Long competicion, Long categoria) {
		try{
	        return BltClient.get().executeCommand(
	        		"/deleteByCompeticionAndCategoria/"+competicion+"/"+categoria,
	        		new SaltoEntity(), SaltoEntity.class,
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
