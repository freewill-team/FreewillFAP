package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import freewill.nextgen.blts.data.DerrapesEntity;

public interface DerrapesRepository extends CrudRepository<DerrapesEntity, Long> {

	DerrapesEntity findById(Long id);
	List<DerrapesEntity> findByCompany(Long id);
	List<DerrapesEntity> findByPatinador(Long recId);
	DerrapesEntity findByPatinadorAndCompeticion(Long patinador, Long competicion);
	List<DerrapesEntity> findByCompeticionAndCategoriaOrderByClasificacionAsc(
			Long competicion, Long categoria);
	DerrapesEntity findByCompeticionAndCategoriaAndClasificacion(
			Long competicion, Long categoria, int clasificacion);
	List<DerrapesEntity> findByCompeticionAndCategoriaOrderByOrdenAsc(
			Long competicion, Long categoria);
	DerrapesEntity findByCompeticionAndCategoriaAndOrden(
			Long competicion, Long categoria, int orden);
	
	@Modifying
	@Query("DELETE FROM DerrapesEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	@Transactional
	@Modifying
	@Query("DELETE FROM DerrapesEntity u WHERE u.competicion = ?1 AND u.categoria = ?2")
	void deleteByCompeticionAndCategoria(Long competicion, Long categoria);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM DerrapesEntity m")
	Long getMaxId();
		
}
