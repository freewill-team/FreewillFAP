package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.ClassicShowEntity;

public interface ClassicShowRepository extends CrudRepository<ClassicShowEntity, Long> {

	ClassicShowEntity findById(Long id);
	List<ClassicShowEntity> findByCompany(Long id);
	List<ClassicShowEntity> findByPatinador(Long recId);
	ClassicShowEntity findByPatinadorAndCompeticion(Long patinador, Long competicion);
	
	ClassicShowEntity findByCompeticionAndCategoriaAndClasificacionFinal(
			Long competicion, Long categoria, int clasificacionFinal);
	List<ClassicShowEntity> findByCompeticionAndCategoriaOrderByOrden1Asc(
			Long competicion, Long categoria);
	
	@Modifying
	@Query("DELETE FROM ClassicShowEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM ClassicShowEntity m")
	Long getMaxId();
	
	List<ClassicShowEntity> findByCompeticionAndCategoriaOrderByClasificacionFinalAsc(
			Long competicion, Long categoria);
	ClassicShowEntity findByCompeticionAndCategoriaAndOrden1(
			Long competicion, Long categoria, int i);
	List<ClassicShowEntity> findByCompeticionAndCategoriaOrderByTotalJuez1Desc(
			Long competicion, Long categoria);
	List<ClassicShowEntity> findByCompeticionAndCategoriaOrderByTotalJuez2Desc(
			Long competicion, Long categoria);
	List<ClassicShowEntity> findByCompeticionAndCategoriaOrderByTotalJuez3Desc(
			Long competicion, Long categoria);
	List<ClassicShowEntity> findByCompeticionAndCategoriaOrderBySumaPVDesc(Long competicion, Long categoria);
	List<ClassicShowEntity> findByCompeticionAndCategoriaOrderBySumaPonderadaDesc(Long competicion, Long categoria);
		
}
