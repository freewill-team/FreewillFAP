package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.ParejaJamEntity;

public interface ParejaJamRepository extends CrudRepository<ParejaJamEntity, Long> {

	ParejaJamEntity findById(Long id);
	List<ParejaJamEntity> findByCompany(Long id);
	
	@Modifying
	@Query("DELETE FROM ParejaJamEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM ParejaJamEntity m")
	Long getMaxId();
	
}
