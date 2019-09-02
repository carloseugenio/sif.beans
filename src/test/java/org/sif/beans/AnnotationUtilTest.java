package org.sif.beans;

import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static org.junit.Assert.*;

public class AnnotationUtilTest {
/*
	@Test(expected = IllegalArgumentException.class)
	public void testFillAnnotatedFieldsWithInvalidParameter() {
		AnnotationUtil.fillAllAnnotatedFields(null, null);
	}

	@Test
	public void fillAllAnnotatedFields() {
		List<AnnotatedElement> annotatedFields = AnnotationUtil.fillAllAnnotatedFields(Employee.class, null);
		assertNotNull(annotatedFields);
	}
*/
	@Test
	public void testGetFieldsWithAnnotation() {
		assertEquals(1, AnnotationUtil.getFieldsWithAnnotation(Employee.class, ManyToOne.class).size());
	}

	@Test
	public void testGetField() {
		assertNotNull(AnnotationUtil.getField(Employee.class, "id"));
		assertNotNull(AnnotationUtil.getField(Employee.class, "department"));
		assertNotNull(AnnotationUtil.getField(Employee.class, "name"));
		assertNotNull(AnnotationUtil.getField(Employee.class, "age"));
		assertNotNull(AnnotationUtil.getField(Employee.class, "name"));
		assertNotNull(AnnotationUtil.getField(Employee.class, "friends"));
		assertNotNull(AnnotationUtil.getField(Employee.class, "departments"));
		assertNotNull(AnnotationUtil.getField(Employee.class, "employed"));
		assertNotNull(AnnotationUtil.getField(Employee.class, "address"));
		assertNotNull(AnnotationUtil.getField(Employee.class, "departments.name"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNonExistingField() {
		AnnotationUtil.getField(Employee.class, "error");
	}

	@Test
	public void testFieldHasAnnotation() {
		assertTrue(AnnotationUtil.fieldHasAnnotation(Employee.class, "address", OneToOne.class));
	}

	@Test
	public void testFieldDoesNotHaveAnnotation() {
		assertFalse(AnnotationUtil.fieldHasAnnotation(Employee.class, "address", ManyToOne.class));
	}

	@Test
	public void testFieldHasAnnotationNonExistentField() {
		assertFalse(AnnotationUtil.fieldHasAnnotation(Employee.class, "error", OneToOne.class));
	}

	@Test
	public void testFieldHasRelationAnnotationForOneToOne() throws Exception {
		assertTrue(AnnotationUtil.fieldHasRelationAnnotation(Employee.class, "address"));
	}

	@Test
	public void testFieldHasRelationAnnotationForOneToMany() throws Exception {
		assertTrue(AnnotationUtil.fieldHasRelationAnnotation(Employee.class, "departments"));
	}

	@Test
	public void testFieldHasRelationAnnotationForManyToOne() throws Exception {
		assertTrue(AnnotationUtil.fieldHasRelationAnnotation(Department.class, "coordinator"));
	}

	@Test
	public void testgetTypeParameterReturnsNullWithoutGenericDeclaration() {
		assertNull(AnnotationUtil.getTypeParameterClassForCollectionField(Employee.class, "name"));
	}

	@Test
	public void hasClassLevelAnnotation() {
		assertTrue(AnnotationUtil.hasClassLevelAnnotation(Employee.class, Entity.class));
	}
}