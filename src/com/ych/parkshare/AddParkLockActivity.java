package com.ych.parkshare;

import org.apache.http.Header;
import org.json.JSONObject;

import com.ych.http.AsyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
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
	private EditText editTextopenkey;
	private EditText editTextclosekey;
	private EditText editTextmacaddress;
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

		editTextaddress=(EditText)findViewById(R.id.editopenkey);
		editTextopenkey=(EditText)findViewById(R.id.editopenkey);
		editTextclosekey=(EditText)findViewById(R.id.editclosekey);
		editTextmacaddress=(EditText)findViewById(R.id.editmac);
		editTextserialnumber=(EditText)findViewById(R.id.editlocknumber);
		editTextdescribe=(EditText)findViewById(R.id.editdescribe);
		editTextaddress=(EditText)findViewById(R.id.editparkaddress);
		addlockbutton = (Button) findViewById(R.id.addlockbutton);
		
		asyncHttpClient = new AsyncHttpClient();
		PersistentCookieStore PersistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		
		asyncHttpClient.setCookieStore(PersistentCookieStore);
		
		addlockbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String openkey=editTextopenkey.getText().toString();
				String closekey=editTextopenkey.getText().toString();
				String macaddress=editTextmacaddress.getText().toString();
				String serialnumber=editTextserialnumber.getText().toString();
				String describe=editTextdescribe.getText().toString();
				String address=editTextaddress.getText().toString();
				String comment="haha";
				RequestParams requestParams=new RequestParams();
				requestParams.put("openkey",openkey );
				requestParams.put("closekey",closekey );
				requestParams.put("macaddress",macaddress );
				requestParams.put("serialnumber",serialnumber );
				requestParams.put("address", address);
				requestParams.put("describe", describe);
				requestParams.put("comment", comment);
				asyncHttpClient.post(AppConstants.BASE_URL+AppConstants.URL_ADDPARK, requestParams,bookJsonHttpResponseHandler);
			}
		});
	}
	private JsonHttpResponseHandler bookJsonHttpResponseHandler=new JsonHttpResponseHandler("utf-8"){

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			// TODO Auto-generated method stub
			if(statusCode==200){
				Toast.makeText(AddParkLockActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
			}
			super.onSuccess(statusCode, headers, response);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			// TODO Auto-generated method stub
			super.onFailure(statusCode, headers, responseString, throwable);
			Toast.makeText(AddParkLockActivity.this, "添加失败 , 网络有问题"+statusCode,Toast.LENGTH_SHORT).show();
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
