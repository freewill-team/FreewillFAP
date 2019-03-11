package freewill.nextgen.resultados;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.data.RankingEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.GenericHeader;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.ranking.RankingForm;

@SuppressWarnings("serial")
public class ResultadosActaFinal extends CssLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("resultados");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<ParticipanteEntity> grid;
	private ResultadosCrudLogic viewLogic;
	private ResultadosCrudView parent = null;
	private ParticipanteForm form = null;
	private CategoriaEntity category = null;
	private Label competicionLabel = null;
	
	public void setLabelVisible(boolean visible){
		competicionLabel.setVisible(visible);
	}

	public ResultadosActaFinal(Long categoria, String labelcategoria, 
			Long competicion, String labelcompeticion, ResultadosCrudView parent){
		this.competicion = competicion;
		this.competicionStr = labelcompeticion;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		setSizeFull();
        addStyleName("crud-view");
        viewLogic = new ResultadosCrudLogic(this);
        
        HorizontalLayout topLayout = createTopBar();
		
		category = viewLogic.findCategoria(""+categoria);
		
		switch(category.getModalidad()){
		case SLIDE:
		case BATTLE:	
		case CLASSIC:
			grid = new GenericGrid<ParticipanteEntity>(ParticipanteEntity.class,
					"id", "dorsal", "clasificacion", "nombre", "apellidos", 
					"clubStr", "puntuacion");
			break;
		case JUMP:
		case SPEED:
			grid = new GenericGrid<ParticipanteEntity>(ParticipanteEntity.class,
					"id", "dorsal", "clasificacion", "nombre", "apellidos", 
					"clubStr", "puntuacion", "mejorMarca");
			break;
		case JAM:
			grid = new GenericGrid<ParticipanteEntity>(ParticipanteEntity.class,
					"id", "dorsal", "clasificacion", "nombre", "apellidos", 
					"dorsalPareja", "nombrePareja", "apellidosPareja", "clubStr");
			break;
		}
        
		grid.addSelectionListener(new SelectionListener() {
	        @Override
	        public void select(SelectionEvent event) {
	            viewLogic.rowSelected(grid.getSelectedRow());
	        }
	    });
		
		form = new ParticipanteForm(viewLogic);
		
		VerticalLayout barAndGridLayout = new VerticalLayout();
		//addComponent(new GenericHeader(competicionStr+" / "+categoriaStr, FontAwesome.TROPHY));
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setMargin(true);
        barAndGridLayout.setSpacing(true);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");
	    
	    addComponent(barAndGridLayout);
	    addComponent(form);
	    
	    viewLogic.initGrid(this.competicion, this.categoria);
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public HorizontalLayout createTopBar() {
		
		CompeticionEntity competi = viewLogic.findCompeticion(""+competicion);
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fecha = format.format(competi.getFechaInicio());
		
		competicionLabel = new Label(competicionStr+" / "+categoriaStr+ " "+ fecha, 
				ContentMode.HTML);
        competicionLabel.setStyleName(ValoTheme.LABEL_LARGE);
        competicionLabel.addStyleName(ValoTheme.LABEL_COLORED);
        competicionLabel.addStyleName(ValoTheme.LABEL_BOLD);
		
		Button prevButton = new Button(/*Messages.get().getKey("prev")*/);
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Previous screen
            	parent.enter(null);
            }
        });
		
		Button printButton = new Button(Messages.get().getKey("acta"));
		//printButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		printButton.setIcon(FontAwesome.DOWNLOAD);
		printButton.addClickListener(e -> {
    		File file = null;
    		switch(category.getModalidad()){
    		case SLIDE:
    		case BATTLE:	
    		case CLASSIC:
    			file = Export2Xls.get().createXLS(
        				(List<ParticipanteEntity>)grid.getContainerDataSource().getItemIds(),
        				ParticipanteEntity.class,
        				(VIEW_NAME+" " + competicionLabel.getValue()).toUpperCase(),
    					"dorsal", "clasificacion", "nombre", "apellidos", "clubStr", "puntuacion");
    			break;
    		case JUMP:
    		case SPEED:
    			file = Export2Xls.get().createXLS(
        				(List<ParticipanteEntity>)grid.getContainerDataSource().getItemIds(),
        				ParticipanteEntity.class,
        				(VIEW_NAME+" " + competicionLabel.getValue()).toUpperCase(),
    					"dorsal", "clasificacion", "nombre", "apellidos", "clubStr", "puntuacion", "mejorMarca");
    			break;
    		case JAM:
    			file = Export2Xls.get().createXLS(
        				(List<ParticipanteEntity>)grid.getContainerDataSource().getItemIds(),
        				ParticipanteEntity.class,
        				(VIEW_NAME+" " + competicionLabel.getValue()).toUpperCase(),
    					"dorsal", "clasificacion", "nombre", "apellidos", 
    					"dorsalPareja", "nombrePareja", "apellidosPareja", "clubStr");
    			break;
    		}
    		if(file!=null){
    			FileResource resource = new FileResource(file);
    			Page.getCurrent().open(resource, "Export File", false);
    		    // Finally, removes the temporal file
    		    // file.delete();
    		}
        });
		
		Button newRecord = new Button(Messages.get().getKey("new"));
        newRecord.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newRecord.setIcon(FontAwesome.PLUS_CIRCLE);
        newRecord.addClickListener(new ClickListener() {
			@Override
            public void buttonClick(ClickEvent event) {
            	try{
            		ParticipanteEntity rec = new ParticipanteEntity();
            		rec.setCategoria(categoria);
    	        	rec.setCompeticion(competicion);
    	        	editRecord(rec);
            	}
            	catch(Exception e){
            		showError(e.getMessage());
            	};
            }
        });
		
		Label expander = new Label("");
        
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setSpacing(true);
        //topLayout.setMargin(true);
        topLayout.setWidth("100%");
        topLayout.addComponent(competicionLabel);
        topLayout.addComponent(expander);
        topLayout.addComponent(prevButton);
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER))
        	topLayout.addComponent(printButton);
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN))
        	topLayout.addComponent(newRecord);
        topLayout.setComponentAlignment(competicionLabel, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(competicionLabel, 1);
        topLayout.setComponentAlignment(expander, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(expander, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }
	
	public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

    public void showRecords(List<ParticipanteEntity> records) {
        grid.setRecords(records);
    }
    
    public void clearSelection() {
    	try{
    		grid.getSelectionModel().reset();
    	}
    	catch(Exception e){
    		System.out.println("clearSelection: "+e.getMessage());
    	}
    }

    public void selectRow(ParticipanteEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public ParticipanteEntity getSelectedRow() {
        return grid.getSelectedRow();
    }
    
    public void editRecord(ParticipanteEntity rec) {    
        if (rec != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
        } else {
            form.removeStyleName("visible");
            form.setEnabled(false);
        }
        form.editRecord(rec);
    }

    public void showRecords(Collection<ParticipanteEntity> records) {
        grid.setRecords(records);
    }

    public void refreshRecord(ParticipanteEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(ParticipanteEntity rec) {
        grid.remove(rec);
    }
    
}
