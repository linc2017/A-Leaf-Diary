package com.rainstorm.aleaf.bean;

import java.io.Serializable;

/**
 * @description Image bean
 * @author liys
 */
public class Image implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title;
	private String imageId;

	public Image() {
		super();
	}

	public Image(String title, String imageId) {
		super();
		this.title = title;
		this.imageId = imageId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
}
