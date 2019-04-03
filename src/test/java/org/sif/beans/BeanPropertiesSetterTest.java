package org.sif.beans;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sif.beans.persistence.jpa.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BeanPropertiesSetterTest {

	BeanPropertiesSetter bps = new BeanPropertiesSetter();
	TestBean bean = new TestBean();
	Map<String, Object> parameters = new HashMap<>();

	@Mock
	ManyToOneRelationPropertySetter manyToOneRelationPropertySetter;

	@Mock
	PersistenceManager facade;

	PropertySetterFactory factory = spy(new PropertySetterFactory());

	private void addParameter(String key, Object value) {
		parameters.put(key, value);
	}

	@Before
	public void setup() {

		factory.setManyToOneRelationSetter(manyToOneRelationPropertySetter);
		when(manyToOneRelationPropertySetter.getFacade()).thenReturn(facade);

		PropertySetter simpleSetter = new SimplePropertySetter();
		PropertyValueConverterUtil converterUtil = new PropertyValueConverterUtil();
		((SimplePropertySetter) simpleSetter).setConverterUtil(converterUtil);

		((AbstractJPAPropertySetter) simpleSetter).setRelationFacade(mock(PersistenceManager.class));
		((AbstractJPAPropertySetter) simpleSetter).setFacade(mock(PersistenceManager.class));
		factory.setSimplePropertySetter(simpleSetter);

		PropertyRelationUtil relationUtil = new PropertyRelationUtil();
		factory.setPropertyRelationUtil(relationUtil);
		bps.setFactory(factory);
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

	@Test
	public void testNamePropertySetToNull() {
		addParameter("name", null);
		bps.setAllProperties(bean, parameters);
		assertNull(bean.getName());
	}


	@Test
	public void testNamePropertySetToEmpty() {
		addParameter("name", "");
		bps.setAllProperties(bean, parameters);
		assertEquals("", bean.getName());
	}

	@Test
	public void testSetPropertiesIgnoreProperty() {
		String name = "NameTest";
		String originalName = bean.getName();
		addParameter("name", name);
		addParameter(BeanPropertiesSetter.IGNORE_PROPERTY, "name");
		bps.setAllProperties(bean, parameters);
		assertEquals(originalName, bean.getName());
	}

	@Test
	public void testSetPropertiesIgnorePropertyIfEmpty() {
		String originalName = bean.getName();
		addParameter("name", "");
		addParameter(BeanPropertiesSetter.IGNORE_EMPTY_PROPERTY, "name");
		bps.setAllProperties(bean, parameters);
		assertEquals(originalName, bean.getName());
	}

	@Test
	public void testSetPropertiesIgnorePropertyIfNull() {
		String originalName = bean.getName();
		addParameter("name", null);
		addParameter(BeanPropertiesSetter.IGNORE_EMPTY_PROPERTY, "name");
		bps.setAllProperties(bean, parameters);
		assertEquals(originalName, bean.getName());
	}

	@Test
	public void testRelationPropertySet() {
		Employee employee = new Employee();
		Department department = new Department();


		addParameter("department", department);

		bps.setAllProperties(employee, parameters);

		verify(this.manyToOneRelationPropertySetter, times(1)).doSetProperty(employee, "department", department);
	}

	@Test
	public void testRelationPropertyUnSet() {
		Employee employee = new Employee();

		addParameter(BeanPropertiesSetter.DISSOCIATE_PREFIX + "department", 1);

		bps.setAllProperties(employee, parameters);

		verify(this.manyToOneRelationPropertySetter,
				times(1)).unsetProperty(employee, "department", 1);
	}


}

