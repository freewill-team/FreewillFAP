package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.JamShowEntity;

public interface JamShowRepository extends CrudRepository<JamShowEntity, Long> {

	JamShowEntity findById(Long id);
	List<JamShowEntity> findByCompany(Long id);
	List<JamShowEntity> findByPatinador(Long recId);
	JamShowEntity findByPatinadorAndCompeticion(Long patinador, Long competicion);
	
	JamShowEntity findByCompeticionAndCategoriaAndClasificacionFinal(
			Long competicion, Long categoria, int clasificacionFinal);
	List<JamShowEntity> findByCompeticionAndCategoriaOrderByOrden1Asc(
			Long competicion, Long categoria);
	
	@Modifying
	@Query("DELETE FROM JamShowEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM JamShowEntity m")
	Long getMaxId();
	
	List<JamShowEntity> findByCompeticionAndCategoriaOrderByClasificacionFinalAsc(
			Long competicion, Long categoria);
	JamShowEntity findByCompeticionAndCategoriaAndOrden1(
			Long competicion, Long categoria, int i);
	List<JamShowEntity> findByCompeticionAndCategoriaOrderByTotalJuez1Desc(
			Long competicion, Long categoria);
	List<JamShowEntity> findByCompeticionAndCategoriaOrderByTotalJuez2Desc(
			Long competicion, Long categoria);
	List<JamShowEntity> findByCompeticionAndCategoriaOrderByTotalJuez3Desc(
			Long competicion, Long categoria);
	List<JamShowEntity> findByCompeticionAndCategoriaOrderBySumaPVDesc(Long competicion, Long categoria);
	List<JamShowEntity> findByCompeticionAndCategoriaOrderBySumaPonderadaDesc(Long competicion, Long categoria);
		
}
