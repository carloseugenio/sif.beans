package org.sif.beans;

import javax.inject.Inject;
import javax.persistence.Basic;

import org.sif.core.persistence.Concrete;
import org.slf4j.Logger;

import static org.sif.beans.Classes.getFieldClass;

@Concrete(delegate = Basic.class)
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
