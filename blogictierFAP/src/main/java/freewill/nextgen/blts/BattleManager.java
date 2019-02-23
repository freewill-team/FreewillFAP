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
import freewill.nextgen.blts.daos.RankingAbsRepository;
import freewill.nextgen.blts.daos.CompeticionRepository;
import freewill.nextgen.blts.daos.BattleRepository;
import freewill.nextgen.blts.daos.BattleRondaRepository;
import freewill.nextgen.blts.daos.CircuitoRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PuntuacionesEntity;
import freewill.nextgen.blts.data.BattleRondaEntity;
import freewill.nextgen.blts.data.CircuitoEntity;
import freewill.nextgen.blts.data.BattleRondaEntity.EliminatoriaEnum;
import freewill.nextgen.blts.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.blts.data.CompeticionEntity;
import freewill.nextgen.blts.data.BattleEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   BattleManager.java
 * Date:   05/01/2019
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Battle
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/BattleEntity")
public class BattleManager {
	
	@Autowired
	BattleRepository repository;
	
	@Autowired
	ParticipanteRepository inscripcionesrepo;
	
	@Autowired
	BattleRondaRepository korepo;
	
	@Autowired
	PuntuacionesRepository puntosrepo;
	
	@Autowired
	CompeticionRepository competirepo;
	
	@Autowired
	CircuitoRepository circuitorepo;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	RankingAbsRepository rankingrepo;

	@RequestMapping("/create")
	public BattleEntity add(@RequestBody BattleEntity rec) throws Exception {
		return null;
	}
	
