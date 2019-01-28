package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import freewill.nextgen.blts.data.SpeedTimeTrialEntity;

public interface SpeedTimeTrialRepository extends CrudRepository<SpeedTimeTrialEntity, Long> {

	SpeedTimeTrialEntity findById(Long id);
	List<SpeedTimeTrialEntity> findByCompany(Long id);
	List<SpeedTimeTrialEntity> findByPatinador(Long recId);
	SpeedTimeTrialEntity findByPatinadorAndCompeticion(Long patinador, Long competicion);
	List<SpeedTimeTrialEntity> findByCompeticionAndCategoriaOrderByClasificacionAsc(
			Long competicion, Long categoria);
	SpeedTimeTrialEntity findByCompeticionAndCategoriaAndClasificacion(
			Long competicion, Long categoria, int clasificacion);
	List<SpeedTimeTrialEntity> findByCompeticionAndCategoriaOrderByOrden1Asc(
			Long competicion, Long categoria);
	List<SpeedTimeTrialEntity> findByCompeticionAndCategoriaOrderByOrden2Asc(
			Long competicion, Long categoria);
	
	@Modifying
	@Query("DELETE FROM SpeedTimeTrialEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	@Transactional
	@Modifying
	@Query("DELETE FROM SpeedTimeTrialEntity u WHERE u.competicion = ?1 AND u.categoria = ?2")
	void deleteByCompeticionAndCategoria(Long competicion, Long categoria);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM SpeedTimeTrialEntity m")
	Long getMaxId();
	List<SpeedTimeTrialEntity> findByCompeticionAndCategoriaOrderByClasificacionFinalAsc(
			Long competicion, Long categoria);
	List<SpeedTimeTrialEntity> findByCompeticionAndCategoriaOrderByTiempoAjustado1Asc(
			Long competicion, Long categoria);
	List<SpeedTimeTrialEntity> findByCompeticionAndCategoriaOrderByMejorTiempoAsc(
			Long competicion, Long categoria);
		
}
