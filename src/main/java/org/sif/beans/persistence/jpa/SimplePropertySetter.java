package org.sif.beans.persistence.jpa;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.sif.beans.PropertyValueConverterUtil;
import org.sif.beans.converters.IgnoreConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.util.Collection;

import static org.sif.beans.Classes.classFor;
import static org.sif.beans.Debugger.debug;

@Named("SimplePropertySetter")
public class SimplePropertySetter<T, I> extends AbstractJPAPropertySetter<T, I> {

	Logger log = LoggerFactory.getLogger(getClass());

	PropertyValueConverterUtil converterUtil;

	public PropertyValueConverterUtil getConverterUtil() {
		return converterUtil;
	}

	@Inject
	public void setConverterUtil(PropertyValueConverterUtil converterUtil) {
		this.converterUtil = converterUtil;
	}

	@Override
	public T doSetProperty(T bean, String property, Object value) {
		Object originalValue = value;
		log.debug("Setting property [" + property + "] simple value " + debug(value) + " on bean with class [" + bean.getClass().getSimpleName() + "]");
		Class<?> propertyType = null;
		try {
			propertyType = PropertyUtils.getPropertyType(bean, property);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		log.debug("Target property type on bean: " + propertyType);
		if (classFor(originalValue).isArray() && propertyType.isArray()) {
			log.debug("The value[" + originalValue + "] and target properties are arrays. Setting directly...");
			setPropertyDirectly(bean, property, originalValue);
			return bean;
		} else if (Collection.class.isAssignableFrom(classFor(originalValue))) {
			log.debug("The value provided is a collection. Check for target field on bean...");
			if (Collection.class.isAssignableFrom(propertyType)) {
				log.debug("Target property is a collection, setting directly...");
				setPropertyDirectly(bean, property, originalValue);
				return bean;
			} else {
				log.debug("The target property is not a collection. Getting the first value...");
				Collection collection = ((Collection) originalValue);
				if (!collection.isEmpty()) {
					originalValue = collection.iterator().next();
				}
			}
		}
		log.debug("Setting value " + debug(originalValue) + " on bean [" + bean + "]");
		// Sets the converted value in the target property
		try {
			Object converted = converterUtil.convert(bean.getClass(), property, originalValue);
			log.debug("Setting Converted value: " + debug(converted));
			BeanUtils.copyProperty(bean, property, converted);
		} catch (IgnoreConversionException ex) {
			// If this exception is throw then simply ignore it
			log.debug("Conversion threw " + ex + ". Ignoring it!");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// Return the modified bean
		return bean;
	}

	private boolean setPropertyDirectly(T bean, String property, Object originalValue) {
		try {
			Field f = classFor(bean).getDeclaredField(property);
			f.setAccessible(true);
			f.set(bean, originalValue);
		} catch (Exception ex) {
			log.debug("Could not set. ArrayField not found on bean: " + property + ". Exception: " + ex);
			return true;
		}
		return false;
	}

	@Override
	public T unsetProperty(T bean, String property, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

}
