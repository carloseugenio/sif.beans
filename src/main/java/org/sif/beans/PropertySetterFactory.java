package org.sif.beans;

import org.sif.beans.persistence.jpa.PropertyRelationUtil;
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
public class PropertySetterFactory<T, I> {

	Logger log = LoggerFactory.getLogger(getClass());

	private PropertySetter<T, I> oneToOneRelationSetter;

	private PropertySetter<T, I> manyToOneRelationSetter;

	private PropertySetter<T, I> manyToManyRelationSetter;

	private PropertySetter<T, I> oneToManyRelationSetter;

	private PropertySetter<T, I> simplePropertySetter;

	private PropertyRelationUtil propertyRelationUtil;

	@Inject
	@Named("OneToOneRelationPropertySetter")
	public void setOneToOneRelationSetter(PropertySetter<T, I> oneToOneRelationSetter) {
		this.oneToOneRelationSetter = oneToOneRelationSetter;
	}

	@Inject
	@Named("ManyToOneRelationPropertySetter")
	public void setManyToOneRelationSetter(PropertySetter<T, I> manyToOneRelationSetter) {
		this.manyToOneRelationSetter = manyToOneRelationSetter;
	}

	@Inject
	@Named("ManyToManyRelationPropertySetter")
	public void setManyToManyRelationSetter(PropertySetter<T, I> manyToManyRelationSetter) {
		this.manyToManyRelationSetter = manyToManyRelationSetter;
	}

	@Inject
	@Named("OneToManyRelationPropertySetter")
	public void setOneToManyRelationSetter(PropertySetter<T, I> oneToManyRelationSetter) {
		this.oneToManyRelationSetter = oneToManyRelationSetter;
	}

	@Inject
	@Named("SimplePropertySetter")
	public void setSimplePropertySetter(PropertySetter<T, I> simplePropertySetter) {
		this.simplePropertySetter = simplePropertySetter;
	}

	@Inject
	public void setPropertyRelationUtil(PropertyRelationUtil propertyRelationUtil) {
		this.propertyRelationUtil = propertyRelationUtil;
	}

	public PropertySetter<T, I> getFor(T bean, String property) {
		log.debug("Bean: {}", bean);
		log.debug("Getting PropertySetter for bean [{}] and property [{}]", classFor(bean), property);
		PropertySetter<T, I> setter = null;
		try {
			if (propertyRelationUtil.isOneToOneRelation(
					classFor(bean), property)) {
				setter = oneToOneRelationSetter;
			} else if (propertyRelationUtil.isManyToOneRelation(
					classFor(bean), property)) {
				setter = manyToOneRelationSetter;
			} else if (propertyRelationUtil.isManyToManyRelation(
					classFor(bean), property)) {
				setter = manyToManyRelationSetter;
			} else if (propertyRelationUtil.isOneToManyRelation(
					classFor(bean), property)) {
				setter = oneToManyRelationSetter;
			} else {
				setter = simplePropertySetter;
			}
			log.debug("Setter found: {}", setter);
			if (setter == null) {
				throw new UnsupportedOperationException("Field [" + property
						+ "] on bean [" + bean
						+ "] has an unsupported relation annotation.");
			}
			return setter;
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			throw new RuntimeException(ex);
		}
	}

}
