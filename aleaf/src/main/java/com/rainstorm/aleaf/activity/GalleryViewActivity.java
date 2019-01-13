package com.rainstorm.aleaf.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.rainstorm.aleaf.widget.GalleryView;
import com.rainstorm.aleaf.adapter.ImageAdapter;
import com.rainstorm.aleaf.R;

/**
 * @description Gallery view
 * @author liys
 */
public class GalleryViewActivity extends Activity {
	public int iPosition = 0;
	private DisplayMetrics dm;
	private ImageAdapter ia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gallery_view);
        ImmersionBar.with(this).statusBarColor(R.color.aleaf).fitsSystemWindows(true).init();

		init();
	}
	
	private void init() {
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		GalleryView galleryView = (GalleryView) findViewById(R.id.gallery_view);
		Intent intent = getIntent();
		iPosition = intent.getIntExtra("position", 0);
		ia = new ImageAdapter(this);
		galleryView.setAdapter(ia);
		galleryView.setSelection(iPosition);

		Animation an = AnimationUtils.loadAnimation(this, R.anim.scale);
		galleryView.setAnimation(an);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gallery_view, menu);
		return true;
	}
}
