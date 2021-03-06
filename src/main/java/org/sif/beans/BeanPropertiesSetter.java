package org.sif.beans;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class BeanPropertiesSetter<T, I> implements PropertiesSetter<T> {

	Logger log = LoggerFactory.getLogger(BeanPropertiesSetter.class);

	public static final String IGNORE_EMPTY_PROPERTY = "org.sif.beans.ignore.empty";
	/**
	 * If this prefix is found in any parameter, the remaining suffix will be considered
	 * as a property in the bean to unset (dissociate)
	 */
	public static final String DISSOCIATE_PREFIX = "dissociate-";
	public static final String IGNORE_PROPERTY = "org.sif.beans.ignore";

	private PropertySetterFactory<T, I> factory;

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
	public void setAllProperties(T bean, Map<String, Object> parameters) {
		log.debug("Setting all properties with parameters: " + parameters);
		// Filter the ignore properties
		final List<String> ignoreList = new ArrayList<>();
		for(Map.Entry entry : parameters.entrySet()) {
			if (IGNORE_PROPERTY.equals(entry.getKey())) {
				ignoreList.add(entry.getValue().toString());
			}
			if (IGNORE_EMPTY_PROPERTY.equals(entry.getKey())) {
				// The property name to ignore. Check if it is empty
				String ignorePropertyName = entry.getValue().toString();
				// The ignore property name is another value in parameters using it as a key
				Object value = parameters.get(ignorePropertyName);
				if (value == null ||
						(value instanceof String && StringUtils.isEmpty(value.toString()))) {
					// Ignore empty will ignore nulls
					ignoreList.add(ignorePropertyName);
				}
			}
		}
		// For each key in the parameters map
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			String property = entry.getKey();
			// If the ignore property was defined for this property then skip it
			if (ignoreList.contains(property)) {
				log.debug("Ignoring property [" + property + "]");
				continue;
			}
			// First get the parameter value before any modifications
			Object parameterValue = entry.getValue();
			log.debug("Handling property: [" + property + "] with value: [" + parameterValue + "]");
			boolean dissociate = false;
			// If the dissociate prefix was used, change it to the real property
			// name
			if (property.startsWith(DISSOCIATE_PREFIX)) {
				property = remove(property, DISSOCIATE_PREFIX);
				dissociate = true;
				log.debug("Found DISSOCIATE PREFIX. Property to unset: " + property);
			}
			if (!PropertyUtils.isReadable(bean, property)) {
				log.warn("The property [" + property + "] is not readable on bean [" + bean + "]");
				continue;
			}
			// Create the appropriate property setter
			PropertySetter<T, I> setter = getFactory().getFor(bean, property);
			if (dissociate) {
				// unset the property
				setter.unsetProperty(bean, property, parameterValue);
			} else {
				// set the property
				setter.setProperty(bean, property, parameterValue);
			}
		}
	}

	@Override
	public PropertySetterFactory<T, I> getFactory() {
		return this.factory;
	}

	@Inject
	public void setFactory(BeanPropertySetterFactory<T, I> factory) {
		this.factory = factory;
	}
}
