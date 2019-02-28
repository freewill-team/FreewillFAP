package freewill.nextgen.podiums;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Responsive;

import freewill.nextgen.competicion.SelectCompeticionCombo;
import freewill.nextgen.categoria.SelectModalidad;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;

@SuppressWarnings("serial")
public class PodiumsView extends Panel {

	private VerticalLayout content = null;
    private int modalidad = 0;
    private ModalidadEnum modalidadenum = null;
    private SelectCompeticionCombo selectcompeticion = null;
    private VerticalLayout selectionarea = new VerticalLayout();
    private SelectModalidad selectmodalidad = null;
    private PodiumsByCategoria selectcategoria = null;
    private AnonymousLoginPodium parentPanel = null;
    
    public PodiumsView() {
    	this.addStyleName(ValoTheme.PANEL_BORDERLESS);
    	setSizeFull();
    	
    	content = new VerticalLayout();
    	content.setSizeFull();
    	content.addStyleName("dashboard-view");
    	content.setSpacing(false);
    	content.setMargin(false);
    	this.setContent(content);
    	Responsive.makeResponsive(content);
    	
    	createContent();
    }

    private void createContent() {
    	
    	selectcompeticion = new SelectCompeticionCombo();
    	selectcompeticion.addAction(
    		new ValueChangeListener() {
	            public void valueChange(ValueChangeEvent event) {
	            	selectionarea.removeAllComponents();
	            	selectmodalidad = createselectmodalidad();
	                selectionarea.addComponent(selectmodalidad);
	                if(parentPanel!=null){
	        			parentPanel.setCompeticion(getCompeticionStr());
	        		}
	            }
    		});
    	
    	selectmodalidad = createselectmodalidad();
        
    	content.removeAllComponents();
    	content.addComponent(selectcompeticion);
    	content.addComponent(selectionarea);
        selectionarea.removeAllComponents();
        selectionarea.setMargin(false);
        selectionarea.setSpacing(false);
        selectionarea.addComponent(selectmodalidad);
        content.setExpandRatio(selectionarea, 1);
    }
	
	private SelectModalidad createselectmodalidad(){
		return new SelectModalidad(
        		new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	//modalidadStr = event.getButton().getDescription();
            	modalidad = Integer.parseInt(event.getButton().getId());
            	modalidadenum = ModalidadEnum.values()[modalidad];
            	if(selectcategoria!=null)
            		selectionarea.removeComponent(selectcategoria);
            	selectcategoria = new PodiumsByCategoria(getCompeticion(), modalidadenum);
            	selectionarea.addComponent(selectcategoria);
            	if(parentPanel!=null){
        			parentPanel.setCompeticion(getCompeticionStr()+" / "+modalidadenum);
        		}
            }
        });
	}

	public Long getCompeticion() {
		return selectcompeticion.getValue();
	}
	
	public String getCompeticionStr() {
		return selectcompeticion.getCaption();
	}

	public void setInfoPanel(AnonymousLoginPodium parentPanel){
    	this.parentPanel = parentPanel;
    	if(parentPanel!=null){
			parentPanel.setCompeticion(getCompeticionStr());
		}
    }
	
}
