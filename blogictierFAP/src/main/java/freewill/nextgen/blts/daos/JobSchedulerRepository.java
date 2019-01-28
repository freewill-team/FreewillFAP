package freewill.nextgen.blts.daos;

import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.entities.JobScheduled;

public interface JobSchedulerRepository extends CrudRepository<JobScheduled, Long> {

	JobScheduled findById(Long id);
	
}
