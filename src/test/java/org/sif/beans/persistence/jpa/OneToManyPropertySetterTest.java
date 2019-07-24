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
	OneToManyRelationPropertySetter setter = new OneToManyRelationPropertySetter();

	@Mock
	PersistenceManager facade;

	@Mock
	PersistenceManager relationFacade;

	Employee bean = new Employee();
	Department dep1 = new Department();
	Department dep2 = new Department();
	Employee friend1 = new Employee();

	@Before
	public void setup() {
		bean.setId(1L);
		bean.setName("employee1");

		dep1.setId(1L);
		dep1.setName("dep1");
		dep2.setId(2L);
		dep2.setName("dep2");
		friend1.setId(10L);
		friend1.setName("friend1");

		setter.converterUtil = converterUtil;
		setter.setFacade(facade);
		setter.setRelationFacade(relationFacade);
		doReturn(dep1).when(relationFacade).load(1L);
		doReturn(dep2).when(relationFacade).load(2L);
		// Used by departments relation
		doReturn(Arrays.asList(dep1, dep2)).when(relationFacade).findByField("id", "1,2");
		// Used by friends relation
		doReturn(friend1).when(relationFacade).load(10L);
		doReturn(Arrays.asList(bean)).when(relationFacade).findByField("id", 1L);
		setter.collectionUtil = new CollectionUtil();
	}

	@Test
	public void testSetOneManyWithOneElementUnidirectional() {
		setter.setProperty(bean, "friends", friend1.getId());
		assertEquals(1, bean.getFriends().size());
		assertEquals(friend1.getName(), bean.getFriends().iterator().next().getName());
	}


	@Test
	public void testSetOneManyWithOneElementOnBidirectionalRelation() {
		setter.setProperty(bean, "departments", dep1.getId());
		assertEquals(1, bean.getDepartments().size());
		assertEquals(bean, dep1.getCoordinator());
	}

	@Test
	public void testSetOneManyWithTwoElementOnBidirectionalRelation() {
		setter.setProperty(bean, "departments", dep1.getId() + "," + dep2.getId());
		assertEquals(2, bean.getDepartments().size());
		assertEquals(bean, dep1.getCoordinator());
		assertEquals(bean, dep2.getCoordinator());
	}

}
