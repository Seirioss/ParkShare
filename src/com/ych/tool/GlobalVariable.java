package com.ych.tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.navisdk.util.common.LogUtil;
import com.ych.http.PersistentCookieStore;
import com.ych.parkshare.CollapseActivity;

import android.R.anim;
import android.app.Application;
import android.content.Intent;
import android.os.Environment;

public class GlobalVariable extends Application {
	private PersistentCookieStore persistentCookieStore;

	@Override
	public void onCreate() {
		persistentCookieStore = new PersistentCookieStore(getApplicationContext());
		FrontiaApplication.initFrontiaApplication(getApplicationContext());
		//Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
	}

	public PersistentCookieStore getPersistentCookieStore() {
		return persistentCookieStore;
	}

	public void setPersistentCookieStore(PersistentCookieStore persistentCookieStore) {
		this.persistentCookieStore = persistentCookieStore;
	}

	// 处理每次程序奔溃
	private UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			SpUtils.put(getApplicationContext(), AppConstants.ERROR_APP, true);
			String path = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/errorlog.txt";
			if (!FileUtils.isFileExist(path)) {
				FileUtils.makeDirs(path);
			}

			File file = new File(path);
			try {
				ex.printStackTrace(new PrintStream(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};
}
