package org.sif.beans;

public interface PropertySetterFactory<T, I> {

	/**
	 * Returns a {@link PropertySetter} for the given bean and property.
	 * @param bean the bean having the property
	 * @param property the property to get the {@link PropertySetter} for
	 * @return a suitable {@link PropertySetter} for the given bean and property
	 */
	PropertySetter<T, I> getFor(T bean, String property);
}
