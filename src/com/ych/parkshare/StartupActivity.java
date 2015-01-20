package com.ych.parkshare;

import com.ych.tool.AssetsProperties;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_startup);
		boolean state = NetworkConnections.isNetworkAvailable(getApplicationContext());
		if (!state) {
			Toast.makeText(StartupActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
		} 
		new Thread(timerRunnable).start();
	}

	private Runnable timerRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				int time = AssetsProperties.load(getApplicationContext(), "propertie").getInt("startuptime", 0);
				Thread.sleep(1000);
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
				Intent intent = new Intent(StartupActivity.this, TabHostActivity.class);
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
