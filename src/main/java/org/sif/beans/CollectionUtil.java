package org.sif.beans;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
/**
 * Contains utility methods for handling collection of values
 * 
 * @author Carlos Eugenio P. da Purificacao
 * 
 */
public class CollectionUtil {

	/**
	 * Returns whether the provided value is a subclass of Collection
	 */
	public boolean isRawCollection(Object value) {
		if (Collection.class.isAssignableFrom(value.getClass())) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether the provided value is a Java Array of some class
	 */
	public boolean isArrayCollection(Object value) {
		if ((value.getClass()).isArray()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether the provided value is a String representing a comma
	 * separated values of elements.
	 */
	public boolean isStringCommaSeparatedNumberArray(Object value) {
		String[] splitArray = StringUtils.split(String.valueOf(value), ',');
		if (isEmptyOrOneElementArray(splitArray)) {
			return false;
		}
		for (String item : splitArray) {
			if (isNotNumber(StringUtils.trim(item))) {
				return false;
			}
		}
		return true;
	}

	private boolean isEmptyOrOneElementArray(String[] splitArray) {
		return splitArray == null || ArrayUtils.isEmpty(splitArray)
				|| splitArray.length == 1;
	}

	private boolean isNotNumber(String item) {
		return !NumberUtils.isNumber(item);
	}

	/**
	 * Returns true if this value is a collection. It will evaluate to true
	 * collections, arrays and even comma separated values.
	 */
	public boolean isCollection(Object value) {
		return value != null
				&& (this.isRawCollection(value)
						|| this.isArrayCollection(value) || this
							.isStringCommaSeparatedNumberArray(value));
	}

	/**
	 * Returns true if the value is null or if it is a collection and the
	 * collection is empty.
	 */
	public boolean isEmpty(Object value) {
		// If null it is empty
		if (value == null) {
			return true;
		}
		if (isCollection(value)) {
			if (isRawCollection(value)) {
				@SuppressWarnings("rawtypes")
				Collection col = (Collection) value;
				return col.isEmpty();
			} else if (isArrayCollection(value)) {
				return Array.getLength(value) == 0;
			}
		}
		return false;
	}

	private static Map<Class<?>, Class<?>> collectionImplentations;
	static {
		collectionImplentations = new HashMap<Class<?>, Class<?>>();
		collectionImplentations.put(Set.class, HashSet.class);
		collectionImplentations.put(HashSet.class, HashSet.class);
		collectionImplentations.put(List.class, ArrayList.class);
		collectionImplentations.put(ArrayList.class, ArrayList.class);
	}

	public Collection<?> newCollection(Class<?> collectionType)
			throws Exception {
		return (Collection<?>) collectionImplentations.get(collectionType)
				.newInstance();
	}

}
