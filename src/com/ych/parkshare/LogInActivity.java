package com.ych.parkshare;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ych.http.AsyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.RequestParams;
import com.ych.http.TextHttpResponseHandler;
import com.ych.tool.AppConstants;
import com.ych.tool.SpUtils;
import com.ych.views.LinkClickTextView;

import android.app.Activity;
import android.content.Intent;
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
	private static LogInActivity logInActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_log_in);
		linkClickTextView = (LinkClickTextView) findViewById(R.id.forgetpassword);
		buttonLogin = (Button) findViewById(R.id.buttonlogin);
		buttonSignup = (Button) findViewById(R.id.buttonsignup);
		editTextname = (EditText) findViewById(R.id.editTextname);
		editTextpassword = (EditText) findViewById(R.id.editTextpassword);
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
	}
	public static LogInActivity getInstance(){
		return (LogInActivity) logInActivity;
	}
	private View.OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonlogin:
				login(editTextname.getText().toString(), editTextpassword.getText().toString());
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

	private void login(final String name, final String password) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("username", name);
		params.add("password", password);
		//测试账户:name:test   password:test
		client.post("http://121.40.61.76:8080/parkManagementSystem/login/", params, new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if(statusCode==200){
					if(responseString.endsWith("0")||responseString.endsWith("1")){
						SpUtils.put(getApplicationContext(), AppConstants.USER_LOGIN,true);
						SpUtils.put(getApplicationContext(), AppConstants.USER_REMEMBER,true);
						SpUtils.put(getApplicationContext(), AppConstants.USER_NAME, name);
						SpUtils.put(getApplicationContext(), AppConstants.USER_PASSWORD,password);
						Intent intent =new Intent(LogInActivity.this,TabHostActivity.class);
						startActivity(intent);
						LogInActivity.this.finish();
					}else {
						SpUtils.put(getApplicationContext(), AppConstants.USER_LOGIN,false);
						SpUtils.put(getApplicationContext(), AppConstants.USER_REMEMBER,false);
						SpUtils.put(getApplicationContext(), AppConstants.USER_NAME, "");
						SpUtils.put(getApplicationContext(), AppConstants.USER_PASSWORD,"");
						Toast.makeText(LogInActivity.this, "账户名或密码错误", Toast.LENGTH_SHORT).show();
						editTextname.getText().clear();
						editTextpassword.getText().clear();
					}
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Toast.makeText(LogInActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
				Intent intent =new Intent(LogInActivity.this,TabHostActivity.class);
				startActivity(intent);
				LogInActivity.this.finish();
			}
			
		});
	}
}
