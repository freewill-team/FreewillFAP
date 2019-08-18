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
import freewill.nextgen.blts.daos.JamShowRepository;
import freewill.nextgen.blts.daos.CircuitoRepository;
import freewill.nextgen.blts.daos.CompeticionRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PuntuacionesEntity;
import freewill.nextgen.blts.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.blts.data.JamShowEntity;
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
	RankingAbsRepository rankingrepo;

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
			rec.setTotalJuez1(rec.getArtisticaJuez1() + rec.getTecnicaJuez1() + rec.getSincronizacionJuez1()- rec.getPenalizaciones());
			rec.setTotalJuez2(rec.getArtisticaJuez2() + rec.getTecnicaJuez2() + rec.getSincronizacionJuez2()- rec.getPenalizaciones());
			rec.setTotalJuez3(rec.getArtisticaJuez3() + rec.getTecnicaJuez3() + rec.getSincronizacionJuez3()- rec.getPenalizaciones());
			rec.setTotalTecnica(rec.getTecnicaJuez1() + rec.getTecnicaJuez2() + rec.getTecnicaJuez3());
			rec.setPuntuacionTotal(rec.getTotalJuez1() + rec.getTotalJuez2() + rec.getTotalJuez3());
			
			JamShowEntity res = repository.save(rec);
			
			// Calcular los rankings
			setRankingJuez1(rec.getCompeticion(), rec.getCategoria());
			setRankingJuez2(rec.getCompeticion(), rec.getCategoria());
			setRankingJuez3(rec.getCompeticion(), rec.getCategoria());

			res = repository.findById(res.getId());
			
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
		System.out.println("JamShow List By competicion and categoria..." + competicion + "," + categoria);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		long userCompany = user.getCompany();
		
		CompeticionEntity competi = competirepo.findById(competicion);
		
		// Verifica si la competición puede empezar
		Date now = new Date();
		if(competi.getFechaInicio().after(now)){
			//throw new IllegalArgumentException("Esta Competición aun no puede comenzar.");
			return mockByCompeticionAndCategoria(competicion, categoria);
		}
		
		List<JamShowEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(
				competicion, categoria);
		if(recs==null || recs.size()==0)
		{
			if(competi.getActive()==false) 
				return recs; // evita modificar datos introducidos manualmente
			
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
					rec.setNombre(inscripcion.getNombre());
					rec.setApellidos(inscripcion.getApellidos());
					rec.setPatinadorPareja(inscripcion.getPatinadorPareja());
					rec.setApellidosPareja(inscripcion.getApellidosPareja());
					rec.setNombrePareja(inscripcion.getNombrePareja());
					rec.setCategoria(inscripcion.getCategoria());
					rec.setCompeticion(inscripcion.getCompeticion());
					rec.setDorsal(inscripcion.getDorsal());
					rec.setDorsalPareja(inscripcion.getDorsalPareja());
					rec.setCompany(userCompany);
					
					rec.setOrden1(
							rankingrepo.getSortedRanking(inscripcion.getPatinador(), ModalidadEnum.CLASSIC)+
							rankingrepo.getSortedRanking(inscripcion.getPatinadorPareja(), ModalidadEnum.CLASSIC)
							);
					System.out.println("Creating "+rec+" Orden "+rec.getOrden1());
					
					repository.save(rec);
				}
			}
			
			// Para evitar duplicados en el orden?, ordena los registros por el ranking absoluto 
			orden = 1;
			recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
			for(JamShowEntity rec:recs){
				rec.setOrden1(orden++);
				repository.save(rec);
			}
									
			// retrieve and return new created records
			recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
		}
		return recs;
	}
	
	private List<JamShowEntity> mockByCompeticionAndCategoria(Long competicion, Long categoria) {
		// Simula la ordenacion por Ranking, pero no la persiste
		List<JamShowEntity> recs = new ArrayList<JamShowEntity>();
		
		List<ParticipanteEntity> inscripciones = 
				inscripcionesrepo.findByCompeticionAndCategoria(competicion, categoria);
		
		for(ParticipanteEntity inscripcion:inscripciones){
			// Create individual record
			JamShowEntity rec = new JamShowEntity();
			rec.setPatinador(inscripcion.getPatinador());
			rec.setNombre(inscripcion.getNombre()); // BVM
			rec.setApellidos(inscripcion.getApellidos());
			rec.setPatinadorPareja(inscripcion.getPatinadorPareja());
			rec.setApellidosPareja(inscripcion.getApellidosPareja());
			rec.setNombrePareja(inscripcion.getNombrePareja());
			rec.setCategoria(inscripcion.getCategoria());
			rec.setCompeticion(inscripcion.getCompeticion());
			rec.setDorsal(inscripcion.getDorsal());
			rec.setDorsalPareja(inscripcion.getDorsalPareja());
			rec.setCompany(inscripcion.getCompany());
			
			rec.setOrden1(
					rankingrepo.getSortedRanking(inscripcion.getPatinador(), ModalidadEnum.CLASSIC)+
					rankingrepo.getSortedRanking(inscripcion.getPatinadorPareja(), ModalidadEnum.CLASSIC)
					);
			System.out.println("Creating "+rec+" Orden "+rec.getOrden1());
			
			rec.setClasificacionFinal(rec.getOrden1());
			recs.add(rec);
		}
		
		// ordena los registros por el ranking absoluto
		Collections.sort(recs, new Comparator<JamShowEntity>() {
		    @Override
		    public int compare(JamShowEntity o1, JamShowEntity o2) {
		        return o1.getOrden1()-o2.getOrden1();
		    }
		});
		int orden = 1;
		for(JamShowEntity rec:recs){
			rec.setOrden1(orden++);
			rec.setId(rec.getOrden1());
		}
		
		return recs;
	}
	
	@RequestMapping("/getResultados/{competicion}/{categoria}")
	public List<JamShowEntity> getResultados(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting JamShow Results By competicion and categoria..." 
			+ competicion + "," + categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		//Long userCompany = user.getCompany();
		
		System.out.println("Calculando Clasificacion Jam");
		
		// Paso 1: crear tabla puntos victoria
		List<JamShowEntity> recs = 
				repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
		int numParticipantes = recs.size();
		
		float[][] PV = new float[numParticipantes][numParticipantes];
		int[] RankingI = new int[3];
		int[] TecnicaI = new int[3];
		int[] RankingJ = new int[3];
		int[] TecnicaJ = new int[3];
		
		for (int i = 0; i< numParticipantes; i++)
		{
			JamShowEntity recI = recs.get(i);

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
						JamShowEntity recJ = recs.get(j);
						
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
			JamShowEntity recI = recs.get(i);
			
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
		
		return repository.findByCompeticionAndCategoriaOrderByClasificacionFinalAsc(competicion, categoria);
	}
	
	@RequestMapping("/getResultadosFinal/{competicion}/{categoria}")
	public List<JamShowEntity> getResultadosFinal(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting JamShow Results Final By competicion and categoria..." 
			+ competicion + "," + categoria);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		Long userCompany = user.getCompany();
		
		List<JamShowEntity> recs = 
				repository.findByCompeticionAndCategoriaOrderByClasificacionFinalAsc(competicion, categoria);
		
		// Aprovechamos y actualizamos aqui los registros ParticipanteEntity
		CompeticionEntity competi = competirepo.findById(competicion);
		if(competi!=null && competi.getActive()){
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
		
	@RequestMapping("/moveRecordUp/{recId}")
	public JamShowEntity moveRecordUp(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Moving ClassicShow Up..."+recId);
			JamShowEntity rec = repository.findById(recId);
			if(rec!=null){
				if(rec.getOrden1()==1) return rec;
				JamShowEntity prev = repository.findByCompeticionAndCategoriaAndOrden1(
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
	public JamShowEntity moveRecordDown(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Moving ClassicShow Down..."+recId);
			JamShowEntity rec = repository.findById(recId);
			if(rec!=null){
				List<JamShowEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(
						rec.getCompeticion(), rec.getCategoria());
				if(rec.getOrden1()==recs.size()) return rec;
				JamShowEntity post = repository.findByCompeticionAndCategoriaAndOrden1(
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
		List<JamShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez1Desc(competicion, categoria);
		
		// Solo tengo en cuenta la puntuacion total
		for(JamShowEntity rec:recs)
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
		List<JamShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez2Desc(competicion, categoria);
		
		for(JamShowEntity rec:recs)
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
		List<JamShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByTotalJuez3Desc(competicion, categoria);
		
		for(JamShowEntity rec:recs)
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
		List<JamShowEntity>recs = repository.findByCompeticionAndCategoriaOrderByOrden1Asc(competicion, categoria);
		int numParticipantes = recs.size();

		float currentPV = 0;

		int puntosLocales = 0;
		
		for(int i=0; i< numParticipantes;i++)
		{
			JamShowEntity recI = recs.get(i);
			currentPV = recI.getSumaPV();
			puntosLocales= 0;
			
			if(currentPV !=0)
			{
				for (int j= 0; j<numParticipantes; j++)
				{
					if (i!=j)
					{
						JamShowEntity recJ = recs.get(j);
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
		
		for(JamShowEntity rec:recs)
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
	
	@RequestMapping("/updateJuez1")
	public boolean updateJuez1(@RequestBody JamShowEntity record) throws Exception {
		System.out.println("Entering updateJuez1 " + record);
		JamShowEntity rec = null;
		if(record!=null){
			rec = repository.findById(record.getId());
		}
		if(rec!=null){
			rec.setPenalizaciones(record.getPenalizaciones());
			rec.setArtisticaJuez1(record.getArtisticaJuez1());
			rec.setTecnicaJuez1(record.getTecnicaJuez1());
			rec.setSincronizacionJuez1(record.getSincronizacionJuez1());
			
			rec.setTotalJuez1(rec.getArtisticaJuez1() + rec.getTecnicaJuez1() + rec.getSincronizacionJuez1() - rec.getPenalizaciones());
			rec.setTotalJuez2(rec.getArtisticaJuez2() + rec.getTecnicaJuez2() + rec.getSincronizacionJuez2() - rec.getPenalizaciones());
			rec.setTotalJuez3(rec.getArtisticaJuez3() + rec.getTecnicaJuez3() + rec.getSincronizacionJuez3() - rec.getPenalizaciones());
			rec.setTotalTecnica(rec.getTecnicaJuez1() + rec.getTecnicaJuez2() + rec.getTecnicaJuez3());
			rec.setPuntuacionTotal(rec.getTotalJuez1() + rec.getTotalJuez2() + rec.getTotalJuez3());
			
			JamShowEntity res = repository.save(rec);
			
			// Calcular los rankings
			setRankingJuez1(rec.getCompeticion(), rec.getCategoria());

			res = repository.findById(res.getId());
			
			System.out.println("Updating JamShow Id = " + res.getId() + res.getApellidos() + " " + res.getTotalJuez1());
			return true;
		}
		return false;	
	}
	
	@RequestMapping("/updateJuez2")
	public boolean updateJuez2(@RequestBody JamShowEntity record) throws Exception {
		System.out.println("Entering updateJuez2 " + record);
		JamShowEntity rec = null;
		if(record!=null){
			rec = repository.findById(record.getId());
		}
		if(rec!=null){
			rec.setArtisticaJuez2(record.getArtisticaJuez2());
			rec.setTecnicaJuez2(record.getTecnicaJuez2());
			rec.setSincronizacionJuez2(record.getSincronizacionJuez2());
			
			rec.setTotalJuez2(rec.getArtisticaJuez2() + rec.getTecnicaJuez2() + rec.getSincronizacionJuez2() - rec.getPenalizaciones());
			rec.setTotalTecnica(rec.getTecnicaJuez1() + rec.getTecnicaJuez2() + rec.getTecnicaJuez3());
			rec.setPuntuacionTotal(rec.getTotalJuez1() + rec.getTotalJuez2() + rec.getTotalJuez3());
			
			JamShowEntity res = repository.save(rec);
			
			// Calcular los rankings
			setRankingJuez2(rec.getCompeticion(), rec.getCategoria());

			res = repository.findById(res.getId());
			
			System.out.println("Updating JamShow Id = " + res.getId() + res.getApellidos() + " " + res.getTotalJuez1());
			return true;
		}
		return false;	
	}
	
	@RequestMapping("/updateJuez3")
	public boolean updateJuez3(@RequestBody JamShowEntity record) throws Exception {
		System.out.println("Entering updateJuez3 " + record);
		JamShowEntity rec = null;
		if(record!=null){
			rec = repository.findById(record.getId());
		}
		if(rec!=null){
			rec.setArtisticaJuez3(record.getArtisticaJuez3());
			rec.setTecnicaJuez3(record.getTecnicaJuez3());
			rec.setSincronizacionJuez3(record.getSincronizacionJuez3());
			
			rec.setTotalJuez3(rec.getArtisticaJuez3() + rec.getTecnicaJuez3() + rec.getSincronizacionJuez3()- rec.getPenalizaciones());
			rec.setTotalTecnica(rec.getTecnicaJuez1() + rec.getTecnicaJuez2() + rec.getTecnicaJuez3());
			rec.setPuntuacionTotal(rec.getTotalJuez1() + rec.getTotalJuez2() + rec.getTotalJuez3());
			
			JamShowEntity res = repository.save(rec);
			
			// Calcular los rankings
			setRankingJuez3(rec.getCompeticion(), rec.getCategoria());

			res = repository.findById(res.getId());
			
			System.out.println("Updating JamShow Id = " + res.getId() + res.getApellidos() + " " + res.getTotalJuez1());
			return true;
		}
		return false;
	}
	
	@RequestMapping("/getResultadosRT/{competicion}/{categoria}")
	public List<JamShowEntity> getResultadosRT(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting JamShow Results RT By competicion and categoria..." 
			+ competicion + "," + categoria);
		
		List<JamShowEntity> recs = 
				repository.findByCompeticionAndCategoriaOrderByClasificacionFinalAsc(competicion, categoria);
	
		return recs;	
	}	
	
}