package org.sif.beans;

import org.sif.beans.persistence.jpa.PropertyRelationUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import static org.sif.beans.Classes.classFor;

@Named
public class PropertySetterFactory<T, I> {

	@Inject
	Logger log;

	@Inject
	@Named("ManyToOneRelationPropertySetter")
	private PropertySetter<T, I> manyToOneRelationSetter;

	@Inject
	@Named("ManyToManyRelationPropertySetter")
	private PropertySetter<T, I> manyToManyRelationSetter;

	@Inject
	@Named("SimplePropertySetter")
	private PropertySetter<T, I> simplePropertySetter;

	@Inject
	private PropertyRelationUtil propertyRelationUtil;

	public PropertySetter<T, I> getFor(T bean, String property) throws Exception {
		log.debug("Bean: " + bean);
		log.debug("Getting PropertySetter for bean ["
				+ classFor(bean) + "] and property [" + property + "]");
		PropertySetter<T, I> setter = null;
		try {
			if (propertyRelationUtil.isNotFromRelation(classFor(bean),
					property)) {
				setter = simplePropertySetter;
			} else if (propertyRelationUtil.isManyToOneRelation(
					classFor(bean), property)) {
				setter = manyToOneRelationSetter;
			} else if (propertyRelationUtil.isManyToManyRelation(
					classFor(bean), property)) {
				setter = manyToManyRelationSetter;
			} else if (propertyRelationUtil.isOneToManyRelation(
					classFor(bean), property)) {
				setter = manyToManyRelationSetter;
			}
			log.debug("Setter found: " + setter);
			if (setter == null) {
				throw new UnsupportedOperationException("Field [" + property
						+ "] on beean [" + bean
						+ "] has an unsupported relation annotation.");
			}
			return setter;
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			throw ex;
		}
	}

}
