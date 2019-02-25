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

import freewill.nextgen.blts.daos.RankingAbsRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.blts.data.RankingAbsEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   RankingAbsManager.java
 * Date:   31/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage RankingAbs
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/RankingAbsEntity")
public class RankingAbsManager {
	
	@Autowired
	RankingAbsRepository repository;
	
	//@Autowired
	//ParticipanteRepository inscripcionesrepo;
	
	//@Autowired
	//CompeticionRepository competirepo;
	
	//@Autowired
	//CategoriaRepository categorepo;
	
	//@Autowired
	//CircuitoRepository circuitorepo;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public RankingAbsEntity add(@RequestBody RankingAbsEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving RankingAbs..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		rec.setPuntuacion(getBest3Of4(rec));
    		RankingAbsEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
						
			// Devuelve registro actualizado
			return repository.findById(res.getId());
		}
		return null;
	}
	
	@RequestMapping("/update")
	public RankingAbsEntity update(@RequestBody RankingAbsEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating RankingAbs..."+rec);
			rec.setPuntuacion(getBest3Of4(rec));
			RankingAbsEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			
			// Devuelve registro actualizado
			return repository.findById(res.getId());
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting RankingAbs..."+recId);
			RankingAbsEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<RankingAbsEntity> getlist() throws Exception {
		System.out.println("Getting Entire RankingAbs List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<RankingAbsEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public RankingAbsEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving RankingAbs..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getByModalidad/{modalidad}")
	public List<RankingAbsEntity> getByModalidad(
			@PathVariable ModalidadEnum modalidad) throws Exception {
		System.out.println("Getting RankingAbs List By modalidad..."+modalidad);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<RankingAbsEntity> recs = 
				repository.findByModalidadAndCompanyOrderByPuntuacionDesc(
						modalidad, user.getCompany());
		int orden = 1;
		for(RankingAbsEntity rec:recs)
			rec.setOrden(orden++);
		return recs;
	}
	
	private int getBest3Of4(RankingAbsEntity rec) {
		int minimo = 20000;
		int suma = rec.getPuntos1()+rec.getPuntos2()+rec.getPuntos3()+rec.getPuntos4();
		//System.out.println("    Suma = "+suma);
		if(rec.getPuntos1()<minimo)
			minimo = rec.getPuntos1();
		if(rec.getPuntos2()<minimo)
			minimo = rec.getPuntos2();
		if(rec.getPuntos3()<minimo)
			minimo = rec.getPuntos3();
		if(rec.getPuntos4()<minimo)
			minimo = rec.getPuntos4();
		if(minimo!=20000)
			suma = suma - minimo;
		//System.out.println("    Minimo = "+minimo);
		//System.out.println("    Best3of4 = "+suma);
		return suma;
	}
	
}