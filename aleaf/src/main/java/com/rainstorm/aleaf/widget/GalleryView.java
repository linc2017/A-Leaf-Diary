package com.rainstorm.aleaf.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;
import android.widget.Toast;

import com.rainstorm.aleaf.R;
import com.rainstorm.aleaf.adapter.ImageAdapter;

/**
 * @description Gallery view
 * @author liys
 */
public class GalleryView extends Gallery {
	boolean isFirst = false;
	boolean isLast = false;

	public GalleryView(Context context) {
		super(context);
	}

	public GalleryView(Context context, AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		ImageAdapter ia = (ImageAdapter) this.getAdapter();
		int p = ia.getOwnPosition();
		int count = ia.getCount();
		int kEvent;
		if (isScrollingLeft(e1, e2)) {
			if (p == 0 && isFirst) {
				Toast.makeText(this.getContext(), this.getContext().getString(R.string.first_page_tip), Toast.LENGTH_SHORT).show();
			} else if (p == 0) {
				isFirst = true;
			} else {
				isLast = false;
			}
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			if (p == count - 1 && isLast) {
				Toast.makeText(this.getContext(), this.getContext().getString(R.string.first_page_tip), Toast.LENGTH_SHORT).show();
			} else if (p == count - 1) {
				isLast = true;
			} else {
				isFirst = false;
			}
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);
		return true;
	}
}