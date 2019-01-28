package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.ParticipanteEntity;

public interface ParticipanteRepository extends CrudRepository<ParticipanteEntity, Long> {

	ParticipanteEntity findById(Long id);
	List<ParticipanteEntity> findByCompany(Long id);
	List<ParticipanteEntity> findByPatinador(Long recId);
	List<ParticipanteEntity> findByPatinadorAndCompeticion(Long patinador, Long competicion);
	ParticipanteEntity findByPatinadorAndCategoriaAndCompeticion(
			Long patinador, Long categoria, Long competicion);
	List<ParticipanteEntity> findByCompeticionAndCategoria(Long competicion, Long catmodalidad);
	List<ParticipanteEntity> findByCircuitoAndCategoria(Long circuito, Long categoria);
	
	@Modifying
	@Query("DELETE FROM ParticipanteEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM ParticipanteEntity m")
	Long getMaxId();
	Long countByCompeticionAndCategoria(Long competicion, Long categoria);
	List<ParticipanteEntity> findByCategoriaAndCompeticionIn(Long categoria, List<Long> competis);
	List<ParticipanteEntity> findByCompeticionAndCategoriaOrderByClasificacion(
			Long competicion, Long categoria);
	List<ParticipanteEntity> findByPatinadorParejaAndCompeticion(Long id, Long competicion); // Solo valido para Jam
	List<ParticipanteEntity> findByDorsalAndCompeticion(int dorsal, Long competicion);
	List<ParticipanteEntity> findByDorsalParejaAndCompeticion(int dorsal, Long competicion);
	ParticipanteEntity findByPatinadorParejaAndCategoriaAndCompeticion(Long patId, Long categoria, Long competicion);
	List<ParticipanteEntity> findByCompeticion(Long competicion);
	
}
