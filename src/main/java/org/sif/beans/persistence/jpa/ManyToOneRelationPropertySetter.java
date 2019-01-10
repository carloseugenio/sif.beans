package org.sif.beans.persistence.jpa;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.ManyToOne;

import org.apache.commons.beanutils.BeanUtils;
import org.sif.beans.AnnotationUtil;
import org.sif.beans.CollectionUtil;
import org.sif.beans.PropertyValueConverterUtil;
import org.sif.core.persistence.Concrete;
import org.slf4j.Logger;

import static org.sif.beans.Classes.classFor;
import static org.sif.beans.Classes.getFieldClass;

/**
 * Property setter capable of setting ManyToOne relation properties on the
 * target bean provided the relation primary key. If the property to set is not
 * a ManyToOne JPA Relation property or the given value isn't a unique value for
 * the relation bean primary key property this converter will throw an
 * IllegalArgumentException.
 * 
 * @author Carlos Eugenio P. da Purificacao
 * 
 * @param <T>
 *            the target bean type.
 */
@Concrete(delegate = ManyToOne.class)
public class ManyToOneRelationPropertySetter<T, I> extends
		AbstractJPAPropertySetter<T, I> {

	@Inject
	Logger log;

	@Inject
	CollectionUtil collectionUtil;

	@Inject
	PropertyValueConverterUtil converterUtil;

	@Override
	public T doSetProperty(T bean, String property, Object value)
			throws Exception {
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
			// entity from repository and set it on the bean field
			handleManyToOneRelation(bean, property, value);
		} else {
			throw new IllegalArgumentException("The provided property ["
					+ property + "] is not a many to one property on bean ["
					+ bean + "].");
		}
		return bean;
	}

	@SuppressWarnings("unchecked")
	private void handleManyToOneRelation(T bean, String property, Object value)
			throws Exception {
		// Get the property class that is the relation class
		Class<?> relationBeanClass = getFieldClass(classFor(bean),
				property);
		// can only setup the relation facade now
		relationFacade.setBeanClass(relationBeanClass);

		// TODO: This "ID" is fixed, refactor it to make it configurable
		String primaryKeyField = "id";
		// We are using the converter utility instead of a
		// PropertyValueConverter
		Object primaryKeyValue = converterUtil.convert(relationBeanClass,
				primaryKeyField, value);
		// Find the target entity using the converted value for the primary key
		// It must use the relation facade
		List<?> relationEntities = relationFacade.findByField(primaryKeyField,
				primaryKeyValue);
		// Check the return value
		ensureOnlyOneValueReturned(relationEntities, primaryKeyField,
				primaryKeyValue, relationBeanClass);
		// Pickup the first element found
		Object relationEntity = relationEntities.get(0);
		// Sets the relation entity found in the target bean
		BeanUtils.setProperty(bean, property, relationEntity);
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
	public T unsetProperty(T bean, String property, Object value)
			throws Exception {
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
			BeanUtils.setProperty(bean, property, null);
		} else {
			throw new IllegalArgumentException("The provided property ["
					+ property + "] is not a many to one property on bean ["
					+ bean + "].");
		}
		return bean;
	}

}
