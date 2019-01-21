package org.sif.beans;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Temporary hack to use the "form.map.property" EL style in views.
 */
public class BackMap {
	private Map map = new HashMap();

	public BackMap(Object model, Object keyparameter) {
		this(model);
		map.put("keyparameter", keyparameter);
	}

	public BackMap(Object model) {
		Field[] allModelFields = FieldUtils.getAllFields(model.getClass());
		for(Field modelField : allModelFields) {
			Field field = Classes.getField(model.getClass(), modelField.getName());
			try {
				map.put(field.getName(), PropertyUtils.getProperty(model, field.getName()));
			} catch (Exception ex) {}
		}
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map object) {
		this.map = map;
	}

}