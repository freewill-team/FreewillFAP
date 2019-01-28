package freewill.nextgen.data;

import java.io.Serializable;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the record editor form and the data source, including
 * fetching and saving records.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
public abstract interface CrudLogicInterface<T> extends Serializable {

    public abstract void init();
    public abstract void cancelRecord();
    public abstract void setFragmentParameter(String recId);
    public abstract void enter(String recId);
    public abstract T findRecord(Long recId);
    public abstract void saveRecord(T rec, boolean saveAndNext);
    public abstract void deleteRecord(T rec);
    public abstract void editRecord(T rec);
    public abstract void newRecord();
    public abstract void rowSelected(T rec);

}
