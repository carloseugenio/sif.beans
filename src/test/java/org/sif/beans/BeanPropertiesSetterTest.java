package org.sif.beans;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sif.beans.persistence.jpa.AbstractJPAPropertySetter;
import org.sif.beans.persistence.jpa.PersistenceManager;
import org.sif.beans.persistence.jpa.PropertyRelationUtil;
import org.sif.beans.persistence.jpa.SimplePropertySetter;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BeanPropertiesSetterTest {

	BeanPropertiesSetter bps = new BeanPropertiesSetter();
	TestBean bean = new TestBean();
	Map<String, Object> parameters = new HashMap<>();

	private void addParameter(String key, Object value) {
		parameters.put(key, value);
	}

	@Before
	public void setup() {
		PropertySetterFactory factory = new PropertySetterFactory();
		PropertySetter simpleSetter = new SimplePropertySetter();
		((AbstractJPAPropertySetter)simpleSetter).setRelationFacade(Mockito.mock(PersistenceManager.class));
		((AbstractJPAPropertySetter)simpleSetter).setFacade(Mockito.mock(PersistenceManager.class));
		factory.simplePropertySetter = simpleSetter;
		PropertyRelationUtil relationUtil = new PropertyRelationUtil();
		factory.propertyRelationUtil = relationUtil;
		bps.factory = factory;
	}

	@Test
	public void testNoPropertiesSet() {
		bps.setAllProperties(bean, parameters);
		assertNull(bean.getName());
	}

	@Test
	public void testNonReadableProperty() {
		addParameter("nonReadable", "any value");
		bps.setAllProperties(bean, parameters);
	}

	@Test
	public void testNamePropertySet() {
		String name = "NameTest";
		addParameter("name", name);
		bps.setAllProperties(bean, parameters);
		assertEquals(name, bean.getName());
	}

}

