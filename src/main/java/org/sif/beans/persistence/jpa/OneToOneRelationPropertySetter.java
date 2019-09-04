package org.sif.beans.persistence.jpa;

import org.apache.commons.beanutils.BeanUtils;
import org.sif.beans.AnnotationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Collection;
import java.util.List;

import static org.sif.beans.Classes.classFor;
import static org.sif.beans.Classes.getFieldClass;

/**
 * Converts values to expected primary key values for ManyToOne relations.
 *
 * @author Carlos Eugenio P. da Purificacao
 *
 * @param <T>
 */
@Named("OneToOneRelationPropertySetter")
public class OneToOneRelationPropertySetter<T, I> extends
		AbstractJPAPropertySetter<T, I> {

	Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public T doSetProperty(T bean, String property, Object value) {
		boolean isOneToOne = AnnotationUtil.fieldHasAnnotation(
				classFor(bean), property, OneToOne.class);
		if (isOneToOne) {
			log.debug("Setting one to one property [" + property + "] -> " + value);
			if (value != null) {
				log.debug("Value class: " + value.getClass());
				if (String.class.isAssignableFrom(value.getClass())) {
					log.debug("Value is String. Check if empty");
					String stringValue = (String) value;
					if (stringValue.isEmpty()) {
						log.debug("The string is empty. Unsetting the property!");
						return unsetProperty(bean, property, null);
					}
				}
			}
			// This converter can't handle collection of values since it will
			// lookup a single bean to convert
			if (collectionUtil.isCollection(value)) {
				/*throw new IllegalArgumentException("The provided value ["
						+ value + "] to convert can't be a collection.");*/
				log.warn("The provided value is a collection. Attempting to use the first value...");
				Collection collection = (Collection) value;
				if (!collection.isEmpty()) {
					value = collection.iterator().next();
					log.debug("First value: " + value + " of class: " + value.getClass());
					if (String.class.isAssignableFrom(value.getClass())) {
						log.warn("The first value is an String, this shouldn't happen!");
						log.warn("Unsetting the property!");
						return unsetProperty(bean, property, null);
					}
				} else {
					log.debug("Collection is empty. Unsetting the property on bean...");
					return unsetProperty(bean, property, null);
				}
			}
			// This is a many to one annotation. Handler should get the related
			// entity from repository and set it on the bean field
			handleOneToOneRelation(bean, property, value);
		} else {
			throw new IllegalArgumentException("The provided property ["
					+ property + "] is not a many to one property on bean ["
					+ bean + "].");
		}
		return bean;
	}

	@SuppressWarnings("unchecked")
	private void handleOneToOneRelation(T bean, String property, Object value) {
		// Get the property class that is the relation class
		Class<?> relationBeanClass = getFieldClass(classFor(bean),
				property);
		// can only setup the relation facade now
		getRelationFacade().setBeanClass(relationBeanClass);

		// TODO: This "ID" is fixed, refactor it to make it configurable
		String primaryKeyField = "id";
		// We are using the converter utility instead of a
		// PropertyValueConverter
		Object primaryKeyValue = converterUtil.convert(relationBeanClass,
				primaryKeyField, value);
		// Find the target entity using the converted value for the primary key
		// It must use the relation facade
		List<?> relationEntities = getRelationFacade().findByField(primaryKeyField,
				primaryKeyValue);
		// Check the return value
		ensureOnlyOneValueReturned(relationEntities, primaryKeyField,
				primaryKeyValue, relationBeanClass);
		// Pickup the first element found
		Object relationEntity = relationEntities.get(0);
		// Sets the relation entity found in the target bean
		try {
			BeanUtils.setProperty(bean, property, relationEntity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void ensureOnlyOneValueReturned(List<?> relationEntities,
			String primaryKeyField, Object primaryKeyValue,
			Class<?> relationBeanClass) {
		final StringBuilder message = messagePayload(primaryKeyField,
				primaryKeyValue, relationBeanClass);
		if (relationEntities.isEmpty()) {
			message.append("] didn't return any matches!");
			throw new IllegalArgumentException(message.toString());
		} else if (relationEntities.size() > 1) {
			message.append("] returned more than one match!");
			throw new IllegalArgumentException(message.toString());
		}

	}

	private StringBuilder messagePayload(String primaryKeyField,
			Object primaryKeyValue, Class<?> relationBeanClass) {
		StringBuilder message = new StringBuilder();
		message.append("The query for the provided value [");
		message.append(primaryKeyValue);
		message.append("] for the primary key field [");
		message.append(primaryKeyField);
		message.append("] in class [");
		message.append(relationBeanClass);
		return message;
	}

	@Override
	public T unsetProperty(T bean, String property, Object value) {
		boolean isManyToOne = AnnotationUtil.fieldHasAnnotation(
				classFor(bean), property, ManyToOne.class);
		if (isManyToOne) {
			// This converter can't handle collection of values since it will
			// lookup a single bean to convert
			if (collectionUtil.isCollection(value)) {
				throw new IllegalArgumentException("The provided value ["
						+ value + "] to convert can't be a collection.");
			}
			// This is a many to one annotation. Handler should get the related
			// entity from repository and unset it on the bean field
			try {
				BeanUtils.setProperty(bean, property, null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new IllegalArgumentException("The provided property ["
					+ property + "] is not a many to one property on bean ["
					+ bean + "].");
		}
		return bean;
	}


}
