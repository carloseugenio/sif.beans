package org.sif.beans.persistence.jpa;

import org.sif.beans.CollectionUtil;
import org.sif.beans.PropertyValueConverterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;

import static org.sif.beans.Classes.getFieldClass;

@Named("ManyToManyPropertyValueConverter")
public class ManyToManyRelationPropertyValueConverter<T> extends
		ManyToOneRelationPropertyValueConverter<T> {

	Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	PropertyValueConverterUtil converterUtil;

	@Inject
	CollectionUtil collectionUtil;

	/**
	 * Converts a provided value to a value whose type is the expected type for
	 * the given property on bean. As this converter is suitable only for
	 * many-to-many properties, it will throw an exception if the property is
	 * not of correct type.
	 * 
	 * @param beanClass
	 *            the bean which contains the property
	 * @param beanIDProperty
	 *            the target property we want to convert the value to
	 * @param value
	 *            the value that needs to be converted to the correct type the
	 *            property is expecting
	 * @return the value converted to the correct type the property on bean is
	 *         expecting
	 */
	@Override
	public Object convertBeanPropertyValue(Class<?> beanClass,
			String beanIDProperty, Object value) throws Exception {

		log.debug("Converting: [" + value
				+ "] to a compatible type for ID field [" + beanIDProperty
				+ "] on bean: [" + beanClass + "]");
		log.debug("This is a many to many relation. Handling,..");
		// This is a many to one annotation.
		return handleManyToManyRelation(beanClass, beanIDProperty, value);
	}

	private Object handleManyToManyRelation(Class<?> bean, String idProperty,
			Object value) throws Exception {
		if (collectionUtil.isCollection(value)) {
			log.debug("The value [" + value + "] is a collection or array.");
			return convertCollectionPropertyValue(bean, idProperty, value);
		} else {
			log.debug("The value [" + value
					+ "] is a not a collection or array.");
			Class<?> fieldType = getFieldClass(bean, idProperty);
			log.debug("Trying to convert value [" + value
					+ "] to target Field Type [" + fieldType + "]");
			return converterUtil.convert(fieldType, value);
		}
	}

	private Object convertCollectionPropertyValue(Class<?> bean,
			String idProperty, Object value) throws Exception {
		log.debug("Converting value [" + value
				+ "] to a compatible collection on bean [" + bean
				+ "] for ID property [" + idProperty + "]");
		Class<?> fieldType = getFieldClass(bean, idProperty);
		if (fieldType == null) {
			Exception ex = new Exception(
					"Don't know what this means. idProperty: " + idProperty
							+ ", CLASS of bean: " + bean + ", is null!?");
			throw ex;
		}
		log.debug("Field type for [" + idProperty + "]: " + fieldType);
		Collection<?> ids = null;// converterUtil.valueToList(value, fieldType);
		log.debug("Final ids: " + ids);
		return ids;
	}

}
