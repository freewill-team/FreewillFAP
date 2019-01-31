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

import freewill.nextgen.blts.daos.ParticipanteRepository;
import freewill.nextgen.blts.daos.PuntuacionesRepository;
import freewill.nextgen.blts.daos.RankingRepository;
import freewill.nextgen.blts.daos.JamShowRepository;
import freewill.nextgen.blts.daos.CircuitoRepository;
import freewill.nextgen.blts.daos.CompeticionRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PuntuacionesEntity;
import freewill.nextgen.blts.data.JamShowEntity;
import freewill.nextgen.blts.data.CircuitoEntity;
import freewill.nextgen.blts.data.CompeticionEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   JamShowManager.java
 * Date:   09/12/2018
 * Author: Maria Farfan
 * Refs:   None
 * 
 * This static class provides the business logic to manage JamShow
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/JamShowEntity")
public class JamShowManager {
	
	@Autowired
	JamShowRepository repository;
	
	@Autowired
	ParticipanteRepository inscripcionesrepo;
	
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
	public JamShowEntity add(@RequestBody JamShowEntity rec) throws Exception {
		if(rec!=null){
			if(rec.getPatinador()==null)
				throw new IllegalArgumentException("El registro debe contener un Patinador.");
			if(rec.getCompeticion()==null)
				throw new IllegalArgumentException("El registro debe contener un Campeonato.");
			JamShowEntity old = repository.findByPatinadorAndCompeticion(
					rec.getPatinador(), rec.getCompeticion());
			if(old!=null)
				throw new IllegalArgumentException("El registro ya existe.");
			// Injects the new record
			System.out.println("Creating JamShow..." + rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
    		JamShowEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public JamShowEntity update(@RequestBody JamShowEntity rec) throws Exception {
		if(rec!=null){
			rec.setTotalJuez1(rec.getArtisticaJuez1() + rec.getTecnicaJuez1() - rec.getPenalizaciones());
			rec.setTotalJuez2(rec.getArtisticaJuez2() + rec.getTecnicaJuez2() - rec.getPenalizaciones());
			rec.setTotalJuez3(rec.getArtisticaJuez3() + rec.getTecnicaJuez3() - rec.getPenalizaciones());
			
			JamShowEntity res = repository.save(rec);

			System.out.println("Updating JamShow Id = " + res.getId() + res.getApellidos() + " " + res.getTotalJuez1());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting JamShow..."+recId);
			JamShowEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<JamShowEntity> getlist() throws Exception {
		System.out.println("Getting Entire JamShow List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<JamShowEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public JamShowEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving JamShow..."+recId);
		return repository.findById(recId);
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping("/getByCompeticionAndCategoria/{competicion}/{categoria}")
	public List<JamShowEntity> getByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		long userCompany = user.getCompany();
		
		CompeticionEntity competi = competirepo.findById(competicion);
		Date ultimoAnno = new Date();
		ultimoAnno.setYear(ultimoAnno.getYear()-1);
		CircuitoEntity circuitoUltimoAnno = circuitorepo.findByTemporada(ultimoAnno.getYear()+1900);
		
		List<JamShowEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(
				competicion, categoria);
		if(recs==null || recs.size()==0)
		{
			System.out.println("Creating JamShow List By competicion and categoria..." + competicion + "," + categoria);
			
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
				if (inscripcion.getDorsal() !=0){
					// Create individual record
					JamShowEntity rec = new JamShowEntity();
					rec.setPatinador(inscripcion.getPatinador());
					rec.setNombre(inscripcion.getNombre()); // BVM
					rec.setApellidos(inscripcion.getApellidos());
					rec.setPatinadorPareja(inscripcion.getPatinadorPareja()); // BVM
					rec.setApellidosPareja(inscripcion.getApellidosPareja()); // BVM
					rec.setNombrePareja(inscripcion.getNombrePareja()); // BVM
					rec.setCategoria(inscripcion.getCategoria());
					rec.setCompeticion(inscripcion.getCompeticion());
					rec.setDorsal(inscripcion.getDorsal());
					rec.setDorsalPareja(inscripcion.getDorsalPareja()); // BVM
					//rec.setClasificacionFinal(0);
					rec.setCompany(userCompany);
					
					rec.setOrden1(rankingrepo.getSortedRanking(inscripcion.getPatinador(), 
							competi.getCircuito(), categoria, circuitoUltimoAnno.getId()));
					System.out.println("Creating "+rec+" Orden "+rec.getOrden1());
					
					repository.save(rec);
				}
			}
			
			// Para evitar duplicados en el orden?, ordena los registros por el ranking absoluto 
			orden = 1;
			recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
			for(JamShowEntity rec:recs){
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
	public List<JamShowEntity> getResultadosFinal(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting JamShow Results Final By competicion and categoria..." 
			+ competicion + "," + categoria);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		Long userCompany = user.getCompany();
		
		
		// Asignar Ranking de jueces
		
		setRankingJuez1(competicion, categoria);
		setRankingJuez2(competicion, categoria);
		setRankingJuez3(competicion, categoria);
		
		// Paso 2: crear tabla puntos victoria
		List<JamShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
		int numParticipantes = recs.size();
		int[][] PV = new int[numParticipantes][numParticipantes];
		
		JamShowEntity recI, recJ;
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
			//recI.setSumaPV(sumPV);
			//recI.setTotalPV(totalPV);
			//recI.sumaPonderada = sumPV*100000+totalPV + xxx;
			//repository.save(recI)
		}
		
		
		// Calcular clasificacion final segun puntos ponderados
		
		recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
		// Obtiene lista con la clasificacion final	
		int orden = 1;
		for(JamShowEntity rec:recs){
			rec.setClasificacionFinal(orden++);
			repository.save(rec);
		}
			
		// Aprovechamos y actualizamos aqui los registros ParticipanteEntity
		CompeticionEntity competi = competirepo.findById(competicion);
		if(competi!=null){
			for(JamShowEntity rec:recs){
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
	
	
	
	private void setRankingJuez1(Long competicion, Long categoria)
	{
		int nextRanking = 1;
		int currentRanking = 1;
		int lastTotal = 0;
		List<JamShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez1Desc(competicion, categoria);
		
		for(JamShowEntity rec:recs){
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
		List<JamShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez2Desc(competicion, categoria);
		
		for(JamShowEntity rec:recs){
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
		List<JamShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez3Desc(competicion, categoria);
		
		for(JamShowEntity rec:recs){
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
	
	@RequestMapping("/deleteByCompeticionAndCategoria/{competicion}/{categoria}")
	public boolean deleteByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Deleting JamShow By competicion y categoria..."
			+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<JamShowEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(
				competicion, categoria);
		for(JamShowEntity rec:recs){
			repository.delete(rec);
		}
		return true;
	}
	
}