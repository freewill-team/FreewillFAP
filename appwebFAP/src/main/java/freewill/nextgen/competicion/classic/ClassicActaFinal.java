package freewill.nextgen.competicion.classic;

import java.io.File;
import java.util.List;

import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
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

import freewill.nextgen.data.ClassicShowEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class ClassicActaFinal extends VerticalLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("classicacta");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<ClassicShowEntity> grid;
	private ClassicCrudLogic viewLogic;
	private ClassicCrudView parent = null;

	public ClassicActaFinal(Long categoria, String labelcategoria, Long competicion, 
			String label, ClassicCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		
		viewLogic = new ClassicCrudLogic();
		
		grid = new GenericGrid<ClassicShowEntity>(ClassicShowEntity.class,
		       "id", "dorsal", "clasificacionFinal", "nombre", "apellidos",
		       "sumaPV", "PVLocales", "totalTecnica", "PVTotal", "puntuacionTotal"
		       //"rankingJuez1","rankingJuez2", "rankingJuez3", "orden1"
		       );
        
        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setSizeFull();
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.addComponent(grid);
        	
		HorizontalLayout topLayout = createTopBar();
	    //addComponent(new GenericHeader(VIEW_NAME, FontAwesome.FOLDER));
	    addComponent(topLayout);
	    addComponent(gridLayout);
	    setSizeFull();
	    setExpandRatio(gridLayout, 1);
	    setStyleName("crud-main-layout");
	    showRecords(viewLogic.initGridResults(this.competicion, this.categoria));  	
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public HorizontalLayout createTopBar() {
		
		Button prevButton = new Button(Messages.get().getKey("prev"));
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Previous screen
            	parent.gotoClassicFinal();
            }
        });
		
		Button printButton = new Button(Messages.get().getKey("acta"));
		//printButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		printButton.setIcon(FontAwesome.DOWNLOAD);
		printButton.addClickListener(e -> {
    		File file = Export2Xls.get().createXLS(
    				(List<ClassicShowEntity>)grid.getContainerDataSource().getItemIds(),
    				ClassicShowEntity.class,
    				("Resultados "+competicionStr+" / "+categoriaStr).toUpperCase(),
    				"clasificacionFinal", "dorsal", "nombre", "apellidos", 
    			    "sumaPV", "PVLocales", "totalTecnica", "PVTotal", "puntuacionTotal");
    		if(file!=null){
    			FileResource resource = new FileResource(file);
    			Page.getCurrent().open(resource, "Export File", false);
    		    // Finally, removes the temporal file
    		    // file.delete();
    		}
        });
        
        Label competicionLabel = new Label(competicionStr+" / "+categoriaStr);
        competicionLabel.setStyleName(ValoTheme.LABEL_LARGE);
        competicionLabel.addStyleName(ValoTheme.LABEL_COLORED);
        competicionLabel.addStyleName(ValoTheme.LABEL_BOLD);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setSpacing(true);
        topLayout.setMargin(true);
        topLayout.setWidth("100%");
        topLayout.addComponent(competicionLabel);
        topLayout.addComponent(prevButton);
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

    public void showRecords(List<ClassicShowEntity> records) {
        grid.setRecords(records);
    }
    
}
