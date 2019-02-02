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

import freewill.nextgen.blts.daos.ConfigRepository;
import freewill.nextgen.blts.daos.SpeedKOSystemRepository;
import freewill.nextgen.blts.daos.SpeedTimeTrialRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.SpeedKOSystemEntity;
import freewill.nextgen.blts.data.SpeedKOSystemEntity.EliminatoriaEnum;
import freewill.nextgen.blts.data.SpeedTimeTrialEntity;
import freewill.nextgen.blts.data.ConfigEntity.ConfigItemEnum;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   SpeedKOSystemManager.java
 * Date:   13/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage SpeedKOSystem
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/SpeedKOSystemEntity")
public class SpeedKOSystemManager {
	
	@Autowired
	SpeedKOSystemRepository repository;
	
	@Autowired
	SpeedTimeTrialRepository timetrialrepo;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	ConfigRepository configrepo;

	@RequestMapping("/create")
	public SpeedKOSystemEntity add(@RequestBody SpeedKOSystemEntity rec) throws Exception {
		if(rec!=null){
			if(rec.getCompeticion()==null)
				throw new IllegalArgumentException("El registro debe contener un Campeonato.");
			List<SpeedKOSystemEntity> old = repository.findByPatinador1AndPatinador2AndCompeticion(
					rec.getPatinador1(), rec.getPatinador2(), rec.getCompeticion());
			if(old!=null && old.size()>0)
				throw new IllegalArgumentException("El registro ya existe.");
			// Injects the new record
			System.out.println("Saving SpeedKOSystem..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
    		SpeedKOSystemEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public SpeedKOSystemEntity update(@RequestBody SpeedKOSystemEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating SpeedKOSystem..."+rec);	
			int gana1=0, gana2=0, ganadorDorsal=0;
			if(rec.getPat1gana1()) gana1++;
			if(rec.getPat1gana2()) gana1++;
			if(rec.getPat1gana3()) gana1++;
			if(rec.getPat2gana1()) gana2++;
			if(rec.getPat2gana2()) gana2++;
			if(rec.getPat2gana3()) gana2++;
			if(gana1>gana2){
				rec.setGanador(rec.getPatinador1());
				rec.setGanadorStr(rec.getNombre1()+" "+rec.getApellidos1());
				ganadorDorsal = rec.getDorsal1();
			}
			else if(gana2>gana1){
				rec.setGanador(rec.getPatinador2());
				rec.setGanadorStr(rec.getNombre2()+" "+rec.getApellidos2());
				ganadorDorsal = rec.getDorsal2();
			}
			else{
				rec.setGanador(null);
				rec.setGanadorStr("");
				ganadorDorsal = 0;
			}
			if(rec.getGanador()==null)
				throw new IllegalArgumentException("Debe rellenar el resultado de las carreras.");
			
			SpeedKOSystemEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		boolean consolacion = configrepo.getConfigBoolean(
    				ConfigItemEnum.FINALCONSOLACIONSPEED, user.getCompany());
			
			// implica actualizar también el registro padre
			if(rec.getEliminatoria().ordinal()>0 && rec.getGanador()!=null){
				EliminatoriaEnum parentElim = EliminatoriaEnum.values()[rec.getEliminatoria().ordinal()-1];
				int parentGrupo = (rec.getGrupo())/2;
			
				SpeedKOSystemEntity parent = repository.findByCompeticionAndCategoriaAndEliminatoriaAndGrupo(
					rec.getCompeticion(), rec.getCategoria(), parentElim, parentGrupo);
				if(parent!=null){
					if(rec.getGrupo()%2==0){
						parent.setPatinador1(rec.getGanador());
						parent.setNombre1(rec.getGanadorStr());
						parent.setDorsal1(ganadorDorsal);
					}
					else{
						parent.setPatinador2(rec.getGanador());
						parent.setNombre2(rec.getGanadorStr());
						parent.setDorsal2(ganadorDorsal);
					}
					repository.save(parent);
					System.out.println("Saved Parent = "+parent.getId());
				}
				// Actualiza tambien final consolacion
				if(consolacion && rec.getEliminatoria()==EliminatoriaEnum.SEMIS){
					parent = repository.findByCompeticionAndCategoriaAndEliminatoriaAndGrupo(
							rec.getCompeticion(), rec.getCategoria(), parentElim, 1);
					if(parent!=null){
						Long perdedor = (rec.getGanador()==rec.getPatinador1()?rec.getPatinador2():rec.getPatinador1());
						String perdedorStr = (rec.getGanador()==rec.getPatinador1()?rec.getNombre2():rec.getNombre1());
						int perdedorDorsal = (rec.getGanador()==rec.getPatinador1()?rec.getDorsal2():rec.getDorsal1());
						if(rec.getGrupo()%2==0){
							parent.setPatinador1(perdedor);
							parent.setNombre1(perdedorStr);
							parent.setDorsal1(perdedorDorsal);
						}
						else{
							parent.setPatinador2(perdedor);
							parent.setNombre2(perdedorStr);
							parent.setDorsal2(perdedorDorsal);
						}
						repository.save(parent);
						System.out.println("Saved Parent (consolacion)= "+parent.getId());
					}
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
			System.out.println("Deleting SpeedKOSystem..."+recId);
			SpeedKOSystemEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<SpeedKOSystemEntity> getlist() throws Exception {
		System.out.println("Getting Entire SpeedKOSystems List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<SpeedKOSystemEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public SpeedKOSystemEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving SpeedKOSystem..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/findByCompeticionAndCategoriaAndEliminatoria/{competicion}/{categoria}/{ronda}")
	public List<SpeedKOSystemEntity> findByCompeticionAndCategoriaAndRonda(@PathVariable Long competicion,
			@PathVariable Long categoria, @PathVariable EliminatoriaEnum ronda) throws Exception {
		int posicion[] = {1,2};
		int posFinal[] = {1,2};
		int posSemis[] = {1,4, 2,3};
		int posCuartos[] = {1,8, 4,5, 3,6, 2,7};
		int posOctavos[] = {1,16, 8,9, 5,12, 4,13, 3,14, 6,11, 7,10, 2,15};
		int posDieciseis[] = {1,32, 3,30, 4,5, 6,7, 8,9, 10,11, 12,13, 14,15, 16,17,
								18,19, 20,21, 22,23, 24,25, 26,27, 28,29, 2,31}; 
								// TODO ordenar correctamente el caso de 16avos
		System.out.println("Getting SpeedKOSystems List By competicion, categoria y eliminatoria..."
			+competicion+","+categoria+","+ronda);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		boolean consolacion = configrepo.getConfigBoolean(
				ConfigItemEnum.FINALCONSOLACIONSPEED, user.getCompany());
		
		if(ronda==EliminatoriaEnum.FINAL)
			posicion = posFinal;
		else if(ronda==EliminatoriaEnum.SEMIS)
			posicion = posSemis;
		else if(ronda==EliminatoriaEnum.CUARTOS)
			posicion = posCuartos;
		else if(ronda==EliminatoriaEnum.OCTAVOS)
			posicion = posOctavos;
		else if(ronda==EliminatoriaEnum.DIECISEIS)
			posicion = posDieciseis;
		
		List<SpeedKOSystemEntity> recs = repository.findByCompeticionAndCategoria(competicion, categoria);
		if(recs==null || recs.size()==0){
			
			// Actualiza clasificacion Final
			List<SpeedTimeTrialEntity> ttrs = timetrialrepo.findByCompeticionAndCategoriaOrderByMejorTiempoAsc(
					competicion, categoria);
			//int orden = 1;
			for(SpeedTimeTrialEntity rec:ttrs){
				//rec.setClasificacion(orden++);
				rec.setClasificacionFinal(rec.getClasificacion());
				timetrialrepo.save(rec);
			}
			
			// Needs to create records
			SpeedKOSystemEntity rec = null;
			int numLevels = ronda.ordinal()+1;
			
			for(int i=0;i<numLevels;i++){
				int n = (int)(Math.pow(2.0, i));
				int k = 0;
				for(int j=0; j<n;j++){
					rec = new SpeedKOSystemEntity();
					rec.setCategoria(categoria);
					rec.setCompeticion(competicion);
					rec.setGrupo(j);
					rec.setEliminatoria(EliminatoriaEnum.values()[i]);
					rec.setCompany(user.getCompany());
					if(i==numLevels-1){
						SpeedTimeTrialEntity resultado =
								timetrialrepo.findByCompeticionAndCategoriaAndClasificacion(
										competicion, categoria, posicion[k++]);
						if(resultado!=null){
							rec.setApellidos1(resultado.getApellidos());
							rec.setNombre1(resultado.getNombre());
							rec.setPatinador1(resultado.getPatinador());
							rec.setDorsal1(resultado.getDorsal());
						}
						resultado = timetrialrepo.findByCompeticionAndCategoriaAndClasificacion(
										competicion, categoria, posicion[k++]);
						if(resultado!=null){
							rec.setApellidos2(resultado.getApellidos());
							rec.setNombre2(resultado.getNombre());
							rec.setPatinador2(resultado.getPatinador());
							rec.setDorsal2(resultado.getDorsal());
						}
					}
					repository.save(rec);
					// Crea tambien la Final de consolacion
					if(consolacion && i==0){
						rec = new SpeedKOSystemEntity();
						rec.setCategoria(categoria);
						rec.setCompeticion(competicion);
						rec.setGrupo(1);
						rec.setEliminatoria(EliminatoriaEnum.values()[i]);
						rec.setCompany(user.getCompany());
						repository.save(rec);
					}
				}
			}
			// retrieve and return new created records
			recs = repository.findByCompeticionAndCategoria(competicion, categoria);
		}
		return recs;
	}
	
	@RequestMapping("/existByCompeticionAndCategoria/{competicion}/{categoria}")
	public SpeedKOSystemEntity existByCompeticionAndCategoria(@PathVariable Long competicion,
			@PathVariable Long categoria) throws Exception {
		System.out.println("Getting existByCompeticionAndCategoria..."+competicion+","+categoria);
		List<SpeedKOSystemEntity> recs = repository.findByCompeticionAndCategoria(competicion, categoria);
		SpeedKOSystemEntity out = new SpeedKOSystemEntity();
		out.setEliminatoria(null);
		if(recs==null || recs.size()==0)
			return out;
		out.setEliminatoria(EliminatoriaEnum.FINAL);
		for(SpeedKOSystemEntity rec:recs){
			if(rec.getEliminatoria().ordinal()>out.getEliminatoria().ordinal())
				out.setEliminatoria(rec.getEliminatoria());
		}
		return out;	
	}
	
}