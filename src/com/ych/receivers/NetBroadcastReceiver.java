package com.ych.receivers;

import java.util.ArrayList;

import com.baidu.android.bbalbs.common.a.a;
import com.baidu.navisdk.CommonParams.NetConnectStatus;
import com.ych.tool.GlobalVariable;
import com.ych.tool.NetworkConnections;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetBroadcastReceiver extends BroadcastReceiver {
	public static ArrayList<NetListener> mListeners = new ArrayList<NetListener>();
	private static String NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(NET_CHANGE_ACTION)) {
			GlobalVariable.netWorkAvailable = NetworkConnections.isNetworkAvailable(context);
			if (mListeners.size() > 0)
				for (NetListener handler : mListeners) {
					handler.onNetAvailableChange();
				}
		}
	}

	public static abstract interface NetListener {
		public abstract void onNetAvailableChange();
	}
}
