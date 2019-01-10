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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	private AnnotationUtil() {
		if (log == null) {
			log = LoggerFactory.getLogger(classFor(this).getName());
		}
	}

	private static Logger getLog() {
		if (log == null) {
			log = LoggerFactory.getLogger(AnnotationUtil.class.getName());
		}
		return log;
	}

	/**
	 * Retrieves all class level annotations
	 * 
	 * @param cls
	 *            the class to get annotations
	 * @return all class level annotations
	 */
	public static List<Annotation> getAnnotationsForClass(Class<?> cls) {
		Annotation[] annotations = cls.getAnnotations();
		for (Annotation anno : annotations) {
			getLog().trace("Annotation in class [" + cls.getSimpleName() + "]: " + anno);
		}
		return Arrays.asList(annotations);
	}

	/**
	 * Retrieves all class level annotations, which are Qualifiers
	 * 
	 * @param cls
	 *            the class to get annotations
	 * @return all class level qualifiers annotations
	 */
	public static List<Annotation> getQualifiersAnnotationsForClass(Class<? extends Object> cls) {
		Annotation[] annotations = cls.getAnnotations();
		log.trace("All: " + Arrays.toString(annotations));
		List<Annotation> ret = new ArrayList<Annotation>();
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			log.trace("Annotation class: " + classFor(annotation) + ", type: " + annotationType);
			Qualifier qualifierAnnotation = annotationType.getAnnotation(Qualifier.class);
			log.trace("Qualifier: " + qualifierAnnotation);
			if (qualifierAnnotation != null) {
				ret.add(annotation);
			}
		}
		return ret;
	}

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
		getLog().trace("Annotation: " + annotation);
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
		log.debug("Finding field [" + fieldName + "] in bean class [" + bean.getSimpleName() + "]");
		Field field = getField(bean, fieldName);
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
	private static Field getField(Class<?> beanClass, String fieldName) {
		if (isNested(fieldName)) {
			String lastPart = fieldName.substring(fieldName.indexOf(".") + 1);
			log.trace("This is a nested field. Recursing for lastPart: " + lastPart);
			String firstPart = fieldName.substring(0, fieldName.indexOf("."));
			log.trace("Getting type for first part property [" + firstPart + "]");
			Class<?> firstType = getFieldClass(beanClass, firstPart);
			log.trace("FirstType class: [" + firstType + "]");
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
	private static Field _getField(Class<?> bean, String fieldName) {
		try {
			return bean.getField(fieldName);
		} catch (NoSuchFieldException ex) {
			log.debug("Find field [" + fieldName + "] on bean: [" + bean + "] threw exception: " + ex);
			return null;
		}
	}

	/**
	 * Returns the annotation for the bean or null if no such annotation exists.
	 * 
	 * @param bean
	 *            the bean that contains the annotation
	 * @param annotationClass
	 *            the Annotation class
	 * @return the Annotation for the the bean or null if it doesn't exists
	 */
	public static <T extends Annotation> Annotation getAnnotationForBean(Class<?> bean, Class<T> annotationClass) {
		return bean.getAnnotation(annotationClass);
	}

	/**
	 * Returns all fields in the provided bean class that are annotated with
	 * ManyToOne annotation
	 * 
	 * @param beanClass
	 *            the class to query for the annotation
	 * @return all field in the provided bean class annotated with ManyToOne
	 *         annotation
	 */
	public static List<Field> getAllManyToOneAnnotatedProperties(Class<?> beanClass) {
		List<Field> fields = new ArrayList<Field>();
		List<AnnotatedElement> annotatedFields = getAllAnnotatedElements(beanClass);
		for (AnnotatedElement element : annotatedFields) {
			getLog().trace("AnnotatedElement: " + element);
			getLog().trace("Annotations for this element: " + element.getDeclaredAnnotations().length);
			ManyToOne manyToOne = element.getAnnotation(ManyToOne.class);
			getLog().trace("HasManyToOne?: " + manyToOne);
			if (manyToOne != null) {
				fields.add((Field) element);
			}
		}
		return fields;
	}

	/**
	 * Returns whether the given property in the bean class is a many-to-one
	 * annotated field.
	 * 
	 * @param beanClass
	 *            the class to query for the annotation
	 * @param propertyName
	 *            the property name
	 * @return true if the property is annotated with a many-to-one JPA annotation.
	 */
	public static boolean isManyToOneAnnotatedProperty(Class<?> beanClass, String propertyName) {
		List<Field> manyToOneFields = getAllManyToOneAnnotatedProperties(beanClass);
		for (Field field : manyToOneFields) {
			if (field.getName().equals(propertyName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns, if any, the field name for the bean class annotated with {@link Id}
	 * JPA annotation.
	 * 
	 * @param beanClass
	 *            the bean class to query the field name
	 * @return the field name annotated with {@link Id} JPA annotation.
	 */
	public static String getBeanIdentifierAnnotatedField(Class<?> beanClass) {
		List<Field> allAnnotatedFields = getAllAnnotatedFields(beanClass);
		for (Field field : allAnnotatedFields) {
			if (fieldHasAnnotation(beanClass, field.getName(), Id.class)) {
				return field.getName();
			}
		}
		return null;
	}

	/**
	 * If the field has a OneToMany annotation, returns the generic type the
	 * collection is parameterized with.
	 * 
	 * @param beanClass
	 *            the bean class to query for the parameterized type
	 * @param fieldName
	 *            the field name.
	 * @return the parameterized type for the field collection type
	 */
	public static Class<?> getOneToManyFieldTypeArgumentClassName(Class<?> beanClass, String fieldName) {
		for (Field field : beanClass.getDeclaredFields()) {
			if (field.getName().equals(fieldName)) {
				getLog().trace("Field found: " + field);
				Type genericType = field.getGenericType();
				getLog().trace("Generic type: " + genericType);
				if (genericType == null) {
					// This field doesn't have a generic type. So I can't
					// determine the
					// correct type this one-to-many fields relates to.
					return null;
				}
				ParameterizedType parType = (ParameterizedType) genericType;
				Type[] typeArguments = parType.getActualTypeArguments();
				if (typeArguments == null || typeArguments.length == 0) {
					// No type argument specified. Can't determine the actual
					// type
					// for the relation
					return null;
				}
				// This is the type argument declared for the one-to-many field
				// like
				// Set<Entity>
				Type entityTypeArgument = typeArguments[0];
				getLog().trace("EntityTypeArgument: " + entityTypeArgument);
				// Don't know how to pick that up in another way. It comes with
				// format "class correct.type.Name". So I split and pick the
				// last string
				String className = entityTypeArgument.toString().split(" ")[1];
				try {
					getLog().trace("ClassName: " + className);
					Class<?> cls = ClassUtils.getClass(className);
					getLog().trace("Created class: " + cls);
					return cls;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// ClassUtils.getShortClassName(className);
			}
		}
		return null;
	}

	/**
	 * Returns all fields as {@link AnnotatedElement} for the bean, and in its
	 * hierarchy
	 * <p>
	 * TODO: At this moment only classes with JPA {@link Entity} annotation are
	 * considered. Parameterize it.
	 * 
	 * @param beanClass
	 *            the bean to query properties for
	 * @return all bean properties down to its hierarchy
	 */
	public static List<AnnotatedElement> getAllAnnotatedElements(Class<?> beanClass) {
		List<AnnotatedElement> annotatedFields = new ArrayList<AnnotatedElement>();
		getLog().trace("Loading all annotated fields for Many to Many...");
		fillAllAnnotatedFields(beanClass, annotatedFields);
		return annotatedFields;
	}

	/**
	 * Returns all fields for the bean, and in its hierarchy
	 * <p>
	 * TODO: At this moment only classes with JPA {@link Entity} annotation are
	 * considered. Parameterize it.
	 * 
	 * @param beanClass
	 *            the bean to query properties for
	 * @return all bean {@link Field}s down to its hierarchy
	 */
	public static List<Field> getAllAnnotatedFields(Class<?> beanClass) {
		List<AnnotatedElement> annotatedElements = new ArrayList<AnnotatedElement>();
		getLog().trace("Loading all annotated fields for bean [" + beanClass.getSimpleName() + "] ...");
		fillAllAnnotatedFields(beanClass, annotatedElements);
		List<Field> fields = new ArrayList<Field>();
		for (AnnotatedElement element : annotatedElements) {
			fields.add((Field) element);
		}
		return fields;
	}

	/**
	 * Fill the provided List of annotated fields with all annotated field for the
	 * provided bean class. This method is recursive, so it will pick up all the
	 * annotated fields for the bean super class also.
	 * <p>
	 * TODO: At this moment only classes with JPA {@link Entity} annotation are
	 * considered. Parameterize it.
	 * 
	 * @param beanClass
	 *            the bean class to query fields for
	 * @param annotatedFields
	 *            a collection to hold all annotated fields for the bean class.
	 */
	static List<AnnotatedElement> fillAllAnnotatedFields(Class<?> beanClass, List<AnnotatedElement> annotatedFields) {
		if (annotatedFields == null) {
			annotatedFields = new ArrayList<AnnotatedElement>();
		}
		if (beanClass == null) {
			getLog().error("Can't fill annotated fields for beanClass null!!!");
			return annotatedFields;
		}
		String simpleName = beanClass.getSimpleName();
		getLog().trace("Filling annotated fields for: " + simpleName);
		Field[] declaredFields = beanClass.getDeclaredFields();
		getLog().debug("Declared fields for [" + simpleName + "]: " + Arrays.toString(declaredFields));
		for (Field field : declaredFields) {
			getLog().debug("Checking field: " + field);
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			Annotation[] annotations = field.getAnnotations();
			getLog().debug("AnnotatedFields for [" + field.getName() + "]: " + Arrays.toString(annotations));
			if (ArrayUtils.isNotEmpty(annotations)) {
				annotatedFields.add(field);
			}
		}
		Class<?> base = beanClass.getSuperclass();
		if (base != null && (AnnotationUtil.hasClassLevelAnnotation(base, Entity.class)
				|| (AnnotationUtil.hasClassLevelAnnotation(base, MappedSuperclass.class)))) {
			getLog().trace("BaseClass found with entity annotation: " + base + ". Adding fields...");
			fillAllAnnotatedFields(base, annotatedFields);
		}
		getLog().trace("All fields added.");
		return annotatedFields;
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
			getLog().warn("Exception getting annotation [" + annotationClass + "] for field: ["
					+ fieldName + "] on bean [" + bean + "]: " + ex);
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
		List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();
		annotations.add(ManyToMany.class);
		annotations.add(OneToMany.class);
		annotations.add(ManyToOne.class);
		return AnnotationUtil.fieldHasAnyAnnotation(beanClass, fieldName, annotations);
	}

	public static Class<?> getTypeParameterClassForManyToManyField(Class<?> beanClass, String fieldName)
			throws Exception {
		// It is the generic type applied to the collection.
		Field annotatedField = getField(beanClass, fieldName);
		// getLog().info("Annotated field: " + annotatedField);
		Type genericType = annotatedField.getGenericType();
		// getLog().info("Generic type: " + genericType);
		if (genericType == null) {
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
		// getLog().info("EntityTypeArgument: " + entityTypeArgument);
		return (Class<?>) entityTypeArgument;
	}

}
