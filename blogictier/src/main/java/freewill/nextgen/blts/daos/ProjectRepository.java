package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.ProjectEntity;

public interface ProjectRepository extends CrudRepository<ProjectEntity, Long> {

	ProjectEntity findById(Long id);
	List<ProjectEntity> findByCompany(Long id);
	
	@Modifying
	@Query("DELETE FROM ProjectEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM ProjectEntity m")
	Long getMaxId();
	long countByCompanyAndActive(Long company, boolean active);
	
}
