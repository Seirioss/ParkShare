package com.ych.receivers;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.ych.http.AsyncHttpClient;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
import com.ych.http.TextHttpResponseHandler;
import com.ych.parkshare.ParkRentActivity;
import com.ych.parkshare.StartupActivity;
import com.ych.tool.AppConstants;
import com.ych.tool.GlobalVariable;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class BaiduPushMessageReceiver extends FrontiaPushMessageReceiver {

	@Override
	public void onBind(Context arg0, int arg1, String arg2, String arg3, String arg4, String arg5) {
		// TODO Auto-generated method stub
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("pushuserid", arg3);
		params.add("pushchannelid", arg4);
		PersistentCookieStore persistentCookieStore = ((GlobalVariable) arg0.getApplicationContext()).getPersistentCookieStore();
		asyncHttpClient.setCookieStore(persistentCookieStore);
		asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_UPDATEPUSHINFO, params, updatepushinfoHttpResponseHandler);
	}

	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(Context arg0, String arg1, String arg2) {
		

	}

	@Override
	public void onNotificationClicked(Context arg0, String arg1, String arg2, String arg3) {
		try {
			if (!TextUtils.isEmpty(arg3)) {
				JSONObject jsonObject = new JSONObject(arg3);
				if (!jsonObject.isNull("parkid")) {
					String parkid = jsonObject.getString("parkid");
					Intent intent = new Intent(arg0, ParkRentActivity.class);
					intent.putExtra("pk", parkid);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(AppConstants.ACTION_BaiduPushMessageReceiver);
					arg0.getApplicationContext().startActivity(intent);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	private TextHttpResponseHandler updatepushinfoHttpResponseHandler = new TextHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			// TODO Auto-generated method stub
			System.out.println(responseString);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			// TODO Auto-generated method stub

		}
	};
}
