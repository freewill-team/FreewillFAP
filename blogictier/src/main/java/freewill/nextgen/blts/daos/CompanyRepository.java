package freewill.nextgen.blts.daos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.CompanyEntity;

public interface CompanyRepository extends CrudRepository<CompanyEntity, Long> {

	CompanyEntity findById(Long id);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM CompanyEntity m")
	Long getMaxId();
	
}
