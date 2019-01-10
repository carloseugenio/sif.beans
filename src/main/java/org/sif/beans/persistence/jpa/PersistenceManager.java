package org.sif.beans.persistence.jpa;

import java.util.List;

public interface PersistenceManager<T, I> {

	void setBeanClass(Class<T> clazz);

	List<?> findByField(String primaryKeyField, Object value);

	Object load(I convertedValue);
}
