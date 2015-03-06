package com.ych.parkshare;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import com.litesuits.common.assist.Toastor;
import com.ych.dao.DatabaseHelper;
import com.ych.dao.Park;
import com.ych.http.AsyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
import com.ych.http.TextHttpResponseHandler;
import com.ych.parkshare.R.menu;
import com.ych.serves.BLEservice;
import com.ych.tool.AppConstants;
import com.ych.tool.GlobalVariable;
import com.ych.tool.SpUtils;

import android.R.anim;
import android.R.bool;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ParkRentActivity extends Activity {
	private final static String TAG = "ParkRentActivity";
	private final static int UPDATEVIEW = 1;
	private final static int TOASTSHOW = 2;
	private Intent intentaccept;
	private String pk = new String();
	protected Messenger serviceMessenger;
	// private Switch switchpark;
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
	private AsyncHttpClient asyncHttpClient;
	private boolean is_borrowed;
	private boolean is_shared;

	private String macaddress = new String();
	private ImageView imagenavigation;
	private String action;

	private Button openbutton;
	private Button closebutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_park_rent);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("返回");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		action = getIntent().getAction();

		parkpk = getIntent().getStringExtra("pk").toString();
		asyncHttpClient = new AsyncHttpClient();
		PersistentCookieStore PersistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		asyncHttpClient.setCookieStore(PersistentCookieStore);
		textaddress = (TextView) findViewById(R.id.address);
		textdescription = (TextView) findViewById(R.id.description);
		texttimestart = (TextView) findViewById(R.id.activity_park_rent_text_starttime);
		texttimesend = (TextView) findViewById(R.id.activity_park_rent_text_endtime);
		textremark = (TextView) findViewById(R.id.remarks);
		// switchpark = (Switch) findViewById(R.id.parkdetail_switch_control);
		Intent intent = new Intent(ParkRentActivity.this, BLEservice.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		// switchpark.setOnCheckedChangeListener(onCheckedChangeListener);
		imagenavigation = (ImageView) findViewById(R.id.navigation);
		imagenavigation.setOnClickListener(onImageClickListener);

		openbutton = (Button) findViewById(R.id.openbutton);
		openbutton.setOnClickListener(onClickListener);
		closebutton = (Button) findViewById(R.id.closebutton);
		closebutton.setOnClickListener(onClickListener);

	}

	@Override
	protected void onStart() {
		if (GlobalVariable.netWorkAvailable) {
			asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKSDETAIL, refreshuiJsonHttpResponseHandler);
		} else {
			Message message = uiHandler.obtainMessage();
			message.what = UPDATEVIEW;
			message.sendToTarget();
		}
		super.onStart();
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
		menu.add(MENU_REFRESH);
		menu.add(MENU_STORE);
		if (is_borrowed == true && is_shared == true) {
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
			if (!TextUtils.isEmpty(action)) {
				if (action.equals(AppConstants.ACTION_BaiduPushMessageReceiver)) {
					Intent intent = new Intent(ParkRentActivity.this, TabHostActivity.class);
					startActivity(intent);
				}
			}
			finish();
			return super.onOptionsItemSelected(item);
		}
		String title = item.getTitle().toString();
		if (GlobalVariable.netWorkAvailable) {
			if (title.equals(MENU_STORE)) {

			}
			if (title.equals(MENU_BOOK_CANLCER)) {
				asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_BOOKCANCEL, new RequestParams("parkid", parkpk), bookcanceltextHttpResponseHandler);
			}
			if (title.equals(MENU_REFRESH)) {
				asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_PARKINFO, new RequestParams("parkid", parkpk), refreshuiJsonHttpResponseHandler);
			}
		} else {
			Message message = uiHandler.obtainMessage();
			message.what = TOASTSHOW;
			message.obj = "网络不可用";
			message.sendToTarget();
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onDestroy() {
		asyncHttpClient.cancelAllRequests(true);
		unbindService(conn);
		super.onDestroy();
	}

	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Message message = null;
			if (!isChecked) {
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

	private JsonHttpResponseHandler refreshuiJsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

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
			textremark.setText("刷新失败：" + statusCode);
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
	private TextHttpResponseHandler bookcanceltextHttpResponseHandler = new TextHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			if (statusCode == 200) {
				if (responseString.equals("0")) {
					textremark.setText("退订成功\n" + (int) (Math.random() * 100 + 1));
					textaddress.setText("");
					textdescription.setText("");
					texttimesend.setText("");
					texttimestart.setText("");
					// switchpark.setEnabled(false);
					openbutton.setClickable(false);
					closebutton.setClickable(false);
					asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKSDETAIL, new RequestParams("parkid", parkpk), refreshuiJsonHttpResponseHandler);
				} else {
					Toast.makeText(ParkRentActivity.this, responseString, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

		}
	};

	private View.OnClickListener onImageClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			launchNavigator();
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
					is_borrowed = parkinfo.isIs_borrowed();
					is_shared = parkinfo.isIs_borrowed();
					macaddress = parkinfo.getMAC();
					if (is_borrowed == true && is_shared == true) {
						// switchpark.setEnabled(true);
						openbutton.setClickable(true);
						closebutton.setClickable(true);
					}
					if (is_borrowed == false) {
						// switchpark.setEnabled(false);
						openbutton.setClickable(false);
						closebutton.setClickable(false);
					}
					textaddress.setText(parkinfo.getAddress());
					textdescription.setText(parkinfo.getDescribe());
					texttimesend.setText(parkinfo.getTime_share_end().toLocaleString());
					texttimestart.setText(parkinfo.getTime_share_begig().toLocaleString());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case TOASTSHOW:
				new Toastor(ParkRentActivity.this).showSingletonToast((String) msg.obj);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

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
						Intent intent = new Intent(ParkRentActivity.this, BNavigatorActivity.class);
						intent.putExtras(configParams);
						startActivity(intent);
					}

					@Override
					public void onJumpToDownloader() {
					}
				});
	}
}
