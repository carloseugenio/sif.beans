package org.sif.beans;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class BeanPropertiesSetterTest {

	BeanPropertiesSetter setter = new BeanPropertiesSetter();

	@Before
	public void setup() {
		BeanPropertySetterFactory factory = new BeanPropertySetterFactory();
		factory.setSimplePropertySetter(new SimplePropertySetter());
		setter.setFactory(factory);
	}

	@Test
	public void setAllProperties() {
		Employee bean = new Employee();
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("id", 1L);
		properties.put("name", "Test");
		setter.setAllProperties(bean, properties);
		assertEquals(bean.getName(), "Test");
		assertEquals(bean.getId(), new Long(1L));
	}

	@Test
	public void setAllPropertiesWithDissociate() {
		Employee bean = new Employee();
		bean.setDepartment(new Department());
		HashMap<String, Object> properties = new HashMap<>();
		properties.put(BeanPropertiesSetter.DISSOCIATE_PREFIX + "department", null);
		setter.setAllProperties(bean, properties);
		assertEquals(null, bean.getDepartment());
	}

	@Test
	public void setFactory() {
		setter.setFactory(new BeanPropertySetterFactory());
		assertNotNull(setter.getFactory());
	}
}

class SimplePropertySetter implements PropertySetter {

	@Override
	public Object setProperty(Object bean, String property, Object value) {
		try {
			PropertyUtils.setProperty(bean, property, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

	@Override
	public Object unsetProperty(Object bean, String property, Object value) {
		try {
			PropertyUtils.setProperty(bean, property, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;

	}
}