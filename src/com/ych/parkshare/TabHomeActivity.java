package com.ych.parkshare;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.PersistentCookieStore;
import com.ych.http.SyncHttpClient;
import com.ych.tool.AppConstants;
import com.ych.tool.GlobalVariable;
import com.ych.tool.SpUtils;
import com.ych.views.RefreshableView;
import com.ych.views.RefreshableView.PullToRefreshListener;
//fdfsdf
//123

public class TabHomeActivity extends Activity {
	RefreshableView refreshableView;
	ListView listView;
	private ListAdapter listAdapter;
	private final static int UPDATE = 1;
	private List<Map<String, String>> listparks;
	private SyncHttpClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		client = new SyncHttpClient();
		PersistentCookieStore persistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		client.setCookieStore(persistentCookieStore);
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		listView = (ListView) findViewById(R.id.list_view);
		listView.setOnItemClickListener(onItemClickListener);
		refreshableView.setOnRefreshListener(pullToRefreshListener, 0);
	}
	private PullToRefreshListener pullToRefreshListener=new PullToRefreshListener() {
		@Override
		public void onRefresh() {
			client.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKS, refreshjsonHttpResponseHandler);
			refreshableView.finishRefreshing();
		}
	};
	private JsonHttpResponseHandler refreshjsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
			if (statusCode == 200) {
				listparks = new ArrayList<Map<String, String>>();
				try {
					for (int i = 0; i < response.length(); i++) {
						JSONObject jsonObject = response.getJSONObject(i);
						String address = jsonObject.getString("address");
						String comment = jsonObject.getString("comment");
						String describe = jsonObject.getString("describe");
						String pk = jsonObject.getString("pk");
						boolean is_borrowed = jsonObject.getBoolean("is_borrowed");
						boolean is_shared = jsonObject.getBoolean("is_shared");
						String username = jsonObject.getString("username");
						Map<String, String> map = new HashMap<String, String>();
						map.put("address", address);
						map.put("comment", comment);
						map.put("describe", describe);
						map.put("username", username);
						map.put("pk", pk);
						if (is_borrowed) {
							map.put("is_borrowed", "被租用");
						} else {
							map.put("is_borrowed", "未背租用");
						}
						if (is_shared) {
							map.put("is_shared", "已分享");
						} else {
							map.put("is_shared", "未分享");
						}
						listparks.add(map);

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message message = uihHandler.obtainMessage();
				message.what = UPDATE;
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

			String name = ((TextView) view.findViewById(R.id.textviewusername)).getText().toString();
			String pk= listparks.get(position).get("pk");
			
			
			String currentusername = (String) SpUtils.get(getApplicationContext(), AppConstants.USER_NAME, "");
			name=name.trim();
			currentusername=currentusername.trim();
			if (name.equals(currentusername)) {
				Intent intent = new Intent(TabHomeActivity.this, ParkOwnActivity.class);
				intent.putExtra("pk", pk);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {
				Intent intent = new Intent(TabHomeActivity.this, ParkRentActivity.class);
				intent.putExtra("pk", pk);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

		}
	};
	private Handler uihHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE:
				String[] from = new String[] { "username", "describe", "is_borrowed","is_shared" ,"address","comment"};
				int[] to = new int[] { R.id.textviewusername, R.id.textviewdescribe, R.id.textviewrentstate, R.id.textviewsharestate, R.id.textviewaddress, R.id.textviewcomment };
				listAdapter = new SimpleAdapter(TabHomeActivity.this, listparks, R.layout.item_parkinfo, from, to);
				listView.setAdapter(listAdapter);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};
}
