package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.CheckinStudentEntity;

public interface CheckinStudentRepository extends CrudRepository<CheckinStudentEntity, Long> {

	CheckinStudentEntity findById(Long id);
	List<CheckinStudentEntity> findByCompany(Long id);
	
	@Modifying
	@Query("DELETE FROM CheckinStudentEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM CheckinStudentEntity m")
	Long getMaxId();
	long countByCompanyAndActive(Long company, boolean active);
	
}
