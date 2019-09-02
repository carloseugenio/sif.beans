package org.sif.beans;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Qualifier;
import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

import static org.sif.beans.Classes.classFor;

/**
 * Utility methods for working with annotated classes
 * 
 * @author eugenio
 * 
 */
public final class AnnotationUtil {

	/**
	 * Logger.
	 */
	public static Logger log = LoggerFactory.getLogger(AnnotationUtil.class);

	/**
	 * Returns true if the class has the indicated annotation
	 * 
	 * @param cls
	 *            the class to query for the annotation
	 * @param annotationClass
	 *            the annotation we are checking
	 * @return true if the class has the provided annotation
	 */
	public static boolean hasClassLevelAnnotation(Class<? extends Object> cls,
			final Class<? extends Annotation> annotationClass) {
		Annotation annotation = cls.getAnnotation(annotationClass);
		return annotation != null;
	}

	/**
	 * Returns the annotation for the field in the bean or null if no such
	 * annotation exists.
	 * 
	 * @param bean
	 *            the bean that contains the field
	 * @param fieldName
	 *            the field to look for the annotation
	 * @param annotationClass
	 *            the Annotation class
	 * @return the Annotation for the field or null if it doesn't exists
	 */
	public static <T extends Annotation> Annotation getAnnotationForField(Class<?> bean, String fieldName,
			Class<T> annotationClass) {
		log.debug("Finding field [{}] in bean class [{}]", fieldName, bean.getSimpleName());
		Field field = getField(bean, fieldName);
		log.debug("Found: {}", field);
		if (field == null) {
			throw new IllegalArgumentException(
					"Couldn't find [" + fieldName + "] in bean class [" + bean.getSimpleName() + "]");
		}
		return field.getAnnotation(annotationClass);
	}

	/**
	 * Returns the field in the target bean. This method will recurse to supper
	 * classes.
	 */
	static Field getField(Class<?> beanClass, String fieldName) {
		if (isNested(fieldName)) {
			String lastPart = fieldName.substring(fieldName.indexOf(".") + 1);
			log.debug("This is a nested field. Recursion for lastPart: {}", lastPart);
			String firstPart = fieldName.substring(0, fieldName.indexOf("."));
			log.debug("Getting type for first part property [{}]", firstPart);
			Class<?> firstType = getFieldClass(beanClass, firstPart);
			if (Collection.class.isAssignableFrom(firstType)) {
				log.debug("Found a collection. Trying the generic type...");
				firstType = getTypeParameterClassForCollectionField(beanClass, firstPart);
			}
			log.debug("FirstType class: [{}]", firstType);
			return getField(firstType, lastPart);
		}
		Field field = FieldUtils.getField(beanClass, fieldName, true);
		if (field != null) {
			return field;
		} else {
			throw new IllegalArgumentException("Field [" + fieldName + "] not found in [" + beanClass + "]");
		}
	}

	/**
	 * Returns the type of a field in the target bean class. This method will
	 * recurse to supper classes.
	 */
	public static Class<?> getFieldClass(Class<?> beanClass, String fieldName) {
		Field field = getField(beanClass, fieldName);
		if (field != null) {
			return field.getType();
		} else {
			throw new IllegalArgumentException("Field [" + fieldName + "] not found in [" + beanClass + "]");
		}
	}

	/**
	 * Inidica se a propriedade em questao e do tipo nested (ex. filho.nome)
	 *
	 * @param property
	 *            nome da propriedade a ser verificada
	 * @return true se a propriedade e do tipo nested.
	 */
	public static boolean isNested(String property) {
		if (StringUtils.trimToNull(property) == null) {
			return false;
		}
		return property.indexOf(".") != -1;
	}

	public static List<Field> getFieldsWithAnnotation(final Class<?> source,
			final Class<? extends Annotation> annotationClass) {
		List<Field> declaredAccessableFields = AccessController.doPrivileged(new PrivilegedAction<List<Field>>() {
			@Override
			public List<Field> run() {
				List<Field> foundFields = new ArrayList<Field>();
				Class<?> nextSource = source;
				while (nextSource != Object.class) {
					for (Field field : nextSource.getDeclaredFields()) {
						if (field.isAnnotationPresent(annotationClass)) {
							if (!field.isAccessible()) {
								field.setAccessible(true);
							}
							foundFields.add(field);
						}
					}
					nextSource = nextSource.getSuperclass();
				}
				return foundFields;
			}
		});
		return declaredAccessableFields;
	}

	public static boolean fieldHasAnnotation(Class<?> bean, String fieldName,
			Class<? extends Annotation> annotationClass) {
		try {
			Annotation annotation = AnnotationUtil.getAnnotationForField(bean, fieldName, annotationClass);
			return annotation != null;
		} catch (Exception ex) {
			log.warn("Exception getting annotation [{}] for field: [{}] on bean [{}]: {}",
					annotationClass, fieldName, bean, ex, ex);
		}
		return false;
	}

	/**
	 * Returns whether the provided field has any of the provided annotations
	 * 
	 * @param beanClass
	 *            the bean to check
	 * @param fieldName
	 *            the field in the bean
	 * @param annotations
	 *            a list of annotations to check
	 * @return true if the field has, at least, one of the provided annotations
	 * @throws Exception
	 */
	public static boolean fieldHasAnyAnnotation(Class<?> beanClass, String fieldName,
			List<Class<? extends Annotation>> annotations) {
		for (Class<? extends Annotation> annotation : annotations) {
			if (fieldHasAnnotation(beanClass, fieldName, annotation)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether a field in a bean has a JPA relation annotation (ManyToMany,
	 * OneToMany, ManyToOne).
	 * 
	 * @param beanClass
	 *            the class to check
	 * @param fieldName
	 *            the field in the class to check
	 * @return true if the field has a JPA Relation annotation
	 * @throws Exception
	 */
	public static boolean fieldHasRelationAnnotation(Class<?> beanClass, String fieldName) throws Exception {
		List<Class<? extends Annotation>> annotations = new ArrayList<>();
		annotations.add(ManyToMany.class);
		annotations.add(OneToMany.class);
		annotations.add(ManyToOne.class);
		annotations.add(OneToOne.class);
		return AnnotationUtil.fieldHasAnyAnnotation(beanClass, fieldName, annotations);
	}

	public static Class<?> getTypeParameterClassForCollectionField(Class<?> beanClass, String fieldName) {
		// It is the generic type applied to the collection.
		Field annotatedField = getField(beanClass, fieldName);
		// log.info("Annotated field: " + annotatedField);
		Type genericType = annotatedField.getGenericType();
		log.debug("Generic type: " + genericType);
		if (genericType == null || !(genericType instanceof ParameterizedType)) {
			// This field doesn't have a generic type. So I can't determine the
			// correct type this one-to-many fields relates to.
			return null;
		}
		ParameterizedType parType = (ParameterizedType) genericType;
		Type[] typeArguments = parType.getActualTypeArguments();
		if (typeArguments == null || typeArguments.length == 0) {
			// No type argument specified. Can't determine the actual type
			// for the relation
			return null;
		}
		// This is the type argument declared for the one-to-many field like
		// Set<Entity>
		Type entityTypeArgument = typeArguments[0];
		// log.info("EntityTypeArgument: " + entityTypeArgument);
		return (Class<?>) entityTypeArgument;
	}

}
