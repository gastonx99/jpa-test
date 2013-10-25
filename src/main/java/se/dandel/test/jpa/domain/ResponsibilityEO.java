package se.dandel.test.jpa.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name = "responsibility")
public class ResponsibilityEO {

    @Id
    private String name;

    @ManyToOne
    @Id
    @JoinColumn(name = "DEPARTMENT_ID", referencedColumnName = "ID")
    private DepartmentEO department;

    @SuppressWarnings("unused")
    private ResponsibilityEO() {

    }

    public ResponsibilityEO(String name, DepartmentEO department) {
        this.name = name;
        this.department = department;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
