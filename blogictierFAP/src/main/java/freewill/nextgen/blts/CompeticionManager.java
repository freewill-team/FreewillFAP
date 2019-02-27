package freewill.nextgen.blts;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.CategoriaRepository;
import freewill.nextgen.blts.daos.CircuitoRepository;
import freewill.nextgen.blts.daos.CompeticionRepository;
import freewill.nextgen.blts.daos.ParticipanteRepository;
import freewill.nextgen.blts.daos.PatinadorRepository;
import freewill.nextgen.blts.daos.RankingAbsRepository;
import freewill.nextgen.blts.daos.RankingRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CategoriaEntity;
import freewill.nextgen.blts.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.blts.data.CompeticionEntity;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PatinadorEntity;
import freewill.nextgen.blts.data.RankingAbsEntity;
import freewill.nextgen.blts.data.RankingEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   CompeticionManager.java
 * Date:   04/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Competicion
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/CompeticionEntity")
public class CompeticionManager {
	
	@Autowired
	CompeticionRepository repository;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	CategoriaRepository categorepo;
	
	@Autowired
	CircuitoRepository circuitorepo;
	
	@Autowired
	ParticipanteRepository inscripcionesrepo;
	
	@Autowired
	RankingRepository rankingrepo;
	
	@Autowired
	RankingAbsRepository rankingabsrepo;
	
	@Autowired
	PatinadorRepository patinrepo;

