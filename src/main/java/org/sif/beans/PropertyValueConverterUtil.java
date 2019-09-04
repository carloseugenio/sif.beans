package org.sif.beans;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.sif.beans.converters.IgnoreEmptyConverter;
import org.sif.beans.converters.SerializableConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.sif.beans.Classes.*;
import static org.sif.beans.Debugger.debug;

/**
 * Utility class for converting a simple value to a provided class.
 * <p>
 * <b>Important:</b> This class methods are not suitable for converting
 * collection values or collection properties. Use {@link CollectionUtil}
 * instead.
 *
 * @author Carlos Eugenio P. da Purificacao
 */
@Named
public class PropertyValueConverterUtil<T> {

	Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Register converters without using the default value
	 */ {
		registerConverters();
	}

	public void registerConverters() {
		Converter booleanConverter = new IgnoreEmptyConverter(new BooleanConverter());
		ConvertUtils.register(booleanConverter, Boolean.TYPE);
		ConvertUtils.register(booleanConverter, Boolean.class);

		Converter byteConverter = new IgnoreEmptyConverter(new ByteConverter());
		ConvertUtils.register(byteConverter, Byte.TYPE);
		ConvertUtils.register(byteConverter, Byte.class);

		Converter shortConverter = new IgnoreEmptyConverter(new ShortConverter());
		ConvertUtils.register(shortConverter, Short.TYPE);
		ConvertUtils.register(shortConverter, Short.class);

		Converter intConverter = new IgnoreEmptyConverter(new IntegerConverter());
		ConvertUtils.register(intConverter, Integer.TYPE);
		ConvertUtils.register(intConverter, Integer.class);

		Converter longConverter = new IgnoreEmptyConverter(new LongConverter());
		ConvertUtils.register(longConverter, Long.TYPE);
		ConvertUtils.register(longConverter, Long.class);

		Converter floatConverter = new IgnoreEmptyConverter(new FloatConverter());
		ConvertUtils.register(floatConverter, Float.TYPE);
		ConvertUtils.register(floatConverter, Float.class);

		Converter doubleConverter = new IgnoreEmptyConverter(new DoubleConverter());
		ConvertUtils.register(doubleConverter, Double.TYPE);
		ConvertUtils.register(doubleConverter, Double.class);

		Converter dateConverter = new IgnoreEmptyConverter(new DateConverter());
		ConvertUtils.register(dateConverter, java.util.Date.class);
		ConvertUtils.register(dateConverter, java.sql.Date.class);
		ConvertUtils.register(dateConverter, Timestamp.class);

		Converter serializableConverter = new SerializableConverter();
		ConvertUtils.register(serializableConverter, Serializable.class);
		//ConvertUtils.register(new CommonsDateConverter(), java.util.Date.class);
		//ConvertUtils.register(new CommonsDateConverter(), java.sql.Date.class);
		//ConvertUtils.register(new CommonsDateConverter(), java.sql.Timestamp.class);
	}

	/**
	 * Helper method for the {@link #valueListToCollection(Object, Class, Class)} method in which the
	 * collection type is always the {@link java.util.List} class.
	 *
	 * @param elementType the result list elements type
	 * @param value    the value to convert
	 * @return as list of the given type with the given elements
	 */
	public List<T> asList(Class<T> elementType, Object value) {
		log.debug("Converting value {} of class: {}, to a List of type: {} ...", debug(value), classFor(value).getSimpleName(), elementType);
		log.debug("Converting to List...");
		// The given object is an array.
		List<T> resultingList = (List<T>) valueListToCollection(value, List.class, elementType);
		log.debug("The resultingList class from Arrays.asList: {}", classFor(resultingList).getSimpleName());
		log.debug("The resulting list with size({}) values: {}", resultingList.size(), resultingList);
		return resultingList;
	}

