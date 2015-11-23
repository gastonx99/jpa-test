package se.dandel.test.jpa.department.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ResponsibilityPk implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long departmentId;

	private String name;

	public ResponsibilityPk() {
	}

	public ResponsibilityPk(Long departmentId, String name) {
		this.departmentId = departmentId;
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((departmentId == null) ? 0 : departmentId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponsibilityPk other = (ResponsibilityPk) obj;
		if (departmentId == null) {
			if (other.departmentId != null)
				return false;
		} else if (!departmentId.equals(other.departmentId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public String getName() {
		return name;
	}

}
