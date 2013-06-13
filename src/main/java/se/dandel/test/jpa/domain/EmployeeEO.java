package se.dandel.test.jpa.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class EmployeeEO {
	@Id
	@GeneratedValue
	private Long id;

	private String name;
	
	@ManyToOne
	private DepartmentEO department;

	public EmployeeEO() {}

	public EmployeeEO(String name, DepartmentEO department) {
		this.name = name;
		this.department = department;
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
		return "Employee [id=" + id + ", name=" + name + ", department="
				+ department.getName() + "]";
	}

}
