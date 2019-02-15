package org.sif.beans.persistence.jpa;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.sif.beans.Employee;
import org.sif.beans.PropertyValueConverterUtil;

import static org.junit.Assert.*;

public class SimplePropertySetterTest {

	private static final String NAME_TEST = "Name to test";
	PropertyValueConverterUtil converterUtil = new PropertyValueConverterUtil();
	SimplePropertySetter setter = new SimplePropertySetter();

	@Before
	public void setup() {
		setter.converterUtil = converterUtil;
	}

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
	public void testSetExistingLongPropertyToEmpty() {
		Employee employee = new Employee();
		employee.setId(1L);
		setter.doSetProperty(employee, "id", StringUtils.EMPTY);
		assertEquals(new Long(1L), employee.getId());
	}

	@Test
	public void testSetBooleanPropertyToEmpty() {
		Employee employee = new Employee();
		employee.setEmployed(true);
		setter.doSetProperty(employee, "employed", StringUtils.EMPTY);
		assertEquals(true, employee.getEmployed());
	}

}