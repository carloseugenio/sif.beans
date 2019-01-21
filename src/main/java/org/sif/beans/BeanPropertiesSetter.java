package org.sif.beans;

import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import static org.apache.commons.lang3.StringUtils.remove;

/**
 * Defines operations that help set properties on a bean, using the persistence
 * storage as the resource for looking up relations.
 * <p>
 * Relations can be set passing the relation property and the id' of
 * corresponding relation beans found in data store. The implementations of
 * PropertySetter will handle the actual processing of such behavior.
 * </p>
 * This is the default implementation but other more involved implementations
 * can be used.
 * 
 * @author eugenio
 * 
 */
@Named
public class BeanPropertiesSetter<T, I> implements PropertiesSetter<T, I> {

	@Inject
	Logger log;

	private static final String DISSOCIATE_PREFIX = "dissociate";

	@Inject
	PropertySetterFactory<T, I> factory;

	/**
	 * Set all properties found in the provided bean with the provided
	 * parameters. Properties are set based on keys of provided parameters Map.
	 * This implementation will choose the correct implementation for setting
	 * each property instead of trying to setting all of them in a bulk
	 * operation.
	 * <p>
	 * This implementation will leave the properties not defined in the provided
	 * parameters untouched.
	 * </p>
	 * 
	 * @param bean
	 *            the bean to set the properties
	 * @param parameters
	 *            a Map providing property values to set in the destination bean
	 * @throws Exception
	 *             if exception is thrown when setting a bean property or
	 *             looking up a bean.
	 */
	@Override
	public void setAllProperties(T bean, Map<String, Object> parameters)
			throws Exception {
		// For each key in the parameters map
		for (String property : parameters.keySet()) {
			// First get the parameter value before any modifications
			Object parameterValue = parameters.get(property);
			boolean dissociate = false;
			// If the dissociate prefix was used, change it to the real property
			// name
			if (property.startsWith(DISSOCIATE_PREFIX)) {
				property = remove(property, DISSOCIATE_PREFIX);
				dissociate = true;
			}
			//BeanUtilsBean2.getInstance().getProperty(bean, property);
			if (!PropertyUtils.isReadable(bean, property)) {
				log.warn("The property [" + property + "] is not readable on bean [" + bean + "]");
				continue;
			}
			// Create the appropriate property setter
			PropertySetter<T, I> setter = factory.getFor(bean, property);
			if (dissociate) {
				// unset the property
				setter.unsetProperty(bean, property, parameterValue);
			} else {
				// set the property
				setter.setProperty(bean, property, parameterValue);
			}
		}
	}
}
