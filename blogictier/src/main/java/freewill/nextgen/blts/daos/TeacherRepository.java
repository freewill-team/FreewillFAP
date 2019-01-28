package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.TeacherEntity;

public interface TeacherRepository extends CrudRepository<TeacherEntity, Long> {

	TeacherEntity findById(Long id);
	List<TeacherEntity> findByCompany(Long id);
	
	@Modifying
	@Query("DELETE FROM TeacherEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM TeacherEntity m")
	Long getMaxId();
	long countByCompanyAndActive(Long company, boolean active);
	
}
