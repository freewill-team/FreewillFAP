package freewill.nextgen.appwebFAP;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

import freewill.nextgen.categoria.CategoriaForm;
import freewill.nextgen.circuito.CircuitoForm;
import freewill.nextgen.club.ClubForm;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.company.CompanyAloneView;
import freewill.nextgen.company.CompanyCrudView;
import freewill.nextgen.competicion.CompeticionForm;
import freewill.nextgen.competicion.battle.BattleCrudView;
import freewill.nextgen.competicion.classic.ClassicCrudView;
import freewill.nextgen.competicion.derrapes.DerrapesCrudView;
import freewill.nextgen.competicion.jam.JamCrudView;
import freewill.nextgen.competicion.salto.SaltoCrudView;
import freewill.nextgen.competicion.speed.SpeedCrudView;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CircuitoEntity;
import freewill.nextgen.data.ClubEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ConfigEntity;
import freewill.nextgen.data.ParejaJamEntity;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.data.PuntuacionesEntity;
import freewill.nextgen.dorsal.DorsalCrudView;
import freewill.nextgen.genericCrud.GenericCrudView;
import freewill.nextgen.gestioncategorias.GestionCrudView;
import freewill.nextgen.hmi.common.AboutView;
import freewill.nextgen.hmi.common.Menu;
import freewill.nextgen.hmi.common.NoPermissionView;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.informes.InformesView;
import freewill.nextgen.mejoresmarcas.MarcasCrudView;
import freewill.nextgen.palmares.PalmaresCrudView;
import freewill.nextgen.parejajam.ParejaJamForm;
import freewill.nextgen.patinador.PatinadorForm;
import freewill.nextgen.preinscripcion.PreinscripcionCrudView;
import freewill.nextgen.preinscripcion.PreinscripcionCrudView.InscripcionEnum;
import freewill.nextgen.ranking.RankingCrudView;
import freewill.nextgen.rankingabsoluto.RankingAbsCrudView;
import freewill.nextgen.resultados.ResultadosCrudView;
//import freewill.nextgen.mail.MailServerCrudView;
//import freewill.nextgen.support.SupportCrudView;
import freewill.nextgen.user.UserAloneView;
import freewill.nextgen.user.UserCrudView;

@SuppressWarnings("serial")
public class MainScreen extends HorizontalLayout {
	
	private Menu menu = null;
	
