package se.dandel.test.jpa.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "department")
public class DepartmentEO {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST)
	private List<EmployeeEO> employees = new ArrayList<EmployeeEO>();

	public DepartmentEO() {
		super();
	}

	public DepartmentEO(String name) {
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

	public List<EmployeeEO> getEmployees() {
		return employees;
	}

	public void setEmployees(List<EmployeeEO> employees) {
		this.employees = employees;
	}
}