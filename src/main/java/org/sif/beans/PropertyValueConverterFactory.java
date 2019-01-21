package org.sif.beans;

import org.sif.beans.persistence.jpa.PropertyRelationUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import static org.sif.beans.Classes.classFor;
/**
 * Factory for providing implementations for the PropertyValueConverter
 * interface.
 * 
 * @author Carlos Eugenio P. da Purificacao
 * 
 * @param <T>
 *            the type for the bean
 */
public class PropertyValueConverterFactory<T> {

	@Inject
	Logger log;

	@Inject
	@Named("ManyToOnePropertyValueConverter")
	private PropertyValueConverter<T> manyToOneRelationConverter;

	@Inject
	@Named("ManyToManyPropertyValueConverter")
	private PropertyValueConverter<T> manyToManyRelationConverter;

	@Inject
	@Named("BasicPropertyValueConverter")
	private PropertyValueConverter<T> basicValueConverter;

	@Inject
	private PropertyRelationUtil propertyRelationUtil;

	/**
	 * Returns an appropriate value converter for the bean and the provided
	 * property
	 */
	public PropertyValueConverter<T> getDefaultConverter() {
		return basicValueConverter;
	}

	public PropertyValueConverter<T> getFor(T bean, String property)
			throws Exception {
		try {
			PropertyValueConverter<T> converter = null;
			log.debug("Bean: " + bean);
			log.debug("Getting PropertyValueConverter for bean ["
					+ classFor(bean) + "] and property [" + property + "]");
			// if (BeanUtil.isNested(property) &&
			if (propertyRelationUtil.isManyToOneRelation(classFor(bean),
					property)) {
				converter = manyToOneRelationConverter;
			} else if (propertyRelationUtil.isManyToManyRelation(
					classFor(bean), property)) {
				converter = manyToManyRelationConverter;
			} else if (propertyRelationUtil.isNotFromRelation(classFor(bean),
					property)) {
				converter = basicValueConverter;
			}
			log.debug("Converter found: " + converter);
			if (converter == null) {
				throw new UnsupportedOperationException("Field [" + property
						+ "] on beean [" + classFor(bean)
						+ "] has an unsupported relation annotation.");
			}
			return converter;
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			throw ex;
		}
	}

	public PropertyValueConverter<T> getManytoManyConverter() {
		return manyToManyRelationConverter;
	}
}
