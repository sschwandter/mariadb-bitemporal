package at.xion.mariadbbitemporal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity
public class Person {
	private String  name;
	private Long id;
	private Date startTimestamp;
	private Date endTimestamp;

	public void setId(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Date fromTimestamp) {
		this.startTimestamp = fromTimestamp;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(Date toTimestamp) {
		this.endTimestamp = toTimestamp;
	}

	@Override
	public String toString() {
		return "Person{" +
				"name='" + name + '\'' +
				", id=" + id +
				", startTimestamp=" + startTimestamp +
				", endTimestamp=" + endTimestamp +
				'}';
	}
}
