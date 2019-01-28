package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.PuntuacionesEntity;

public interface PuntuacionesRepository extends CrudRepository<PuntuacionesEntity, Long> {

	PuntuacionesEntity findById(Long id);
	List<PuntuacionesEntity> findByCompany(Long id);
	
	@Modifying
	@Query("DELETE FROM PuntuacionesEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM PuntuacionesEntity m")
	Long getMaxId();
	PuntuacionesEntity findByClasificacionAndCompany(int clasificacion, Long company);
	List<PuntuacionesEntity> findByCompanyOrderByClasificacionAsc(Long company);
	
}
