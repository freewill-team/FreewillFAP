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
import freewill.nextgen.competicion.classic.ClassicShowForm;
import freewill.nextgen.data.ClassicShowEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class ClassicFinal extends VerticalLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("classicshow");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<ClassicShowEntity> grid;
	private ClassicShowForm form;
	private ClassicCrudLogic viewLogic;
	private ClassicCrudView parent = null;
	
	public ClassicFinal(Long categoria, String labelcategoria, Long competicion, 
			String label, ClassicCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		
		viewLogic = new ClassicCrudLogic(this);
		
		grid = new GenericGrid<ClassicShowEntity>(ClassicShowEntity.class,
        		"id", "orden1", "dorsal", "nombre", "apellidos",
        		"penalizaciones",
        		"tecnicaJuez1", "artisticaJuez1",
        		"tecnicaJuez2", "artisticaJuez2",
        		"tecnicaJuez3", "artisticaJuez3");
		
		grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
            	viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
        
        form = new ClassicShowForm(viewLogic);
       
        
        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setWidth("100%");
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.addComponent(grid);
        gridLayout.setExpandRatio(grid, 2);
        
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
	}
	
	public HorizontalLayout createTopBar() {
		/*
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
		*/
		
		Button prevButton = new Button(Messages.get().getKey("prev"));
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	// Previous screen
            	parent.gotoTrial();
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
            	parent.gotoActaFinal();
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

    public void selectRow(ClassicShowEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public ClassicShowEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(ClassicShowEntity rec) {
    	form.editRecord(rec);
    }

    public void showRecords(List<ClassicShowEntity> records) {
        grid.setRecords(records); 
        if(records!=null && records.size()>0)
    		this.selectRow(records.get(0));
    }

    public void refreshRecord(ClassicShowEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(ClassicShowEntity rec) {
        // Not allowed here grid.remove(rec);
    }
    
}
