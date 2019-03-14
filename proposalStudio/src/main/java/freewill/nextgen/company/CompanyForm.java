package freewill.nextgen.company;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.data.CompanyEntity.PlanEnum;
import freewill.nextgen.data.Style;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;
import freewill.nextgen.styles.StylesCrudLogic;
import freewill.nextgen.user.UserCrudLogic;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class CompanyForm extends CompanyFormDesign {
	
	private UserCrudLogic userLogic = new UserCrudLogic(null);
	private StylesCrudLogic stylesLogic = new StylesCrudLogic(null);
	private List<Style> styleList = null;
	
	public enum TemplateEnum{ 
		XLS("XLSX"),
		DOC("DOCX"),
		STYLES(".DOCX");
		private final String type;
		TemplateEnum(String t){ type = t; }
		public String toString(){ return type; } 
	}

    private CompanyCrudLogic viewLogic;
    private BeanFieldGroup<CompanyEntity> fieldGroup;

    @SuppressWarnings("rawtypes")
    public CompanyForm(CompanyCrudLogic sampleCrudLogic) {
        super();
        viewLogic = sampleCrudLogic;
        addStyleName("product-form");

        for (PlanEnum s : PlanEnum.values()) {
            Plan.addItem(s);
        }
        
        Expirationdate.setResolution(Resolution.DAY);
        Expirationdate.setLocale(new Locale(EntryPoint.get().getAccessControl().getLocale()));
        Laborcostrate.setDescription("Labor Cost Rate per Hour (p.e. €/h)");
        nextinvoice.setCaption(Messages.get().getKey("nextinvoice"));
        userscount.setCaption(Messages.get().getKey("userscount"));
        Active.setEnabled(false);
        lastinvoice.setEnabled(false);
        nextinvoice.setEnabled(false);
        userscount.setEnabled(false);
        image.setSizeFull();
        Add.addStyleName(ValoTheme.BUTTON_SMALL);
        Del.addStyleName(ValoTheme.BUTTON_SMALL);
        imageLayout.setExpandRatio(imageLayout2, 1L);
        imageLayout.setExpandRatio(image, 2L);
        
        Styles.setIcon(FontAwesome.PAGELINES);
        Styles.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        Docx.setIcon(FontAwesome.FILE_WORD_O);
        Docx.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        Xlsx.setIcon(FontAwesome.FILE_EXCEL_O);
        Xlsx.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

        fieldGroup = new BeanFieldGroup<CompanyEntity>(CompanyEntity.class);
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
            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Update Record...");
            	CompanyEntity rec = fieldGroup.getItemDataSource().getBean();
            	if(viewLogic!=null){
            		viewLogic.saveRecord(rec, false);
            		stylesLogic.saveStyles(styleList);
            	}
            	removeStyleName("visible");
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
                    Notification n = new Notification(
                            "Please re-check the fields", Type.ERROR_MESSAGE);
                    n.setDelayMsec(500);
                    n.show(getUI().getPage());
                }
            }
        });

        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if(viewLogic!=null)
            		viewLogic.cancelRecord();
            	else
            		fieldGroup.discard();
            	removeStyleName("visible");
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
            			CompanyEntity rec = fieldGroup.getItemDataSource().getBean();
            			if(viewLogic!=null)
            				viewLogic.deleteRecord(rec);
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
        Add.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	getUI().addWindow(new CompanyLoadImage(CompanyForm.this));
            }
        });
        
        Del.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	CompanyEntity rec = fieldGroup.getItemDataSource().getBean();
            	rec.setImage(new byte[0]);
            	image.setSource(null);
            	fieldGroup.setItemDataSource(new BeanItem<CompanyEntity>(rec));
            }
        });
        
        Styles.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	getUI().addWindow(new CompanyLoadTemplate(CompanyForm.this, TemplateEnum.STYLES));
            }
        });
        
        Docx.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	getUI().addWindow(new CompanyLoadTemplate(CompanyForm.this, TemplateEnum.DOC));
            }
        });
        
        Xlsx.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	getUI().addWindow(new CompanyLoadTemplate(CompanyForm.this, TemplateEnum.XLS));
            }
        });
        
    }

    public void editRecord(CompanyEntity rec) {
        if (rec == null) {
            rec = new CompanyEntity();
        }
        fieldGroup.setItemDataSource(new BeanItem<CompanyEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        Name.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        // Company Logo
        image.setSource(null);
        byte[] data = rec.getImage();
    	if(data!=null && data.length>0){
        	StreamResource resource = new StreamResource(
        			new StreamResource.StreamSource() {
        				@Override
        				public InputStream getStream() {
        					return new ByteArrayInputStream(data);
        				}
        	       }, rec.getImagename()/*rec.getName()+".jpg"*/);
        	image.setSource(resource);
    	}
    	
    	// Invoicing details
    	long users = /*viewLogic*/userLogic.countActiveUsers();
    	userscount.setValue(""+users);
    	long nextinvoicecost = 0;
    	if(users<6)
    		nextinvoicecost = 40 * users;
    	else if(users<6)
    		nextinvoicecost = 30 * users;
    	else
    		nextinvoicecost = 25 * users;
    	nextinvoice.setValue(""+nextinvoicecost);
        
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        Name.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<CompanyEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	CompanyEntity rec = item.getBean();
        	if(rec!=null)
        		canRemoveRecord = (rec.getID() != null);
        }
        delete.setEnabled(canRemoveRecord && viewLogic!=null);
        lastinvoice.setEnabled(false);
        
        Expirationdate.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        Plan.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        Active.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        save.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
        
        Styles.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
        Docx.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
        Xlsx.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
    	
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        save.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
    }

	public void addImage(File tempFile) {
		try {
    		FileResource resource = new FileResource(tempFile);
            image.setSource(resource);
        	// first converts File to byte[]
    		byte[] array = Files.readAllBytes(tempFile.toPath());
        	// then saves image into Company
    		CompanyEntity rec = fieldGroup.getItemDataSource().getBean();
        	rec.setImage(array);
        	rec.setImagename(tempFile.getName());
        	fieldGroup.setItemDataSource(new BeanItem<CompanyEntity>(rec));
        	// tempFile.delete();
    	} catch (IOException e) {
			e.printStackTrace();
			Notification n = new Notification(
                    "Error uploading image file", Type.ERROR_MESSAGE);
            n.setDelayMsec(500);
            n.show(getUI().getPage());
		}
	}
	
	public void addTemplate(File tempFile, TemplateEnum type, List<Style> styleList) {
		try {
    		byte[] array = Files.readAllBytes(tempFile.toPath());
        	// then saves image into Company
    		CompanyEntity rec = fieldGroup.getItemDataSource().getBean();
    		if( type==TemplateEnum.STYLES ){
    			this.styleList = styleList;
    		}
    		else if( type==TemplateEnum.DOC ){
    			rec.setDocxtemplate(array);
    		}
    		else{
    			rec.setXlsxtemplate(array);
    		}
        	fieldGroup.setItemDataSource(new BeanItem<CompanyEntity>(rec));
        	// tempFile.delete();
    	} catch (IOException e) {
			e.printStackTrace();
			Notification n = new Notification(
                    "Error uploading template file", Type.ERROR_MESSAGE);
            n.setDelayMsec(500);
            n.show(getUI().getPage());
		}
	}
	
}
