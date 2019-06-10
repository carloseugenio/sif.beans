package org.sif.beans;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class StreamTest {

	Logger log = LoggerFactory.getLogger(getClass());

	public static final String EMP_1 = "emp1";
	List<Employee> employees = new ArrayList<>();

	@Before
	public void setup() {
		Employee emp1 = new Employee();
		emp1.setName(EMP_1);
		employees.add(emp1);
		Employee emp2 = new Employee();
		emp2.setName("emp2");
		employees.add(emp2);

	}
	@Test
	public void testStreamSize() {
		Stream<Employee> stream = employees.stream();
		assertEquals(2, employees.size());
		assertEquals(2, stream.count());
	}

	@Test
	public void  testFindFirst() {
		Stream<Employee> stream = employees.stream();
		Stream<Employee> filtered = stream.filter(e -> EMP_1.equals(e.getName()));
		Employee employee = filtered.findFirst().get();
		assertEquals(EMP_1, employee.getName());
	}

	@Test
	public void  testFindFirstNonExistent() {
		Stream<Employee> stream = employees.stream();
		Stream<Employee> filtered = stream.filter(e -> {
			log.debug("Testing: {}", e);
			return "empX".equals(e.getName());
		});
		Employee employee = filtered.findFirst().orElseGet(()-> new Employee());
		assertEquals(null, employee.getName());
	}

}
