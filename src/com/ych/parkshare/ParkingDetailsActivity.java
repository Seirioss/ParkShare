package com.ych.parkshare;

import java.util.List;

import com.ych.parkshare.R.menu;
import com.ych.serves.BLEservice;
import com.ych.tool.AppConstants;
import com.ych.tool.SpUtils;

import android.R.anim;
import android.R.integer;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.DocumentsContract.Root;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.Switch;

public class ParkingDetailsActivity extends Activity {

	private Intent intentaccept;
	private String pk = new String();
	protected Messenger serviceMessenger;
	private Switch switchpark;
	private Menu menumenu;
	private final static String MENU_EDIT = "编辑";
	private final static String MENU_SHARE = "分享";
	private final static String MENU_SHARE_CANLCER = "取消分享";
	private final static String MENU_BOOK_CANLCER = "取消预订";
	private final static String MENU_STORE = "收藏";
	private final static int ROLE_OWER = 0;
	private final static int ROLE_RENTER = 1;
	private int role;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parking_details);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("返回");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		String name1 = (String) SpUtils.get(getApplicationContext(), AppConstants.USER_NAME, "");
		if (name1.equals(getIntent().getStringExtra("name"))) {
			role = ROLE_OWER;
		} else {
			role = ROLE_RENTER;
		}

		Intent intent = new Intent(ParkingDetailsActivity.this, BLEservice.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		switchpark = (Switch) findViewById(R.id.unlock);
		switchpark.setOnCheckedChangeListener(onCheckedChangeListener);

	}

	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Message message = null;
			if (isChecked) {
				message = Message.obtain(null, BLEservice.MSG_OPENT_BLE, BLEservice.ADDRESS_);

			} else {
				message = Message.obtain(null, BLEservice.MSG_CLOSE_BLE, BLEservice.ADDRESS_);
			}
			try {
				if (serviceMessenger != null) {
					serviceMessenger.send(message);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceMessenger = new Messenger(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(MENU_EDIT);
		menu.add(MENU_STORE);
		if (role == ROLE_OWER) {
			menu.add(MENU_SHARE);
			menu.add(MENU_SHARE_CANLCER);
		}
		if (role == ROLE_RENTER) {
			menu.add(MENU_BOOK_CANLCER);
		}

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return super.onOptionsItemSelected(item);
		}
		String title = item.getTitle().toString();
		if (title.equals(MENU_EDIT)) {
			return super.onOptionsItemSelected(item);
		}
		if (title.equals(MENU_STORE)) {
			return super.onOptionsItemSelected(item);
		}
		if (title.equals(MENU_SHARE)) {
			return super.onOptionsItemSelected(item);
		}
		if (title.equals(MENU_SHARE_CANLCER)) {
			return super.onOptionsItemSelected(item);
		}
		if (title.equals(MENU_BOOK_CANLCER)) {
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		unbindService(conn);
	}

}
