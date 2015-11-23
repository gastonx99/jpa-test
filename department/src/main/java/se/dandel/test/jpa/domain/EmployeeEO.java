package se.dandel.test.jpa.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name = "employee")
public class EmployeeEO {
	@Id
	@SequenceGenerator(allocationSize = 1, name = "EMPLOYEE_SEQUENCE", sequenceName = "employee_sequence")
	@GeneratedValue(generator = "EMPLOYEE_SEQUENCE")
	private Long id;

	private String name;

	@ManyToOne
	private DepartmentEO department;

	public EmployeeEO() {
	}

	public EmployeeEO(String name) {
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

	public DepartmentEO getDepartment() {
		return department;
	}

	public void setDepartment(DepartmentEO department) {
		this.department = department;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
