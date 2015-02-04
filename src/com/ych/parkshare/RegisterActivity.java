package com.ych.parkshare;

import org.apache.http.Header;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ych.http.AsyncHttpClient;
import com.ych.http.RequestParams;
import com.ych.http.TextHttpResponseHandler;
import com.ych.tool.AppConstants;
import com.ych.tool.SpUtils;

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
		actionBar.setTitle("注册");
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.title_register));
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// actionBar.setDisplayShowCustomEnabled(true);
		// ActionBar.LayoutParams lp = new
		// ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.MATCH_PARENT,Gravity.LEFT);
		// actionBar.setCustomView(getLayoutInflater().inflate(R.layout.title_register,
		// null),lp);

		nameEditText = (EditText) findViewById(R.id.register_userEdit);
		pass1EditText = (EditText) findViewById(R.id.passwordEdit);
		pass2EditText = (EditText) findViewById(R.id.passconfirmEdit);
		phoneEditText = (EditText) findViewById(R.id.mailEdit);
		emailEditText = (EditText) findViewById(R.id.phoneEdit);
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
					} else {
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
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(this, LogInActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
