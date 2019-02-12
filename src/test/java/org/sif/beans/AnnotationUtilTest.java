package org.sif.beans;

import org.junit.Test;

import javax.persistence.ManyToOne;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static org.junit.Assert.*;

public class AnnotationUtilTest {

	@Test(expected = IllegalArgumentException.class)
	public void testFillAnnotatedFieldsWithInvalidParameter() {
		AnnotationUtil.fillAllAnnotatedFields(null, null);
	}

	@Test
	public void fillAllAnnotatedFields() {
		List<AnnotatedElement> annotatedFields = AnnotationUtil.fillAllAnnotatedFields(Employee.class, null);
		assertNotNull(annotatedFields);
	}

	@Test
	public void testGetFieldsWithAnnotation() {
		assertEquals(1, AnnotationUtil.getFieldsWithAnnotation(Employee.class, ManyToOne.class).size());
	}
}