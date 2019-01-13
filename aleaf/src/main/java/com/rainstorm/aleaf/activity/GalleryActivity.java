package com.rainstorm.aleaf.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.gyf.barlibrary.ImmersionBar;
import com.rainstorm.aleaf.bean.Image;
import com.rainstorm.aleaf.bean.ImageSource;
import com.rainstorm.aleaf.R;

/**
 * @description Gallery
 * @author liys
 */
public class GalleryActivity extends Activity {
	private TextView tipView;
	private GridView gridView;
	private ImageAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
        setupActionBar();
        ImmersionBar.with(this).statusBarColor(R.color.aleaf).fitsSystemWindows(true).supportActionBar(true).init();
		
		init();
	}
	
	private void init() {
		tipView = (TextView)findViewById(R.id.tip);
		gridView = (GridView)findViewById(R.id.gallery_grid);

		ImageSource.imagePathList = new ArrayList<Image>();
		getImagePathFromSD();
		adapter = new ImageAdapter(ImageSource.imagePathList, this);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClick(this));
		gridView.setOnItemLongClickListener(new OnItemLongClick());

		if(!(ImageSource.imagePathList.size()>0)){
			tipView.setVisibility(View.VISIBLE);
		}else{
			tipView.setVisibility(View.GONE);
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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}
	
	public class OnItemClick implements OnItemClickListener {
		private Context mContext;
		public OnItemClick(Context c) {
			mContext = c;
		} 
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
			Intent intent = new Intent();
			intent.setClass(GalleryActivity.this, GalleryViewActivity.class);
			intent.putExtra("position", position);
			startActivity(intent);
		} 
	}
	
	public class OnItemLongClick implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				final int position, long arg3) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
			builder.setMessage(getString(R.string.delete_painting_reconfirm));
			builder.setPositiveButton(
					getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int which) {
							File file = new File(ImageSource.imagePathList.get(position).getImageId());
							file.delete();
							
							ImageSource.imagePathList.clear();
							getImagePathFromSD();
							adapter = new ImageAdapter(ImageSource.imagePathList, GalleryActivity.this);
							gridView.setAdapter(adapter);
							
							if(position > 0){
								gridView.smoothScrollToPosition(position - 1);
							}
							if(!(ImageSource.imagePathList.size()>0)){
								tipView.setVisibility(View.VISIBLE);
							}
							
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
			return true;
		}
	}
	
	private List<Image> getImagePathFromSD(){
		boolean sdCardExist = Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);
		if(!sdCardExist){
			Toast.makeText(GalleryActivity.this, getString(R.string.sd_read_warning), Toast.LENGTH_SHORT).show();
		}
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aleaf/";
		File mFile = new File(filePath);
		File[] files = mFile.listFiles();
		
		try{
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				Image img=new Image();
				img.setTitle(file.getName());
				img.setImageId(file.getPath());
				if (checkIsImageFile(file.getPath())) {
					ImageSource.imagePathList.add(img);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return ImageSource.imagePathList;
	}
	
	@SuppressLint("DefaultLocale")
	private static boolean checkIsImageFile(String fName) {
		boolean isImageFormat;
		String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
		if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
			isImageFormat = true;
		} else {
			isImageFormat = false;
		}

		return isImageFormat;
	}
	
	static class ViewHolder {
		public ImageView img;
		public TextView my_img_item_name;
	}

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<Image> list;
		public int CurrentItemID = 0;
		private Context mContext;

		public ImageAdapter(List<Image> imagePath, Context context) {
			super();
			list = imagePath;
			mInflater = LayoutInflater.from(context);
			mContext = context;
		}

		@Override
		public int getCount() {
			if (null != list) {
				return list.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ViewHolder holder = null;
			if (arg1 == null) {
				arg1 = mInflater.inflate(R.layout.imagelist_item, null);
				holder = new ViewHolder();
				holder.img = (ImageView) arg1.findViewById(R.id.image);
				holder.img.setAdjustViewBounds(true);
				holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
				
				holder.my_img_item_name = (TextView) arg1.findViewById(R.id.title);
				arg1.setTag(holder);
			} else {
				holder = (ViewHolder) arg1.getTag();
			}
			
			Animation an = AnimationUtils.loadAnimation(mContext,R.anim.zoom_enter );
			holder.img.setAnimation(an);

			holder.my_img_item_name.setText(list.get(arg0).getTitle());
			Bitmap bitmap = BitmapFactory.decodeFile(list.get(arg0).getImageId());
			holder.img.setImageBitmap(bitmap);
			return arg1;
		}
	}
}
