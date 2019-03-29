package org.sif.beans.converters;

import org.apache.commons.beanutils.Converter;

import java.io.Serializable;

public class SerializableConverter implements Converter {
	@Override
	public <T> T convert(Class<T> type, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Serializable) {
			return (T) value;
		}
		throw new IllegalArgumentException("The given value [" + value + "] is not subtype of Serializable!");
	}
}
