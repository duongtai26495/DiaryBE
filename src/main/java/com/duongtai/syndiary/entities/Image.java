package com.duongtai.syndiary.entities;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name ="Images")
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long id;
	
	private String name;
	
	private String added_at;
	
	private String added_by;

	
	public Image() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdded_at() {
		return added_at;
	}

	public void setAdded_at(String added_at) {
		this.added_at = added_at;
	}

	public String getAdded_by() {
		return added_by;
	}

	public void setAdded_by(String added_by) {
		this.added_by = added_by;
	}

}
