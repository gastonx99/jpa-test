package se.dandel.test.jpa.car.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name = "car")
public class CarEO {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "CAR_SEQUENCE", sequenceName = "car_sequence")
	@GeneratedValue(generator = "CAR_SEQUENCE")
	private Long id;

	@Column(unique = true)
	private String name;

	@Version
	private long version;

	public long getVersion() {
		return version;
	}

	public CarEO() {
		super();
	}

	public CarEO(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
