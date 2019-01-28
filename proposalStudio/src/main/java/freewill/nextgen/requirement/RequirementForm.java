package freewill.nextgen.requirement;

import java.util.Collection;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.MappingEntity;
import freewill.nextgen.data.ProductEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.Requirement2Entity;
import freewill.nextgen.data.RequirementEntity;
import freewill.nextgen.data.RequirementEntity.ReqCategoryEnum;
import freewill.nextgen.data.RequirementEntity.ReqTypeEnum;
import freewill.nextgen.feature.FeatureCrudLogic;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.mapping.MappingCrudLogic;
import freewill.nextgen.product.ProductCrudLogic;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class RequirementForm extends RequirementFormDesign {

	private FeatureCrudLogic featLogic = new FeatureCrudLogic(null);
	private MappingCrudLogic mappLogic = new MappingCrudLogic(null);
	private ProductCrudLogic prodLogic = new ProductCrudLogic(null);
    private RequirementCrudLogic viewLogic;
    private BeanFieldGroup<RequirementEntity> fieldGroup;
    private boolean editable = false;
    private Requirement2Entity rec2 = null;

    @SuppressWarnings("rawtypes")
    public RequirementForm(RequirementCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        viewLogic = sampleCrudLogic;
        
        Description.setRows(6);
        DocDesc.setRows(4);
        MappingLayout.setCaption(Messages.get().getKey("mappedto"));
        Response.setCaption(Messages.get().getKey("response"));
        DocTitle.setCaption(Messages.get().getKey("title"));
        DocProduct.setCaption(Messages.get().getKey("product"));
        DocDesc.setCaption(Messages.get().getKey("description"));
        Laboreffort.setCaption(Messages.get().getKey("laboreffort"));
        Totalcost.setCaption(Messages.get().getKey("totalcost"));
        Text.setCaption(Messages.get().getKey("additionalanswer"));
        Text.setRows(4);
        MappingLayout.setEnabled(false);
        
        for (ReqTypeEnum s : ReqTypeEnum.values()) {
            Type.addItem(s);
        }
        
        for (ReqCategoryEnum s : ReqCategoryEnum.values()) {
        	Category.addItem(s);
        }

        fieldGroup = new BeanFieldGroup<RequirementEntity>(RequirementEntity.class);
        fieldGroup.bindMemberFields(this);

        // perform validation and enable/disable buttons while editing
        ValueChangeListener valueListener = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                formHasChanged();
            }
        };
        for (Field f : fieldGroup.getFields()) {
            f.addValueChangeListener(valueListener);
            f.setCaption(Messages.get().getKey(f.getCaption())); // Translations
        }

        fieldGroup.addCommitHandler(new CommitHandler() {

            @Override
            public void preCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Commit...");
            	//RequirementEntity rec = fieldGroup.getItemDataSource().getBean();
            	//System.out.println("Requirement Resolved = "+rec.getResolved());
            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Update Record...");
            	RequirementEntity rec = fieldGroup.getItemDataSource().getBean();
            	viewLogic.saveRecord(rec, rec2, saveAndNext.getValue());
            }
        });

        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    fieldGroup.commit();
                    // only if validation succeeds
                } catch (CommitException e) {
                	System.out.println(e.getMessage());
                    Notification n = new Notification( e.getMessage()
                            /*"Please re-check the fields"*/, 
                            com.vaadin.ui.Notification.Type.ERROR_MESSAGE);
                    n.setDelayMsec(500);
                    n.show(getUI().getPage());
                }
            }
        });

        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                viewLogic.cancelRecord();
            }
        });

        delete.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog cd = new ConfirmDialog("Do you really want to remove this record?");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			//RequirementEntity rec = fieldGroup.getItemDataSource().getBean();
            			viewLogic.deleteRecord(rec2);
                    }
                });
            	getUI().addWindow(cd);
            }
        });
    }

    public void editRecord(RequirementEntity rec, Requirement2Entity rec2, boolean editable) {
        if (rec == null) {
            rec = new RequirementEntity();
        }
        this.editable = editable;
        this.rec2 = rec2;
        
        // Rellenar ComboBox Projects a partir de Company
        Project.removeAllItems();
        try{
	        Collection<ProjectEntity> projects = BltClient.get().getEntities(ProjectEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	        for (ProjectEntity s : projects) {
	        	Project.addItem(s.getID());
	        	Project.setItemCaption(s.getID(), s.getName());
	        }
	    }
        catch(Exception e){
        	e.printStackTrace();
        }
        
        Assignedto.removeAllItems();
        try{
	        Collection<UserEntity> users = BltClient.get().getEntities(UserEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	        for (UserEntity s : users) {
	        	Assignedto.addItem(s.getID());
	        	Assignedto.setItemCaption(s.getID(), s.getName());
	        }
	    }
        catch(Exception e){
        	e.printStackTrace();
        }
        
        fieldGroup.setItemDataSource(new BeanItem<RequirementEntity>(rec));

        Response.setValue("");
        DocTitle.setValue("");
        DocProduct.setValue("");
        DocDesc.setValue("");
        Laboreffort.setValue("");
        Totalcost.setValue("");
        Text.setValue("");
        
        if(rec.getID()!=null){
        	MappingEntity map = mappLogic.getMappingByReq(rec.getID());
	        if(map!=null){
	        	Response.setValue(map.getResponse());
	        	if(map.getDoc()!=null){
	        		FeatureEntity feat = featLogic.findRecord(map.getDoc());
	        		if(feat!=null){
	        			DocTitle.setValue(feat.getTitle());
	        			DocDesc.setValue(feat.getDescription());
	        			if(feat.getProduct()!=null && feat.getProduct()>0L){
	        				ProductEntity product = prodLogic.findRecord(feat.getProduct());
	        				DocProduct.setValue(product.getName());
	        			}
	        		}
	        	}
	        	Laboreffort.setValue(""+map.getLaboreffort());
	            Totalcost.setValue(""+map.getTotalcost());
	            Text.setValue(map.getText());
	        }
        }
        MappingLayout.setVisible(rec.getResolved());
        
        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        Customid.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        Customid.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<RequirementEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	RequirementEntity rec = item.getBean();
        	if(rec!=null)
        		canRemoveRecord = (rec.getID() != null);
        }
        delete.setEnabled(canRemoveRecord && editable);
        save.setEnabled(editable);
        
        Assignedto.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
        Company.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        Resolved.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
        
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER));
        save.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER));
    }
}
