package org.sif.core.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Defines a concrete implementation for an Abstraction. It can be used to avoid
 * ambiguous implementations where there are one or more implementations and a
 * default implementation to be used. The type is used to construct and define
 * different implementations of the same interface and must be defined for all
 * different implementations other than the default one.
 * 
 * @author eugenio
 * 
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Documented
public @interface Concrete {

	/**
	 * The correct delegate class for this concrete service implementation
	 */
	Class<?> delegate();
	
}
