package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import freewill.nextgen.blts.data.SpeedKOSystemEntity;
import freewill.nextgen.blts.data.SpeedKOSystemEntity.EliminatoriaEnum;

public interface SpeedKOSystemRepository extends CrudRepository<SpeedKOSystemEntity, Long> {

	SpeedKOSystemEntity findById(Long id);
	List<SpeedKOSystemEntity> findByCompany(Long id);
	List<SpeedKOSystemEntity> findByPatinador1(Long recId);
	List<SpeedKOSystemEntity> findByPatinador2(Long recId);
	List<SpeedKOSystemEntity> findByCompeticionAndCategoriaAndEliminatoria(
			Long competicion, Long catmodalidad, EliminatoriaEnum ronda);
	List<SpeedKOSystemEntity> findByPatinador1AndPatinador2AndCompeticion(
			Long patinador1, Long patinador2, Long competicion);
	List<SpeedKOSystemEntity> findByCompeticionAndCategoria(Long competicion, Long categoria);
	SpeedKOSystemEntity findByCompeticionAndCategoriaAndEliminatoriaAndGrupo(
			Long competicion, Long categoria, EliminatoriaEnum eliminatoria, int grupo);
	
	@Modifying
	@Query("DELETE FROM SpeedKOSystemEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	@Transactional
	@Modifying
	@Query("DELETE FROM SpeedKOSystemEntity u WHERE u.competicion = ?1 AND u.categoria = ?2")
	void deleteByCompeticionAndCategoria(Long competicion, Long categoria);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM SpeedKOSystemEntity m")
	Long getMaxId();
	
}
