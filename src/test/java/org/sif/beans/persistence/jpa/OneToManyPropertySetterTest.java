package org.sif.beans.persistence.jpa;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sif.beans.CollectionUtil;
import org.sif.beans.Department;
import org.sif.beans.Employee;
import org.sif.beans.PropertyValueConverterUtil;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class OneToManyPropertySetterTest {

	PropertyValueConverterUtil converterUtil = new PropertyValueConverterUtil();
	ManyToManyRelationPropertySetter setter = new ManyToManyRelationPropertySetter();

	@Mock
	PersistenceManager facade;

	@Mock
	PersistenceManager relationFacade;

	Department dep1 = new Department();
	Department dep2 = new Department();

	@Before
	public void setup() {
		dep1.setId(1L);
		dep1.setName("dep1");
		dep2.setId(2L);
		dep2.setName("dep2");

		setter.converterUtil = converterUtil;
		setter.setFacade(facade);
		setter.setRelationFacade(relationFacade);
		doReturn(dep1).when(relationFacade).load(1L);
		doReturn(dep2).when(relationFacade).load(2L);
		doReturn(Arrays.asList(dep1, dep2)).when(relationFacade).findByField("id", "1,2");
		setter.collectionUtil = new CollectionUtil();
	}

	@Test
	public void testSetOneManyWithOneElement() {
		Employee bean = new Employee();
		bean.setId(1L);
		bean.setName("employee1");
		setter.setProperty(bean, "departments", dep1.getId());
		assertEquals(1, bean.getDepartments().size());
	}

	@Test
	public void testSetOneManyWithTwoElements() {
		Employee bean = new Employee();
		bean.setId(1L);
		bean.setName("employee1");
		setter.setProperty(bean, "departments", dep1.getId() + "," + dep2.getId());
		assertEquals(2, bean.getDepartments().size());
	}

}
