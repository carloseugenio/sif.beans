package org.sif.beans;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class ClassesTest {

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
}