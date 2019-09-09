package org.sif.beans.converters;

import org.apache.commons.beanutils.converters.LongConverter;
import org.junit.Test;

import static org.junit.Assert.*;

public class IgnoreEmptyConverterTest {

	@Test
	public void testIgnoreEmptyToLong() {
		IgnoreEmptyConverter converter = new IgnoreEmptyConverter(new LongConverter());
		assertEquals(new Long(1L), converter.convert(Long.class, 1L));
	}

}