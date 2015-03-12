package com.ych.parkshare;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
import com.litesuits.common.assist.Toastor;
import com.ych.dao.DatabaseHelper;
import com.ych.dao.Park;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class ParkOwnActivity extends Activity {
	private final static String TAG = "ParkOwnActivity";
	private final static int UPDATEVIEW = 1;
	private final static int TOASTSHOW = 2;
	private Intent intentaccept;
	private String pk = new String();
	protected Messenger serviceMessenger;
	// private Switch switchpark;
	private final static String MENU_REFRESH = "刷新";
	private final static String MENU_SHARE = "普通分享";
	private final static String MENU_SHAREVIP = "分享给指定用户";
	private final static String MENU_SHARE_CANLCER = "取消分享";
	private final static String MENU_BORROW_END = "结束租用";
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
	private ImageView imagenavigation;

	private Button openbutton;
	private Button closebutton;

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
		// switchpark = (Switch) findViewById(R.id.parkdetail_switch_control);

		imagenavigation = (ImageView) findViewById(R.id.navigation);
		imagenavigation.setOnClickListener(onImageClickListener);

		Intent intent = new Intent(ParkOwnActivity.this, BLEservice.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		// switchpark.setOnCheckedChangeListener(onCheckedChangeListener);
		// switchpark.setEnabled(false);

		openbutton = (Button) findViewById(R.id.openbutton);
		openbutton.setOnClickListener(onClickListener);
		closebutton = (Button) findViewById(R.id.closebutton);
		closebutton.setOnClickListener(onClickListener);
		// asyncHttpClient.post(AppConstants.BASE_URL+AppConstants.URL_PARKINFO,new
		// RequestParams("parkid",parkpk) ,refreshJsonHttpResponseHandler);
		// asyncHttpClient.post(AppConstants.BASE_URL +
		// AppConstants.URL_PARKINFO, new RequestParams("parkid", parkpk),
		// refreshJsonHttpResponseHandler);

	}

	@Override
	protected void onStart() {
		if (GlobalVariable.netWorkAvailable) {
			asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKSDETAIL, refreshJsonHttpResponseHandler);
		} else {
			Message message = uiHandler.obtainMessage();
			message.what = UPDATEVIEW;
			message.sendToTarget();
		}
		super.onStart();
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
			if (!isChecked) {
				message = Message.obtain(null, BLEservice.MSG_OPENT_BLE, macaddress);
				System.out.println(isChecked);

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

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Message message = null;
			switch (v.getId()) {
			case (R.id.closebutton):
				message = Message.obtain(null, BLEservice.MSG_OPENT_BLE, macaddress);
				break;
			case (R.id.openbutton):
				message = Message.obtain(null, BLEservice.MSG_CLOSE_BLE, macaddress);
				break;
			default:
				break;
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

	private View.OnClickListener onImageClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			launchNavigator();
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(MENU_REFRESH);
		menu.add(MENU_SHARE);
		menu.add(MENU_SHARE_CANLCER);
		menu.add(MENU_BORROW_END);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(MENU_REFRESH);
		menu.add(MENU_BORROW_END);
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
		if (GlobalVariable.netWorkAvailable) {
			if (title.equals(MENU_REFRESH)) {
				asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKSDETAIL, new RequestParams("parkid", parkpk), refreshJsonHttpResponseHandler);
			}
			if (title.equals(MENU_SHARE)) {
				sharepark();
			}
			if (title.equals(MENU_SHARE_CANLCER)) {
				asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_SHARECANCEL, new RequestParams("parkid", parkpk), sharecancelTextHttpResponseHandler);
			}
			if (title.equals(MENU_SHAREVIP)) {
				shareparkvip();
			}
			if (title.equals(MENU_BORROW_END)) {
			    settlement();
			}
		} else {
			Message message = uiHandler.obtainMessage();
			message.what = TOASTSHOW;
			message.obj = "网络不可用";
			message.sendToTarget();
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
				EditText editTextname = (EditText) layout.findViewById(R.id.et_sharedialog_name);
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
	
	private void settlement() {
		LayoutInflater inflater = getLayoutInflater();
		final View layout = inflater.inflate(R.layout.dialog_settlement, (ViewGroup)findViewById(R.id.settlement));
		AlertDialog.Builder builder = new Builder(ParkOwnActivity.this);
		builder.setTitle("确认支付");
		builder.setView(layout);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确认支付", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(ParkOwnActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
			}
		});
		builder.create().show();
	}

	private JsonHttpResponseHandler refreshJsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
			if (statusCode == 200) {
				updateparkcache(response);
				Message message = uiHandler.obtainMessage();
				message.what = UPDATEVIEW;
				message.sendToTarget();
			}
			super.onSuccess(statusCode, headers, response);
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
					asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKSDETAIL, new RequestParams("parkid", parkpk), refreshJsonHttpResponseHandler);
				} else {
					textremark.setText(responseString);
				}
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		}
	};
	private TextHttpResponseHandler sharevipTextHttpResponseHandler = new TextHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			// TODO Auto-generated method stub
			if (statusCode == 200) {
				if (responseString.equals("0")) {
					textremark.setText(responseString);
					asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKSDETAIL, new RequestParams("parkid", parkpk), refreshJsonHttpResponseHandler);
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
	private TextHttpResponseHandler sharecancelTextHttpResponseHandler = new TextHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			if (statusCode == 200) {
				if (responseString.equals("0")) {
					asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKSDETAIL, new RequestParams("parkid", parkpk), refreshJsonHttpResponseHandler);
				} else {
					textremark.setText(responseString);
				}
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		}
	};
	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATEVIEW:
				DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
				Dao<Park, Integer> parkdao = null;
				try {
					parkdao = databaseHelper.getParkDataDao();
					Park parkinfo = parkdao.queryForEq("pk", parkpk).get(0);
					is_shared = parkinfo.isIs_shared();
					is_borrowed = parkinfo.isIs_borrowed();
					textaddress.setText(parkinfo.getAddress());
					textdescription.setText(parkinfo.getDescribe());
					if (!parkinfo.isIs_shared()) {

						// switchpark.setEnabled(true);
						openbutton.setClickable(true);
						closebutton.setClickable(true);
						macaddress = parkinfo.getMAC();
						textrentstate.setText("车位还没有分享");
						texttimestart.setText("");
						texttimesend.setText("");
					}
					if (is_shared == true && is_borrowed == true) {
						// switchpark.setEnabled(false);
						openbutton.setClickable(false);
						closebutton.setClickable(false);

						textrentstate.setText("车位被借用");
						texttimestart.setText(parkinfo.getTime_share_begig().toLocaleString());
						texttimesend.setText(parkinfo.getTime_share_end().toLocaleString());
					}
					if (is_shared == true && is_borrowed == false) {
						// switchpark.setEnabled(false);
						openbutton.setClickable(false);
						closebutton.setClickable(false);

						textrentstate.setText("车位分享还没有被借用");
						texttimestart.setText(parkinfo.getTime_share_begig().toLocaleString());
						texttimesend.setText(parkinfo.getTime_share_end().toLocaleString());
					}

				} catch (SQLException e) {
					Log.e(TAG, e.toString());
					e.printStackTrace();
				}
				// parkdao.queryBuilder().
				break;
			case TOASTSHOW:
				new Toastor(ParkOwnActivity.this).showSingletonToast((String) msg.obj);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private void launchNavigator() {
		// 这里给出一个起终点示例，实际应用中可以通过POI检索、外部POI来源等方式获取起终点坐标
		BNaviPoint startPoint = new BNaviPoint(121.508693, 31.285126, "书香公寓", BNaviPoint.CoordinateType.BD09_MC);
		BNaviPoint endPoint = new BNaviPoint(121.521191, 31.303805, "五角场", BNaviPoint.CoordinateType.BD09_MC);
		BaiduNaviManager.getInstance().launchNavigator(this, startPoint, // 起点（可指定坐标系）
				endPoint, // 终点（可指定坐标系）
				NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // 算路方式
				true, // 真实导航
				BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // 在离线策略
				new OnStartNavigationListener() { // 跳转监听

					@Override
					public void onJumpToNavigator(Bundle configParams) {
						Intent intent = new Intent(ParkOwnActivity.this, BNavigatorActivity.class);
						intent.putExtras(configParams);
						startActivity(intent);
					}

					@Override
					public void onJumpToDownloader() {
					}
				});
	}

	private void updateparkcache(JSONArray response) {
		DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
		Dao<Park, Integer> parkdao = null;
		try {
			parkdao = databaseHelper.getParkDataDao();
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		try {
			parkdao.delete(parkdao.queryForAll());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int i = 0; i < response.length(); i++) {
			try {
				JSONObject jsonObject = response.getJSONObject(i);
				int pk = jsonObject.getInt("pk");
				String username = jsonObject.getString("username");
				String describe = jsonObject.getString("describe");
				String address = jsonObject.getString("address");
				String comment = jsonObject.getString("comment");
				double longitude = jsonObject.getDouble("longitude");
				double latitude = jsonObject.getDouble("latitude");
				boolean is_borrowed = jsonObject.getBoolean("is_borrowed");
				boolean is_shared = jsonObject.getBoolean("is_shared");
				Date end_time = null;
				float price = 0;
				Date start_time = null;
				String user_borrowed = null;
				String close_key = null;
				String mac_address = null;
				String lock_name = null;
				String open_key = null;
				String serial_number;
				if (is_shared == true) {
					JSONObject jsonObjectshareinfo = jsonObject.getJSONObject("shareinfo");
					price = Float.valueOf(jsonObjectshareinfo.getString("price"));
					user_borrowed = jsonObjectshareinfo.getString("user_borrowed");
					String timetemp = jsonObjectshareinfo.getString("end_time");
					timetemp = timetemp.replace("T", " ").replace("Z", "");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					end_time = sdf.parse(timetemp);
					timetemp = jsonObjectshareinfo.getString("start_time");
					timetemp = timetemp.replace("T", " ").replace("Z", "");
					start_time = sdf.parse(timetemp);
				}
				JSONObject jsonObjectlockkey = jsonObject.getJSONObject("lockkey");
				if (jsonObjectlockkey != null) {
					close_key = jsonObjectlockkey.getString("close_key");
					mac_address = jsonObjectlockkey.getString("mac_address");
					lock_name = jsonObjectlockkey.getString("lock_name");
					open_key = jsonObjectlockkey.getString("open_key");
					serial_number = jsonObjectlockkey.getString("serial_number");
				}
				Park park = new Park();
				park.setPk(pk);
				park.setName_own(username);
				park.setDescribe(describe);
				park.setAddress(address);
				park.setComment(comment);
				park.setLatitude(latitude);
				park.setLongitude(latitude);
				park.setIs_borrowed(is_borrowed);
				park.setIs_shared(is_shared);
				park.setTime_share_end(end_time);
				park.setTime_share_begig(start_time);
				park.setPrice(price);
				park.setName_borrowed(user_borrowed);
				park.setKey_open(open_key);
				park.setKey_lock(close_key);
				park.setLock_name(lock_name);
				park.setMAC(mac_address);
				parkdao.create(park);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
