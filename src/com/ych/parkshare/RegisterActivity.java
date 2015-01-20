package com.ych.parkshare;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private Button buttonRegister;
	private EditText nameEditText;
	private EditText pass1EditText;
	private EditText pass2EditText;
	private EditText phoneEditText;
	private EditText emailEditText;
	private boolean canregister=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("返回");
		actionBar.setIcon(new Drawable() {

			@Override
			public void setColorFilter(ColorFilter cf) {
			}

			@Override
			public void setAlpha(int alpha) {
			}

			@Override
			public int getOpacity() {
				return 0;
			}

			@Override
			public void draw(Canvas canvas) {
			}
		});
		actionBar.setDisplayHomeAsUpEnabled(true);
		nameEditText = (EditText) findViewById(R.id.edittextregistername);
		pass1EditText = (EditText) findViewById(R.id.edittextregisterpassone);
		pass2EditText = (EditText) findViewById(R.id.edittextregisterpasstwo);
		phoneEditText = (EditText) findViewById(R.id.edittextregisterphone);
		emailEditText = (EditText) findViewById(R.id.edittextregisteremail);
		buttonRegister = (Button) findViewById(R.id.buttonregister);
		buttonRegister.setOnClickListener(onClickListener);
		nameEditText.setOnFocusChangeListener(onFocusChangeListener);
		pass2EditText.setOnFocusChangeListener(onFocusChangeListener);
		emailEditText.setOnFocusChangeListener(onFocusChangeListener);
		phoneEditText.setOnFocusChangeListener(onFocusChangeListener);
	}

	private View.OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(canregister){
				AsyncHttpClient client=new AsyncHttpClient();
				RequestParams params=new RequestParams();
				params.put("name", nameEditText.getText().toString());
				client.post("", params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							int result=response.getInt("result");
							if(result==1){
								Intent intent =new Intent(RegisterActivity.this,MainActivity.class);
								startActivity(intent);
								RegisterActivity.this.finish();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				});
			}else {
				Toast.makeText(RegisterActivity.this,"信息不正确", 0).show();
			}
		}
	};
	private View.OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			switch (v.getId()) {
			case R.id.edittextregistername:
				if (!hasFocus) {
					// 检测用户名是否已经被注册，字符范围是否有特殊字符
				}
				break;
			case R.id.edittextregisterpasstwo:
				if (hasFocus) {
					//检测两次的密码是否一致
				}
				break;
			case R.id.edittextregisterphone:
				if (hasFocus) {
					//检测手机号是否为空，号码是否正确
				}
				break;
			case R.id.edittextregisteremail:
				if (hasFocus) {
					//检测email格式时候正确
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}
}
