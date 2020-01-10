package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.InscripcionEntity;

public interface InscripcionRepository extends CrudRepository<InscripcionEntity, Long> {

	InscripcionEntity findById(Long id);
	List<InscripcionEntity> findByCompany(Long id);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM CompanyEntity m")
	Long getMaxId();
	InscripcionEntity findByClubAndCompeticion(Long club, Long competicion);
	List<InscripcionEntity> findByEnviado(boolean enviado);
	
}
