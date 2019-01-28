package freewill.nextgen.file;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.apoi.Report;
import freewill.nextgen.apoi.ReportCoberturaXls;
import freewill.nextgen.apoi.ReportCostsXls;
import freewill.nextgen.apoi.ReportDeliverables;
import freewill.nextgen.apoi.ReportDesign;
import freewill.nextgen.apoi.ReportFuncional;
import freewill.nextgen.apoi.ReportFuncionalXls;
import freewill.nextgen.apoi.ReportTOCXls;
import freewill.nextgen.apoi.ReportTender;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.FileEntity;
import freewill.nextgen.data.ProductEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.ReportInfo;
import freewill.nextgen.data.ReportInfo.ReportTypeEnum;
import freewill.nextgen.hmi.common.GenericCombo;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * Simple name editor Window.
 */
@SuppressWarnings("serial")
public class NewFileWizard extends Window {

	private FileCrudLogic viewLogic = null;
	private Report rep = null; // output report file
	private TextField name;
	private OptionGroup options;
	private Label optionsLabel;
	private GenericCombo<ProjectEntity> project;
	private OptionGroup product;
	private ProgressBar PB;
	private Button save;
	private TabSheet tabs;
	private int selectedTab = 0;
	private CheckBox selectAll = null;

    public NewFileWizard(FileCrudLogic viewLogic) {
    	this.viewLogic = viewLogic;
        setCaption(Messages.get().getKey("newfilewizard"));
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(680.0f, Unit.PIXELS);
        this.setHeight(440.0f, Unit.PIXELS);
        addStyleName("edit-dashboard");
        setContent(buildContent());
    }

    private Component buildContent() {
    	
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        result.setHeight("100%");
        
        PB = new ProgressBar();
        PB.setWidth("100%");
        PB.setValue(0f);
        
        tabs = new TabSheet();
        tabs.setHeight("100%");
        
        tabs.addTab( buildTabProject(), Messages.get().getKey("selectproject") );
        tabs.addTab( buildTabReports(), Messages.get().getKey("selectrequiredreport") );
        tabs.addTab( buildTabProducts(), Messages.get().getKey("selectproduct") );
        
        tabs.addSelectedTabChangeListener(new SelectedTabChangeListener(){
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet tabsheet = event.getTabSheet();
				Component tab = (Layout) tabsheet.getSelectedTab();
				Tab seltab = tabsheet.getTab(tab);
				selectedTab = tabsheet.getTabPosition(seltab);
				System.out.println("Selected Tab = "+selectedTab);	
			}
        });
        