	/**
	 * Tries to determine the correct type for an array of elements and return
	 * an appropriate {@link java.util.List} of elements converted to that type. The
	 * resulting {@link java.util.List} type will depend on the provided array. If it
	 * was an array of primitive types, the final {@link java.util.List} will contain
	 * elements with the same type as the original array.
	 * @throws IllegalArgumentException if the provided value is not an array
	 */
	public List<?> asList(Object value) {
		log.debug("Converting the array of class: "
				+ classFor(value).getSimpleName() + " to a collection...");
		if (Object[].class.isAssignableFrom(classFor(value))) {
			List<T> resultingList = Arrays.asList((T[]) value);
			log.debug("The resultingList class from Arrays.asList: "
					+ classFor(resultingList).getSimpleName());
			log.debug("The resulting list with size({}) values: {}", resultingList.size(), resultingList);
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
				log.debug("The resulting list with size({}) values: {}", resultingList.size(), resultingList);
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
	@SuppressWarnings({"rawtypes", "unchecked"})
	private Collection<?> convertAll(Class<?> elementType, Collection<?> values) {
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
	@SuppressWarnings({"rawtypes", "unchecked"})
	private Collection stringArrayToCollection(String value,
											   Class<? extends Collection> collectionType, Class<?> elementType) {
		CollectionUtil collectionUtil = new CollectionUtil();
		Collection collection = collectionUtil.newCollection(collectionType);
		log.debug("Created new collection instance: "
				+ classFor(collection).getSimpleName());
		List<String> stringList = Arrays.asList(StringUtils.split(value, ","));
		log.debug("Created a simple string list for the comma separated values: "
				+ stringList);
		for (String str : stringList) {
			String trimmed = str.trim();
			log.trace("Conventing the list element " + debug(trimmed)
					+ " to the provided type [" + elementType.getSimpleName());
			Object convertedElement = convert(elementType, trimmed);
			log.trace("Converted element type: "
					+ classFor(convertedElement).getSimpleName());
			collection.add(convertedElement);
		}
		return collection;
	}

	/**
	 * Converts the value to a compatible type on the target bean and property.
	 */
	public Object convert(Class<?> beanClass, String beanProperty, Object value) {
		log.debug("Getting the type for property [" + beanProperty
				+ "] of class [" + beanClass + "].");
		Class<?> fieldType = getFieldClass(beanClass, beanProperty);
		log.debug("Trying to convert value " + debug(value)
				+ " to target Field Type [" + fieldType + "]");
		return this.convert(fieldType, value);
	}

	/**
	 * Converts the value to the provided class. The value will be first
	 * transformed into a String then the conversion is attempted.
	 *
	 * @param clazz the type to convert the value to
	 * @param value the value to convert
	 * @return the value converted to the expected type
	 * @throws Exception if an error occurs in the conversion process.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Object convert(Class<?> clazz, Object value) {
		if (clazz == null) {
			throw new IllegalArgumentException(
					"The class to convert is null. Can't convert to a null type!");
		}
		if (value == null) {
			log.debug("Value is null. No conversion!");
			return null;
		}
		log.debug("Trying to convert value {} of class: {}, to target Class [{}]",
				debug(value), value.getClass(), clazz);
		if (Collection.class.isAssignableFrom(clazz)) {
			/*log.debug("This class [{}] is a collection! Using valueToCollection ...", clazz);
			Collection col = (Collection) value;
			// TODO: Why Integer? Why not Number?
			Class<?> targetType = Integer.class;
			if (!col.isEmpty()) {
				// Try to infer the type
				targetType = col.iterator().next().getClass();
			}
			return valueListToCollection(value,
					(Class<? extends Collection>) clazz, targetType);*/
			throw new IllegalArgumentException("The convert methods should not be used to convert to collections.");
		}
		if (clazz == Object.class) {
			log.warn("Asked to convert to Object.class. Not possible. Using the value class instead!");
			clazz = value.getClass();
		}
		Object convertedValue = null;
		if (value != null) {
			if (Collection.class.isAssignableFrom(value.getClass())) {
				log.warn("The value is still a collection [{}], but the target {}, is not.", value.getClass(), clazz);
				log.warn("Attempting to get the first element...");
				Collection colValue = (Collection) value;
				if (!colValue.isEmpty()) {
					value = colValue.iterator().next();
					log.warn("Converted the collection element to: {}", debug(value));
				}
			}
			log.debug("Converting {} to [{}]", debug(value), clazz);
			Converter converter = ConvertUtils.lookup(clazz);
			log.debug("Converter found: {}", converter);
			convertedValue = converter.convert(clazz, value);
			log.debug("Converted value: {}", debug(convertedValue));
			if (convertedValue != null) {
				log.debug("ConvertedType: {}", classFor(convertedValue));
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
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Collection<?> valueListToCollection(Object value,
											   Class<? extends Collection> collectionType, Class<?> elementType) {
		log.debug("The conversion from value {} to the collection type [{}] will result in elements of type [{}]",
				debug(value), collectionType.getSimpleName(), elementType.getSimpleName());
		CollectionUtil collectionUtil = new CollectionUtil();
		Collection elements = collectionUtil.newCollection(collectionType);
		log.debug("Created new Collection instance of class: {}", classFor(elements).getSimpleName());
		if (collectionUtil.isRawCollection(value)) {
			log.debug("This is array of raw collection of values from a Collection subtype: {}", value);
			Collection<?> allConverted = convertAll(elementType, (Collection) value);
			log.debug("All elements converted with size({}): {}", allConverted.size(), allConverted);
			elements.addAll(allConverted);
		} else if (collectionUtil.isArrayCollection(value)) {
			log.debug("This is a Java Array of elements...");
			Collection asList = asList(value);
			log.debug("Created list from the array: {}", asList);
			elements.addAll(convertAll(elementType, asList));
		} else if (collectionUtil.isStringCommaSeparatedArray(value)) {
			log.debug("This is a String Comma Separated Array: {}", value);
			Collection stringArrayAsTypeCollection = stringArrayToCollection(
					value.toString(), collectionType, elementType);
			log.debug("The stirng list was converted to: {}", stringArrayAsTypeCollection);
			elements.addAll(stringArrayAsTypeCollection);
		} else if (value != null) {
			// & Number.class.isAssignableFrom(elementType) & NumberUtils.isCreatable(value.toString())) {
			log.debug("The value is a single element convertible to a Number!");
			Object converted = convert(elementType, value);
			log.debug("Converted: {}", converted);
			elements.add(converted);
		}
		return elements;
	}

}
