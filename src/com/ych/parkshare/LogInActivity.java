package com.ych.parkshare;

import com.ych.views.LinkClickTextView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class LogInActivity extends Activity {

	private LinkClickTextView linkClickTextView;
	private Button buttonSignup;
	private Button buttonLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_log_in);
		linkClickTextView = (LinkClickTextView) findViewById(R.id.forgetpassword);
		buttonLogin = (Button) findViewById(R.id.buttonlogin);
		buttonSignup = (Button) findViewById(R.id.buttonsignup);
		linkClickTextView.setOnClickListener(onClickListener);
		buttonSignup.setOnClickListener(onClickListener);
		buttonLogin.setOnClickListener(onClickListener);
	}

	private View.OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonlogin:

				break;
			case R.id.buttonsignup:

				break;
			case R.id.forgetpassword:

				break;

			default:
				break;
			}
		}
	};
}
