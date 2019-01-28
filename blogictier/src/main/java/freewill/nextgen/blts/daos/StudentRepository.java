package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.StudentEntity;

public interface StudentRepository extends CrudRepository<StudentEntity, Long> {

	StudentEntity findById(Long id);
	List<StudentEntity> findByCompany(Long id);
	
	@Modifying
	@Query("DELETE FROM StudentEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM StudentEntity m")
	Long getMaxId();
	long countByCompanyAndActive(Long company, boolean active);
	
}
