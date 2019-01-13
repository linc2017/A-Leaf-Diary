package com.rainstorm.aleaf.db;


import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rainstorm.aleaf.bean.Diary;

/**
 * @description Database manager
 * @author liys
 */
public class DatabaseManager {
	private DatabaseHelper helper;
	
	public DatabaseManager(Context context) {
		helper = new DatabaseHelper(context);
	}
	
	public void insert(Diary diary){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("title", diary.getTitle());
		values.put("date", diary.getDate());
		values.put("text", diary.getText());
		values.put("imageFilePath", diary.getImageFilePath());
		db.insert("DIARY_DATA", null, values);
		db.close();
	}
	
	public void query(List<Diary> diaries) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("DIARY_DATA", null, null,
				null, null, null, "_id desc");
		diaries.clear();
		while(cursor.moveToNext()){
			Diary diary = new Diary();
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String date = cursor.getString(cursor.getColumnIndex("date"));
			String text = cursor.getString(cursor.getColumnIndex("text"));
			String imageFilePath = cursor.getString(cursor.getColumnIndex("imageFilePath"));
			int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			diary.setTitle(title);
			diary.setDate(date);
			diary.setText(text);
			diary.setImageFilePath(imageFilePath);
			diary.setId(_id);
			diaries.add(diary);
		}
		cursor.close();
		db.close();
	}
	
	public void delete(int _id){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from DIARY_DATA where _id = " + _id);
		db.close();
	}
}
