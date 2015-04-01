package com.ych.parkshare;

import java.util.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.litesuits.common.assist.Toastor;
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
import com.ych.tool.SpUtils;

public class MyParkAcitvity extends Activity implements NetListener{

	private final static int UPDATELISTVIEW = 1;
	private final static int SHOWTOAST = 2;
	
	private final String TAG = "MyPark";
	ListView listView;
	private ListAdapter listAdapter;
	private List<Map<String, String>> listparks;
	private SyncHttpClient syncHttpClient;
	private AsyncHttpClient asyncHttpClient;
	private DatabaseHelper databaseHelper;
	private Dao<Park, Integer> parkdao;
	
	private String currentusername;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myparks);
		asyncHttpClient = new AsyncHttpClient();
		syncHttpClient = new SyncHttpClient();
		NetBroadcastReceiver.mListeners.add(this);
		PersistentCookieStore persistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		syncHttpClient.setCookieStore(persistentCookieStore);
		asyncHttpClient.setCookieStore(persistentCookieStore);
		currentusername = (String) SpUtils.get(getApplicationContext(), AppConstants.USER_NAME, "");
		currentusername = currentusername.trim();
		listView = (ListView)findViewById(R.id.mypark_list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String pk = listparks.get(position).get("pk");
                Intent intent = new Intent(MyParkAcitvity.this, ParkOwnActivity.class);
                intent.putExtra("pk", pk);
                intent.setAction(AppConstants.ACTION_TabHomeActivity);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
			}
			
		});
		
		databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
	    try {
			parkdao = databaseHelper.getParkDataDao();
		} catch (SQLException e) {
			// TODO: handle exception
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		if (GlobalVariable.netWorkAvailable) {
			asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKSDETAIL, jsonHttpResponseHandler);		    
		} else {
			Message message = uihHandler.obtainMessage();
			message.what = UPDATELISTVIEW;
			message.sendToTarget();
		}
		super.onStart();
	}

	private JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONArray response) {
			if (statusCode == 200) {
				updateparkcache(response);
				Message message = uihHandler.obtainMessage();
				message.what = UPDATELISTVIEW;
				message.sendToTarget();
			}
			super.onSuccess(statusCode, headers, response);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			// TODO Auto-generated method stub
			super.onFailure(statusCode, headers, responseString, throwable);
		}
		
	};
	
	private Handler uihHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATELISTVIEW:
				String[] from = new String[] {"username", "address", "status"};
				int[] to = new int[] {R.id.parkinfo_user, R.id.parkinfo_address, R.id.parkinfo_status};
				listparks = getListParks();
				listAdapter = new SimpleAdapter(MyParkAcitvity.this, listparks, R.layout.item_parkinfo, from, to);
				listView.setAdapter(listAdapter);
				break;
			case SHOWTOAST:
				new Toastor(MyParkAcitvity.this).showSingletonToast((String)msg.obj);
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	protected List<Map<String, String>> getListParks() {
		listparks = new ArrayList<Map<String,String>>();
		try {
			List<Park> list = parkdao.queryForAll();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Park park = (Park) iterator.next();
				Map<String, String> map = new HashMap<String, String>();
				if (currentusername.equals(park.getName_own())) {
					map.put("address", park.getAddress());
				map.put("comment", park.getComment());
				map.put("describe", park.getDescribe());
				map.put("username", park.getName_own());
				map.put("pk", String.valueOf(park.getPk()));
				if (!park.isIs_shared()) {
					map.put("status", "未分享");
				} else {
					if (park.isIs_borrowed()) {
						map.put("status", "被租用");
					} else {
						map.put("status", "五人组用");
					}
				}
				}
				listparks.add(map);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return listparks;
	}
	
	private void updateparkcache(JSONArray response) {
		try {
			parkdao.delete(parkdao.queryForAll());
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
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
					timetemp = timetemp.replace("T", " ").replace("Z", " ");
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
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	@Override
	public void onNetAvailableChange() {
		// TODO Auto-generated method stub
		
	}
	
	
}
