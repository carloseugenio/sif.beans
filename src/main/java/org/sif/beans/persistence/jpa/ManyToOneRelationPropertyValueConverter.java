package org.sif.beans.persistence.jpa;

import org.sif.beans.AnnotationUtil;
import org.sif.beans.PropertyValueConverter;
import org.sif.beans.PropertyValueConverterUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.ManyToOne;

import static org.sif.beans.Classes.classFor;
import static org.sif.beans.Classes.getFieldClass;

/**
 * Converts values to expected primary key values for ManyToOne relations.
 * 
 * @author Carlos Eugenio P. da Purificacao
 * 
 * @param <T>
 */
@Named("ManyToOnePropertyValueConverter")
public class ManyToOneRelationPropertyValueConverter<T> implements
		PropertyValueConverter<T> {

	@Inject
	Logger log;

	@Inject
	PropertyValueConverterUtil converterUtil;

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
	@Override
	public Object convertBeanPropertyValue(Class<?> beanClass,
			String beanProperty, Object value) throws Exception {

		log.debug("Converting: [" + value + "] for field [" + beanProperty
				+ "] on bean: [" + beanClass + "]");
		boolean isManyToOne = AnnotationUtil.fieldHasAnnotation(beanClass,
				beanProperty, ManyToOne.class);
		if (isManyToOne) {
			log.debug("This is a many to one relation. Handling,..");
			// This is a many to one annotation.
			return handleManyToOneRelation(beanClass, beanProperty, value);
		} else {
			throw new IllegalArgumentException(
					"The can't convert this property: [" + beanProperty
							+ "] on bean [" + beanClass + "]");
		}
	}

	private Object handleManyToOneRelation(Class<?> bean, String property,
			Object value) throws Exception {
		log.debug("Getting the relation bean class for bean [" + bean
				+ "], property [" + property + "]");
		// Get the property class that is the relation class
		Class<?> relationBeanClass = getFieldClass(bean, property);
		log.debug("RelationBeanClass: " + relationBeanClass);
		// TODO: This "ID" is fixed, refactor it to make it configurable
		String primaryKeyField = "id";
		log.debug("Using the converter to get converted value for relationBeanClass ["
				+ relationBeanClass
				+ "], field ["
				+ primaryKeyField
				+ "] and value [" + value + "]");
		// Convert the value to the correct type on the target bean property
		Object convertedValue = converterUtil.convert(relationBeanClass,
				primaryKeyField, value);
		log.debug("Converted value: " + convertedValue);
		log.debug("Converted value class: " + classFor(convertedValue));
		return convertedValue;
	}

}
