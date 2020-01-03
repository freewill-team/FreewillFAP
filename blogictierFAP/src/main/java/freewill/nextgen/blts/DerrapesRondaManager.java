package freewill.nextgen.blts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.DerrapesRepository;
import freewill.nextgen.blts.daos.DerrapesRondaRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.DerrapesEntity;
import freewill.nextgen.blts.data.DerrapesRondaEntity;
import freewill.nextgen.blts.data.DerrapesRondaEntity.EliminatoriaEnum;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   DerrapesRondaManager.java
 * Date:   27/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage DerrapesRonda
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/DerrapesRondaEntity")
public class DerrapesRondaManager {
	
	@Autowired
	DerrapesRondaRepository repository;
	
	@Autowired
	DerrapesRepository derrapesrepo;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public DerrapesRondaEntity add(@RequestBody DerrapesRondaEntity rec) throws Exception {
		return null;
	}
	
	@RequestMapping("/update")
	public DerrapesRondaEntity update(@RequestBody DerrapesRondaEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating DerrapesRonda..."+rec);
			
			DerrapesRondaEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			
			// implica actualizar también el registro padre
			if(rec.getEliminatoria().ordinal()>0 && 
					rec.getGanador1()!=null && rec.getGanador2()!=null){
				EliminatoriaEnum parentElim = EliminatoriaEnum.values()[rec.getEliminatoria().ordinal()-1];
				int parentGrupo = (rec.getGrupo())/2;
			
				DerrapesRondaEntity parent = repository.findByCompeticionAndCategoriaAndEliminatoriaAndGrupo(
					rec.getCompeticion(), rec.getCategoria(), parentElim, parentGrupo);
				if(parent!=null){
					int dorsalGan1 = 0, dorsalGan2 = 0;
					if(rec.getGanador1().compareTo(rec.getPatinador1())==0)
						dorsalGan1 = rec.getDorsal1();
					else if(rec.getGanador1().compareTo(rec.getPatinador2())==0)
						dorsalGan1 = rec.getDorsal2();
					else if(rec.getGanador1().compareTo(rec.getPatinador3())==0)
						dorsalGan1 = rec.getDorsal3();
					else if(rec.getGanador1().compareTo(rec.getPatinador4())==0)
						dorsalGan1 = rec.getDorsal4();
					
					if(rec.getGanador2().compareTo(rec.getPatinador1())==0)
						dorsalGan2 = rec.getDorsal1();
					else if(rec.getGanador2().compareTo(rec.getPatinador2())==0)
						dorsalGan2 = rec.getDorsal2();
					else if(rec.getGanador2().compareTo(rec.getPatinador3())==0)
						dorsalGan2 = rec.getDorsal3();
					else if(rec.getGanador2().compareTo(rec.getPatinador4())==0)
						dorsalGan2 = rec.getDorsal4();
					
					if(rec.getGrupo()%2==0){
						parent.setPatinador1(rec.getGanador1());
						parent.setNombre1(rec.getGanadorStr1());
						parent.setDorsal1(dorsalGan1);
						parent.setPatinador2(rec.getGanador2());
						parent.setNombre2(rec.getGanadorStr2());
						parent.setDorsal2(dorsalGan2);
					}
					else{
						parent.setPatinador3(rec.getGanador1());
						parent.setNombre3(rec.getGanadorStr1());
						parent.setDorsal3(dorsalGan1);
						parent.setPatinador4(rec.getGanador2());
						parent.setNombre4(rec.getGanadorStr2());
						parent.setDorsal4(dorsalGan2);
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
			System.out.println("Deleting DerrapesRonda..."+recId);
			DerrapesRondaEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<DerrapesRondaEntity> getlist() throws Exception {
		System.out.println("Getting Entire DerrapesRondas List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<DerrapesRondaEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public DerrapesRondaEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving DerrapesRonda..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/findByCompeticionAndCategoria/{competicion}/{categoria}")
	public List<DerrapesRondaEntity> findByCompeticionAndCategoriaAndRonda(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		int posicion[] = null;
		int posFinal[] = {1,2,3,4};
		int posSemis[] = {1,8,4,5, 3,6,2,7};
		int posSemisFD[] = {1,2,7,8, 3,4,5,6};
		int posCuartos[] = {1,16,8,9, 5,12,4,13, 3,14,6,11, 7,10,2,15};
		System.out.println("Getting DerrapesRondas List By competicion y categoria..."
			+competicion+","+categoria);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		
		List<DerrapesRondaEntity> recs = repository.findByCompeticionAndCategoria(competicion, categoria);
		if(recs==null || recs.size()==0){
			// Needs to create records
			DerrapesRondaEntity rec = null;
			
			// calcula eliminatoria inicial en funcion del numero de participantes
			EliminatoriaEnum ronda = EliminatoriaEnum.CUARTOS;
			int numpatines = derrapesrepo.findByCompeticionAndCategoriaOrderByOrdenAsc(
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
					rec = new DerrapesRondaEntity();
					rec.setCategoria(categoria);
					rec.setCompeticion(competicion);
					rec.setGrupo(j);
					rec.setEliminatoria(EliminatoriaEnum.values()[i]);
					rec.setCompany(user.getCompany());
					if(i==numLevels-1){ // en el último nivel asignamos los primeros enfrentamientos
						for(int l=0, ll=0;l<4;l++){ // Hay 4 patinadores en cada caja
							System.out.println("Retrieving k,pos = "+k+","+posicion[k]);
							DerrapesEntity resultado =
									derrapesrepo.findByCompeticionAndCategoriaAndOrden(
											competicion, categoria, posicion[k++]);
							System.out.println("Retrieving Resultado = "+resultado);
							if(resultado==null) continue;
							switch(ll++){
								case 0:
									rec.setApellidos1(resultado.getApellidos());
									rec.setNombre1(resultado.getNombre());
									rec.setPatinador1(resultado.getPatinador());
									rec.setDorsal1(resultado.getDorsal());
									break;
								case 1:
									rec.setApellidos2(resultado.getApellidos());
									rec.setNombre2(resultado.getNombre());
									rec.setPatinador2(resultado.getPatinador());
									rec.setDorsal2(resultado.getDorsal());
									break;
								case 2:
									rec.setApellidos3(resultado.getApellidos());
									rec.setNombre3(resultado.getNombre());
									rec.setPatinador3(resultado.getPatinador());
									rec.setDorsal3(resultado.getDorsal());
									break;
								case 3:
									rec.setApellidos4(resultado.getApellidos());
									rec.setNombre4(resultado.getNombre());
									rec.setPatinador4(resultado.getPatinador());
									rec.setDorsal4(resultado.getDorsal());
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
	public DerrapesRondaEntity existByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting existByCompeticionAndCategoria..."+competicion+","+categoria);
		List<DerrapesRondaEntity> recs = repository.findByCompeticionAndCategoria(competicion, categoria);
		DerrapesRondaEntity out = new DerrapesRondaEntity();
		out.setEliminatoria(null);
		if(recs==null || recs.size()==0)
			return out;
		out.setEliminatoria(EliminatoriaEnum.FINAL);
		for(DerrapesRondaEntity rec:recs){
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
		derrapesrepo.deleteByCompeticionAndCategoria(competicion, categoria);
		return true;
	}
	
	@RequestMapping("/mockByCompeticionAndCategoria/{competicion}/{categoria}")
	public List<DerrapesRondaEntity> mockByCompeticionAndCategoriaAndRonda(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		int posicion[] = null;
		int posFinal[] = {1,2,3,4};
		int posSemis[] = {1,8,4,5, 3,6,2,7};
		int posSemisFD[] = {1,2,7,8, 3,4,5,6};
		int posCuartos[] = {1,16,8,9, 5,12,4,13, 3,14,6,11, 7,10,2,15};
		System.out.println("Getting Mock DerrapesRondas List By competicion y categoria..."
			+competicion+","+categoria);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		
		List<DerrapesRondaEntity> recs = repository.findByCompeticionAndCategoria(competicion, categoria);
		if(recs==null || recs.size()==0){
			recs = new ArrayList<DerrapesRondaEntity>();
			// Needs to create records
			DerrapesRondaEntity rec = null;
			
			// calcula eliminatoria inicial en funcion del numero de participantes
			EliminatoriaEnum ronda = EliminatoriaEnum.CUARTOS;
			int numpatines = derrapesrepo.findByCompeticionAndCategoriaOrderByOrdenAsc(
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
					rec = new DerrapesRondaEntity();
					rec.setCategoria(categoria);
					rec.setCompeticion(competicion);
					rec.setGrupo(j);
					rec.setEliminatoria(EliminatoriaEnum.values()[i]);
					rec.setCompany(user.getCompany());
					if(i==numLevels-1){ // en el último nivel asignamos los primeros enfrentamientos
						for(int l=0, ll=0;l<4;l++){ // Hay 4 patinadores en cada caja
							System.out.println("Retrieving k,pos = "+k+","+posicion[k]);
							DerrapesEntity resultado =
									derrapesrepo.findByCompeticionAndCategoriaAndOrden(
											competicion, categoria, posicion[k++]);
							System.out.println("Retrieving Resultado = "+resultado);
							if(resultado==null) continue;
							switch(ll++){
								case 0:
									rec.setApellidos1(resultado.getApellidos());
									rec.setNombre1(resultado.getNombre());
									rec.setPatinador1(resultado.getPatinador());
									rec.setDorsal1(resultado.getDorsal());
									break;
								case 1:
									rec.setApellidos2(resultado.getApellidos());
									rec.setNombre2(resultado.getNombre());
									rec.setPatinador2(resultado.getPatinador());
									rec.setDorsal2(resultado.getDorsal());
									break;
								case 2:
									rec.setApellidos3(resultado.getApellidos());
									rec.setNombre3(resultado.getNombre());
									rec.setPatinador3(resultado.getPatinador());
									rec.setDorsal3(resultado.getDorsal());
									break;
								case 3:
									rec.setApellidos4(resultado.getApellidos());
									rec.setNombre4(resultado.getNombre());
									rec.setPatinador4(resultado.getPatinador());
									rec.setDorsal4(resultado.getDorsal());
									break;
							}
						}
					}
					recs.add(rec);
				}
			}
		}
		return recs;
	}
	
}