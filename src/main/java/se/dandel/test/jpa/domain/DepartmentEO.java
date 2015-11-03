package se.dandel.test.jpa.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name = "department")
public class DepartmentEO {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "DEPARTMENT_SEQUENCE", sequenceName = "department_sequence")
	@GeneratedValue(generator = "DEPARTMENT_SEQUENCE")
	private Long id;

	@Column(unique = true)
	private String name;

	@OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<EmployeeEO> employees = new ArrayList<EmployeeEO>();

	@OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<AgendaEO> agendas = new ArrayList<AgendaEO>();

	@OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<ResponsibilityEO> responsibilities = new ArrayList<ResponsibilityEO>();

	@Version
	private long version;

	public long getVersion() {
		return version;
	}

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
		return new ArrayList<EmployeeEO>(employees);
	}

	public void addEmployee(EmployeeEO employee) {
		employees.add(employee);
	}

	public void addAgenda(AgendaEO agenda) {
		agendas.add(agenda);
	}

	public void removeAgenda(AgendaEO agenda) {
		agendas.remove(agenda);
	}

	public List<AgendaEO> getAgendas() {
		return Collections.unmodifiableList(agendas);
	}

	public List<ResponsibilityEO> getResponsibilities() {
		return responsibilities;
	}

	public void setResponsibilities(List<ResponsibilityEO> responsibilities) {
		this.responsibilities = responsibilities;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
