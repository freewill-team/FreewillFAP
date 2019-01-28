package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import freewill.nextgen.blts.data.DerrapesRondaEntity;
import freewill.nextgen.blts.data.DerrapesRondaEntity.EliminatoriaEnum;

public interface DerrapesRondaRepository extends CrudRepository<DerrapesRondaEntity, Long> {

	DerrapesRondaEntity findById(Long id);
	List<DerrapesRondaEntity> findByCompany(Long id);
	List<DerrapesRondaEntity> findByCompeticionAndCategoriaAndEliminatoria(
			Long competicion, Long catmodalidad, EliminatoriaEnum ronda);
	List<DerrapesRondaEntity> findByCompeticionAndCategoria(Long competicion, Long categoria);
	DerrapesRondaEntity findByCompeticionAndCategoriaAndEliminatoriaAndGrupo(
			Long competicion, Long categoria, EliminatoriaEnum eliminatoria, int grupo);
	
	@Modifying
	@Query("DELETE FROM DerrapesRondaEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	@Transactional
	@Modifying
	@Query("DELETE FROM DerrapesRondaEntity u WHERE u.competicion = ?1 AND u.categoria = ?2")
	void deleteByCompeticionAndCategoria(Long competicion, Long categoria);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM DerrapesRondaEntity m")
	Long getMaxId();
	
}
