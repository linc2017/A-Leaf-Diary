package com.rainstorm.aleaf.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;

import com.gyf.barlibrary.ImmersionBar;
import com.rainstorm.aleaf.db.DatabaseManager;
import com.rainstorm.aleaf.bean.Diary;
import com.rainstorm.aleaf.R;

/**
 * @description Edit a diary
 * @author liys
 */
public class DiaryEditActivity extends Activity {
	private LinearLayout linear;
	private LinearLayout linearText;
	private LinearLayout linearImage;
	private EditText diaryTitle = null;
	private EditText diaryText = null;
	private EditText diaryImageTitle;
	private EditText diaryImageText;
	private ImageView diaryImage;
	private String imageFilePath;
	private boolean mStateModify = false;
	private String oldTitle = null;
	private String oldText = null;
	private int oldId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_edit);
		setupActionBar();
        ImmersionBar.with(this).statusBarColor(R.color.aleaf).fitsSystemWindows(true).supportActionBar(true).init();

		init();
		bundleEvents();
	}
	
	private void init() {
		linear = (LinearLayout) findViewById(R.id.linear);
		linearText = (LinearLayout) findViewById(R.id.text_diary_layout);
		linearImage = (LinearLayout) findViewById(R.id.image_diary_layout);
		diaryTitle = (EditText) this.findViewById(R.id.edit_title);
		diaryText = (EditText) this.findViewById(R.id.edit_text);
		diaryImageTitle = (EditText) this.findViewById(R.id.edit_image_title);
		diaryImageText = (EditText) this.findViewById(R.id.edit_image_text);
		diaryImage = (ImageView) this.findViewById(R.id.image);
	}
	
	private void bundleEvents() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			mStateModify = true;
			linear.setFocusable(true);
			linear.setFocusableInTouchMode(true);

			imageFilePath = bundle.getString("imageFilePath");
			if (imageFilePath == null) {
				diaryTitle.setText(bundle.getString("title"));
				diaryText.setText(bundle.getString("text"));

				oldTitle = diaryTitle.getText().toString();
				oldText = diaryText.getText().toString();
				oldId = bundle.getInt("_id");
			} else {
				linearText.setVisibility(View.GONE);
				linearImage.setVisibility(View.VISIBLE);
				diaryImageTitle.setText(bundle.getString("title"));
				diaryImageText.setText(bundle.getString("text"));

				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = false;
				try {
					Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, opts);
					if(bmp == null){
						diaryImage.setImageResource(R.drawable.diary_miss_image);
					} else {
						diaryImage.setImageBitmap(bmp);
					}
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}

				oldTitle = diaryImageTitle.getText().toString();
				oldText = diaryImageText.getText().toString();
				oldId = bundle.getInt("_id");
			}

		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			backAndSave();
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			backAndSave();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("SimpleDateFormat")
	private void backAndSave() {
		if (imageFilePath == null) {
			if ((!diaryTitle.getText().toString().trim().equals("")) || (!diaryText.getText().toString().trim().equals(""))) {

				if (mStateModify == true && oldTitle.equals(diaryTitle.getText().toString()) && oldText.equals(diaryText.getText().toString())) {
					DiaryEditActivity.this.finish();
				} else if (mStateModify == true && (!oldTitle.equals(diaryTitle.getText().toString()) || !oldText.equals(diaryText.getText().toString()))) {
					DatabaseManager manager = new DatabaseManager(DiaryEditActivity.this);manager.delete(oldId);
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:mm");
					Date curDate = new Date(System.currentTimeMillis());
					String strDate = formatter.format(curDate);

					Diary diary = new Diary();
					diary.setTitle(diaryTitle.getText().toString());
					diary.setDate(strDate);
					diary.setText(diaryText.getText().toString());
					manager.insert(diary);

					boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
					if (sdCardExist) {
						String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aleaf/.diary_backup/";
						File file = new File(filePath);
						if (!file.isDirectory()) {
							file.mkdirs();
						}

						SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
						String fileName = diaryTitle.getText().toString() + "_" + sf.format(curDate);
						String filePathName = filePath + fileName + ".txt";
						String info = diaryTitle.getText().toString() + "\n" + strDate + "\n" + diaryText.getText().toString();

						try {
							FileOutputStream outStream = new FileOutputStream(filePathName, true);
							OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
							writer.write(info);
							writer.write("\n");
							writer.flush();
							writer.close();
							outStream.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					Intent intent = new Intent();
					intent.setClass(DiaryEditActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					DatabaseManager manager = new DatabaseManager(DiaryEditActivity.this);
					Diary diary = new Diary();

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:mm");
					Date curDate = new Date(System.currentTimeMillis());
					String strDate = formatter.format(curDate);

					diary.setTitle(diaryTitle.getText().toString());
					diary.setDate(strDate);
					diary.setText(diaryText.getText().toString());
					manager.insert(diary);

					boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
					if (sdCardExist) {
						String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aleaf/.diary_backup/";
						File file = new File(filePath);
						if (!file.isDirectory()) {
							file.mkdirs();
						}

						SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
						String fileName = diaryTitle.getText().toString() + "_" + sf.format(curDate);
						String filePathName = filePath + fileName + ".txt";
						String info = diaryTitle.getText().toString() + "\n" + strDate + "\n" + diaryText.getText().toString();

						try {
							FileOutputStream outStream = new FileOutputStream(filePathName, true);
							OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
							writer.write(info);
							writer.write("\n");
							writer.flush();
							writer.close();
							outStream.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					Intent intent = new Intent();
					intent.setClass(DiaryEditActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
			} else {
				DiaryEditActivity.this.finish();
			}
		} else {
			if (oldTitle.equals(diaryImageTitle.getText().toString()) && oldText.equals(diaryImageText.getText().toString())) {
				DiaryEditActivity.this.finish();
			} else {
				new UpdateImageDiaryData().execute();
			}
		}
	}
	
	class UpdateImageDiaryData extends AsyncTask <String,Integer,Boolean> {

		@SuppressLint("SimpleDateFormat")
		@Override
		protected Boolean doInBackground(String... params) {
			
			DatabaseManager manager = new DatabaseManager(DiaryEditActivity.this);
			manager.delete(oldId);

			Diary diary = new Diary();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:mm");
			Date curDate = new Date(System.currentTimeMillis());
			String strDate = formatter.format(curDate);

			diary.setTitle(diaryImageTitle.getText().toString());
			diary.setDate(strDate);
			if(!diaryImageText.getText().toString().equals("")){
				diary.setText(diaryImageText.getText().toString());
			}
			diary.setImageFilePath(imageFilePath);
			manager.insert(diary);
			
			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			if (sdCardExist) {
				String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aleaf/.diary_backup/";
				File file = new File(filePath);
				if (!file.isDirectory()) {
					file.mkdirs();
				}

				SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
				String fileName = diaryImageTitle.getText().toString() + "_" + sf.format(curDate);
				String filePathName = filePath + fileName + ".txt";
				String info = diaryImageTitle.getText().toString() + "\n" + strDate + "\n" + diaryImageText.getText().toString();

				try {
					FileOutputStream outStream = new FileOutputStream(filePathName, true);
					OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
					writer.write(info);
					writer.write("\n");
					writer.flush();
					writer.close();
					outStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return true;
		}
		
		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(Boolean result) {
			if (true == result) {
				Intent intent = new Intent();
				intent.setClass(DiaryEditActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			} 
		}
	}
}
