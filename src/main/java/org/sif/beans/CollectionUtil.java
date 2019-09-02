package org.sif.beans;

import java.lang.reflect.Array;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

import static org.sif.beans.Classes.classFor;
import static org.sif.beans.Classes.typeFor;
import static org.sif.beans.Debugger.debug;

/**
 * Contains utility methods for handling collection of values
 * 
 * @author Carlos Eugenio P. da Purificacao
 * 
 */
@SuppressWarnings("unused")
@Named
public class CollectionUtil<T> {

	Logger log = LoggerFactory.getLogger(CollectionUtil.class);

	/**
	 * If the given object is a collection, returns the first element on the collection, if there is
	 * one, or else return the object itself
	 */
	public Object getFirstCollectionElement(final Object collectionObject, Class<?> type) {
		log.debug("Getting first element for: " + collectionObject);
		if (!isCollectionOfAnyType(collectionObject)) {
			log.debug("Element is not a collection!");
			return collectionObject;
		} else {
			PropertyValueConverterUtil converterUtil = new PropertyValueConverterUtil();
			Collection collection = converterUtil.valueListToCollection(
					collectionObject, List.class, type);
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
		List<T> collection = toCollection(value);
		return collection.isEmpty();
	}

	private static Map<Class<?>, Class<?>> collectionImplementations;
	static {
		collectionImplementations = new HashMap<>();
		collectionImplementations.put(Set.class, HashSet.class);
		collectionImplementations.put(HashSet.class, HashSet.class);
		collectionImplementations.put(List.class, ArrayList.class);
		collectionImplementations.put(ArrayList.class, ArrayList.class);
		collectionImplementations.put(AbstractList.class, ArrayList.class);
	}

	private Class<?> implementation(Class<?> type) {
		Optional<Class<?>> implementationKey = collectionImplementations.keySet().stream()
				.filter(it -> it.isAssignableFrom(type)).findFirst();
		if (!implementationKey.isPresent()) {
			throw new IllegalArgumentException("Couldn't find a collection implementation for: " + type);
		}
		return collectionImplementations.get(implementationKey.get());
	}

	public Collection<?> newCollection(Class<?> collectionType) {
		try {
			return (Collection<?>) implementation(collectionType).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<T> toCollection(Object value) {
		if (value == null) {
			return Collections.emptyList();
		}
		Class<?> collectionType = List.class;
		Class<?> elementType = typeFor(value);
		log.debug("Converting value " + debug(value) + " of class: "
				+ classFor(value).getSimpleName() + ", to a " + collectionType.getSimpleName() + " of type: " +
				elementType + " ...");
		PropertyValueConverterUtil<T> converterUtil = new PropertyValueConverterUtil();
		if (elementType.isArray() || Collection.class.isAssignableFrom(elementType)) {
			log.debug("The provided type for value is an Array or Collection. The result Collection will be of Strings!");
			elementType = String.class;
		}
		return converterUtil.asList((Class<T>) elementType, value);
	}
}
