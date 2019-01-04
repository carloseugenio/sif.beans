package org.sif.beans;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.sif.beans.Classes.*;

/**
 * Utility class for converting a simple value to a provided class.
 * <p>
 * <b>Important:</b> This class methods are not suitable for converting
 * collection values or collection properties. Use {@link CollectionUtil}
 * instead.
 * 
 * @author Carlos Eugenio P. da Purificacao
 * 
 */
public class PropertyValueConverterUtil {

	Logger log = LoggerFactory.getLogger(getClass());

	CollectionUtil collectionUtil = new CollectionUtil();

	/**
	 * Tries to determine the correct type for an array of elements and return
	 * an appropriate collection of elements converted to that type. The
	 * resulting collection type will depend on the provided collection. If it
	 * was an array of primitive type, the final collection will contain
	 * elements with the same type as the original array.
	 */
	Collection<?> asList(Object value) {
		log.debug("Converting the array of class: "
				+ classFor(value).getSimpleName() + " to a collection...");
		if (Object[].class.isAssignableFrom(classFor(value))) {
			Collection<?> resultingList = Arrays.asList((Object[]) value);
			log.debug("The resultingList class from Arrays.asList: "
					+ classFor(resultingList).getSimpleName());
			log.debug("The resulting list values: " + resultingList);
			return resultingList;
		}
		if (isPrimitiveArrayType(classFor(value))) {
			log.debug("This is a primitive type array...");
			if (isIntArray(classFor(value))) {
				log.debug("Int array detected. Converting...");
				int[] intArray = (int[]) value;
				log.debug("Int array [" + intArray.length + "] : "
						+ Arrays.toString(intArray));
				Integer[] integerResultingList = ArrayUtils.toObject(intArray);
				List<Integer> resultingList = Arrays
						.asList(integerResultingList);
				log.debug("Resulting list[" + resultingList.size() + "]: "
						+ resultingList);
				return resultingList;
			} else {
				return null;
			}
		} else {
			throw new IllegalArgumentException(
					"The value must be an object of some type array!!!");
		}
	}

	/**
	 * Converts all elements of provided collection to the provided element
	 * type.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection<?> convertAll(Class<?> elementType, Collection<?> values)
			throws Exception {
		log.debug("Converting all values " + values + ", to type: "
				+ elementType);
		List convertedValues = new ArrayList();
		for (Object valueToConvert : values) {
			convertedValues.add(convert(elementType, valueToConvert));
		}
		return convertedValues;
	}

	/**
	 * Converts the provided value, that must be a String with numbers separated
	 * with commas, to a Collection of collectionType with elements of
	 * elementType.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection stringArrayToCollection(String value,
			Class<? extends Collection> collectionType, Class<?> elementType)
			throws Exception {
		Collection collection = collectionUtil.newCollection(collectionType);
		log.debug("Created new collection instance: "
				+ classFor(collection).getSimpleName());
		List<String> stringList = Arrays.asList(StringUtils.split(value, ","));
		log.debug("Created a simple string list for the comma separated values: "
				+ stringList);
		for (String string : stringList) {
			log.trace("Conventing the list element [" + string
					+ "] to the provided type [" + elementType.getSimpleName());
			Object convertedElement = convert(elementType, string);
			log.trace("Converted element type: "
					+ classFor(convertedElement).getSimpleName());
			collection.add(convertedElement);
		}
		return collection;
	}

	/**
	 * Converts the value to a compatible type on the target bean and property.
	 */
	public Object convert(Class<?> beanClass, String beanProperty, Object value)
			throws Exception {
		log.debug("Getting the type for property [" + beanProperty
				+ "] of class [" + beanClass + "].");
		Class<?> fieldType = getFieldClass(beanClass, beanProperty);
		log.debug("Trying to convert value [" + value
				+ "] to target Field Type [" + fieldType + "]");
		return this.convert(fieldType, value);
	}

	/**
	 * Converts the value to the provided class. The value will be first
	 * transformed into a String then the conversion is attempted.
	 * 
	 * @param clazz
	 *            the type to convert the value to
	 * @param value
	 *            the value to convert
	 * @return the value converted to the expected type
	 * @throws Exception
	 *             if an error occurs in the conversion process.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object convert(Class<?> clazz, Object value) throws Exception {
		if (clazz == null) {
			throw new IllegalArgumentException(
					"The class to convert is null. Can't convert to a null type!");
		}
		log.debug("Trying to convert value [" + value + "] to target Class ["
				+ clazz + "]");
		if (Collection.class.isAssignableFrom(clazz)) {
			log.debug("This class [" + clazz.getSimpleName()
					+ "] is a collection! Using valueToCollection ...");
			// TODO: Why Integer? Why not Number?
			return valueListToCollection(value,
					(Class<? extends Collection>) clazz, Integer.class);
		}
		Object convertedValue = null;
		if (value != null) {
			// >>>>>>>>> Verify collection???
			log.debug("Converting [" + value + "] to [" + clazz + "]");
			String propertyValue = value.toString();
			convertedValue = ConvertUtils.convert(propertyValue, clazz);
			log.debug("Converted value: " + convertedValue);
			if (convertedValue != null) {
				log.debug("ConvertedType: " + classFor(convertedValue));
			}
		}
		return convertedValue;
	}

	/**
	 * This is a helper method capable to transform a raw value, either as a
	 * string separated comma of values, a real collection of values or an array
	 * of values to the provided collection class, whose elements will be of
	 * provided type.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> Collection<?> valueListToCollection(Object value,
			Class<? extends Collection> collectionType, Class<?> elementType)
			throws Exception {
		log.debug("The convertion from value [" + value
				+ "] to the collection type [" + collectionType.getSimpleName()
				+ "] will result in elements of type ["
				+ elementType.getSimpleName() + "]");
		Collection elements = collectionUtil.newCollection(collectionType);
		log.debug("Created new Collection instance of class: "
				+ classFor(elements).getSimpleName());
		if (collectionUtil.isRawCollection(value)) {
			log.debug("This is array of raw collection of values from a Collection subtype: "
					+ value);
			elements.addAll(convertAll(elementType, (Collection) value));
		} else if (collectionUtil.isArrayCollection(value)) {
			log.debug("This is a Java Array of elements...");
			Collection asList = asList(value);
			log.debug("Created list from the array: " + asList);
			elements.addAll(convertAll(elementType, asList));
		} else if (collectionUtil.isStringCommaSeparatedNumberArray(value)) {
			log.debug("This is a String Comma Separated Array: " + value);
			Collection stringArrayAsTypeCollection = stringArrayToCollection(
					value.toString(), collectionType, elementType);
			log.debug("The stirng list was converted to: "
					+ stringArrayAsTypeCollection);
			elements.addAll(stringArrayAsTypeCollection);
		}
		return elements;
	}

}
