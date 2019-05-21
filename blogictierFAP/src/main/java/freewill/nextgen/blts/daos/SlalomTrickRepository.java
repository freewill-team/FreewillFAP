package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.SlalomTrickEntity;
import freewill.nextgen.blts.data.SlalomTrickEntity.TrickFamilyEnum;

public interface SlalomTrickRepository extends CrudRepository<SlalomTrickEntity, Long> {

	SlalomTrickEntity findById(Long id);
	
	@Modifying
	@Query("DELETE FROM SlalomTrickEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM SlalomTrickEntity m")
	Long getMaxId();
	
	List<SlalomTrickEntity> findByFamiliaAndCompany(TrickFamilyEnum family, Long company);
	SlalomTrickEntity findByNombreAndCompany(String nombre, Long company);
	List<SlalomTrickEntity> findByCompany(Long company);
	
}
