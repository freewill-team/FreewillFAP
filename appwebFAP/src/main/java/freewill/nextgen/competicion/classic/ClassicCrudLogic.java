package freewill.nextgen.competicion.classic;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;
import com.vaadin.ui.VerticalLayout;

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

    private ClassicClasificacion clasificacionView;
    private ClassicFinal finalView;
    private VerticalLayout activeView;
    private Logger log = null;

    public ClassicCrudLogic(ClassicClasificacion simpleCrudView) {
    	clasificacionView = simpleCrudView;
    	activeView = simpleCrudView;
    	setLogger();
    	
    }
    public ClassicCrudLogic(ClassicFinal simpleCrudView) {
    	finalView = simpleCrudView;
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
	        if(clasificacionView!=null){
	        	clasificacionView.showRecords(records);
	        }
	        else if(finalView!=null){
	        	finalView.showRecords(records);
	        }
    	}
		catch(Exception e){
			//log.error(e.getMessage());
			if(clasificacionView!=null)
				clasificacionView.showError(e.getMessage());
			else if(finalView!=null)
		        finalView.showError(e.getMessage());
		}
    }

    public void cancelRecord() {
        setFragmentParameter("");
        if(clasificacionView!=null){
	        clasificacionView.clearSelection();
	        clasificacionView.editRecord(null);
        }
        else if(finalView!=null){
        	finalView.clearSelection();
 	        finalView.editRecord(null);
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
        if(clasificacionView!=null)
        	page.setUriFragment("!" + clasificacionView.VIEW_NAME + "/" + fragmentParameter, false);
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
                    if(clasificacionView!=null)
                    	clasificacionView.selectRow(rec);
                    else if(finalView != null)
                    	finalView.selectRow(rec);;
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
			if(clasificacionView!=null)
				clasificacionView.showError(e.getMessage());
		}
    	return null;
    }

    public ClassicShowEntity saveRecord(ClassicShowEntity rec) {
    	try{
    		if(rec==null) return rec;
    		ClassicShowEntity res = null;
    		System.out.println("Saving = "+rec.toString());
    		//TODO MMFL por aqui no deberia pasar nunca porque el save esta inactivo en deseleccion
    		if(rec.getId()==null)
        		res = (ClassicShowEntity) BltClient.get().createEntity(rec, ClassicShowEntity.class,
        				EntryPoint.get().getAccessControl().getTokenKey());
        	else
        		res = (ClassicShowEntity) BltClient.get().updateEntity(rec, ClassicShowEntity.class,
        				EntryPoint.get().getAccessControl().getTokenKey());
    		
	    	if(clasificacionView!=null){
		        clasificacionView.showSaveNotification(rec.getNombre() + " (" + res.getId() + ") updated");
		        //view.clearSelection();
		        clasificacionView.editRecord(res);
		        clasificacionView.refreshRecord(res);
		        return res;
	    	}
	    	else if(finalView!=null){
	    		finalView.showSaveNotification(rec.getNombre() + " (" + res.getId() + ") updated");
	    		finalView.clearSelection();
	    		finalView.editRecord(res);
	    		finalView.refreshRecord(res);
		        return res;
	    	}
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			if(clasificacionView!=null)
				clasificacionView.showError(e.getMessage());
	    	else if(finalView!=null)
	    		finalView.showError(e.getMessage());
		}
    	return null;
    }
    
    public void deleteRecord(ClassicShowEntity rec) {
    	// Actually Not used
    	try{
	    	if(clasificacionView!=null){
		    	System.out.println("Deleting = "+rec.toString());
		    	BltClient.get().deleteEntity(""+rec.getId(), ClassicShowEntity.class,
		    			EntryPoint.get().getAccessControl().getTokenKey());
		        clasificacionView.showSaveNotification(rec.getNombre() + " (" + rec.getId() + ") removed");
		        clasificacionView.clearSelection();
		        clasificacionView.removeRecord(rec);
		        clasificacionView.editRecord(null);
	        }
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
			clasificacionView.showError(e.getMessage());
		}
    }

    public void editRecord(ClassicShowEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(clasificacionView!=null)
        	clasificacionView.editRecord(rec);
        else if(finalView!=null)
        	finalView.editRecord(rec);
    }

    public void rowSelected(ClassicShowEntity rec) {
    	if(clasificacionView!=null)
    		clasificacionView.editRecord(rec);
    	else if(finalView!=null)
    		finalView.editRecord(rec);
    }

	public boolean existeKO(Long competicion, Long categoria) {
		try{
			ClassicShowEntity rec = (ClassicShowEntity) BltClient.get().executeCommand(
		    		"/existByCompeticionAndCategoria/"+competicion+"/"+categoria,
		    		ClassicShowEntity.class,
		    		EntryPoint.get().getAccessControl().getTokenKey());
			// TODO implementar criterio para distinguir si la final ya comenzó 
			// y por lo tanto la Preclasificacion no puede ser ya modificada
			return false;
		}
		catch(Exception e){
			log.error(e.getMessage());
			if(clasificacionView!=null)
				clasificacionView.showError(e.getMessage());
		}
		return false;
	}

	public List<ClassicShowEntity> initGridResults(Long competicion, Long categoria) {
		try{
	        return BltClient.get().executeQuery(
	        		"/getResultadosFinal/"+competicion+"/"+categoria,
	        		ClassicShowEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			//log.error(e.getMessage());
			//view.showError(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
/*
	public ClassicShowEntity moveRecordUp(ClassicShowEntity rec) {
		if(rec==null) return null;
		try{
			ClassicShowEntity res = (ClassicShowEntity) BltClient.get().executeCommand(
	        		"/moveRecordUp/"+rec.getId(), ClassicShowEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
	        if(clasificacionView!=null){
		        clasificacionView.showSaveNotification(rec.getNombre() + " (" + res.getId() + ") updated");
	        }
	        return res;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			clasificacionView.showError(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public ClassicShowEntity moveRecordDown(ClassicShowEntity rec) {
		if(rec==null) return null;
		try{
			ClassicShowEntity res = (ClassicShowEntity) BltClient.get().executeCommand(
	        		"/moveRecordDown/"+rec.getId(), ClassicShowEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
	        if(clasificacionview!=null){
		        clasificacionview.showSaveNotification(rec.getNombre() + " (" + res.getId() + ") updated");
	        }
	        return res;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			clasificacionview.showError(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	*/
	public boolean deleteAll(Long competicion, Long categoria) {
		try{
	        return BltClient.get().executeCommand(
	        		"/deleteByCompeticionAndCategoria/"+competicion+"/"+categoria,
	        		new ClassicShowEntity(), ClassicShowEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			e.printStackTrace();
			if(clasificacionView!=null)
				clasificacionView.showError(e.getMessage());
		}
		return false;
	}
	
}
