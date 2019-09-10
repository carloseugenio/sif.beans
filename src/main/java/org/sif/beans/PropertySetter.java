package org.sif.beans;

/**
 * Contract for Property Setters. These are handlers for setting values on
 * beans.
 * 
 * @author eugenio
 * 
 * @param <T>
 */
public interface PropertySetter<T, I> {

	/**
	 * Sets a single property for the bean. The implementation must be able to
	 * handle the correct type of property such as simple fields, nested and
	 * association fields
	 * 
	 * @param bean
	 *            the bean to set the property
	 * @param property
	 *            the property to set on the bean
	 * @param value
	 *            the value to set
	 * @return the bean whose property was set
	 * @throws Exception
	 *             if there is a problem setting the property
	 */
	T setProperty(T bean, String property, Object value);
	
	/**
	 * Unset a single property for the bean. The implementation must be able to
	 * handle the correct type of property such as simple fields, nested and
	 * association fields
	 * 
	 * @param bean
	 *            the bean to unbind the property
	 * @param property
	 *            the property to set on the bean
	 * @param value
	 *            the value to set
	 * @return the bean whose property was unset
	 * @throws Exception
	 *             if there is a problem setting the property
	 */	
	T unsetProperty(T bean, String property, Object value);

}
