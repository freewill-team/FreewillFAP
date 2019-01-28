package freewill.nextgen.user;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.LanguageEnum;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
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
public class UserForm extends UserFormDesign {

    private UserCrudLogic viewLogic;
    private BeanFieldGroup<UserEntity> fieldGroup;
    private boolean passwordChanged = false;

    @SuppressWarnings("rawtypes")
    public UserForm(UserCrudLogic sampleCrudLogic) {
        super();
        viewLogic = sampleCrudLogic;
        addStyleName("product-form");
        
        for (UserRoleEnum s : UserRoleEnum.values()) {
        	if (s==UserRoleEnum.SUPER &&
        		!EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER))
        		continue; // Avoid other users taking over SuperUser role
            Role.addItem(s);
        }
        for (LanguageEnum s : LanguageEnum.values()) {
            Language.addItem(s);
        }
        
        Password.addValidator(new PasswordValidator());
        Password.setRequired(true);
        //Password.setVisible(false);
        Active.setEnabled(false);
        
        // CPassword = new PasswordField("Confirm Password");

        fieldGroup = new BeanFieldGroup<UserEntity>(UserEntity.class);
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
        
        ValueChangeListener passwordListener = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
            	 passwordChanged = true;
            }
        };
        Password.addValueChangeListener(passwordListener);

        fieldGroup.addCommitHandler(new CommitHandler() {

            @Override
            public void preCommit(CommitEvent commitEvent)
                    throws CommitException {
            	System.out.println("Entrando en Commit...");
            }
            
            @Override
            public void postCommit(CommitEvent commitEvent)
                    /*throws CommitException*/ {
            	UserEntity rec = fieldGroup.getItemDataSource().getBean();
            	
            	if(passwordChanged){
	            	String digestedPassword = EntryPoint.get().getAccessControl().getMD5Digest(Password.getValue());
	            	rec.setPassword(digestedPassword);
            	}
            	
            	if(viewLogic!=null)
	            	viewLogic.saveRecord(rec, false);
            	removeStyleName("visible");
            }
        });

        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                	if (!Password.isValid() ) return;
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
            			UserEntity rec = fieldGroup.getItemDataSource().getBean();
            			if(viewLogic!=null)
            				viewLogic.deleteRecord(rec);
                    }
                });
            	getUI().addWindow(cd);
            }
        });
    }

    public void editRecord(UserEntity rec) {
        if (rec == null) {
            rec = new UserEntity();
        }
        fieldGroup.setItemDataSource(new BeanItem<UserEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        Name.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        passwordChanged = false;
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        Name.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<UserEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	UserEntity rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (rec.getID() != null);
        	}
        }
        delete.setEnabled(canRemoveRecord && viewLogic!=null);
        
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        Company.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        Active.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
        Role.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
        Loginname.setEnabled(!canRemoveRecord);
    }
    
    // Validator for validating the passwords
    public static final class PasswordValidator extends AbstractValidator<String> {

    	public PasswordValidator() {
    		super("The password provided is not valid");
    	}

    	@Override
    	protected boolean isValidValue(String value) {
    		// Password must be at least 8 characters long and contain at least
    		// one number
    		if (value != null && (value.length() < 8 || !value.matches(".*\\d.*"))) {
    			return false;
    		}
    		return true;
    	}
    	
    	@Override
    	public Class<String> getType() {
    		return String.class;
    	}
    	
    }
    
}
