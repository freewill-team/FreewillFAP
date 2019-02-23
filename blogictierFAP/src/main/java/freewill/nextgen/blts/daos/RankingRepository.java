package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.RankingEntity;

public interface RankingRepository extends CrudRepository<RankingEntity, Long> {
	
	RankingEntity findById(Long id);
	List<RankingEntity> findByCompany(Long id);
	List<RankingEntity> findByCircuitoAndCategoriaOrderByPuntuacionDesc(
			Long circuito, Long categoria);
	RankingEntity findByPatinadorAndCircuitoAndCategoria(
			Long patinador, Long circuito, Long categoria);
	
	@Modifying
	@Query("DELETE FROM RankingEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM RankingEntity m")
	Long getMaxId();
	long countByCircuitoAndCategoria(Long circuito, Long categoria);
	
	/* To be deleted
	default int getSortedRanking(Long patinador, Long circuito, Long categoria, Long circuitoUltimoAnno){
		Random numRandom = new Random();
		RankingEntity ranking = findByPatinadorAndCircuitoAndCategoria(patinador, circuito, categoria);
		if(ranking!=null)
			return 10000 - ranking.getPuntuacion();
		else{
			if(circuitoUltimoAnno!=null)
				ranking = findByPatinadorAndCircuitoAndCategoria(patinador, circuitoUltimoAnno, categoria);
			if(ranking!=null)
				return 10000 - ranking.getPuntuacion();
			else
				return 20000 - numRandom.nextInt(500);
		}
	}*/
	
}
