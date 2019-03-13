package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.PatinadorEntity;

public interface PatinadorRepository extends CrudRepository<PatinadorEntity, Long> {

	PatinadorEntity findById(Long id);
	List<PatinadorEntity> findByCompany(Long id);
	List<PatinadorEntity> findByCompanyAndActive(Long id, boolean estado);
	
	@Modifying
	@Query("DELETE FROM PatinadorEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM PatinadorEntity m")
	Long getMaxId();
	long countByCompanyAndActive(Long company, boolean active);
	List<PatinadorEntity> findByCompanyOrderByNombreAsc(Long company);
	List<PatinadorEntity> findByCompanyAndClubOrderByNombreAsc(
			Long company, Long club);
	List<PatinadorEntity> findByCompanyAndClubAndActiveOrderByNombreAsc(
			Long company, Long club, boolean active);
	
}
