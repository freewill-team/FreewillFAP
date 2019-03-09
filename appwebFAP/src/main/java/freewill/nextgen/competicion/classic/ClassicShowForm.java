package freewill.nextgen.competicion.classic;

import com.google.gwt.user.client.ui.Grid;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.ClassicShowEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class ClassicShowForm extends ClassicShowFormDesign {
	
    private ClassicCrudLogic viewLogic;
    private BeanFieldGroup<ClassicShowEntity> fieldGroup;
    
    @SuppressWarnings("rawtypes")
    public ClassicShowForm(ClassicCrudLogic logic) {
        super();
        //addStyleName("product-form");
        this.viewLogic = logic;
        this.setMargin(true);
        this.setSpacing(true);
        
        penalizaciones.setRequired(true);
        tecnicaJuez1.setRequired(true);
        artisticaJuez1.setRequired(true);
        tecnicaJuez2.setRequired(true);
        artisticaJuez2.setRequired(true);
        tecnicaJuez3.setRequired(true);
        artisticaJuez3.setRequired(true);
        expand.setIcon(FontAwesome.ARROW_LEFT);
        
        fieldGroup = new BeanFieldGroup<ClassicShowEntity>(ClassicShowEntity.class);
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
            	ClassicShowEntity rec = fieldGroup.getItemDataSource().getBean();
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
                    Notification n = new Notification(
                            "Please re-check the fields", Type.ERROR_MESSAGE);
                    n.setDelayMsec(500);
                    n.show(getUI().getPage());
                }
            }
        });
        
        expand.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	if(viewLogic!=null){
            		if(viewLogic.setGridVisibility()){
            			 expand.setIcon(FontAwesome.ARROW_LEFT);
            			 expand.setCaption("Expandir");
            		}
            		else{
            			 expand.setIcon(FontAwesome.ARROW_RIGHT);
            			 expand.setCaption("Contraer");
            		}
            	}
            }
        });
        
        tabSheet.addSelectedTabChangeListener(new SelectedTabChangeListener(){
        	@Override
			public void selectedTabChange(SelectedTabChangeEvent e) {
        		if(viewLogic==null || tabSheet.getSelectedTab()==null) return;
        		Tab tab = tabSheet.getTab(tabSheet.getSelectedTab());
        		int pos = tabSheet.getTabPosition(tab);
            	//System.out.println("Selected Tab = "+pos);
            	viewLogic.setGridColumns(pos);
        	}
		});
        
        save1.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	try{
	            	ClassicShowEntity rec = fieldGroup.getItemDataSource().getBean();
	            	rec.setPenalizaciones(Float.parseFloat(penalizaciones.getValue()));
	            	rec.setArtisticaJuez1(Integer.parseInt(artisticaJuez1b.getValue()));
	            	rec.setTecnicaJuez1(Integer.parseInt(tecnicaJuez1b.getValue()));
	            	if(viewLogic!=null)
	            		viewLogic.saveJuez(rec, 1);
	            } catch (Exception e) {
	            	System.out.println(e.getMessage());
	                Notification n = new Notification(
	                        "Please re-check the fields", Type.ERROR_MESSAGE);
	                n.setDelayMsec(500);
	                n.show(getUI().getPage());
	            }
            }
        });
        
        save2.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	try{
	            	ClassicShowEntity rec = fieldGroup.getItemDataSource().getBean();
	            	rec.setArtisticaJuez2(Integer.parseInt(artisticaJuez2b.getValue()));
	            	rec.setTecnicaJuez2(Integer.parseInt(tecnicaJuez2b.getValue()));
	            	if(viewLogic!=null)
	            		viewLogic.saveJuez(rec, 2);
	            } catch (Exception e) {
	            	System.out.println(e.getMessage());
	                Notification n = new Notification(
	                        "Please re-check the fields", Type.ERROR_MESSAGE);
	                n.setDelayMsec(500);
	                n.show(getUI().getPage());
	            }
            }
        });
        
        save3.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	try{
	            	ClassicShowEntity rec = fieldGroup.getItemDataSource().getBean();
	            	rec.setArtisticaJuez3(Integer.parseInt(artisticaJuez3b.getValue()));
	            	rec.setTecnicaJuez3(Integer.parseInt(tecnicaJuez3b.getValue()));
	            	if(viewLogic!=null)
	            		viewLogic.saveJuez(rec, 3);
	            } catch (Exception e) {
	            	System.out.println(e.getMessage());
	                Notification n = new Notification(
	                        "Please re-check the fields", Type.ERROR_MESSAGE);
	                n.setDelayMsec(500);
	                n.show(getUI().getPage());
	            }
            }
        });
        
    }
    
    public void editRecord(ClassicShowEntity rec) {
        if (rec == null) {
        	//TODO MMFL esto no deberia ocurrir borrar
            rec = new ClassicShowEntity();
            fieldGroup.setItemDataSource(new BeanItem<ClassicShowEntity>(rec));
            save.setEnabled(false);
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<ClassicShowEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        //nombre.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        artisticaJuez1b.setValue(""+rec.getArtisticaJuez1());
    	tecnicaJuez1b.setValue(""+rec.getTecnicaJuez1());
    	artisticaJuez2b.setValue(""+rec.getArtisticaJuez2());
    	tecnicaJuez2b.setValue(""+rec.getTecnicaJuez2());
    	artisticaJuez3b.setValue(""+rec.getArtisticaJuez3());
    	tecnicaJuez3b.setValue(""+rec.getTecnicaJuez3());
        
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        //nombre.setValidationVisible(true);
        /*
        BeanItem<ClassicShowEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	ClassicShowEntity rec = item.getBean();
        	if(rec!=null){
        		//
        	}
        }
        */
        save.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }
    		
}
