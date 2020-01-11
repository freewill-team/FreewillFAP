package freewill.nextgen.informes;

import java.io.File;
import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.circuito.SelectCircuito;
import freewill.nextgen.competicion.SelectCompeticion;
import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.data.RankingEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class InformesView extends Panel implements View {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("informes");
    private InformesLogic viewLogic = new InformesLogic(this);
    private VerticalLayout content = null;
    
    public InformesView() {
    	this.setStyleName("crud-view");
    	this.addStyleName(ValoTheme.PANEL_BORDERLESS);
    	setSizeFull();
        
    	content = new VerticalLayout();
    	content.setSizeFull();
    	content.addStyleName("dashboard-view");
    	content.setSpacing(true);
    	content.setMargin(true);
    	this.setContent(content);
    	Responsive.makeResponsive(content);
    	
    	createContent();
    }
    
    private void createContent() {
    	VerticalLayout selectionarea = new VerticalLayout();
        selectionarea.setMargin(false);
        selectionarea.setSpacing(true);
        content.addComponent(createTopBar());
    	content.addComponent(selectionarea);
        content.setExpandRatio(selectionarea, 1);
    	
        selectionarea.addComponent(createReport1());
        selectionarea.addComponent(createReport3());
        selectionarea.addComponent(createReport2());
    }

	@SuppressWarnings("deprecation")
	private VerticalLayout createReport1() {
		
		Label title = new Label("Actas de Competición (XLSX)");
	    title.setStyleName(ValoTheme.LABEL_LARGE);
	    title.addStyleName(ValoTheme.LABEL_COLORED);
	    
	    SelectCompeticion selectcompeticion = new SelectCompeticion();
		
		Button printButton = new Button(Messages.get().getKey("generar"));
		//printButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		printButton.setIcon(FontAwesome.DOWNLOAD);
		printButton.addClickListener(e -> {
			List<ParticipanteEntity>[] data = 
					viewLogic.getParticipantes(selectcompeticion.getValue());
			
	    	File file = Export2XlsMultiple.get().createXLS(
	    			selectcompeticion.getCaption(),
	    			data, ParticipanteEntity.class,
	    			viewLogic.getCategoriasStr(),
	    			"dorsal", "clasificacion", "nombre", "apellidos", 
					"clubStr", "puntuacion", "mejorMarca");
	    	if(file!=null){
	    		FileResource resource = new FileResource(file);
	    		Page.getCurrent().open(resource, "Export File", false);
	    		// Finally, removes the temporal file
	    		// file.delete();
	    	}
	    	else
	    		showError("Error durante la generación del informe.");
	    });
		//printButton.setEnabled(false);
		
		HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.setWidth("100%");
        hlayout.setMargin(false);
        hlayout.setSpacing(true);
        hlayout.addComponents(new Label(), title, printButton);
        hlayout.setExpandRatio(title, 1);
		
		VerticalLayout layout = new VerticalLayout();
		layout.setStyleName(ValoTheme.LAYOUT_CARD);
		layout.setSpacing(false);
	    layout.setMargin(false);
	    layout.setWidth("100%");
	    layout.addComponent(hlayout);
	    layout.addComponent(selectcompeticion);
		return layout;
	}

	@SuppressWarnings("deprecation")
	private VerticalLayout createReport2() {
		
		Label title = new Label("Informe de Gestión (DOCX)");
	    title.setStyleName(ValoTheme.LABEL_LARGE);
	    title.addStyleName(ValoTheme.LABEL_COLORED);
	    
	    SelectCircuito selectcircuito = new SelectCircuito();
		
		Button printButton = new Button(Messages.get().getKey("generar"));
		//printButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		printButton.setIcon(FontAwesome.DOWNLOAD);
		printButton.addClickListener(e -> {
			
			ReportGestion report = new ReportGestion(
					selectcircuito.getValue(),
	    			selectcircuito.getCaption(),
	    			viewLogic);
			
	    	File file = report.getFile();
	    	if(report.isSuccess() && file!=null){
	    		FileResource resource = new FileResource(file);
	    		Page.getCurrent().open(resource, "Report File", false);
	    		// Finally, removes the temporal file
	    		// file.delete();
	    	}
	    	else
	    		showError("Error durante la generación del informe.");
	    });
		//printButton.setEnabled(false);
		
		HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.setWidth("100%");
        hlayout.setMargin(false);
        hlayout.setSpacing(true);
        hlayout.addComponents(new Label(), title, printButton);
        hlayout.setExpandRatio(title, 1);
		
		VerticalLayout layout = new VerticalLayout();
		layout.setStyleName(ValoTheme.LAYOUT_CARD);
		layout.setSpacing(false);
	    layout.setMargin(false);
	    layout.setWidth("100%");
	    layout.addComponent(hlayout);
	    layout.addComponent(selectcircuito);
		return layout;
	}
	
	@SuppressWarnings("deprecation")
	private VerticalLayout createReport3() {
		
		Label title = new Label("Actas del Circuito (XLSX)");
	    title.setStyleName(ValoTheme.LABEL_LARGE);
	    title.addStyleName(ValoTheme.LABEL_COLORED);
	    
	    SelectCircuito selectcircuito = new SelectCircuito();
		
		Button printButton = new Button(Messages.get().getKey("generar"));
		//printButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		printButton.setIcon(FontAwesome.DOWNLOAD);
		printButton.addClickListener(e -> {
			List<RankingEntity>[] data = 
					viewLogic.getRankingCircuito(selectcircuito.getValue());
			
	    	File file = Export2XlsMultiple.get().createXLS(
	    			selectcircuito.getCaption(),
	    			data, RankingEntity.class,
	    			viewLogic.getCategoriasStr(),
	    			"orden", "nombre", "apellidos", 
	    			"clubStr", "puntuacion", 
					"puntos1", "puntos2", "puntos3", "puntos4");
	    	if(file!=null){
	    		FileResource resource = new FileResource(file);
	    		Page.getCurrent().open(resource, "Export File", false);
	    		// Finally, removes the temporal file
	    		// file.delete();
	    	}
	    	else
	    		showError("Error durante la generación del informe.");
	    });
		//printButton.setEnabled(false);
		
		HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.setWidth("100%");
        hlayout.setMargin(false);
        hlayout.setSpacing(true);
        hlayout.addComponents(new Label(), title, printButton);
        hlayout.setExpandRatio(title, 1);
		
		VerticalLayout layout = new VerticalLayout();
		layout.setStyleName(ValoTheme.LAYOUT_CARD);
		layout.setSpacing(false);
	    layout.setMargin(false);
	    layout.setWidth("100%");
	    layout.addComponent(hlayout);
	    layout.addComponent(selectcircuito);
		return layout;
	}
	
	public HorizontalLayout createTopBar() {
        
        Label title = new Label(this.VIEW_NAME+"");
        title.setStyleName(ValoTheme.LABEL_HUGE);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        topLayout.setSpacing(true);
        topLayout.setMargin(false);
        topLayout.setWidth("100%");
        topLayout.addComponent(title);
        topLayout.setExpandRatio(title, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	viewLogic.enter(event.getParameters());
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }
	
}
