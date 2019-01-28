package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import freewill.nextgen.blts.data.BattleRondaEntity;
import freewill.nextgen.blts.data.BattleRondaEntity.EliminatoriaEnum;

public interface BattleRondaRepository extends CrudRepository<BattleRondaEntity, Long> {

	BattleRondaEntity findById(Long id);
	List<BattleRondaEntity> findByCompany(Long id);
	List<BattleRondaEntity> findByCompeticionAndCategoriaAndEliminatoria(
			Long competicion, Long catmodalidad, EliminatoriaEnum ronda);
	List<BattleRondaEntity> findByCompeticionAndCategoria(Long competicion, Long categoria);
	BattleRondaEntity findByCompeticionAndCategoriaAndEliminatoriaAndGrupo(
			Long competicion, Long categoria, EliminatoriaEnum eliminatoria, int grupo);
	
	@Modifying
	@Query("DELETE FROM BattleRondaEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	@Transactional
	@Modifying
	@Query("DELETE FROM BattleRondaEntity u WHERE u.competicion = ?1 AND u.categoria = ?2")
	void deleteByCompeticionAndCategoria(Long competicion, Long categoria);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM BattleRondaEntity m")
	Long getMaxId();
	
}
