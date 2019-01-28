package freewill.nextgen.blts.daos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.ConfigEntity;

public interface ConfigRepository extends CrudRepository<ConfigEntity, Long> {

	ConfigEntity findById(Long id);
	ConfigEntity findByName(String name);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM ConfigEntity m")
	Long getMaxId();
	
}
