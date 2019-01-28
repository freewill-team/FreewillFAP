package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import freewill.nextgen.blts.data.BattleEntity;

public interface BattleRepository extends CrudRepository<BattleEntity, Long> {

	BattleEntity findById(Long id);
	List<BattleEntity> findByCompany(Long id);
	List<BattleEntity> findByPatinador(Long recId);
	BattleEntity findByPatinadorAndCompeticion(Long patinador, Long competicion);
	List<BattleEntity> findByCompeticionAndCategoriaOrderByClasificacionAsc(
			Long competicion, Long categoria);
	BattleEntity findByCompeticionAndCategoriaAndClasificacion(
			Long competicion, Long categoria, int clasificacion);
	List<BattleEntity> findByCompeticionAndCategoriaOrderByOrdenAsc(
			Long competicion, Long categoria);
	BattleEntity findByCompeticionAndCategoriaAndOrden(
			Long competicion, Long categoria, int orden);
	
	@Modifying
	@Query("DELETE FROM BattleEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	@Transactional
	@Modifying
	@Query("DELETE FROM BattleEntity u WHERE u.competicion = ?1 AND u.categoria = ?2")
	void deleteByCompeticionAndCategoria(Long competicion, Long categoria);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM BattleEntity m")
	Long getMaxId();
		
}
