package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.ConfigEntity;
import freewill.nextgen.blts.data.ConfigEntity.ConfigItemEnum;

public interface ConfigRepository extends CrudRepository<ConfigEntity, Long> {

	ConfigEntity findById(Long id);
	
	@Modifying
	@Query("DELETE FROM ConfigEntity u where u.company = ?1")
	void deleteByCompany(Long company);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM ConfigEntity m")
	Long getMaxId();
	
	ConfigEntity findByNameAndCompany(ConfigItemEnum name, Long company);
	List<ConfigEntity> findByCompany(Long company);
	
	default int getConfigInteger(ConfigItemEnum name, Long company){
		try{
			ConfigEntity config = this.findByNameAndCompany(name, company);
			return Integer.parseInt(config.getValue());
		}
		catch(Exception e){
			return Integer.parseInt(name.defaultVal());
		}
	}
	
	default long getConfigLong(ConfigItemEnum name, Long company){
		try{
			ConfigEntity config = this.findByNameAndCompany(name, company);
			return Long.parseLong(config.getValue());
		}
		catch(Exception e){
			return Long.parseLong(name.defaultVal());
		}
	}
	
	default String getConfigString(ConfigItemEnum name, Long company){
		try{
			ConfigEntity config = this.findByNameAndCompany(name, company);
			return config.getValue();
		}
		catch(Exception e){
			return name.defaultVal();
		}
	}
	
	default float getConfigFloat(ConfigItemEnum name, Long company){
		try{
			ConfigEntity config = this.findByNameAndCompany(name, company);
			return Float.parseFloat(config.getValue());
		}
		catch(Exception e){
			return Float.parseFloat(name.defaultVal());
		}
	}
	
	default boolean getConfigBoolean(ConfigItemEnum name, Long company){
		try{
			ConfigEntity config = this.findByNameAndCompany(name, company);
			return Boolean.parseBoolean(config.getValue());
		}
		catch(Exception e){
			return Boolean.parseBoolean(name.defaultVal());
		}
	}
	
}
