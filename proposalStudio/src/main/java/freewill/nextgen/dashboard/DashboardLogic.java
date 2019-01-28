package freewill.nextgen.dashboard;

import java.io.Serializable;
import java.util.Collection;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.MappingEntity;
import freewill.nextgen.data.ProductEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.RequirementEntity;
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
public class DashboardLogic implements Serializable {

    private DashboardView view;

    public DashboardLogic(DashboardView simpleCrudView) {
        view = simpleCrudView;
    }
    
    public Collection<RequirementEntity> getRequirementsByProject(Long prj) {
	    try {
	       	Collection<RequirementEntity> recs = BltClient.get().executeQuery("/getbyproject/"+prj, 
	       		RequirementEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return recs;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    	return null;
    }

    public void init() {
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    @SuppressWarnings("unused")
	private void setFragmentParameter(String recId) {
        String fragmentParameter;
        if (recId == null || recId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = recId;
        }

        Page page = EntryPoint.get().getPage();
        page.setUriFragment("!" + view.VIEW_NAME + "/" + fragmentParameter, false);
    }
    
    public ProjectEntity getProjectById(Long recId) {
    	try {
    		ProjectEntity rec = (ProjectEntity) BltClient.get().getEntityById(""+recId, 
    			ProjectEntity.class, 
 				EntryPoint.get().getAccessControl().getTokenKey());
 			return rec;
 		} catch (Exception e) {
 			e.printStackTrace();
 			if(view!=null)
 				view.showError("Error: "+e.getMessage());
 		}
        return null;
    }

	public long countActiveProducts() {
		try {
			ProductEntity rec = (ProductEntity) BltClient.get().executeCommand(
				"/countActiveProducts", ProductEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return rec.getID();
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return 0;
	}

	public long countNoActiveProducts() {
		try {
			ProductEntity rec = (ProductEntity) BltClient.get().executeCommand(
				"/countNoActiveProducts", ProductEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return rec.getID();
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return 0;
	}

	public long countClosedProjects() {
		try {
			ProjectEntity rec = (ProjectEntity) BltClient.get().executeCommand(
				"/countNoActiveProjects", ProjectEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return rec.getID();
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return 0;
	}

	public long countActiveProjects() {
		try {
			ProjectEntity rec = (ProjectEntity) BltClient.get().executeCommand(
				"/countActiveProjects", ProjectEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return rec.getID();
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return 0;
	}

	public long countActiveFeatures() {
		try {
			FeatureEntity rec = (FeatureEntity) BltClient.get().executeCommand(
				"/countActiveFeatures", FeatureEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return rec.getID();
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return 0;
	}

	public long countNoActiveFeatures() {
		try {
			FeatureEntity rec = (FeatureEntity) BltClient.get().executeCommand(
				"/countNoActiveFeatures", FeatureEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return rec.getID();
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return 0;
	}

	public long countPendingRequirements() {
		try {
			RequirementEntity rec = (RequirementEntity) BltClient.get().executeCommand(
				"/countPendingRequirements", RequirementEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return rec.getID();
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return 0;
	}

	public long countResolvedRequirements() {
		try {
			RequirementEntity rec = (RequirementEntity) BltClient.get().executeCommand(
				"/countResolvedRequirements", RequirementEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return rec.getID();
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
		return 0;
	}

	public Collection<ProjectEntity> getProjects() {
		try {
	       	Collection<ProjectEntity> recs = BltClient.get().getEntities(ProjectEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return recs;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    	return null;
	}
    
	public Collection<ProductEntity> getProducts() {
		try {
	       	Collection<ProductEntity> recs = BltClient.get().getEntities(ProductEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return recs;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    	return null;
	}

	public FeatureEntity getFeatureById(Long recId) {
		try {
			FeatureEntity rec = (FeatureEntity) BltClient.get().getEntityById(""+recId, 
				FeatureEntity.class, 
 				EntryPoint.get().getAccessControl().getTokenKey());
 			return rec;
 		} catch (Exception e) {
 			e.printStackTrace();
 			if(view!=null)
 				view.showError("Error: "+e.getMessage());
 		}
		return null;
	}
	
	public Collection<MappingEntity> getMappingByProject(Long project) {
		try {
	       	Collection<MappingEntity> recs = BltClient.get().executeQuery("/getbyproject/"+project, 
	       		MappingEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
	       	return recs;
		} catch (Exception e) {
			e.printStackTrace();
			if(view!=null)
				view.showError("Error: "+e.getMessage());
		}
    	return null;
	}

	public ProductEntity getProductById(Long recId) {
		try {
			ProductEntity rec = (ProductEntity) BltClient.get().getEntityById(""+recId, 
					ProductEntity.class, 
 				EntryPoint.get().getAccessControl().getTokenKey());
 			return rec;
 		} catch (Exception e) {
 			e.printStackTrace();
 			if(view!=null)
 				view.showError("Error: "+e.getMessage());
 		}
		return null;
	}
	
}
