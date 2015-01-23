package com.ych.parkshare;

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
import com.ych.tool.GlobalVariable;

import android.R.integer;
import android.R.string;
import android.app.ActionBar;
import android.app.Activity;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends Activity {
	private ActionBar actionBar;
	private SearchView searchView;
	private ListView searchresult;
	private String parkpk;
	private Map<String, String> rentableparkinfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		actionBar = getActionBar();
		actionBar.setTitle("返回");
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		searchresult = (ListView)findViewById(R.id.searchresult);
		searchresult.setAdapter(ListAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item_actionbar_searchactivity, menu);
		searchView = (SearchView) menu.findItem(R.id.action_item_search).getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	
	private void seachresult(){
		AsyncHttpClient client = new AsyncHttpClient();
		PersistentCookieStore persistentCookieStore = ((GlobalVariable)getApplication()).getPersistentCookieStore();
		client.setCookieStore(persistentCookieStore);
		client.post("http://121.40.61.76:8080/parkManagementSystem/park/search/", new RequestParams(), new JsonHttpResponseHandler("utf-8"){
			
			@Override
			public void onSuccess(int statusCode,Header[] headers,JSONObject response){
				super.onSuccess(statusCode, headers, response);
				if(statusCode == 200){
					rentableparkinfo=jsontomap(response);
					searchresult.
				}
			}
		});
	}
	
	private Map<String, String> jsontomap(JSONObject jsonObject){
		Map<String, String> map = new HashMap<String, String>();
		try{
			JSONObject jsonObject1 = jsonObject.getJSONObject("status");
			JSONArray jsonArray = jsonObject.getJSONArray("parks");
			JSONObject jsonObject2 = jsonArray.getJSONObject(0).getJSONObject("fields");
//			map.put("status", jsonObject1.getString("status"));
			if(jsonObject1.equals(1)){
				map.put("username",jsonObject2.getString("username"));
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		return map;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
