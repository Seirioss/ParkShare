package com.ych.parkshare;

import com.ych.tool.NetworkConnections;
import com.ych.tool.SpUtils;

import android.R.integer;
import android.R.menu;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

public class StartupActivity extends Activity {

	private final static int TIMER = 1;
	private boolean rememberPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_startup);
		boolean state = NetworkConnections.isNetworkAvailable(getApplicationContext());
		if (!state) {
			Toast.makeText(StartupActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
		} else {
			new Thread(timerRunnable).start();
			rememberPassword=(Boolean) SpUtils.get(getApplicationContext(), "rememberPassword", false);
		}
	}

	private Runnable timerRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message message = handler.obtainMessage();
			message.what = TIMER;
			message.sendToTarget();
		}
	};
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TIMER) {
				Intent intent=null;
				if(rememberPassword){
					intent=new Intent(StartupActivity.this,MainActivity.class);
				}else {
					intent=new Intent(StartupActivity.this,LogInActivity.class);
				}
				startActivity(intent);
				StartupActivity.this.finish();
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler = null;
	}

}
