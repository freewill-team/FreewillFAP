package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.ProductEntity;

public interface ProductRepository extends CrudRepository<ProductEntity, Long> {

	ProductEntity findById(Long id);
	List<ProductEntity> findByCompany(Long id);
	
	@Modifying
	@Query("DELETE FROM ProductEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM ProductEntity m")
	Long getMaxId();
	Long countByCompanyAndActive(Long company, boolean active);
	
}
