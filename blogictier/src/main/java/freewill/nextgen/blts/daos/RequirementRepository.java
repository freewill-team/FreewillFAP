package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.Requirement2Entity;
import freewill.nextgen.blts.data.RequirementEntity;

public interface RequirementRepository extends CrudRepository<RequirementEntity, Long> {

	RequirementEntity findById(Long id);
	List<RequirementEntity> findByCompany(Long company);
	List<RequirementEntity> findByCompanyAndProjectOrderByCustomidAsc(Long company, Long prj);
	List<RequirementEntity> findByProjectOrderByCustomidAsc(Long prj);
	List<RequirementEntity> findByAssignedtoAndProjectOrderByCustomidAsc(Long user, Long recId);
	
	@Modifying
	@Query("DELETE FROM RequirementEntity u where u.project = ?1")
	void deleteByProject(Long prj);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM RequirementEntity m")
	Long getMaxId();
	Long countByCompanyAndResolved(Long company, boolean b);
	
	@Query("SELECT m.id as id, m.customid as customid, m.description as description, m.resolved as resolved, p.name as user, m.company as company "
			+ "FROM RequirementEntity as m, UserEntity as p "
			+ "WHERE (m.assignedto=p.id) "
			+ "AND m.project=?1 ORDER BY m.customid ASC")
	List<Requirement2Entity> findByProjectWithUser(Long prj);
	@Query("SELECT m.id as id, m.customid as customid, m.description as description, m.resolved as resolved, p.name as user, m.company as company "
			+ "FROM RequirementEntity as m, UserEntity as p "
			+ "WHERE (m.assignedto is Null OR m.assignedto=0) "
			+ "AND m.project=?1 ORDER BY m.customid ASC")
	List<Requirement2Entity> findByProjectWithNoUser(Long prj);
	
}
