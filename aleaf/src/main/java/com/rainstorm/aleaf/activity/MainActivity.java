package com.rainstorm.aleaf.activity;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.gyf.barlibrary.ImmersionBar;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.rainstorm.aleaf.adapter.MainAdapter;
import com.rainstorm.aleaf.bean.Diary;
import com.rainstorm.aleaf.R;
import com.rainstorm.aleaf.db.DatabaseManager;

/**
 * @description Main activity
 * @author liys
 */
public class MainActivity extends Activity {
	private ListView diaryLV;
	private List<Map<String, Object>> diaryData = new ArrayList<>();
	private List<Diary> diaries = null;
	private DatabaseManager manager = null;
	private SharedPreferences aleafConfig;
	private Editor editor;
	private int mPosition;
	private int lvChildTop;
	private int oldLVSize;
	private int newLVSize;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		forceShowOverflowMenu();
		getActionBar().setDisplayShowHomeEnabled(false);
        ImmersionBar.with(this).statusBarColor(R.color.aleaf).fitsSystemWindows(true).supportActionBar(true).init();

		initDiary();
		bundleEvents();
		getWritePermission();
	}

	private void forceShowOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initDiary() {
		aleafConfig = getSharedPreferences("aleafConfigFile",
				Context.MODE_PRIVATE);
		if (aleafConfig.getBoolean("firststart", true)) {
			DatabaseManager manager = new DatabaseManager(MainActivity.this);
			Diary diary = new Diary();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:mm");
			Date curDate = new Date(System.currentTimeMillis());
			String strDate = formatter.format(curDate);

			diary.setTitle(getString(R.string.default_diary_title));
			diary.setDate(strDate);
			diary.setText(getString(R.string.default_diary_content));
			manager.insert(diary);

			editor = aleafConfig.edit();
			editor.putBoolean("firststart", false);
			editor.commit();
		}

		diaryLV = findViewById(R.id.diary_lv);
		diaries = new ArrayList<Diary>();
		manager = new DatabaseManager(MainActivity.this);
		manager.query(diaries);

		diaryData = getData(diaries);
		MainAdapter adapter = new MainAdapter(this, diaryData);
		diaryLV.setAdapter(adapter);

		SharedPreferences mySharedPreferences = getSharedPreferences("SCROLL", Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit = mySharedPreferences.edit();
		edit.putInt("mPositionChildTop", 0);
		edit.putInt("mPosition", 0);
		edit.commit();
	}

	private void bundleEvents() {
		diaryLV.setOnItemClickListener(new ItemClickListener());
		diaryLV.setOnItemLongClickListener(new ItemLongClickListener());
		diaryLV.setOnScrollListener(lvScrollListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					method.setAccessible(true);
					method.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent intent = new Intent(this, DiaryEditActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_palette:
			Intent intentPalette = new Intent(this, PaletteActivity.class);
			startActivity(intentPalette);
			return true;

		case R.id.action_settings:
			Intent intentSettings = new Intent(this, SettingsActivity.class);
			startActivity(intentSettings);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override 
	protected void onPause() {
		super.onPause();
		SharedPreferences mySharedPreferences = getSharedPreferences("SCROLL", Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit = mySharedPreferences.edit();
		edit.putInt("mPositionChildTop", lvChildTop);
		edit.putInt("mPosition", mPosition);
		edit.commit();
	} 

	@Override
	protected void onResume() {
		super.onResume();
		
		oldLVSize = diaryData.size();
		manager.query(diaries);
		diaryData = getData(diaries);
		MainAdapter adapter = new MainAdapter(this, diaryData);
		diaryLV.setAdapter(adapter);
		
		newLVSize = diaryData.size();
		if(newLVSize == oldLVSize){
			SharedPreferences mySharedPreferences = getSharedPreferences("SCROLL", Activity.MODE_PRIVATE);
			lvChildTop = mySharedPreferences.getInt("mPositionChildTop", 0);
			mPosition = mySharedPreferences.getInt("mPosition", 0);
			diaryLV.setSelectionFromTop(mPosition, lvChildTop);
		}
	}

	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, DiaryEditActivity.class);
			intent.putExtra("title", diaries.get(position).getTitle());
			intent.putExtra("text", diaries.get(position).getText());
			intent.putExtra("imageFilePath", diaries.get(position)
					.getImageFilePath());
			intent.putExtra("_id", diaries.get(position).getId());

			startActivity(intent);
		}
	}

	class ItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v,
				final int position, long _id) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setItems(new String[] { getString(R.string.share_diary),
					getString(R.string.delete_diary) },
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (which == 0) {
								String sharedTitle = diaries.get(position).getTitle();
								String sharedText = diaries.get(position).getText();
								String sharedImagePath = diaries.get(position).getImageFilePath();

								shareMsg(MainActivity.this, getTitle().toString(), sharedTitle, sharedText, sharedImagePath);

								dialog.dismiss();
							} else if (which == 1) {
								dialog.dismiss();
								AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
								builder.setMessage(getString(R.string.delete_diary_reconfirm));
								builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialogInterface,
													int which) {
												manager.delete(diaries.get(
														position).getId());

												manager.query(diaries);
												diaryData = getData(diaries);
												MainAdapter adapter = new MainAdapter(MainActivity.this, diaryData);
												diaryLV.setAdapter(adapter);
												diaryLV.setSelectionFromTop(mPosition, lvChildTop);

												dialogInterface.cancel();
											}
										});
								builder.setNegativeButton(
										getString(R.string.cancel),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialogInterface, int which) {
												dialogInterface.dismiss();
											}
										});
								builder.create().show();
							}

						}

					});
			builder.create().show();
			return true;
		}
	}
	
	private OnScrollListener lvScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				mPosition = diaryLV.getFirstVisiblePosition();
				View v = diaryLV.getChildAt(0);
				lvChildTop = (v == null) ? 0 : v.getTop();
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		}
	};
    
	public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText, String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain");
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/png");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, activityTitle));
	}

	@SuppressLint("SimpleDateFormat")
	private List<Map<String, Object>> getData(List<Diary> diaries) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());
		String strCurDate = formatter.format(curDate);

		for (int i = 0; i < diaries.size(); i++) {
			String strDate = diaries.get(i).getDate();
			try {
				Date compareDate = formatter.parse(strDate);
				String strCompareDate = formatter.format(compareDate);
				if (strCompareDate.equals(strCurDate)) {
					String arrays[] = strDate.trim().split(" ");
					strDate = arrays[1];
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

			String strText = diaries.get(i).getText();
			String strImageFilePath = diaries.get(i).getImageFilePath();

			if (strImageFilePath != null) {
				if (strText == null) {
					strText = getString(R.string.click_to_enjoy);
				}
				strText = "<font color = '#FF7F2F'>" + strText + "</font>";
			}
			String strColorText = "<font color = '#43C932'>" + strDate + "</font>" + "  " + strText;

			map = new HashMap<String, Object>();
			map.put("title", diaries.get(i).getTitle());
			map.put("color_text", strColorText);
			map.put("imageFilePath", diaries.get(i).getImageFilePath());
			list.add(map);
		}
		return list;
	}
	
	private void getWritePermission() {
		Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).build(),
				new AcpListener() {
					@Override
					public void onGranted() {
					}

					@Override
					public void onDenied(List<String> permissions) {
					}
				});

	}
}
