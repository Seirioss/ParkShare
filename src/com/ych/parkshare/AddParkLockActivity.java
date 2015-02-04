package com.ych.parkshare;

import org.apache.http.Header;
import org.json.JSONException;
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
import android.util.Log;
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
	private EditText editTextremake;

	private AsyncHttpClient asyncHttpClient;
	
	//baidu LBS CLOUD
		public final static String LBS_BASIC_URL="http://api.map.baidu.com/geodata/v3/poi/";
		public final static String LBS_POI_CREATE="create";
		public final static String LBS_POI_DELETE="delete";
		public final static String LBS_POI_LIST="list";
		public final static String LBS_POI_DETAIL="detail";
		public final static String LBS_POI_UPDATE="update";
		public final static String BD_STORE_AK="6M4od7BaqsQFsjbOgOlFkgX8";
		public String geotableid = "93340";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_park_lock);

		ActionBar actionBar = getActionBar();
		actionBar.setTitle("添加车位锁");
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		editTextaddress = (EditText) findViewById(R.id.et_addpark_address);
		editTextserialnumber = (EditText) findViewById(R.id.et_addpark_serialr);
		editTextdescribe = (EditText) findViewById(R.id.et_addpark_describe);
		editTextremake = (EditText) findViewById(R.id.et_addpark_remark);
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
				String remake = editTextremake.getText().toString();
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
				requestParams.put("remake", remake);
				asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_ADDPARK, requestParams, addparkTextHttpResponseHandler);
				
				//LBS add poi
				RequestParams lbsRequestParams = new RequestParams();
				lbsRequestParams.put("geotable_id", geotableid);                  
				lbsRequestParams.put("ak", BD_STORE_AK);
				lbsRequestParams.put("title", "testpoi");
				lbsRequestParams.put("address", "testpoiaddress");
				lbsRequestParams.put("longitude", 121.512856);
				lbsRequestParams.put("latitude", 31.288289);
				lbsRequestParams.put("park_num", 3);
				lbsRequestParams.put("coord_type", "3");
				asyncHttpClient.post(LBS_BASIC_URL+LBS_POI_CREATE,lbsRequestParams, lbsaddpoiJsonHttpResponseHandler);
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
	
private JsonHttpResponseHandler lbsaddpoiJsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8"){
		
		@Override
		public void onSuccess(int statuscode , Header[] headers, JSONObject response){
			super.onSuccess(statuscode, headers, response);
			if(statuscode == 200){
				try {
					if(response.getInt("status") == 0){
						String parkpoiid = response.getString("id");
						String statusdescribe = response.getString("message");
						Log.i("parkpoiid",parkpoiid);
//						Toast.makeText(AddParkLockActivity.this, parkpoiid, Toast.LENGTH_SHORT).show();
					}else{
						Log.i("status", response.getString("status"));
						Log.i("lbscreaterror", response.getString("message"));
//						Toast.makeText(AddParkLockActivity.this, statuscode, Toast.LENGTH_SHORT).show();						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				
			}
			
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
//			Toast.makeText(AddParkLockActivity.this, "添加失败 , 网络有问题" + statusCode, Toast.LENGTH_SHORT).show();
		}
		
	};
}
