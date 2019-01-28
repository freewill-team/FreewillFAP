package freewill.nextgen.hmi.common;

import java.util.Collection;
import java.util.List;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Simple name editor Window.
 */
@SuppressWarnings("serial")
public class GenericEntityUpload<T> extends Window {

	private GenericGrid<T> grid;
    private Button save = null;
    List<T> list = null;
    Class<T> myentity = null;
    
    public void setOKAction(ClickListener action){
		save.addClickListener(action);
	}
    
    @SuppressWarnings("unchecked")
	public Collection<T> getSelectedRows(){
    	return (Collection<T>) grid.getSelectedRows();
    }

    public GenericEntityUpload(Class<T> entity, List<T> list, String idfield, String... fields) {
    	myentity = entity;
    	this.list = list;
        setCaption(Messages.get().getKey("uploadrecords"));
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(900.0f, Unit.PIXELS);

        addStyleName("edit-dashboard");

        setContent(buildContent(entity, idfield, fields));
    }

    private Component buildContent(Class<T> entity, String idfield, String... fields) {
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        
        grid = new GenericGrid<T>(entity, idfield, fields);
        grid.setRecords(list);
        /*for(T obj:list){
        	System.out.println("Rec="+obj.toString());
        }*/
        grid.setSelectionMode(SelectionMode.MULTI);
        result.addComponent(grid);
        result.addComponent(buildFooter());
        return result;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        
        Button cancel = new Button(Messages.get().getKey("cancel"));
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                close();
            }
        });
        cancel.setClickShortcut(KeyCode.ESCAPE, null);

        save = new Button(Messages.get().getKey("save"));
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                close();
            }
        });

        Label expander = new Label();
        
        footer.addComponents(expander, cancel, save);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        footer.setExpandRatio(expander, 1);
        return footer;
    }
    
}
