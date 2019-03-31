package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.CategoriaEntity;
import freewill.nextgen.blts.data.CategoriaEntity.ModalidadEnum;

public interface CategoriaRepository extends CrudRepository<CategoriaEntity, Long> {

	CategoriaEntity findById(Long id);
	List<CategoriaEntity> findByCompany(Long id);
	CategoriaEntity findByNombreAndCompany(String nombre, Long company);
	List<CategoriaEntity> findByModalidadAndCompany(ModalidadEnum modalidad, Long comany);
	
	@Modifying
	@Query("DELETE FROM CategoriaEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM CategoriaEntity m")
	Long getMaxId();
	List<CategoriaEntity> findByCompanyOrderByNombreAsc(Long company);
	List<CategoriaEntity> findByModalidadAndCompanyAndActive(ModalidadEnum modalidad, Long company, boolean b);
	List<CategoriaEntity> findByCompanyOrderByEdadMinimaAsc(Long company);
	
}
