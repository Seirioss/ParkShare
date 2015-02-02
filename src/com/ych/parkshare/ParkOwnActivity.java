package com.ych.parkshare;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ych.http.AsyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
import com.ych.http.TextHttpResponseHandler;
import com.ych.serves.BLEservice;
import com.ych.tool.AppConstants;
import com.ych.tool.GlobalVariable;
import com.ych.tool.SpUtils;

import android.R.anim;
import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class ParkOwnActivity extends Activity {

	private Intent intentaccept;
	private String pk = new String();
	protected Messenger serviceMessenger;
	private Switch switchpark;
	private final static String MENU_REFRESH = "刷新";
	private final static String MENU_SHARE = "普通分享";
	private final static String MENU_SHAREVIP = "分享给指定用户";
	private final static String MENU_SHARE_CANLCER = "取消分享";
	private AsyncHttpClient asyncHttpClient;
	private TextView textdescription;
	private TextView texttimestart;
	private TextView texttimesend;
	private TextView textaddress;
	private TextView textremark;
	private TextView textrentstate;
	private String parkpk;
	private boolean is_shared;
	private boolean is_borrowed = true;

	private String macaddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_park_own);

		ActionBar actionBar = getActionBar();
		actionBar.setTitle("返回");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		asyncHttpClient = new AsyncHttpClient();
		PersistentCookieStore persistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		asyncHttpClient.setCookieStore(persistentCookieStore);

		parkpk = getIntent().getStringExtra("pk").toString();
		textaddress = (TextView) findViewById(R.id.address);
		textdescription = (TextView) findViewById(R.id.description);
		texttimestart = (TextView) findViewById(R.id.activity_park_rent_text_starttime);
		texttimesend = (TextView) findViewById(R.id.activity_park_rent_text_endtime);
		textremark = (TextView) findViewById(R.id.remarks);
		textrentstate = (TextView) findViewById(R.id.rentstate);
		switchpark = (Switch) findViewById(R.id.parkdetail_switch_control);
		Intent intent = new Intent(ParkOwnActivity.this, BLEservice.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		switchpark.setOnCheckedChangeListener(onCheckedChangeListener);
		switchpark.setEnabled(false);
		// asyncHttpClient.post(AppConstants.BASE_URL+AppConstants.URL_PARKINFO,new
		// RequestParams("parkid",parkpk) ,refreshJsonHttpResponseHandler);
		asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_PARKINFO, new RequestParams("parkid", parkpk), refreshJsonHttpResponseHandler);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
	}

	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Message message = null;
			if (isChecked) {
				message = Message.obtain(null, BLEservice.MSG_OPENT_BLE, macaddress);

			} else {
				message = Message.obtain(null, BLEservice.MSG_CLOSE_BLE, macaddress);
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

		menu.add(MENU_REFRESH);
		menu.add(MENU_SHARE);
		menu.add(MENU_SHARE_CANLCER);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(MENU_REFRESH);
		if (is_shared == false) {
			menu.add(MENU_SHARE);
			menu.add(MENU_SHAREVIP);
		}
		if (is_shared == true && is_borrowed == false) {
			menu.add(MENU_SHARE_CANLCER);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return super.onOptionsItemSelected(item);
		}
		String title = item.getTitle().toString();
		if (title.equals(MENU_REFRESH)) {
			asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_PARKINFO, new RequestParams("parkid", parkpk), refreshJsonHttpResponseHandler);
		}
		if (title.equals(MENU_SHARE)) {
			sharepark();
		}
		if (title.equals(MENU_SHARE_CANLCER)) {
			asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_SHARECANCEL, new RequestParams("parkid", parkpk), sharecancelTextHttpResponseHandler);
		}
		if(title.equals(MENU_SHAREVIP)){
			shareparkvip();
		}
		return super.onOptionsItemSelected(item);
	}

	private void shareparkvip() {
		LayoutInflater inflater = getLayoutInflater();
		final View layout = inflater.inflate(R.layout.dialog_vpisharetime, (ViewGroup) findViewById(R.id.sharetime));
		AlertDialog.Builder builder = new Builder(ParkOwnActivity.this);
		builder.setTitle("分享时间");
		builder.setView(layout);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText editTextstart = (EditText) layout.findViewById(R.id.dialog_edit_starttime);
				EditText editTextend = (EditText) layout.findViewById(R.id.dialog_edit_endtime);
				EditText editTextname=(EditText)layout.findViewById(R.id.et_sharedialog_name);
				String timeStart = editTextstart.getEditableText().toString();
				String timeend = editTextend.getEditableText().toString();
				String name = editTextname.getEditableText().toString();
				AsyncHttpClient client = new AsyncHttpClient();
				PersistentCookieStore persistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
				client.setCookieStore(persistentCookieStore);
				RequestParams requestParams = new RequestParams();
				requestParams.put("parkid", parkpk);
				requestParams.put("starttime", timeStart);
				requestParams.put("endtime", timeend);
				requestParams.put("price", 100);
				requestParams.put("to_user", name);
				asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_SHARE, requestParams, sharevipTextHttpResponseHandler);
			}
		});
		builder.create().show();
	}

	private void sharepark() {
		LayoutInflater inflater = getLayoutInflater();
		final View layout = inflater.inflate(R.layout.dialog_sharetime, (ViewGroup) findViewById(R.id.sharetime));
		AlertDialog.Builder builder = new Builder(ParkOwnActivity.this);
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
				asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_SHARE, requestParams, shareTextHttpResponseHandler);
			}
		});
		builder.create().show();
	}

	private JsonHttpResponseHandler refreshJsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			super.onSuccess(statusCode, headers, responseString);
			System.out.println(responseString);
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			if (statusCode == 200) {
				try {
					is_borrowed = response.getBoolean("is_borrowed");
					is_shared = response.getBoolean("is_shared");
					textaddress.setText(response.getString("address"));
					textdescription.setText(response.getString("describe"));
					if (is_shared == false) {

						switchpark.setEnabled(true);
						macaddress = response.getJSONObject("lockkey").getString("mac_address");
						textrentstate.setText("车位还没有分享");
						texttimestart.setText("");
						texttimesend.setText("");
					}
					if (is_shared == true && is_borrowed == true) {
						switchpark.setEnabled(false);
						textrentstate.setText("车位被借用");
						texttimestart.setText(response.getJSONObject("shareinfo").getString("start_time"));
						texttimesend.setText(response.getJSONObject("shareinfo").getString("end_time"));
					}
					if (is_shared == true && is_borrowed == false) {
						switchpark.setEnabled(false);
						textrentstate.setText("车位分享还没有被借用");
						texttimestart.setText(response.getJSONObject("shareinfo").getString("start_time"));
						texttimesend.setText(response.getJSONObject("shareinfo").getString("end_time"));
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			super.onFailure(statusCode, headers, responseString, throwable);
			textremark.setText("刷新失败:" + statusCode);
		}
	};
	private TextHttpResponseHandler shareTextHttpResponseHandler = new TextHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			if (statusCode == 200) {
				if (responseString.equals("0")) {
					asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_PARKINFO, new RequestParams("parkid", parkpk), refreshJsonHttpResponseHandler);
				} else {
					textremark.setText(responseString);
				}
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		}
	};
	private TextHttpResponseHandler sharevipTextHttpResponseHandler=new TextHttpResponseHandler("utf-8") {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			// TODO Auto-generated method stub
			if (statusCode == 200) {
				if (responseString.equals("0")) {
					textremark.setText(responseString);
					asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_PARKINFO, new RequestParams("parkid", parkpk), refreshJsonHttpResponseHandler);
				} else {
					textremark.setText(responseString);
				}
			}
		}
		
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			// TODO Auto-generated method stub
			
		}
	};
	private TextHttpResponseHandler sharecancelTextHttpResponseHandler=new TextHttpResponseHandler("utf-8") {
		
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			if (statusCode == 200) {
				if (responseString.equals("0")) {
					asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_PARKINFO, new RequestParams("parkid", parkpk), refreshJsonHttpResponseHandler);
				} else {
					textremark.setText(responseString);
				}
			}
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		}
	};
}
