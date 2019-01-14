package com.rainstorm.aleaf.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.gyf.barlibrary.ImmersionBar;
import com.rainstorm.aleaf.bean.Diary;
import com.rainstorm.aleaf.R;
import com.rainstorm.aleaf.db.DatabaseManager;
import com.rainstorm.aleaf.widget.ColorPickerDialog;
import com.rainstorm.aleaf.widget.ColorPickerDialog.OnColorChangedListener;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.support.v4.app.NavUtils;

/**
 * @description Palette
 * @author liys
 */
public class PaletteActivity extends Activity implements OnColorChangedListener{

	private SharedPreferences aLeafConfig;
	private CanvasView canvasView;
	private PopupWindow toolsPW;
	private Paint paint;
	private Path path;
	private List<DrawPath> paths;
	private List<DrawPath> redoPaths;
	private Canvas canvas;
	private DisplayMetrics screen = new DisplayMetrics();
	private int contentTop = 0;
	private boolean isToolsShow = true;
	private String filePath;
	private int penSize;
	private int penColor;
	private boolean isEraser = false;
	private String imageFilePath = null;
	private int oldId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((BitmapDrawable) (getResources().getDrawable(R.drawable.background))).getBitmap();
		bundleEvents();
		canvasView = new CanvasView(this);
		canvasView.setBackgroundColor(Color.WHITE);
		setContentView(canvasView);
		setupActionBar();
        ImmersionBar.with(this).statusBarColor(R.color.aleaf).fitsSystemWindows(true).supportActionBar(true).init();
        
