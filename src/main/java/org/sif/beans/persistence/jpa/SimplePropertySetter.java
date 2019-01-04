package org.sif.beans.persistence.jpa;

import org.apache.commons.beanutils.BeanUtils;
import org.sif.core.persistence.Concrete;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.Basic;
import java.lang.reflect.Field;

import static org.sif.beans.Classes.classFor;

@Concrete(delegate = Basic.class)
public class SimplePropertySetter<T, I> extends AbstractJPAPropertySetter<T, I> {

	@Inject
	Logger log;

	@Override
	public T doSetProperty(T bean, String property, Object value) throws Exception {
		Object convertedValue = value;
		if (classFor(convertedValue).isArray()) {
			log.debug("The value[" + convertedValue + "] is array. Setting directly...");
			try {
				Field f = classFor(bean).getDeclaredField(property);
				f.setAccessible(true);
				f.set(bean, convertedValue);
			} catch (Exception ex) {
				log.debug("Could not set. ArrayField not found on bean: " + property + ". Exception: " + ex);
				return bean;
			}
		} else {
			// Sets the converted value in the target property
			BeanUtils.setProperty(bean, property, convertedValue);
		}
		// Return the modified bean
		return bean;
	}

	@Override
	public T unsetProperty(T bean, String property, Object value) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
