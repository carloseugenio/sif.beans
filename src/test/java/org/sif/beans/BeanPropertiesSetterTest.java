package org.sif.beans;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sif.beans.persistence.jpa.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class BeanPropertiesSetterTest {

	BeanPropertiesSetter bps = new BeanPropertiesSetter();
	TestBean bean = new TestBean();
	Map<String, Object> parameters = new HashMap<>();

	ManyToOneRelationPropertySetter manyToOneRelationPropertySetter;

	PropertySetterFactory factory = spy(new PropertySetterFactory());

	private void addParameter(String key, Object value) {
		parameters.put(key, value);
	}

	@Before
	public void setup() {

		factory.setManyToOneRelationSetter(manyToOneRelationPropertySetter);

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

		PersistenceManager manager = mock(PersistenceManager.class);
		manyToOneRelationPropertySetter = mock(ManyToOneRelationPropertySetter.class);
		((ManyToOneRelationPropertySetter) manyToOneRelationPropertySetter).setFacade(manager);

		when(factory.getFor(any(), anyString())).thenReturn(manyToOneRelationPropertySetter);

		when(((ManyToOneRelationPropertySetter) manyToOneRelationPropertySetter).
				doSetProperty(any(), any(), any())).then(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return manyToOneRelationPropertySetter;
			}
		});

		when(((ManyToOneRelationPropertySetter) manyToOneRelationPropertySetter).
				setProperty(any(), any(), any())).then(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return manyToOneRelationPropertySetter;
			}
		});

		addParameter("department", department);
		bps.setAllProperties(employee, parameters);
		assertEquals(department, employee.getDepartment());
	}

}

