package freewill.nextgen.blts.daos;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.CheckinEntity;

public interface CheckinRepository extends CrudRepository<CheckinEntity, Long> {

	CheckinEntity findById(Long id);
	List<CheckinEntity> findByCompany(Long id);
	List<CheckinEntity> findByCompanyAndTeacher(Long id, String teacher);
	
	@Query(value = "select e from CheckinEntity e where e.company=?1"
			+ " and e.location = ?2"
			+ " and e.created between ?3 and ?4"
			+ " order by e.created asc")
	List<CheckinEntity> findByTimestampBetween(Long company, String location, Date sdate, Date edate);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM CheckinEntity m")
	Long getMaxId();
	
}
