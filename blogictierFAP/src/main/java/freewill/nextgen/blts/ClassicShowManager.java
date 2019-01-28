package freewill.nextgen.blts;

import java.util.List;
import java.util.Random;

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
import freewill.nextgen.blts.daos.ClassicShowRepository;
import freewill.nextgen.blts.daos.CompeticionRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PuntuacionesEntity;
import freewill.nextgen.blts.data.RankingEntity;
import freewill.nextgen.blts.data.ClassicShowEntity;
import freewill.nextgen.blts.data.CompeticionEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   ClassicShowManager.java
 * Date:   09/12/2018
 * Author: Maria Farfan
 * Refs:   None
 * 
 * This static class provides the business logic to manage ClassicShow
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/ClassicShowEntity")
public class ClassicShowManager {
	
	@Autowired
	ClassicShowRepository repository;
	
	@Autowired
	ParticipanteRepository inscripcionesrepo;
	
	@Autowired
	PuntuacionesRepository puntosrepo;
	
	@Autowired
	CompeticionRepository competirepo;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	RankingRepository rankingrepo;

	@RequestMapping("/create")
	public ClassicShowEntity add(@RequestBody ClassicShowEntity rec) throws Exception {
		if(rec!=null){
			if(rec.getPatinador()==null)
				throw new IllegalArgumentException("El registro debe contener un Patinador.");
			if(rec.getCompeticion()==null)
				throw new IllegalArgumentException("El registro debe contener un Campeonato.");
			ClassicShowEntity old = repository.findByPatinadorAndCompeticion(
					rec.getPatinador(), rec.getCompeticion());
			if(old!=null)
				throw new IllegalArgumentException("El registro ya existe.");
			// Injects the new record
			System.out.println("Creating ClassicShow..." + rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
    		ClassicShowEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public ClassicShowEntity update(@RequestBody ClassicShowEntity rec) throws Exception {
		if(rec!=null){
			//System.out.println("Updating ClassicShow..."+rec);
			rec.setTotalJuez1(rec.getArtisticaJuez1() + rec.getTecnicaJuez1() - rec.getPenalizaciones());
			rec.setTotalJuez2(rec.getArtisticaJuez2() + rec.getTecnicaJuez2() - rec.getPenalizaciones());
			rec.setTotalJuez3(rec.getArtisticaJuez3() + rec.getTecnicaJuez3() - rec.getPenalizaciones());
			rec.setTotalTecnica(rec.getTecnicaJuez1() + rec.getTecnicaJuez2() + rec.getTecnicaJuez3());
			rec.setPuntuacionTotal(rec.getTotalJuez1() + rec.getTotalJuez2() + rec.getTotalJuez3() );
			
			ClassicShowEntity res = repository.save(rec);

			System.out.println("Updating ClassicShow Id = " + res.getId() + res.getApellidos() + " " + res.getTotalJuez1());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting ClassicShow..."+recId);
			ClassicShowEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<ClassicShowEntity> getlist() throws Exception {
		System.out.println("Getting Entire ClassicShow List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ClassicShowEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public ClassicShowEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving ClassicShow..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getByCompeticionAndCategoria/{competicion}/{categoria}")
	public List<ClassicShowEntity> getByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		
		Random numRandom = new Random();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		long userCompany = user.getCompany();
		
		CompeticionEntity competi = competirepo.findById(competicion);
		
		List<ClassicShowEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(
				competicion, categoria);
		if(recs==null || recs.size()==0)
		{
			System.out.println("Creating ClassicShow List By competicion and categoria..." + competicion + "," + categoria);
			
			// Needs to create records
			List<ParticipanteEntity> inscripciones = 
					inscripcionesrepo.findByCompeticionAndCategoria(competicion, categoria);
			int orden = 1;
			for(ParticipanteEntity inscripcion:inscripciones){
				if (inscripcion.getDorsal() !=0){
					// Create individual record
					ClassicShowEntity rec = new ClassicShowEntity();
					rec.setPatinador(inscripcion.getPatinador());
					rec.setNombre(inscripcion.getNombre());
					rec.setApellidos(inscripcion.getApellidos());
					rec.setCategoria(inscripcion.getCategoria());
					rec.setCompeticion(inscripcion.getCompeticion());
					rec.setDorsal(inscripcion.getDorsal());
					//rec.setClasificacionFinal(0);
					rec.setCompany(userCompany);
					
					RankingEntity ranking = rankingrepo.findByPatinadorAndCircuitoAndCategoria(
							inscripcion.getPatinador(), competi.getCircuito(), categoria);
					if(ranking!=null)
						rec.setOrden1(10000 - ranking.getPuntuacion()); // Fija orden temporal
					else
						rec.setOrden1(20000 - numRandom.nextInt(500)); // Fija orden temporal random
					
					
					repository.save(rec);
				}
			}
			
			// Para evitar duplicados en el orden?, ordena los registros por el ranking absoluto 
			orden = 1;
			recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
			for(ClassicShowEntity rec:recs){
				rec.setOrden1(orden++);
				//rec.setClasificacionFinal(rec.getOrden1());
				repository.save(rec);
			}
									
			// retrieve and return new created records
			recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
		}
		return recs;
	}
	
	
	
	@RequestMapping("/getResultadosFinal/{competicion}/{categoria}")
	public List<ClassicShowEntity> getResultadosFinal(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting ClassicShow Results Final By competicion and categoria..." 
			+ competicion + "," + categoria);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		Long userCompany = user.getCompany();
		
		
		// Asignar Ranking de jueces
		
		setRankingJuez1(competicion, categoria);
		setRankingJuez2(competicion, categoria);
		setRankingJuez3(competicion, categoria);
		
		// Paso 2: crear tabla puntos victoria
		List<ClassicShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
		int numParticipantes = recs.size();
		int[][] PV = new int[numParticipantes][numParticipantes];
		
		ClassicShowEntity recI, recJ;
		for (int i = 0; i< numParticipantes; i++)
		{
			int sumaPV = 0;
			int totalPV = 0;
			recI = recs.get(i);
			//comparar con todos
			for (int j=0; j< numParticipantes; j++)
			{
				PV[i][j]=0;
				
				recJ = recs.get(j);
				if(recI.getRankingJuez1() > recJ.getRankingJuez1())
					PV[i][j] ++;
				if(recI.getRankingJuez2() > recJ.getRankingJuez2())
					PV[i][j] ++;
				if(recI.getRankingJuez3() > recJ.getRankingJuez3())
					PV[i][j] ++;
				
				// si la mayoria de jueces estan de acuerdo, tenemos un punto de victoria para el patinador i
				if (PV[i][j]>=2)
					sumaPV++;
				// sumo todos los puntos por si hay que desempatar	
				totalPV += PV[i][j];
			}
			//Guarda sumas de puntos del patinador
			recI.setSumaPV(sumaPV);
			recI.setPVTotal(totalPV);
			// Criterio1 : numero de victorias totales (sumaPV)
			// Criterio2 : numero de victorias locales ??? ni idea
			// Criterio3 : suma puntuaciones tecnicas  (recI.getTotalTecnica)
			// Criterio4 : suma de puntos de la victoria (totalPV)
			// Criterio5 : suma de puntuacion total (recI.getPuntuacionTotal)
			recI.setSumaPonderada(  sumaPV            * 100000000000F +
									0                   * 1000000000 + 
									recI.getTotalTecnica() * 1000000 + 
									recI.getPVTotal()         * 1000 +
									recI.getPuntuacionTotal());
			repository.save(recI);
		}
		
		
		// Calcular clasificacion final segun puntos ponderados. 
		// OJO: si hay empate debe quedar empate TODO: copiar de setRanking
		
		recs = repository.findByCompeticionAndCategoriaOrderBySumaPonderadaAsc(competicion, categoria);
		// Obtiene lista con la clasificacion final	
		int orden = 1;
		for(ClassicShowEntity rec:recs){
			rec.setClasificacionFinal(orden++);
			repository.save(rec);
		}
			
		// Aprovechamos y actualizamos aqui los registros ParticipanteEntity
		CompeticionEntity competi = competirepo.findById(competicion);
		if(competi!=null){
			for(ClassicShowEntity rec:recs){
				ParticipanteEntity inscripcion = inscripcionesrepo.findByPatinadorAndCategoriaAndCompeticion(
						rec.getPatinador(), categoria, competicion);
				if(inscripcion!=null){
					inscripcion.setClasificacion(rec.getClasificacionFinal());
					PuntuacionesEntity puntos = puntosrepo.findByClasificacionAndCompany(
							rec.getClasificacionFinal(), userCompany);	
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
	public ClassicShowEntity moveRecordUp(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Moving ClassicShow Up..."+recId);
			ClassicShowEntity rec = repository.findById(recId);
			if(rec!=null){
				if(rec.getOrden1()==1) return rec;
				ClassicShowEntity prev = repository.findByCompeticionAndCategoriaAndOrden1(
						rec.getCompeticion(), rec.getCategoria(), rec.getOrden1()-1);
				prev.setOrden1(rec.getOrden1());
				repository.save(prev);
				rec.setOrden1(rec.getOrden1()-1);
				return repository.save(rec);
			}
		}
		return null;	
	}
	
	@RequestMapping("/moveRecordDown/{recId}")
	public ClassicShowEntity moveRecordDown(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Moving ClassicShow Down..."+recId);
			ClassicShowEntity rec = repository.findById(recId);
			if(rec!=null){
				List<ClassicShowEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(
						rec.getCompeticion(), rec.getCategoria());
				if(rec.getOrden1()==recs.size()) return rec;
				ClassicShowEntity post = repository.findByCompeticionAndCategoriaAndOrden1(
						rec.getCompeticion(), rec.getCategoria(), rec.getOrden1()+1);
				post.setOrden1(rec.getOrden1());
				repository.save(post);
				rec.setOrden1(rec.getOrden1()+1);
				return repository.save(rec);
			}
		}
		return null;	
	}
	
	private void setRankingJuez1(Long competicion, Long categoria)
	{
		int nextRanking = 1;
		int currentRanking = 1;
		int lastTotal = 0;
		List<ClassicShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez1Desc(competicion, categoria);
		
		for(ClassicShowEntity rec:recs){
			if (lastTotal == rec.getTotalJuez1())
			{
				rec.setRankingJuez1(currentRanking);
			}
			else
			{
				rec.setRankingJuez1(nextRanking);
				currentRanking=nextRanking;
			}
			repository.save(rec);
			nextRanking++;
		}
	}
	private void setRankingJuez2(Long competicion, Long categoria)
	{
		int nextRanking = 1;
		int currentRanking = 1;
		int lastTotal = 0;
		List<ClassicShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez2Desc(competicion, categoria);
		
		for(ClassicShowEntity rec:recs){
			if (lastTotal == rec.getTotalJuez2())
			{
				rec.setRankingJuez2(currentRanking);
			}
			else
			{
				rec.setRankingJuez2(nextRanking);
				currentRanking=nextRanking;
			}
			repository.save(rec);
			nextRanking++;
		}
	}
	private void setRankingJuez3(Long competicion, Long categoria)
	{
		int nextRanking = 1;
		int currentRanking = 1;
		int lastTotal = 0;
		List<ClassicShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez3Desc(competicion, categoria);
		
		for(ClassicShowEntity rec:recs){
			if (lastTotal == rec.getTotalJuez3())
			{
				rec.setRankingJuez3(currentRanking);
			}
			else
			{
				rec.setRankingJuez3(nextRanking);
				currentRanking=nextRanking;
			}
			repository.save(rec);
			nextRanking++;
		}
	}
	
}