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

import freewill.nextgen.blts.daos.RankingRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.RankingEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   RankingManager.java
 * Date:   31/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Ranking
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/RankingEntity")
public class RankingManager {
	
	@Autowired
	RankingRepository repository;
	
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
	public RankingEntity add(@RequestBody RankingEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Ranking..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		rec.setPuntuacion(getBest3Of4(rec));
    		RankingEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			
			// Actualiza orden
			List<RankingEntity> recs = repository.findByCircuitoAndCategoriaOrderByPuntuacionDesc(
					rec.getCircuito(), rec.getCategoria());
			int orden = 1;
			for(RankingEntity item:recs){
				item.setOrden(orden++);
				repository.save(item);
			}
						
			// Devuelve registro actualizado
			return repository.findById(res.getId());
		}
		return null;
	}
	
	@RequestMapping("/update")
	public RankingEntity update(@RequestBody RankingEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Ranking..."+rec);
			rec.setPuntuacion(getBest3Of4(rec));
			RankingEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			
			// Actualiza orden
			List<RankingEntity> recs = repository.findByCircuitoAndCategoriaOrderByPuntuacionDesc(
					rec.getCircuito(), rec.getCategoria());
			int orden = 1;
			for(RankingEntity item:recs){
				item.setOrden(orden++);
				repository.save(item);
			}
			
			// Devuelve registro actualizado
			return repository.findById(res.getId());
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Ranking..."+recId);
			RankingEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<RankingEntity> getlist() throws Exception {
		System.out.println("Getting Entire Ranking List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<RankingEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public RankingEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Ranking..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getByCircuitoAndCategoria/{circuito}/{categoria}")
	public List<RankingEntity> getByCircuitoAndCategoria(@PathVariable Long circuito,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting Ranking List By circuito y categoria..."
			+circuito+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<RankingEntity> recs = repository.findByCircuitoAndCategoriaOrderByPuntuacionDesc(circuito, categoria);
		int orden = 1;
		for(RankingEntity rec:recs)
			rec.setOrden(orden++);
		return recs;
	}
	
	@RequestMapping("/countByCircuitoAndCategoria/{circuito}/{categoria}")
	public RankingEntity countByCompeticionAndCategoria(
			@PathVariable Long circuito, @PathVariable Long categoria) throws Exception {
		System.out.println("Counting Participantes List By circuito y categoria..."
			+circuito+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		RankingEntity rec = new RankingEntity();
		try{
			rec.setId(repository.countByCircuitoAndCategoria(circuito, categoria));
		}
		catch(Exception e){
			rec.setId(0L);
		}
		return rec;
	}
	
	private int getBest3Of4(RankingEntity rec) {
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