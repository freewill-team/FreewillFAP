package freewill.nextgen.competicion.jam;

import java.util.List;
import java.util.Date;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
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
import freewill.nextgen.competicion.jam.JamShowForm;
import freewill.nextgen.data.JamShowEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.genericCrud.GenericCrudLogic;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class JamFinal extends VerticalLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("Jamshow");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<JamShowEntity> grid;
	private JamShowForm form;
	private JamCrudLogic viewLogic;
	private JamCrudView parent = null;
	private boolean competiOpen = false;
	private Button nextButton = null;
	
	public JamFinal(Long categoria, String labelcategoria, Long competicion, 
			String label, JamCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		
		viewLogic = new JamCrudLogic(this);
		
		grid = new GenericGrid<JamShowEntity>(JamShowEntity.class,
				"id", "dorsalDuo", "orden1", "nombreDuo",
        		"penalizaciones",
        		"tecnicaJuez1", "artisticaJuez1", "sincronizacionJuez1", "totalJuez1", "rankingJuez1", 
        		"tecnicaJuez2", "artisticaJuez2", "sincronizacionJuez2", "totalJuez2", "rankingJuez2", 
        		"tecnicaJuez3", "artisticaJuez3", "sincronizacionJuez3", "totalJuez3", "rankingJuez3");

		grid.setFrozenColumnCount(2);
		grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
            	viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
        
        form = new JamShowForm(viewLogic);
          
        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setSizeFull();
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.addComponent(grid);
        gridLayout.setExpandRatio(grid, 3);    
        gridLayout.addComponent(form);
	    gridLayout.setExpandRatio(form, 1);     
        
		HorizontalLayout topLayout = createTopBar();
	    //addComponent(new GenericHeader(VIEW_NAME, FontAwesome.FOLDER));
	    addComponent(topLayout);
	    addComponent(gridLayout);
	    setSizeFull();
	    setExpandRatio(gridLayout, 1);
	    setStyleName("crud-main-layout");
	    
	    viewLogic.initGrid(this.competicion, this.categoria);
	    
	    GenericCrudLogic<CompeticionEntity> competiLogic = 
	    		new GenericCrudLogic<CompeticionEntity>(null, CompeticionEntity.class, "id");
	    CompeticionEntity competi = competiLogic.findRecord(""+competicion);
	    competiOpen = competi.getActive();
	    if(competi.getFechaInicio().after(new Date())){
    		this.showError("Esta Competición aun no puede comenzar.");
    		competiOpen = false;
    		nextButton.setEnabled(false);
    	}
	    form.setEnabled(competiOpen);
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
		prevButton.setEnabled(true);
		
		nextButton = new Button(Messages.get().getKey("next"));
		nextButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		nextButton.setIcon(FontAwesome.ARROW_RIGHT);
		nextButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {	
            	// Next screen
             	parent.gotoResultados();
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
        
        Label competicionLabel = new Label(competicionStr + " / " + categoriaStr);
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

    public void selectRow(JamShowEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public JamShowEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(JamShowEntity rec) {
    	form.editRecord(rec);
    }

    public void showRecords(List<JamShowEntity> records) {
        grid.setRecords(records); 
        if(records!=null && records.size()>0){
    		grid. sort("orden1", SortDirection.DESCENDING);
    		this.selectRow(records.get(0));
        }
    }

    public void refreshRecord(JamShowEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(JamShowEntity rec) {
        // Not allowed here grid.remove(rec);
    }
    
}
