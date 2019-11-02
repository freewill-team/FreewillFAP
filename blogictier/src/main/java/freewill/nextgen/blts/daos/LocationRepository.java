package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.LocationEntity;

public interface LocationRepository extends CrudRepository<LocationEntity, Long> {

	LocationEntity findById(Long id);
	List<LocationEntity> findByCompany(Long id);
	
	@Modifying
	@Query("DELETE FROM LocationEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM LocationEntity m")
	Long getMaxId();
	long countByCompanyAndActive(Long company, boolean active);
	
}
