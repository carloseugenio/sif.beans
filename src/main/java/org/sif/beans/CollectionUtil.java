package org.sif.beans;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.sif.beans.Classes.classFor;
import static org.sif.beans.Debugger.debug;

/**
 * Contains utility methods for handling collection of values
 *
 * @author Carlos Eugenio P. da Purificacao
 */
@SuppressWarnings("unused")
@Named
public class CollectionUtil<T> {

	private static Map<Class<?>, Class<?>> collectionImplementations;

	static {
		collectionImplementations = new HashMap<>();
		collectionImplementations.put(Set.class, HashSet.class);
		collectionImplementations.put(HashSet.class, HashSet.class);
		collectionImplementations.put(List.class, ArrayList.class);
		collectionImplementations.put(ArrayList.class, ArrayList.class);
		collectionImplementations.put(AbstractList.class, ArrayList.class);
	}

	private Logger log = LoggerFactory.getLogger(CollectionUtil.class);

	/**
	 * If the given object is a collection, returns the first element on the collection.
	 *
	 * @throws IllegalArgumentException if the provided value is not a collection.
	 * @throws NoSuchElementException   if the provided collection is empty.
	 */
	public <T> T getFirstCollectionElement(final Object value, Class<T>... type) {
		log.debug("Getting first element for: {} and type {}", value, type);
		if (!isCollectionOfAnyType(value)) {
			throw new IllegalArgumentException("The provided value (" + value + ") is not a collection");
		} else {
			PropertyValueConverterUtil converterUtil = new PropertyValueConverterUtil();
			Class<?> finalType = String.class;
			if (isNotMissing(type)) {
				finalType = type[0];
			} else if (isArrayCollection(value)) {
				log.debug("Type not provided, but the value is a RAW array. Getting its type");
				Class<?> arrayType = value.getClass().getComponentType();
				log.debug("Array type: {}", arrayType);
				finalType = arrayType;
			}
			log.debug("Getting first element collection for type {}", finalType);
			Collection<T> collection = converterUtil.valueListToCollection(value, List.class, finalType);
			if (!collection.isEmpty()) {
				return collection.iterator().next();
			} else {
				throw new NoSuchElementException("The provided collection (" + value + ") is empty!");
			}
		}
	}

	private <T> boolean isNotMissing(Class<T>[] type) {
		return type != null && type.length > 0;
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
	 * Returns whether the provided value is a String representing a comma separated values of Number elements.
	 */
	public boolean isStringCommaSeparatedNumberArray(Object value) {
		if (value == null) {
			return false;
		}
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
	 * Returns whether the provided value is a String representing a comma separated values of any type of elements.
	 */
	public boolean isStringCommaSeparatedArray(Object value) {
		String[] splitArray = StringUtils.split(String.valueOf(value), ',');
		if (isEmptyOrOneElementArray(splitArray)) {
			return false;
		}
		return true;
	}

	private boolean isEmptyOrOneElementArray(String[] splitArray) {
		return splitArray == null || ArrayUtils.isEmpty(splitArray) || splitArray.length == 1;
	}

	private boolean isNotNumber(String item) {
		return !NumberUtils.isNumber(item);
	}

	/**
	 * Returns true if this value is a collection. It will evaluate to true collections, arrays and even comma separated
	 * values. If it is a comma separated values, it must conform to {@link #isStringCommaSeparatedNumberArray(Object)}.
	 */
	public boolean isCollection(Object value) {
		return value != null && (this.isRawCollection(value) || this.isArrayCollection(value) || this
				.isStringCommaSeparatedNumberArray(value));
	}

	/**
	 * Returns true if this value is a collection. It will evaluate to true collections, arrays and even comma separated
	 * values. This will be true even if the elements are not numbers.
	 */
	public boolean isCollectionOfAnyType(Object value) {
		return value != null && (this.isRawCollection(value) || this.isArrayCollection(value) || this
				.isStringCommaSeparatedArray(value));
	}

	/**
	 * Returns true if the value is null or if it is a collection and the collection is empty.
	 */
	public boolean isEmpty(Object value) {
		// If null it is empty
		if (value == null) {
			return true;
		}
		PropertyValueConverterUtil converterUtil = new PropertyValueConverterUtil();
		Collection<T> collection = converterUtil.valueListToCollection(value, List.class, Object.class);
		return collection.isEmpty();
	}

	private Class<?> implementation(Class<?> type) {
		Optional<Class<?>> implementationKey =
				collectionImplementations.keySet().stream().filter(it -> it.isAssignableFrom(type)).findFirst();
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

	/**
	 * Returns the given value as a {@link java.util.List}. If the value is null, an empty List is
	 * returned. If it is a single element it will be wrapped into a List, unless it is a comma
	 * separated list of values. In the last case, it will be wrapped in a List of strings. Unlike
	 * the {@link Arrays#asList(Object[])} method, it not act only on arrays. It will try to narrow
	 * any value into a {@link java.util.List}. If the provided value is a {@link java.util.Collection},
	 * or an array, it will be coerced into a {@link java.util.List}.
	 *
	 * @param value the value to be converted into a List
	 * @return the value wrapped into a List.
	 */
	public List<?> asList(Object value) {
		if (value == null) {
			return Collections.emptyList();
		}
		PropertyValueConverterUtil<T> converterUtil = new PropertyValueConverterUtil();
		Class<?> elementType = String.class;
		log.debug("Trying to transform value {}, of class {}, to List...", value, classFor(value));
		if (isCollectionOfAnyType(value)) {
			try {
				elementType = getFirstCollectionElement(value, elementType).getClass();
			} catch (Exception ex) {
				log.warn("Could not infer the collection element type!");
			}
		} else {
			elementType = value.getClass();
		}
		log.debug("Converting value {} of class: {} to a List", debug(value), elementType);
		List<?> convertedValue = (List<?>) converterUtil.valueListToCollection(value, List.class, elementType);
		return convertedValue;
	}
}