	@RequestMapping("/create")
	public CompeticionEntity add(@RequestBody CompeticionEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Competicion..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			CompeticionEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public CompeticionEntity update(@RequestBody CompeticionEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Competicion..."+rec);
			CompeticionEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Competicion..."+recId);
			CompeticionEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<CompeticionEntity> getlist() throws Exception {
		System.out.println("Getting Entire Competicion List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<CompeticionEntity> recs = repository.findByCompanyOrderByFechaInicioDesc(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public CompeticionEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Competicion..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getCompeticiones/{recId}")
	public List<CompeticionEntity> getCompeticiones(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Competicion List By Circuito..."+recId);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<CompeticionEntity> recs = repository.findByCircuitoOrderByFechaInicioDesc(recId);
		return recs;
	}
	
	@RequestMapping("/getLastCompeticion")
	public CompeticionEntity getLastCompeticion() throws Exception {
		System.out.println("Getting Last Competicion...");
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<CompeticionEntity> recs = repository.findTop1ByFechaInicioBeforeOrderByFechaInicioDesc(new Date());
		if(recs!=null && recs.size()>0)
			return recs.get(0);
		else
			throw new IllegalArgumentException("No hay Competiciones definidas");
	}
	
	@RequestMapping("/closeCompeticion/{recId}")
	public CompeticionEntity closeCompeticion(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Closing Competicion..."+recId);
			CompeticionEntity rec = repository.findById(recId);
			if(rec!=null){
				// forzar ejecución de recalculo de Rankings para todas las categorias y modalidades
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				UserEntity user = userrepo.findByLoginname(auth.getName());
				CalculateRankingThread thread1 = new CalculateRankingThread(rec.getCircuito(), user);
				thread1.start();
				CalculateRankingAbsThread thread2 = new CalculateRankingAbsThread(user);
				thread2.start();
				
				rec.setActive(false);
				repository.save(rec);
				return rec;
			}
		}
		return null;	
	}
	
	class CalculateRankingThread extends Thread {
		
		private Long circuito;
		private UserEntity user;
    	
    	public CalculateRankingThread(Long circuito, UserEntity user){
    		this.circuito = circuito;
    		this.user = user;
    	}
    	
        @Override
        public void run() {
            try {
            	//CircuitoEntity circuito = circuitorepo.findById(circuitoId);
            	System.out.println("CalculateRankingThread thread started for circuito..."+circuito);
            	// Inicializa los valores ya existentes
        		List<RankingEntity> recs = rankingrepo.findByCircuito(circuito);
        		for(RankingEntity rec:recs){
        			rec.setPuntuacion(0);
        			rec.setPuntos1(0);
        			rec.setPuntos2(0);
        			rec.setPuntos3(0);
        			rec.setPuntos4(0);
        			rec.setCompeticion1("");
        			rec.setCompeticion2("");
        			rec.setCompeticion3("");
        			rec.setCompeticion4("");
        			rec.setCategoriaStr("");
        			rankingrepo.save(rec);
        			//System.out.println("  Reseting..."+rec);
        			if(rec.getCategoriaStr().contains("Mixto"))
        				rankingrepo.delete(rec);
        		}
            	// Procesa valores para cada modalidad
            	for(ModalidadEnum modalidad:ModalidadEnum.values()){
            		generateByModalidad(modalidad, circuito);
            	}
            	
            	System.out.println("CalculateRankingThread thread finished.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void generateByModalidad(ModalidadEnum modalidad, Long circuito) 
        		throws Exception {
    		System.out.println("Generating Ranking By modalidad..."+modalidad);
    		// Obtiene categorias para la modalidad
    		List<CategoriaEntity> categorias = categorepo.findByModalidadAndCompany(
    				modalidad, user.getCompany());
    		
    		// Obtiene las competiciones de este circuito
    		//List<CompeticionEntity> campeonatos = repository.findByCircuito(circuito);
    		List<CompeticionEntity> campeonatos = null;
    		Date now = new Date();
    		switch(modalidad){
    			case SPEED:
    				campeonatos = repository.findTop4ByCircuitoAndSpeedAndFechaInicioBeforeOrderByFechaFinDesc(circuito,true, now);
    				break;
    			case BATTLE:
    				campeonatos = repository.findTop4ByCircuitoAndBattleAndFechaInicioBeforeOrderByFechaFinDesc(circuito,true, now);
    				break;
    			case CLASSIC:
    				campeonatos = repository.findTop4ByCircuitoAndClassicAndFechaInicioBeforeOrderByFechaFinDesc(circuito,true, now);
    				break;
    			case JAM:
    				campeonatos = repository.findTop4ByCircuitoAndJamAndFechaInicioBeforeOrderByFechaFinDesc(circuito,true, now);
    				break;
    			case SLIDE:
    				campeonatos = repository.findTop4ByCircuitoAndDerrapesAndFechaInicioBeforeOrderByFechaFinDesc(circuito,true, now);
    				break;
    			case JUMP:
    				campeonatos = repository.findTop4ByCircuitoAndSaltoAndFechaInicioBeforeOrderByFechaFinDesc(circuito, true, now);
    				break;
    		}
    		
    		// Procesa las inscripciones de estas competiciones para cada categoria
    		int i = 1;
    		for(CompeticionEntity rec:campeonatos){
    			System.out.println("  Procesando Competicion "+rec);
    			for(CategoriaEntity categoria:categorias){
    				System.out.println("    Procesando Categoria "+categoria);
        			saveRankingByCompeticionAndCategoria(i, rec, categoria, categorias);
        		}
    			i++;
    		}	
    	}
        
        private void saveRankingByCompeticionAndCategoria(int i,
        		CompeticionEntity competi, CategoriaEntity categoria, 
        		List<CategoriaEntity> categorias) {
			List<ParticipanteEntity> inscripciones = 
					inscripcionesrepo.findByCompeticionAndCategoria(competi.getId(), categoria.getId());
			// Acumula las puntuaciones conseguidas
    		for(ParticipanteEntity inscripcion:inscripciones){
    			// Obtiene la categoria estandard para este patinador
    			CategoriaEntity categoriaStd = getCategoriaStd(categorias,
    					inscripcion.getPatinador(), categoria.getModalidad());
    			if(categoriaStd==null){
    				System.out.println("Hay un registro huerfano = "+inscripcion);
    				continue;
    			}
    			//System.out.println("      Patinador="+inscripcion.getId()+" CatStd="+categoriaStd);
    			
    			// Crea o Salva el registro de ranking
    			RankingEntity rec = rankingrepo.findByPatinadorAndCircuitoAndCategoria(
    					inscripcion.getPatinador(), circuito, categoriaStd.getId());
    			if(rec==null){
    				// Create new record
    				rec = new RankingEntity();
    				rec.setApellidos(inscripcion.getApellidos());
    				rec.setNombre(inscripcion.getNombre());
    				rec.setPatinador(inscripcion.getPatinador());
    				rec.setClub(inscripcion.getClub());
    				rec.setClubStr(inscripcion.getClubStr());
    				rec.setCompany(user.getCompany());
    				rec.setCategoria(categoriaStd.getId());
    				rec.setCircuito(circuito);
    				//rec.setPuntuacion(inscripcion.getPuntuacion());
    				//rec.setPuntos1(0);
    				//rec.setPuntos2(0);
    				//rec.setPuntos3(0);
    				//rec.setPuntos4(0);
    			}
    			// acumula puntuaciones y calcula best of 3
    			switch(i){
				case 1: 
					rec.setPuntos1(inscripcion.getPuntuacion());
    				rec.setCompeticion1(competi.getNombre());
    				break;
				case 2: 
					rec.setPuntos2(inscripcion.getPuntuacion());
    				rec.setCompeticion2(competi.getNombre());
    				break;
				case 3: 
					rec.setPuntos3(inscripcion.getPuntuacion());
    				rec.setCompeticion3(competi.getNombre());
    				break;
				case 4: 
					rec.setPuntos4(inscripcion.getPuntuacion());
    				rec.setCompeticion4(competi.getNombre());
    				break;
				}
    			rec.setPuntuacion(getBest3Of4(rec));
				rec.setCategoriaStr(categoriaStd.getNombre());
    			rankingrepo.save(rec);
				System.out.println("      Saving..."+rec);
    		}
		}
    	
        private int getBest3Of4(RankingEntity rec) {
    		int minimo = 20000;
    		int suma = rec.getPuntos1()+rec.getPuntos2()+rec.getPuntos3()+rec.getPuntos4();
    		//System.out.println("    Suma = "+suma);
    		if(rec.getPuntos1()<minimo)
    			minimo = rec.getPuntos1();
    		if(rec.getPuntos2()<minimo)
    			minimo = rec.getPuntos2();
    		if(rec.getPuntos3()<minimo)
    			minimo = rec.getPuntos3();
    		if(rec.getPuntos4()<minimo)
    			minimo = rec.getPuntos4();
    		if(minimo!=20000)
    			suma = suma - minimo;
    		//System.out.println("    Minimo = "+minimo);
    		//System.out.println("    Best3of4 = "+suma);
    		return suma;
    	}
        
    }
	
	class CalculateRankingAbsThread extends Thread {
		private UserEntity user;
    	
    	public CalculateRankingAbsThread(UserEntity user){
    		this.user = user;
    	}
    	
        @Override
        public void run() {
            try {
            	System.out.println("CalculateRankingAbsThread thread started...");
            	for(ModalidadEnum modalidad:ModalidadEnum.values()){
            		saveRankingByModalidad(modalidad);
            	}
            	System.out.println("CalculateRankingAbsThread thread finished.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void saveRankingByModalidad(ModalidadEnum modalidad) 
        		throws Exception {
    		System.out.println("Generating RankingAbs By modalidad..."+modalidad);
    		// Obtiene categorias para la modalidad
    		List<CategoriaEntity> categorias = categorepo.findByModalidadAndCompany(
    				modalidad, user.getCompany());
    		
    		// Inicializa los valores ya existentes
    		List<RankingAbsEntity> recs = 
    				rankingabsrepo.findByModalidadAndCompanyOrderByPuntuacionDesc(
    						modalidad, user.getCompany());
    		for(RankingAbsEntity rec:recs){
    			rec.setPuntuacion(0);
    			rec.setPuntos1(0);
    			rec.setPuntos2(0);
    			rec.setPuntos3(0);
    			rec.setPuntos4(0);
    			rec.setCompeticion1("");
    			rec.setCompeticion2("");
    			rec.setCompeticion3("");
    			rec.setCompeticion4("");
    			rec.setCategoriaStr("");
    			rankingabsrepo.save(rec);
    			//System.out.println("  Reseting..."+rec);
    		}
    		// Obtiene los Ids de las ultimas 4 competiciones
    		List<CompeticionEntity> campeonatos = null;
    		Date now = new Date();
    		switch(modalidad){
    			case SPEED:
    				campeonatos = repository.findTop4BySpeedAndFechaInicioBeforeOrderByFechaFinDesc(true, now);
    				break;
    			case BATTLE:
    				campeonatos = repository.findTop4ByBattleAndFechaInicioBeforeOrderByFechaFinDesc(true, now);
    				break;
    			case CLASSIC:
    				campeonatos = repository.findTop4ByClassicAndFechaInicioBeforeOrderByFechaFinDesc(true, now);
    				break;
    			case JAM:
    				campeonatos = repository.findTop4ByJamAndFechaInicioBeforeOrderByFechaFinDesc(true, now);
    				break;
    			case SLIDE:
    				campeonatos = repository.findTop4ByDerrapesAndFechaInicioBeforeOrderByFechaFinDesc(true, now);
    				break;
    			case JUMP:
    				campeonatos = repository.findTop4BySaltoAndFechaInicioBeforeOrderByFechaFinDesc(true, now);
    				break;
    		}
    		
    		// Procesa las inscripciones de estas 4 competiciones para cada categoria
    		int i = 1;
    		for(CompeticionEntity rec:campeonatos){
    			System.out.println("  Procesando Competicion "+rec);
    			for(CategoriaEntity categoria:categorias){
    				System.out.println("    Procesando Categoria "+categoria);
        			saveRankingByCompeticionAndCategoria(i, rec, categoria, categorias);
    			}
        		i++;
    		}	
    	}

		private void saveRankingByCompeticionAndCategoria(int i,
				CompeticionEntity competi, CategoriaEntity categoria,
				List<CategoriaEntity> categorias) {
			List<ParticipanteEntity> inscripciones = 
					inscripcionesrepo.findByCompeticionAndCategoria(
							competi.getId(), categoria.getId());
			// Acumula las puntuaciones conseguidas
    		for(ParticipanteEntity inscripcion:inscripciones){
    			// Obtiene la categoria estandard para este patinador
    			CategoriaEntity categoriaStd = getCategoriaStd(categorias,
    					inscripcion.getPatinador(), categoria.getModalidad());
    			if(categoriaStd==null){
    				System.out.println("Hay un registro huerfano = "+inscripcion);
    				continue;
    			}
    			//System.out.println("      Patinador="+inscripcion.getId()+" CatStd="+categoriaStd);
    			
    			// Crea o salva el registro de Ranking
    			RankingAbsEntity rec = rankingabsrepo.findByPatinadorAndModalidad(
    					inscripcion.getPatinador(), categoria.getModalidad());
    			if(rec==null){
    				// Create new record
    				rec = new RankingAbsEntity();
    				rec.setApellidos(inscripcion.getApellidos());
    				rec.setNombre(inscripcion.getNombre());
    				rec.setPatinador(inscripcion.getPatinador());
    				rec.setClub(inscripcion.getClub());
    				rec.setClubStr(inscripcion.getClubStr());
    				rec.setCompany(user.getCompany());
    				rec.setModalidad(categoria.getModalidad());
    				//rec.setPuntuacion(inscripcion.getPuntuacion());
    				//rec.setPuntos1(0);
    				//rec.setPuntos2(0);
    				//rec.setPuntos3(0);
    				//rec.setPuntos4(0);
    			}
    			// acumula puntuaciones y calcula best of 3
    			switch(i){
				case 1: 
					rec.setPuntos1(inscripcion.getPuntuacion());
    				rec.setCompeticion1(competi.getNombre());
    				break;
				case 2: 
					rec.setPuntos2(inscripcion.getPuntuacion());
    				rec.setCompeticion2(competi.getNombre());
    				break;
				case 3: 
					rec.setPuntos3(inscripcion.getPuntuacion());
    				rec.setCompeticion3(competi.getNombre());
    				break;
				case 4: 
					rec.setPuntos4(inscripcion.getPuntuacion());
    				rec.setCompeticion4(competi.getNombre());
    				break;
				}
    			rec.setPuntuacion(getBest3Of4(rec));
    			rec.setCategoriaStr(categoriaStd.getNombre());
    			rankingabsrepo.save(rec);
				System.out.println("  Saving..."+rec);
    		}
		}
    	
		private int getBest3Of4(RankingAbsEntity rec) {
			int minimo = 20000;
			int suma = rec.getPuntos1()+rec.getPuntos2()+rec.getPuntos3()+rec.getPuntos4();
			//System.out.println("    Suma = "+suma);
			if(rec.getPuntos1()<minimo)
				minimo = rec.getPuntos1();
			if(rec.getPuntos2()<minimo)
				minimo = rec.getPuntos2();
			if(rec.getPuntos3()<minimo)
				minimo = rec.getPuntos3();
			if(rec.getPuntos4()<minimo)
				minimo = rec.getPuntos4();
			if(minimo!=20000)
				suma = suma - minimo;
			//System.out.println("    Minimo = "+minimo);
			//System.out.println("    Best3of4 = "+suma);
			return suma;
		}
		
    }
	
	@SuppressWarnings("deprecation")
	private CategoriaEntity getCategoriaStd(List<CategoriaEntity> categorias,
			Long patinador, ModalidadEnum modalidad) {
		if(categorias==null){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UserEntity user = userrepo.findByLoginname(auth.getName());
			categorias = categorepo.findByModalidadAndCompany(
	    			modalidad, user.getCompany());
		}
		PatinadorEntity patin = patinrepo.findById(patinador);
		if(patin==null) return null;
		// A partir de la edad del niño inferirá su posible categoria
    	Date now = new Date();
    	int edad = now.getYear() - patin.getFechaNacimiento().getYear();
    	//System.out.println("Edad = "+edad);
    	
    	for(CategoriaEntity cat:categorias){
    		//System.out.println("Checking ParticipanteEntity..."+cat.getNombre()+" "+
    		//		cat.getEdadMinima()+"-"+cat.getEdadMaxima());
    		if(cat.getEdadMinima()<=edad && edad<=cat.getEdadMaxima() &&
    				(patin.getGenero()==cat.getGenero()) ){
    			return cat;
    		}
    	}
		return null;
	}
	
}