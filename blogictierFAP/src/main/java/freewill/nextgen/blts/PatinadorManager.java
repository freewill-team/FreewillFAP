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
import freewill.nextgen.blts.daos.ClubRepository;
import freewill.nextgen.blts.daos.CompeticionRepository;
import freewill.nextgen.blts.daos.ParticipanteRepository;
import freewill.nextgen.blts.daos.PatinadorRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CategoriaEntity;
import freewill.nextgen.blts.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PatinadorEntity;
import freewill.nextgen.blts.data.PatinadorEntity.GenderEnum;
import freewill.nextgen.blts.entities.UserEntity;
import freewill.nextgen.blts.data.ClubEntity;
import freewill.nextgen.blts.data.CompeticionEntity;

/** 
 * File:   PatinadorManager.java
 * Date:   02/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Patinador
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/PatinadorEntity")
public class PatinadorManager {
	
	@Autowired
	PatinadorRepository repository;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	ParticipanteRepository participanterepo;
	
	@Autowired
	CategoriaRepository categoriarepo;
	
	@Autowired
	ClubRepository clubrepo;
	
	@Autowired
	CategoriaRepository categorepo;
	
	@Autowired
	CompeticionRepository competirepo;

	@RequestMapping("/create")
	public PatinadorEntity add(@RequestBody PatinadorEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Patinador..."+rec.toString());
			rec.setCreated(new Date());
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
    		PatinadorEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public PatinadorEntity update(@RequestBody PatinadorEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Patinador..."+rec);
			rec.setTimestamp(new Date());
			PatinadorEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Patinador..."+recId);
			PatinadorEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<PatinadorEntity> getlist() throws Exception {
		System.out.println("Getting Entire Patinador List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<PatinadorEntity> recs = repository.findByCompanyOrderByNombreAsc(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public PatinadorEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Patinador..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/countActivePatinadores")
	public PatinadorEntity countActiveStudents() throws Exception {
		System.out.println("Getting countActivePatinadores...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		PatinadorEntity rec = new PatinadorEntity();
		rec.setId(repository.countByCompanyAndActive(user.getCompany(), true));
		return rec;
	}
	
	@RequestMapping("/countNoActivePatinadores")
	public PatinadorEntity countNoActiveStudents() throws Exception {
		System.out.println("Getting countNoActivePatinadores...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		PatinadorEntity rec = new PatinadorEntity();
		rec.setId(repository.countByCompanyAndActive(user.getCompany(), false));
		return rec;
	}
	
	@RequestMapping("/getInscripciones/{competicion}/{all}")
	public List<PatinadorEntity> getInscripcionesByPatinadorAndCompeticion(
			@PathVariable Long competicion, @PathVariable boolean all) throws Exception {
		System.out.println("Getting Inscripciones List By competicion..."+competicion);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<PatinadorEntity> output = new ArrayList<PatinadorEntity>();
		List<PatinadorEntity> recs =
				repository.findByCompanyOrderByNombreAsc(user.getCompany());
		for(PatinadorEntity rec:recs){
			enrichPatinadorData(rec, competicion);
			if(all || rec.getSalto() || rec.getSpeed() || rec.getClassic() 
					|| rec.getJam() || rec.getDerrapes() || rec.getBattle())
				output.add(rec);
		}
		return output;
	}
	
	@RequestMapping("/updateDorsal/{patId}/{competicion}/{dorsal}")
	public PatinadorEntity updateDorsal(@PathVariable Long patId, @PathVariable Long competicion, 
			@PathVariable int dorsal) throws Exception {
		System.out.println("Updating updateDorsal..."+patId+","+competicion+","+dorsal);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	//UserEntity user = userrepo.findByLoginname(auth.getName());
		if(dorsal<0 || dorsal>9999)
			throw new IllegalArgumentException("Número de dorsal no válido.");
		
		PatinadorEntity rec = repository.findById(patId);
		if(rec!=null){
			if(dorsal>0){
				List<ParticipanteEntity> recs = null;
				// Antes verifica si el dorsal ya ha sido asignado a otro patinador
				recs = participanterepo.findByDorsalAndCompeticion(dorsal, competicion);
				for(ParticipanteEntity insc:recs){
					System.out.println("Checking Inscripcion "+insc.getNombre()+" "+insc.getPatinador());
					if(insc.getPatinador()!=patId)
						throw new IllegalArgumentException("Este dorsal ya está asignado a otro patinador.");
				}
				recs = participanterepo.findByDorsalParejaAndCompeticion(dorsal, competicion);
				for(ParticipanteEntity insc:recs){
					System.out.println("Checking Inscripcion "+insc.getNombrePareja()+" "+insc.getPatinadorPareja());
					if(insc.getPatinadorPareja()!=patId)
						throw new IllegalArgumentException("Este dorsal ya está asignado a otro patinador.");
				}
			}
			// Entonces Actualiza Dorsal
			int numInscripciones = 0;
			// Revisa primero las Inscripciones normales
			List<ParticipanteEntity> inscripciones = 
					participanterepo.findByPatinadorAndCompeticion(rec.getId(), competicion);
			for(ParticipanteEntity insc:inscripciones){
				insc.setDorsal(dorsal);
				participanterepo.save(insc);
				numInscripciones = inscripciones.size();
			}
			// Y despues las Inscripciones en Jam
			inscripciones = participanterepo.findByPatinadorParejaAndCompeticion(rec.getId(), competicion);
			for(ParticipanteEntity insc:inscripciones){
				insc.setDorsalPareja(dorsal);
				participanterepo.save(insc);
				numInscripciones += inscripciones.size();
			}
			if(numInscripciones==0)
				throw new IllegalArgumentException("No existen inscripciones para este patinador.");
			else
				rec.setDorsal(dorsal);
		}
		else
			throw new IllegalArgumentException("El patinador indicado no existe.");
		return rec;
	}
	
	@RequestMapping("/getInscripcionesbyclub/{competicion}")
	public List<PatinadorEntity> getInscripcionesByClub(
			@PathVariable Long competicion) throws Exception {
		System.out.println("Getting Inscripciones List By competicion..."+competicion);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());	
		ClubEntity club = clubrepo.findByCoordinadorAndCompany(user.getName(), user.getCompany());
		if(club==null)
			throw new IllegalArgumentException("Sólo el Coordinador de Club puede registrar Pre-Inscripciones.");
		
		List<PatinadorEntity> recs = 
				repository.findByCompanyAndClubOrderByNombreAsc(user.getCompany(), club.getId());
		for(PatinadorEntity rec:recs){
			enrichPatinadorData(rec, competicion);
		}
		return recs;
	}
	
	@RequestMapping("/updateInscripcion/{competicion}")
	public boolean updateInscripcion(@RequestBody PatinadorEntity rec,
			@PathVariable Long competicion) throws Exception {
		System.out.println("Updating updateInscripcion para competicion ..."+competicion + ","+ rec);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	//UserEntity user = userrepo.findByLoginname(auth.getName());
		if(rec!=null){
			// Inscripciones ya existentes para este campeonato
			List<ParticipanteEntity> inscripciones = 
					participanterepo.findByPatinadorAndCompeticion(rec.getId(), competicion);
			List<ParticipanteEntity> inscripciones2 = 
					participanterepo.findByPatinadorParejaAndCompeticion(rec.getId(), competicion);
			inscripciones.addAll(inscripciones2);
			
			if(rec.getSpeed())
				updateInscripcion(ModalidadEnum.SPEED, rec.getCatSpeed(), rec, competicion, inscripciones);
			else
				removeInscripcion(ModalidadEnum.SPEED, inscripciones);
			
			if(rec.getBattle())
				updateInscripcion(ModalidadEnum.BATTLE, rec.getCatBattle(), rec, competicion, inscripciones);
			else
				removeInscripcion(ModalidadEnum.BATTLE, inscripciones);
			
			if(rec.getClassic())
				updateInscripcion(ModalidadEnum.CLASSIC, rec.getCatClassic(), rec, competicion, inscripciones);
			else
				removeInscripcion(ModalidadEnum.CLASSIC, inscripciones);
			
			if(rec.getDerrapes())
				updateInscripcion(ModalidadEnum.SLIDE, rec.getCatDerrapes(), rec, competicion, inscripciones);
			else
				removeInscripcion(ModalidadEnum.SLIDE, inscripciones);
			if(rec.getSalto())
				updateInscripcion(ModalidadEnum.JUMP, rec.getCatSalto(), rec, competicion, inscripciones);
			else
				removeInscripcion(ModalidadEnum.JUMP, inscripciones);
			
			if(rec.getJam() && rec.getIdPareja()!=null)
				updateInscripcion(ModalidadEnum.JAM, rec.getCatJam(), rec, competicion, inscripciones);
			else
				removeInscripcion(ModalidadEnum.JAM, inscripciones);
		}
		else
			throw new IllegalArgumentException("El patinador indicado no existe.");
		return true;
	}

	private void removeInscripcion(ModalidadEnum modalidad, List<ParticipanteEntity> inscripciones) {
		for(ParticipanteEntity rec:inscripciones){
			CategoriaEntity categoria = categorepo.findById(rec.getCategoria());
			if(categoria!=null && categoria.getModalidad()==modalidad){
				participanterepo.delete(rec);
				inscripciones.remove(rec);
				System.out.println("Removed ParticipanteEntity..."+rec+" "+modalidad);
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void updateInscripcion(ModalidadEnum modalidad, String categoria, PatinadorEntity rec, 
			Long competicion, List<ParticipanteEntity> inscripciones) throws Exception {
		// TODO usar el idcategoria en lugar del nombre
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	UserEntity user = userrepo.findByLoginname(auth.getName());
    	
		// Primero verifica si ya existe
		for(ParticipanteEntity inscr:inscripciones){
			CategoriaEntity cat = categorepo.findById(inscr.getCategoria());
			if(cat!=null && cat.getModalidad()==modalidad){
				System.out.println("Registro ParticipanteEntity ya existe ..."+inscr+" "+modalidad);
				if(categoria.equals(cat.getNombre()))
					return; // Ya existe y no ha cambiado
				// Actualizar la categoria
				CategoriaEntity newcat = categorepo.findByNombreAndCompany(categoria, user.getCompany());
				if(newcat!=null){
					inscr.setCategoria(newcat.getId());
					saveInscripcionData(inscr, cat);
	    			System.out.println("Updated (manual) ParticipanteEntity..."+inscr+" "+modalidad);
				}
    			return;
			}
		}
		
		// No existe, crear registro nuevo
		ParticipanteEntity inscripcion = new ParticipanteEntity();
    	inscripcion.setPatinador(rec.getId());
    	inscripcion.setNombre(rec.getNombre());
    	inscripcion.setApellidos(rec.getApellidos());
    	inscripcion.setClub(rec.getClub());
    	inscripcion.setClubStr(rec.getClubStr());
    	inscripcion.setCompany(rec.getCompany());
    	inscripcion.setCompeticion(competicion);
    	inscripcion.setPatinadorPareja(rec.getIdPareja());
    	inscripcion.setNombrePareja(rec.getNombrePareja());
    	inscripcion.setApellidosPareja(rec.getApellidosPareja());
    	CompeticionEntity competi = competirepo.findById(competicion);
    	inscripcion.setCircuito(competi.getCircuito());
    	// A partir de la edad del niño inferirá su posible categoria
    	Date now = new Date();
    	int edad = now.getYear() - rec.getFechaNacimiento().getYear();
    	System.out.println("Edad = "+edad);
    	if(categoria!=null && !categoria.isEmpty() && !categoria.equals("")){
    		CategoriaEntity cat = categorepo.findByNombreAndCompany(categoria, user.getCompany());
    		if(cat!=null){
    			inscripcion.setCategoria(cat.getId());
    			saveInscripcionData(inscripcion, cat);
    			System.out.println("Created (manual) ParticipanteEntity..."+inscripcion+" "+modalidad);
    			return;
    		}	
    		throw new IllegalArgumentException("No existe la Categoría indicada para la modalidad "+modalidad);
    	}
    	else{
	    	List<CategoriaEntity> categorias = categorepo.findByModalidadAndCompanyAndActive(
	    			modalidad, user.getCompany(), true);
	    	for(CategoriaEntity cat:categorias){
	    		if(cat.getEdadMinima()<=edad && edad<=cat.getEdadMaxima() &&
	    				(rec.getGenero()==cat.getGenero() || cat.getGenero()==GenderEnum.MIXTO) ){
	    			inscripcion.setCategoria(cat.getId());
	    			saveInscripcionData(inscripcion, cat);
	    			System.out.println("Created (auto) ParticipanteEntity..."+inscripcion+" "+modalidad);
	    			return;
	    		}
	    	}
	    	throw new IllegalArgumentException("No existe una Categoría adecuada para la modalidad "+modalidad);
    	}
	}
	
	private ParticipanteEntity saveInscripcionData(ParticipanteEntity rec, 
			CategoriaEntity cat) throws Exception {
		if(cat.getModalidad()==ModalidadEnum.JAM){
			// revisa campos adicionales para el caso de la Jam
			if(rec.getPatinadorPareja()==null || rec.getPatinadorPareja()==0)
				throw new IllegalArgumentException("Para JAM Debe rellenar obligatoriamente la pareja.");
			if(rec.getPatinadorPareja()==rec.getPatinador())
				throw new IllegalArgumentException("Error: patinador y su pareja no pueden ser el mismo.");
			// Revisa si ya está inscrito como pareja anteriormente
			List<ParticipanteEntity> otros = participanterepo.findByPatinadorParejaAndCompeticion(
				rec.getPatinador(), rec.getCompeticion());
			if(otros!=null & otros.size()>0)
				throw new IllegalArgumentException("El patinador ya está inscrito en otra pareja de JAM.");
		}
		else{
			// Para asegurarnos de que no entra basura
			rec.setPatinadorPareja(null);
			rec.setNombrePareja("");
			rec.setApellidosPareja("");
		}
			
		// Injects the new record
		//System.out.println("Saving Participante..."+rec.toString());
    	ParticipanteEntity res = participanterepo.save(rec);
		//System.out.println("Id = "+res.getId());
		return res;
	}

	@RequestMapping("/getInscripcion/{patId}/{competicion}")
	public PatinadorEntity getInscripcionByPatinadorAndCompeticion(@PathVariable Long patId,
			@PathVariable Long competicion) throws Exception {
		System.out.println("Getting Inscripcion List By patinador and competicion..."+patId+" "+competicion);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		PatinadorEntity rec = repository.findById(patId);
		if(rec==null)
			throw new IllegalArgumentException("El patinador indicado no existe.");		
		enrichPatinadorData(rec, competicion);
		return rec;
	}

	private void enrichPatinadorData(PatinadorEntity rec, Long competicion) {
		// Primero revisa si hay inscripciones generales
		List<ParticipanteEntity> inscripciones = 
				participanterepo.findByPatinadorAndCompeticion(rec.getId(), competicion);
		for(ParticipanteEntity insc:inscripciones){
			if(insc!=null && insc.getCategoria()!=null){
				CategoriaEntity cat = categoriarepo.findById(insc.getCategoria());
				if(cat!=null){
					switch(cat.getModalidad()){
					case SPEED: 
						rec.setSpeed(true);
						rec.setIdCatSpeed(cat.getId());
						rec.setCatSpeed(cat.getNombre());
						break;
					case CLASSIC: 
						rec.setClassic(true);
						rec.setIdCatClassic(cat.getId());
						rec.setCatClassic(cat.getNombre());
						break;
					case BATTLE: 
						rec.setBattle(true);
						rec.setIdCatBattle(cat.getId());
						rec.setCatBattle(cat.getNombre());
						break;
					case JAM: 
						rec.setJam(true);
						rec.setIdCatJam(cat.getId());
						rec.setCatJam(cat.getNombre());
						rec.setNombrePareja(insc.getNombrePareja());
						rec.setApellidosPareja(insc.getApellidosPareja());
						rec.setDorsalPareja(insc.getDorsalPareja());
						rec.setIdPareja(insc.getPatinadorPareja());
						break;
					case SLIDE: 
						rec.setDerrapes(true);
						rec.setIdCatDerrapes(cat.getId());
						rec.setCatDerrapes(cat.getNombre());
						break;
					case JUMP: 
						rec.setSalto(true);
						rec.setIdCatSalto(cat.getId());
						rec.setCatSalto(cat.getNombre());
						break;
					}
				}
				rec.setDorsal(insc.getDorsal());
			}
		}
		// Y despues si hay Inscripciones en Jam
		inscripciones = participanterepo.findByPatinadorParejaAndCompeticion(rec.getId(), competicion);
		for(ParticipanteEntity insc:inscripciones){
			if(insc!=null && insc.getCategoria()!=null){
				CategoriaEntity cat = categoriarepo.findById(insc.getCategoria());
				if(cat!=null && cat.getModalidad()==ModalidadEnum.JAM){
					rec.setJam(true);
					rec.setIdCatJam(cat.getId());
					rec.setCatJam(cat.getNombre());
					rec.setNombrePareja(insc.getNombre());
		            rec.setApellidosPareja(insc.getApellidos());
		            rec.setDorsalPareja(insc.getDorsal());
		            rec.setIdPareja(null); // insc.getPatinador()); // para ocultar boton seleccion pareja
					rec.setDorsal(insc.getDorsalPareja());
				}
			}
		}
	}
	
}