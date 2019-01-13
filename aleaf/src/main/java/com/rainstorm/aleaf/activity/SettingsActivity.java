package com.rainstorm.aleaf.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.gyf.barlibrary.ImmersionBar;
import com.rainstorm.aleaf.R;

/**
 * @description Settings
 * @author liys
 */
public class SettingsActivity extends ListActivity {
	ListView listView = null;
	ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setupActionBar();
        ImmersionBar.with(this).statusBarColor(R.color.aleaf).fitsSystemWindows(true).supportActionBar(true).init();

        init();
	}
	
	private void init() {
        String[] listTitle = { getString(R.string.security), getString(R.string.help), getString(R.string.support), getString(R.string.version) };
        String[] listText = { getString(R.string.password_tip), getString(R.string.diary_desc), getString(R.string.qq), getString(R.string.version_text) };
        
		listView = getListView();
		int lengh = listTitle.length;
		for (int i = 0; i < lengh; i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("title", listTitle[i]);
			item.put("text", listText[i]);
			mData.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, mData,
				android.R.layout.simple_list_item_2, new String[] { "title",
				"text" }, new int[] { android.R.id.text1,
				android.R.id.text2 });
		setListAdapter(adapter);
		listView.setOnItemClickListener(new ItemClickListener());
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

	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			if(position ==0){
				Intent intent = new Intent(SettingsActivity.this,SetPasswordActivity.class);
				startActivity(intent);
			}
		}
	}
}
