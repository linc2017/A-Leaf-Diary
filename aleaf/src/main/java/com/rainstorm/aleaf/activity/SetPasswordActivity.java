package com.rainstorm.aleaf.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.rainstorm.aleaf.R;

/**
 * @description Set password
 * @author liys
 */
public class SetPasswordActivity extends Activity {
	private EditText setPassword;
	private EditText confirmPassword;
	private boolean hasPassword = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_password);
        setupActionBar();
        ImmersionBar.with(this).statusBarColor(R.color.aleaf).fitsSystemWindows(true).supportActionBar(true).init();

		init();
	}
	
	private void init() {
		setPassword = (EditText)findViewById(R.id.set_password);
		confirmPassword = (EditText)findViewById(R.id.confirm_password);
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
	
	public void setPassword(View view){
		if (confirmPassword.getText().toString().trim().equals(setPassword.getText().toString().trim())) {
			SharedPreferences preferences = getSharedPreferences("aleafConfigFile",Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putBoolean("hasPassword",!hasPassword);
			editor.putString("password",setPassword.getText().toString().trim());
			editor.commit();
			Toast.makeText(SetPasswordActivity.this, getString(R.string.set_password_success), Toast.LENGTH_LONG).show();
			finish();
		} else {
			Toast.makeText(SetPasswordActivity.this, getString(R.string.set_password_fail), Toast.LENGTH_LONG).show();
		}
	}
	
	public void cansel(View view){
		finish();
	}
}
