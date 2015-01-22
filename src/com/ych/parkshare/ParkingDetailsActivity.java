package com.ych.parkshare;

import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ParkingDetailsActivity extends Activity {

	private Intent intentaccept;
	private String pk = new String();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parking_details);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("返回");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		intentaccept = getIntent();
		pk = intentaccept.getStringExtra("pk");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.parking_details, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.edit:

			break;
		case R.id.share:

			break;
		case R.id.cancelshare:

			break;
		case R.id.cancelbooking:

			break;
		case R.id.collect:

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);

	}
}
