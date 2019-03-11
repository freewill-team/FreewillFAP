package freewill.nextgen.competicion.speed;

import java.io.File;
import java.util.Date;
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
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.SpeedKOSystemEntity.EliminatoriaEnum;
import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.SpeedTimeTrialEntity;
import freewill.nextgen.data.SpeedTimeTrialEntity.RondaEnum;
import freewill.nextgen.genericCrud.GenericCrudLogic;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class SpeedTimeTrial extends CssLayout {
	
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
	private boolean competiOpen = false;
	private Button nextButton = null;

	public SpeedTimeTrial(Long categoria, String labelcategoria, Long competicion, 
			String label, RondaEnum ronda, SpeedCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.ronda = ronda;
		this.parent = parent;
		viewLogic = new SpeedCrudLogic(this, null);
		
		eliminatoria = viewLogic.existeKO(competicion, categoria);
		
		setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();
		
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
        
        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setMargin(true);
        barAndGridLayout.setSpacing(true);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");
        
        addComponent(barAndGridLayout);
        if(ronda==RondaEnum.PRIMERA){
	        addComponent(form1);
        }
        else if(ronda==RondaEnum.SEGUNDA){
	        addComponent(form2);
        }
	    
	    viewLogic.initGrid(this.competicion, this.categoria, this.ronda);
	    
	    GenericCrudLogic<CompeticionEntity> competiLogic = 
	    		new GenericCrudLogic<CompeticionEntity>(null, CompeticionEntity.class, "id");
	    CompeticionEntity competi = competiLogic.findRecord(""+competicion);
	    competiOpen = competi.getActive();
    	if(competi.getFechaInicio().after(new Date())){
    		this.showError("Esta Competición aun no puede comenzar.");
    		competiOpen = false;
    		nextButton.setEnabled(false);
    	}
    	//form1.setEnabled(eliminatoria==null && competiOpen);
    	//form2.setEnabled(eliminatoria==null && competiOpen);
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
		
		Button prevButton = new Button(/*Messages.get().getKey("prev")*/);
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
		
		nextButton = new Button(/*Messages.get().getKey("next")*/);
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
    				("Resultados Time-Trial "+competicionStr+" / "+categoriaStr).toUpperCase(),
    				"dorsal", "clasificacion", "nombre", "apellidos", "tiempoAjustado1", /*"valido1",*/ 
    				"tiempoAjustado2", /*"valido2",*/ "mejorTiempo");
    		if(file!=null){
    			FileResource resource = new FileResource(file);
    			Page.getCurrent().open(resource, "Export File", false);
    		    // Finally, removes the temporal file
    		    // file.delete();
    		}
        });
		
		Button exportButton = new Button();
		exportButton.setIcon(FontAwesome.DOWNLOAD);
		exportButton.addClickListener(e -> {
    		File file = Export2Xls.get().createXLS(
    				(List<SpeedTimeTrialEntity>)grid.getContainerDataSource().getItemIds(),
    				SpeedTimeTrialEntity.class,
    				("Export Time-Trial "+competicionStr+" / "+categoriaStr).toUpperCase(),
    				"dorsal", "orden1", "fullName");
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
        //topLayout.setMargin(true);
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
        else
        	topLayout.addComponent(exportButton);
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

    public void selectRow(SpeedTimeTrialEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public SpeedTimeTrialEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(SpeedTimeTrialEntity rec) {
    	if(ronda==RondaEnum.PRIMERA){
    		System.out.println("Entrando en editRecord1 "+rec);
        	if (rec != null) {
                form1.addStyleName("visible");
                //form.setEnabled(true);
            } else {
                form1.removeStyleName("visible");
                //form.setEnabled(false);
            }
    		form1.editRecord(rec, eliminatoria==null && competiOpen);
    	}
    	else if(ronda==RondaEnum.SEGUNDA){
    		System.out.println("Entrando en editRecord2 "+rec);
        	if (rec != null) {
                form2.addStyleName("visible");
                //form.setEnabled(true);
            } else {
                form2.removeStyleName("visible");
                //form.setEnabled(false);
            }
    		form2.editRecord(rec, eliminatoria==null && competiOpen);
    	}
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
        	{}//this.selectRow(records.get(0));
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
