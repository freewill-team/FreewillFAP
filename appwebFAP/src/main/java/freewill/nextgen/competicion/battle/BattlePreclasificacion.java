package freewill.nextgen.competicion.battle;

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
import freewill.nextgen.data.BattleEntity;
import freewill.nextgen.data.BattleRondaEntity.EliminatoriaEnum;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class BattlePreclasificacion extends VerticalLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("Battle");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<BattleEntity> grid;
	private VerticalLayout form = null;
	private BattleCrudLogic viewLogic;
	private BattleCrudView parent = null;
	private EliminatoriaEnum eliminatoria = EliminatoriaEnum.SEMIS;
	private BattleEntity selectedRec = null;

	public BattlePreclasificacion(Long categoria, String labelcategoria, Long competicion, 
			String label, BattleCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		
		viewLogic = new BattleCrudLogic(this, null);
		
		grid = new GenericGrid<BattleEntity>(BattleEntity.class,
        		"id", "dorsal", "orden", "nombre", "apellidos");
		grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
            	viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
		
		form = createUpDownButtons();
        
        eliminatoria = viewLogic.existeKO(competicion, categoria);
        if(eliminatoria!=null){
        	// no permitir cambios si el arbol ya esta creado
        	form.setEnabled(false);
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
	
	private VerticalLayout createUpDownButtons() {
		
		Button upButton = new Button();
		upButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		upButton.setIcon(FontAwesome.ARROW_UP);
		upButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Sube orden registro seleccionado
            	if(selectedRec!=null && selectedRec.getOrden()>1){
            		selectedRec = viewLogic.moveRecordUp(selectedRec);
            		//System.out.println("selectedRec="+selectedRec);
            		viewLogic.initGrid(selectedRec.getCompeticion(), selectedRec.getCategoria());
            	}
            }
        });
		
		Button downButton = new Button();
		downButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		downButton.setIcon(FontAwesome.ARROW_DOWN);
		downButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	// baja orden registro seleccionado
            	if(selectedRec!=null && selectedRec.getOrden()<grid.getContainerDataSource().size()){
            		selectedRec = viewLogic.moveRecordDown(selectedRec);
            		//System.out.println("selectedRec="+selectedRec);
            		viewLogic.initGrid(selectedRec.getCompeticion(), selectedRec.getCategoria());
            	}
            }
        });
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
	    layout.setMargin(false);
	    layout.addComponents(upButton, downButton);
		return layout;
	}

	public HorizontalLayout createTopBar() {
		
		Button prevButton = new Button(Messages.get().getKey("prev"));
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Previous screen
            	parent.enter(null);
            }
        });
		//prevButton.setEnabled(false);
		
		Button nextButton = new Button(Messages.get().getKey("next"));
		nextButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		nextButton.setIcon(FontAwesome.ARROW_RIGHT);
		nextButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Next screen
            	if(eliminatoria!=null){
            		parent.gotoKO(eliminatoria);
            	}
            	else{
            		ConfirmDialog cd = new ConfirmDialog(
            				"Esta acción creará el arbol de enfrentamientos de \n" +
            				"Battle y ya no podrá modificar los datos iniciales.\n" +
            				"¿ Desea continuar ?");
                    cd.setOKAction(new ClickListener() {
                        @Override
                        public void buttonClick(final ClickEvent event) {
                    		cd.close();
                    		parent.gotoKO(eliminatoria);
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

    public void selectRow(BattleEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public BattleEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(BattleEntity rec) {
    	selectedRec = rec;
    	form.setEnabled(selectedRec!=null && eliminatoria==null);
    }

    public void showRecords(List<BattleEntity> records) {
        grid.setRecords(records);
        if(records!=null && records.size()>0)
        	if(selectedRec!=null)
        		this.selectRow(records.get(selectedRec.getOrden()-1));
        	else
        		this.selectRow(records.get(0));
        else{
        	showError("No existen inscripciones para esta prueba!");
        	this.setEnabled(false);
        }
        	
    }

    public void refreshRecord(BattleEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(BattleEntity rec) {
        // Not allowed here grid.remove(rec);
    }
    
}
