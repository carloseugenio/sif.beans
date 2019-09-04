package org.sif.beans;

import org.junit.Test;

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

	@Test(expected = IllegalArgumentException.class)
	public void testGetPropertyIgnoreNullNonExistent() {
		Classes.getPropertyIgnoreNull(new Employee(), "other");
	}

}