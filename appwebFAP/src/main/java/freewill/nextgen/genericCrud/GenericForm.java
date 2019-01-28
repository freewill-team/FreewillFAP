package freewill.nextgen.genericCrud;

import org.vaadin.ui.NumberField;

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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Notification.Type;

import freewill.nextgen.hmi.common.GenericCombo;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */

@SuppressWarnings("serial")
public class GenericForm<T> extends CssLayout implements CustomFormInterface<T> {

    private GenericCrudLogic<T> viewLogic;
    private BeanFieldGroup<T> fieldGroup;
    Class<T> entity = null;
    
    private Button save;
    private Button cancel;
    private Button delete;

    @SuppressWarnings("rawtypes")
    public GenericForm(GenericCrudLogic<T> sampleCrudLogic, Class<T> entity, String idfield, String... fields) {
        
        viewLogic = sampleCrudLogic;
        this.entity = entity;

        fieldGroup = new BeanFieldGroup<T>(entity);
        createFormAndBind(fieldGroup, fields);
        addStyleName("product-form");

        // perform validation and enable/disable buttons while editing
        ValueChangeListener valueListener = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                formHasChanged();
            }
        };
        for (com.vaadin.ui.Field f : fieldGroup.getFields()) {
            f.addValueChangeListener(valueListener);
            f.setCaption(Messages.get().getKey(f.getCaption())); // Translations
        }

        fieldGroup.addCommitHandler(new CommitHandler() {

            @Override
            public void preCommit(CommitEvent commitEvent)
                    throws CommitException {
            	System.out.println("Entrando en preCommit...");
            }
            
            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	System.out.println("Entrando en postCommit...");
            	
            	T rec = fieldGroup.getItemDataSource().getBean();
            	
            	if(viewLogic!=null)
            		viewLogic.saveRecord(rec);
            	else 
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
            		removeStyleName("visible");
            }
        });

        delete.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog cd = new ConfirmDialog(Messages.get().getKey("reallyremovequestion"));
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			T rec = fieldGroup.getItemDataSource().getBean();
            			if(viewLogic!=null){
            				viewLogic.deleteRecord(rec);
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });
    }

	private void createFormAndBind(BeanFieldGroup<T> fieldGroup, String... fields) {
		// This method creates a vertical layout form for "entity" according to "fields"
    	
    	// First sets the form styles
    	this.setStyleName("product-form-wrapper");
    	VerticalLayout layout = new VerticalLayout();
    	layout.setStyleName("form-layout");
    	layout.setHeight("100%");
    	layout.setSpacing(true);
    	this.addComponent(layout);
    	
    	/*for(java.lang.reflect.Field member:entity.getDeclaredFields()){
    		 System.out.println("createFormAndBind: "+member.getName());
    	}*/
    	
    	// Add Text, Combo box, Check box Fields, etc for "fields"
    	for (String field : fields) {
    		try {
	            System.out.println("createFormAndBind - Adding field: "+field);
	            // Looks for field type
	            java.lang.reflect.Field member = entity.getDeclaredField(field);
	            if(member==null) continue;
				String paramType = member.getType().toGenericString();
				System.out.println("createFormAndBind - Param type: "+paramType);
				
	            // Creates and Adds to the layout a new field according to the type
				if("Date".equals(paramType) || paramType.contains("java.util.Date")){
					DateField newfield = new DateField(field);
					newfield.setWidth("100%");
		        	layout.addComponent(newfield);
		        	// Binds the new field
		        	fieldGroup.bind(newfield, field);
				}
	    	    else if("boolean".equals(paramType) || paramType.contains("java.lang.Boolean")
	    	    		|| "char".equals(paramType) || paramType.contains("java.lang.Char")){
	    	    	CheckBox newfield = new CheckBox(field);
	    	    	newfield.setWidth("100%");
		        	layout.addComponent(newfield);
		        	// Binds the new field
		        	fieldGroup.bind(newfield, field);
	    	    }
	    	    else if("int".equals(paramType) || paramType.contains("java.lang.Integer")
		        		|| "long".equals(paramType) || paramType.contains("java.lang.Long")
		        		){
	    	    	NumberField newfield = new NumberField(field);
	    	    	newfield.setNegativeAllowed(false);
	    	    	newfield.setDecimalAllowed(false);
	    	    	newfield.setMinValue(0);
	    	    	newfield.setMaxValue(99999999);
	    	    	newfield.removeAllValidators();
	    	    	newfield.setWidth("100%");
		        	layout.addComponent(newfield);
		        	// Binds the new field
		        	fieldGroup.bind(newfield, field);
		        	System.out.println("createFormAndBind: Integer > "+member.getName());
		        }
	    	    else if("double".equals(paramType) || paramType.contains("java.lang.Double")
		        		|| "float".equals(paramType) || paramType.contains("java.lang.Float")
		        		|| "BigDecimal".equals(paramType) || paramType.contains( "java.math.BigDecimal")
	    	    		){
		        	//newfield.addValidator(new RegexpValidator("[-]?[0-9]*\\.?,?[0-9]+", "This is not a number!"));
	    	    	NumberField newfield = new NumberField(field);
	    	    	newfield.setNegativeAllowed(true);
	    	    	newfield.setDecimalAllowed(true);
	    	    	newfield.setDecimalPrecision(3);
	    	    	newfield.removeAllValidators(); // 
	    	    	newfield.setWidth("100%");
		        	layout.addComponent(newfield);
		        	// Binds the new field
		        	fieldGroup.bind(newfield, field);
		        	System.out.println("createFormAndBind: Double > "+member.getName());
		        }
	    	    else if("String".equals(paramType) || paramType.contains("java.lang.String")) {
	    	    	TextField newfield = new TextField(field);
	    	    	newfield.setWidth("100%");
		        	layout.addComponent(newfield);
		        	// Binds the new field
		        	fieldGroup.bind(newfield, field);
	    	    }
	    	    else if(member.getType().isEnum()){
	    	    	ComboBox newfield = new ComboBox(field);
	    	    	newfield.setWidth("100%");
		        	layout.addComponent(newfield);
		        	for(Object item:member.getType().getEnumConstants())
		        		newfield.addItem(item);
		        	// Binds the new field
		        	fieldGroup.bind(newfield, field);
				}
	    	    else{
	    	    	@SuppressWarnings("unchecked")
					Class<T> clazz = (Class<T>) member.getType();
	    	    	GenericCombo<T> newfield = 
							new GenericCombo<T>(field, clazz);
	    	    	newfield.setWidth("100%");
		        	layout.addComponent(newfield);
		        	// Binds the new field
		        	fieldGroup.bind(newfield, field);
	    	    }
	        	
    		} catch (Exception e) {
				System.out.println("Error en createFormAndBind: "+e.getMessage());
			}
        }
    	
    	// Finally add the expander and buttons
		createButtons(layout);
	}
    
	private void createButtons(VerticalLayout layout) {
		// Add the expander and save/cancel/delete buttons
		CssLayout expander = new CssLayout();
    	expander.setStyleName("expander");
    	layout.addComponent(expander);
    	layout.setExpandRatio(expander, 1);
		
		save = new Button("Salvar");
    	save.setStyleName("primary");
    	layout.addComponent(save);
    	
    	cancel = new Button("Cancelar");
    	cancel.setStyleName("cancel");
    	layout.addComponent(cancel);
    	
    	delete = new Button("Eliminar");
    	delete.setStyleName("danger");
    	layout.addComponent(delete);
	}

	public void editRecord(T rec) {
    	try{
	        if (rec == null) {
	            rec = entity.newInstance();
	        }
	        System.out.println("Entrando en editRecord: "+rec);
	        fieldGroup.setItemDataSource(new BeanItem<T>(rec));
	        
	        // before the user makes any changes, disable validation error indicator
	        // of the product name field (which may be empty)
	        //Label.setValidationVisible(false);
	
	        // Scroll to the top
	        // As this is not a Panel, using JavaScript
	        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
	        Page.getCurrent().getJavaScript().execute(scrollScript);
	        
	        formHasChanged();
	        System.out.println("Saliendo de editRecord");
    	}
    	catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    }
	
    private void formHasChanged() {
        // show validation errors after the user has changed something
        //Label.setValidationVisible(true);
        
        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<T> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	T rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (viewLogic.getIdMethod(rec)!=null);	
        	}
        }
        delete.setEnabled(canRemoveRecord);
    }

	@Override
	public void setLogic(GenericCrudLogic<T> logic) {
		viewLogic = logic;
	}

	@Override
	public Component getComponent() {
		return this;
	}    
    
}
