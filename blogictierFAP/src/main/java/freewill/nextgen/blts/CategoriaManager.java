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
import freewill.nextgen.blts.daos.ParticipanteRepository;
import freewill.nextgen.blts.daos.PatinadorRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CategoriaEntity;
import freewill.nextgen.blts.data.ParticipanteEntity;
import freewill.nextgen.blts.data.PatinadorEntity;
import freewill.nextgen.blts.data.PatinadorEntity.GenderEnum;
import freewill.nextgen.blts.data.CategoriaEntity.AccionEnum;
import freewill.nextgen.blts.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   CategoriaManager.java
 * Date:   05/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Categoria
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/CategoriaEntity")
public class CategoriaManager {
	
	@Autowired
	CategoriaRepository repository;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	ParticipanteRepository inscripcionesrepo;
	
	@Autowired
	PatinadorRepository patinrepo;

	@RequestMapping("/create")
	public CategoriaEntity add(@RequestBody CategoriaEntity rec) throws Exception {
		if(rec!=null){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    	UserEntity user = userrepo.findByLoginname(auth.getName());
			CategoriaEntity old = repository.findByNombreAndCompany(rec.getNombre(), user.getCompany());
			if(old!=null)
				throw new IllegalArgumentException("Este registro ya existe. Cambie el nombre.");
			// Injects the new record
			System.out.println("Saving Categoria..."+rec.toString());
    		rec.setCompany(user.getCompany());
    		
			CategoriaEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public CategoriaEntity update(@RequestBody CategoriaEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Categoria..."+rec);
			CategoriaEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Categoria..."+recId);
			CategoriaEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<CategoriaEntity> getlist() throws Exception {
		System.out.println("Getting Entire Categoria List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<CategoriaEntity> recs = repository.findByCompanyOrderByEdadMinimaAsc(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public CategoriaEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Categoria..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getByModalidad/{modalidad}")
	public List<CategoriaEntity> getByModalidad(@PathVariable ModalidadEnum modalidad) throws Exception {
		System.out.println("Getting Categoria List By Modalidad..."+modalidad);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<CategoriaEntity> recs = repository.findByModalidadAndCompany(modalidad, user.getCompany());
		return recs;
	}
	
	@RequestMapping("/getByCompeticion/{competicion}")
	public List<CategoriaEntity> getByCompeticion(@PathVariable Long competicion) throws Exception {
		System.out.println("Getting Categoria List By Competicion..."+competicion);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<CategoriaEntity> output = new ArrayList<CategoriaEntity>();
		List<CategoriaEntity> recs =
				repository.findByCompanyOrderByEdadMinimaAsc(user.getCompany());
		for(CategoriaEntity rec:recs){
			List<ParticipanteEntity> lista = 
					inscripcionesrepo.findByCompeticionAndCategoria(competicion, rec.getId());
			int hombres=0, mujeres=0;
			for(ParticipanteEntity inscr:lista){
				PatinadorEntity patin = patinrepo.findById(inscr.getPatinador());
				if(patin==null) continue;
				if(patin.getGenero()==GenderEnum.MALE)
					hombres++;
				else
					mujeres++;
			}
			rec.setHombres(hombres);
			rec.setMujeres(mujeres);
			int total = hombres + mujeres;
			// TODO hacerlo configurable
			if(rec.getGenero()==GenderEnum.MALE && hombres<3)
				rec.setAccion(AccionEnum.UNIR);
			if(rec.getGenero()==GenderEnum.FEMALE && mujeres<3) 
				rec.setAccion(AccionEnum.UNIR);
			if(rec.getGenero()==GenderEnum.MIXTO && hombres>=3 && mujeres>=3)
				rec.setAccion(AccionEnum.DIVIDIR);
			if(rec.getGenero()==GenderEnum.MIXTO && total<3 && rec.getEdadMinima()>1)
				rec.setAccion(AccionEnum.BAJAR);
			if(total>0)
				output.add(rec);
		}
		return output;
	}
	
	@RequestMapping("/executeAction/{recId}/{accion}/{competicion}")
	public boolean executeAction(@PathVariable Long recId, @PathVariable AccionEnum accion, 
			@PathVariable Long competicion) throws Exception {
		if(recId==null || accion==null) return false;
		System.out.println("executeAction for Categoria..."+recId+" "+accion+" "+competicion);
		CategoriaEntity origen = repository.findById(recId);
		if(origen!=null){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UserEntity user = userrepo.findByLoginname(auth.getName());
			List<CategoriaEntity> recs = 
					repository.findByModalidadAndCompany(origen.getModalidad(), user.getCompany());
			switch(accion){
			case DEFAULT:
				defaultCategoria(competicion, origen, recs);
				break;
			case DIVIDIR:
				dividirCategoria(competicion, origen, recs);
				break;
			case UNIR:
				unirCategorias(competicion, origen, recs);
				break;
			case BAJAR:
				bajarCategoria(competicion, origen, recs);
				break;
			case SUBIR:
				subirCategoria(competicion, origen, recs);
				break;
			case NADA:
				break;
			}
			return true;
		}
		return false;	
	}

	private void subirCategoria(Long competicion, CategoriaEntity origen, 
			List<CategoriaEntity> recs) {
		System.out.println("Origen  = "+origen);
		CategoriaEntity destino = null;
		for(CategoriaEntity cat:recs){
			if(cat.getEdadMinima()==origen.getEdadMaxima()+1 &&
			   cat.getGenero()==origen.getGenero())
				destino = cat;
		}
		if(destino==null)
			throw new IllegalArgumentException("No existe una Categoría de destino para ejecutar esta acción.");
		System.out.println("Destino = "+destino);
		List<ParticipanteEntity> inscripciones = 
				inscripcionesrepo.findByCompeticionAndCategoria(competicion, origen.getId());
		for(ParticipanteEntity rec:inscripciones){
			rec.setCategoria(destino.getId());
			inscripcionesrepo.save(rec);
		}
		System.out.println("Done");
	}

	private void bajarCategoria(Long competicion, CategoriaEntity origen, 
			List<CategoriaEntity> recs) {
		System.out.println("Origen  = "+origen);
		CategoriaEntity destino = null;
		for(CategoriaEntity cat:recs){
			if(cat.getEdadMaxima()==origen.getEdadMinima()-1 &&
			   cat.getGenero()==origen.getGenero())
				destino = cat;
		}
		if(destino==null)
			throw new IllegalArgumentException("No existe una Categoría de destino para ejecutar esta acción.");
		System.out.println("Destino = "+destino);
		List<ParticipanteEntity> inscripciones = 
				inscripcionesrepo.findByCompeticionAndCategoria(competicion, origen.getId());
		for(ParticipanteEntity rec:inscripciones){
			rec.setCategoria(destino.getId());
			inscripcionesrepo.save(rec);
		}
		System.out.println("Done");
	}

	private void unirCategorias(Long competicion, CategoriaEntity origen, 
			List<CategoriaEntity> recs) {
		System.out.println("Origen  = "+origen);
		if(origen.getGenero()==GenderEnum.MIXTO)
			throw new IllegalArgumentException("No se puede unir una categoría mixta.");
		CategoriaEntity omasc=null, ofem=null, destino = null;
		for(CategoriaEntity cat:recs){
			if(cat.getEdadMinima()==origen.getEdadMinima() &&
			   cat.getEdadMaxima()==origen.getEdadMaxima()){
				if(cat.getGenero()==GenderEnum.MIXTO)
					destino = cat;
				else if(cat.getGenero()==GenderEnum.FEMALE)
					ofem = cat;
				else
					omasc = cat;
			}
		}
		if(ofem == null || omasc == null || destino == null)
			throw new IllegalArgumentException("No existe una Categoría de destino para ejecutar esta acción.");
		System.out.println("Destino = "+destino);
		List<ParticipanteEntity> inscripciones = 
				inscripcionesrepo.findByCompeticionAndCategoria(competicion, omasc.getId());
		for(ParticipanteEntity rec:inscripciones){
			rec.setCategoria(destino.getId());
			inscripcionesrepo.save(rec);
		}
		inscripciones = inscripcionesrepo.findByCompeticionAndCategoria(competicion, ofem.getId());
		for(ParticipanteEntity rec:inscripciones){
			rec.setCategoria(destino.getId());
			inscripcionesrepo.save(rec);
		}
		System.out.println("Done");
	}

	private void dividirCategoria(Long competicion, CategoriaEntity origen, 
			List<CategoriaEntity> recs) {
		System.out.println("Origen  = "+origen);
		if(origen.getGenero()!=GenderEnum.MIXTO)
			throw new IllegalArgumentException("Sólo se puede dividir una categoría mixta.");
		CategoriaEntity dmasc=null, dfem=null;
		for(CategoriaEntity cat:recs){
			if(cat.getEdadMinima()==origen.getEdadMinima() &&
			   cat.getEdadMaxima()==origen.getEdadMaxima()){
				if(cat.getGenero() == GenderEnum.FEMALE)
					dfem = cat;
				else if(cat.getGenero() == GenderEnum.MALE)
					dmasc = cat;
			}
		}
		if(dfem == null || dmasc == null)
			throw new IllegalArgumentException("No existe una Categoría de destino para ejecutar esta acción.");
		System.out.println("Destino Masc = "+dmasc);
		System.out.println("Destino Fem  = "+dfem);
		List<ParticipanteEntity> inscripciones = 
				inscripcionesrepo.findByCompeticionAndCategoria(competicion, origen.getId());
		for(ParticipanteEntity rec:inscripciones){
			PatinadorEntity patin = patinrepo.findById(rec.getPatinador());
			if(patin!=null){
				if(patin.getGenero()==GenderEnum.FEMALE)
					rec.setCategoria(dfem.getId());
				else 
					rec.setCategoria(dmasc.getId());
				inscripcionesrepo.save(rec);
			}
		}
		System.out.println("Done");
	}
	
	@SuppressWarnings("deprecation")
	private void defaultCategoria(Long competicion, CategoriaEntity origen,
			List<CategoriaEntity> recs) {
		System.out.println("Origen  = "+origen);
		List<ParticipanteEntity> inscripciones = 
				inscripcionesrepo.findByCompeticionAndCategoria(competicion, origen.getId());
		for(ParticipanteEntity rec:inscripciones){
			PatinadorEntity patin = patinrepo.findById(rec.getPatinador());
			if(patin!=null){
				// A partir de la edad del niño inferirá su posible categoria
		    	Date now = new Date();
		    	int edad = now.getYear() - patin.getFechaNacimiento().getYear();
		    	for(CategoriaEntity cat:recs){
		    		if(cat.getEdadMinima()<=edad && edad<=cat.getEdadMaxima() 
		    			&& patin.getGenero()==cat.getGenero() ){
		    			rec.setCategoria(cat.getId());
		    			inscripcionesrepo.save(rec);
		    		}
		    	}
	    	}
		}
		System.out.println("Done");
	}
	
}