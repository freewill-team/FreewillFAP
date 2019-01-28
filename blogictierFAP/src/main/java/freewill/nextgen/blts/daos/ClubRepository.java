package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.ClubEntity;

public interface ClubRepository extends CrudRepository<ClubEntity, Long> {

	ClubEntity findById(Long id);
	List<ClubEntity> findByCompany(Long id);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM CompanyEntity m")
	Long getMaxId();
	List<ClubEntity> findByCompanyOrderByNombreAsc(Long company);
	ClubEntity findByCoordinadorAndCompany(String name, Long company);
	
}
