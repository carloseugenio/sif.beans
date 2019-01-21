package org.sif.beans;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class PropertyValueConverterUtilTest {

	PropertyValueConverterUtil converterUtil = new PropertyValueConverterUtil();
	Logger log = LoggerFactory.getLogger(getClass());

	@Test
	public void intArrayAsList() {
		Object array = new int[]{1, 2};
		List<Integer> expected = Arrays.asList(1, 2);
		assertEquals(expected, converterUtil.asList(array));
	}

	@Test
	public void stringArrayAsList() {
		String[] array = new String[]{"1", "2"};
		List<String> expected = Arrays.asList("1", "2");
		Collection<?> result = converterUtil.asList(array);
		assertEquals(expected, result);
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
		List<String> value = Arrays.asList("1");
		Object result = converterUtil.convert(Integer.class, value);
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
	public void convert1() {
	}

	@Test
	public void valueListToCollection() {
	}
}