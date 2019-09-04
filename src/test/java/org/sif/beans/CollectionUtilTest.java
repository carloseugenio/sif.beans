package org.sif.beans;

import org.apache.commons.beanutils.ConversionException;
import org.junit.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
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
	public void toListSimpleStringValue() {
		List<String> expected = Arrays.asList(simpleStringValue);
		List<String> result = collectionUtil.asList(simpleStringValue);
		assertEquals(expected, result);
	}

	@Test
	public void toListSimpleIntegerValue() {
		List<Integer> expected = Arrays.asList(simpleIntegerValue);
		List<Integer> result = collectionUtil.asList(simpleIntegerValue);
		assertThat(result, is(expected));
	}

	@Test
	public void toListCommaSeparatedStringValue() {
		List<String> expected = Arrays.asList("A", "1");
		List<String> result = collectionUtil.asList(commaSeparatedList);
		assertEquals(expected, result);
	}

	@Test
	public void toListStringArray() {
		List<String> expected = Arrays.asList("A", "1");
		List<String> result = collectionUtil.asList(stringArrayCollection);
		assertEquals(expected, result);
	}

	@Test
	public void toListNullValue() {
		assertTrue(collectionUtil.asList(null).isEmpty());
	}

	@Test
	public void toListEmptyString() {
		assertEquals(1, collectionUtil.asList("").size());
	}

	@Test
	public void getFirstCollectionElementStringArray() {
		assertEquals("A", collectionUtil.getFirstCollectionElement(stringArrayCollection));
	}

	@Test
	public void getFirstCollectionElementListArray() {
		assertEquals("A", collectionUtil.getFirstCollectionElement(listArrayCollection));
	}

	@Test
	public void getFirstCollectionElementCommaStringArray() {
		assertEquals("A", collectionUtil.getFirstCollectionElement(commaSeparatedList));
	}

	@Test
	public void getFirstCollectionElementForString() {
		assertEquals("1", collectionUtil.getFirstCollectionElement(stringNumberArrayCollection, String.class));
	}

	@Test
	public void getFirstCollectionElementForInteger() {
		assertEquals(1, collectionUtil.getFirstCollectionElement(stringNumberArrayCollection, Integer.class));
	}
	@Test
	public void getFirstCollectionElementForLong() {
		assertEquals(1L, collectionUtil.getFirstCollectionElement(stringNumberArrayCollection, Long.class));
	}

	@Test(expected = ConversionException.class)
	public void getFirstCollectionElementWrongType() {
		collectionUtil.getFirstCollectionElement(commaSeparatedList, Integer.class);
	}

	@Test(expected = ConversionException.class)
	public void getFirstCollectionElementWrongTypeForArray() {
		collectionUtil.getFirstCollectionElement(stringArrayCollection, Integer.class);
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
