package org.sif.beans;

import org.apache.commons.beanutils.ConversionException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
		assertEquals(0, collectionUtil.toCollection("").size());
	}

	@Test
	public void getFirstCollectionElement() {
		assertEquals("A", collectionUtil.getFirstCollectionElement(stringArrayCollection, String.class));
		assertEquals("A", collectionUtil.getFirstCollectionElement(listArrayCollection, String.class));
		assertEquals("A", collectionUtil.getFirstCollectionElement(commaSeparatedList, String.class));
	}

	@Test
	public void getFirstCollectionElementForBeanIdType() {
		assertEquals(1, collectionUtil.getFirstCollectionElement(stringNumberArrayCollection, Integer.class));
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
	public void isRawCollection() {
	}

	@Test
	public void isArrayCollection() {
	}

	@Test
	public void isStringCommaSeparatedNumberArray() {
	}

	@Test
	public void isStringCommaSeparatedArray() {
	}

	@Test
	public void isCollection() {
	}

	@Test
	public void isCollectionOfAnyType() {
	}

	@Test
	public void isEmpty() {
	}

	@Test
	public void newCollection() {
	}

}