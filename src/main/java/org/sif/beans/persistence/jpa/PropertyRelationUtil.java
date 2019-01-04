package org.sif.beans.persistence.jpa;

import org.sif.beans.AnnotationUtil;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


/**
 * Utility class for determine bean relation type for a property
 * 
 * @author Carlos Eugenio P. da Purificacao
 * 
 */
public class PropertyRelationUtil {

	/**
	 * Returns true if the provided property is not from a relation
	 * 
	 * @param bean
	 *            the bean to look the property
	 * @param property
	 *            the property to query in the bean
	 * @return true if the provided property is not from a relation in the bean
	 * @throws Exception
	 *             if an error occurs when trying to determine the relation
	 */
	public boolean isNotFromRelation(Class<?> bean, String property)
			throws Exception {
		return !AnnotationUtil.fieldHasRelationAnnotation(bean, property);
	}

	/**
	 * Returns true if the provided property is marked with a ManyToOne relation
	 * annotation
	 * 
	 * @param bean
	 *            the bean to look the property
	 * @param property
	 *            the property to query in the bean
	 * @return true if the provided property is from a ManyToOne relation in the
	 *         bean
	 * @throws Exception
	 *             if an error occurs when trying to determine the relation
	 */
	public boolean isManyToOneRelation(Class<?> bean, String property)
			throws Exception {
		return AnnotationUtil.fieldHasAnnotation(bean, property,
				ManyToOne.class);
	}

	/**
	 * Returns true if the provided property is marked with a ManyToMany
	 * relation annotation
	 * 
	 * @param bean
	 *            the bean to look the property
	 * @param property
	 *            the property to query in the bean
	 * @return true if the provided property is from a ManyToMany relation in
	 *         the bean
	 * @throws Exception
	 *             if an error occurs when trying to determine the relation
	 */
	public boolean isManyToManyRelation(Class<?> bean, String property)
			throws Exception {
		return AnnotationUtil.fieldHasAnnotation(bean, property,
				ManyToMany.class);
	}

	/**
	 * Returns true if the provided property is marked with a OneToMany
	 * relation annotation
	 *
	 * @param bean
	 *            the bean to look the property
	 * @param property
	 *            the property to query in the bean
	 * @return true if the provided property is from a OneToMany relation in
	 *         the bean
	 * @throws Exception
	 *             if an error occurs when trying to determine the relation
	 */
	public boolean isOneToManyRelation(Class<?> bean, String property)
			throws Exception {
		return AnnotationUtil.fieldHasAnnotation(bean, property,
				OneToMany.class);
	}

}
