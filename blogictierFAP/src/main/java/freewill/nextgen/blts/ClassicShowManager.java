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
import freewill.nextgen.blts.daos.CircuitoRepository;
import freewill.nextgen.blts.daos.ClassicShowRepository;
import freewill.nextgen.blts.daos.CompeticionRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PuntuacionesEntity;
import freewill.nextgen.blts.data.CircuitoEntity;
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
	CircuitoRepository circuitorepo;
	
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
			
			// Calcular los rankings
			setRankingJuez1(rec.getCompeticion(), rec.getCategoria());
			setRankingJuez2(rec.getCompeticion(), rec.getCategoria());
			setRankingJuez3(rec.getCompeticion(), rec.getCategoria());
			
			res = repository.findById(res.getId());

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
	
	@SuppressWarnings("deprecation")
	@RequestMapping("/getByCompeticionAndCategoria/{competicion}/{categoria}")
	public List<ClassicShowEntity> getByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		long userCompany = user.getCompany();
		
		CompeticionEntity competi = competirepo.findById(competicion);
		Date ultimoAnno = new Date();
		ultimoAnno.setYear(ultimoAnno.getYear()-1);
		CircuitoEntity circuitoUltimoAnno = circuitorepo.findByTemporada(ultimoAnno.getYear()+1900);
		
		// Verifica si la competición puede empezar
		Date now = new Date();
		if(competi.getFechaInicio().after(now))
			throw new IllegalArgumentException("Esta Competición aun no puede comenzar.");
		
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
				// Inicializa valores (just in case)
				inscripcion.setClasificacion(999);
				inscripcion.setPuntuacion(0);
				inscripcion.setMejorMarca(0);
				inscripcionesrepo.save(inscripcion);
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
					
					rec.setOrden1(rankingrepo.getSortedRanking(inscripcion.getPatinador(), 
							competi.getCircuito(), categoria, circuitoUltimoAnno.getId()));
					System.out.println("Creating "+rec+" Orden "+rec.getOrden1());
					
					repository.save(rec);
				}
			}
			
			// Para evitar duplicados en el orden?, ordena los registros por el ranking absoluto 
			orden = 1;
			recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
			for(ClassicShowEntity rec:recs){
				rec.setOrden1(orden++);
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
		
		System.out.println("Calculando Clasificacion Classic");
		
		// Paso 1: crear tabla puntos victoria
		List<ClassicShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
		int numParticipantes = recs.size();
		
		float[][] PV = new float[numParticipantes][numParticipantes];
		int[] RankingI = new int[3];
		int[] TecnicaI = new int[3];
		int[] RankingJ = new int[3];
		int[] TecnicaJ = new int[3];
		
		for (int i = 0; i< numParticipantes; i++)
		{
			ClassicShowEntity recI = recs.get(i);

			System.out.print(recI.getNombre()+ ": ");
			if(recI.getPuntuacionTotal() > 0)
			{
				RankingI[0] = recI.getRankingJuez1();
				RankingI[1] = recI.getRankingJuez2();
				RankingI[2] = recI.getRankingJuez3();
				TecnicaI[0] = recI.getTecnicaJuez1();
				TecnicaI[1] = recI.getTecnicaJuez2();
				TecnicaI[2] = recI.getTecnicaJuez3();
				
				//comparar con todos los otros
				for (int j=0; j< numParticipantes; j++)
				{
					PV[i][j]=0;
					
					if(i!=j)
					{
						ClassicShowEntity recJ = recs.get(j);
						
						RankingJ[0] = recJ.getRankingJuez1();
						RankingJ[1] = recJ.getRankingJuez2();
						RankingJ[2] = recJ.getRankingJuez3();
						TecnicaJ[0] = recJ.getTecnicaJuez1();
						TecnicaJ[1] = recJ.getTecnicaJuez2();
						TecnicaJ[2] = recJ.getTecnicaJuez3();
						
						// Para todos los jueces
						for (int k=0; k<3;k++)
						{
							if(RankingI[k] < RankingJ[k])
							{
								PV[i][j]++;
							}
							else
							{
								if(RankingI[k] == RankingJ[k])
								{
									if(TecnicaI[k] > TecnicaJ[k])
									{
										PV[i][j]++;
									}
									else if (TecnicaI[k] == TecnicaJ[k])
									{
										PV[i][j]+=0.5; //Empate total segun juez k
									}
									
								}
							}
						}
					}
					System.out.print(PV[i][j] + "| ");
				}
				System.out.println();
			}
			else
			{
				//System.out.println("No tiene puntos");
			}
		}
		
		// Paso 2: Calcular Puntos	

		for (int i = 0; i< numParticipantes; i++)
		{
			float sumaPV = 0;
			float sumaEmpates = 0;
			float totalPV = 0;
			ClassicShowEntity recI = recs.get(i);
			
			for (int j=0; j< numParticipantes; j++)
			{
				if(PV[i][j] > 1.5)
				{
					sumaPV++;
				}
				else if (PV[i][j] == 1.5)
				{
					sumaEmpates++;
				}
				
				totalPV+=PV[i][j];
			}
			recI.setSumaPV(sumaPV + sumaEmpates/2);
		    recI.setPVTotal(totalPV);
		    repository.save(recI);
		}
		
		// Ya tengo todos los datos y la tabla para calcular puntos locales
		// Paso 3: Calcular clasificacion final 
		
		CalculaClasificacionFinal(competicion, categoria, PV);
		
		recs = repository.findByCompeticionAndCategoriaOrderByClasificacionFinalAsc(competicion, categoria);

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
		int posicion = 0;
		int contador = 0;
		float lastTotal = 100000; //Mejor participante posible
		float currentTotal = 0;
		
		// Ordeno a los participantes de mejor a peor
		List<ClassicShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez1Desc(competicion, categoria);
		
		// Solo tengo en cuenta la puntuacion total
		for(ClassicShowEntity rec:recs)
		{
			currentTotal = rec.getTotalJuez1();
			if (currentTotal != 0)
			{
				//Si un participante es peor que el anterior, se le asigna una posicion mas alta
				if (lastTotal > currentTotal)
				{
					posicion = contador + 1;
					lastTotal = currentTotal;					
				}
				else
				{
					// Empate
				}
				rec.setRankingJuez1(posicion);
				contador++;
				
				//System.out.println("ranking asignado..."+rec.getNombre() + ":" + posicion + ":" + currentTotal );
			}
			else
			{
				//Si no tiene puntos es peor que el último que tuvo puntos
				//System.out.println("LOS ULTIMOS..."+rec.getNombre() + ":" + posicion + ":" + currentTotal );
				rec.setRankingJuez1(contador+1);
			}
			repository.save(rec);
		}
	}
	private void setRankingJuez2(Long competicion, Long categoria)
	{
		int posicion = 0;
		int contador = 0;
		float lastTotal = 100000; //Mejor participante posible
		float currentTotal = 0;
		
		// Ordeno a los participantes de mejor a peor
		List<ClassicShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez2Desc(competicion, categoria);
		
		for(ClassicShowEntity rec:recs)
		{
			currentTotal = rec.getTotalJuez2();
			if (currentTotal != 0)
			{
				//Si un participante es peor que el anterior, se le asigna una posicion mas alta
				if (lastTotal > currentTotal)
				{
					posicion = contador + 1;
					lastTotal = currentTotal;
				}
				else
				{
					// Empate
				}
				rec.setRankingJuez2(posicion);
				contador++;
			}
			else
			{
				//Si no tiene puntos es peor que el último que tuvo puntos
				//System.out.println("LOS ULTIMOS..."+rec.getNombre() + ":" + posicion + ":" + currentTotal );
				rec.setRankingJuez2(contador+1);
			}
			repository.save(rec);
		}
	}
	private void setRankingJuez3(Long competicion, Long categoria)
	{
		int posicion = 0;
		int contador = 0;
		float lastTotal = 100000; //Mejor participante posible
		float currentTotal = 0;
		
		// Ordeno a los participantes de mejor a peor
		List<ClassicShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez3Desc(competicion, categoria);
		
		for(ClassicShowEntity rec:recs)
		{
			currentTotal = rec.getTotalJuez3();
			if (currentTotal != 0)
			{
				//Si un participante es peor que el anterior, se le asigna una posicion mas alta
				if (lastTotal > currentTotal)
				{
					posicion = contador + 1;
					lastTotal = currentTotal;
				}
				else
				{
					// Empate
				}
				rec.setRankingJuez3(posicion);
				contador++;
			}
			else
			{
				//Si no tiene puntos es peor que el último que tuvo puntos
				//System.out.println("LOS ULTIMOS..."+rec.getNombre() + ":" + posicion + ":" + currentTotal + ":" + currentTecnica);
				rec.setRankingJuez3(contador+1);
			}
			repository.save(rec);
		}
	}
	private void CalculaClasificacionFinal(Long competicion, Long categoria,  float[][]PV)
	{

		//System.out.println("Calculando Puntos Locales");
		
		// Tengo que seguir el orden de la tabla PV[i][j]
		List<ClassicShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
		int numParticipantes = recs.size();

		float currentPV = 0;

		int puntosLocales = 0;
		
		for(int i=0; i< numParticipantes;i++)
		{
			ClassicShowEntity recI = recs.get(i);
			currentPV = recI.getSumaPV();
			puntosLocales= 0;
			
			if(currentPV !=0)
			{
				for (int j= 0; j<numParticipantes; j++)
				{
					if (i!=j)
					{
						ClassicShowEntity recJ = recs.get(j);
						if(currentPV == recJ.getSumaPV())
						{
							puntosLocales += PV[i][j];
						}
					}
				}
				recI.setPVLocales(puntosLocales);
			}
			
			
			// Criterio1 : numero de victorias totales (sumaPV)
			// Criterio2 : numero de victorias locales 
			// Criterio3 : suma puntuaciones tecnicas  (recI.getTotalTecnica)
			// Criterio4 : suma de puntos de la victoria (totalPV)
			// Criterio5 : suma de puntuacion total (recI.getPuntuacionTotal)
			
			recI.setSumaPonderada(  recI.getSumaPV()           * 100000000000F +
					puntosLocales       * 1000000000 + 
					recI.getTotalTecnica() * 1000000 + 
					recI.getPVTotal()         * 1000 +
					recI.getPuntuacionTotal());	
			
			repository.save(recI);
			//System.out.println(recI.getNombre() + " " + recI.getSumaPonderada());
		}
		//System.out.println();
		//System.out.println("Calculando Clasificacion");
		
		//Calcular clasificacion final en funcion de suma ponderada
		int posicion = 0; 
		float lastTotal = Float.MAX_VALUE;
		float currentTotal = 0;
		
		// Ordeno a los participantes de mejor a peor
		recs = repository.findByCompeticionAndCategoriaOrderBySumaPonderadaDesc(competicion, categoria);
		
		for(ClassicShowEntity rec:recs)
		{
			currentTotal = rec.getSumaPonderada();
			
			//Si un participante es peor que el anterior, se le asigna una posicion mas alta
			if (lastTotal > currentTotal)
			{
				posicion = posicion + 1;
				lastTotal = currentTotal;
			}
			else
			{
				// Empate
			}
			rec.setClasificacionFinal(posicion);
			repository.save(rec);
		}
	}
	
	@RequestMapping("/deleteByCompeticionAndCategoria/{competicion}/{categoria}")
	public boolean deleteByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Deleting ClassicShow By competicion y categoria..."
			+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ClassicShowEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(
				competicion, categoria);
		for(ClassicShowEntity rec:recs){
			repository.delete(rec);
		}
		return true;
	}
	@RequestMapping("/rankingValido/{competicion}/{categoria}")
	public boolean rankingValido(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Checking rankings By competicion y categoria..."
			+competicion+","+categoria);

		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ClassicShowEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(
				competicion, categoria);
		for(ClassicShowEntity rec:recs){
			repository.delete(rec);
		}
		return true;
	}
	
}