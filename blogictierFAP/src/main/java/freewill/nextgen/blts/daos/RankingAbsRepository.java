package freewill.nextgen.blts.daos;

import java.util.List;
import java.util.Random;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.blts.data.RankingAbsEntity;

public interface RankingAbsRepository extends CrudRepository<RankingAbsEntity, Long> {
	
	RankingAbsEntity findById(Long id);
	List<RankingAbsEntity> findByCompany(Long id);
	List<RankingAbsEntity> findByModalidadAndCompanyOrderByPuntuacionDesc(
			ModalidadEnum modalidad, Long company);
	RankingAbsEntity findByPatinadorAndModalidad(
			Long patinador, ModalidadEnum modalidad);
	
	@Modifying
	@Query("DELETE FROM RankingAbsEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM RankingAbsEntity m")
	Long getMaxId();
	
	default int getSortedRanking(Long patinador, ModalidadEnum modalidad){
		Random numRandom = new Random();
		RankingAbsEntity ranking = findByPatinadorAndModalidad(patinador, modalidad);
		if(ranking!=null)
			return 10000 - ranking.getPuntuacion();
		else
			return 20000 - numRandom.nextInt(500);
	}
	
}
