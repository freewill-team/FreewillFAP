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

import freewill.nextgen.blts.daos.CategoriaRepository;
import freewill.nextgen.blts.daos.ClubRepository;
import freewill.nextgen.blts.daos.CompeticionRepository;
import freewill.nextgen.blts.daos.InscripcionRepository;
import freewill.nextgen.blts.daos.ParticipanteRepository;
import freewill.nextgen.blts.daos.PatinadorRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CategoriaEntity;
import freewill.nextgen.blts.data.ClubEntity;
import freewill.nextgen.blts.data.CompeticionEntity;
import freewill.nextgen.blts.data.InscripcionEntity;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PatinadorEntity;
import freewill.nextgen.blts.data.CategoriaEntity.AccionEnum;
import freewill.nextgen.blts.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.blts.entities.UserEntity;
import freewill.nextgen.common.entities.EmailEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * File:   InscripcionManager.java
 * Date:   04/04/2019
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Inscripcion
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/InscripcionEntity")
public class InscripcionManager {
	
	@Autowired
	InscripcionRepository repository;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	PatinadorRepository patirepo;
	
	@Autowired
	ClubRepository clubrepo;
	
	@Autowired
	CompeticionRepository competirepo;
	
	@Autowired
	ParticipanteRepository participanterepo;
	
	@Autowired
	CategoriaRepository categorepo;

	@RequestMapping("/create")
	public InscripcionEntity add(@RequestBody InscripcionEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Inscripcion..."+rec.toString());
			//rec.setCreated(new Date());
			//rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		if(rec.getCompeticion()==0)
    			throw new IllegalArgumentException("Debe rellenar el campo competicion.");
    		if(rec.getEmail().isEmpty())
    			throw new IllegalArgumentException("Debe rellenar el campo email.");
    		if(rec.getTelefono().isEmpty())
    			throw new IllegalArgumentException("Debe rellenar el campo telefono.");
    		
			InscripcionEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public InscripcionEntity update(@RequestBody InscripcionEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Inscripcion..."+rec);
			if(rec.getCompeticion()==0)
    			throw new IllegalArgumentException("Debe rellenar el campo competicion.");
    		if(rec.getEmail().isEmpty())
    			throw new IllegalArgumentException("Debe rellenar el campo email.");
    		if(rec.getTelefono().isEmpty())
    			throw new IllegalArgumentException("Debe rellenar el campo telefono.");
    		
			InscripcionEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Inscripcion..."+recId);
			InscripcionEntity rec = repository.findById(recId);
			if(rec!=null){
				//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    		//UserEntity user = userrepo.findByLoginname(auth.getName());
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<InscripcionEntity> getlist() throws Exception {
		System.out.println("Getting Entire Inscripcion List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<InscripcionEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public InscripcionEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Inscripcion..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getByClubAndCompeticion/{competicion}")
	public InscripcionEntity getByClubAndCompeticion(@PathVariable Long competicion) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());	
		ClubEntity club = clubrepo.findByCoordinadorAndCompany(user.getName(), user.getCompany());
		if(club==null)
			throw new IllegalArgumentException("Sólo el Coordinador de Club puede registrar Pre-Inscripciones.");
		
		System.out.println("Retrieving Inscripcion by "+club.getId()+" "+competicion);
		InscripcionEntity res = repository.findByClubAndCompeticion(club.getId(), competicion);
		if(res==null){
			res = new InscripcionEntity();
			res.setCompany(club.getCompany());
			res.setClub(club.getId());
			res.setClubStr(club.getNombre());
			res.setCompeticion(competicion);
			res.setCoordinador(user.getName());
			res.setEmail(user.getEmail());
			res.setTelefono(club.getTelefono());
			res.setEnviado(false);
			CompeticionEntity competi = competirepo.findById(competicion);
			if(competi!=null && competi.getActive() 
				&& competi.getFechaFinInscripcion().after(new Date()))
				res = repository.save(res);
		}
		return res;
	}
	
	@RequestMapping("/sendInscripcion/{recId}")
	public InscripcionEntity sendInscripcion(@PathVariable Long recId) throws Exception {
		InscripcionEntity rec = repository.findById(recId);
		if(rec!=null){
			System.out.println("sendInscripcion for "+rec);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UserEntity user = userrepo.findByLoginname(auth.getName());
			// envio definitivo de inscripcion, generar correo resumen
			CompeticionEntity competi = competirepo.findById(rec.getCompeticion());
			if(competi!=null){
				String title = "Inscripción '"+competi.getNombre()+"' confirmada";
				String message = 
					"Estimad@ "+rec.getCoordinador()+",\n\n"+
					"L@s siguientes patinador@s pertenecientes a '"+rec.getClubStr()+"' han sido inscritos en la competición del asunto:\n"+
					"\n";
				List<PatinadorEntity> recs = 
						patirepo.findByCompanyAndClubAndActiveOrderByNombreAsc(user.getCompany(), rec.getClub(), true);
				for(PatinadorEntity pat:recs){
					enrichPatinadorData(pat, rec.getCompeticion());
					String modalidades = "";
					if(pat.getClassic())
						modalidades += "Classic ";
					if(pat.getBattle())
						modalidades += "Battle ";
					if(pat.getJam())
						modalidades += "Jam ";
					if(pat.getDerrapes())
						modalidades += "Derrapes ";
					if(pat.getSpeed())
						modalidades += "Speed ";
					if(pat.getSalto())
						modalidades += "Salto ";
					if(!modalidades.equals(""))
						message += pat.getNombre()+" "+pat.getApellidos()+": "+modalidades+"\n";
				}
				message += "\n\nSaludos cordiales\n";
				if(!rec.getEmail().equals(""))
					RtdbDataService.get().pushEmail(new EmailEntity(
						rec.getEmail(),
						title, message,
			    		0L // user.getCompany() Para que el correo salga aunque esta Company no tenga configurado ningun servidor
			    		));
			}
			// actualiza registro
			rec.setEnviado(true);
			rec.setFechaEnvio(new Date());
			InscripcionEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	// Copia de la funcion en PatinadorManager
	private void enrichPatinadorData(PatinadorEntity rec, Long competicion) {
		// Primero revisa si hay inscripciones generales
		List<ParticipanteEntity> inscripciones = 
				participanterepo.findByPatinadorAndCompeticion(rec.getId(), competicion);
		for(ParticipanteEntity insc:inscripciones){
			if(insc!=null && insc.getCategoria()!=null){
				CategoriaEntity cat = categorepo.findById(insc.getCategoria());
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
				CategoriaEntity cat = categorepo.findById(insc.getCategoria());
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