package com.ych.parkshare;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class TabHostActivity extends TabActivity {
	/** Called when the activity is first created. */
	private ActionBar actionBar;
	private final static String TAB_HOME = "home";
	private final static String TAB_STORE = "store";
	private final static String TAB_PERSONAL = "personal";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tab_host);
		actionBar = getActionBar();
		actionBar.setTitle("主页");
		actionBar.setDisplayShowHomeEnabled(false);
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, TabHomeActivity.class);
		spec = tabHost.newTabSpec(TAB_HOME).setIndicator("主页", res.getDrawable(R.drawable.ic_tab_home)).setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs

		intent = new Intent().setClass(this, TabNavigateActivity.class);
		spec = tabHost.newTabSpec(TAB_STORE).setIndicator("收藏", res.getDrawable(R.drawable.ic_tab_about)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, TabMyCenterActivity.class);
		spec = tabHost.newTabSpec(TAB_PERSONAL).setIndicator("个人", res.getDrawable(R.drawable.ic_tab_contact)).setContent(intent);
		tabHost.addTab(spec);
		// set tab which one you want open first time 0 or 1 or 2
		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(onTabChangeListener);
	}

	private OnTabChangeListener onTabChangeListener = new OnTabChangeListener() {

		@Override
		public void onTabChanged(String tabId) {
			if (tabId.equals(TAB_HOME)) {
				actionBar.setTitle("主页");
			}
			if (tabId.equals(TAB_STORE)) {
				actionBar.setTitle("收藏");
			}
			if (tabId.equals(TAB_PERSONAL)) {
				actionBar.setTitle("个人");
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			Intent intent = new Intent(TabHostActivity.this, SearchActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.menu_addpark:
			Intent intent2 = new Intent(TabHostActivity.this, AddParkLockActivity.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent2);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.item_actionbar_tabhostactivity, menu);
		return true;
	}

}