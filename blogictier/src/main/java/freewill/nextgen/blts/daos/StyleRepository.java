package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.Style;

public interface StyleRepository extends CrudRepository<Style, Long> {

	Style findById(Long id);
	List<Style> findByCompany(Long company);
	
	@Modifying
	@Query("DELETE FROM Style u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM Style m")
	Long getMaxId();
	
}
