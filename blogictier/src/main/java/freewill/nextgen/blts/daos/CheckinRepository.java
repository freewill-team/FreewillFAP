package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.CheckinEntity;

public interface CheckinRepository extends CrudRepository<CheckinEntity, Long> {

	CheckinEntity findById(Long id);
	List<CheckinEntity> findByCompany(Long id);
	List<CheckinEntity> findByCompanyAndTeacher(Long id, String teacher);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM CheckinEntity m")
	Long getMaxId();
	
}
