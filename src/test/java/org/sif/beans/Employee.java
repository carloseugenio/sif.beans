package org.sif.beans;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name="department",
			referencedColumnName="id",
			insertable=true,
			updatable=true,
			nullable=true,
			unique=false)
	private Department department;

	@Basic(optional = true)
	// The column for this attribute
	@Column(
			name="name",
			insertable=true,
			updatable=true,
			nullable=true,
			unique=false)
	private String name;

	@Basic(optional = true)
	@Column(name = "age", insertable = true, updatable = true, nullable = true, unique = false)
	/** Declaration of Attribute idade */
	private Integer age;


	@OneToMany(targetEntity=Employee.class)
	private Set<Employee> friends = new HashSet<>();

	@OneToMany(mappedBy="coordinator", targetEntity=Department.class)
	private Set<Department> departments = new HashSet<>();

	@Basic(optional = true)
	private Boolean employed;

	@OneToOne
	private Address address;

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

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}


	public Set<Employee> getFriends() {
		return friends;
	}

	public void setFriends(Set<Employee> friends) {
		this.friends = friends;
	}

	public Set<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(Set<Department> departments) {
		this.departments = departments;
	}

	@Override
	public String toString() {
		return "Employee{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}

	public Boolean getEmployed() {
		return employed;
	}

	public void setEmployed(Boolean employed) {
		this.employed = employed;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Employee employee = (Employee) o;
		return Objects.equals(id, employee.id) && Objects.equals(department, employee.department) && Objects
				.equals(name, employee.name) && Objects.equals(age, employee.age) && Objects
				.equals(friends, employee.friends) && Objects.equals(departments, employee.departments) && Objects
				.equals(employed, employee.employed);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, department, name, age, friends, departments, employed);
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
