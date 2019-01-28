package freewill.nextgen.blts;

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
import freewill.nextgen.blts.daos.CompeticionRepository;
import freewill.nextgen.blts.daos.ParticipanteRepository;
import freewill.nextgen.blts.daos.PatinadorRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CategoriaEntity;
import freewill.nextgen.blts.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.blts.data.CompeticionEntity;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PatinadorEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   ParticipanteManager.java
 * Date:   06/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Participante
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/ParticipanteEntity")
public class ParticipanteManager {
	
	@Autowired
	ParticipanteRepository repository;
	
	@Autowired
	CompeticionRepository competirepo;
	
	@Autowired
	PatinadorRepository patinrepo;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	CategoriaRepository categoriarepo;

	@RequestMapping("/create")
	public ParticipanteEntity add(@RequestBody ParticipanteEntity rec) throws Exception {
		if(rec!=null){
			if(rec.getPatinador()==null)
				throw new IllegalArgumentException("La inscripcion debe contener un Patinador.");
			if(rec.getCompeticion()==null || rec.getCategoria()==null)
				throw new IllegalArgumentException("Debe rellenar el Campeonato y la Categoria.");
			ParticipanteEntity old = repository.findByPatinadorAndCategoriaAndCompeticion(
					rec.getPatinador(), rec.getCategoria(), rec.getCompeticion());
			if(old!=null){
				rec.setId(old.getId()); // Para evitar duplicados - realmente hará un update
			}
			
			CategoriaEntity cat = categoriarepo.findById(rec.getCategoria());
			if(cat==null)
				throw new IllegalArgumentException("Error: esta Categoria no existe.");
			if(cat.getModalidad()==ModalidadEnum.JAM){
				// revisa campos adicionales para el caso de la Jam
				if(rec.getPatinadorPareja()==null || rec.getPatinadorPareja()==0)
					throw new IllegalArgumentException("Para JAM Debe rellenar obligatoriamente la pareja.");
				if(rec.getPatinadorPareja()==rec.getPatinador())
					throw new IllegalArgumentException("Error: patinador y su pareja no pueden ser el mismo.");
				// Revisa si ya está inscrito como pareja anteriormente
				List<ParticipanteEntity> otros = repository.findByPatinadorParejaAndCompeticion(
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
			System.out.println("Saving Participante..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		// relleno tambien el circuito
    		CompeticionEntity competi = competirepo.findById(rec.getCompeticion());
    		rec.setCircuito(competi.getCircuito());
    		
    		ParticipanteEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public ParticipanteEntity update(@RequestBody ParticipanteEntity rec) throws Exception {
		if(rec!=null){
			// Usar mejor el método "create" si se quiere actualizar un registro
			System.out.println("Updating Participante..."+rec);
			ParticipanteEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Participante..."+recId);
			ParticipanteEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<ParticipanteEntity> getlist() throws Exception {
		System.out.println("Getting Entire Participantes List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ParticipanteEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public ParticipanteEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Participante..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getByPatinadorAndCompeticion/{patinador}/{competicion}")
	public List<ParticipanteEntity> getByPatinadorAndCompeticion(@PathVariable Long patinador,
			@PathVariable Long competicion) throws Exception {
		System.out.println("Getting Participantes List By patinador y competicion..."
			+patinador+","+competicion);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ParticipanteEntity> recs = repository.findByPatinadorAndCompeticion(patinador, competicion);
		List<ParticipanteEntity> recs2 = repository.findByPatinadorParejaAndCompeticion(patinador, competicion);
		recs.addAll(recs2);
		return recs;
	}
	
	/*@RequestMapping("/getByPatinadorAndCompeticionAndCategoria/{patinador}/{competicion}/{categoria}")
	public List<ParticipanteEntity> getByPatinadorAndCompeticionAndCategoria(
			@PathVariable Long patinador, @PathVariable Long competicion, 
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting Participantes List By patinador, competicion y categoria..."
			+patinador+","+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ParticipanteEntity> recs = repository.findByPatinadorAndCompeticionAndCategoria(
				patinador, competicion, categoria);
		return recs;
	}*/
	
	@RequestMapping("/getByPatinadorAndCompeticionAndCategoria/{patinador}/{competicion}/{categoria}")
	public ParticipanteEntity getByPatinadorAndCompeticionAndCategoria(
			@PathVariable Long patinador, @PathVariable Long competicion, 
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting Participantes List By patinador, competicion y categoria..."
			+patinador+","+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		ParticipanteEntity rec = repository.findByPatinadorAndCategoriaAndCompeticion(
				patinador, categoria, competicion);
		return rec;
	}
	
	@RequestMapping("/getByCompeticion/{competicion}")
	public List<ParticipanteEntity> getByCompeticion(
			@PathVariable Long competicion) throws Exception {
		System.out.println("Getting Participantes List By competicion..."+competicion);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ParticipanteEntity> recs = repository.findByCompeticion(competicion);
		return recs;
	}
	
	@RequestMapping("/getByCompeticionAndCategoria/{competicion}/{categoria}")
	public List<ParticipanteEntity> getByCompeticionAndCategoria(
			@PathVariable Long competicion, @PathVariable Long categoria) throws Exception {
		System.out.println("Getting Participantes List By competicion y categoria..."
			+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ParticipanteEntity> recs = repository.findByCompeticionAndCategoria(
				competicion, categoria);
		return recs;
	}
	
	@RequestMapping("/countByCompeticionAndCategoria/{competicion}/{categoria}")
	public ParticipanteEntity countByCompeticionAndCategoria(
			@PathVariable Long competicion, @PathVariable Long categoria) throws Exception {
		System.out.println("Counting Participantes List By competicion y categoria..."
			+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		ParticipanteEntity rec = new ParticipanteEntity();
		try{
			rec.setId(repository.countByCompeticionAndCategoria(competicion, categoria));
		}
		catch(Exception e){
			rec.setId(0L);
		}
		return rec;
	}
	
	@RequestMapping("/getResultados/{competicion}/{categoria}")
	public List<ParticipanteEntity> getResultados(
			@PathVariable Long competicion, @PathVariable Long categoria) throws Exception {
		System.out.println("Getting Participantes Results By competicion y categoria..."
			+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ParticipanteEntity> recs = repository.findByCompeticionAndCategoriaOrderByClasificacion(
				competicion, categoria);
		// Oculta datos de niños que no han firmado RPGD 
		for(ParticipanteEntity rec:recs){
			PatinadorEntity patin = patinrepo.findById(rec.getPatinador());
			if(patin!=null && patin.getActive()==false){
				rec.setApellidos("XXXXXX XXXXXX");
				rec.setNombre("XXXXXX");
			}
		}
		return recs;
	}
	
}