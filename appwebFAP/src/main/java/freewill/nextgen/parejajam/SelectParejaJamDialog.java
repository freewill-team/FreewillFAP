package freewill.nextgen.parejajam;

import java.util.Collection;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.ParejaJamEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Simple Student selector Window.
 */

@SuppressWarnings("serial")
public class SelectParejaJamDialog extends Window {
	private boolean isOK = false;
	private Button save = null;
	private ParejaJamEntity selected = null;
	private GenericGrid<ParejaJamEntity> grid = null;
	
	public ParejaJamEntity getSelected(){
		return selected;
	}
	
	public boolean isOK(){
		return isOK;
	}
	
	public void setOKAction(ClickListener action){
		save.addClickListener(action);
	}
	
    public SelectParejaJamDialog(Collection<ParejaJamEntity> students) {
    	setCaption("<b>Por favor, seleccione una pareja:</b>");
    	this.setCaptionAsHtml(true);
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(400.0f, Unit.PIXELS);

        addStyleName("edit-dashboard");

        setContent(buildContent(students));
    }

    private Component buildContent(Collection<ParejaJamEntity> students) {
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        
        grid = new GenericGrid<ParejaJamEntity>(ParejaJamEntity.class, 
        		"id", "nombre1", "apellidos1", "nombre2", "apellidos2", "clubStr");
        grid.setStyleName(ValoTheme.TABLE_SMALL);
        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                selected = grid.getSelectedRow();
                save.setEnabled((selected!=null));
            }
        });
        
        grid.setRecords(students);
        
        result.addComponent(buildFooter());
        result.addComponent(grid);
        result.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
        return result;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        
        TextField filter = new TextField();
        filter.setStyleName("filter-textfield");
        filter.setInputPrompt("Filter"); 
        filter.setImmediate(true);
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                grid.setFilter(event.getText());
            }
        });
        
        Button cancel = new Button(Messages.get().getKey("cancel"));
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	isOK = false;
                close();
            }
        });
        cancel.setClickShortcut(KeyCode.ESCAPE, null);

        save = new Button(Messages.get().getKey("OK"));
        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	isOK = true;
                close();
            }
        });
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER, null);
        save.setEnabled(false);
        
        Label expander = new Label("");

        footer.addComponents(filter, expander, cancel, save);
        //footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        //footer.setComponentAlignment(cancel, Alignment.MIDDLE_RIGHT);
        //footer.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
        footer.setExpandRatio(expander, 1L);
        
        return footer;
    }

}
