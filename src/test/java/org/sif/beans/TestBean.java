package org.sif.beans;

import javax.persistence.Basic;

public class TestBean {

	private String nonReadable;

	@Basic
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNonReadable() {
		return nonReadable;
	}

	public void setNonReadable(String nonReadable) {
		this.nonReadable = nonReadable;
	}
}
