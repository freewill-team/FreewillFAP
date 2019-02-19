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

import freewill.nextgen.blts.daos.ParticipanteRepository;
import freewill.nextgen.blts.daos.PuntuacionesRepository;
import freewill.nextgen.blts.daos.RankingRepository;
import freewill.nextgen.blts.daos.CircuitoRepository;
import freewill.nextgen.blts.daos.CompeticionRepository;
import freewill.nextgen.blts.daos.DerrapesRepository;
import freewill.nextgen.blts.daos.DerrapesRondaRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PuntuacionesEntity;
import freewill.nextgen.blts.data.DerrapesRondaEntity;
import freewill.nextgen.blts.data.DerrapesRondaEntity.EliminatoriaEnum;
import freewill.nextgen.blts.data.CircuitoEntity;
import freewill.nextgen.blts.data.CompeticionEntity;
import freewill.nextgen.blts.data.DerrapesEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   DerrapesManager.java
 * Date:   27/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Derrapes
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/DerrapesEntity")
public class DerrapesManager {
	
	@Autowired
	DerrapesRepository repository;
	
	@Autowired
	ParticipanteRepository inscripcionesrepo;
	
	@Autowired
	DerrapesRondaRepository korepo;
	
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

	@RequestMapping("/create")
	public DerrapesEntity add(@RequestBody DerrapesEntity rec) throws Exception {
		return null;
	}
	
