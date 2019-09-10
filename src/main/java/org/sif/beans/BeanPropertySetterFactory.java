package org.sif.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import static org.sif.beans.Classes.classFor;

/**
 * Factory for {@link PropertySetter}s.
 * @param <T> the bean type
 * @param <I> the bean primary key type
 */
@Named
public class BeanPropertySetterFactory<T, I> implements  PropertySetterFactory<T, I> {

	Logger log = LoggerFactory.getLogger(getClass());

	private PropertySetter<T, I> simplePropertySetter;

	@Inject
	@Named("SimplePropertySetter")
	public void setSimplePropertySetter(PropertySetter<T, I> simplePropertySetter) {
		this.simplePropertySetter = simplePropertySetter;
	}

	@Override
	public PropertySetter<T, I> getFor(T bean, String property) {
		log.debug("Bean: {}", bean);
		log.debug("Getting PropertySetter for bean [{}] and property [{}]", classFor(bean), property);
		return simplePropertySetter;
	}

}
