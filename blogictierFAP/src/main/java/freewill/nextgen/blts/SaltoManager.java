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
import freewill.nextgen.blts.daos.ParticipanteRepository;
import freewill.nextgen.blts.daos.PuntuacionesRepository;
import freewill.nextgen.blts.daos.RankingRepository;
import freewill.nextgen.blts.daos.SaltoIntentoRepository;
import freewill.nextgen.blts.daos.SaltoRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CircuitoEntity;
import freewill.nextgen.blts.data.CompeticionEntity;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PuntuacionesEntity;
import freewill.nextgen.blts.data.SaltoEntity;
import freewill.nextgen.blts.data.SaltoIntentoEntity;
import freewill.nextgen.blts.data.SaltoIntentoEntity.ResultEnum;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   SaltoManager.java
 * Date:   22/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage SaltoEntity
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/SaltoEntity")
public class SaltoManager {
	
	@Autowired
	SaltoRepository repository;
	
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
	SaltoIntentoRepository saltosrepo;
	
	@Autowired
	RankingRepository rankingrepo;

	@RequestMapping("/create")
	public SaltoEntity add(@RequestBody SaltoEntity rec) throws Exception {
		if(rec!=null){
			if(rec.getPatinador()==null)
				throw new IllegalArgumentException("El registro debe contener un Patinador.");
			if(rec.getCompeticion()==null)
				throw new IllegalArgumentException("El registro debe contener un Campeonato.");
			List<SaltoEntity> old = repository.findByPatinadorAndCompeticion(
					rec.getPatinador(), rec.getCompeticion());
			if(old!=null && old.size()>0)
				throw new IllegalArgumentException("El registro ya existe.");
			// Injects the new record
			System.out.println("Saving Salto..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
    		SaltoEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public SaltoEntity update(@RequestBody SaltoEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Salto..."+rec);
			
			// actualizar SaltoIntento a partir de datos Transient
			SaltoIntentoEntity salto = saltosrepo.findByRondaAndSaltoPatinadorId(
					rec.getRonda(), rec.getId());
			if(salto!=null){
				salto.setSalto1(rec.getSalto1());
				salto.setSalto2(rec.getSalto2());
				salto.setSalto3(rec.getSalto3());
				saltosrepo.save(salto);
			}
			
			// actualizar resultados y estadisticas
			SaltoEntity res = repository.findById(rec.getId());
			int alturaPrimerFallo = -1;
			int mejorSalto = 0;
			int numeroFallos = 0;
			int numeroOKs = 0;
			int numeroSaltos = 0;
			for(SaltoIntentoEntity salto2:res.getIntentos()){
				switch(salto2.getSalto1()){
					case PENDIENTE:
						break;
					case PASA:
						break;
					case OK: 
						mejorSalto = salto2.getAltura();
						numeroOKs++;
						numeroSaltos++;
						break;
					case FALLO:
						if(alturaPrimerFallo==-1) 
							alturaPrimerFallo = salto2.getAltura();
						numeroFallos++;
						numeroSaltos++;
						break;
				}
				switch(salto2.getSalto2()){
				case PENDIENTE:
					break;
				case PASA:
					break;
				case OK: 
					mejorSalto = salto2.getAltura();
					numeroOKs++;
					numeroSaltos++;
					break;
				case FALLO:
					if(alturaPrimerFallo==-1) 
						alturaPrimerFallo = salto2.getAltura();
					numeroFallos++;
					numeroSaltos++;
					break;
				}
				switch(salto2.getSalto3()){
				case PENDIENTE:
					break;
				case PASA:
					break;
				case OK: 
					mejorSalto = salto2.getAltura();
					numeroOKs++;
					numeroSaltos++;
					break;
				case FALLO:
					if(alturaPrimerFallo==-1) 
						alturaPrimerFallo = salto2.getAltura();
					numeroFallos++;
					numeroSaltos++;
					break;
				}
			}
			res.setAlturaPrimerFallo(alturaPrimerFallo);
			res.setMejorSalto(mejorSalto);
			res.setNumeroFallos(numeroFallos);
			res.setNumeroOKs(numeroOKs);
			res.setNumeroSaltos(numeroSaltos);
			
			res = repository.save(res);
			System.out.println("Id = "+res.getId());
			return rec;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Salto..."+recId);
			SaltoEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<SaltoEntity> getlist() throws Exception {
		System.out.println("Getting Entire Saltos List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<SaltoEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public SaltoEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Salto..."+recId);
		return repository.findById(recId);
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping("/getByCompeticionAndCategoria/{competicion}/{categoria}")
	public List<SaltoEntity> getByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		
		System.out.println("Getting Saltos List By competicion y categoria..."
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
		
		List<SaltoEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(
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
				SaltoEntity rec = new SaltoEntity();
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
				rec.setIntentos(new ArrayList<SaltoIntentoEntity>());
				repository.save(rec);
			}
			
			// ordena los registros por el ranking absoluto
			orden = 1;
			recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(competicion, categoria);
			for(SaltoEntity rec:recs){
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
	private List<SaltoEntity> mockByCompeticionAndCategoria(Long competicion, Long categoria) {
		// Simula la ordenacion por Ranking, pero no la persiste
		List<SaltoEntity> recs = new ArrayList<SaltoEntity>();
		
		CompeticionEntity competi = competirepo.findById(competicion);
		Date ultimoAnno = new Date();
		ultimoAnno.setYear(ultimoAnno.getYear()-1);
		CircuitoEntity circuitoUltimoAnno = circuitorepo.findByTemporada(ultimoAnno.getYear()+1900);
		
		List<ParticipanteEntity> inscripciones = 
				inscripcionesrepo.findByCompeticionAndCategoria(competicion, categoria);
		
		for(ParticipanteEntity inscripcion:inscripciones){
			// Create individual record
			SaltoEntity rec = new SaltoEntity();
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
			rec.setCompany(inscripcion.getCompany());
			recs.add(rec);
		}
		
		// ordena los registros por el ranking absoluto
		Collections.sort(recs, new Comparator<SaltoEntity>() {
			@Override
			public int compare(SaltoEntity o1, SaltoEntity o2) {
				return o2.getOrden()-o1.getOrden();
			}
		});
		int orden = 1;
		for(SaltoEntity rec:recs){
			rec.setOrden(orden++);
			rec.setId(rec.getOrden());
		}
		
		return recs;
	}
	
	@RequestMapping("/getByPatinadorAndCompeticion/{patinador}/{competicion}")
	public List<SaltoEntity> getByPatinadorAndCompeticion(@PathVariable Long patinador,
			@PathVariable Long competicion) throws Exception {
		System.out.println("Getting Saltos List By patinador y competicion..."
			+patinador+","+competicion);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<SaltoEntity> recs = repository.findByPatinadorAndCompeticion(patinador, competicion);
		return recs;
	}
	
	@RequestMapping("/getByCompeticionAndCategoriaAndRonda/{competicion}/{categoria}/{ronda}")
	public List<SaltoEntity> getByCompeticionAndCategoriaAndRonda(@PathVariable Long competicion,
			@PathVariable Long categoria, @PathVariable int ronda) throws Exception {
		System.out.println("Getting Saltos List By competicion, categoria y ronda..."
			+competicion+","+categoria+","+ronda);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<SaltoEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(
				competicion, categoria);
		List<SaltoEntity> output = new ArrayList<SaltoEntity>();
		for(SaltoEntity rec:recs){
			SaltoIntentoEntity salto = saltosrepo.findByRondaAndSaltoPatinadorId(ronda, rec.getId());
			if(salto!=null){
				rec.setRonda(salto.getRonda());
				rec.setAltura(salto.getAltura());
				rec.setSalto1(salto.getSalto1());
				rec.setSalto2(salto.getSalto2());
				rec.setSalto3(salto.getSalto3());
				output.add(rec);
			}
		}
		return output;
	}
	
	@RequestMapping("/getResultados/{competicion}/{categoria}")
	public List<SaltoEntity> getResultados(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting Saltos Results By competicion y categoria..."
			+competicion+","+categoria);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		
		List<SaltoEntity> saltos = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(
				competicion, categoria);
		for(SaltoEntity res:saltos){
			List<SaltoEntity> recs = repository.findByCompeticionAndCategoriaOrderByClasificacionAsc(
					competicion, categoria);
			
			for(int i=1; i<res.getClasificacion()+1; i++){
				System.out.println("Rec "+i+" = "+recs.get(i-1).getClasificacion()
						+" "+recs.get(i-1).getMejorSalto()+" vs "+res.getMejorSalto());
				if(res.getMejorSalto()>recs.get(i-1).getMejorSalto()){
					changeOrdenClasificacion(res, recs, i);
					break;
				}
				else if(res.getMejorSalto()==recs.get(i-1).getMejorSalto()){
					// aplicar criterios desempate salto
					if(res.getNumeroSaltos()<recs.get(i-1).getNumeroSaltos()){
						changeOrdenClasificacion(res, recs, i);
						break;
					}
					else if(res.getNumeroSaltos()==recs.get(i-1).getNumeroSaltos()){
						if(res.getNumeroFallos()<recs.get(i-1).getNumeroFallos()){
							changeOrdenClasificacion(res, recs, i);
							break;
						}
						else if(res.getNumeroFallos()==recs.get(i-1).getNumeroFallos()){
							if(res.getAlturaPrimerFallo()>recs.get(i-1).getAlturaPrimerFallo()){
								changeOrdenClasificacion(res, recs, i);
								break;
							}
						}
					}
				}
			}
			repository.save(recs);
		}
		
		// Obtiene lista con la clasificacion final
		List<SaltoEntity> recs = repository.findByCompeticionAndCategoriaOrderByClasificacionAsc(
				competicion, categoria);
		
		// Aprovechamos y actualizamos aqui los registros ParticipanteEntity
		CompeticionEntity competi = competirepo.findById(competicion);
		if(competi!=null){
			for(SaltoEntity rec:recs){
				ParticipanteEntity inscripcion = inscripcionesrepo.findByPatinadorAndCategoriaAndCompeticion(
						rec.getPatinador(), categoria, competicion);
				if(inscripcion!=null){
					inscripcion.setClasificacion(rec.getClasificacion());
					inscripcion.setMejorMarca(rec.getMejorSalto());
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

	private void changeOrdenClasificacion(SaltoEntity res, List<SaltoEntity> recs, int i) {
		int last = res.getClasificacion();
		res.setClasificacion(recs.get(i-1).getClasificacion());
		int orden = res.getClasificacion()+1;
		System.out.println("Setting order Res="+res.getClasificacion());
		repository.save(res);
		for(int j=i; j<last; j++){
			recs.get(j-1).setClasificacion(orden++);	
			System.out.println("Setting Rec "+(j-1)+"="+recs.get(j-1).getClasificacion());
			repository.save(recs.get(j-1));
		}
	}
	
	@RequestMapping("/createByCompeticionAndCategoriaAndRonda/{competicion}/{categoria}/{ronda}/{altura}")
	public boolean createByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria, @PathVariable int ronda, @PathVariable int altura) throws Exception {
		System.out.println("Creating Saltos List By competicion, categoria, ronda y altura..."
			+competicion+","+categoria+","+ronda+","+altura);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<SaltoEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(
				competicion, categoria);
		int numRecords = 0;
		for(SaltoEntity rec:recs){
			if(ronda==1){
				SaltoIntentoEntity salto = new SaltoIntentoEntity();
				salto.setRonda(ronda);
				salto.setAltura(altura);
				salto.setCompany(user.getCompany());
				rec.getIntentos().add(salto);
				repository.save(rec);
				numRecords++;
			}
			else {
				SaltoIntentoEntity salto_1 = saltosrepo.findByRondaAndSaltoPatinadorId(ronda-1, rec.getId());
				if(salto_1!=null && (
						salto_1.getSalto1()==ResultEnum.PASA || salto_1.getSalto1()==ResultEnum.OK ||
						salto_1.getSalto2()==ResultEnum.PASA || salto_1.getSalto2()==ResultEnum.OK ||
						salto_1.getSalto3()==ResultEnum.PASA || salto_1.getSalto3()==ResultEnum.OK
						)){
					SaltoIntentoEntity salto = new SaltoIntentoEntity();
					salto.setRonda(ronda);
					salto.setAltura(altura);
					salto.setCompany(user.getCompany());
					rec.getIntentos().add(salto);
					repository.save(rec);
					numRecords++;
				}
			}
		}
		if(numRecords>0)
			return true;
		else
			return false;
	}
	
	@RequestMapping("/checkRondaByCompeticionAndCategoria/{competicion}/{categoria}/{ronda}")
	public SaltoEntity checkRondaByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria, @PathVariable int ronda) throws Exception {
		System.out.println("checkRonda By competicion, categoria y ronda..."
			+competicion+","+categoria+","+ronda);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<SaltoEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(
				competicion, categoria);
		SaltoEntity output = new SaltoEntity();
		for(SaltoEntity rec:recs){
			SaltoIntentoEntity salto = saltosrepo.findByRondaAndSaltoPatinadorId(ronda, rec.getId());
			if(salto!=null){
				output.setAltura(salto.getAltura());
				return output;
			}
		}
		return output;
	}
	
	@RequestMapping("/deleteByCompeticionAndCategoria/{competicion}/{categoria}")
	public boolean deleteByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Deleting Saltos By competicion y categoria..."
			+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<SaltoEntity> recs = repository.findByCompeticionAndCategoriaOrderByOrdenAsc(
				competicion, categoria);
		for(SaltoEntity rec:recs){
			repository.delete(rec);
		}
		// Nótese que SaltosIntentosEntity se borrarán al mismo tiempo 
		return true;
	}
	
}