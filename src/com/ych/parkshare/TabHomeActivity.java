package com.ych.parkshare;
//
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ych.http.AsyncHttpClient;
import com.ych.http.RequestParams;
import com.ych.http.SaxAsyncHttpResponseHandler;
import com.ych.http.SyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.TextHttpResponseHandler;
import com.ych.views.RefreshableView;
import com.ych.views.RefreshableView.PullToRefreshListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
//fdfsdf
//123
public class TabHomeActivity extends Activity {
	RefreshableView refreshableView;
	ListView listView;
	ArrayAdapter<String> adapter;
	String[] items = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L" };
	private SyncHttpClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		client = new SyncHttpClient();
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		listView = (ListView) findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		listView.setAdapter(adapter);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				
				refreshableView.finishRefreshing();
			}
		}, 0);
	}
}