package se.dandel.test.jpa.department.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name = "agenda")
public class AgendaEO {

	@EmbeddedId
	private AgendaPk pk;

	@MapsId("departmentId")
	@ManyToOne
	@PrimaryKeyJoinColumn
	private DepartmentEO department;

	@SuppressWarnings("unused")
	private AgendaEO() {

	}

	public AgendaEO(String name, DepartmentEO department) {
		this.pk = new AgendaPk(department.getId(), name);
		this.department = department;
	}

	public String getName() {
		return pk.getName();
	}

	public DepartmentEO getDepartment() {
		return department;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
