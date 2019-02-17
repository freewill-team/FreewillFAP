package freewill.nextgen.blts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import freewill.nextgen.blts.daos.CircuitoRepository;
import freewill.nextgen.blts.daos.CompeticionRepository;
import freewill.nextgen.blts.daos.ConfigRepository;
import freewill.nextgen.blts.daos.ParticipanteRepository;
import freewill.nextgen.blts.daos.PuntuacionesRepository;
import freewill.nextgen.blts.daos.RankingRepository;
import freewill.nextgen.blts.daos.SpeedKOSystemRepository;
import freewill.nextgen.blts.daos.SpeedTimeTrialRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CircuitoEntity;
import freewill.nextgen.blts.data.CompeticionEntity;
import freewill.nextgen.blts.data.ConfigEntity.ConfigItemEnum;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PuntuacionesEntity;
import freewill.nextgen.blts.data.SpeedKOSystemEntity;
import freewill.nextgen.blts.data.SpeedKOSystemEntity.EliminatoriaEnum;
import freewill.nextgen.blts.data.SpeedTimeTrialEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   SpeedTimeTrialManager.java
 * Date:   09/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage SpeedTimeTrial
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/SpeedTimeTrialEntity")
public class SpeedTimeTrialManager {
	
	@Autowired
	SpeedTimeTrialRepository repository;
	
	@Autowired
	ParticipanteRepository inscripcionesrepo;
	
	@Autowired
	SpeedKOSystemRepository korepo;
	
	@Autowired
	PuntuacionesRepository puntosrepo;
	
	@Autowired
	CompeticionRepository competirepo;
	
	@Autowired
	CircuitoRepository circuitorepo;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	RankingRepository rankingrepo;
	
	@Autowired
	ConfigRepository configrepo;

