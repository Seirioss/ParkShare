package com.ych.parkshare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ych.http.AsyncHttpClient;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
import com.ych.http.SaxAsyncHttpResponseHandler;
import com.ych.http.SyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.TextHttpResponseHandler;
import com.ych.tool.AppConstants;
import com.ych.tool.GlobalVariable;
import com.ych.tool.SpUtils;
import com.ych.views.RefreshableView;
import com.ych.views.RefreshableView.PullToRefreshListener;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
//fdfsdf
//123

public class TabHomeActivity extends Activity {
	RefreshableView refreshableView;
	ListView listView;
	private ListAdapter listAdapter;
	private final static int UPDATE = 1;
	private List<Map<String, String>> listparks;

	String[] from = new String[] { "username", "describe", "is_borrowed","is_shared" ,"address","comment"};
	int[] to = new int[] { R.id.textviewusername, R.id.textviewdescribe, R.id.textviewrentstate, R.id.textviewsharestate, R.id.textviewaddress, R.id.textviewcomment };
	private SyncHttpClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);

		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		listView = (ListView) findViewById(R.id.list_view);
		listView.setOnItemClickListener(onItemClickListener);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {

				client = new SyncHttpClient();
				PersistentCookieStore persistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
				client.setCookieStore(persistentCookieStore);
				client.post(AppConstants.BASE_URL + AppConstants.URL_USERPARKS, refreshjsonHttpResponseHandler);
				/*
				 * client.post(
				 * "http://121.40.61.76:8080/parkManagementSystem/user/park/",
				 * new JsonHttpResponseHandler("utf-8") {
				 * 
				 * @Override public void onSuccess(int statusCode, Header[]
				 * headers, JSONObject response) { super.onSuccess(statusCode,
				 * headers, response); try { int status =
				 * response.getInt("status"); if (status == 0) { JSONArray
				 * jsonArray = response.getJSONArray("parks"); List<Map<String,
				 * String>> list = jsonArraytoList(jsonArray); listAdapter = new
				 * SimpleAdapter(TabHomeActivity.this, list,
				 * R.layout.item_parkinfo, from, to); Message message =
				 * uihHandler.obtainMessage(); message.what = UPDATE;
				 * message.sendToTarget(); } } catch (JSONException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); } }
				 * 
				 * @Override public void onFailure(int statusCode, Header[]
				 * headers, Throwable throwable, JSONObject errorResponse) {
				 * super.onFailure(statusCode, headers, throwable,
				 * errorResponse); System.out.println(throwable.toString()); }
				 * 
				 * });
				 */
				refreshableView.finishRefreshing();
			}
		}, 0);
	}

	private JsonHttpResponseHandler refreshjsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
			if (statusCode == 200) {
				listparks = new ArrayList<Map<String, String>>();
				System.out.println(response.toString());
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
						map.put(",", address);
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
			// String pk = ((TextView)
			// view.findViewById(R.id.textview)).getText().toString();
			String currentusername = (String) SpUtils.get(getApplicationContext(), AppConstants.USER_NAME, "");
			if (currentusername.equals(name)) {
				Intent intent = new Intent(TabHomeActivity.this, ParkOwnActivity.class);
				intent.putExtra("name", name);
				intent.putExtra("pk", "1");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {
				Intent intent = new Intent(TabHomeActivity.this, ParkRentActivity.class);
				intent.putExtra("name", name);
				intent.putExtra("pk", "1");
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
				listAdapter = new SimpleAdapter(TabHomeActivity.this, listparks, R.layout.item_parkinfo, from, to);
				listView.setAdapter(listAdapter);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private List<Map<String, String>> jsonArraytoList(JSONArray jsonArray) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				map.put("pk", jsonObject.getString("pk"));
				JSONObject tempJsonObject = jsonObject.getJSONObject("fields");
				map.put("address", tempJsonObject.getString("address"));
				map.put("name", tempJsonObject.getString("username"));
				list.add(map);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
