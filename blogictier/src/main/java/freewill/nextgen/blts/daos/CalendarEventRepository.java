package freewill.nextgen.blts.daos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import freewill.nextgen.blts.data.CalendarEvent;

public interface CalendarEventRepository extends CrudRepository<CalendarEvent, Long> {

	CalendarEvent findById(Long id);
	List<CalendarEvent> findByCompany(Long id);
	
	@Query("SELECT coalesce(max(m.id), 0) FROM CalendarEvent m")
	Long getMaxId();
	
}
