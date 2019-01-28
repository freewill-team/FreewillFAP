package freewill.nextgen.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CrudLogicInterface;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.MappingEntity;
import freewill.nextgen.data.Requirement2Entity;
import freewill.nextgen.data.RequirementMapping;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

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
public class MappingCrudLogic implements CrudLogicInterface<MappingEntity> /*Serializable*/ {

    private MappingCrudView view;

    public MappingCrudLogic(MappingCrudView simpleCrudView) {
        view = simpleCrudView;
    }
    
    public void setProject(Long prj) {
    	if(view!=null && prj!=null){
	        try {
	        	Collection<RequirementMapping> recs = BltClient.get().executeQuery(
	        		"/getmyreqsbyproject/"+prj, 
	        		RequirementMapping.class,
					EntryPoint.get().getAccessControl().getTokenKey());
	        	view.showRecords(recs);
			} catch (Exception e) {
				e.printStackTrace();
				view.showRecords(null);
				view.showError("Error: "+e.getMessage());
			}
    	}
    }

    public void init() {
        editRecord(null);
        view.setAutoMappingEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER));
        view.setUploadEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER));
    }

    public void cancelRecord() {
        setFragmentParameter("");
        view.clearSelection();
        view.editRecord((MappingEntity)null, null);
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    public void setFragmentParameter(String recId) {
        String fragmentParameter;
        if (recId == null || recId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = recId;
        }

        Page page = EntryPoint.get().getPage();
        page.setUriFragment("!" + view.getName() + "/" + fragmentParameter, false);
    }

    public void enter(String recId) {
        if (recId != null && !recId.isEmpty()) {
            if (recId.equals("new")) {
                newRecord();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                	/*long pid = Long.parseLong(recId);
                    RequirementEntity rec = findRecord(pid);
                    view.selectRow(rec);*/
                } catch (NumberFormatException e) {
                }
            }
        }
    }
    
    public MappingEntity findRecord(Long recId) {
        try {
        	MappingEntity rec = (MappingEntity) BltClient.get().getEntityById(""+recId, 
        			MappingEntity.class, 
 				EntryPoint.get().getAccessControl().getTokenKey());
 			return rec;
 		} catch (Exception e) {
 			e.printStackTrace();
 			if(view!=null)
 				view.showError("Error: "+e.getMessage());
 		}
        return null;
    }

    public void saveRecord(MappingEntity rec, boolean saveAndNext) {
    	// unused
    	try {
    		MappingEntity res = null;
	    	if(rec.getID()==null){
	    		res = (MappingEntity) BltClient.get().createEntity(rec, MappingEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getID() + " created");
	    	}
	    	else{
	    		res = (MappingEntity) BltClient.get().updateEntity(rec, MappingEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null)
	    			view.showSaveNotification(res.getID() + " updated");
	    	}
	    	if(view!=null){
		        /*view.clearSelection();
		        view.refreshRecord(res);
		        if(saveAndNext){
		        	this.newRecord();
		        }
		        else{
		            view.editRecord((MappingEntity)null, null);
		            setFragmentParameter("");
		        }*/
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }
    
    public void saveRecord(MappingEntity rec, RequirementMapping req, 
    		String feature, String product,
    		boolean saveAndNext) {
    	try {
    		MappingEntity res = null;
	    	if(rec.getID()==null){
	    		res = (MappingEntity) BltClient.get().createEntity(rec, MappingEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null){
	    			view.showSaveNotification(res.getID() + " created");
	    			// Refresh Requirement in the Grid, as status changed
	    			req.setResolved(true);
	    			req.setMapping(feature);
	    			req.setProduct(product);
	    			req.setResponse(rec.getResponse());
	    			req.setDoc(rec.getDoc());
	    			//view.refreshRecord(req);
	    		}
	    	}
	    	else{
	    		res = (MappingEntity) BltClient.get().updateEntity(rec, MappingEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	    		if(view!=null){
	    			view.showSaveNotification(res.getID() + " updated");
	    			req.setMapping(feature);
	    			req.setProduct(product);
	    			req.setResponse(rec.getResponse());
	    			req.setDoc(rec.getDoc());
	    			view.refreshRecord(req);
	    		}
	    	}
	    	if(view!=null){
		        view.clearSelection();
		        view.refreshRecord(req);
		        if(saveAndNext){
		        	this.newRecord();
		        }
		        else{
		            view.editRecord((MappingEntity)null, null);
		            setFragmentParameter("");
		        }
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }
    
    public void saveRecord(RequirementMapping rec) {
    	// Usado en Import from XLS
    	try {
    		if(!rec.getResolved()) return; // Ignore unresolved requirements
    		MappingEntity mapRec = this.getMappingByReq(rec.getId());
    		if(mapRec==null){
    			mapRec = new MappingEntity();
    			mapRec.setReq(rec.getId());
    			mapRec.setDoc(-1);
    		}
    		else if(rec.getDoc()!=null && rec.getDoc()>0L){
    			mapRec.setDoc(rec.getDoc());
    		}
    		else{
    			mapRec.setDoc(-1);
    		}
    		mapRec.setResponse(rec.getResponse()); // TODO chequear que el valor de Response sea valido
			mapRec.setText(rec.getText());
			mapRec.setNotes(rec.getNotes());
			mapRec.setLaboreffort(rec.getLaboreffort());
			mapRec.setTotalcost(rec.getTotalcost());
    		
    		this.saveRecord(mapRec, rec, rec.getMapping(), rec.getProduct(), false);
    	} 
    	catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }

    public void deleteRecord(MappingEntity rec) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getID(), MappingEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result){
				if(view!=null)
					view.showSaveNotification(rec.getID() + " removed");
			}
			else{
				if(view!=null)
					view.showError("Error: "+rec.getID() + " cannot be removed");
			}
			if(view!=null){
				view.clearSelection();
		        view.editRecord((MappingEntity)null, null);
			}
	        setFragmentParameter("");
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }
    
    public void deleteRecord(MappingEntity rec, RequirementMapping req) {
    	try {
			boolean result = BltClient.get().deleteEntity(""+rec.getID(), MappingEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result){
				if(view!=null){
					view.showSaveNotification(rec.getID() + " removed");
					
					// Refresh Requirement in the Grid, as status changed
					req.setResolved(false);
					req.setMapping("");
	    			req.setProduct("");
	    			req.setResponse("");
	    			req.setDoc(-1L);
					view.refreshRecord(req);
				}
			}
			else{
				if(view!=null)
					view.showError("Error: "+rec.getID() + " cannot be removed");
			}
			if(view!=null){
				view.clearSelection();
		        view.editRecord((MappingEntity)null, null);
			}
	        setFragmentParameter("");
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    }

	public void editRecord(MappingEntity rec) {
		// unused
        /*if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getID() + "");
        }
        view.editRecord(rec);*/
		view.editRecord((MappingEntity)null, null);
    }

    public void newRecord() {
    	// unused
        /*view.clearSelection();
        setFragmentParameter("new");
        MappingEntity rec = new MappingEntity();
        view.editRecord(rec, null);*/
    }

    public void rowSelected(RequirementMapping rec) {
    	if(rec==null){
    		view.editRecord(null, null);
    		return;
    	}
    	MappingEntity mapRec = this.getMappingByReq(rec.getId());
		if(mapRec==null){
			mapRec = new MappingEntity();
			mapRec.setReq(rec.getId());
			mapRec.setDoc(-1);
			mapRec.setResponse("");
			mapRec.setText("");
			mapRec.setNotes("");
		}
        view.editRecord(mapRec, rec);
    }
    
	public MappingEntity getMappingByReq(Long reqId) {
		try {
			MappingEntity rec = (MappingEntity) BltClient.get().executeCommand(
				"/getbyreq/"+reqId, MappingEntity.class, 
				EntryPoint.get().getAccessControl().getTokenKey());
			return rec;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
        return null;
	}

	public void openSearchForm(Long project) {
		((MappingCrudView)view).openSearchForm(project);
	}
	
	public void closeSearchForm() {
		((MappingCrudView)view).closeSearchForm();
	}

	public void selectFeature(FeatureEntity rec) {
		((MappingCrudView)view).selectFeature(rec);
	}

	public void autoMapping(Collection<RequirementMapping> recs) {
		// Auto Mapping
		System.out.println("Entering Auto Mapping...");
		List<FeatureEntity> features = (List<FeatureEntity>) this.getAllFeatures();
		System.out.println(features.size()+" features will be used...");
		for(RequirementMapping rec:recs){
			if(rec.getResolved()) continue;
			autoMapReq(rec, features);
		}
	}

	private void autoMapReq(RequirementMapping req, List<FeatureEntity> features){
		System.out.println("Mapping Req="+req.toString());
		FeatureEntity selectedFeature = null;  
		Long selectedFeatureRate = 0L;
		
		// First calculate most appropriate feature
		for(FeatureEntity feature:features){
			Long featureRate = 0L;
			if(feature.getTags()==null || !feature.getActive()) continue;
			
			String[] valores = feature.getTags().split(",");
	    	for(String s:valores){
	    		if(s.isEmpty() || s.length()==0 || s.equals(" ")) continue;
	    		
	    		if(req.getDescription().toUpperCase().contains(s.toUpperCase()))
	    			featureRate++;	
	    	}
	    	
	    	if(featureRate>selectedFeatureRate){
	    		selectedFeatureRate = featureRate;
	    		selectedFeature = feature;
	    	}
		}
		
		// Create Mapping if selectedFeatureRate>0
		if(selectedFeatureRate>0){
			MappingEntity rec = new MappingEntity();
			rec.setDoc(selectedFeature.getID());
			rec.setReq(req.getId());
			rec.setText(Messages.get().getKey("reqautomapped"));
			rec.setTimestamp(new Date());
			rec.setLaboreffort(0);
        	rec.setTotalcost(0);
        	rec.setResponse(Messages.get().getKey("supported"));
        	rec.setImage(new byte[0]);
        	
        	this.saveRecord(rec, req, selectedFeature.getTitle(), "", false);
            System.out.println("Mapping Found for Feature "+selectedFeature.toString());
		}
		else
			System.out.println("Mapping Not Found");
		
	}
	
	private Collection<FeatureEntity> getAllFeatures() {
		// Aunque deberia estar en FeatureCrudLogic, voy a dejar esta función aquí
		// Para no crear una dependencia
		try {
        	Collection<FeatureEntity> recs = BltClient.get().getEntities( 
        		FeatureEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
        	return recs;
		} catch (Exception e) {
			e.printStackTrace();
			view.showRecords(null);
			view.showError("Error: "+e.getMessage());
		}
		return null;
	}
	
	public Collection<MappingEntity> getMappingByFeature(Long recId) {
		try {
        	Collection<MappingEntity> recs = BltClient.get().executeQuery("/getbyfeature/"+recId, 
        		MappingEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
        	return recs;
		} catch (Exception e) {
			e.printStackTrace();
			view.showRecords(null);
			view.showError("Error: "+e.getMessage());
		}
		return null;
	}

	public long countMappingsByfeature(Long recId) {
		try {
			MappingEntity rec = (MappingEntity) BltClient.get().executeCommand(
				"/countMappingsByfeature/"+recId, MappingEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return rec.getID();
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return 0;
	}

	@Override
	public void rowSelected(MappingEntity rec) {
		// unused
	}
	
	public List<RequirementMapping> getRequirementsMapped(List<Requirement2Entity> itemIds) {
		// TODO mover getRequirementsMapped() a BlogicTier ?
		List<RequirementMapping> recs = new ArrayList<RequirementMapping>();
		for(Requirement2Entity item:itemIds){
			RequirementMapping rec = new RequirementMapping();
			rec.setId(item.getId());
			rec.setCustomid(item.getCustomid());
			rec.setDescription(item.getDescription());
			rec.setUser(item.getUser());
			rec.setResolved(item.getResolved());
			MappingEntity map = this.getMappingByReq(item.getId());
			if(map!=null){
				rec.setResponse(map.getResponse());
				rec.setDoc(map.getDoc());
				rec.setText(map.getText());
				rec.setLaboreffort(map.getLaboreffort());
				rec.setTotalcost(map.getTotalcost());
				rec.setNotes(map.getNotes());
				rec.setMapping("");
				rec.setProduct("");
			}
			recs.add(rec);
		}
		return recs;
	}

	public Collection<RequirementMapping> getRequirementsMappedByProject(Long recId) {
		try {
			Collection<RequirementMapping> recs = BltClient.get().executeQuery(
					"/getbyproject/"+recId, RequirementMapping.class,
					EntryPoint.get().getAccessControl().getTokenKey());
        	return recs;
		} catch (Exception e) {
			e.printStackTrace();
			view.showRecords(null);
			view.showError("Error: "+e.getMessage());
		}
		return null;
	}
	
}
