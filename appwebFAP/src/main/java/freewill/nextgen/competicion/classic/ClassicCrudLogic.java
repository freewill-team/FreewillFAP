package freewill.nextgen.competicion.classic;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.ClassicShowEntity;

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
public class ClassicCrudLogic implements Serializable {

    private ClassicFinal activeView;
    private Logger log = null;

    
    public ClassicCrudLogic(ClassicFinal simpleCrudView) {
    	activeView = simpleCrudView;
    	setLogger();
    }
    
    private void setLogger() {
    	if (activeView != null)
    	    log = Logger.getLogger(activeView.getClass());
    }
    
    public ClassicCrudLogic() {
          log = Logger.getLogger(this.getClass());
    }
    
    public void initGrid(Long competicion, Long categoria) {
    	try{
    		List<ClassicShowEntity> records = (List<ClassicShowEntity>) 
	        		BltClient.get().executeQuery(
	        		"/getByCompeticionAndCategoria/"+competicion+"/"+categoria,
	        		ClassicShowEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
	        
	       	activeView.showRecords(records);  
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(activeView!=null){
				activeView.showError(e.getMessage());
				activeView.setEnabled(false);
			}
		}
    }

    public void cancelRecord() {
        setFragmentParameter("");
        if(activeView!=null){
        	activeView.clearSelection();
        	activeView.editRecord(null);
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
        if(activeView!=null)
        	page.setUriFragment("!" + activeView.VIEW_NAME + "/" + fragmentParameter, false);
    }

    public void enter(String recId) {
        if (recId != null && !recId.isEmpty()) {
            if (recId.equals("new")) {
                //newRecord();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    ClassicShowEntity rec = findRecord(recId);
                    if(activeView!=null)
                    	activeView.selectRow(rec);
                } catch (NumberFormatException e) {
                	log.error(e.getMessage());
                }
            }
        }
    }

    private ClassicShowEntity findRecord(String recId) {
    	try{
    		ClassicShowEntity rec = (ClassicShowEntity) BltClient.get().getEntityById(
    				recId, ClassicShowEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		return rec;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(activeView!=null)
				activeView.showError(e.getMessage());
		}
    	return null;
    }

    public ClassicShowEntity saveRecord(ClassicShowEntity rec) {
    	try{
    		if(rec==null) 
    			return rec;
    		ClassicShowEntity res = null;
    		System.out.println("Saving = "+rec.toString());
    		//TODO MMFL por aqui no deberia pasar nunca porque el save esta inactivo en deseleccion
    		if(rec.getId()==null)
        		res = (ClassicShowEntity) BltClient.get().createEntity(rec, ClassicShowEntity.class,
        				EntryPoint.get().getAccessControl().getTokenKey());
        	else
        		res = (ClassicShowEntity) BltClient.get().updateEntity(rec, ClassicShowEntity.class,
        				EntryPoint.get().getAccessControl().getTokenKey());
    		
	    	if(activeView!=null){
	    		activeView.showSaveNotification(rec.getNombre() + " (" + res.getId() + ") updated");
	    		//activeView.clearSelection();
	    		activeView.editRecord(null);
	    		//activeView.refreshRecord(res);
	    		initGrid(res.getCompeticion(), res.getCategoria());
		        return res;
	    	}
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(activeView!=null)
				activeView.showError(e.getMessage());
		}
    	return null;
    }
    
    public void deleteRecord(ClassicShowEntity rec) {
    	// Actually Not used
    	try{
	    	if(activeView!=null){
		    	System.out.println("Deleting = "+rec.toString());
		    	BltClient.get().deleteEntity(""+rec.getId(), ClassicShowEntity.class,
		    			EntryPoint.get().getAccessControl().getTokenKey());
		    	activeView.showSaveNotification(rec.getNombre() + " (" + rec.getId() + ") removed");
		    	activeView.clearSelection();
		    	activeView.removeRecord(rec);
		    	activeView.editRecord(null);
	        }
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			activeView.showError(e.getMessage());
		}
    }

    public void editRecord(ClassicShowEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(activeView!=null)
        	activeView.editRecord(rec);
    }

    public void rowSelected(ClassicShowEntity rec) {
    	System.out.println("Entrando en rowSelected "+rec);
    	if(activeView!=null)
    		activeView.editRecord(rec);
    }

	public List<ClassicShowEntity> initGridResults(Long competicion, Long categoria) {
		try{
	        return BltClient.get().executeQuery(
	        		"/getResultados/"+competicion+"/"+categoria,
	        		ClassicShowEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(activeView!=null){
				activeView.showError(e.getMessage());
			}
			e.printStackTrace();
		}
		return null;
	}
	
	public List<ClassicShowEntity> initGridFinalResults(Long competicion, Long categoria) {
		try{
	        return BltClient.get().executeQuery(
	        		"/getResultadosFinal/"+competicion+"/"+categoria,
	        		ClassicShowEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(activeView!=null){
				activeView.showError(e.getMessage());
			}
			e.printStackTrace();
		}
		return null;
	}

	public boolean deleteAll(Long competicion, Long categoria) {
		try{
	        return BltClient.get().executeCommand(
	        		"/deleteByCompeticionAndCategoria/"+competicion+"/"+categoria,
	        		new ClassicShowEntity(), ClassicShowEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			e.printStackTrace();
			if(activeView!=null)
				activeView.showError(e.getMessage());
		}
		return false;
	}

	public void setGridColumns(int i) {
		if(activeView!=null){
			activeView.setGridColumns(i);
		}
	}

	public ClassicShowEntity saveJuez(ClassicShowEntity rec, int juez) {
		try{
    		if(rec==null || rec.getId()==null) 
    			return rec;
    		
    		System.out.println("Saving = "+rec.toString());
    		boolean result = BltClient.get().executeCommand(
    				"/updateJuez"+juez, rec, ClassicShowEntity.class,
        			EntryPoint.get().getAccessControl().getTokenKey());
    		
    		if(result){
    			rec = this.findRecord(""+rec.getId());
    		}
    		
	    	if(activeView!=null && result){
	    		activeView.showSaveNotification(rec.getNombre() + " (" + rec.getId() + ") updated");
		        //view.clearSelection();
	    		activeView.editRecord(null);
	    		//activeView.refreshRecord(res);
	    		initGrid(rec.getCompeticion(), rec.getCategoria());
		        return rec;
	    	}
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(activeView!=null)
				activeView.showError(e.getMessage());
		}
    	return null;
	}

	public void showCalculadora(ClassicShowEntity rec) {
		if (activeView != null)
			activeView.openCalculadora(rec);
	}
		
}