	@RequestMapping("/update")
	public DerrapesEntity update(@RequestBody DerrapesEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Derrapes..."+rec);
			
			DerrapesEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Derrapes..."+recId);
			DerrapesEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<DerrapesEntity> getlist() throws Exception {
		System.out.println("Getting Entire Derrapes List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<DerrapesEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public DerrapesEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Derrapes..."+recId);
		return repository.findById(recId);
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping("/getByCompeticionAndCategoria/{competicion}/{categoria}")
	public List<DerrapesEntity> getByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		
		System.out.println("Getting Derrapess List By competicion y categoria..."
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
		
		List<DerrapesEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(
				competicion, categoria);
		if(recs==null || recs.size()==0){
			if(competi.getActive()==false) 
				return recs; // evita modificar datos introducidos manualmente
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
				DerrapesEntity rec = new DerrapesEntity();
				rec.setApellidos(inscripcion.getApellidos());
				rec.setCategoria(inscripcion.getCategoria());
				rec.setCompeticion(inscripcion.getCompeticion());
				rec.setNombre(inscripcion.getNombre());
				rec.setDorsal(inscripcion.getDorsal());
				
				rec.setOrden(rankingrepo.getSortedRanking(inscripcion.getPatinador(), 
						competi.getCircuito(), categoria, circuitoUltimoAnno.getId()));
				System.out.println("Creating "+rec+" Orden "+rec.getOrden());
				
				rec.setClasificacion(rec.getOrden());
				rec.setPatinador(inscripcion.getPatinador());
				rec.setCompany(user.getCompany());
				repository.save(rec);
			}
			
			// ordena los registros por el ranking absoluto
			orden = 1;
			recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(competicion, categoria);
			for(DerrapesEntity rec:recs){
				rec.setOrden(orden++);
				rec.setClasificacion(rec.getOrden());
				repository.save(rec);
			}
						
			// retrieve and return new created records
			recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(competicion, categoria);
		}
		return recs;
	}
	
	@SuppressWarnings("deprecation")
	private List<DerrapesEntity> mockByCompeticionAndCategoria(Long competicion, Long categoria) {
		// Simula la ordenacion por Ranking, pero no la persiste
		List<DerrapesEntity> recs = new ArrayList<DerrapesEntity>();
		
		CompeticionEntity competi = competirepo.findById(competicion);
		Date ultimoAnno = new Date();
		ultimoAnno.setYear(ultimoAnno.getYear()-1);
		CircuitoEntity circuitoUltimoAnno = circuitorepo.findByTemporada(ultimoAnno.getYear()+1900);
		
		List<ParticipanteEntity> inscripciones = 
				inscripcionesrepo.findByCompeticionAndCategoria(competicion, categoria);
		
		for(ParticipanteEntity inscripcion:inscripciones){
			// Create individual record
			DerrapesEntity rec = new DerrapesEntity();
			rec.setApellidos(inscripcion.getApellidos());
			rec.setCategoria(inscripcion.getCategoria());
			rec.setCompeticion(inscripcion.getCompeticion());
			rec.setNombre(inscripcion.getNombre());
			rec.setDorsal(inscripcion.getDorsal());
			
			rec.setOrden(rankingrepo.getSortedRanking(inscripcion.getPatinador(), 
					competi.getCircuito(), categoria, circuitoUltimoAnno.getId()));
			System.out.println("Mocking "+rec+" Orden "+rec.getOrden());
			
			rec.setClasificacion(rec.getOrden());
			rec.setPatinador(inscripcion.getPatinador());
			//rec.setCompany(user.getCompany());
			recs.add(rec);
		}
		
		// ordena los registros por el ranking absoluto
		Collections.sort(recs, new Comparator<DerrapesEntity>() {
			@Override
			public int compare(DerrapesEntity o1, DerrapesEntity o2) {
				return o2.getOrden()-o1.getOrden();
			}
		});
		int orden = 1;
		for(DerrapesEntity rec:recs){
			rec.setOrden(orden++);
			rec.setId(rec.getOrden());
		}
				
		return recs;
	}
	
	@RequestMapping("/getResultados/{competicion}/{categoria}")
	public List<DerrapesEntity> getResultados(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		int posicion[] = {1, 2, 3, 4};
		//int posSemis[] = {5, 6, 7, 8};
		int posSemis[] = {5, 5, 7, 7};
		//int posCuartos[] = {9, 10, 13, 14, 11, 12, 15, 16};
		int posCuartos[] = {9, 9, 13, 13, 9, 9, 13, 13};
		//int posOctavos[] = {17, 19, 21, 23, 25, 27, 29, 31};
		System.out.println("Getting Derrapess Results By competicion y categoria..."
			+competicion+","+categoria);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		
		// Derrapes ordenar por clasificacion final
		for(EliminatoriaEnum eliminatoria:EliminatoriaEnum.values()){
			List<DerrapesRondaEntity> recs = 
					korepo.findByCompeticionAndCategoriaAndEliminatoria(competicion, categoria, eliminatoria);
			
			if(eliminatoria==EliminatoriaEnum.SEMIS)
				posicion = posSemis;
			else if(eliminatoria==EliminatoriaEnum.CUARTOS)
				posicion = posCuartos;
			/*else if(eliminatoria==EliminatoriaEnum.OCTAVOS)
				posicion = posOctavos;
			else if(eliminatoria==EliminatoriaEnum.DIECISEIS)
				posicion = posDieciseis;*/
			
			for(DerrapesRondaEntity rec:recs){
				if(eliminatoria==EliminatoriaEnum.FINAL){
					// Ganador final - primer clasificado
					DerrapesEntity patin = (DerrapesEntity)
							repository.findByPatinadorAndCompeticion(
							rec.getGanador1(), rec.getCompeticion());
					if(patin!=null){
						patin.setClasificacion(1);
						repository.save(patin);
						System.out.println("Setting "+rec.getGanador1()+" to "+1);
					}
					// segundo clasificado
					patin = (DerrapesEntity) 
							repository.findByPatinadorAndCompeticion(
							rec.getGanador2(), rec.getCompeticion());
					if(patin!=null){
						patin.setClasificacion(2);
						repository.save(patin);
						System.out.println("Setting "+rec.getGanador2()+" to "+2);
					}
					// tercer clasificado
					patin = (DerrapesEntity) 
							repository.findByPatinadorAndCompeticion(
							rec.getGanador3(), rec.getCompeticion());
					if(patin!=null){
						patin.setClasificacion(3);
						repository.save(patin);
						System.out.println("Setting "+rec.getGanador3()+" to "+3);
					}
					// cuarto clasificado
					patin = (DerrapesEntity) 
							repository.findByPatinadorAndCompeticion(
							rec.getGanador4(), rec.getCompeticion());
					if(patin!=null){
						patin.setClasificacion(4);
						repository.save(patin);
						System.out.println("Setting "+rec.getGanador4()+" to "+4);
					}
				}
				//else rec.getEliminatoria()==EliminatoriaEnum.SEMIS){}
				else{
					// Resto de Eliminatorias
					DerrapesEntity patin = (DerrapesEntity) 
							repository.findByPatinadorAndCompeticion(
							rec.getGanador3(), rec.getCompeticion());
					if(patin!=null){
						patin.setClasificacion(posicion[rec.getGrupo()]);
						repository.save(patin);
						System.out.println("Setting "+rec.getGanador3()+" to "+
								posicion[rec.getGrupo()]+" grupo "+rec.getGrupo());
					}
					patin = (DerrapesEntity) 
							repository.findByPatinadorAndCompeticion(
							rec.getGanador4(), rec.getCompeticion());
					if(patin!=null){
						patin.setClasificacion(posicion[rec.getGrupo()]+2);
						repository.save(patin);
						System.out.println("Setting "+rec.getGanador4()+" to "+
								(posicion[rec.getGrupo()]+2)+" grupo "+rec.getGrupo());
					}
				}
			}
		}
		
		// Obtiene lista con la clasificacion final
		List<DerrapesEntity> recs = repository.findByCompeticionAndCategoriaOrderByClasificacionAsc(
				competicion, categoria);
		/* En derrapes el orden no es consecutivo
		int orden = 1;
		for(DerrapesEntity rec:recs){
			rec.setClasificacion(orden++);
			repository.save(rec);
		}*/
		
		// Aprovechamos y actualizamos aqui los registros ParticipanteEntity
		CompeticionEntity competi = competirepo.findById(competicion);
		if(competi!=null){
			for(DerrapesEntity rec:recs){
				ParticipanteEntity inscripcion = inscripcionesrepo.findByPatinadorAndCategoriaAndCompeticion(
						rec.getPatinador(), categoria, competicion);
				if(inscripcion!=null){
					inscripcion.setClasificacion(rec.getClasificacion());
					PuntuacionesEntity puntos = puntosrepo.findByClasificacionAndCompany(
							rec.getClasificacion(), user.getCompany());
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
		
		return recs;
	}
	
	@RequestMapping("/moveRecordUp/{recId}")
	public DerrapesEntity moveRecordUp(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Moving Derrapes Up..."+recId);
			DerrapesEntity rec = repository.findById(recId);
			if(rec!=null){
				if(rec.getOrden()==1) return rec;
				DerrapesEntity prev = repository.findByCompeticionAndCategoriaAndOrden(
						rec.getCompeticion(), rec.getCategoria(), rec.getOrden()-1);
				prev.setOrden(rec.getOrden());
				repository.save(prev);
				rec.setOrden(rec.getOrden()-1);
				return repository.save(rec);
			}
		}
		return null;	
	}
	
	@RequestMapping("/moveRecordDown/{recId}")
	public DerrapesEntity moveRecordDown(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Moving Derrapes Down..."+recId);
			DerrapesEntity rec = repository.findById(recId);
			if(rec!=null){
				List<DerrapesEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(
						rec.getCompeticion(), rec.getCategoria());
				if(rec.getOrden()==recs.size()) return rec;
				DerrapesEntity post = repository.findByCompeticionAndCategoriaAndOrden(
						rec.getCompeticion(), rec.getCategoria(), rec.getOrden()+1);
				post.setOrden(rec.getOrden());
				repository.save(post);
				rec.setOrden(rec.getOrden()+1);
				return repository.save(rec);
			}
		}
		return null;	
	}
	
}