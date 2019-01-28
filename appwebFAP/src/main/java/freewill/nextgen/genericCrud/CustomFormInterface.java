package freewill.nextgen.genericCrud;

import com.vaadin.ui.Component;

/**
 * A interface to define access to a customized form for editing a single record.
 */

public interface CustomFormInterface<T> {

	public void editRecord(T rec);
	public void setLogic(GenericCrudLogic<T> logic);
	public Component getComponent();
	
}
