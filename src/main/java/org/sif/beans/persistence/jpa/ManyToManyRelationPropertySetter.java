package org.sif.beans.persistence.jpa;

import org.apache.commons.beanutils.PropertyUtils;
import org.sif.beans.AnnotationUtil;
import org.sif.beans.CollectionUtil;
import org.sif.beans.PropertyValueConverterFactory;
import org.sif.beans.PropertyValueConverterUtil;
import org.sif.core.persistence.Concrete;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.List;

import static org.sif.beans.Classes.classFor;

/**
 * Property setter capable of setting ManyToMany relation properties on the
 * target bean. The values passed to this implementation are primary keys in the
 * relation bean. The implementation will be responsible for looking up the
 * relation entities on database.
 * 
 * @author Carlos Eugenio P. da Purificacao
 * 
 * @param <T>
 *            the target bean type.
 */
@Concrete(delegate = ManyToMany.class)
public class ManyToManyRelationPropertySetter<T, I> extends AbstractJPAPropertySetter<T, I> {

	@Inject
	Logger log;

	@Inject
	PropertyValueConverterFactory<T> factory;

	@Inject
	CollectionUtil collectionUtil;

	@Inject
	PropertyValueConverterUtil converterUtil;
	
	@Override
	public T doSetProperty(T bean, String property, Object value)
			throws Exception {
		log.debug("Setting property [" + property + "] on bean ["
				+ classFor(bean).getSimpleName() + "] with value [" + value
				+ "]");
		boolean isManyToMany = AnnotationUtil.fieldHasAnnotation(
				classFor(bean), property, ManyToMany.class);
		boolean isOneToMany = AnnotationUtil.fieldHasAnnotation(
				classFor(bean), property, OneToMany.class);
		if (isManyToMany || isOneToMany) {
			// This is a one to many or many to many annotation. Handler should get the related
			// entity from repository and set it on the bean field
			handleManyToManyRelation(bean, property, value);
		} else {
			throw new IllegalArgumentException("Invalid property [" + property
					+ "] provided. It is not a one-to-many or many-to-many relation.");
		}
		return bean;
	}

	@SuppressWarnings("unchecked")
	private void handleManyToManyRelation(T bean, String property, Object value)
			throws Exception {
		log.debug("Getting the relation bean class for ["
				+ classFor(bean).getSimpleName() + "] and property ["
				+ property + "]");
		// Get the property class that is the relation class
		Class<?> relationBeanClass = AnnotationUtil
				.getTypeParameterClassForManyToManyField(classFor(bean),
						property);
		log.debug("RelationBeanClass: " + relationBeanClass);
		// TODO: This "ID" is fixed, refactor it to make it configurable
		String primaryKeyField = "id";
		// Instantiate a facade for the relation property class (as declared in
		// the main bean). The facade will be later used to retrieve elements
		// from persistence storage.
		relationFacade.setBeanClass(relationBeanClass);
		log.debug("Getting property [" + property + "] on bean [" + bean + "]");
		// The collection field value. This is the current collection values
		Object fieldValue = PropertyUtils.getProperty(bean, property);
		log.debug("Property [" + property + "] found. Value: " + fieldValue);
		@SuppressWarnings("rawtypes")
		Collection collectionValue = (Collection) fieldValue;
		log.debug("Current collection values on bean: " + collectionValue);
		if (collectionUtil.isCollection(value)) {
			// If the value is a collection, get all values in one query
			log.debug("The value ["
					+ value
					+ "] is a collection. Using the facade to retrieve elements from persistence storage...");
			List<?> loaded = relationFacade.findByField(primaryKeyField, value);
			collectionValue.addAll(loaded);
			log.debug("All Values added to the collection!");
		} else {
			log.debug("Finding a converter for bean [" + bean
					+ "] and property [" + property + "]");
			I convertedValue = (I) converterUtil.convert(relationBeanClass,
					primaryKeyField, value);
			// Find the target entity using the converted value for the primary
			// key
			Object loaded = relationFacade.load(convertedValue);
			log.debug("Loaded: " + loaded);
			// Adds the loaded entity on the collection field
			collectionValue.add(loaded);
			log.debug("Value added to the collection!");
		}
	}

	@Override
	public T unsetProperty(T bean, String property, Object value)
			throws Exception {
		log.debug("UnSetting property [" + property + "] on bean ["
				+ classFor(bean).getSimpleName() + "] with value [" + value
				+ "]");
		boolean isManyToMany = AnnotationUtil.fieldHasAnnotation(
				classFor(bean), property, ManyToMany.class);
		if (isManyToMany) {
			// This is a many to many annotation. Handler should get the related
			// entity from repository and set it on the bean field
			handleManyToManyRemoveRelation(bean, property, value);
		} else {
			throw new IllegalArgumentException("Invalid property [" + property
					+ "] provided. It is not a many-to-many relation.");
		}
		return bean;
	}

	@SuppressWarnings("unchecked")
	private void handleManyToManyRemoveRelation(T bean, String property,
			Object value) throws Exception {
		log.debug("Getting the relation bean class for ["
				+ classFor(bean).getSimpleName() + "] and property ["
				+ property + "]");
		// Get the property class that is the relation class
		Class<?> relationBeanClass = AnnotationUtil
				.getTypeParameterClassForManyToManyField(classFor(bean),
						property);
		log.debug("RelationBeanClass: " + relationBeanClass);
		// TODO: This "ID" is fixed, refactor it to make it configurable
		String primaryKeyField = "id";
		// Instantiate a facade for the relation property class (as declared in
		// the main bean). The facade will be later used to retrieve elements
		// from persistence storage.
		relationFacade.setBeanClass(relationBeanClass);
		log.debug("Getting property [" + property + "] on bean [" + bean + "]");
		// The collection field value. This is the current collection values
		Object fieldValue = PropertyUtils.getProperty(bean, property);
		log.debug("Property [" + property + "] found. Value: " + fieldValue);
		@SuppressWarnings("rawtypes")
		Collection collectionValue = (Collection) fieldValue;
		log.debug("Current collection values on bean: " + collectionValue);
		if (collectionUtil.isCollection(value)) {
			// If the value is a collection, get all values in one query
			log.debug("The value ["
					+ value
					+ "] is a collection. Using the facade to retreive elements from persistence storage...");
			List<?> loaded = relationFacade.findByField(primaryKeyField, value);
			collectionValue.removeAll(loaded);
			log.debug("All Values removed from collection!");
			log.debug("New collection value: " + collectionValue);
		} else {
			log.debug("Converting value [" + value + "] of class ["
					+ relationBeanClass + "] to the primarykey field type...");
			I convertedValue = (I) converterUtil.convert(relationBeanClass,
					primaryKeyField, value);
			log.debug("PrimaryKey field [" + primaryKeyField
					+ "] converted value [" + convertedValue + "] type ["
					+ classFor(convertedValue) + "]");
			// Find the target entity using the converted value for the primary
			// key
			Object loaded = relationFacade.load(convertedValue);
			log.debug("Loaded: " + loaded);
			// Adds the loaded entity on the collection field
			collectionValue.remove(loaded);
			log.debug("Value removed to the collection!");
			log.debug("New collection value: " + collectionValue);
		}
	}

}
