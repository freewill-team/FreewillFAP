package freewill.nextgen.blts.daos;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.SaltoIntentoEntity;

public interface SaltoIntentoRepository extends CrudRepository<SaltoIntentoEntity, Long> {

	SaltoIntentoEntity findById(Long id);
	
	@Modifying
	@Query("DELETE FROM SaltoIntentoEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM SaltoIntentoEntity m")
	Long getMaxId();
	
	SaltoIntentoEntity findByRondaAndSaltoPatinadorId(int ronda, Long saltoid);
		
}
