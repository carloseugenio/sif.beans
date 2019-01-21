package org.sif.beans.persistence.jpa;

import org.sif.beans.PropertyValueConverter;
import org.sif.beans.PropertyValueConverterUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import static org.sif.beans.Classes.getFieldClass;

@Named("BasicPropertyValueConverter")
public class SimplePropertyValueConverter<T> implements
		PropertyValueConverter<T> {

	@Inject
	Logger log;

	@Inject
	PropertyValueConverterUtil converterUtil;

	@Override
	public Object convertBeanPropertyValue(Class<?> beanClass,
			String beanProperty, Object value) throws Exception {
		Object convertedValue = null;
		if (value != null) {
			Class<?> type = getFieldClass(beanClass, beanProperty);
			log.debug("Trying to convert value [" + value
					+ "] to target type [" + type + "]");
			if (type == null) {
				String msg = "Type not found for property [" + beanProperty
						+ "] on bean [" + beanClass + "]";
				log.warn(msg);
				throw new IllegalArgumentException(msg);
			}
			convertedValue = converterUtil.convert(type, value);
		}
		return convertedValue;
	}

}
