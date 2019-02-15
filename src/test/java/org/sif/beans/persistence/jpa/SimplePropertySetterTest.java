package org.sif.beans.persistence.jpa;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.sif.beans.Employee;

import static org.junit.Assert.*;

public class SimplePropertySetterTest {

	private static final String NAME_TEST = "Name to test";
	SimplePropertySetter setter = new SimplePropertySetter();
	
	@Test
	public void setSimpleStringProperty() {
		Employee employee = new Employee();
		setter.doSetProperty(employee, "name", NAME_TEST);
		assertEquals(NAME_TEST, employee.getName());
	}

	@Test
	public void testSetExistingStringPropertyToEmpty() {
		Employee employee = new Employee();
		employee.setName(NAME_TEST);
		setter.doSetProperty(employee, "name", StringUtils.EMPTY);
		assertEquals(StringUtils.EMPTY, employee.getName());
	}

	@Test
	public void testSetBooleanPropertyToEmpty() {
		Employee employee = new Employee();
		employee.setEmployed(true);
		setter.doSetProperty(employee, "employed", StringUtils.EMPTY);
		assertEquals(true, employee.getEmployed());
	}

}