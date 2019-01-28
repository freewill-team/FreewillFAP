package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

	UserEntity findById(Long id);
	UserEntity findByLoginname(String login);
	UserEntity findByLoginnameAndActive(String login, boolean active);
	List<UserEntity> findByCompany(Long id);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM UserEntity m")
	Long getMaxId();
	Long countByCompanyAndActive(Long company, boolean active);
	
}
