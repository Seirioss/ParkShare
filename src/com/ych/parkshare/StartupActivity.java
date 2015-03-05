package com.ych.parkshare;

import java.io.File;
import java.sql.SQLException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.utils.KJLoger;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Logger;
import com.litesuits.common.assist.Toastor;
import com.litesuits.common.data.DataKeeper;
import com.ych.dao.DatabaseHelper;
import com.ych.dao.Park;
import com.ych.http.AsyncHttpClient;
import com.ych.http.FileAsyncHttpResponseHandler;
import com.ych.http.JsonHttpResponseHandler;
import com.ych.http.PersistentCookieStore;
import com.ych.http.RequestParams;
import com.ych.tool.*;

import android.R.integer;
import android.R.menu;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StartupActivity extends Activity {

	private final static int TIMER = 1;
	private String version_newest;
	private String version_current;
	private String appdownurl;
	private AsyncHttpClient asyncHttpClient;
	private TextView tv_version;
	private DatabaseHelper helper=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_startup);
		asyncHttpClient = new AsyncHttpClient();
		tv_version = (TextView) findViewById(R.id.tv_startup_version);
		helper=OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
		PackageManager packageManager = getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		version_current = packageInfo.versionName;
		tv_version.setText("版本:" + version_current);
		boolean netstate = NetworkConnections.isNetworkAvailable(getApplicationContext());
		/*if (!netstate) {
			Toast.makeText(StartupActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
			new Thread(timerRunnable).start();
		} else {
			asyncHttpClient.post(AppConstants.BASE_URL + AppConstants.URL_CHECHAPPVERSION, versionjsonHttpResponseHandler);
		}*/
		if(!netstate){
			new Toastor(this).showSingletonToast("网络不可用");
		}
		new Thread(timerRunnable).start();
	}

	private Runnable timerRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (handler != null) {
				Message message = handler.obtainMessage();
				message.what = TIMER;
				message.sendToTarget();
			}
		}
	};
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TIMER) {
				boolean loginstate= (Boolean) SpUtils.get(getApplicationContext(), AppConstants.USER_LOGIN, false);
				if(loginstate){
					Intent intent = new Intent(StartupActivity.this, TabHostActivity.class);
					startActivity(intent);
					StartupActivity.this.finish();
				}else {
					Intent intent = new Intent(StartupActivity.this, LogInActivity.class);
					startActivity(intent);
					StartupActivity.this.finish();
				}
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler = null;
	}

	private JsonHttpResponseHandler versionjsonHttpResponseHandler = new JsonHttpResponseHandler("utf-8") {

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			// TODO Auto-generated method stub

			if (statusCode == 200) {
				try {
					version_newest = response.getString("version");
					appdownurl = response.getString("url");
					if (!version_newest.equals(version_current)) {
						AlertDialog.Builder builder = new Builder(StartupActivity.this);

						builder.setMessage("检测到有新版本,是否更新");
						builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								new Thread(timerRunnable).start();
							}
						});
						builder.setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								final String path = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/download/parkshare" + version_newest + ".apk";
								if (!FileUtils.isFileExist(path)) {
									FileUtils.makeDirs(path);
								}
								File file = new File(path);
								final ProgressDialog progressDialog = new ProgressDialog(StartupActivity.this);
								progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								progressDialog.setCancelable(false);
								asyncHttpClient.post(appdownurl, new FileAsyncHttpResponseHandler(file, false) {

									@Override
									public void onStart() {
										progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog, int which) {
												// TODO Auto-generated method
												// stub
												asyncHttpClient.getHttpClient().getConnectionManager().shutdown();
												new Thread(timerRunnable).start();
											}
										});
										progressDialog.show();
										super.onStart();
									}

									@Override
									public void onSuccess(int statusCode, Header[] headers, File file) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onProgress(int bytesWritten, int totalSize) {
										// TODO Auto-generated method stub
										progressDialog.setMax(totalSize);
										progressDialog.setProgress(bytesWritten);
										if (bytesWritten == totalSize) {
											File file = new File(path);
											Uri uri = Uri.fromFile(file);
											Intent installintent = new Intent(Intent.ACTION_VIEW);
											installintent.setDataAndType(uri, "application/vnd.android.package-archive");
											startActivity(installintent);
											StartupActivity.this.finish();
										}
										super.onProgress(bytesWritten, totalSize);
									}

								});
							}
						});
						AlertDialog alertDialog = builder.create();
						alertDialog.setCancelable(false);
						alertDialog.show();
					} else {
						new Thread(timerRunnable).start();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				new Thread(timerRunnable).start();
			}
			super.onSuccess(statusCode, headers, response);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			// TODO Auto-generated method stub
			new Thread(timerRunnable).start();
			super.onFailure(statusCode, headers, responseString, throwable);
		}

	};
}
