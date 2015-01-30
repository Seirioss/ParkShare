package com.ych.parkshare;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.ych.http.AsyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
import com.ych.http.SyncHttpClient;
import com.ych.http.TextHttpResponseHandler;
import com.ych.tool.AppConstants;
import com.ych.tool.BaiduUtils;
import com.ych.tool.GlobalVariable;
import com.ych.tool.SpUtils;
import com.ych.views.LinkClickTextView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends Activity {

	private LinkClickTextView linkClickTextView;
	private Button buttonSignup;
	private Button buttonLogin;
	private EditText editTextname;
	private EditText editTextpassword;
	private View backgroud;
	private static LogInActivity logInActivity;
	private AsyncHttpClient asyncHttpClient;
	private String username;
	private String password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_log_in);
		
		linkClickTextView = (LinkClickTextView) findViewById(R.id.forgetpassword);
		buttonLogin = (Button) findViewById(R.id.buttonlogin);
		buttonSignup = (Button) findViewById(R.id.buttonsignup);
		editTextname = (EditText) findViewById(R.id.loginEdit);
		editTextpassword = (EditText) findViewById(R.id.login_password);
		linkClickTextView.setOnClickListener(onClickListener);
		buttonSignup.setOnClickListener(onClickListener);
		buttonLogin.setOnClickListener(onClickListener);
		if((Boolean) SpUtils.get(getApplicationContext(), AppConstants.USER_REMEMBER, false)){
			String name=(String) SpUtils.get(getApplicationContext(), AppConstants.USER_NAME, "");
			String password=(String) SpUtils.get(getApplicationContext(), AppConstants.USER_PASSWORD, "");
			editTextname.setText(name);
			editTextpassword.setText(password);
		};
		logInActivity=this;
		asyncHttpClient=new AsyncHttpClient();
	}
	public static LogInActivity getInstance(){
		return (LogInActivity) logInActivity;
	}
	private View.OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonlogin:
				username =editTextname.getText().toString();
				password=editTextpassword.getText().toString();
				RequestParams params = new RequestParams();
				params.add("username", username);
				params.add("password", password);
				PersistentCookieStore persistentCookieStore=((GlobalVariable)getApplication()).getPersistentCookieStore();
				persistentCookieStore.clear();
				asyncHttpClient.setCookieStore(persistentCookieStore);
				asyncHttpClient.post(AppConstants.BASE_URL+AppConstants.URL_LOGIN,params, loginTextHttpResponseHandler);

				break;
			case R.id.buttonsignup:
				Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case R.id.forgetpassword:

				break;

			default:
				break;
			}
		}
	};

	
	private TextHttpResponseHandler loginTextHttpResponseHandler = new TextHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			// TODO Auto-generated method stub
			if (statusCode == 200) {
				if (responseString.endsWith("0")) {
					SpUtils.put(getApplicationContext(), AppConstants.USER_LOGIN, true);
					SpUtils.put(getApplicationContext(), AppConstants.USER_REMEMBER, true);
					SpUtils.put(getApplicationContext(), AppConstants.USER_NAME, username);
					SpUtils.put(getApplicationContext(), AppConstants.USER_PASSWORD, password);
					PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,BaiduUtils.getMetaValue(LogInActivity.this, "api_key"));
					Intent intent = new Intent(LogInActivity.this, TabHostActivity.class);
					startActivity(intent);
					LogInActivity.this.finish();
				} else {
					SpUtils.put(getApplicationContext(), AppConstants.USER_LOGIN, false);
					SpUtils.put(getApplicationContext(), AppConstants.USER_REMEMBER, false);
					SpUtils.put(getApplicationContext(), AppConstants.USER_NAME, "");
					SpUtils.put(getApplicationContext(), AppConstants.USER_PASSWORD, "");
					Toast.makeText(LogInActivity.this, responseString, Toast.LENGTH_SHORT).show();
					editTextname.getText().clear();
					editTextpassword.getText().clear();
				}
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			// TODO Auto-generated method stub
			Toast.makeText(LogInActivity.this, "错误代码：" + statusCode + "  " + responseString, Toast.LENGTH_SHORT).show();
			editTextname.getText().clear();
			editTextpassword.getText().clear();
		}
	};
}
