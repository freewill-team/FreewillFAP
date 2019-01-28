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
public abstract interface CrudLogicInterface extends Serializable {

    public abstract void init();
    public abstract void cancelRecord();
    public abstract void setFragmentParameter(String recId);
    public abstract void enter(String recId);
    public abstract void rowSelected(CompanyEntity rec);

    public abstract Object findRecord(Long company);
    public abstract void saveRecord(CompanyEntity rec);
    public abstract void deleteRecord(CompanyEntity rec);
    public abstract void newRecord();
    
}
