package freewill.nextgen.mapping;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.data.DeliverableEntity;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.MappingEntity;
import freewill.nextgen.data.ProductEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.RequirementEntity;
import freewill.nextgen.data.RequirementMapping;
import freewill.nextgen.deliverable.DeliverableCrudLogic;
import freewill.nextgen.feature.DocumentShowImage;
import freewill.nextgen.feature.FeatureCrudLogic;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.product.ProductCrudLogic;
import freewill.nextgen.project.ProjectCrudLogic;
import freewill.nextgen.proposalStudio.EntryPoint;
import freewill.nextgen.requirement.RequirementCrudLogic;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class MappingForm extends MappingFormDesign {

	private DeliverableCrudLogic deliLogic = new DeliverableCrudLogic(null);
	private FeatureCrudLogic featLogic = new FeatureCrudLogic(null);
	private RequirementCrudLogic reqLogic = new RequirementCrudLogic(null);
	private ProjectCrudLogic projLogic = new ProjectCrudLogic(null);
	private ProductCrudLogic prodLogic = new ProductCrudLogic(null);
    private MappingCrudLogic viewLogic;
    private BeanFieldGroup<MappingEntity> fieldGroup;
    private float laborcost = 0;
    private List<FeatureEntity> docs = null;
    private int docidx = 0;
    private boolean editable = false;
    private RequirementMapping req = null;

    @SuppressWarnings("rawtypes")
    public MappingForm(MappingCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        viewLogic = sampleCrudLogic;
        
        SearchLayout.setCaption(Messages.get().getKey("mappedto"));
        find.setCaption(Messages.get().getKey("advanced"));
        
        //prev.setStyleName(ValoTheme.BUTTON_SMALL);
        prev.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        //next.setStyleName(ValoTheme.BUTTON_SMALL);
        next.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        prev.setIcon(FontAwesome.CARET_LEFT);
        next.setIcon(FontAwesome.CARET_RIGHT);
        
        ReqDesc.setRows(4);
        DocDesc.setRows(4);
        Text.setRows(5);
        notes.setRows(4);
        
        DocDesc.setImmediate(true);
        DocDesc.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
            	MappingEntity rec = fieldGroup.getItemDataSource().getBean();
            	FeatureEntity doc = featLogic.findRecord(rec.getDoc());
            	if(doc!=null){
	            	System.out.println("Updating Id = " + doc);
	            	doc.setDescription(event.getText());
	            	featLogic.saveRecord(doc);
            	}
            }
        });
        
        Response.addValidator(new ResponseValidator());
        image.setSizeFull();
        Add.addStyleName(ValoTheme.BUTTON_SMALL);
        Del.addStyleName(ValoTheme.BUTTON_SMALL);
        imageLayout.setExpandRatio(imageLayout2, 1L);
        imageLayout.setExpandRatio(image, 2L);
        
        Search.setInputPrompt("search docs");
        Search.setImmediate(true);
        Search.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
            	if(event.getText().equals("")) return;
            	//MappingEntity rec = fieldGroup.getItemDataSource().getBean();
            	//RequirementEntity req = reqLogic.findRecord(rec.getReq());
                //if(req!=null && !event.getText().equals("")){
                    docs = (List<FeatureEntity>) featLogic.getFeaturesFiltered(event.getText());
                    setDocumentInfo(docidx=0);
                //}
            }
        });

        fieldGroup = new BeanFieldGroup<MappingEntity>(MappingEntity.class);
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
        Customid.setCaption(Messages.get().getKey("customid"));
        ReqDesc.setCaption(Messages.get().getKey("reqdesc"));
        Search.setCaption(Messages.get().getKey("search"));
        DocTitle.setCaption(Messages.get().getKey("title"));
        DocProduct.setCaption(Messages.get().getKey("product"));
        DocDesc.setCaption(Messages.get().getKey("description"));
        
        Req.setEnabled(false);
        Customid.setEnabled(false);
        ReqDesc.setEnabled(false);
        Doc.setEnabled(false);
        DocTitle.setEnabled(false);
        DocProduct.setEnabled(false);
        Totalcost.setEnabled(false);

        fieldGroup.addCommitHandler(new CommitHandler() {

            @Override
            public void preCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Commit...");
            	//MappingEntity rec = fieldGroup.getItemDataSource().getBean();
            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Update Record...");
            	
            	MappingEntity rec = fieldGroup.getItemDataSource().getBean();
            	rec.setTotalcost(rec.getLaboreffort() * laborcost);
            	//System.out.println("after Commit: "+rec.toString());
            	viewLogic.saveRecord(rec, req, DocTitle.getValue(), DocProduct.getValue(), false);
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
            	ConfirmDialog cd = new ConfirmDialog("Do you really want to remove this record?");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			MappingEntity rec = fieldGroup.getItemDataSource().getBean();
                        viewLogic.deleteRecord(rec, req);
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
        next.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	docidx++;
                setDocumentInfo(docidx);
            }
        });
        
        prev.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	docidx--;
                setDocumentInfo(docidx);
            }
        });
        
        Add.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	getUI().addWindow(new MappingLoadImage(MappingForm.this));
            }
        });
        
        Del.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	MappingEntity rec = fieldGroup.getItemDataSource().getBean();
            	rec.setImage(new byte[0]);
            	image.setSource(null);
            	fieldGroup.setItemDataSource(new BeanItem<MappingEntity>(rec));
            }
        });
        
        find.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	MappingEntity rec = fieldGroup.getItemDataSource().getBean();
            	RequirementEntity req = reqLogic.findRecord(rec.getReq());
                if(req!=null){
                	viewLogic.openSearchForm(req.getProject());
                }
            }
        });
        
        image.addClickListener(new MouseEvents.ClickListener() {
			@Override
			public void click(MouseEvents.ClickEvent event) {
				MappingEntity rec = fieldGroup.getItemDataSource().getBean();
				FeatureEntity feat = new FeatureEntity();
				feat.setImage(rec.getImage());
				feat.setImagename(rec.getImagename());
                if(feat.getImage()!=null){
                	getUI().addWindow(new DocumentShowImage(feat));
                }
			}
        });
        
    }
    
    private void setDocumentInfo(int idx){
    	if(docs==null){
    		Doc.setCaption(/*Messages.get().getKey("feature")+*/" Id (0/0)");
    		return;
    	}
    	if(docs.size()>0){
    		if(idx<0) idx=0;
    		if(idx>=docs.size()) idx=docs.size()-1;
    		MappingEntity rec = fieldGroup.getItemDataSource().getBean();
        	rec.setDoc(docs.get(idx).getID());
        	fieldGroup.setItemDataSource(new BeanItem<MappingEntity>(rec));
    		DocTitle.setValue(docs.get(idx).getTitle());
    		DocDesc.setValue(docs.get(idx).getDescription());
    		docidx=idx;
    		Doc.setCaption("Id ("+(docidx+1)+"/"+docs.size()+")");
    		DocProduct.setValue(getProduct(docs.get(idx)));
    	}
    	else{
    		MappingEntity rec = fieldGroup.getItemDataSource().getBean();
        	rec.setDoc(-1);
        	fieldGroup.setItemDataSource(new BeanItem<MappingEntity>(rec));
    		DocTitle.setValue("");
    		DocDesc.setValue("");
    		docidx=0;
    		Doc.setCaption("Id (0/0)");
    		DocProduct.setValue("");
    	}
    }

    private String getProduct(FeatureEntity rec) {
		ProductEntity prod = prodLogic.findRecord(rec.getProduct());
		if(prod!=null)
			return prod.getName();
		else
			return "";
	}

	public void editRecord(MappingEntity rec, RequirementMapping req, boolean editable) {
        if (rec == null) {
            rec = new MappingEntity();
        }
        this.editable = editable;
        Search.setValue("");
        this.req = req;
        //RequirementEntity req = reqLogic.findRecord(rec.getReq());
        FeatureEntity doc = featLogic.findRecord(rec.getDoc());
        
        if(req!=null){
	        // Rellenar ComboBox Response a partir de Project
        	ProjectEntity project = projLogic.findRecord(req.getProject());
	        Response.removeAllItems();
	        String[] valores = project.getAnswers().split(",");
	        for (String s : valores) {
	        	Response.addItem(s);
	        }
	        if(valores.length>0)
	        	Response.setValue(valores[0]);
	        // Set Requirement Text Area
	        ReqDesc.setValue(req.getDescription());
	        Customid.setValue(req.getCustomid());
	        
	        // Rellenar ComboBox deliverable
	        deliverable.removeAllItems();
	        Collection<DeliverableEntity> deliverables = deliLogic.getDeliverablesByProject(req.getProject());
	        for (DeliverableEntity s : deliverables) {
	        	deliverable.addItem(s.getID());
	        	deliverable.setItemCaption(s.getID(), s.getName());
	        }
        }
        else{
        	ReqDesc.setValue("");
        	Customid.setValue("");
        }
        
        if(doc!=null){
        	// Set Document Text Area
        	DocTitle.setValue(doc.getTitle());
	        DocDesc.setValue(doc.getDescription());
	        DocProduct.setValue(getProduct(doc));
        }
        else{
        	DocTitle.setValue("");
        	DocDesc.setValue("");
        	DocProduct.setValue("");
        }
        
        // Search doc list must be empty
        docs = null;
        Doc.setCaption(/*Messages.getInstance().getKey("feature")+*/" Id (0/0)");
        
        // Fijar labor costs
        CompanyEntity company = EntryPoint.get().getAccessControl().getCompany();
    	laborcost = company.getLaborcostrate();
        
        fieldGroup.setItemDataSource(new BeanItem<MappingEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        Req.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        // Other
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
    	
    	// Rellenar Internal Notes
        if(rec.getNotes()==null)
        	notes.setValue("");
        
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        Req.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<MappingEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	MappingEntity rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (rec.getID() != null);
        		if(rec.getNotes()==null)
                	notes.setValue("");
        	}
        }
        delete.setEnabled(canRemoveRecord && editable);
        save.setEnabled(editable);
        
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.READONLY));
        save.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.READONLY));
    }
    
    // Validator for validating the Response field
    public static final class ResponseValidator extends AbstractValidator<String> {

    	public ResponseValidator() {
    		super("The field 'Response' cannot be empty");
    	}

    	@Override
    	protected boolean isValidValue(String value) {
    		if (value != null && !value.equals("")) {
    			return true;
    		}
    		return false;
    	}
    	
    	@Override
    	public Class<String> getType() {
    		return String.class;
    	}
    	
    }
    
    public void addImage(File tempFile) {
		try {
    		FileResource resource = new FileResource(tempFile);
            image.setSource(resource);
        	// first converts File to byte[]
    		byte[] array = Files.readAllBytes(tempFile.toPath());
        	// then saves image into Project
    		MappingEntity rec = fieldGroup.getItemDataSource().getBean();
        	rec.setImage(array);
        	rec.setImagename(tempFile.getName());
        	fieldGroup.setItemDataSource(new BeanItem<MappingEntity>(rec));
        	// tempFile.delete();
    	} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void selectFeature(FeatureEntity rec) {
		Search.setValue("");
		docs = new ArrayList<FeatureEntity>();
		docs.add(rec);
		setDocumentInfo(docidx=0);
	}
    
}
