package com.ych.tool;

import com.ych.http.PersistentCookieStore;

import android.app.Application;

public class GlobalVariable extends Application {
	private PersistentCookieStore persistentCookieStore;

	@Override
	public void onCreate() {
		persistentCookieStore=new PersistentCookieStore(getApplicationContext());
		super.onCreate();
	}

	public PersistentCookieStore getPersistentCookieStore() {
		return persistentCookieStore;
	}

	public void setPersistentCookieStore(PersistentCookieStore persistentCookieStore) {
		this.persistentCookieStore = persistentCookieStore;
	}
	
}
