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

import freewill.nextgen.blts.daos.BattleRepository;
import freewill.nextgen.blts.daos.BattleRondaRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.BattleEntity;
import freewill.nextgen.blts.data.BattleRondaEntity;
import freewill.nextgen.blts.data.BattleRondaEntity.EliminatoriaEnum;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   BattleRondaManager.java
 * Date:   05/01/2019
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage BattleRonda
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/BattleRondaEntity")
public class BattleRondaManager {
	
	@Autowired
	BattleRondaRepository repository;
	
	@Autowired
	BattleRepository battlerepo;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public BattleRondaEntity add(@RequestBody BattleRondaEntity rec) throws Exception {
		return null;
	}
	
	@RequestMapping("/update")
	public BattleRondaEntity update(@RequestBody BattleRondaEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating BattleRonda..."+rec);
			
			BattleRondaEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			
			// implica actualizar también el registro padre
			if(rec.getEliminatoria().ordinal()>0 && 
					rec.getGanador1()!=null && rec.getGanador2()!=null){
				EliminatoriaEnum parentElim = EliminatoriaEnum.values()[rec.getEliminatoria().ordinal()-1];
				int parentGrupo = (rec.getGrupo())/2;
			
				BattleRondaEntity parent = repository.findByCompeticionAndCategoriaAndEliminatoriaAndGrupo(
					rec.getCompeticion(), rec.getCategoria(), parentElim, parentGrupo);
				if(parent!=null){
					if(rec.getGrupo()%2==0){
						parent.setPatinador1(rec.getGanador1());
						parent.setNombre1(rec.getGanadorStr1());
						parent.setPatinador2(rec.getGanador2());
						parent.setNombre2(rec.getGanadorStr2());
					}
					else{
						parent.setPatinador3(rec.getGanador1());
						parent.setNombre3(rec.getGanadorStr1());
						parent.setPatinador4(rec.getGanador2());
						parent.setNombre4(rec.getGanadorStr2());
					}
					repository.save(parent);
					System.out.println("Saved Parent = "+parent.getId());
				}
			}
			
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting BattleRonda..."+recId);
			BattleRondaEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<BattleRondaEntity> getlist() throws Exception {
		System.out.println("Getting Entire BattleRondas List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<BattleRondaEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public BattleRondaEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving BattleRonda..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/findByCompeticionAndCategoria/{competicion}/{categoria}")
	public List<BattleRondaEntity> findByCompeticionAndCategoriaAndRonda(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		int posicion[] = null;
		int posFinal[] = {1,2,3,4};
		int posSemis[] = {1,8,4,5, 3,6,2,7};
		int posSemisFD[] = {1,2,7,8, 3,4,5,6};
		int posCuartos[] = {1,16,8,9, 5,12,4,13, 3,14,6,11, 7,10,2,15};
		System.out.println("Getting BattleRondas List By competicion y categoria..."
			+competicion+","+categoria);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		
		List<BattleRondaEntity> recs = repository.findByCompeticionAndCategoria(competicion, categoria);
		if(recs==null || recs.size()==0){
			// Needs to create records
			BattleRondaEntity rec = null;
			
			// calcula eliminatoria inicial en funcion del numero de participantes
			EliminatoriaEnum ronda = EliminatoriaEnum.CUARTOS;
			int numpatines = battlerepo.findByCompeticionAndCategoriaOrderByOrdenAsc(
					competicion, categoria).size();
			if(numpatines<5)
				ronda = EliminatoriaEnum.FINAL;
			else if(numpatines<11) // TODO con 9 patinadores, hacer 3 grupos de 3
				ronda = EliminatoriaEnum.SEMIS;
			// Si hay 17 o mas, entonces debe haber una pre-clasificatoria
			
			int numLevels = ronda.ordinal()+1;
			if(ronda==EliminatoriaEnum.FINAL)
				posicion = posFinal;
			else if(ronda==EliminatoriaEnum.SEMIS)
				posicion = posSemis;
			else if(ronda==EliminatoriaEnum.CUARTOS)
				posicion = posCuartos;
			if(numpatines==5)
				posicion = posSemisFD;
			
			for(int i=0;i<numLevels;i++){ // Cada i es una eliminatoria: final, semis, cuartos, ...
				int n = (int)(Math.pow(2.0, i));
				int k = 0;
				for(int j=0; j<n;j++){ // Cada n es una caja donde se enfrentan 4 patinadores
					rec = new BattleRondaEntity();
					rec.setCategoria(categoria);
					rec.setCompeticion(competicion);
					rec.setGrupo(j);
					rec.setEliminatoria(EliminatoriaEnum.values()[i]);
					rec.setCompany(user.getCompany());
					if(i==numLevels-1){ // en el último nivel asignamos los primeros enfrentamientos
						for(int l=0, ll=0;l<4;l++){ // Hay 4 patinadores en cada caja
							
							BattleEntity resultado =
									battlerepo.findByCompeticionAndCategoriaAndOrden(
											competicion, categoria, posicion[k++]);
							
							if(resultado==null) continue;
							switch(ll++){
								case 0:
									rec.setApellidos1(resultado.getApellidos());
									rec.setNombre1(resultado.getNombre());
									rec.setPatinador1(resultado.getPatinador());
									break;
								case 1:
									rec.setApellidos2(resultado.getApellidos());
									rec.setNombre2(resultado.getNombre());
									rec.setPatinador2(resultado.getPatinador());
									break;
								case 2:
									rec.setApellidos3(resultado.getApellidos());
									rec.setNombre3(resultado.getNombre());
									rec.setPatinador3(resultado.getPatinador());
									break;
								case 3:
									rec.setApellidos4(resultado.getApellidos());
									rec.setNombre4(resultado.getNombre());
									rec.setPatinador4(resultado.getPatinador());
									break;
							}
						}
					}
					repository.save(rec);
				}
			}
			// retrieve and return new created records
			recs = repository.findByCompeticionAndCategoria(competicion, categoria);
		}
		return recs;
	}
	
	@RequestMapping("/existByCompeticionAndCategoria/{competicion}/{categoria}")
	public BattleRondaEntity existByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting existByCompeticionAndCategoria..."+competicion+","+categoria);
		List<BattleRondaEntity> recs = repository.findByCompeticionAndCategoria(competicion, categoria);
		BattleRondaEntity out = new BattleRondaEntity();
		out.setEliminatoria(null);
		if(recs==null || recs.size()==0)
			return out;
		out.setEliminatoria(EliminatoriaEnum.FINAL);
		for(BattleRondaEntity rec:recs){
			if(rec.getEliminatoria().ordinal()>out.getEliminatoria().ordinal())
				out.setEliminatoria(rec.getEliminatoria());
		}
		return out;
	}
	
	@RequestMapping("/deleteByCompeticionAndCategoria/{competicion}/{categoria}")
	public boolean deleteByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Deleting Battle By competicion y categoria..."
			+competicion+","+categoria);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		repository.deleteByCompeticionAndCategoria(competicion, categoria);
		battlerepo.deleteByCompeticionAndCategoria(competicion, categoria);
		return true;
	}
}