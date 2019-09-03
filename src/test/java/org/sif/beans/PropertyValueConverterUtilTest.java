package org.sif.beans;

import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class PropertyValueConverterUtilTest {

	private PropertyValueConverterUtil converterUtil = new PropertyValueConverterUtil();

	@Test
	public void stringArrayAsList() {
		String[] array = new String[]{"1", "2"};
		List<String> expected = Arrays.asList("1", "2");
		Collection<?> result = converterUtil.asList(array);
		assertEquals(expected, result);
	}

	@Test
	public void intArrayAsList() {
		Object array = new int[]{1, 2};
		List<Integer> expected = Arrays.asList(1, 2);
		assertEquals(expected, converterUtil.asList(array));
	}

	@Test(expected=IllegalArgumentException.class)
	public void invalidStringToListInvocation() {
		converterUtil.asList("a");
	}

	@Test(expected=IllegalArgumentException.class)
	public void invalidCollectionToListInvocation() {
		Set set = new HashSet();
		set.add("a");
		converterUtil.asList(set);
	}

	@Test
	public void convertSingleStringNumberValueToLongList() {
		List<Long> expected = Collections.singletonList(1L);
		String value = "1";
		List<Long> result = converterUtil.asList(Long.class, value);
		assertEquals(expected, result);
	}

	@Test
	public void convertStringListValueToLongList() {
		List<Long> expected = Arrays.asList(1L, 2L);
		List<String> value = Arrays.asList("1", "2");
		Object result = converterUtil.asList(Long.class, value);
		assertEquals(expected, result);
	}

	@Test
	public void convertStringDelimitedListValueToLongList() {
		List<Long> expected = Arrays.asList(1L, 2L);
		String value = "1, 2";
		Object result = converterUtil.asList(Long.class, value);
		assertEquals(expected, result);
	}

	@Test
	public void intArrayAsSet() {
		Object array = new int[]{1, 2};
		Set<Integer> expected = new HashSet(Arrays.asList(1, 2));
		assertEquals(expected, converterUtil.valueListToCollection(array, Set.class, Integer.class));
	}

	@Test
	public void intArrayAsSetUniqueValues() {
		Object array = new int[]{1, 2, 2, 1};
		Set<Integer> expected = new HashSet(Arrays.asList(1, 2));
		assertEquals(expected, converterUtil.valueListToCollection(array, Set.class, Integer.class));
	}

	@Test
	public void stringArrayAsSet() {
		String[] array = new String[]{"1", "2", "2", "1"};
		Set<String> expected = new HashSet(Arrays.asList("1", "2"));
		Collection<?> result = converterUtil.valueListToCollection(array, Set.class, String.class);
		assertEquals(expected, result);
	}

	@Test
	public void intListAsSet() {
		Object list = Arrays.asList(1, 2);
		Set<Integer> expected = new HashSet(Arrays.asList(1, 2));
		assertEquals(expected, converterUtil.valueListToCollection(list, Set.class, Integer.class));
	}

	@Test
	public void convertStringValueToInteger() {
		Integer expected = 1;
		String value = "1";
		Object result = converterUtil.convert(Integer.class, value);
		assertEquals(expected, result);
	}

	@Test
	public void convertStringArrayValueToInteger() {
		Integer expected = 1;
		String[] value = {"1"};
		Object result = converterUtil.convert(Integer.class, value);
		assertEquals(expected, result);
	}

	@Test
	public void convertStringListValueToInteger() {
		Integer expected = 1;
		List<String> value = Collections.singletonList("1");
		Object result = converterUtil.convert(Integer.class, value);
		assertEquals(expected, result);
	}

	@Test
	public void convertStringToObject() {
		String expected = "1";
		Object result = converterUtil.convert(Object.class, expected);
		assertEquals(expected, result);
	}

	@Test
	public void convertSingleStringValueToSerializable() {
		Serializable expected = "1";
		String value = "1";
		Object result = converterUtil.convert(Serializable.class, value);
		assertEquals(expected, result);
	}

	@Test(expected=IllegalArgumentException.class)
	public void convertCollectionToList() {
		converterUtil.convert(List.class, "1");
	}

}