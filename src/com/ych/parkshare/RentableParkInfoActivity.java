package com.ych.parkshare;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RentableParkInfoActivity extends Activity {
	
	private Button makeorderbutton ;
	private EditText editparkdescription;
	private EditText editvalidity;
	private EditText editparkaddress;
	private EditText editownertelephone;
	private EditText editofeescale;
	private EditText editremark;
	private EditText editstarttime;
	private EditText editendtime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rentable_park_info);
		
		ActionBar actionbar = getActionBar();
		actionbar.setTitle("可租用车位详情");
		actionbar.setIcon(new Drawable() {
			
			@Override
			public void setColorFilter(ColorFilter arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setAlpha(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public int getOpacity() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public void draw(Canvas arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		makeorderbutton=(Button)findViewById(R.id.makeorderbutton);
		editparkdescription=(EditText)findViewById(R.id.editparkdescription);
		editvalidity=(EditText)findViewById(R.id.editvalidity);
		editparkaddress=(EditText)findViewById(R.id.editparkaddress);
		editownertelephone=(EditText)findViewById(R.id.editownertelephone);
		editstarttime=(EditText)findViewById(R.id.editstarttime);
		editendtime=(EditText)findViewById(R.id.editendtime);
		editofeescale=(EditText)findViewById(R.id.editofeescale);
		editremark=(EditText)findViewById(R.id.editremark);
		makeorderbutton.setOnClickListener(OnClickListener);
		
	}
	
	private View.OnClickListener OnClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
	}; 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rentable_park_info, menu);
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
