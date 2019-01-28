package freewill.nextgen.apoi;

import java.io.File;

import freewill.nextgen.deliverable.DeliverableCrudLogic;
import freewill.nextgen.feature.FeatureCrudLogic;
import freewill.nextgen.mapping.MappingCrudLogic;
import freewill.nextgen.product.ProductCrudLogic;
import freewill.nextgen.project.ProjectCrudLogic;
import freewill.nextgen.requirement.RequirementCrudLogic;

public abstract class Report {
	private boolean success = false;
	private File tempFile = null;
	protected FeatureCrudLogic featureLogic = new FeatureCrudLogic(null);
	protected ProductCrudLogic productLogic = new ProductCrudLogic(null);
	protected RequirementCrudLogic requirementLogic = new RequirementCrudLogic(null);
	protected ProjectCrudLogic projectLogic = new ProjectCrudLogic(null);
	protected MappingCrudLogic mappingLogic = new MappingCrudLogic(null);
	protected DeliverableCrudLogic deliverableLogic = new DeliverableCrudLogic(null);
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean ss) {
		success = ss;
	}

	public File getFile() {
		return tempFile;
	}
	
	public void setFile(File ff) {
		tempFile = ff;
	}
	
}
