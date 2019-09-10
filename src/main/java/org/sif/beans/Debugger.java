package org.sif.beans;

public class Debugger {


	public static  String debug(Object value) {
		if (value == null) {
			return "(value is NULL!)";
		}
		String strValue = value.toString();
		if (strValue.length() == 0) {
			return "(value is EMPTY!)";
		}
		return "(" + strValue + ")";
	}
}
