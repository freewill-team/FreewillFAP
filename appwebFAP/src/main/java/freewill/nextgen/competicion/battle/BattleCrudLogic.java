package freewill.nextgen.competicion.battle;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.BattleRondaEntity;
import freewill.nextgen.data.BattleRondaEntity.EliminatoriaEnum;
import freewill.nextgen.data.BattleEntity;

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
public class BattleCrudLogic implements Serializable {

    private BattlePreclasificacion view;
    private BattleRonda koView;
    private Logger log = null;

    public BattleCrudLogic(BattlePreclasificacion simpleCrudView, BattleRonda simpleKoView) {
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
	        	List<BattleEntity> records = (List<BattleEntity>) 
	        			BltClient.get().executeQuery(
	        			"/getByCompeticionAndCategoria/"+competicion+"/"+categoria,
	        			BattleEntity.class,
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
                    //BattleEntity rec = findRecord(pid);
                	BattleEntity rec = findRecord(recId);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private BattleEntity findRecord(String recId) {
    	try{
    		BattleEntity rec = (BattleEntity) BltClient.get().getEntityById(
    				recId, BattleEntity.class,
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

    public BattleEntity saveRecord(BattleEntity rec) {
    	try{
	    	if(view!=null && rec!=null){
	    		BattleEntity res = null;
	    		System.out.println("Saving = "+rec.toString());
	    		
	    		if(rec.getId()==null)
	        		res = (BattleEntity) BltClient.get().createEntity(rec, BattleEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	        	else
	        		res = (BattleEntity) BltClient.get().updateEntity(rec, BattleEntity.class,
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
    
    public void deleteRecord(BattleEntity rec) {
    	// Actually Not used
    	try{
	    	if(view!=null){
		    	System.out.println("Deleting = "+rec.toString());
		    	BltClient.get().deleteEntity(""+rec.getId(), BattleEntity.class,
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

    public void editRecord(BattleEntity rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(view!=null)
        	view.editRecord(rec);
    }

    public void rowSelected(BattleEntity rec) {
    	view.editRecord(rec);
    }

	public List<BattleRondaEntity> initKO(Long competicion, Long categoria) {
		try{
		    return BltClient.get().executeQuery(
		    		"/findByCompeticionAndCategoria/"+
		    				competicion+"/"+categoria,
					BattleRondaEntity.class,
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

	public BattleRondaEntity saveRecordKO(BattleRondaEntity rec) {
		try{
			BattleRondaEntity res = null;
	    	System.out.println("Saving = "+rec.toString());
	    		
	    	if(rec.getId()==null)
	        	res = (BattleRondaEntity) BltClient.get().createEntity(rec, BattleRondaEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        else
	        	res = (BattleRondaEntity) BltClient.get().updateEntity(rec, BattleRondaEntity.class,
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

	public BattleRondaEntity findRecordKO(String recId) {
		try{
			BattleRondaEntity rec = (BattleRondaEntity) BltClient.get().getEntityById(
    				recId, BattleRondaEntity.class,
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
			BattleRondaEntity rec = (BattleRondaEntity) BltClient.get().executeCommand(
		    		"/existByCompeticionAndCategoria/"+competicion+"/"+categoria,
					BattleRondaEntity.class,
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

	public List<BattleEntity> initGridResults(Long competicion, Long categoria) {
		try{
	        return BltClient.get().executeQuery(
	        		"/getResultados/"+competicion+"/"+categoria,
	        		BattleEntity.class,
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

	public BattleEntity moveRecordUp(BattleEntity rec) {
		if(rec==null) return null;
		try{
			BattleEntity res = (BattleEntity) BltClient.get().executeCommand(
	        		"/moveRecordUp/"+rec.getId(), BattleEntity.class,
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
	
	public BattleEntity moveRecordDown(BattleEntity rec) {
		if(rec==null) return null;
		try{
			BattleEntity res = (BattleEntity) BltClient.get().executeCommand(
	        		"/moveRecordDown/"+rec.getId(), BattleEntity.class,
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
	        		new BattleRondaEntity(), BattleRondaEntity.class,
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
