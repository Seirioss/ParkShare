package com.ych.parkshare;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ych.http.AsyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
import com.ych.parkshare.R.menu;
import com.ych.serves.BLEservice;
import com.ych.tool.AppConstants;
import com.ych.tool.GlobalVariable;
import com.ych.tool.SpUtils;

import android.R.anim;
import android.R.integer;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

public class ParkRentActivity extends Activity {

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
	private final static String MENU_REFRESH = "刷新";
	private final static int ROLE_OWER = 0;
	private final static int ROLE_RENTER = 1;
	private int role;
	private TextView textdescription;
	private TextView textperiod;
	private TextView textaddress;
	private TextView textremark;
	private String parkpk;
	private Map<String, String> parkinfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parking_details);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("返回");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		String name1 = (String) SpUtils.get(getApplicationContext(), AppConstants.USER_NAME, "");
		parkpk = getIntent().getStringExtra("pk");
		if (name1.equals(getIntent().getStringExtra("name"))) {
			role = ROLE_OWER;
		} else {
			role = ROLE_RENTER;
		}
		textaddress = (TextView) findViewById(R.id.address);
		textdescription = (TextView) findViewById(R.id.description);
		textperiod = (TextView) findViewById(R.id.period);
		textremark = (TextView) findViewById(R.id.remarks);
		Intent intent = new Intent(ParkRentActivity.this, BLEservice.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		switchpark = (Switch) findViewById(R.id.parkdetail_switch_control);
		switchpark.setOnCheckedChangeListener(onCheckedChangeListener);
		updateuiinfo();

	}

	private void updateuiinfo() {
		AsyncHttpClient client = new AsyncHttpClient();
		PersistentCookieStore persistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		client.setCookieStore(persistentCookieStore);
		client.post("http://121.40.61.76:8080/parkManagementSystem/park/", new RequestParams("parkid", parkpk), new JsonHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				if (statusCode == 200) {
					try {
						JSONArray jsonArrayinfos = response.getJSONArray("parks");
						parkinfo = new HashMap<String, String>();
						JSONObject jsonObject1 = jsonArrayinfos.getJSONObject(0).getJSONObject("fields");
						JSONObject jsonObject2 = jsonArrayinfos.getJSONObject(1).getJSONObject("fields");
						JSONObject jsonObject3 = jsonArrayinfos.getJSONObject(2).getJSONObject("fields");
						parkinfo.put("username", jsonObject1.getString("username"));
						parkinfo.put("is_borrowed", jsonObject1.getString("is_borrowed"));
						parkinfo.put("comment", jsonObject1.getString("comment"));
						parkinfo.put("describe", jsonObject1.getString("describe"));
						parkinfo.put("address", jsonObject1.getString("address"));
						parkinfo.put("user_borrowed", jsonObject2.getString("user_borrowed"));
						parkinfo.put("price", jsonObject2.getString("price"));
						parkinfo.put("start_time", jsonObject2.getString("start_time"));
						parkinfo.put("end_time", jsonObject2.getString("end_time"));
						parkinfo.put("mac_address", jsonObject3.getString("mac_address"));
						parkinfo.put("close_key", jsonObject3.getString("close_key"));
						parkinfo.put("open_key", jsonObject3.getString("open_key"));
						parkinfo.put("serial_number", jsonObject3.getString("serial_number"));

						textdescription.setText(parkinfo.get("describe"));
						textperiod.setText(parkinfo.get("start_time") + "\n" + parkinfo.get("end_time"));
						textaddress.setText(parkinfo.get("address"));

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				System.out.println("net error:  statusCode" + statusCode);
			}

		});
	}

	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Message message = null;
			if (!isChecked) {
				message = Message.obtain(null, BLEservice.MSG_OPENT_BLE, BLEservice.ADDRESS);

			} else {
				message = Message.obtain(null, BLEservice.MSG_CLOSE_BLE, BLEservice.ADDRESS);
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
		menu.add(MENU_REFRESH);
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
			sharepark();
			return super.onOptionsItemSelected(item);
		}
		if (title.equals(MENU_SHARE_CANLCER)) {

			return super.onOptionsItemSelected(item);
		}
		if (title.equals(MENU_BOOK_CANLCER)) {
			return super.onOptionsItemSelected(item);
		}
		if (title.equals(MENU_REFRESH)) {
			updateuiinfo();
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		unbindService(conn);
	}

	private void sharepark() {
		LayoutInflater inflater = getLayoutInflater();
		final View layout = inflater.inflate(R.layout.dialog_sharetime, (ViewGroup) findViewById(R.id.sharetime));
		AlertDialog.Builder builder = new Builder(ParkRentActivity.this);
		builder.setTitle("分享时间");
		builder.setView(layout);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText editTextstart = (EditText) layout.findViewById(R.id.dialog_edit_starttime);
				EditText editTextend = (EditText) layout.findViewById(R.id.dialog_edit_endtime);
				String timeStart = editTextstart.getEditableText().toString();
				String timeend = editTextend.getEditableText().toString();
				String name = (String) SpUtils.get(getApplicationContext(), "name", "");
				AsyncHttpClient client = new AsyncHttpClient();
				PersistentCookieStore persistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
				client.setCookieStore(persistentCookieStore);
				RequestParams requestParams=new RequestParams();
				requestParams.put("parkid", parkpk);
				requestParams.put("starttime", timeStart);
				requestParams.put("endtime", timeend);
				requestParams.put("price", 100);
				System.out.println(timeStart);
				System.out.println(timeend);
				System.out.println(parkpk);
				client.post("http://121.40.61.76:8080/parkManagementSystem/user/share/",requestParams,new JsonHttpResponseHandler("utf-8") {

					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						System.out.println(response.toString());
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						super.onFailure(statusCode, headers, responseString, throwable);
					}

				});

			}
		});
		builder.create().show();

	}
}
