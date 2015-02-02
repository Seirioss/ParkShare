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
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
import com.ych.tool.AppConstants;
import com.ych.tool.GlobalVariable;

import android.R.anim;
import android.R.integer;
import android.R.string;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SearchActivity extends Activity {
	private ActionBar actionBar;
	private SearchView searchView;
	private ListView listViewsearch;
	private String parkpk;
	private Map<String, String> rentableparkinfo;
	private AsyncHttpClient client;
	private List<Map<String, String>> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		listViewsearch = (ListView) findViewById(R.id.searchresult);
		client = new AsyncHttpClient();
		PersistentCookieStore persistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		client.setCookieStore(persistentCookieStore);
		// searchresult.setAdapter(ListAdapter);
		listViewsearch.setOnItemClickListener(onItemClickListener);
		
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			TextView textView = (TextView) view.findViewById(android.R.id.text1);
			Intent intent = new Intent(SearchActivity.this, RentableParkInfoActivity.class);
			intent.putExtra("pk", textView.getText());
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		getMenuInflater().inflate(R.menu.item_actionbar_searchactivity, menu);
		MenuItem menuItem=menu.findItem(R.id.action_item_search);
		searchView = (SearchView)menuItem.getActionView();
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				client.post("http://121.40.61.76:8080/parkManagementSystem/park/search/", searchJsonHttpResponseHandler);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	private JsonHttpResponseHandler searchJsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
			if (statusCode == 200) {
				data = new ArrayList<Map<String, String>>();
				try {
					for (int i = 0; i < response.length(); i++) {
						Map<String, String> map = new HashMap<String, String>();
						JSONObject jsonObject = response.getJSONObject(i);
						String pk = jsonObject.getString("pk");
						String username=jsonObject.getString("username");
						String describe=jsonObject.getString("username");
						String address=jsonObject.getString("address");
						String comment=jsonObject.getString("comment");
						String longitude=jsonObject.getString("longitude");
						String latitude=jsonObject.getString("latitude");
						String start_time=jsonObject.getJSONObject("shareinfo").getString("start_time");
						String end_time=jsonObject.getJSONObject("shareinfo").getString("end_time");
						map.put("pk", pk);
						map.put("username", username);
						map.put("describe", describe);
						map.put("address", address);
						map.put("comment", comment);
						map.put("longitude", longitude);
						map.put("latitude", latitude);
						map.put("start_time", start_time);
						map.put("end_time", end_time);
						data.add(map);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String[] from=new String[]{"start_time","end_time","address"};
				int[] to=new int[]{R.id.tv_search_starttime,R.id.tv_search_endtime,R.id.tv_search_address};
				SimpleAdapter simpleAdapter = new SimpleAdapter(SearchActivity.this, data,R.layout.listitem_search,from,to);
				listViewsearch.setAdapter(simpleAdapter);
			}
			super.onSuccess(statusCode, headers, response);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			super.onFailure(statusCode, headers, responseString, throwable);
		}

	};

}
