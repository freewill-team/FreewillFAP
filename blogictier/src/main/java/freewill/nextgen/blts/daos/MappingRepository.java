package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.MappingEntity;

public interface MappingRepository extends CrudRepository<MappingEntity, Long> {

	MappingEntity findById(Long id);
	List<MappingEntity> findByDoc(Long doc);
	MappingEntity findByReq(Long req);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM MappingEntity m")
	Long getMaxId();
	long countByDoc(Long recId);
	
	@Query("SELECT m FROM MappingEntity as m, RequirementEntity as r, FeatureEntity as d "
			+ "WHERE (m.req=r.id AND m.doc=d.id) "
			+ "AND r.project=?1 ORDER BY r.customid ASC")
	List<MappingEntity> findByProject(Long recId);
	
}