	@RequestMapping("/update")
	public BattleEntity update(@RequestBody BattleEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Battle..."+rec);
			
			BattleEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Battle..."+recId);
			BattleEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<BattleEntity> getlist() throws Exception {
		System.out.println("Getting Entire Battle List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<BattleEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public BattleEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Battle..."+recId);
		return repository.findById(recId);
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping("/getByCompeticionAndCategoria/{competicion}/{categoria}")
	public List<BattleEntity> getByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		
		System.out.println("Getting Battles List By competicion y categoria..."
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
		
		List<BattleEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(
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
				BattleEntity rec = new BattleEntity();
				rec.setApellidos(inscripcion.getApellidos());
				rec.setCategoria(inscripcion.getCategoria());
				rec.setCompeticion(inscripcion.getCompeticion());
				rec.setNombre(inscripcion.getNombre());
				rec.setDorsal(inscripcion.getDorsal());
				
				rec.setOrden(rankingrepo.getSortedRanking(inscripcion.getPatinador(), 
						ModalidadEnum.BATTLE));
				System.out.println("Creating "+rec+" Orden "+rec.getOrden());
				
				rec.setClasificacion(rec.getOrden());
				rec.setPatinador(inscripcion.getPatinador());
				rec.setCompany(user.getCompany());
				repository.save(rec);
			}
			
			// ordena los registros por el ranking absoluto
			orden = 1;
			recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(competicion, categoria);
			for(BattleEntity rec:recs){
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
	private List<BattleEntity> mockByCompeticionAndCategoria(Long competicion, Long categoria) {
		// Simula la ordenacion por Ranking, pero no la persiste
		List<BattleEntity> recs = new ArrayList<BattleEntity>();
		
		CompeticionEntity competi = competirepo.findById(competicion);
		Date ultimoAnno = new Date();
		ultimoAnno.setYear(ultimoAnno.getYear()-1);
		CircuitoEntity circuitoUltimoAnno = circuitorepo.findByTemporada(ultimoAnno.getYear()+1900);
		
		List<ParticipanteEntity> inscripciones = 
				inscripcionesrepo.findByCompeticionAndCategoria(competicion, categoria);
		
		for(ParticipanteEntity inscripcion:inscripciones){
			// Create individual record
			BattleEntity rec = new BattleEntity();
			rec.setApellidos(inscripcion.getApellidos());
			rec.setCategoria(inscripcion.getCategoria());
			rec.setCompeticion(inscripcion.getCompeticion());
			rec.setNombre(inscripcion.getNombre());
			rec.setDorsal(inscripcion.getDorsal());
			
			rec.setOrden(rankingrepo.getSortedRanking(inscripcion.getPatinador(), 
					ModalidadEnum.BATTLE));
			System.out.println("Mocking "+rec+" Orden "+rec.getOrden());
			
			rec.setClasificacion(rec.getOrden());
			rec.setPatinador(inscripcion.getPatinador());
			//rec.setCompany(user.getCompany());
			recs.add(rec);
		}
		
		// ordena los registros por el ranking absoluto
		Collections.sort(recs, new Comparator<BattleEntity>() {
			@Override
			public int compare(BattleEntity o1, BattleEntity o2) {
				return o2.getOrden()-o1.getOrden();
			}
		});
		int orden = 1;
		for(BattleEntity rec:recs){
			rec.setOrden(orden++);
			rec.setId(rec.getOrden());
		}
		
		return recs;
	}
		
	@RequestMapping("/getResultados/{competicion}/{categoria}")
	public List<BattleEntity> getResultados(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		int posicion[] = {1, 2, 3, 4};
		//int posSemis[] = {5, 6, 7, 8};
		int posSemis[] = {5, 5, 7, 7};
		//int posCuartos[] = {9, 10, 13, 14, 11, 12, 15, 16};
		int posCuartos[] = {9, 9, 13, 13, 9, 9, 13, 13};
		//int posOctavos[] = {17, 19, 21, 23, 25, 27, 29, 31};
		System.out.println("Getting Battles Results By competicion y categoria..."
			+competicion+","+categoria);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		
		// Battle ordenar por clasificacion final
		for(EliminatoriaEnum eliminatoria:EliminatoriaEnum.values()){
			List<BattleRondaEntity> recs = 
					korepo.findByCompeticionAndCategoriaAndEliminatoria(competicion, categoria, eliminatoria);
			
			if(eliminatoria==EliminatoriaEnum.SEMIS)
				posicion = posSemis;
			else if(eliminatoria==EliminatoriaEnum.CUARTOS)
				posicion = posCuartos;
			/*else if(eliminatoria==EliminatoriaEnum.OCTAVOS)
				posicion = posOctavos;
			else if(eliminatoria==EliminatoriaEnum.DIECISEIS)
				posicion = posDieciseis;*/
			
			for(BattleRondaEntity rec:recs){
				if(eliminatoria==EliminatoriaEnum.FINAL){
					// Ganador final - primer clasificado
					BattleEntity patin = (BattleEntity)
							repository.findByPatinadorAndCompeticion(
							rec.getGanador1(), rec.getCompeticion());
					if(patin!=null){
						patin.setClasificacion(1);
						repository.save(patin);
						System.out.println("Setting "+rec.getGanador1()+" to "+1);
					}
					// segundo clasificado
					patin = (BattleEntity) 
							repository.findByPatinadorAndCompeticion(
							rec.getGanador2(), rec.getCompeticion());
					if(patin!=null){
						patin.setClasificacion(2);
						repository.save(patin);
						System.out.println("Setting "+rec.getGanador2()+" to "+2);
					}
					// tercer clasificado
					patin = (BattleEntity) 
							repository.findByPatinadorAndCompeticion(
							rec.getGanador3(), rec.getCompeticion());
					if(patin!=null){
						patin.setClasificacion(3);
						repository.save(patin);
						System.out.println("Setting "+rec.getGanador3()+" to "+3);
					}
					// cuarto clasificado
					patin = (BattleEntity) 
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
					BattleEntity patin = (BattleEntity) 
							repository.findByPatinadorAndCompeticion(
							rec.getGanador3(), rec.getCompeticion());
					if(patin!=null){
						patin.setClasificacion(posicion[rec.getGrupo()]);
						repository.save(patin);
						System.out.println("Setting "+rec.getGanador3()+" to "+
								posicion[rec.getGrupo()]+" grupo "+rec.getGrupo());
					}
					patin = (BattleEntity) 
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
		List<BattleEntity> recs = repository.findByCompeticionAndCategoriaOrderByClasificacionAsc(
				competicion, categoria);
		/* En battle el orden no es consecutivo
		int orden = 1;
		for(BattleEntity rec:recs){
			rec.setClasificacion(orden++);
			repository.save(rec);
		}*/
		
		// Aprovechamos y actualizamos aqui los registros ParticipanteEntity
		CompeticionEntity competi = competirepo.findById(competicion);
		if(competi!=null && competi.getActive()){
			for(BattleEntity rec:recs){
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
	public BattleEntity moveRecordUp(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Moving Battle Up..."+recId);
			BattleEntity rec = repository.findById(recId);
			if(rec!=null){
				if(rec.getOrden()==1) return rec;
				BattleEntity prev = repository.findByCompeticionAndCategoriaAndOrden(
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
	public BattleEntity moveRecordDown(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Moving Battle Down..."+recId);
			BattleEntity rec = repository.findById(recId);
			if(rec!=null){
				List<BattleEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(
						rec.getCompeticion(), rec.getCategoria());
				if(rec.getOrden()==recs.size()) return rec;
				BattleEntity post = repository.findByCompeticionAndCategoriaAndOrden(
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