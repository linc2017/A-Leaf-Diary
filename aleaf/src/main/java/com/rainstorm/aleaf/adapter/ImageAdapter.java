package com.rainstorm.aleaf.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;

import com.rainstorm.aleaf.bean.ImageSource;
import com.rainstorm.aleaf.widget.GalleryView;

/**
 * @description Image adapter
 * @author liys
 */
public class ImageAdapter extends BaseAdapter {
	private int ownPosition;
	public int getOwnPosition() {
		return ownPosition;
	}
	public void setOwnPosition(int ownPosition) {
		this.ownPosition = ownPosition;
	}
	private Context context;

	public ImageAdapter(Context context) {
		this.context = context;
	}
	
	public int getCount() {
		return ImageSource.imagePathList.size();
	}

	public Object getItem(int position) {
		ownPosition = position;
		return position;
	}
	
	public long getItemId(int position) {
		ownPosition = position;
		return position;
	}

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		ownPosition = position;
		ImageView imageview = new ImageView(context);
		imageview.setBackgroundColor(0xFFFAF0E6);
		imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageview.setLayoutParams(new GalleryView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		Bitmap bitmap = BitmapFactory.decodeFile(ImageSource.imagePathList.get(
				position).getImageId());
		imageview.setImageBitmap(bitmap);
		
		return imageview;
	}
}
