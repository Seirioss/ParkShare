package com.ych.parkshare;

import com.ych.views.LinkClickTextView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LogInActivity extends Activity {

	private LinkClickTextView linkClickTextView;
	private Button buttonSignup;
	private Button buttonLogin;
	private EditText editTextname;
	private EditText editTextpassword;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_log_in);
		linkClickTextView = (LinkClickTextView) findViewById(R.id.forgetpassword);
		buttonLogin = (Button) findViewById(R.id.buttonlogin);
		buttonSignup = (Button) findViewById(R.id.buttonsignup);
		editTextname=(EditText)findViewById(R.id.editTextname);
		editTextpassword=(EditText)findViewById(R.id.editTextpassword);
		linkClickTextView.setOnClickListener(onClickListener);
		buttonSignup.setOnClickListener(onClickListener);
		buttonLogin.setOnClickListener(onClickListener);
	}

	private View.OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonlogin:
				if(login(editTextname.getText().toString(), editTextpassword.getText().toString())){
					Intent intent=new Intent(LogInActivity.this, MainActivity.class);
					startActivity(intent);
				}
				break;
			case R.id.buttonsignup:
				Intent intent=new Intent(LogInActivity.this, MainActivity.class);
				startActivity(intent);
				break;
			case R.id.forgetpassword:

				break;

			default:
				break;
			}
		}
	};
	private boolean login(String name,String password){
		boolean state=false;
		if(name.equals("123")&&password.equals("123")){
			state=true;
		}
		return state;
	}
}
