package com.ych.parkshare;

import java.util.HashMap;
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
import android.R.array;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RentableParkInfoActivity extends Activity {

	private Button makeorderbutton;
	private EditText editparkdescription;
	private EditText editvalidity;
	private EditText editparkaddress;
	private EditText editownertelephone;
	private EditText editofeescale;
	private EditText editremark;
	private EditText editstarttime;
	private EditText editendtime;
	private String pk;
	private AsyncHttpClient asyncHttpClient;
	private Map<String, Object> parkinfo;;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rentable_park_info);

		ActionBar actionbar = getActionBar();
		actionbar.setTitle("可租用车位详情");
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayHomeAsUpEnabled(true);

		makeorderbutton = (Button) findViewById(R.id.makeorderbutton);
		editparkdescription = (EditText) findViewById(R.id.editparkdescription);
		editvalidity = (EditText) findViewById(R.id.editvalidity);
		editparkaddress = (EditText) findViewById(R.id.editparkaddress);
		editownertelephone = (EditText) findViewById(R.id.editownertelephone);
		editstarttime = (EditText) findViewById(R.id.editstarttime);
		editendtime = (EditText) findViewById(R.id.editendtime);
		editofeescale = (EditText) findViewById(R.id.editofeescale);
		editremark = (EditText) findViewById(R.id.editremark);
		makeorderbutton.setOnClickListener(OnClickListener);
		pk = getIntent().getStringExtra("pk");
		
		makeorderbutton.setOnClickListener(OnClickListener);
		
		asyncHttpClient = new AsyncHttpClient();
		PersistentCookieStore PersistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		RequestParams requestParams=new RequestParams();
		requestParams.put("parkid", pk);
		asyncHttpClient.setCookieStore(PersistentCookieStore);
		asyncHttpClient.post(AppConstants.BASE_URL+AppConstants.URL_PARKINFO, requestParams,refreshjsonhHttpResponseHandler);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private View.OnClickListener OnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String time_start=editstarttime.getText().toString();
			String time_end=editendtime.getText().toString();
			RequestParams requestParams=new RequestParams();
			requestParams.put("parkid", pk);
			asyncHttpClient.post(AppConstants.BASE_URL+AppConstants.URL_BOOK,requestParams, bookjsonhHttpResponseHandler);
		}
	};

	private JsonHttpResponseHandler bookjsonhHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			// TODO Auto-generated method stub
			if(statusCode==200){
				try {
					int status=response.getInt("status");
					if(status==0){
						String mes=response.getString("message");
						Toast.makeText(RentableParkInfoActivity.this,mes, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			super.onSuccess(statusCode, headers, response);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			// TODO Auto-generated method stub
			Toast.makeText(RentableParkInfoActivity.this, "预约失败，网络错误", Toast.LENGTH_SHORT).show();
			super.onFailure(statusCode, headers, responseString, throwable);
		}
		
	};
	private JsonHttpResponseHandler refreshjsonhHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			// TODO Auto-generated method stub
			super.onSuccess(statusCode, headers, response);
			if(statusCode==200){
				parkinfo= jsontomap(response);
				 editparkdescription.setText(parkinfo.get("describe").toString());
				 editparkaddress.setText(parkinfo.get("address").toString());
				 editofeescale.setText(parkinfo.get("price").toString());
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			// TODO Auto-generated method stub
			Toast.makeText(RentableParkInfoActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
			super.onFailure(statusCode, headers, responseString, throwable);
			
		}
		
	};
	private Map<String, Object> jsontomap(JSONObject jsonObject) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JSONArray jsonArrayinfos = jsonObject.getJSONArray("parks");
			JSONObject jsonObject1 = jsonArrayinfos.getJSONObject(0).getJSONObject("fields");
			JSONObject jsonObject2 = jsonArrayinfos.getJSONObject(1).getJSONObject("fields");
			JSONObject jsonObject3 = jsonArrayinfos.getJSONObject(2).getJSONObject("fields");
			map.put("username", jsonObject1.getString("username"));
			map.put("is_borrowed", jsonObject1.getBoolean("is_borrowed"));
			map.put("comment", jsonObject1.getString("comment"));
			map.put("describe", jsonObject1.getString("describe"));
			map.put("address", jsonObject1.getString("address"));
			map.put("user_borrowed", jsonObject2.getString("user_borrowed"));
			map.put("price", jsonObject2.getString("price"));
			map.put("start_time", jsonObject2.getString("start_time"));
			map.put("end_time", jsonObject2.getString("end_time"));
			map.put("mac_address", jsonObject3.getString("mac_address"));
			map.put("close_key", jsonObject3.getString("close_key"));
			map.put("open_key", jsonObject3.getString("open_key"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
}
