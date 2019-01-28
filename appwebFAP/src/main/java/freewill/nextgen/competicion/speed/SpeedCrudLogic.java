package freewill.nextgen.competicion.speed;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.SpeedKOSystemEntity;
import freewill.nextgen.data.SpeedKOSystemEntity.EliminatoriaEnum;
import freewill.nextgen.data.SpeedTimeTrialEntity;
import freewill.nextgen.data.SpeedTimeTrialEntity.RondaEnum;

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
public class SpeedCrudLogic implements Serializable {

    private SpeedTimeTrial view;
    private SpeedKOsystem koView;
    private Logger log = null;

    public SpeedCrudLogic(SpeedTimeTrial simpleCrudView, SpeedKOsystem simpleKoView) {
        view = simpleCrudView;
        koView = simpleKoView;
        if(view!=null)
        	log = Logger.getLogger(view.getClass());
        else
        	log = Logger.getLogger(this.getClass());
    }

    public void initGrid(Long competicion, Long categoria, RondaEnum ronda) {
    	try{
	        editRecord(null);
	        if(view!=null && ronda==RondaEnum.RESULTADOS){
	        	List<SpeedTimeTrialEntity> records = (List<SpeedTimeTrialEntity>) 
	        			BltClient.get().executeQuery(
	        			"/getResultados/"+competicion+"/"+categoria,
	        			SpeedTimeTrialEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        	view.showRecords(records);
	        }
	        else if(view!=null && ronda==RondaEnum.PRIMERA){
	        	List<SpeedTimeTrialEntity> records = (List<SpeedTimeTrialEntity>) 
	        			BltClient.get().executeQuery(
	        			"/getByCompeticionAndCategoriaOrden1/"+competicion+"/"+categoria,
	        			SpeedTimeTrialEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        	view.showRecords(records);
	        }
	        else if(view!=null && ronda==RondaEnum.SEGUNDA){
	        	List<SpeedTimeTrialEntity> records = (List<SpeedTimeTrialEntity>) 
	        			BltClient.get().executeQuery(
	        			"/getByCompeticionAndCategoriaOrden2/"+competicion+"/"+categoria,
	        			SpeedTimeTrialEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        	view.showRecords(records);
	        }
    	}
		catch(Exception e){
			//log.error(e.getMessage());
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
                    //SpeedTimeTrialEntity rec = findRecord(pid);
                	SpeedTimeTrialEntity rec = findRecord(recId);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private SpeedTimeTrialEntity findRecord(String recId) {
    	try{
    		SpeedTimeTrialEntity rec = (SpeedTimeTrialEntity) BltClient.get().getEntityById(
    				recId, SpeedTimeTrialEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		return rec;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
    	return null;
    }

    public SpeedTimeTrialEntity saveRecord(SpeedTimeTrialEntity rec) {
    	try{
	    	if(view!=null && rec!=null){
	    		SpeedTimeTrialEntity res = null;
	    		System.out.println("Saving = "+rec.toString());
	    		
	    		if(rec.getId()==null)
	        		res = (SpeedTimeTrialEntity) BltClient.get().createEntity(rec, SpeedTimeTrialEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	        	else
	        		res = (SpeedTimeTrialEntity) BltClient.get().updateEntity(rec, SpeedTimeTrialEntity.class,
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
			view.showError(e.getMessage());
		}
    	return null;
    }
    
    public void deleteRecord(SpeedTimeTrialEntity rec) {
    	// Actually Not used
    	try{
	    	if(view!=null){
		    	System.out.println("Deleting = "+rec.toString());
		    	BltClient.get().deleteEntity(""+rec.getId(), SpeedTimeTrialEntity.class,
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
			view.showError(e.getMessage());
		}
    }

    public void editRecord(SpeedTimeTrialEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(view!=null)
        	view.editRecord(rec);
    }

    public void rowSelected(SpeedTimeTrialEntity rec) {
    	view.editRecord(rec);
    }

	public List<SpeedKOSystemEntity> initKO(Long competicion, Long categoria, EliminatoriaEnum eliminatoria) {
		try{
		    return BltClient.get().executeQuery(
		    		"/findByCompeticionAndCategoriaAndEliminatoria/"+
		    				competicion+"/"+categoria+"/"+eliminatoria.name(),
					SpeedKOSystemEntity.class,
		    		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public SpeedKOSystemEntity saveRecordKO(SpeedKOSystemEntity rec) {
		try{
			SpeedKOSystemEntity res = null;
	    	System.out.println("Saving = "+rec.toString());
	    		
	    	if(rec.getId()==null)
	        	res = (SpeedKOSystemEntity) BltClient.get().createEntity(rec, SpeedKOSystemEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        else
	        	res = (SpeedKOSystemEntity) BltClient.get().updateEntity(rec, SpeedKOSystemEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());	
		    
	    	if(koView!=null)
	    		koView.showRecords(this.initKO(rec.getCompeticion(), rec.getCategoria(), rec.getEliminatoria()));
	    	
	    	return res;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
		}
    	return null;
	}

	public SpeedKOSystemEntity findRecordKO(String recId) {
		try{
			SpeedKOSystemEntity rec = (SpeedKOSystemEntity) BltClient.get().getEntityById(
    				recId, SpeedKOSystemEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		return rec;
    	}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
		}
    	return null;
	}
    
	public SpeedTimeTrialEntity saveRecordTiempo(SpeedTimeTrialEntity rec, RondaEnum ronda) {
    	try{
	    	if(view!=null && rec!=null){
	    		SpeedTimeTrialEntity res = null;
	    		System.out.println("Saving Tiempo = "+rec.toString());
	    		
	    		if(ronda == RondaEnum.PRIMERA)
	    			BltClient.get().executeCommand(
	        				"/updateTiempo1",
	        				rec, SpeedTimeTrialEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	    		else
	    			BltClient.get().executeCommand(
	        				"/updateTiempo2",
	        				rec, SpeedTimeTrialEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	    		
	    		res = (SpeedTimeTrialEntity) BltClient.get().getEntityById(
	    				""+rec.getId(), SpeedTimeTrialEntity.class,
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
			view.showError(e.getMessage());
		}
    	return null;
    }

	public EliminatoriaEnum existeKO(Long competicion, Long categoria) {
		try{
			SpeedKOSystemEntity rec = (SpeedKOSystemEntity) BltClient.get().executeCommand(
		    		"/existByCompeticionAndCategoria/"+competicion+"/"+categoria,
					SpeedKOSystemEntity.class,
		    		EntryPoint.get().getAccessControl().getTokenKey());
			return rec.getEliminatoria();
		}
		catch(Exception e){
			log.error(e.getMessage());
			view.showError(e.getMessage());
		}
		return null;
	}

	public List<SpeedTimeTrialEntity> initGridResults(Long competicion, Long categoria) {
		try{
	        return BltClient.get().executeQuery(
	        		"/getResultadosFinal/"+competicion+"/"+categoria,
	        		SpeedTimeTrialEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			//log.error(e.getMessage());
			//view.showError(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean deleteAll(Long competicion, Long categoria) {
		try{
	        return BltClient.get().executeCommand(
	        		"/deleteByCompeticionAndCategoria/"+competicion+"/"+categoria,
	        		new SpeedTimeTrialEntity(), SpeedTimeTrialEntity.class,
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
