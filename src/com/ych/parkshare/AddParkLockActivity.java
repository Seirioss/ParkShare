package com.ych.parkshare;

import android.app.ActionBar;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddParkLockActivity extends Activity {

	private Button addlockbutton;
	private EditText locknumberEditText;
	private EditText lockcodeEditText;
	private EditText parkaddressEditText;
	private EditText remarkEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_park_lock);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("返回");
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		locknumberEditText=(EditText)findViewById(R.id.editlocknumber);
		lockcodeEditText=(EditText)findViewById(R.id.editlockcode);
		parkaddressEditText=(EditText)findViewById(R.id.editparkaddress);
		remarkEditText=(EditText)findViewById(R.id.editremark);
		addlockbutton=(Button)findViewById(R.id.addlockbutton);
		addlockbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_park_lock, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		finish();
		return super.onOptionsItemSelected(item);
	}
}
