package freewill.nextgen.product;

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
import com.vaadin.ui.Notification.Type;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.ProductEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class ProductForm extends ProductFormDesign {

    private ProductCrudLogic viewLogic;
    private BeanFieldGroup<ProductEntity> fieldGroup;

    @SuppressWarnings("rawtypes")
    public ProductForm(ProductCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        viewLogic = sampleCrudLogic;
        
        Description.setRows(4);

        fieldGroup = new BeanFieldGroup<ProductEntity>(ProductEntity.class);
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
            	//ProductEntity rec = fieldGroup.getItemDataSource().getBean();
            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Update Record...");
            	ProductEntity rec = fieldGroup.getItemDataSource().getBean();
            	viewLogic.saveRecord(rec, false);
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
                            /*"Please re-check the fields"*/, Type.ERROR_MESSAGE);
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
            	ConfirmDialog cd = new ConfirmDialog("Do you really want to remove this record?"
            			+"\n(This action will also remove related features)");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			ProductEntity rec = fieldGroup.getItemDataSource().getBean();
                        viewLogic.deleteRecord(rec);
                    }
                });
            	getUI().addWindow(cd);
            }
        });
    }

    public void editRecord(ProductEntity rec) {
        if (rec == null) {
            rec = new ProductEntity();
        }
        
        // Rellenar ComboBox Projects a partir de Company
        Project.removeAllItems();
		try {
			Collection<ProjectEntity> projects = BltClient.get().getEntities(ProjectEntity.class,
				EntryPoint.get().getAccessControl().getTokenKey());
			for (ProjectEntity s : projects) {
	        	Project.addItem(s.getID());
	        	Project.setItemCaption(s.getID(), s.getName());
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}	
        
        fieldGroup.setItemDataSource(new BeanItem<ProductEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        Name.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        if(rec.getID()!=null){
        	// calcula numero de features para este producto
        	long features = viewLogic.countFeaturesPerProduct(rec.getID());
        	Features.setValue(""+features);
        }
        else
        	Features.setValue("0");
        Features.setEnabled(false);
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        Name.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<ProductEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	ProductEntity rec = item.getBean();
        	if(rec!=null)
        		canRemoveRecord = (rec.getID() != null);
        }
        delete.setEnabled(canRemoveRecord);
        
        Company.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
        save.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }
}
