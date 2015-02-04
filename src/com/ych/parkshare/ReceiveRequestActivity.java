package com.ych.parkshare;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class ReceiveRequestActivity extends Activity {

	private ActionBar actionbar;
	private EditText parkdescriptioneditText;
	private EditText starttimeeditText;
	private EditText endtimeEditText;
	private EditText estimatedfeeEditText;
	private EditText rentertelephoneEditText;
	private Button acceptrequestbutton;
	private Button refuserequestbutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive);

		actionbar = getActionBar();
		actionbar.setTitle("车位预约申请");
		actionbar.setDisplayHomeAsUpEnabled(true);

		parkdescriptioneditText = (EditText) findViewById(R.id.editparkdescription);
		starttimeeditText = (EditText) findViewById(R.id.editstarttime);
		endtimeEditText = (EditText) findViewById(R.id.editendtime);
		estimatedfeeEditText = (EditText) findViewById(R.id.editestimatedfee);
		rentertelephoneEditText = (EditText) findViewById(R.id.editrentertelephone);
		acceptrequestbutton = (Button) findViewById(R.id.acceptrequestbutton);
		refuserequestbutton = (Button) findViewById(R.id.acceptrequestbutton);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.receive, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
