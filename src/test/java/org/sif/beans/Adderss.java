package org.sif.beans;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Adderss implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Basic(optional = true)
	// The column for this attribute
	@Column(
			name="name",
			insertable=true,
			updatable=true,
			nullable=true,
			unique=false)
	private String name;



}