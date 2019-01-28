package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.SaltoEntity;

public interface SaltoRepository extends CrudRepository<SaltoEntity, Long> {

	SaltoEntity findById(Long id);
	List<SaltoEntity> findByCompany(Long id);
	List<SaltoEntity> findByPatinador(Long recId);
	
	@Modifying
	@Query("DELETE FROM SaltoEntity u WHERE u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM SaltoEntity m")
	Long getMaxId();
	List<SaltoEntity> findByPatinadorAndCompeticion(Long patinador, Long competicion);
	List<SaltoEntity> findByCompeticionAndCategoriaOrderByOrdenAsc(Long competicion, Long categoria);
	List<SaltoEntity> findByCompeticionAndCategoriaOrderByClasificacionAsc(Long competicion, Long categoria);
		
}
