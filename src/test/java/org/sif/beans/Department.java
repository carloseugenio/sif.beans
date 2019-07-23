package org.sif.beans;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Department implements Serializable {

	private static final long serialVersionUID = 1L;

	@Version
	private Long versionNumber;

	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(targetEntity = Employee.class)
	private Set<Employee> employeelist = new HashSet<>();

	@ManyToOne
	private Employee coordinator;

	private String name;

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

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (this == other) {
			return true;
		}
		if (!(other instanceof Department)) {
			return false;
		}

		final Department department = (Department) other;

		return new EqualsBuilder()
				.appendSuper(super.equals(other))
				.append(name, department.name)
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 37).
				appendSuper(37).
				append(name).
				toHashCode();
	}

	public Set<Employee> getEmployeelist() {
		return employeelist;
	}

	public void setEmployeelist(Set<Employee> employeelist) {
		this.employeelist = employeelist;
	}

	public Employee getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(Employee coordinator) {
		this.coordinator = coordinator;
	}
}