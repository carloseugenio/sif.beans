package org.sif.beans;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StreamTest {

	Logger log = LoggerFactory.getLogger(getClass());

	public static final String EMP_1 = "emp1", EMP_2 = "emp2", EMP_3 = "emp3";
	List<Employee> employees = new ArrayList<>();
	
	public static final int SIZE = 4;

	@Before
	public void setup() {
		Employee emp1 = new Employee();
		emp1.setName(EMP_1);
		emp1.setAge(10);
		employees.add(emp1);
		Employee emp2 = new Employee();
		emp2.setName(EMP_2);
		employees.add(emp2);
		Employee emp3 = new Employee();
		emp3.setName(EMP_3);
		employees.add(emp3);
		// Another equal employee
		emp3 = new Employee();
		emp3.setName(EMP_3);
		employees.add(emp3);

		emp1.getFriends().add(emp2);
	}

	@Test
	public void testStreamSize() {
		Stream<Employee> stream = employees.stream();
		assertEquals(SIZE, employees.size());
		assertEquals(SIZE, stream.count());
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

	@Test
	public void testCollectorsToList() {
		Stream<Employee> stream = employees.stream();
		List<Employee> list = stream.collect(Collectors.toList());
		assertEquals(SIZE, list.size());
	}

	@Test
	public void testCollectorsToSet() {
		Stream<Employee> stream = employees.stream();
		Set<Employee> set = stream.collect(Collectors.toSet());
		assertEquals(SIZE - 1, set.size());
	}

	@Test
	public void testCollectorsToLinkedList() {
		Stream<Employee> stream = employees.stream();
		List<Employee> list = stream.collect(Collectors.toCollection(LinkedList::new));
		assertEquals(SIZE, list.size());
	}

	@Test(expected=IllegalStateException.class)
	public void testCollectorsToMap() {
		Stream<Employee> stream = employees.stream();
		// toMap must operate with unique map keys
		stream.collect(Collectors.toMap(e -> e.getName(), e -> e));
	}

	@Test
	public void testCollectorsToMapWithBinaryOperator() {
		Stream<Employee> stream = employees.stream();
		// toMap must operate with unique map keys
		Map<String, Employee> map = stream.collect(Collectors.toMap(e -> e.getName(), e -> e, (e1, e2) -> e1));
		assertEquals(SIZE - 1, map.size());
	}

	/**
	 * CollectingAndThen is a special collector that allows performing another action on a result straight
	 * after collecting ends.
	 */
	@Test
	public void testCollectorsCollectingAndThen() {
		Stream<Employee> stream = employees.stream();
		List<Employee> list = stream.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
		assertEquals(SIZE, list.size());
	}

	@Test
	public void testGetNameOf() {
		Stream<Employee> stream = employees.stream();
		assertEquals(EMP_1, getNameOf(stream, EMP_1));
	}

	@Test
	public void testStreamEmptyList() {
		employees.clear();
		Stream<Employee> stream = employees.stream();
		assertEquals("", getNameOf(stream, EMP_1));
	}

	@Test
	public void testGetAgeOfEmployee() {
		Stream<Employee> stream = employees.stream();
		assertTrue(getAgeOf(stream, EMP_1) == 10);
	}

	@Test
	public void testGetAgeOfNonExistentEmployee() {
		Stream<Employee> stream = employees.stream();
		assertTrue(getAgeOf(stream, "") == -1);
	}

	private String getNameOf(Stream<Employee> stream, String name) {
		return stream.filter(e -> e != null && name.equals(e.getName())).map(e-> e.getName()).findFirst().orElse("");
	}

	private Integer getAgeOf(Stream<Employee> stream, String name) {
		return stream.filter(e -> e != null && name.equals(e.getName())).map(e-> e.getAge()).findFirst().orElse(-1);
	}

}
