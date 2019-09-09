package org.sif.beans;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.sif.beans.Classes.classFor;

public class ClassesTest {

	@Test
	public void testClassForNull() {
		assertNull(classFor(null));
	}

	@Test
	public void testClasssFor() {
		assertEquals(String.class, classFor(""));
	}

	@Test
	public void testTypeFor() {

	}

	@Test
	public void testGetPropertyNames() {
		Collection<String> names = Classes.getPropertyNames(new Employee());
		assertTrue(names.contains("name"));
		assertTrue(names.contains("address"));
		assertFalse(names.contains("other"));
	}

	@Test
	public void testGetPropertyIgnoreNull() {
		Object friends = Classes.getPropertyIgnoreNull(new Employee(), "friends");
		assertNotNull(friends);
		Object coordinator = Classes.getPropertyIgnoreNull(new Employee(), "department.coordinator");
		assertNull(coordinator);
	}

	@Test
	public void testGetNestedField() {
		Field coordinator = Classes.getField(Employee.class, "department.coordinator");
		assertNotNull(coordinator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPropertyIgnoreNullNonExistent() {
		Classes.getPropertyIgnoreNull(new Employee(), "other");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFieldNonExistent() {
		Classes.getField(Employee.class, "other");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFieldClassNonExistent() {
		Classes.getFieldClass(Employee.class, "other");
	}

	@Test
	public void isNested() {
		assertTrue(Classes.isNested("a.b"));
		assertTrue(Classes.isNested("a.b.c"));
		assertFalse(Classes.isNested("a"));
		assertFalse(Classes.isNested(""));
		assertFalse(Classes.isNested(null));
	}
}