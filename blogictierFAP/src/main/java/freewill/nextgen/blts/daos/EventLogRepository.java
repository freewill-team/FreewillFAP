package freewill.nextgen.blts.daos;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.entities.EventEntity;

public interface EventLogRepository extends CrudRepository<EventEntity, Long> {

	@Query("select e from EventEntity e where e.company=?1 and e.timestamp between ?2 and ?3")
	List<EventEntity> findByTimestampBetween(Long company, Date sdate, Date edate);
	
	@Query(value = "select e from EventEntity e where e.company=?1 and e.timestamp between ?2 and ?3"
			+" order by e.timestamp desc")
		    //countQuery = "select count(e.id) from EventEntity e where e.company=?1 and e.timestamp between ?2 and ?3",
		    //nativeQuery = false)
	List<EventEntity> findByTimestampBetween(Long company, Date sdate, Date edate, Pageable pageable);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM EventEntity m")
	Long getMaxId();
	
}
