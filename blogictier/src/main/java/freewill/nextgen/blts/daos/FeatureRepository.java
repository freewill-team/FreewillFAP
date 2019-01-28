package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.FeatureEntity;

public interface FeatureRepository extends CrudRepository<FeatureEntity, Long> {

	FeatureEntity findById(Long id);
	List<FeatureEntity> findByCompany(Long id);
	List<FeatureEntity> findByCompanyAndProductOrderByIdAsc(Long id, Long prd);
	List<FeatureEntity> findByProductAndParentOrderByIdAsc(Long prj, Long parent);
	List<FeatureEntity> findByParentOrderByIdAsc(Long parent);
	List<FeatureEntity> findByProductAndTitleLikeAndActiveOrderByIdAsc(Long prd, String filtter, boolean active);
	List<FeatureEntity> findByProductAndDescriptionLikeAndActiveOrderByIdAsc(Long prd, String filter, boolean active);
	List<FeatureEntity> findByCompanyAndTitleLikeAndActiveOrderByIdAsc(Long company, String filter, boolean active);
	List<FeatureEntity> findByCompanyAndDescriptionLikeAndActiveOrderByIdAsc(Long company, String filter, boolean active);
	
	@Modifying
	@Query("DELETE FROM FeatureEntity u where u.product = ?1")
	void deleteByProduct(Long prd);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM FeatureEntity m")
	Long getMaxId();
	Long countByCompanyAndActive(Long company, boolean active);
	long countByCompanyAndProduct(Long company, Long project);
	
}
