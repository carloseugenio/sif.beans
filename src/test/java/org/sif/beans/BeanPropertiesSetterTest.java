package org.sif.beans;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.sif.beans.persistence.jpa.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BeanPropertiesSetterTest {

	BeanPropertiesSetter bps = new BeanPropertiesSetter();
	TestBean bean = new TestBean();
	Map<String, Object> parameters = new HashMap<>();

	@Mock
	ManyToOneRelationPropertySetter manyToOneRelationPropertySetter;

	@Mock PersistenceManager facade;

	PropertySetterFactory factory = spy(new PropertySetterFactory());

	private void addParameter(String key, Object value) {
		parameters.put(key, value);
	}

	@Before
	public void setup() {

		factory.setManyToOneRelationSetter(manyToOneRelationPropertySetter);
		when(manyToOneRelationPropertySetter.getFacade()).thenReturn(facade);

		PropertySetter simpleSetter = new SimplePropertySetter();
		((AbstractJPAPropertySetter)simpleSetter).setRelationFacade(mock(PersistenceManager.class));
		((AbstractJPAPropertySetter)simpleSetter).setFacade(mock(PersistenceManager.class));
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

