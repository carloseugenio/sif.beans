package org.sif.beans;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.sif.beans.persistence.jpa.ManyToOneRelationPropertySetter;
import org.sif.beans.persistence.jpa.OneToManyRelationPropertySetter;
import org.sif.beans.persistence.jpa.OneToOneRelationPropertySetter;
import org.sif.beans.persistence.jpa.SimplePropertySetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		loader= AnnotationConfigContextLoader.class,
		classes=ApplicationContextTestConfigurer.class)
public class PropertySetterFactoryTest {

	@Autowired
	PropertySetterFactory factory;

	@Test
	public void testInject() {
		assertNotNull(factory);
	}

	@Test
	public void testGetSimplePropertySetter() {
		Employee employee = new Employee();
		PropertySetter setter = factory.getFor(employee, "name");
		assertNotNull(setter);
		assertThat(setter, instanceOf(SimplePropertySetter.class));
	}

	@Test
	public void testGetOneToManyPropertySetter() {
		Employee employee = new Employee();
		PropertySetter setter = factory.getFor(employee, "departments");
		assertNotNull(setter);
		assertThat(setter, instanceOf(OneToManyRelationPropertySetter.class));
	}

	@Test
	public void testGetManyToOnePropertySetter() {
		Department department = new Department();
		PropertySetter setter = factory.getFor(department, "coordinator");
		assertNotNull(setter);
		assertThat(setter, instanceOf(ManyToOneRelationPropertySetter.class));
	}

	@Test
	public void testGetOneToOnePropertySetter() {
		Employee employee = new Employee();
		PropertySetter setter = factory.getFor(employee, "address");
		assertNotNull(setter);
		assertThat(setter, instanceOf(OneToOneRelationPropertySetter.class));
	}


}