package com.ych.tool;

import com.baidu.frontia.FrontiaApplication;
import com.ych.http.PersistentCookieStore;

import android.app.Application;

public class GlobalVariable extends Application {
	private PersistentCookieStore persistentCookieStore;

	@Override
	public void onCreate() {
		//super.onCreate();
		persistentCookieStore=new PersistentCookieStore(getApplicationContext());
		FrontiaApplication.initFrontiaApplication(getApplicationContext());
	}

	public PersistentCookieStore getPersistentCookieStore() {
		return persistentCookieStore;
	}

	public void setPersistentCookieStore(PersistentCookieStore persistentCookieStore) {
		this.persistentCookieStore = persistentCookieStore;
	}
	
}
