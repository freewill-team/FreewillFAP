package freewill.nextgen.blts;

import java.util.ArrayList;
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
import freewill.nextgen.blts.daos.RankingRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CategoriaEntity;
import freewill.nextgen.blts.data.CircuitoEntity;
import freewill.nextgen.blts.data.CompeticionEntity;
import freewill.nextgen.blts.data.ParticipanteEntity;
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
				rec.setActive(false);
				repository.save(rec);
				
				// forzar ejecución de recalculo de Rankings para todas las categorias y modalidades
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				UserEntity user = userrepo.findByLoginname(auth.getName());
				CalculateRankingThread thread = new CalculateRankingThread(rec.getCircuito(), user);
				thread.start();
				
				return rec;
			}
		}
		return null;	
	}
	
	class CalculateRankingThread extends Thread {
		
		private Long circuitoId;
		private UserEntity user;
    	
    	public CalculateRankingThread(Long circuito, UserEntity user){
    		this.circuitoId = circuito;
    		this.user = user;
    	}
    	
        @Override
        public void run() {
            try {
            	System.out.println("CalculateRankingThread thread started...");
            	CircuitoEntity circuito = circuitorepo.findById(circuitoId);
            	List<CategoriaEntity> categorias = categorepo.findByCompany(user.getCompany());
            	for(CategoriaEntity categoria:categorias){
            		getByCircuitoAndCategoria(circuito, categoria, user);
            	}
            	System.out.println("CalculateRankingThread thread finished.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private List<RankingEntity> getByCircuitoAndCategoria(CircuitoEntity circuito, 
        		CategoriaEntity categoria, UserEntity user) throws Exception {
    		System.out.println("Generating Ranking List By circuito y categoria..."+
    			circuito.getId()+","+categoria.getId());
    		
    		// Inicializa los valores ya existentes
    		List<RankingEntity> recs = rankingrepo.findByCircuitoAndCategoriaOrderByPuntuacionDesc(
    				circuito.getId(), categoria.getId());
    		for(RankingEntity rec:recs){
    			rec.setPuntuacion(0);
    			rec.setPuntos1(0);
    			rec.setPuntos2(0);
    			rec.setPuntos3(0);
    			rec.setPuntos4(0);
    			rankingrepo.save(rec);
    			System.out.println("  Reseting..."+rec);
    		}
    		// Obtiene los Ids de las ultimas 4 competiciones
    		List<CompeticionEntity> campeonatos = null;
    		Date now = new Date();
    		switch(categoria.getModalidad()){
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
    		if(campeonatos==null || campeonatos.size()==0)
    			throw new IllegalArgumentException("No hay datos para mostrar");
    		List<Long> competis = new ArrayList<Long>();
    		for(CompeticionEntity rec:campeonatos){
    			System.out.println("  Procesando Competicion "+rec);
    			competis.add(rec.getId());
    		}
    		// Obtienen las inscripciones de estos 4 competiciones para la categoria seleccionada
    		List<ParticipanteEntity> inscripciones = 
    				inscripcionesrepo.findByCategoriaAndCompeticionIn(categoria.getId(), competis);
    		// Acumula las puntuaciones conseguidas
    		for(ParticipanteEntity inscripcion:inscripciones){
    			RankingEntity rec = rankingrepo.findByPatinadorAndCircuitoAndCategoria(
    					inscripcion.getPatinador(), circuito.getId(), categoria.getId());
    			if(rec==null){
    				// Create new record
    				rec = new RankingEntity();
    				rec.setApellidos(inscripcion.getApellidos());
    				rec.setNombre(inscripcion.getNombre());
    				rec.setPatinador(inscripcion.getPatinador());
    				rec.setClub(inscripcion.getClub());
    				rec.setClubStr(inscripcion.getClubStr());
    				rec.setCompany(user.getCompany());
    				rec.setCategoria(categoria.getId());
    				rec.setCompeticion(inscripcion.getCompeticion());
    				rec.setCircuito(circuito.getId());
    				rec.setPuntuacion(inscripcion.getPuntuacion());
    				rec.setPuntos1(inscripcion.getPuntuacion());
    				rec.setPuntos2(0);
    				rec.setPuntos3(0);
    				rec.setPuntos4(0);
    				rankingrepo.save(rec);
    				System.out.println("  Creating..."+rec);
    			}
    			else{
    				// acumula puntuaciones y calcula best of 3
    				if(rec.getPuntos1()==0)
    					rec.setPuntos1(inscripcion.getPuntuacion());
    				else if(rec.getPuntos2()==0)
    					rec.setPuntos2(inscripcion.getPuntuacion());
    				else if(rec.getPuntos3()==0)
    					rec.setPuntos3(inscripcion.getPuntuacion());
    				else if(rec.getPuntos4()==0)
    					rec.setPuntos4(inscripcion.getPuntuacion());
    				rec.setPuntuacion(getBest3Of4(rec));
    				rankingrepo.save(rec);
    				System.out.println("  Updating..."+rec);
    			}
    		}
    		// retrieve and return new created records, setting resulted orden
    		recs = rankingrepo.findByCircuitoAndCategoriaOrderByPuntuacionDesc(circuito.getId(), categoria.getId());
    		int orden = 1;
    		for(RankingEntity rec:recs)
    			rec.setOrden(orden++);
    		return recs;
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
	
}