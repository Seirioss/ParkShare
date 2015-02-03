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
import com.ych.http.TextHttpResponseHandler;
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
	private int parkstate;

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
//		editvalidity = (EditText) findViewById(R.id.editvalidity);
		editparkaddress = (EditText) findViewById(R.id.editparkaddress);
		editownertelephone = (EditText) findViewById(R.id.editownertelephone);
		editstarttime = (EditText) findViewById(R.id.editstarttime);
		editendtime = (EditText) findViewById(R.id.editendtime);
		editofeescale = (EditText) findViewById(R.id.editofeescale);
//		editremark = (EditText) findViewById(R.id.editremark);
		makeorderbutton.setOnClickListener(OnClickListener);
		pk = getIntent().getStringExtra("pk");

		makeorderbutton.setOnClickListener(OnClickListener);

		asyncHttpClient = new AsyncHttpClient();
		PersistentCookieStore PersistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		RequestParams requestParams = new RequestParams();
		requestParams.put("parkid", pk);
		asyncHttpClient.setCookieStore(PersistentCookieStore);
		asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_PARKINFO, requestParams, refreshjsonhHttpResponseHandler);

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
			RequestParams requestParams = new RequestParams();
			requestParams.put("parkid", pk);
			asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_BOOK, requestParams, booktexthHttpResponseHandler);
		}
	};

	private TextHttpResponseHandler booktexthHttpResponseHandler = new TextHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			if (responseString.equals(responseString)) {
				Toast.makeText(RentableParkInfoActivity.this, "预约成功", Toast.LENGTH_SHORT).show();
				makeorderbutton.setEnabled(false);
			}else {
				Toast.makeText(RentableParkInfoActivity.this, responseString, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		}
	};
	private JsonHttpResponseHandler refreshjsonhHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			// TODO Auto-generated method stub
			super.onSuccess(statusCode, headers, response);
			if (statusCode == 200) {
				try {
					String address = response.getString("address");
					String describe = response.getString("describe");
					String end_time = response.getJSONObject("shareinfo").getString("end_time");
					String start_time = response.getJSONObject("shareinfo").getString("start_time");
					String price = response.getJSONObject("shareinfo").getString("price");
					editparkaddress.setText(address);
					editparkdescription.setText(describe);
					editendtime.setText(end_time);
					editstarttime.setText(start_time);
					editofeescale.setText(price);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			// TODO Auto-generated method stub
			Toast.makeText(RentableParkInfoActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
			super.onFailure(statusCode, headers, responseString, throwable);

		}

	};
}
