package freewill.nextgen.competicion.classic;

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
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.ClassicShowEntity;
import freewill.nextgen.data.ClubEntity;
import freewill.nextgen.data.SlalomTrickEntity;
import freewill.nextgen.data.SlalomTrickEntity.TrickFamilyEnum;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class SlalomMatrixForm extends SlalomMatrixFormDesign {
	
    private ClassicCrudLogic viewLogic;
    //private BeanFieldGroup<ClassicShowEntity> fieldGroup;
    private ClassicShowEntity classicShow = null;
    private int points = 0;
    
    @SuppressWarnings("rawtypes")
    public SlalomMatrixForm(ClassicCrudLogic logic) {
        super();
        addStyleName("product-form");
        this.viewLogic = logic;
        
        
        //fieldGroup = new BeanFieldGroup<ClassicShowEntity>(ClassicShowEntity.class);
        //fieldGroup.bindMemberFields(this);

        // perform validation and enable/disable buttons while editing
        /*ValueChangeListener valueListener = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                formHasChanged();
            }
        };
        for (Field f : fieldGroup.getFields()) {
            f.addValueChangeListener(valueListener);
            f.setCaption(Messages.get().getKey(f.getCaption())); // Translations
        }*/

        /*fieldGroup.addCommitHandler(new CommitHandler() {

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
        });*/

        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                /*try {
                    fieldGroup.commit();
                    ClassicShowEntity rec = fieldGroup.getItemDataSource().getBean();
                	classicShow.setYotalJuezX(....);
                    // only if validation succeeds
                } catch (CommitException e) {
                	System.out.println(e.getMessage());
                    Notification n = new Notification(
                            "Please re-check the fields", Type.ERROR_MESSAGE);
                    n.setDelayMsec(500);
                    n.show(getUI().getPage());
                }*/
            }
        });
        
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	close();
            }
        });
        
        /*tabSheet.addSelectedTabChangeListener(new SelectedTabChangeListener(){
        	@Override
			public void selectedTabChange(SelectedTabChangeEvent e) {
        		if(viewLogic==null || tabSheet.getSelectedTab()==null) return;
        		Tab tab = tabSheet.getTab(tabSheet.getSelectedTab());
        		int pos = tabSheet.getTabPosition(tab);
            	//System.out.println("Selected Tab = "+pos);
            	viewLogic.setGridColumns(pos);
        	}
		});*/
        
        try{
        	// Rellenar ComboBox elasticidad
        	elasticidad.removeAllItems();
            Collection<SlalomTrickEntity> recs = BltClient.get().executeQuery(
            		"/getbyfamily/"+TrickFamilyEnum.ELASTICIDAD.name(),
            		SlalomTrickEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (SlalomTrickEntity s : recs) {
    			elasticidad.addItem(s);
    			elasticidad.setItemCaption(s, s.getNombre()+" ("+s.getValor()+")");
    	    }
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }
        
        addElasticidad.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	SlalomTrickEntity rec = (SlalomTrickEntity) elasticidad.getValue();
            	if(rec!=null){
            		points += rec.getValor();
            		puntuacion.setValue(""+points);
            	}
            }
        });
        
        try{
        	// Rellenar ComboBox sentados
        	sentados.removeAllItems();
            Collection<SlalomTrickEntity> recs = BltClient.get().executeQuery(
            		"/getbyfamily/"+TrickFamilyEnum.SENTADOS.name(),
            		SlalomTrickEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (SlalomTrickEntity s : recs) {
    			sentados.addItem(s);
    			sentados.setItemCaption(s, s.getNombre()+" ("+s.getValor()+")");
    	    }
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }
        
        addSentados.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	SlalomTrickEntity rec = (SlalomTrickEntity) sentados.getValue();
            	if(rec!=null){
            		points += rec.getValor();
            		puntuacion.setValue(""+points);
            	}
            }
        });
        
    }
    
    public void editRecord(ClassicShowEntity rec) {
        if (rec == null) {
            rec = new ClassicShowEntity();
            //fieldGroup.setItemDataSource(new BeanItem<ClassicShowEntity>(rec));
            //save.setEnabled(false);
            return;
        }
        //fieldGroup.setItemDataSource(new BeanItem<ClassicShowEntity>(rec));
        classicShow = rec;

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        //nombre.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
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
    	puntuacion.setEnabled(false);
        //save.setEnabled(editable && EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }

	public void close(){
		removeStyleName("visible");
		setEnabled(false);
	}
    		
}