	@RequestMapping("/create")
	public SpeedTimeTrialEntity add(@RequestBody SpeedTimeTrialEntity rec) throws Exception {
		if(rec!=null){
			if(rec.getPatinador()==null)
				throw new IllegalArgumentException("El registro debe contener un Patinador.");
			if(rec.getCompeticion()==null)
				throw new IllegalArgumentException("El registro debe contener un Campeonato.");
			SpeedTimeTrialEntity old = repository.findByPatinadorAndCompeticion(
					rec.getPatinador(), rec.getCompeticion());
			if(old!=null)
				throw new IllegalArgumentException("El registro ya existe.");
			// Injects the new record
			System.out.println("Saving SpeedTimeTrial..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
    		SpeedTimeTrialEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public SpeedTimeTrialEntity update(@RequestBody SpeedTimeTrialEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating SpeedTimeTrial..."+rec);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
			int numConos = configrepo.getConfigInteger(ConfigItemEnum.MAXNUMCONOSDERRIBADOS, user.getCompany());
			int penalizacion = configrepo.getConfigInteger(ConfigItemEnum.PENALIZAZIONCONOS, user.getCompany());
			
			if(rec.getTiempo1B()==0)
				rec.setTiempoAjustado1(rec.getTiempo1A()+penalizacion*rec.getConos1());
			else
				rec.setTiempoAjustado1((rec.getTiempo1A()+rec.getTiempo1B())/2+penalizacion*rec.getConos1());
			rec.setValido1(rec.getConos1()==5?false:true);
			if(rec.getTiempo2B()==0)
				rec.setTiempoAjustado2(rec.getTiempo2A()+penalizacion*rec.getConos2());
			else
				rec.setTiempoAjustado2((rec.getTiempo2A()+rec.getTiempo2B())/2+penalizacion*rec.getConos2());
			
			rec.setValido2(rec.getConos2()>=numConos?false:true);
			if(rec.getValido1()==false)
				rec.setMejorTiempo(rec.getTiempoAjustado2());
			else if(rec.getValido2()==false)
				rec.setMejorTiempo(rec.getTiempoAjustado1());
			else
				rec.setMejorTiempo(Math.min(rec.getTiempoAjustado1(), rec.getTiempoAjustado2()));
			
			SpeedTimeTrialEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting SpeedTimeTrial..."+recId);
			SpeedTimeTrialEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<SpeedTimeTrialEntity> getlist() throws Exception {
		System.out.println("Getting Entire SpeedTimeTrials List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<SpeedTimeTrialEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public SpeedTimeTrialEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving SpeedTimeTrial..."+recId);
		return repository.findById(recId);
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping("/getByCompeticionAndCategoriaOrden1/{competicion}/{categoria}")
	public List<SpeedTimeTrialEntity> getByCompeticionAndCategoriaOrden1(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		
		System.out.println("Getting SpeedTimeTrials List By competicion y categoria..."
			+competicion+","+categoria);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		
		CompeticionEntity competi = competirepo.findById(competicion);
		Date ultimoAnno = new Date();
		ultimoAnno.setYear(ultimoAnno.getYear()-1);
		CircuitoEntity circuitoUltimoAnno = circuitorepo.findByTemporada(ultimoAnno.getYear()+1900);
		
		// Verifica si la competición puede empezar
		Date now = new Date();
		if(competi.getFechaInicio().after(now)){
			//throw new IllegalArgumentException("Esta Competición aun no puede comenzar.");
			return mockByCompeticionAndCategoria(competicion, categoria);
		}
		
		List<SpeedTimeTrialEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(
				competicion, categoria);
		if(recs==null || recs.size()==0){
			// Needs to create records
			List<ParticipanteEntity> inscripciones = 
					inscripcionesrepo.findByCompeticionAndCategoria(competicion, categoria);
			int orden = 1;
			for(ParticipanteEntity inscripcion:inscripciones){
				// Inicializa valores (just in case)
				inscripcion.setClasificacion(999);
				inscripcion.setPuntuacion(0);
				inscripcion.setMejorMarca(0);
				inscripcionesrepo.save(inscripcion);
				if(inscripcion.getDorsal()==0) continue;
				// Create individual record
				SpeedTimeTrialEntity rec = new SpeedTimeTrialEntity();
				rec.setApellidos(inscripcion.getApellidos());
				rec.setCategoria(inscripcion.getCategoria());
				rec.setCompeticion(inscripcion.getCompeticion());
				rec.setNombre(inscripcion.getNombre());
				rec.setDorsal(inscripcion.getDorsal());
				
				rec.setOrden1(rankingrepo.getSortedRanking(inscripcion.getPatinador(), 
						competi.getCircuito(), categoria, circuitoUltimoAnno.getId()));
				System.out.println("Creating "+rec+" Orden "+rec.getOrden1());
				
				rec.setOrden2(rec.getOrden1());
				rec.setClasificacion(rec.getOrden1());
				rec.setClasificacionFinal(rec.getOrden1());
				rec.setPatinador(inscripcion.getPatinador());
				rec.setCompany(user.getCompany());
				repository.save(rec);
			}
			
			// ordena los registros por el ranking absoluto
			orden = 1;
			recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
			for(SpeedTimeTrialEntity rec:recs){
				rec.setOrden1(orden++);
				rec.setOrden2(rec.getOrden1());
				rec.setClasificacion(rec.getOrden1());
				rec.setClasificacionFinal(rec.getOrden1());
				repository.save(rec);
			}
						
			// retrieve and return new created records
			recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
		}
		return recs;
	}
	
	@SuppressWarnings("deprecation")
	private List<SpeedTimeTrialEntity> mockByCompeticionAndCategoria(Long competicion, Long categoria) {
		// Simula la ordenacion por Ranking, pero no la persiste
		List<SpeedTimeTrialEntity> recs = new ArrayList<SpeedTimeTrialEntity>();
		
		CompeticionEntity competi = competirepo.findById(competicion);
		Date ultimoAnno = new Date();
		ultimoAnno.setYear(ultimoAnno.getYear()-1);
		CircuitoEntity circuitoUltimoAnno = circuitorepo.findByTemporada(ultimoAnno.getYear()+1900);
		
		List<ParticipanteEntity> inscripciones = 
				inscripcionesrepo.findByCompeticionAndCategoria(competicion, categoria);
		
		for(ParticipanteEntity inscripcion:inscripciones){
			// Create individual record
			SpeedTimeTrialEntity rec = new SpeedTimeTrialEntity();
			rec.setApellidos(inscripcion.getApellidos());
			rec.setCategoria(inscripcion.getCategoria());
			rec.setCompeticion(inscripcion.getCompeticion());
			rec.setNombre(inscripcion.getNombre());
			rec.setDorsal(inscripcion.getDorsal());
			
			rec.setOrden1(rankingrepo.getSortedRanking(inscripcion.getPatinador(), 
					competi.getCircuito(), categoria, circuitoUltimoAnno.getId()));
			System.out.println("Mocking "+rec+" Orden "+rec.getOrden1());
			
			rec.setOrden2(rec.getOrden1());
			rec.setClasificacion(rec.getOrden1());
			rec.setClasificacionFinal(rec.getOrden1());
			rec.setPatinador(inscripcion.getPatinador());
			//rec.setCompany(user.getCompany());
			recs.add(rec);
		}
		
		// ordena los registros por el ranking absoluto
		Collections.sort(recs, new Comparator<SpeedTimeTrialEntity>() {
			@Override
			public int compare(SpeedTimeTrialEntity o1, SpeedTimeTrialEntity o2) {
				return o2.getOrden1()-o1.getOrden1();
			}
		});
		int orden = 1;
		for(SpeedTimeTrialEntity rec:recs){
			rec.setOrden1(orden++);
			rec.setId(rec.getOrden1());
		}
		
		return recs;
	}

	@RequestMapping("/getByCompeticionAndCategoriaOrden2/{competicion}/{categoria}")
	public List<SpeedTimeTrialEntity> getByCompeticionAndCategoriaOrden2(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting SpeedTimeTrials List By competicion y categoria..."
			+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		
		// Obtiene lista resultados ronda 1 del timetrial ordenada por tiempo
		List<SpeedTimeTrialEntity> recs = repository.findByCompeticionAndCategoriaOrderByTiempoAjustado1Asc(
				competicion, categoria);
		// Actualiza orden2
		int orden = 1;
		for(SpeedTimeTrialEntity rec:recs){
			rec.setOrden2(orden++);
			repository.save(rec);
		}
		
		// Obtiene lista resultados ronda 1 del timetrial ordenada
		List<SpeedTimeTrialEntity> output = repository.findByCompeticionAndCategoriaOrderByOrden2Asc(
				competicion, categoria);
		return output;
	}
	
	@RequestMapping("/getResultados/{competicion}/{categoria}")
	public List<SpeedTimeTrialEntity> getResultados(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting SpeedTimeTrials Results TimeTrial By competicion y categoria..."
			+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		//int numConos = configrepo.getConfigInteger(ConfigItemEnum.MAXNUMCONOSDERRIBADOS, user.getCompany());
		
		// Obtiene lista con la clasificacion del timetrial por tiempos
		List<SpeedTimeTrialEntity> recs = repository.findByCompeticionAndCategoriaOrderByMejorTiempoAsc(
				competicion, categoria);
		// Asegura valores correctos para Tiempo Ajustado 1 y Tiempo Ajustado 2
		for(SpeedTimeTrialEntity rec:recs){
			// Asegura valores correctos para Tiempo Ajustado 1
			if(rec.getValido1()==false)
				rec.setTiempoAjustado1(100000+rec.getOrden1());
			
			// Asegura valores correctos para Tiempo Ajustado 2
			if(rec.getValido2()==false)
				rec.setTiempoAjustado2(100000+rec.getOrden1());
			
			// Calcula Mejor Tiempo
			if(rec.getValido1()==false)
				rec.setMejorTiempo(rec.getTiempoAjustado2());
			else if(rec.getValido2()==false)
				rec.setMejorTiempo(rec.getTiempoAjustado1());
			else
				rec.setMejorTiempo(Math.min(rec.getTiempoAjustado1(), rec.getTiempoAjustado2()));
			
			repository.save(rec);
		}
		
		// Actualiza clasificacion
		recs = repository.findByCompeticionAndCategoriaOrderByMejorTiempoAsc(
				competicion, categoria);
		int orden = 1;
		for(SpeedTimeTrialEntity rec:recs){
			rec.setClasificacion(orden++);
			repository.save(rec);
		}
				
		// Obtiene la clasificaci�n final del timetrial ordenada	
		List<SpeedTimeTrialEntity> output = repository.findByCompeticionAndCategoriaOrderByClasificacionAsc(
						competicion, categoria);
		return output;
	}
	
	@RequestMapping("/updateTiempo1")
	public boolean updateTiempo1(@RequestBody SpeedTimeTrialEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating SpeedTimeTrial Tiempo1..."+rec);
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
			int numConos = configrepo.getConfigInteger(ConfigItemEnum.MAXNUMCONOSDERRIBADOS, user.getCompany());
			int penalizacion = configrepo.getConfigInteger(ConfigItemEnum.PENALIZAZIONCONOS, user.getCompany());
			
    		// Calcula Tiempo Ajustado 1
    		if(rec.getTiempo1B()==0)
				rec.setTiempoAjustado1(rec.getTiempo1A()+penalizacion*rec.getConos1());
			else
				rec.setTiempoAjustado1((rec.getTiempo1A()+rec.getTiempo1B())/2+penalizacion*rec.getConos1());
			rec.setValido1(rec.getConos1()>=numConos?false:true);
			if(rec.getValido1()==false)
				rec.setTiempoAjustado1(100000+rec.getOrden1());
			
			SpeedTimeTrialEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return true;
		}
		return false;	
	}
	
	@RequestMapping("/updateTiempo2")
	public boolean updateTiempo2(@RequestBody SpeedTimeTrialEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating SpeedTimeTrial Tiempo2..."+rec);
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
			int numConos = configrepo.getConfigInteger(ConfigItemEnum.MAXNUMCONOSDERRIBADOS, user.getCompany());
			int penalizacion = configrepo.getConfigInteger(ConfigItemEnum.PENALIZAZIONCONOS, user.getCompany());
			
    		// Calcula Tiempo Ajustado 2
    		if(rec.getTiempo2B()==0)
				rec.setTiempoAjustado2(rec.getTiempo2A()+penalizacion*rec.getConos2());
			else
				rec.setTiempoAjustado2((rec.getTiempo2A()+rec.getTiempo2B())/2+penalizacion*rec.getConos2());
			rec.setValido2(rec.getConos2()>=numConos?false:true);
			if(rec.getValido2()==false)
				rec.setTiempoAjustado2(100000+rec.getOrden1());
			
			// Calcula Mejor Tiempo
			if(rec.getValido1()==false)
				rec.setMejorTiempo(rec.getTiempoAjustado2());
			else if(rec.getValido2()==false)
				rec.setMejorTiempo(rec.getTiempoAjustado1());
			else
				rec.setMejorTiempo(Math.min(rec.getTiempoAjustado1(), rec.getTiempoAjustado2()));
			
			SpeedTimeTrialEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return true;
		}
		return false;	
	}
	
	@RequestMapping("/getResultadosFinal/{competicion}/{categoria}")
	public List<SpeedTimeTrialEntity> getResultadosFinal(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting SpeedTimeTrials Results Final By competicion y categoria..."
			+competicion+","+categoria);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		boolean consolacion = configrepo.getConfigBoolean(
				ConfigItemEnum.FINALCONSOLACIONSPEED, user.getCompany());
		
		for(EliminatoriaEnum eliminatoria:EliminatoriaEnum.values()){
			List<SpeedKOSystemEntity> recs = 
					korepo.findByCompeticionAndCategoriaAndEliminatoria(competicion, categoria, eliminatoria);
			
			for(SpeedKOSystemEntity rec:recs){
				if(eliminatoria==EliminatoriaEnum.FINAL){
					if(rec.getGrupo()==0){
						// Ganador final - primer clasificado
						SpeedTimeTrialEntity patin = (SpeedTimeTrialEntity)
								repository.findByPatinadorAndCompeticion(
								rec.getGanador(), rec.getCompeticion());
						if(patin!=null){
							patin.setClasificacionFinal(1);
							repository.save(patin);
							System.out.println("Setting "+rec.getGanador()+" to "+1);
						}
						// segundo clasificado
						Long segundo = (rec.getGanador().longValue()==rec.getPatinador1().longValue()?
								rec.getPatinador2():rec.getPatinador1());
						patin = (SpeedTimeTrialEntity) 
								repository.findByPatinadorAndCompeticion(
								segundo, rec.getCompeticion());
						if(patin!=null){
							patin.setClasificacionFinal(2);
							repository.save(patin);
							System.out.println("Setting "+segundo+" to "+2);
						}
					}
					else{ // rec.getGrupo()==1
						// Ganador consolacion - tercer clasificado
						SpeedTimeTrialEntity patin = (SpeedTimeTrialEntity)
								repository.findByPatinadorAndCompeticion(
								rec.getGanador(), rec.getCompeticion());
						if(patin!=null){
							patin.setClasificacionFinal(3);
							repository.save(patin);
							System.out.println("Setting "+rec.getGanador()+" to "+3);
						}
						// cuarto clasificado
						Long segundo = (rec.getGanador()!=null && rec.getPatinador1()!=null &&
								rec.getGanador().longValue()==rec.getPatinador1().longValue()?
								rec.getPatinador2():rec.getPatinador1());
						patin = (SpeedTimeTrialEntity) 
								repository.findByPatinadorAndCompeticion(
								segundo, rec.getCompeticion());
						if(patin!=null){
							patin.setClasificacionFinal(4);
							repository.save(patin);
							System.out.println("Setting "+segundo+" to "+4);
						}
					}
				}
				else if(consolacion && rec.getEliminatoria()==EliminatoriaEnum.SEMIS){
					// Ignorar, ya han sido tratados antes
				}
				else{
					// Resto de Eliminatorias
					Long segundo = (rec.getGanador()!=null && rec.getPatinador1()!=null &&
							rec.getGanador().longValue()==rec.getPatinador1().longValue()?
							rec.getPatinador2():rec.getPatinador1());
					SpeedTimeTrialEntity patin = (SpeedTimeTrialEntity) 
							repository.findByPatinadorAndCompeticion(
							segundo, rec.getCompeticion());
					if(patin!=null){
						int base = (int)(Math.pow(10.0, eliminatoria.ordinal()+2));
						patin.setClasificacionFinal(base+patin.getMejorTiempo()); // orden temporal
						repository.save(patin);
						//System.out.println("Setting "+segundo+" to "+posicion[rec.getGrupo()]+" grupo "+rec.getGrupo());
						System.out.println("Setting "+segundo+" to "+patin.getClasificacionFinal()+" grupo "+rec.getGrupo());
					}
				}
			}
		}
		
		// Obtiene lista con la clasificacion final
		List<SpeedTimeTrialEntity> output = 
				repository.findByCompeticionAndCategoriaOrderByClasificacionFinalAsc(
				competicion, categoria);
		int orden = 1;
		for(SpeedTimeTrialEntity rec:output){
			rec.setClasificacionFinal(orden++);
			repository.save(rec);
		}
		
		// Aprovechamos y actualizamos aqui los registros ParticipanteEntity
		CompeticionEntity competi = competirepo.findById(competicion);
		if(competi!=null){
			for(SpeedTimeTrialEntity rec:output){
				ParticipanteEntity inscripcion = inscripcionesrepo.findByPatinadorAndCategoriaAndCompeticion(
						rec.getPatinador(), categoria, competicion);
				if(inscripcion!=null){
					inscripcion.setClasificacion(rec.getClasificacionFinal());
					inscripcion.setMejorMarca(rec.getMejorTiempo());
					PuntuacionesEntity puntos = puntosrepo.findByClasificacionAndCompany(
							rec.getClasificacionFinal(), user.getCompany());
					if(puntos!=null){
						switch(competi.getTipo()){
						case A:
							inscripcion.setPuntuacion(puntos.getPuntosCampeonato());
							break;
						case B:
							inscripcion.setPuntuacion(puntos.getPuntosCopa());
							break;
						case C:
							inscripcion.setPuntuacion(puntos.getPuntosTrofeo());
							break;
						}
					}
					inscripcionesrepo.save(inscripcion);
				}
			}
		}
		
		return output;
	}
	
	@RequestMapping("/deleteByCompeticionAndCategoria/{competicion}/{categoria}")
	public boolean deleteByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Deleting Battle By competicion y categoria..."
			+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		korepo.deleteByCompeticionAndCategoria(competicion, categoria);
		repository.deleteByCompeticionAndCategoria(competicion, categoria);
		return true;
	}
	
}