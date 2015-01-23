package com.ych.parkshare;

import java.util.HashMap;
import java.util.Iterator;
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
import android.R.string;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.DocumentsContract.Root;
import android.util.Range;
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
	private final static String MENU_REFRESH = "刷新";
	private final static String MENU_BOOK_CANLCER = "取消预订";
	private final static String MENU_STORE = "收藏";
	private final static int WHAT_REFRESH = 1;
	private TextView textdescription;
	private TextView texttimestart;
	private TextView texttimesend;
	private TextView textaddress;
	private TextView textremark;
	private String parkpk;
	private Map<String, Object> parkinfo;
	private AsyncHttpClient asyncHttpClient;
	private boolean bookstate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_park_rent);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("返回");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		parkpk = getIntent().getStringExtra("pk").toString();
		asyncHttpClient = new AsyncHttpClient();
		PersistentCookieStore PersistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		asyncHttpClient.setCookieStore(PersistentCookieStore);
		textaddress = (TextView) findViewById(R.id.address);
		textdescription = (TextView) findViewById(R.id.description);
		texttimestart = (TextView) findViewById(R.id.activity_park_rent_text_starttime);
		texttimesend = (TextView) findViewById(R.id.activity_park_rent_text_endtime);
		textremark = (TextView) findViewById(R.id.remarks);
		switchpark = (Switch) findViewById(R.id.parkdetail_switch_control);
		Intent intent = new Intent(ParkRentActivity.this, BLEservice.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		switchpark.setOnCheckedChangeListener(onCheckedChangeListener);
		asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_PARKINFO, new RequestParams("parkid", parkpk), refreshuiJsonHttpResponseHandler);
	}

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
		menu.add(MENU_STORE);
		menu.add(MENU_REFRESH);
		menu.add(MENU_BOOK_CANLCER);
		return true;

	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if(!bookstate){
			menu.add(MENU_STORE);
			menu.add(MENU_REFRESH);
			menu.add(MENU_BOOK_CANLCER);
		}
		
		return super.onPrepareOptionsMenu(menu);
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

		if (title.equals(MENU_STORE)) {

		}
		if (title.equals(MENU_BOOK_CANLCER)) {
			asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_BOOKCANCEL, new RequestParams("parkid", parkpk), bookcancelJsonHttpResponseHandler);
		}
		if (title.equals(MENU_REFRESH)) {
			asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_PARKINFO, new RequestParams("parkid", parkpk), refreshuiJsonHttpResponseHandler);
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		unbindService(conn);
	}

	private Map<String, Object> jsontomap(JSONObject jsonObject) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JSONArray jsonArrayinfos = jsonObject.getJSONArray("parks");
			JSONObject jsonObject1 = jsonArrayinfos.getJSONObject(0).getJSONObject("fields");
			JSONObject jsonObject2 = jsonArrayinfos.getJSONObject(1).getJSONObject("fields");
			JSONObject jsonObject3 = jsonArrayinfos.getJSONObject(2).getJSONObject("fields");
			map.put("username", jsonObject1.getString("username"));
			map.put("is_borrowed", jsonObject1.getBoolean("is_borrowed"));
			map.put("comment", jsonObject1.getString("comment"));
			map.put("describe", jsonObject1.getString("describe"));
			map.put("address", jsonObject1.getString("address"));
			map.put("user_borrowed", jsonObject2.getString("user_borrowed"));
			map.put("price", jsonObject2.getString("price"));
			map.put("start_time", jsonObject2.getString("start_time"));
			map.put("end_time", jsonObject2.getString("end_time"));
			map.put("mac_address", jsonObject3.getString("mac_address"));
			map.put("close_key", jsonObject3.getString("close_key"));
			map.put("open_key", jsonObject3.getString("open_key"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
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
				RequestParams requestParams = new RequestParams();
				requestParams.put("parkid", parkpk);
				requestParams.put("starttime", timeStart);
				requestParams.put("endtime", timeend);
				requestParams.put("price", 100);
				System.out.println(timeStart);
				System.out.println(timeend);
				System.out.println(parkpk);
				client.post("http://121.40.61.76:8080/parkManagementSystem/user/share/", requestParams, new JsonHttpResponseHandler("utf-8") {

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

	private JsonHttpResponseHandler refreshuiJsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			if (statusCode == 200) {
				try {
					int status = response.getInt("status");
					if (status == 0) {
						parkinfo=jsontomap(response);
						textdescription.setText(parkinfo.get("describe").toString());
						texttimestart.setText(parkinfo.get("start_time").toString());
						texttimesend.setText(parkinfo.get("end_time").toString());
						textaddress.setText(parkinfo.get("address").toString());
						textremark.setText("刷新成功："+statusCode);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			super.onFailure(statusCode, headers, responseString, throwable);
			textremark.setText("刷新失败："+statusCode);
		}
	};
	private JsonHttpResponseHandler storelJsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {
		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			super.onFailure(statusCode, headers, responseString, throwable);
		}
		
	};
	private JsonHttpResponseHandler bookcancelJsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {
		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			if(statusCode==200){
				try {
					int status= response.getInt("status");
					if(status==0){
						textremark.setText("退订成功\n"+(int)(Math.random()*100+1));
						textaddress.setText("");
						textdescription.setText("");
						texttimesend.setText("");
						texttimestart.setText("");
						switchpark.setEnabled(false);
						bookstate=true;
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			super.onFailure(statusCode, headers, responseString, throwable);
		}
		
	};
}
