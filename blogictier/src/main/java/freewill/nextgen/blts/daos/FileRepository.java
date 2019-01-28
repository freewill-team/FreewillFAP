package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.FileEntity;

public interface FileRepository extends CrudRepository<FileEntity, Long> {

	FileEntity findById(Long id);
	List<FileEntity> findByCompany(Long id);
	List<FileEntity> findByCompanyAndProject(Long id, Long prj);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM FileEntity m")
	Long getMaxId();
	
}
