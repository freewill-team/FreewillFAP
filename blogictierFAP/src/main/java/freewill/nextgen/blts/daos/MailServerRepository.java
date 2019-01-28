package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.entities.MailServerEntity;

public interface MailServerRepository extends CrudRepository<MailServerEntity, Long> {

	MailServerEntity findById(Long id);
	List<MailServerEntity> findByCompany(Long company);
	
}
