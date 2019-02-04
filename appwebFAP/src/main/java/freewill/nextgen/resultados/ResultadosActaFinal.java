package freewill.nextgen.resultados;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.GenericHeader;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class ResultadosActaFinal extends VerticalLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("resultadosacta");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<ParticipanteEntity> grid;
	private ResultadosCrudLogic viewLogic;
	private ResultadosCrudView parent = null;
	private CategoriaEntity category = null;

	public ResultadosActaFinal(Long categoria, String labelcategoria, 
			Long competicion, String labelcompeticion, ResultadosCrudView parent){
		this.competicion = competicion;
		this.competicionStr = labelcompeticion;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		
		viewLogic = new ResultadosCrudLogic(this);
		
		category = viewLogic.findRecord(""+categoria);
		
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
        
        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setSizeFull();
        gridLayout.setMargin(true);
        gridLayout.setSpacing(false); // true
        gridLayout.addComponent(grid);
        
		HorizontalLayout topLayout = createTopBar();
	    addComponent(new GenericHeader(competicionStr+" / "+categoriaStr, FontAwesome.TROPHY));
	    addComponent(topLayout);
	    addComponent(gridLayout);
	    setSizeFull();
	    setMargin(false);
        setSpacing(false);
	    setExpandRatio(gridLayout, 1);
	    setStyleName("crud-main-layout");
	    
	    viewLogic.initGrid(this.competicion, this.categoria);
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public HorizontalLayout createTopBar() {
		
		CompeticionEntity competi = viewLogic.findCompeticion(""+competicion);
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fecha = " "+format.format(competi.getFechaInicio());
		
		Label competicionLabel = new Label("<b>"+competicionStr+" / "+categoriaStr
				+ fecha + "</b>", ContentMode.HTML);
        competicionLabel.setStyleName(ValoTheme.LABEL_LARGE);
        competicionLabel.addStyleName(ValoTheme.LABEL_COLORED);
        competicionLabel.addStyleName(ValoTheme.LABEL_BOLD);
		
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
        				("Resultados " + competicionLabel.getValue()).toUpperCase(),
    					"id", "dorsal", "clasificacion", "nombre", "apellidos", "clubStr", "puntuacion");
    			break;
    		case JUMP:
    		case SPEED:
    			file = Export2Xls.get().createXLS(
        				(List<ParticipanteEntity>)grid.getContainerDataSource().getItemIds(),
        				ParticipanteEntity.class,
        				("Resultados " + competicionLabel.getValue()).toUpperCase(),
    					"id", "dorsal", "clasificacion", "nombre", "apellidos", "clubStr", "puntuacion", "mejorMarca");
    			break;
    		case JAM:
    			file = Export2Xls.get().createXLS(
        				(List<ParticipanteEntity>)grid.getContainerDataSource().getItemIds(),
        				ParticipanteEntity.class,
        				("Resultados " + competicionLabel.getValue()).toUpperCase(),
    					"id", "dorsal", "clasificacion", "nombre", "apellidos", 
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
		
		Label expander = new Label("");
        
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setSpacing(true);
        //topLayout.setMargin(false); // true
        topLayout.setWidth("100%");
        //topLayout.addComponent(competicionLabel);
        topLayout.addComponent(expander);
        topLayout.addComponent(prevButton);
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER))
        	topLayout.addComponent(printButton);
        //topLayout.setComponentAlignment(competicionLabel, Alignment.MIDDLE_LEFT);
        //topLayout.setExpandRatio(competicionLabel, 1);
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
    
}
