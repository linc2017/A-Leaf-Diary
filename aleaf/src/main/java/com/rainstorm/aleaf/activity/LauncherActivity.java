package com.rainstorm.aleaf.activity;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;

import com.gyf.barlibrary.ImmersionBar;
import com.rainstorm.aleaf.R;

public class LauncherActivity extends Activity {
	ImageView image;
	private SharedPreferences aleafConfig;
	private boolean hasPassword = false;
	private String password = null;
	
	@Override                      
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
        ImmersionBar.with(this).statusBarColor(R.color.aleaf).fitsSystemWindows(true).init();

        init();
	}
	
	private void init() {
        aleafConfig = getSharedPreferences("aleafConfigFile",Context.MODE_PRIVATE);
        hasPassword = aleafConfig.getBoolean("hasPassword", false);
        password = aleafConfig.getString("password", "");
        image = (ImageView) findViewById(R.id.launcher_img);
        if (Locale.getDefault().getLanguage().equals("en")) {
            image.setImageResource(R.drawable.welcome_en);
        }
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(hasPassword && !password.equals("")){
                    Intent intent = new Intent(LauncherActivity.this,LoginActivity.class);
                    intent.putExtra("password", password);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.schedule(task, 1500);
    }
}
