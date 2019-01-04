package org.sif.beans;

/**
 * General contract for Property value converters. These are capable of
 * converting values to the expected value of a property in the provided bean
 * object.
 * 
 * @author Carlos Eugenio P. da Purificacao
 * 
 * @param <T> the type for the bean
 */
@Deprecated
public interface PropertyValueConverter<T> {

	/**
	 * Converts a provided value to a value whose type is the expected type for
	 * the given property on bean.
	 * 
	 * @param beanClass
	 *            the bean which contains the property
	 * @param beanProperty
	 *            the target property we want to convert the value to
	 * @param value
	 *            the value that needs to be converted to the correct type the
	 *            property is expecting
	 * @return the value converted to the correct type the property on bean is
	 *         expecting
	 */
	Object convertBeanPropertyValue(Class<?> beanClass,
			String beanProperty, Object value) throws Exception;

}
