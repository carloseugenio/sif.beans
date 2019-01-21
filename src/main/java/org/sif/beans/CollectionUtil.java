package org.sif.beans;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

/**
 * Contains utility methods for handling collection of values
 * 
 * @author Carlos Eugenio P. da Purificacao
 * 
 */
@SuppressWarnings("unused")
@Named
public class CollectionUtil {

	Logger log = LoggerFactory.getLogger(CollectionUtil.class);

	/**
	 * If the given object is a collection, returns the first element on the collection, if there is
	 * one, or else return the object itself
	 */
	public Object getFirstCollectionElement(final Object collectionObject) {
		log.debug("Getting first element for: " + collectionObject);
		if (!isCollection(collectionObject)) {
			log.debug("Element is not a collection!");
			return collectionObject;
		}
		if (isArrayCollection(collectionObject)) {
			Object[] objects = ((Object[])collectionObject);
			if (objects.length > 0) {
				return objects[0];
			}
		}
		if (isRawCollection(collectionObject)) {
			Collection collection = ((Collection) collectionObject);
			if (!collection.isEmpty()) {
				return collection.iterator().next();
			}
		}
		return collectionObject;
	}

	/**
	 * Returns whether the provided value is a subclass of Collection
	 */
	public boolean isRawCollection(Object value) {
		if (value != null && Collection.class.isAssignableFrom(value.getClass())) {
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
	 * separated values of Number elements.
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

	/**
	 * Returns whether the provided value is a String representing a comma
	 * separated values of any type of elements.
	 */
	public boolean isStringCommaSeparatedArray(Object value) {
		String[] splitArray = StringUtils.split(String.valueOf(value), ',');
		if (isEmptyOrOneElementArray(splitArray)) {
			return false;
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
	 * Returns true if this value is a collection. It will evaluate to true
	 * collections, arrays and even comma separated values. This will be true
	 * even if the elements are not numbers.
	 */
	public boolean isCollectionOfAnyType(Object value) {
		return value != null
				&& (this.isRawCollection(value)
				|| this.isArrayCollection(value) || this
				.isStringCommaSeparatedArray(value));
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
		collectionImplentations = new HashMap<>();
		collectionImplentations.put(Set.class, HashSet.class);
		collectionImplentations.put(HashSet.class, HashSet.class);
		collectionImplentations.put(List.class, ArrayList.class);
		collectionImplentations.put(ArrayList.class, ArrayList.class);
	}

	public Collection<?> newCollection(Class<?> collectionType) {
		try {
			return (Collection<?>) collectionImplentations.get(collectionType)
					.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
