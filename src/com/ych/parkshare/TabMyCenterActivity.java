package com.ych.parkshare;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ych.http.AsyncHttpClient;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
import com.ych.http.SyncHttpClient;
import com.ych.http.TextHttpResponseHandler;
import com.ych.tool.AppConstants;
import com.ych.tool.GlobalVariable;
import com.ych.tool.SpUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TabMyCenterActivity extends Activity {

	private TextView usernametext;
	private TextView parkmanagmenttext;
	private TextView logouttext;
	private TextView settingstext;
	private AsyncHttpClient client;
	private String username;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycenter);

		
		usernametext=(TextView)findViewById(R.id.usernametext);
		parkmanagmenttext=(TextView)findViewById(R.id.parmanagment);
		logouttext=(TextView)findViewById(R.id.logout);
		settingstext=(TextView)findViewById(R.id.settings);

		parkmanagmenttext.setOnClickListener(onClickListener);
		logouttext.setOnClickListener(onClickListener);
		settingstext.setOnClickListener(onClickListener);
		getuserinfo();
		username = usernametext.toString();

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case (R.id.parmanagment):
				Intent intent_parmanagent = new Intent(TabMyCenterActivity.this, MyParkAcitvity.class);
			    startActivity(intent_parmanagent);
				break;
			case (R.id.logout):
				AlertDialog.Builder builder = new Builder(TabMyCenterActivity.this);
			    builder.setTitle("确认");
			    builder.setMessage("确认注销并推出？");
			    builder.setNegativeButton("取消", null);
			    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						logout();
					}
				});
                builder.create().show();	
				break;
			case(R.id.settings):
				Intent intent_settings = new Intent(TabMyCenterActivity.this, TabMyCenterSettingsActivity.class);
			    startActivity(intent_settings);
			    System.out.println("helloWorld!");
			    break;
			default:
				break;
			}
		}

	};

	private void logout() {
		SpUtils.put(getApplicationContext(), AppConstants.USER_LOGIN, false);
		client = new AsyncHttpClient();
		PersistentCookieStore persistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		client.setCookieStore(persistentCookieStore);
		client.post("http://121.40.61.76:8080/parkManagementSystem/logout/", new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				// TODO Auto-generated method stub
				Log.i("logout", responseString);
				SpUtils.put(getApplicationContext(), AppConstants.USER_REMEMBER, false);
				System.exit(0);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void getuserinfo() {
		client = new AsyncHttpClient();
		PersistentCookieStore persistentCookieStore = ((GlobalVariable) getApplication()).getPersistentCookieStore();
		client.setCookieStore(persistentCookieStore);
		client.post("http://121.40.61.76:8080/parkManagementSystem/user/park/", new JsonHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("parks");
					JSONObject jsonObject1 = jsonArray.getJSONObject(0).getJSONObject("fields");
					usernametext.setText(jsonObject1.getString("username").toString());

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}
}