	public MainScreen(EntryPoint ui){
		this.setStyleName("main-screen");
		
		// viewContainer is where all different forms/windows will be displayed
	    CssLayout viewContainer = new CssLayout();
	    viewContainer.addStyleName("valo-content");
	    viewContainer.setSizeFull();
	    
        // navigator will allow to change the current form/window
        final Navigator navigator = new Navigator(ui, viewContainer);
        //navigator.setErrorView(ErrorView.class);
        menu = new Menu(navigator);
        
        // Add View for permission errors
        navigator.addView("No Permission", new NoPermissionView());
        
        // Configure forms/windows to be displayed in the user menu
        //CompanyEntity compRec = EntryPoint.get().getAccessControl().getCompany();
        
        UserAloneView userViewAlone = new UserAloneView();
        navigator.addView(userViewAlone.VIEW_NAME, userViewAlone);
        
        CompanyAloneView companyViewAlone = new CompanyAloneView();
        navigator.addView(companyViewAlone.VIEW_NAME, companyViewAlone);
        
        // TODO SupportCrudView supportView = new SupportCrudView();
	    // TODO navigator.addView(supportView.VIEW_NAME, supportView);
        
        // Configure forms/windows to be displayed in the user menu
        
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER)){
	        CompanyCrudView companyView = new CompanyCrudView();
	        menu.addView(companyView, companyView.VIEW_NAME, FontAwesome.BUILDING);
        }
        
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN)){
        	
        	// Section Datos Maestros - Perfil Administrador
            menu.addTitle(Messages.get().getKey("maestros"), FontAwesome.ARCHIVE);
        	
	        UserCrudView userView = new UserCrudView();
	        menu.addView(userView, userView.VIEW_NAME, FontAwesome.USERS);
        
	        // TODO MailServerCrudView emailView = new MailServerCrudView();
			// TODO menu.addView(emailView, emailView.VIEW_NAME, FontAwesome.SERVER);
	        
	        String configuration = Messages.get().getKey("parametros");
		    GenericCrudView<ConfigEntity> configurationCrud = new GenericCrudView<ConfigEntity>(
		       		"PERMISSION", configuration, ConfigEntity.class,
		       		"id", "name", "value");
		    menu.addView(configurationCrud, configuration, FontAwesome.EDIT);
		    
		    String circuitos = Messages.get().getKey("circuitos");
		    CircuitoForm circuitoForm = new CircuitoForm();
		    GenericCrudView<CircuitoEntity> circuitoCrud = new GenericCrudView<CircuitoEntity>(
		       		"PERMISSION", circuitos, circuitoForm, CircuitoEntity.class,
		       		"id", "nombre", "temporada");
		    menu.addView(circuitoCrud, circuitos, FontAwesome.CALENDAR_TIMES_O);
		    
		    String catmodalidades = Messages.get().getKey("categorias");
		    CategoriaForm catmodalidadForm = new CategoriaForm();
		    GenericCrudView<CategoriaEntity> catmodCrud = new GenericCrudView<CategoriaEntity>(
		       		"PERMISSION", catmodalidades, catmodalidadForm, CategoriaEntity.class,
		       		"id", "nombre", "modalidad", "edadMinima", "edadMaxima", "genero", "active");
		    menu.addView(catmodCrud, catmodalidades, FontAwesome.LIST_UL);
		    
		    String puntuaciones = Messages.get().getKey("puntuaciones");
		    GenericCrudView<PuntuacionesEntity> puntuacionesCrud = new GenericCrudView<PuntuacionesEntity>(
		       		"PERMISSION", puntuaciones, PuntuacionesEntity.class,
		       		"id", "clasificacion", "puntosCampeonato", "puntosCopa", "puntosTrofeo");
		    menu.addView(puntuacionesCrud, puntuaciones, FontAwesome.LINE_CHART);
        	
        }
        
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD)){
        	
        	// Section Inscripciones - Perfil Federacion
            menu.addTitle(Messages.get().getKey("inscripciones"), FontAwesome.CLIPBOARD);
        	
        	String clubes = Messages.get().getKey("clubes");
	        ClubForm clubForm = new ClubForm();
		    GenericCrudView<ClubEntity> clubesCrud = new GenericCrudView<ClubEntity>(
		       		"PERMISSION", clubes, clubForm, ClubEntity.class,
		       		"id", "nombre", "coordinador", "email", "telefono", "localidad");
		    menu.addView(clubesCrud, clubes, FontAwesome.UNIVERSITY);
        	
        	String patinadores = Messages.get().getKey("patinadores");
		    GenericCrudView<PatinadorEntity> patinadoresCrud = new GenericCrudView<PatinadorEntity>(
		       		//"PERMISSION", patinadores, patinadorForm, PatinadorEntity.class,
		       		"PERMISSION", patinadores, PatinadorForm.class, PatinadorEntity.class,
		       		"id", "nombre", "apellidos", "fichaFederativa", "clubStr", "fechaNacimiento", "active");
		    menu.addView(patinadoresCrud, patinadores, FontAwesome.CHILD);
		    
		    String parejasjam = Messages.get().getKey("parejasjam");
		    ParejaJamForm parejajamForm = new ParejaJamForm();
		    GenericCrudView<ParejaJamEntity> parejasjamCrud = new GenericCrudView<ParejaJamEntity>(
		    		"PERMISSION", parejasjam, parejajamForm, ParejaJamEntity.class,	
		       		"id", "nombre1", "apellidos1", "nombre2", "apellidos2", "clubStr", "categoriaStr");
		    menu.addView(parejasjamCrud, parejasjam, FontAwesome.CHILD);
		    
		    String campeonatos = Messages.get().getKey("competiciones");
		    CompeticionForm competicionForm = new CompeticionForm();
		    GenericCrudView<CompeticionEntity> campeonatoCrud = new GenericCrudView<CompeticionEntity>(
		       		"PERMISSION", campeonatos, competicionForm, CompeticionEntity.class,
		       		"id", "nombre", "organizador", "circuitoStr", "tipo", "localidad", "fechaInicio", "active");
		    menu.addView(campeonatoCrud, campeonatos, FontAwesome.TROPHY);
		    
		    // Inscripciones
		    PreinscripcionCrudView inscripcionView = new PreinscripcionCrudView(InscripcionEnum.INSCRIPCION);
        	menu.addView(inscripcionView, inscripcionView.VIEW_NAME, FontAwesome.PAPERCLIP);
		    
        	InformesView informesCrud = new InformesView();
		    menu.addView(informesCrud, informesCrud.VIEW_NAME, FontAwesome.FILES_O);
        	
        }
        
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER)){
        	
        	// Section Pruebas - Jueces
        	menu.addTitle(Messages.get().getKey("pruebas"), FontAwesome.GAMEPAD);
        
	        // Dorsales
		    DorsalCrudView dorsalesView = new DorsalCrudView();
		    menu.addView(dorsalesView, dorsalesView.VIEW_NAME, FontAwesome.SIGN_IN);
		    
		    // Gestion Categorias
		    GestionCrudView gestionView = new GestionCrudView();
		    menu.addView(gestionView, gestionView.VIEW_NAME, FontAwesome.ADJUST);
		    
	        // Competicion Speed
		    SpeedCrudView speedView = new SpeedCrudView();
		    menu.addView(speedView, speedView.VIEW_NAME, FontAwesome.TROPHY);
	        
		    // Competicion Salto
		    SaltoCrudView saltoView = new SaltoCrudView();
		    menu.addView(saltoView, saltoView.VIEW_NAME, FontAwesome.TROPHY);
		    
		    // Competicion Derrapes
		    DerrapesCrudView derrapesView = new DerrapesCrudView();
		    menu.addView(derrapesView, derrapesView.VIEW_NAME, FontAwesome.TROPHY);
	    
		    // Competicion Battle
		    BattleCrudView battleView = new BattleCrudView();
		    menu.addView(battleView, battleView.VIEW_NAME, FontAwesome.TROPHY);
		    
		    // Competicion Classic
		    ClassicCrudView classicView = new ClassicCrudView();
		    menu.addView(classicView, classicView.VIEW_NAME, FontAwesome.TROPHY);
		    
		    // Competicion Jam
		    JamCrudView jamView = new JamCrudView();
		    menu.addView(jamView, jamView.VIEW_NAME, FontAwesome.TROPHY);
        
        }
        
	    // Section Resultados - Todos los demás usuarios
        menu.addTitle(Messages.get().getKey("resultados"), FontAwesome.USERS);
        
	        // Resultados Competición en Curso
	    	ResultadosCrudView resultadosView = new ResultadosCrudView();
	    	menu.addView(resultadosView, resultadosView.VIEW_NAME, FontAwesome.CHILD);
    	
	    	// Ranking del Circuito
        	RankingCrudView rankingView = new RankingCrudView();
        	menu.addView(rankingView, rankingView.VIEW_NAME, FontAwesome.SORT_ALPHA_ASC);
        	
        	// Pre-Inscripción
        	PreinscripcionCrudView preinscripcionView = new PreinscripcionCrudView(InscripcionEnum.PREINSCRIPCION);
        	menu.addView(preinscripcionView , Messages.get().getKey("preinscripciones"), FontAwesome.PAPERCLIP);
	    
        	// Palmares
		    PalmaresCrudView participantesCrud = new PalmaresCrudView();
		    menu.addView(participantesCrud, participantesCrud.VIEW_NAME, FontAwesome.TROPHY);
		    
		    // Mejores Marcas
		    MarcasCrudView marcasCrud = new MarcasCrudView();
		    menu.addView(marcasCrud, marcasCrud.VIEW_NAME, FontAwesome.WRENCH);
		    
		    // Ranking Absoluto
        	RankingAbsCrudView rankingabsView = new RankingAbsCrudView();
        	menu.addView(rankingabsView, rankingabsView.VIEW_NAME, FontAwesome.SORT_NUMERIC_ASC);
        	
        // Section About
        //menu.addTitle(Messages.get().getKey("aboutview.viewname"));
        
        AboutView aboutView = new AboutView();
        navigator.addView(aboutView.VIEW_NAME, aboutView);

        // Default screen
        navigator.addViewChangeListener(viewChangeListener);
        navigator.navigateTo(aboutView.VIEW_NAME);
        navigator.setErrorView(AboutView.class);

        // Create layout
        addComponent(menu);
        addComponent(viewContainer);
        setExpandRatio(viewContainer, 1);
        setSizeFull();
	}

	// notify the view menu about view changes so that it can display which view
    // is currently active
    ViewChangeListener viewChangeListener = new ViewChangeListener() {

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            menu.setActiveView(event.getViewName());
        }

    };
	
    /* BVM Component header(){  	
    	HorizontalLayout layout = new HorizontalLayout();
    	layout.addStyleName("menu-title");
    	layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidth("100%");
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        
        // button for toggling the visibility of the menu when on a small screen
        Button menuBtn = new Button("", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                if (menu.isVisible()) {
                    menu.setVisible(false);
                } else {
                    menu.setVisible(true);
                }
            }
        });
        menuBtn.setIcon(FontAwesome.NAVICON);
        menuBtn.setStyleName(ValoTheme.BUTTON_LARGE);
        menuBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        layout.addComponent(menuBtn);
        
        Image image = new Image(null, new ThemeResource("img/LogoFAP90x90.jpg"));
        //image.setWidth("100px");
        image.setHeight("44px");
        layout.addComponent(image);
        layout.setComponentAlignment(image, Alignment.MIDDLE_LEFT);
        
        Label title = new Label("FAP Free<b>Style</b>", ContentMode.HTML);
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        title.setSizeUndefined();
        layout.addComponent(title);
        
        // logout menu item
        MenuBar logoutMenu = new MenuBar();
        logoutMenu.addItem("Logout ("
        	+EntryPoint.get().getAccessControl().getPrincipalName()
        	+")", FontAwesome.SIGN_OUT, new Command() {
	            @Override
	            public void menuSelected(MenuItem selectedItem) {
	                EntryPoint.get().userLogout();
	            }
        });
        logoutMenu.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        layout.addComponent(logoutMenu);
        
        layout.setExpandRatio(title, 1);
    	return layout;
    }*/
    
}
