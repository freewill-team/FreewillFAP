package freewill.nextgen.podiumscircuito;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Responsive;

import freewill.nextgen.circuito.SelectCircuito;
import freewill.nextgen.categoria.SelectModalidad;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;

@SuppressWarnings("serial")
public class PodiumsCircuitoView extends Panel {

	private VerticalLayout content = null;
    private int modalidad = 0;
    private ModalidadEnum modalidadenum = null;
    private SelectCircuito selectcircuito = null;
    private VerticalLayout selectionarea = new VerticalLayout();
    private SelectModalidad selectmodalidad = null;
    private PodiumsCircuitoByCategoria selectcategoria = null;
    private AnonymousLoginPodiumCircuito parentPanel = null;
    
    public PodiumsCircuitoView() {
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
    	
    	selectcircuito = new SelectCircuito();
    	selectcircuito.addAction(
    		new ValueChangeListener() {
	            public void valueChange(ValueChangeEvent event) {
	            	selectionarea.removeAllComponents();
	            	selectmodalidad = createselectmodalidad();
	                selectionarea.addComponent(selectmodalidad);
	                if(parentPanel!=null){
	        			parentPanel.setCircuito(getCircuitoStr());
	        		}
	            }
    		});
    	
    	selectmodalidad = createselectmodalidad();
        
    	content.removeAllComponents();
    	content.addComponent(selectcircuito);
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
            	selectcategoria = new PodiumsCircuitoByCategoria(getCircuito(), modalidadenum);
            	selectionarea.addComponent(selectcategoria);
            	if(parentPanel!=null){
        			parentPanel.setCircuito(getCircuitoStr()+" / "+modalidadenum);
        		}
            }
        });
	}

	public Long getCircuito() {
		return selectcircuito.getValue();
	}
	
	public String getCircuitoStr() {
		return selectcircuito.getCaption();
	}

	public void setInfoPanel(AnonymousLoginPodiumCircuito parentPanel){
    	this.parentPanel = parentPanel;
    	if(parentPanel!=null){
			parentPanel.setCircuito(getCircuitoStr());
		}
    }
	
}
