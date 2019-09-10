package org.sif.beans.converters;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IgnoreEmptyConverter implements Converter {

	Logger log = LoggerFactory.getLogger(getClass());

	private final Converter originalConverter;

	public IgnoreEmptyConverter(Converter original) {
		this.originalConverter = original;
	}

	@Override
	public <T> T convert(Class<T> type, Object value) {
		try {
			return this.originalConverter.convert(type, value);
		} catch (ConversionException ex) {
			if (value != null && value.toString().length() > 0) {
				throw ex;
			}
			log.debug("Conversion threw exception for empty string: {}. Throw Ignore conversion exception...", ex.toString(), ex);
			throw new IgnoreConversionException();
		}
	}
}
