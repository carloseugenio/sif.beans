package org.sif.beans;

import org.apache.commons.beanutils.ConversionException;
import org.junit.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class CollectionUtilTest {

	String[] stringArrayCollection = {"A", "1"};
	String[] stringNumberArrayCollection = {"1", "2"};
	List<String> listArrayCollection = Arrays.asList(stringArrayCollection);
	String commaSeparatedList = "A,1";
	String simpleStringValue = "A";
	Integer simpleIntegerValue = 1;
	CollectionUtil collectionUtil = new CollectionUtil();

	@Test
	public void testToCollectionSimpleStringValue() {
		List<String> expected = Arrays.asList(simpleStringValue);
		Collection<String> result = collectionUtil.toCollection(simpleStringValue);
		assertEquals(expected, result);
	}

	@Test
	public void testToCollectionSimpleIntegerValue() {
		List<Integer> expected = Arrays.asList(simpleIntegerValue);
		Collection<Integer> result = collectionUtil.toCollection(simpleIntegerValue);
		assertEquals(expected, result);
	}

	@Test
	public void testToCollectionCommaSeparatedStringValue() {
		List<String> expected = Arrays.asList("A", "1");
		Collection<String> result = collectionUtil.toCollection(commaSeparatedList);
		assertEquals(expected, result);
	}

	@Test
	public void testToCollectionStringArray() {
		List<String> expected = Arrays.asList("A", "1");
		Collection<String> result = collectionUtil.toCollection(stringArrayCollection);
		assertEquals(expected, result);
	}

	@Test
	public void testToCollectionNullValue() {
		assertTrue(collectionUtil.toCollection(null).isEmpty());
	}

	@Test
	public void testToCollectionEmptyString() {
		assertEquals(1, collectionUtil.toCollection("").size());
	}

	@Test
	public void getFirstCollectionElement() {
		assertEquals("A", collectionUtil.getFirstCollectionElement(stringArrayCollection));
		assertEquals("A", collectionUtil.getFirstCollectionElement(listArrayCollection));
		assertEquals("A", collectionUtil.getFirstCollectionElement(commaSeparatedList));
	}

	@Test
	public void getFirstCollectionElementForBeanIdType() {
		assertEquals(1, collectionUtil.getFirstCollectionElement(stringNumberArrayCollection));
		assertEquals(1L, collectionUtil.getFirstCollectionElement(stringNumberArrayCollection));
	}

	@Test(expected = ConversionException.class)
	public void getFirstCollectionElementWrongType() {
		collectionUtil.getFirstCollectionElement(commaSeparatedList);
	}

	@Test(expected = ConversionException.class)
	public void getFirstCollectionElementWrongTypeForArray() {
		collectionUtil.getFirstCollectionElement(stringArrayCollection);
	}

	@Test
	public void testIsEmptyWithNullValue() {
		assertTrue(collectionUtil.isEmpty(null));
	}

	@Test
	public void testIsEmptyWithEmptyList() {
		assertTrue(collectionUtil.isEmpty(Collections.EMPTY_LIST));
	}

	@Test
	public void testIsNotEmptyWithEmptyString() {
		assertFalse(collectionUtil.isEmpty(""));
	}

	@Test
	public void testIsEmptyWithEmptyArray() {
		assertTrue(collectionUtil.isEmpty(new Object[]{}));
	}

	@Test
	public void testNotEmptyWithArray() {
		assertFalse(collectionUtil.isEmpty(new Object[]{"b"}));
	}

	@Test
	public void testNotEmptyWithList() {
		assertFalse(collectionUtil.isEmpty(Arrays.asList("b")));
	}

	@Test
	public void testNewCollection() {
		assertEquals(ArrayList.class, collectionUtil.newCollection(List.class).getClass());
		assertEquals(ArrayList.class, collectionUtil.newCollection(ArrayList.class).getClass());
		assertEquals(HashSet.class, collectionUtil.newCollection(Set.class).getClass());
		assertEquals(HashSet.class, collectionUtil.newCollection(HashSet.class).getClass());
		// Custom implementations
		assertEquals(ArrayList.class, collectionUtil.newCollection(TestList.class).getClass());
	}
}

class TestList extends AbstractList {

	@Override
	public Object get(int index) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}
}
