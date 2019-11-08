package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class PaymentEntity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "PAYMENTENTITY")
@EntityListeners(BltAuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentEntity implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private Date created;			// Fecha creación
	private String student;			// Event student
	private Double amount;			// Event payment
	private Long company;			// Company Id
	private Long student_id;		// Student ID
	
    /**
     * Default constructor. 
     */
	public PaymentEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	created = new Date();
    	student = "";
    	amount = 0.0;
    }
    
    public String toString()
    {
    	return id+"/"+student+"/"+amount;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof PaymentEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((PaymentEntity) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 41 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public PaymentEntity clone() throws CloneNotSupportedException {
		return (PaymentEntity) super.clone();
	}
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
    }

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getStudent() {
		return student;
	}

	public void setStudent(String student) {
		this.student = student;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getStudent_id() {
		if(student_id==null) student_id = 0L;
		return student_id;
	}

	public void setStudent_id(Long student_id) {
		this.student_id = student_id;
	}
	
}
