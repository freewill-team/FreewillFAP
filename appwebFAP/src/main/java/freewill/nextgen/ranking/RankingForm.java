package freewill.nextgen.ranking;

import java.util.List;

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

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.data.RankingEntity;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.patinador.SelectPatinadorDialog;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class RankingForm extends RankingFormDesign {

    private BeanFieldGroup<RankingEntity> fieldGroup;
    private RankingCrudLogic viewLogic;
    
    @SuppressWarnings("rawtypes")
    public RankingForm(RankingCrudLogic logic) {
        super();
        addStyleName("product-form");
        viewLogic = logic;
        
        fieldGroup = new BeanFieldGroup<RankingEntity>(RankingEntity.class); 
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
            	//System.out.println("Entrando en postCommit...");
				RankingEntity rec = fieldGroup.getItemDataSource().getBean();
				if(viewLogic!=null){
					viewLogic.saveRecord(rec);
				}
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
                	e.printStackTrace();
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
            			RankingEntity rec = fieldGroup.getItemDataSource().getBean();
            			if(viewLogic!=null){
            				viewLogic.deleteRecord(rec);
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
        patinBtn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
				// Abre la ventana de seleccion de patinador
				List<PatinadorEntity> students = viewLogic.getPatinadores();
				
				SelectPatinadorDialog cd = new SelectPatinadorDialog(students);
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			PatinadorEntity user = cd.getSelected();
            			if(user!=null){
            				patinador.setValue(""+user.getId());
                    		nombre.setValue(user.getNombre());
                    		apellidos.setValue(user.getApellidos());
                    		club.setValue(""+user.getClub());
                    		clubStr.setValue(""+user.getClubStr());
            			}
                    }
                });
            	getUI().addWindow(cd);
			}
        });
        
    }
    
	public void editRecord(RankingEntity rec) {
        if (rec == null) {
            rec = new RankingEntity();
            fieldGroup.setItemDataSource(new BeanItem<RankingEntity>(rec));
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<RankingEntity>(rec));
        
        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        nombre.setValidationVisible(false);
        
        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        formHasChanged();
    }
	
	private void formHasChanged() {
        // show validation errors after the user has changed something
		nombre.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canEditRecord = false;
        BeanItem<RankingEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	RankingEntity rec = item.getBean();
        	if(rec!=null){
        		canEditRecord = (rec.getId() == null);
        	}
        }
        patinBtn.setEnabled(canEditRecord);
        patinador.setVisible(false);
        nombre.setEnabled(canEditRecord);
        apellidos.setEnabled(canEditRecord);
        club.setVisible(false);
        clubStr.setEnabled(canEditRecord);
        orden.setEnabled(canEditRecord);
        delete.setEnabled(!canEditRecord);
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
    }
    
}
