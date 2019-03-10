package freewill.nextgen.competicion.salto;

import java.util.Date;
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
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.SaltoEntity;
import freewill.nextgen.data.SaltoIntentoEntity.ResultEnum;
import freewill.nextgen.genericCrud.GenericCrudLogic;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class SaltoTrial extends CssLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("saltotrial");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<SaltoEntity> grid;
	private SaltoTrialForm form;
	private SaltoCrudLogic viewLogic;
	private int ronda = 0;
	private int alturaNextRonda = 0;
	private SaltoCrudView parent = null;
	private TextField newAltura;
	private boolean showOnly2Jumps = false;
	private boolean competiOpen = false;
	private Button nextButton = null;

	public SaltoTrial(Long categoria, String labelcategoria, Long competicion, 
			String label, int ronda, SaltoCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.ronda = ronda;
		this.parent = parent;
		viewLogic = new SaltoCrudLogic(this);
		
		alturaNextRonda = viewLogic.existenDatosRonda(competicion, categoria, ronda+1);
		
		setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();
		
		if(ronda==0)
			grid = new GenericGrid<SaltoEntity>(SaltoEntity.class,
	        	"id", "dorsal", "orden", "nombre", "apellidos");
		else
			grid = new GenericGrid<SaltoEntity>(SaltoEntity.class,
		        "id", "dorsal", "orden", "nombre", "apellidos", "altura", "salto1", "salto2", "salto3");
		
        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
            	if(ronda>0)
            		viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
        
        form = new SaltoTrialForm(viewLogic);
        
        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setMargin(true);
        barAndGridLayout.setSpacing(true);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");
        
        addComponent(barAndGridLayout);
        addComponent(form);
        
	    viewLogic.initGrid(this.competicion, this.categoria, ronda);
	    
	    GenericCrudLogic<CompeticionEntity> competiLogic = 
	    		new GenericCrudLogic<CompeticionEntity>(null, CompeticionEntity.class, "id");
	    CompeticionEntity competi = competiLogic.findRecord(""+competicion);
	    competiOpen = competi.getActive();
	    if(competi.getFechaInicio().after(new Date())){
    		this.showError("Esta Competición aun no puede comenzar.");
    		competiOpen = false;
    		nextButton.setEnabled(false);
    	}
	    //form.setEnabled(alturaNextRonda==0 && competiOpen);
	}
	
	public HorizontalLayout createTopBar() {
		
		newAltura = new TextField();
		newAltura.setValue(""+alturaNextRonda);
		if(alturaNextRonda!=0)
			newAltura.setEnabled(false);
		
		Button prevButton = new Button(/*Messages.get().getKey("prev")*/);
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Previous screen
            	if(ronda>0)
            		parent.gotoSaltoTrial(ronda-1);
            	else
            		parent.enter(null);
            }
        });
		//prevButton.setEnabled(ronda>0);
		
		nextButton = new Button(/*Messages.get().getKey("next")*/);
		nextButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		nextButton.setIcon(FontAwesome.ARROW_RIGHT);
		nextButton.addClickListener(new ClickListener() {
            @SuppressWarnings("unchecked")
			@Override
            public void buttonClick(ClickEvent event) {
                // Next screen
            	if(ronda>0 && allPatinadoresOut((List<SaltoEntity>)grid.getContainerDataSource().getItemIds()))
            		gotoActaFinal(ronda+1);
            	else if(ronda>0 && grid.getContainerDataSource().size()==1)
            		gotoActaFinal(ronda+1);
            	else if(alturaNextRonda==0){
	            	try{
	            		int alturaThisRonda = viewLogic.existenDatosRonda(competicion, categoria, ronda);
	            		int newaltura = Integer.parseInt(newAltura.getValue());
	            		if(newaltura<=alturaThisRonda || newaltura==0){
	            			showError("La nueva Altura no es válida!");
	            			return;
	            		}
	            		ConfirmDialog cd = new ConfirmDialog(
            					"Tras esta acción ya no podrá modificar datos del Salto Actual.\n" +
            					"¿ Desea continuar ?");
                    	cd.setOKAction(new ClickListener() {
                            @Override
                            public void buttonClick(final ClickEvent event) {
                    			cd.close();
                    			viewLogic.createNewAltura(
                    					competicion, categoria, ronda+1, newaltura);
                    			parent.gotoSaltoTrial(ronda+1);
                            }
                        });
                    	getUI().addWindow(cd);
	            	}
	            	catch(Exception e){
	            		showError(e.getMessage());
	            	}
	            }
            	else
            		parent.gotoSaltoTrial(ronda+1);
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
        
        Label competicionLabel = new Label(competicionStr+" / "+categoriaStr);
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
        topLayout.addComponent(newAltura);
        topLayout.addComponent(prevButton);
        topLayout.addComponent(nextButton);
        topLayout.setComponentAlignment(competicionLabel, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(competicionLabel, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }
	
	protected void gotoActaFinal(int i) {
		// Next screen
    	ConfirmDialog cd = new ConfirmDialog(
    			"Esta acción publicará los resultados en la web pública.\n" +
    			"¿ Desea continuar ?");
        cd.setOKAction(new ClickListener() {
        	@Override
            public void buttonClick(final ClickEvent event) {
            	cd.close();
            	parent.gotoActaFinal(i);
        	}
        });
        getUI().addWindow(cd);
	}

	private boolean allPatinadoresOut(List<SaltoEntity> recs) {
		for(SaltoEntity rec:recs){
			if( rec.getSalto1()==ResultEnum.OK || rec.getSalto1()==ResultEnum.PASA ||
				rec.getSalto2()==ResultEnum.OK || rec.getSalto2()==ResultEnum.PASA ||
				rec.getSalto3()==ResultEnum.OK || rec.getSalto3()==ResultEnum.PASA)
				return false;
		}
		return true;
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

    public void selectRow(SaltoEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public SaltoEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(SaltoEntity rec) {
    	System.out.println("Entrando en editRecord "+rec);
    	if (rec != null) {
            form.addStyleName("visible");
            //form.setEnabled(true);
        } else {
            form.removeStyleName("visible");
            //form.setEnabled(false);
        }
    	form.editRecord(rec, showOnly2Jumps, alturaNextRonda==0 && rec!=null && competiOpen);
    }

    public void showRecords(List<SaltoEntity> records) {
        grid.setRecords(records);
        if(records!=null && records.size()>0){
    		showOnly2Jumps = (records.size()>3);
    		Column col = grid.getColumn("salto3");
    		if(col!=null)
    			col.setHidden(showOnly2Jumps);
    		//this.selectRow(records.get(0));
        }
        else{
        	showError("No existen inscripciones para esta prueba!");
        	this.setEnabled(false);
        }
    }

    public void refreshRecord(SaltoEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(SaltoEntity rec) {
        // Not allowed here grid.remove(rec);
    }
    
}