        result.addComponent(tabs);
        return result;
    }
    
    private Component buildTabProject() {
    	
    	name = new TextField(Messages.get().getKey("reporttitle"));
    	name.setWidth("80%");
    	
        project = new GenericCombo<ProjectEntity>(Messages.get().getKey("selectproject"), ProjectEntity.class);
        
        project.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if (project.getValue() != null) {
                	builtProductList((ProjectEntity)project.getValue());
                }
            }
        });
        
        Button next = new Button(Messages.get().getKey("next"));
    	next.setIcon(FontAwesome.ARROW_RIGHT);
    	next.addClickListener(nextListener);
    	
    	Button cancel = new Button(Messages.get().getKey("cancel"));
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                close();
            }
        });
        cancel.setClickShortcut(KeyCode.ESCAPE, null);
        
        HorizontalLayout tabbuttons = buildFooter(cancel, next);
        
        VerticalLayout expander = new VerticalLayout();
        
    	VerticalLayout tablayout = new VerticalLayout();
    	tablayout.setMargin(true);
    	tablayout.setSpacing(true);
    	tablayout.setHeight("100%");
    	tablayout.addComponent(name);
    	tablayout.addComponent(project);
    	tablayout.addComponent(expander);
    	//tablayout.addComponent(PB);
    	tablayout.addComponent(tabbuttons);
    	tablayout.setExpandRatio(expander, 1L);
    	tablayout.setComponentAlignment(tabbuttons, Alignment.MIDDLE_RIGHT);
    	
    	return tablayout;
    }
    
    private Component buildTabReports() {
    	
    	options = new OptionGroup(Messages.get().getKey("selectrequiredreport")+"...");
    	for(ReportTypeEnum type:ReportTypeEnum.values()){
    		options.addItem(type);
    	}
    	options.setValue(ReportTypeEnum.FUNCTIONAL);
        /*options.addItem("Functional Description");
        options.addItem("Functional Description (XLS)");
        options.addItem("Tender Proposal");
        options.addItem("Solution Design");
        options.addItem("Table Of Compliance"); 
        options.addItem("Extended Table Of Compliance");
        options.addItem("Cost Estimation"); 
        options.addItem("Coverage Report"); 
        options.addItem("Deliverables Report");
    	options.setValue("Functional Description");*/
    	options.addValueChangeListener(new ValueChangeListener(){
			@Override
			public void valueChange(ValueChangeEvent event) {
				//switch((String) options.getValue()){
				switch((ReportTypeEnum) options.getValue()){
		    		//case "Functional Description":
		    		case FUNCTIONAL:
		    			optionsLabel.setValue(Messages.get().getKey("functionaldescriptionfile"));
		    			break;
		    		//case "Functional Description (XLS)":
		    		case FUNCTIONALXLS:
		    			optionsLabel.setValue(Messages.get().getKey("functionaldescriptionxlsfile"));
		    			break;
		    		//case "Tender Proposal":
		    		case TENDER:
		    			optionsLabel.setValue(Messages.get().getKey("tenderproposalfile"));
		    			break;
		    		case RFI:
		    			optionsLabel.setValue(Messages.get().getKey("rfiresponsefile"));
		    			break;	
		    		//case "Solution Design":
		    		case DESIGN:
		    			optionsLabel.setValue(Messages.get().getKey("solutiondesignfile"));
		    			break;	
		    		//case "Table Of Compliance":
		    		case TOCXLS:
		    			optionsLabel.setValue(Messages.get().getKey("tableofcompliancefile"));
		    			break;
		    		//case "Extended Table Of Compliance":
		    		case TOCXLSEXT:
		    			optionsLabel.setValue(Messages.get().getKey("tableofcomplianceextendedfile"));
		    			break;
		    		//case "Cost Estimation":
		    		case COSTXLS:	
		    			optionsLabel.setValue(Messages.get().getKey("costestimationfile"));
		    			break;
		    		//case "Coverage Report":
		    		case COVERAGE:
		    			optionsLabel.setValue(Messages.get().getKey("coveragereportfile"));
		    			break;
		    		//case "Deliverables Report":
		    		case DELIVERY:
		    			optionsLabel.setValue(Messages.get().getKey("deliverablesreportfile"));
		    			break;
		    		//default:
		    		//	break;
		    	}	
			}
    	});
    	
    	optionsLabel = new Label(Messages.get().getKey("functionaldescriptionfile"));
    	optionsLabel.setStyleName("wrap");
    	optionsLabel.setWidth("375px");
    	
    	HorizontalLayout optionsLayout = new HorizontalLayout();
    	optionsLayout.setSpacing(true);
    	optionsLayout.setStyleName("login-information");
    	optionsLayout.addComponents(options, optionsLabel);
    	
    	Button next = new Button(Messages.get().getKey("next"));
    	next.setIcon(FontAwesome.ARROW_RIGHT);
    	next.addClickListener(nextListener);
    	
    	Button back = new Button(Messages.get().getKey("back"));
    	back.setIcon(FontAwesome.ARROW_LEFT);        
        back.addClickListener(backListener);
        
        HorizontalLayout tabbuttons = buildFooter(back, next);
        
        VerticalLayout expander = new VerticalLayout();
    	
    	VerticalLayout tablayout = new VerticalLayout();
    	tablayout.setMargin(true);
    	tablayout.setSpacing(true);
    	tablayout.setHeight("100%");
    	tablayout.addComponent(optionsLayout);
    	tablayout.addComponent(expander);
    	tablayout.addComponent(tabbuttons);
    	tablayout.setExpandRatio(expander, 1L);
    	tablayout.setComponentAlignment(tabbuttons, Alignment.MIDDLE_RIGHT);
    	
    	return tablayout;
    }
    
	private Component buildTabProducts() {
		
    	// Rellenar ComboBox Products a partir de Company y Project
		product = new OptionGroup(Messages.get().getKey("selectproduct"));
        product.setMultiSelect(true);
        builtProductList((ProjectEntity)project.getValue());
        
        selectAll = new CheckBox("Select/Unselect All");
    	selectAll.addValueChangeListener(new ValueChangeListener(){
			@Override
			public void valueChange(ValueChangeEvent event) {
				for(Object itemId:product.getItemIds()){
					if(selectAll.getValue())
						product.select(itemId);
					else
						product.unselect(itemId);
				}
			}
    	});
    	
    	Button back = new Button(Messages.get().getKey("back"));
    	back.setIcon(FontAwesome.ARROW_LEFT);
        back.addClickListener(backListener);
        
        save = new Button(Messages.get().getKey("save"));
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setDisableOnClick(true);
        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	if(saveAction()) close();
            }
        });
        save.setClickShortcut(KeyCode.ENTER, null);
        save.setEnabled(true);
         
        HorizontalLayout tabbuttons = buildFooter(back, save);
        
        VerticalLayout expander = new VerticalLayout();
        
    	VerticalLayout tablayout = new VerticalLayout();
    	tablayout.setMargin(true);
    	tablayout.setSpacing(true);
    	tablayout.setHeight("100%");
    	tablayout.addComponent(product);
    	tablayout.addComponent(selectAll);
    	tablayout.addComponent(expander);
    	tablayout.addComponent(tabbuttons);
    	tablayout.setExpandRatio(expander, 1L);
    	tablayout.setComponentAlignment(tabbuttons, Alignment.MIDDLE_RIGHT);
    	
    	return tablayout;
	}

	private void builtProductList(ProjectEntity project) {
		// Rellenar ComboBox Products a partir de Company y Project
		product.removeAllItems();
		try {
        	Collection<ProductEntity> products = BltClient.get().getEntities(ProductEntity.class,
					EntryPoint.get().getAccessControl().getTokenKey());
        	 for (ProductEntity s : products) {
             	if(s.getProject()==null || s.getProject()==0L || (long)s.getProject()==project.getID()){
     	        	product.addItem(s.getID());
     	        	product.setItemCaption(s.getID(), s.getName());
     	        	product.setValue(s.getID());
             	}
             }
		} catch (Exception e) {
			e.printStackTrace();
		}
        // All unselect
        if(selectAll!=null)
        	selectAll.setValue(false);
	}

	private HorizontalLayout buildFooter(Component... items) {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        
        Label expander = new Label("");

        footer.addComponent(expander);
        footer.addComponents(items);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        footer.setExpandRatio(expander, 1);
        
        return footer;
    }

	@SuppressWarnings("unchecked")
	private boolean saveAction(){
		// Generates a new Document
		PB.setValue(0f);
		
		if("".equals(name.getValue())){
    		viewLogic.showSaveNotification(Messages.get().getKey("filenamecannotbenull"));
    		save.setEnabled(true);
    		return false;
    	}
    	
    	if(project.getValue()==null){
    		viewLogic.showSaveNotification(Messages.get().getKey("projectcannotbenull"));
    		save.setEnabled(true);
    		return false;
    	}
    	
    	ReportInfo repinfo = new ReportInfo();
		repinfo.setName(name.getValue());
		repinfo.setType((ReportTypeEnum) options.getValue());
		ProjectEntity rec = (ProjectEntity) project.getValue();
		repinfo.setProject(rec.getID());
		repinfo.setProducts((Set<Long>) product.getValue());
		
		PB.setValue(1f);
		viewLogic.saveRecord(repinfo);
		
		save.setEnabled(true);
    	return true;
	}
	
	@SuppressWarnings("unused")
	@Deprecated
    private boolean saveActionDELETE(){
    	PB.setValue(0f);
    	
    	// Generates Document
    	if("".equals(name.getValue())){
    		viewLogic.showSaveNotification(Messages.get().getKey("filenamecannotbenull"));
    		save.setEnabled(true);
    		return false;
    	}
    	
    	String report = (String) options.getValue();
    	
    	Long prj = 0L;
    	if(project.getValue()!=null){
    		ProjectEntity rec = (ProjectEntity)project.getValue();
    		prj = rec.getID();
    	}
    	
    	@SuppressWarnings("unchecked")
		Set<Long> products = (Set<Long>) product.getValue();
    	
    	if(prj==null || prj==0){
    		viewLogic.showSaveNotification(Messages.get().getKey("projectcannotbenull"));
    		save.setEnabled(true);
    		return false;
    	}
    	
    	viewLogic.showSaveNotification(Messages.get().getKey("generatingreport")+" "+report);
    	switch( report ){
    		case "Functional Description":
    			rep = new ReportFuncional(name.getValue(), report, products, PB);
    			break;
    		case "Functional Description (XLS)":
    			rep = new ReportFuncionalXls(name.getValue(), report, products, PB);
    			break;
    		case "Tender Proposal":
    			rep = new ReportTender(name.getValue(), report, prj, products, PB);
    			break;
    		case "Solution Design":
    			rep = new ReportDesign(name.getValue(), report, prj, products, PB);
    			break;	
    		case "Table Of Compliance":
    			rep = new ReportTOCXls(name.getValue(), report, prj, PB, false);
    			break;
    		case "Extended Table Of Compliance":
    			rep = new ReportTOCXls(name.getValue(), report, prj, PB, true);
    			break;
    		case "Cost Estimation":
    			rep = new ReportCostsXls(name.getValue(), report, prj, PB);
    			break;
    		case "Coverage Report":
    			rep = new ReportCoberturaXls(name.getValue(), report, prj, products, PB);
    			break;
    		case "Deliverables Report":
    			rep = new ReportDeliverables(name.getValue(), report, prj, PB);
    			break;
    		default:
    			viewLogic.showSaveNotification(Messages.get().getKey("unknownreport")+" "+report);
    			break;
    	}
        // It Saves the generated file in FileEntity
    	FileEntity rec = new FileEntity();
        try {
            rec.setName(rep.getFile().getName());
            rec.setDescription(report);
            rec.setProject(prj);
            //Long company = EntryPoint.get().getAccessControl().getUserEntity().getCompany();
            //rec.setCompany(company);
            byte[] array = Files.readAllBytes(rep.getFile().toPath());
            rec.setImage(array);
            viewLogic.saveRecord(rec, false);
            rep.getFile().delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	save.setEnabled(true);
    	return true;
    }
    
    ClickListener nextListener = new ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
        	selectedTab++;
        	tabs.setSelectedTab(selectedTab);
        }
    };
    
    ClickListener backListener = new ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
        	selectedTab--;
        	tabs.setSelectedTab(selectedTab);
        }
    };
    
}
