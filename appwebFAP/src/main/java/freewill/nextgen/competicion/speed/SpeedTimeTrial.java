package freewill.nextgen.competicion.speed;

import java.io.File;
import java.util.List;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.SpeedKOSystemEntity.EliminatoriaEnum;
import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.SpeedTimeTrialEntity;
import freewill.nextgen.data.SpeedTimeTrialEntity.RondaEnum;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class SpeedTimeTrial extends VerticalLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("timetrial");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<SpeedTimeTrialEntity> grid;
	private SpeedTimeTrialForm1 form1;
	private SpeedTimeTrialForm2 form2;
	private SpeedCrudLogic viewLogic;
	private RondaEnum ronda = RondaEnum.PRIMERA;
	private SpeedCrudView parent = null;
	private EliminatoriaEnum eliminatoria = EliminatoriaEnum.CUARTOS;

	public SpeedTimeTrial(Long categoria, String labelcategoria, Long competicion, 
			String label, RondaEnum ronda, SpeedCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.ronda = ronda;
		this.parent = parent;
		
		viewLogic = new SpeedCrudLogic(this, null);
		
		if(ronda==RondaEnum.PRIMERA)
			grid = new GenericGrid<SpeedTimeTrialEntity>(SpeedTimeTrialEntity.class,
        		"id", "dorsal", "orden1", "nombre", "apellidos", "tiempoAjustado1", "valido1");
		else if(ronda==RondaEnum.SEGUNDA)
			grid = new GenericGrid<SpeedTimeTrialEntity>(SpeedTimeTrialEntity.class,
        		"id", "dorsal", "orden2", "nombre", "apellidos", "tiempoAjustado2", "valido2");
		else
			grid = new GenericGrid<SpeedTimeTrialEntity>(SpeedTimeTrialEntity.class,
	        	"id", "dorsal", "clasificacion", "nombre", "apellidos", "tiempoAjustado1", "valido1", "tiempoAjustado2", "valido2", "mejorTiempo");
        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
            	viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
        
        form1 = new SpeedTimeTrialForm1(viewLogic);
        form2 = new SpeedTimeTrialForm2(viewLogic);
        eliminatoria = viewLogic.existeKO(competicion, categoria);
        if(eliminatoria!=null){
        	form1.setEnabled(false);
        	form2.setEnabled(false);
        }
        
        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setSizeFull();
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.addComponent(grid);
        gridLayout.setExpandRatio(grid, 2);
        if(ronda==RondaEnum.PRIMERA){
	        gridLayout.addComponent(form1);
	        gridLayout.setExpandRatio(form1, 1);
        }
        else if(ronda==RondaEnum.SEGUNDA){
	        gridLayout.addComponent(form2);
	        gridLayout.setExpandRatio(form2, 1);
        }
        
		HorizontalLayout topLayout = createTopBar();
	    //addComponent(new GenericHeader(VIEW_NAME, FontAwesome.FOLDER));
	    addComponent(topLayout);
	    addComponent(gridLayout);
	    setSizeFull();
	    setExpandRatio(gridLayout, 1);
	    setStyleName("crud-main-layout");
	    
	    viewLogic.initGrid(this.competicion, this.categoria, this.ronda);
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public HorizontalLayout createTopBar() {
		
		ComboBox selectRonda = new ComboBox();
		selectRonda.setNullSelectionAllowed(false);
        for (EliminatoriaEnum s : EliminatoriaEnum.values()) {
        	selectRonda.addItem(s);
        }
        if(eliminatoria!=null){
        	selectRonda.setValue(eliminatoria);
        	selectRonda.setEnabled(false);
        }
        else
        	selectRonda.setValue(EliminatoriaEnum.CUARTOS);
		
		Button prevButton = new Button(Messages.get().getKey("prev"));
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Previous screen
            	if(ronda==RondaEnum.RESULTADOS)
            		parent.gotoTimeTrial(RondaEnum.SEGUNDA);
            	else if(ronda==RondaEnum.SEGUNDA)
            		parent.gotoTimeTrial(RondaEnum.PRIMERA);
            	else if(ronda==RondaEnum.PRIMERA)
            		parent.enter(null);
            }
        });
		//prevButton.setEnabled(ronda!=RondaEnum.PRIMERA);
		
		Button nextButton = new Button(Messages.get().getKey("next"));
		nextButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		nextButton.setIcon(FontAwesome.ARROW_RIGHT);
		nextButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Next screen
            	if(ronda==RondaEnum.PRIMERA)
            		parent.gotoTimeTrial(RondaEnum.SEGUNDA);
            	else if(ronda==RondaEnum.SEGUNDA)
            		parent.gotoTimeTrial(RondaEnum.RESULTADOS);
            	else{
            		if(eliminatoria!=null){
            			parent.gotoKOsystem(eliminatoria);
            		}
            		else{
            			int numPatines = grid.getContainerDataSource().size();
            			EliminatoriaEnum ronda = (EliminatoriaEnum) selectRonda.getValue();
            			int numRondas = ronda.ordinal() + 1;
            			int numKOPatines = (int)(Math.pow(2.0, numRondas));
            			if(numKOPatines>numPatines){
            				showError("No hay patinadores suficientes para "+ronda.toString());
            				return;
            			}
            			ConfirmDialog cd = new ConfirmDialog(
            					"Esta acción creará el arbol de enfrentamientos del\n" +
            					"KO-System y ya no podrá modificar datos del Time-Trial.\n" +
            					"¿ Desea continuar ?");
                    	cd.setOKAction(new ClickListener() {
                            @Override
                            public void buttonClick(final ClickEvent event) {
                    			cd.close();
                    			parent.gotoKOsystem((EliminatoriaEnum) selectRonda.getValue());
                            }
                        });
                    	getUI().addWindow(cd);
            		}
            	}
            }
        });
		nextButton.setEnabled(true);
		
		Button printButton = new Button(Messages.get().getKey("acta"));
		//printButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		printButton.setIcon(FontAwesome.DOWNLOAD);
		printButton.addClickListener(e -> {
    		File file = Export2Xls.get().createXLS(
    				(List<SpeedTimeTrialEntity>)grid.getContainerDataSource().getItemIds(),
    				SpeedTimeTrialEntity.class,
    				("Resultados Time-Trial"+competicionStr+" / "+categoriaStr).toUpperCase(),
    				"dorsal", "clasificacion", "nombre", "apellidos", "tiempoAjustado1", "valido1", "tiempoAjustado2", "valido2", "mejorTiempo");
    		if(file!=null){
    			FileResource resource = new FileResource(file);
    			Page.getCurrent().open(resource, "Export File", false);
    		    // Finally, removes the temporal file
    		    // file.delete();
    		}
        });
        
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
		
        Label competicionLabel = new Label(competicionStr+" / "+categoriaStr+" / "+ronda);
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
        if(ronda==RondaEnum.RESULTADOS)
        	topLayout.addComponent(selectRonda);
        topLayout.addComponent(prevButton);
        topLayout.addComponent(nextButton);
        if(ronda==RondaEnum.RESULTADOS)
        	topLayout.addComponent(printButton);
        topLayout.setComponentAlignment(competicionLabel, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(competicionLabel, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }
	
	public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
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

    public void selectRow(SpeedTimeTrialEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public SpeedTimeTrialEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(SpeedTimeTrialEntity rec) {
    	form1.setEnabled(eliminatoria==null && rec!=null);
    	form2.setEnabled(eliminatoria==null && rec!=null);
    	if(ronda==RondaEnum.PRIMERA)
    		form1.editRecord(rec);
    	else if(ronda==RondaEnum.SEGUNDA)
    		form2.editRecord(rec);
    }

    public void showRecords(List<SpeedTimeTrialEntity> records) {
        grid.setRecords(records);
        if(ronda==RondaEnum.PRIMERA)
			grid. sort("orden1", SortDirection.DESCENDING);
        else if(ronda==RondaEnum.SEGUNDA)
			grid. sort("orden2", SortDirection.DESCENDING);
        else if(ronda==RondaEnum.RESULTADOS)
			grid. sort("clasificacion", SortDirection.ASCENDING);
        if(records!=null && records.size()>0)
    		this.selectRow(records.get(0));
        else{
        	showError("No existen inscripciones para esta prueba!");
        	this.setEnabled(false);
        }
    }

    public void refreshRecord(SpeedTimeTrialEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(SpeedTimeTrialEntity rec) {
        // Not allowed here grid.remove(rec);
    }
    
}