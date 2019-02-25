package freewill.nextgen.blts.daos;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.CompeticionEntity;

public interface CompeticionRepository extends CrudRepository<CompeticionEntity, Long> {

	CompeticionEntity findById(Long id);
	List<CompeticionEntity> findByCompany(Long id);
	List<CompeticionEntity> findByCircuito(Long circuito);
	
	@Modifying
	@Query("DELETE FROM CompeticionEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM CompeticionEntity m")
	Long getMaxId();
	//List<CompeticionEntity> findTop4ByOrderByFechaInicioDesc();
	//List<CompeticionEntity> findTop4ByFechaInicioBeforeOrderByFechaInicioDesc(Date now);
	List<CompeticionEntity> findTop1ByFechaInicioBeforeOrderByFechaInicioDesc(Date now);
	List<CompeticionEntity> findTop4BySpeedAndFechaInicioBeforeOrderByFechaFinDesc(boolean b, Date now);
	List<CompeticionEntity> findTop4ByBattleAndFechaInicioBeforeOrderByFechaFinDesc(boolean b, Date now);
	List<CompeticionEntity> findTop4ByClassicAndFechaInicioBeforeOrderByFechaFinDesc(boolean b, Date now);
	List<CompeticionEntity> findTop4ByJamAndFechaInicioBeforeOrderByFechaFinDesc(boolean b, Date now);
	List<CompeticionEntity> findTop4ByDerrapesAndFechaInicioBeforeOrderByFechaFinDesc(boolean b, Date now);
	List<CompeticionEntity> findTop4BySaltoAndFechaInicioBeforeOrderByFechaFinDesc(boolean b, Date now);
	List<CompeticionEntity> findByCompanyOrderByFechaInicioDesc(Long company);
	List<CompeticionEntity> findByCircuitoOrderByFechaInicioDesc(Long recId);
	List<CompeticionEntity> findTop4ByCircuitoAndSpeedAndFechaInicioBeforeOrderByFechaFinDesc(Long circuito, boolean b, Date now);
	List<CompeticionEntity> findTop4ByCircuitoAndBattleAndFechaInicioBeforeOrderByFechaFinDesc(Long circuito, boolean b, Date now);
	List<CompeticionEntity> findTop4ByCircuitoAndClassicAndFechaInicioBeforeOrderByFechaFinDesc(Long circuito, boolean b, Date now);
	List<CompeticionEntity> findTop4ByCircuitoAndJamAndFechaInicioBeforeOrderByFechaFinDesc(Long circuito, boolean b, Date now);
	List<CompeticionEntity> findTop4ByCircuitoAndDerrapesAndFechaInicioBeforeOrderByFechaFinDesc(Long circuito, boolean b, Date now);
	List<CompeticionEntity> findTop4ByCircuitoAndSaltoAndFechaInicioBeforeOrderByFechaFinDesc(Long circuito, boolean b, Date now);
	
}
