package org.sif.beans.persistence.jpa;

import org.sif.beans.PropertySetter;

import javax.inject.Inject;

import static org.sif.beans.Classes.classFor;
/**
 * An abstract class that implements the {
 * {@link #setProperty(Object, String, Object)} method of {@link PropertySetter}
 * interface and provides a configured {@link PersistenceManager} to subClasses
 * to perform persistence operations.
 * 
 * @author eugenio
 *
 * @param <T>
 * @param <I>
 */
public abstract class AbstractJPAPropertySetter<T, I> implements
		PropertySetter<T, I> {

	/**
	 * The facade for the main bean.
	 */
	@Inject
	PersistenceManager<T, I> facade;

	/**
	 * Facade used to execute persistence operations on the relation bean of
	 * some source bean property, not the source bean itself. This facade does
	 * not have type parameters because at this point we don't know which type
	 * the relationship is.
	 */
	@SuppressWarnings("rawtypes")
	@Inject
	PersistenceManager relationFacade;

	@SuppressWarnings("unchecked")
	@Override
	public final T setProperty(T bean, String property, Object value)
			throws Exception {
		facade.setBeanClass((Class<T>) classFor(bean));
		return doSetProperty(bean, property, value);
	}

	/**
	 * Must be implemented by subClasses to do the real job
	 * 
	 * @param bean the bean to set the property
	 * @param property the property to set
	 * @param value the value to set
	 * @return the bean hose property was set
	 * @throws Exception if an exception occurs
	 */
	public abstract T doSetProperty(T bean, String property, Object value)
			throws Exception;

}
