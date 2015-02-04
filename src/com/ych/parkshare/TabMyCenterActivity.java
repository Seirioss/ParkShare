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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TabMyCenterActivity extends Activity{
	
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
		username=usernametext.toString();
		
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case(R.id.parmanagment):
				break;
			case(R.id.logout):
				logout();
				break;
			case(R.id.settings):
				Intent intent = new Intent(TabMyCenterActivity.this, TabMyCenterSettingsActivity.class);
			    startActivity(intent);
			    System.out.println("helloWorld!");
			    break;
			default:
				break;				
			}
		}
		
	};
	
	private void logout(){
		client=new AsyncHttpClient();
		PersistentCookieStore persistentCookieStore=((GlobalVariable)getApplication()).getPersistentCookieStore();
		client.setCookieStore(persistentCookieStore);
		client.post("http://121.40.61.76:8080/parkManagementSystem/logout/", new TextHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String responseString) {
				// TODO Auto-generated method stub
				Log.i("logout", responseString);
				SpUtils.put(getApplicationContext(), AppConstants.USER_REMEMBER, false);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
    private void getuserinfo(){
		client = new AsyncHttpClient();
		PersistentCookieStore persistentCookieStore=((GlobalVariable)getApplication()).getPersistentCookieStore();
		client.setCookieStore(persistentCookieStore);
		client.post("http://121.40.61.76:8080/parkManagementSystem/user/park/", new JsonHttpResponseHandler("utf-8"){
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response){
				super.onSuccess(statusCode, headers, response);
				try{
					JSONArray jsonArray=response.getJSONArray("parks");
					JSONObject jsonObject1 = jsonArray.getJSONObject(0).getJSONObject("fields");
					usernametext.setText(jsonObject1.getString("username").toString());
					
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
}