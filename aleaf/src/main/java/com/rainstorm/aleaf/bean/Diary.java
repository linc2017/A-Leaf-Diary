package com.rainstorm.aleaf.bean;

/**
 * @description Diary bean
 * @author liys
 */
public class Diary {
	private String title;
	private String date;
	private String text;
	private String imageFilePath;
	private int _id;
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getDate(){
		return date;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public String getText(){
		return text;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public int getId(){
		return _id;
	}
	
	public void setId(int _id){
		this._id = _id;
	}
	
	public String getImageFilePath(){
		return imageFilePath;
	}
	
	public void setImageFilePath(String imageFilePath){
		this.imageFilePath = imageFilePath;
	}
}
