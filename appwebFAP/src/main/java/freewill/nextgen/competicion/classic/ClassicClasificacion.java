package freewill.nextgen.competicion.classic;

import java.util.List;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.ClassicShowEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class ClassicClasificacion extends VerticalLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("classic");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<ClassicShowEntity> grid;
	private VerticalLayout form = null;
	private ClassicCrudLogic viewLogic;
	private ClassicCrudView parent = null;
	private ClassicShowEntity selectedRec = null;
	private boolean existeKO = false;

	public ClassicClasificacion(Long categoria, String labelcategoria, Long competicion, 
			String label, ClassicCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		
		viewLogic = new ClassicCrudLogic(this);
		
		grid = new GenericGrid<ClassicShowEntity>(ClassicShowEntity.class,
        		"id", "dorsal", "orden", "nombre", "apellidos");
		grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
            	viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
		
		//form = createUpDownButtons();
        
		existeKO = viewLogic.existeKO(competicion, categoria);
        if(existeKO==true){
        	// no permitir cambios si el arbol ya esta creado
        	//form.setEnabled(false);
        }
        
        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setSizeFull();
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.addComponents(grid, form);
        gridLayout.setExpandRatio(grid, 10);
        gridLayout.setExpandRatio(form, 1);
        
		HorizontalLayout topLayout = createTopBar();
	    //addComponent(new GenericHeader(VIEW_NAME, FontAwesome.FOLDER));
	    addComponent(topLayout);
	    addComponent(gridLayout);
	    setSizeFull();
	    setExpandRatio(gridLayout, 1);
	    setStyleName("crud-main-layout");
	    
	    viewLogic.initGrid(this.competicion, this.categoria);
	}
	
	
	public HorizontalLayout createTopBar() {
		
		Button prevButton = new Button(Messages.get().getKey("prev"));
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Previous screen
            }
        });
		prevButton.setEnabled(false);
		
		Button nextButton = new Button(Messages.get().getKey("next"));
		nextButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		nextButton.setIcon(FontAwesome.ARROW_RIGHT);
		nextButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Next screen
            	if(existeKO==true){
            		parent.gotoClassicFinal();
            	}
            	else{
            		ConfirmDialog cd = new ConfirmDialog(
            				"Esta acción creará la final de Classic\n" +
            				" y ya no podrá modificar los datos iniciales.\n" +
            				"¿ Desea continuar ?");
                    cd.setOKAction(new ClickListener() {
                        @Override
                        public void buttonClick(final ClickEvent event) {
                    		cd.close();
                    		parent.gotoClassicFinal();
                        }
                    });
                    getUI().addWindow(cd);
            	}
            }
        });
		nextButton.setEnabled(true);
        
		Button delete = new Button("");
		delete.addStyleName(ValoTheme.BUTTON_DANGER);
		delete.setIcon(FontAwesome.REMOVE);
		delete.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog cd = new ConfirmDialog("Realmente desea eliminar TODOS los datos?");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			if(viewLogic!=null){
            				viewLogic.deleteAll(competicion, categoria);
            				parent.showSaveNotification("Datos borrados.");
            				setEnabled(false);
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });
		
        Label competicionLabel = new Label(competicionStr+" / "+categoriaStr+" / Preclasificación");
        competicionLabel.setStyleName(ValoTheme.LABEL_LARGE);
        competicionLabel.addStyleName(ValoTheme.LABEL_COLORED);
        competicionLabel.addStyleName(ValoTheme.LABEL_BOLD);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setSpacing(true);
        topLayout.setMargin(true);
        topLayout.setWidth("100%");
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN))
        	topLayout.addComponent(delete);
        topLayout.addComponent(competicionLabel);
        topLayout.addComponent(prevButton);
        topLayout.addComponent(nextButton);
        topLayout.setComponentAlignment(competicionLabel, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(competicionLabel, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }
	
	public void showError(String msg) {
        Notification.show(msg, Type.WARNING_MESSAGE); //ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }
    
    public void clearSelection() {
    	try{
    		grid.getSelectionModel().reset();
    	}
    	catch(Exception e){
    		System.out.println("clearSelection: "+e.getMessage());
    	}
    }

    public void selectRow(ClassicShowEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public ClassicShowEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(ClassicShowEntity rec) {
    	selectedRec = rec;
    	form.setEnabled(selectedRec!=null && existeKO==false);
    }

    public void showRecords(List<ClassicShowEntity> records) {
        grid.setRecords(records);
        if(records!=null && records.size()>0)
        	if(selectedRec!=null)
        		this.selectRow(records.get(selectedRec.getOrden1()-1));
        	else
        		this.selectRow(records.get(0));
        else{
        	showError("No existen inscripciones para esta prueba!");
        	this.setEnabled(false);
        }
        	
    }

    public void refreshRecord(ClassicShowEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(ClassicShowEntity rec) {
        // Not allowed here grid.remove(rec);
    }
    
}
