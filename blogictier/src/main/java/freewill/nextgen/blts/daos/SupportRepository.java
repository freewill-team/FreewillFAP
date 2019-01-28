package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.SupportEntity;

public interface SupportRepository extends CrudRepository<SupportEntity, Long> {

	SupportEntity findById(Long id);
	List<SupportEntity> findByUserOrderByCreatedDesc(Long user);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM SupportEntity m")
	Long getMaxId();
	Long countByUserAndResolved(Long user, boolean b);
	
}
