package com.ych.parkshare;

import java.net.URI;

import org.apache.http.Header;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.ych.http.AsyncHttpClient;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
import com.ych.http.TextHttpResponseHandler;
import com.ych.image.SmartImage;
import com.ych.image.SmartImageView;
import com.ych.tool.AppConstants;
import com.ych.tool.BaiduUtils;
import com.ych.tool.GlobalVariable;
import com.ych.tool.SpUtils;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewDebug;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private boolean mIsEngineInitSuccess = false; // baidumap

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,BaiduUtils.getMetaValue(MainActivity.this, "api_key"));
	
		int runtime = (Integer) SpUtils.get(getApplicationContext(), AppConstants.RUN_TIME, 0);
		if (runtime == 0) {
			SpUtils.put(getApplicationContext(), AppConstants.RUN_TIME, runtime + 1);
			Intent intent = new Intent(MainActivity.this, GuideActivity.class);
			startActivity(intent);
		} else {
			SpUtils.put(getApplicationContext(), AppConstants.RUN_TIME, runtime + 1);
			Intent intent = new Intent(MainActivity.this, StartupActivity.class);
			startActivity(intent);
		}
		this.finish();

		// 初始化导航引擎
		BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(), mNaviEngineInitListener, new LBSAuthManagerListener() {

			@Override
			public void onAuthResult(int status, String msg) {
				if (0 != status) {
					Toast.makeText(MainActivity.this, "key校验失败, " + msg, Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	// baidumap
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
		public void engineInitSuccess() {
			// 导航初始化是异步的，需要一小段时间，以这个标志来识别引擎是否初始化成功，为true时候才能发起导航
			mIsEngineInitSuccess = true;
		}

		public void engineInitStart() {
		}

		public void engineInitFail() {
		}
	};

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}
}
