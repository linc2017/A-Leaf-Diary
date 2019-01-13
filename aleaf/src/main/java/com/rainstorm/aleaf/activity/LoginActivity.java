package com.rainstorm.aleaf.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.rainstorm.aleaf.R;

/**
 * @description Login activity
 * @author liys
 */
public class LoginActivity extends Activity {
	private String password;
	private EditText editPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getActionBar().setDisplayShowHomeEnabled(false);
        ImmersionBar.with(this).statusBarColor(R.color.aleaf).fitsSystemWindows(true).supportActionBar(true).init();

		init();
	}
	
	private void init() {
		Intent intent = getIntent();
		password = intent.getStringExtra("password");
		editPassword = (EditText) findViewById(R.id.edit_password);
	}
	
	public void login(View view){
		if(editPassword.getText().toString().trim().equals(password)){
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}else{
			Toast.makeText(LoginActivity.this, getString(R.string.retry_password), Toast.LENGTH_SHORT).show();
		}
	}
}
