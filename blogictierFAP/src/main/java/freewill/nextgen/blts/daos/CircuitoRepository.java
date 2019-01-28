package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.CircuitoEntity;

public interface CircuitoRepository extends CrudRepository<CircuitoEntity, Long> {

	CircuitoEntity findById(Long id);
	List<CircuitoEntity> findByCompany(Long id);
	CircuitoEntity findByTemporada(Integer year);
	
	@Modifying
	@Query("DELETE FROM CircuitoEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM CircuitoEntity m")
	Long getMaxId();
	List<CircuitoEntity> findByCompanyOrderByTemporadaDesc(Long company);
	
}
