package com.ych.parkshare;

import java.net.URI;

import org.apache.http.Header;

import com.ych.http.AsyncHttpClient;
import com.ych.http.TextHttpResponseHandler;
import com.ych.image.SmartImage;
import com.ych.image.SmartImageView;
import com.ych.tool.AppConstants;
import com.ych.tool.SpUtils;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewDebug;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		int runtime = (Integer) SpUtils.get(getApplicationContext(), AppConstants.RUN_TIME, 0);
		if (runtime == 0) {
			SpUtils.put(getApplicationContext(), AppConstants.RUN_TIME, runtime + 1);
			Intent intent=new Intent(MainActivity.this,GuideActivity.class);
			startActivity(intent);
		} else {
			SpUtils.put(getApplicationContext(), AppConstants.RUN_TIME, runtime + 1);
			Intent intent=new Intent(MainActivity.this,StartupActivity.class);
			startActivity(intent);
		}
		this.finish();
	}
}
