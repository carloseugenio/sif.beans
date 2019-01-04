package org.sif.beans;

import java.lang.reflect.Field;

public class Classes {

	public static Class<?> classFor(Object obj) {
		if (obj == null) {
			return null;
		}
		return obj.getClass();
	}

	public static Class<?> getFieldClass(Class<?> bean, String property) {
		try {
			Field f = bean.getField(property);
			return f.getType();
		} catch (Exception ex) {
			return null;
		}
	}

	public static boolean isPrimitiveArrayType(Class<?> clazz) {
		return clazz.isArray() && clazz.getComponentType().isPrimitive();
	}

	public static boolean isIntArray(Class<?> clazz) {
		return clazz.getComponentType().isPrimitive() && clazz.getComponentType() == int.class;
	}
}
