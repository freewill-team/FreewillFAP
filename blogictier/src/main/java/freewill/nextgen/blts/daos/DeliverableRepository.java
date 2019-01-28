package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.DeliverableEntity;

public interface DeliverableRepository extends CrudRepository<DeliverableEntity, Long> {

	DeliverableEntity findById(Long id);
	List<DeliverableEntity> findByCompany(Long id);
	List<DeliverableEntity> findByCompanyAndProject(Long id, Long prj);
	
	@Modifying
	@Query("DELETE FROM DeliverableEntity u where u.project = ?1")
	void deleteByProject(Long prd);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM DeliverableEntity m")
	Long getMaxId();
	
}
