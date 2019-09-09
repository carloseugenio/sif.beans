package org.sif.beans;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class Classes {

	/**
	 * Avoid NPE when getting an object's class
	 * @param obj the given query the class
	 * @return the object's class or null if the given object parameter is null
	 */
	public static Class<?> classFor(Object obj) {
		if (obj == null) {
			return null;
		}
		return obj.getClass();
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
	 * Returns the field in the target bean. This method will recurse to supper
	 * classes.
	 */
	public static Field getField(Class<?> beanClass, String fieldName) {
		if (isNested(fieldName)) {
			String lastPart = fieldName.substring(fieldName.indexOf(".") + 1);
			String firstPart = fieldName.substring(0, fieldName.indexOf("."));
			Class<?> firstType = getFieldClass(beanClass, firstPart);
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
	 * Returns true if the given string is using a dot separated form, e.g. "object.property"
	 * @param property the string to verify
	 * @return true if the string is using dot separated notation
	 */
	public static boolean isNested(String property) {
		if (StringUtils.trimToNull(property) == null) {
			return false;
		}
		return property.indexOf(".") != -1;
	}

	/**
	 * Returns true if the given class parameter is a primitive type array
	 * @param clazz the class to verify
	 * @return if the given class is a primitive array.
	 */
	public static boolean isPrimitiveArrayType(Class<?> clazz) {
		return clazz.isArray() && clazz.getComponentType().isPrimitive();
	}

	/**
	 * Returns true if the given class parameter is a primitive int array
	 * @param clazz the class to verify
	 * @return if the given class is a primitive int array.
	 */
	public static boolean isIntArray(Class<?> clazz) {
		return isPrimitiveArrayType(clazz) && clazz.getComponentType() == int.class;
	}

	public static Object getPropertyIgnoreNull(Object bean, String property) {
		Object ret;
		String errmsg = String.format("The bean [%s] does not have the [%s] property or it is not accessible.",
				classFor(bean), property);
		try {
			if (property.indexOf(".") != -1) {
				try {
					ret = PropertyUtils.getNestedProperty(bean, property);
				} catch (org.apache.commons.beanutils.NestedNullException nne) {
					return null;
				}
			} else {
				ret = PropertyUtils.getProperty(bean, property);
			}
		} catch (InvocationTargetException ex) {
			throw new RuntimeException("Exception getting property [" + property + "] on bean [" + bean + "]: " + ex, ex);
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().trim().indexOf("Null property value for") != -1) {
				// Ignoring a null property
				return null;
			}
			throw new IllegalArgumentException(
					"Bean ou property nullo. [bean: " + bean + ", property: " + property + "]");
		} catch (Exception ex) {
			throw new IllegalArgumentException(errmsg, ex);
		}
		return ret;
	}

	/**
	 * Returns the bean property names.
	 * @param bean the bean to verify the properties
	 * @return a collection of property names
	 */
	public static Collection<String> getPropertyNames(Object bean) {
		Collection<String> names = new ArrayList<String>();
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean);
		for (int i = 0; i < pds.length; names.add(pds[i++].getName()));
		return names;
	}

}
