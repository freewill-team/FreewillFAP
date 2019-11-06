package freewill.nextgen.blts.daos;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.PaymentEntity;

public interface PaymentRepository extends CrudRepository<PaymentEntity, Long> {

	PaymentEntity findById(Long id);
	List<PaymentEntity> findByCompany(Long id);
	List<PaymentEntity> findByCompanyAndStudent(Long id, String student);
	
	@Query(value = "select e from PaymentEntity e where e.company=?1"
			+ " and e.created between ?2 and ?3"
			+ " order by e.created asc")
	List<PaymentEntity> findByTimestampBetween(Long company, Date sdate, Date edate);
	
	@Query(value = "select e from PaymentEntity e where e.company=?1"
			+ " and e.student = ?2"
			+ " and e.created between ?3 and ?4"
			+ " order by e.created asc")
	List<PaymentEntity> findByStudentAndTimestampBetween(Long company, String student, Date sdate, Date edate);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM PaymentEntity m")
	Long getMaxId();
	
}
