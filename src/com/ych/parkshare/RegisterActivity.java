package com.ych.parkshare;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import com.ych.http.AsyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.RequestParams;
import com.ych.http.TextHttpResponseHandler;
import com.ych.tool.AppConstants;
import com.ych.tool.SpUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
	}

	private View.OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final String name = nameEditText.getText().toString();
			final String pass1 = pass1EditText.getText().toString();
			String pass2 = pass2EditText.getText().toString();
			String phone = phoneEditText.getText().toString();
			String email = emailEditText.getText().toString();
			if (name.equals("")) {
				Toast.makeText(RegisterActivity.this, "用户名空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (pass1.equals("") || pass2.equals("")) {
				Toast.makeText(RegisterActivity.this, "密码空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!pass1.equals(pass2)) {
				Toast.makeText(RegisterActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
				return;
			}

			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();
			params.put("username", name);
			params.put("password", pass1);
			params.put("email", phone);
			params.put("phone", email);

			client.post("http://121.40.61.76:8080/parkManagementSystem/register/", params, new TextHttpResponseHandler("utf-8") {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					if (statusCode == 200) {
						if (responseString.equals("0")) {
							AlertDialog.Builder builder = new Builder(RegisterActivity.this);
							builder.setTitle("注册成功");
							builder.setMessage("是否登陆");
							builder.setNegativeButton("取消", null);
							builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									SpUtils.put(getApplicationContext(), AppConstants.USER_LOGIN, true);
									SpUtils.put(getApplicationContext(), AppConstants.USER_REMEMBER, true);
									SpUtils.put(getApplicationContext(), AppConstants.USER_NAME, name);
									SpUtils.put(getApplicationContext(), AppConstants.USER_PASSWORD, pass1);
									Intent intent = new Intent(RegisterActivity.this, TabHostActivity.class);
									startActivity(intent);
									RegisterActivity.this.finish();
									LogInActivity.getInstance().finish();
								}
							});
							builder.create().show();
						}
						if (responseString.equals("1")) {
							Toast.makeText(RegisterActivity.this, "用户名已近存在", Toast.LENGTH_SHORT).show();
						}
					}else {
						Toast.makeText(RegisterActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Toast.makeText(RegisterActivity.this, "网络故障", Toast.LENGTH_SHORT).show();
				}
			});
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}
}