package freewill.nextgen.data;

import java.util.Collection;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 * A interface to define access to a customized form for editing a grid of record.
 * @param <T>
 */

public interface CrudViewInterface<T> extends View {

	public abstract String getName();
	public abstract void enter(ViewChangeEvent event);
	public abstract void showError(String msg);
	public abstract void showSaveNotification(String msg);
	public abstract void setNewRecordEnabled(boolean enabled);
	public abstract void clearSelection();
	public abstract void selectRow(T row);
	public abstract T getSelectedRow();
	public abstract void editRecord(T rec);
	public abstract void showRecords(Collection<T> records);
	public abstract void refreshRecord(T rec);
	public abstract void removeRecord(T rec);
	
}
