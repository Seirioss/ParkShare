package com.ych.parkshare;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.navisdk.CommonParams.NetConnectStatus;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.ych.dao.DatabaseHelper;
import com.ych.dao.Park;
import com.ych.http.AsyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.PersistentCookieStore;
import com.ych.http.SyncHttpClient;
import com.ych.receivers.NetBroadcastReceiver;
import com.ych.receivers.NetBroadcastReceiver.NetListener;
import com.ych.tool.AppConstants;
import com.ych.tool.GlobalVariable;
import com.ych.tool.NetworkConnections;
import com.ych.tool.SpUtils;
import com.ych.views.RefreshableView;
import com.ych.views.RefreshableView.PullToRefreshListener;
//fdfsdf
//123

public class TabHomeActivity extends Activity implements NetListener {
	private final String TAG="TabHomeActivity";
	RefreshableView refreshableView;
	ListView listView;
	private ListAdapter listAdapter;
	private final static int UPDATELISTVIEW = 1;
	private List<Map<String, String>> listparks;
	private SyncHttpClient syncHttpClient;
	private AsyncHttpClient asyncHttpClient;
	private DatabaseHelper databaseHelper;
	private Dao<Park, Integer> parkdao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		asyncHttpClient = new AsyncHttpClient();
		syncHttpClient = new SyncHttpClient();
		NetBroadcastReceiver.mListeners.add(this);
		PersistentCookieStore persistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		syncHttpClient.setCookieStore(persistentCookieStore);
		asyncHttpClient.setCookieStore(persistentCookieStore);
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		listView = (ListView) findViewById(R.id.list_view);
		listView.setOnItemClickListener(onItemClickListener);
		refreshableView.setOnRefreshListener(pullToRefreshListener, 0);
		databaseHelper=OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
		try {
			parkdao=databaseHelper.getParkDataDao();
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		if (GlobalVariable.netWorkAvailable) {
			asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKSDETAIL, refreshjsonHttpResponseHandler_asyncHttpClient);
		}
		super.onStart();
	}

	private PullToRefreshListener pullToRefreshListener = new PullToRefreshListener() {
		@Override
		public void onRefresh() {
			syncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKSDETAIL, refreshjsonHttpResponseHandler_syncHttpClient);
			refreshableView.finishRefreshing();
		}
	};
	private JsonHttpResponseHandler refreshjsonHttpResponseHandler_syncHttpClient = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
			if (statusCode == 200) {
				updateparkcache(response);
				Message message = uihHandler.obtainMessage();
				message.what = UPDATELISTVIEW;
				message.sendToTarget();
			}
			super.onSuccess(statusCode, headers, response);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			super.onFailure(statusCode, headers, responseString, throwable);
		}

	};
	private JsonHttpResponseHandler refreshjsonHttpResponseHandler_asyncHttpClient = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
			if (statusCode == 200) {
				updateparkcache(response);
				Message message = uihHandler.obtainMessage();
				message.what = UPDATELISTVIEW;
				message.sendToTarget();
			}
			super.onSuccess(statusCode, headers, response);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			super.onFailure(statusCode, headers, responseString, throwable);
		}

	};
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.i("htc", "clickyes");
			String name = ((TextView) view.findViewById(R.id.parkinfo_user)).getText().toString();
			String pk = listparks.get(position).get("pk");
			String currentusername = (String) SpUtils.get(getApplicationContext(), AppConstants.USER_NAME, "");
			name = name.trim();
			currentusername = currentusername.trim();
			if (name.equals(currentusername)) {
				Intent intent = new Intent(TabHomeActivity.this, ParkOwnActivity.class);
				intent.putExtra("pk", pk);
				intent.setAction(AppConstants.ACTION_TabHomeActivity);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {
				Intent intent = new Intent(TabHomeActivity.this, ParkRentActivity.class);
				intent.putExtra("pk", pk);
				intent.setAction(AppConstants.ACTION_TabHomeActivity);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

		}
	};
	private void updateparkcache(JSONArray response){
		try {
			parkdao.delete(parkdao.queryForAll());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i=0;i<response.length();i++){
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
				Date end_time=null;
				float price=0;
				Date start_time=null;
				String user_borrowed=null;
				String close_key = null;
				String mac_address = null;
				String lock_name = null;
				String open_key = null;
				String serial_number;
				if(is_shared==true){
					JSONObject jsonObjectshareinfo=jsonObject.getJSONObject("shareinfo");
					price=Float.valueOf(jsonObjectshareinfo.getString("price"));
					user_borrowed=jsonObjectshareinfo.getString("user_borrowed");
					String timetemp=jsonObjectshareinfo.getString("end_time");
					timetemp=timetemp.replace("T", " ").replace("Z", "");
					SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
					end_time=sdf.parse(timetemp);
					timetemp=jsonObjectshareinfo.getString("start_time");
					timetemp=timetemp.replace("T", " ").replace("Z", "");
					start_time=sdf.parse(timetemp);
				}
				JSONObject jsonObjectlockkey=jsonObject.getJSONObject("lockkey");
				if(jsonObjectlockkey!=null){
					close_key=jsonObjectlockkey.getString("close_key");
					mac_address=jsonObjectlockkey.getString("mac_address");
					lock_name=jsonObjectlockkey.getString("lock_name");
					open_key=jsonObjectlockkey.getString("close_key");
					serial_number=jsonObjectlockkey.getString("serial_number");
				}
				Park park=new Park();
				park.setPk(pk);
				park.setName_own(username);
				park.setDescribe(describe);
				park.setAddress(address);
				park.setComment(comment);
				park.setLatitude(latitude);
				park.setLongitude(latitude);
				park.setIs_borrowed(is_borrowed);
				park.setIs_shared(is_borrowed);
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
	private Handler uihHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATELISTVIEW:
				String[] from = new String[] { "username", "address", "status" };
				int[] to = new int[] { R.id.parkinfo_user, R.id.parkinfo_address, R.id.parkinfo_status };
				listparks=getListParks();
				listAdapter = new SimpleAdapter(TabHomeActivity.this, listparks, R.layout.item_parkinfo, from, to);
				//listView.setAdapter(listAdapter);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onNetAvailableChange() {

	}

	protected List<Map<String, String>> getListParks() {
		listparks=new ArrayList<Map<String,String>>();
		try {
			List<Park> list= parkdao.queryForAll();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Park park = (Park) iterator.next();
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
