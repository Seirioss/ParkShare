package com.ych.tool;

import android.R.string;
import android.content.Context;
import android.net.NetworkInfo.State;

public class AppConstants {
	public final static String USER_NAME = "username";
	public final static String USER_PASSWORD = "userpassword";
	public final static String USER_LOGIN = "login";
	public final static String USER_REMEMBER = "passwordremember";
	public final static String RUN_TIME = "runtime";
	public final static String BASE_URL = "http://121.40.61.76:8080/parkManagementSystem/";
	public final static String URL_PARKINFO = "park/";
	public final static String URL_USERPARKS = "user/park/";
	public final static String URL_USERPARKSDETAIL = "user/park/detail/";
	public final static String URL_BOOKCANCEL = "user/cancelborrow/";
	public final static String URL_BOOK = "user/borrow/";
	public final static String URL_SHARECANCEL = "user/cancelshare/";
	public final static String URL_SHARE = "user/share/";
	public final static String URL_ADDPARK = "park/add/";
	public final static String URL_LOGIN = "login/";
	public final static String URL_UPDATEPUSHINFO = "user/addpushinfo/";
	public final static String URL_CHECHAPPVERSION = "update/";
	public final static String ACTION_BaiduPushMessageReceiver = "com.ych.receivers.BaiduPushMessageReceiver";
	public final static String ACTION_TabHomeActivity = "com.ych.parkshare.TabHomeActivity";
	public final static String ERROR_APP = "apperror";
}
