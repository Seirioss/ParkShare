package com.ych.parkshare;

import org.apache.http.Header;
import org.json.JSONObject;

import com.ych.http.AsyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
import com.ych.http.TextHttpResponseHandler;
import com.ych.tool.AppConstants;
import com.ych.tool.GlobalVariable;

import android.app.ActionBar;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddParkLockActivity extends Activity {

	private Button addlockbutton;

	private EditText editTextserialnumber;
	private EditText editTextdescribe;
	private EditText editTextaddress;

	private AsyncHttpClient asyncHttpClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_park_lock);

		ActionBar actionBar = getActionBar();
		actionBar.setTitle("添加车位锁");
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		editTextaddress = (EditText) findViewById(R.id.editparkaddress);
		editTextserialnumber = (EditText) findViewById(R.id.editlocknumber);
		editTextdescribe = (EditText) findViewById(R.id.editdescribe);
		editTextaddress = (EditText) findViewById(R.id.editparkaddress);
		addlockbutton = (Button) findViewById(R.id.addlockbutton);

		asyncHttpClient = new AsyncHttpClient();
		PersistentCookieStore PersistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();

		asyncHttpClient.setCookieStore(PersistentCookieStore);

		addlockbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String latitude = "0";
				String longitude = "0";
				String macaddress ="0";
				String serialnumber = editTextserialnumber.getText().toString();
				String describe = editTextdescribe.getText().toString();
				String address = editTextaddress.getText().toString();
				String comment = "haha";
				RequestParams requestParams = new RequestParams();
				requestParams.put("longitude", longitude);
				requestParams.put("latitude", latitude);
				requestParams.put("macaddress", macaddress);
				requestParams.put("serialnumber", serialnumber);
				requestParams.put("address", address);
				requestParams.put("describe", describe);
				requestParams.put("comment", comment);
				requestParams.put("longitude", latitude);
				requestParams.put("latitude", longitude);
				asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_ADDPARK, requestParams, addparkTextHttpResponseHandler);
			}
		});
	}

	private TextHttpResponseHandler addparkTextHttpResponseHandler = new TextHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			if (statusCode == 200) {
				if (responseString.equals("0")) {
					Toast.makeText(AddParkLockActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(AddParkLockActivity.this, responseString.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			Toast.makeText(AddParkLockActivity.this, "添加失败 , 网络有问题" + statusCode, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