        initData();
		initPaint();
	}
	
	private void bundleEvents() {
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			imageFilePath = bundle.getString("imageFilePath", null);
			oldId = bundle.getInt("_id", -1);
		}
	}

    private void initData() {
        aLeafConfig = getSharedPreferences("aleafConfigFile",Context.MODE_PRIVATE);
        if(!aLeafConfig.contains("penColor")){
            penColor = 0xFFFF7F2F;
            aLeafConfig.edit().putInt("penColor", penColor).commit();
        }else{
            penColor = aLeafConfig.getInt("penColor", 0);
        }
        if(!aLeafConfig.contains("penSize")){
            penSize = 6;
            aLeafConfig.edit().putInt("penSize", penSize).commit();
        }else{
            penSize = aLeafConfig.getInt("penSize", 0);
        }
        
        paths = new ArrayList<DrawPath>();
        redoPaths = new ArrayList<DrawPath>();
    }

    public void initPaint()
    {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(penColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(penSize);
    }
	
	Handler handler = new Handler();
	final Runnable runnable = new Runnable(){
		@Override
		public void run() {
			showPopupWindowTools();
		}
	};
	
	@Override
	protected void onResume()
	{
		super.onResume();
		handler.postDelayed(runnable, 100);
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.palette, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if(paths.size() > 0){
				canvasView.save();
			}
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_gallery:
			Intent intent = new Intent(PaletteActivity.this,GalleryActivity.class);
			startActivity(intent);
			toolsPW.dismiss();
			toolsPW = null;
			return true;
		case R.id.action_tools:
			if (isToolsShow) {
				toolsPW.dismiss();
			}
			else {
				handler.postDelayed(runnable, 100);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(paths.size() > 0){
				canvasView.save();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public int[] drawBoardScreen() {
		screen = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(screen);
		int src[] = new int[2];
		src[0] = screen.widthPixels;
		src[1] = screen.heightPixels;
		return src;
	}
	
	public void showPopupWindowTools() {
		isToolsShow = true;
        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        contentTop = statusBarHeight + getActionBar().getHeight();
        final Context mContext = PaletteActivity.this;
		LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View liView = mLayoutInflater.inflate(R.layout.popupwindow, null);
		toolsPW = new PopupWindow(liView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		toolsPW.showAtLocation(canvasView, Gravity.TOP | Gravity.LEFT, 0, contentTop + 5);
		toolsPW.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss()
			{
				isToolsShow = false;
				toolsPW.dismiss();
			}
		});
		final ImageButton eraserBT = (ImageButton) liView.findViewById(R.id.eraser);
		eraserBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isEraser = true;
				canvasView.initEraserPaint();
			}
		});
		ImageButton penBT = (ImageButton) liView.findViewById(R.id.pen);
		penBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isEraser = false;
				initPaint();
			}
		});
		ImageButton nextIB = (ImageButton) liView.findViewById(R.id.pens);
		nextIB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSeekBarPW();
				isEraser = false;
				initPaint();
			}
		});
		ImageButton colorIB = (ImageButton) liView.findViewById(R.id.colors);
		colorIB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ColorPickerDialog(mContext, PaletteActivity.this, paint.getColor()).show();
				isEraser = false;
				initPaint();
			}
		});
		ImageButton undoIB = (ImageButton) liView.findViewById(R.id.undo);
		undoIB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				canvasView.undo();
			}
		});
		ImageButton redoIB = (ImageButton) liView.findViewById(R.id.redo);
		redoIB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				canvasView.redo();
			}
		});
		ImageButton clearIB = (ImageButton) liView.findViewById(R.id.clear);
		clearIB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isEraser = false;
				initPaint();
				
				AlertDialog.Builder builder = new AlertDialog.Builder(
						PaletteActivity.this);
				builder.setMessage(getString(R.string.clear_palette_reconfirm));
				builder.setPositiveButton(
						getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface,
									int which) {
								canvasView.clear();
								
								dialogInterface.cancel();
							}
						});
				builder.setNegativeButton(
						getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface,
									int which) {
								// TODO Auto-generated method
								// stub
								dialogInterface.dismiss();
							}
						});
				builder.create().show();
			}
		});
	}
    
	public void showSeekBarPW() {
		LayoutInflater mLayoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View liView = mLayoutInflater.inflate(R.layout.seek_bar, null);
		final TextView seekBarTV = (TextView) liView.findViewById(R.id.seek_bar_tv);
		seekBarTV.setText(penSize + "");
		SeekBar sb = (SeekBar) liView.findViewById(R.id.seek_bar);
		sb.setProgress(penSize);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				seekBarTV.setText(progress + "");
			}
		});
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(getString(R.string.set_pen_size));
		dialog.setView(liView);
		dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String size = seekBarTV.getText().toString();
				if (size != null && !"".equals(size)) {
					penSize = Integer.parseInt(size);
					paint.setStrokeWidth(penSize);
					aLeafConfig.edit().putInt("penSize", penSize).commit();
				}
			}
		});
		dialog.setNegativeButton(getString(R.string.cancel), null);
		dialog.create().show();
	}
    
	@Override
	public void colorChanged(int color) {
		paint.setColor(color);
		penColor = color;
		aLeafConfig.edit().putInt("penColor", penColor).commit();
	}
	
	public class DrawPath {
		private Path path;
		private Paint paint;
		private int color;
		private MaskFilter mf;
		private float strokeWidth;
	}
	
	public class CanvasView extends View {
		private Bitmap bitmapDraw;
		private float mX, mY;
		private int drawBoard[] = drawBoardScreen();
		private int drawBoardWidth = drawBoard[0];
		private int drawBoardHeight = drawBoard[1];
		private DrawPath dp;
		private Canvas tempCanvas;

		public CanvasView(Context context) {
			super(context);
			initCanvas();
		}
        
		public void initCanvas() {
			bitmapDraw = Bitmap.createBitmap(drawBoardWidth, drawBoardHeight, Bitmap.Config.ARGB_8888);
			bitmapDraw.eraseColor(Color.TRANSPARENT);
			canvas = new Canvas();
			canvas.setBitmap(bitmapDraw);
			
			if (imageFilePath != null) {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = false;
				try {
					Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, opts);
					if(bmp != null){
						canvas.drawBitmap(bmp, 0 , 0 , paint);
					}
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
			
			tempCanvas = new Canvas(bitmapDraw);
			tempCanvas.drawColor(Color.TRANSPARENT);
		}
        
		public void initEraserPaint() {
			initPaint();
			paint.setStrokeWidth(17);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
			paint.setColor(Color.WHITE);
		}
        
		public void clear() {
			paths.clear();
			redoPaths.clear();
			initCanvas();
			invalidate();
		}
        
		public void undo() {
			if (!paths.isEmpty() && paths.size() > 0) {
				int location = paths.size() - 1;
				redoPaths.add(paths.get(location));
				paths.remove(location);
				initCanvas();
				Iterator<DrawPath> iter = paths.iterator();
				while (iter.hasNext()) {
					DrawPath dp = iter.next();
					dp.paint.setColor(dp.color);
					dp.paint.setMaskFilter(dp.mf);
					dp.paint.setStrokeWidth(dp.strokeWidth);
					canvas.drawPath(dp.path, dp.paint);
				}
				invalidate();
			}
		}
        
		public void redo() {
			if (!redoPaths.isEmpty() && redoPaths.size() > 0) {
				initCanvas();
				if (!paths.isEmpty() && paths.size() > 0) {
					Iterator<DrawPath> iter = paths.iterator();
					while (iter.hasNext()) {
						DrawPath dp = iter.next();
						dp.paint.setColor(dp.color);
						dp.paint.setMaskFilter(dp.mf);
						dp.paint.setStrokeWidth(dp.strokeWidth);
						canvas.drawPath(dp.path, dp.paint);
					}
				}
				DrawPath dp = redoPaths.get(redoPaths.size()-1);
				dp.paint.setColor(dp.color);
				dp.paint.setMaskFilter(dp.mf);
				dp.paint.setStrokeWidth(dp.strokeWidth);
				canvas.drawPath(dp.path, dp.paint);
				paths.add(dp);
				redoPaths.remove(redoPaths.size()-1);
				invalidate();
			}
		}
        
		public void clearMemory(Bitmap bitmap) {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
		}

		@Override
		public void onDraw(Canvas ca) {
			if (path != null) {
				if(isEraser){
					tempCanvas.drawPath(path, paint);
				} else{
					ca.drawPath(path, paint);
				}
			}
			ca.drawBitmap(bitmapDraw, 0, 0, null);
		}
        
		public void touchStart(float x, float y) {
			path.moveTo(x, y);
			mX = x;
			mY = y;
		}
        
		public void touchMove(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx != 0 && dy != 0) {
				path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			}
			mX = x;
			mY = y;
		}
        
		public void touchUP() {
			path.lineTo(mX, mY);
			canvas.drawPath(path, paint);
			paths.add(dp);
			path = null;
		}
        
		@SuppressLint("SimpleDateFormat")
		public void save() {
			try {
				boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
				if(!sdCardExist){
					Toast.makeText(PaletteActivity.this, getString(R.string.sd_write_warning), Toast.LENGTH_SHORT).show();
				}
				String fullFilePath = "";
				Date curDate = new Date(System.currentTimeMillis());
				if (imageFilePath != null) {
					fullFilePath = imageFilePath;
					setResult(RESULT_OK);
				}  else {
					filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aleaf/";

					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
					String fileName = formatter.format(curDate);
					
					File file = new File(filePath);
					if (!file.isDirectory()) {
						file.mkdirs();
					}
					fullFilePath = filePath + fileName + ".png";
				}
				
				Bitmap temp = getBmp();
				FileOutputStream fos = new FileOutputStream(fullFilePath);
				temp.compress(CompressFormat.JPEG, 100, fos);
				fos.close();
				clearMemory(temp);
				
				DatabaseManager manager = new DatabaseManager(PaletteActivity.this);
				if (oldId != -1) {
					manager.delete(oldId);
				}
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd H:mm");
				String strDate = sf.format(curDate);
				Diary diary = new Diary();
				diary.setTitle(getString(R.string.diary_title));
				diary.setDate(strDate);
				diary.setImageFilePath(fullFilePath);
				manager.insert(diary);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		public Bitmap getBmp() {
			Bitmap mCombinedBmp = Bitmap.createBitmap(bitmapDraw.getWidth(), bitmapDraw.getHeight() - contentTop, Bitmap.Config.ARGB_8888);
			mCombinedBmp.eraseColor(Color.TRANSPARENT);
			Canvas canvas = new Canvas(mCombinedBmp);
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bitmapDraw, 0, 0, null);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
			return mCombinedBmp;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					dp = new DrawPath();
					path = new Path();
					dp.paint = paint;
					dp.path = path;
					dp.color = paint.getColor();
					dp.mf = paint.getMaskFilter();
					dp.strokeWidth = paint.getStrokeWidth();
					touchStart(x, y);
					invalidate();
					break;
				case MotionEvent.ACTION_MOVE:
					touchMove(x, y);
					invalidate();
					break;
				case MotionEvent.ACTION_UP:
					touchUP();
					invalidate();
					break;
			}
			return true;
		}
	}
}
