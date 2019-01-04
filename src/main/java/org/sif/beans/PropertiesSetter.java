package org.sif.beans;

import java.util.Map;

/**
 * Defines a general contract for setting properties in a bean.
 * 
 * @author eugenio
 * 
 * @param <T>
 *            the bean type
 */
public interface PropertiesSetter<T, I> {

	/**
	 * Set all properties found in the provided bean with the provided
	 * parameters. Properties are set based on keys of provided parameters Map.
	 * This implementation will choose the correct implementation for setting
	 * each property instead of trying to setting all of them in a bulk
	 * operation.
	 * 
	 * @param bean
	 *            the bean to set the properties
	 * @param parameters
	 *            a Map providing property values to set in the destination bean
	 * @throws Exception if an exception occurs while setting the bean properties 
	 */
	void setAllProperties(T bean, Map<String, Object> parameters) throws Exception;

}
