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
import com.ych.tool.GlobalVariable;
import com.ych.views.RefreshableView;
import com.ych.views.RefreshableView.PullToRefreshListener;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
//fdfsdf
//123

public class TabHomeActivity extends Activity {
	RefreshableView refreshableView;
	ListView listView;
	private ListAdapter listAdapter;
	private final static int UPDATE=1;
	String[] items = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L" };
	String[] from =new String[]{"pk","address","comment","describe","owner"};
	int[] to=new int[]{R.id.pk,R.id.address,R.id.comment,R.id.describe,R.id.owner};
	private SyncHttpClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
	
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		listView = (ListView) findViewById(R.id.list_view);
		listView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				client=new SyncHttpClient();
				PersistentCookieStore persistentCookieStore=((GlobalVariable)getApplication()).getPersistentCookieStore();
				client.setCookieStore(persistentCookieStore);
				client.post("http://121.40.61.76:8080/parkManagementSystem/user/park/", new JsonHttpResponseHandler("utf-8"){

					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							int status=response.getInt("status");
							if(status==0){
								JSONArray jsonArray=response.getJSONArray("parks");
								List<Map<String, String>> list =jsonArraytoList(jsonArray);
								listAdapter=new SimpleAdapter(TabHomeActivity.this, list, R.layout.item_parkinfo, from, to);
								Message message=uihHandler.obtainMessage();
								message.what=UPDATE;
								message.sendToTarget();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable, errorResponse);
						System.out.println(throwable.toString());
					}
					
				});
				refreshableView.finishRefreshing();
			}
		}, 0);
	}
	
	private Handler uihHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE:
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
				JSONObject jsonObject=jsonArray.getJSONObject(i);
				Map<String, String> map = new HashMap<String, String>(); 
				map.put("pk", jsonObject.getString("pk"));
				JSONObject tempJsonObject=jsonObject.getJSONObject("fields");
				map.put("address", tempJsonObject.getString("address"));
				map.put("comment", tempJsonObject.getString("comment"));
				map.put("describe", tempJsonObject.getString("describe"));
				map.put("owner", tempJsonObject.getString("owner"));
				list.add(map);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}