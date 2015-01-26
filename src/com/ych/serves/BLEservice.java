package com.ych.serves;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import javax.security.auth.PrivateCredentialPermission;

import android.R.integer;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class BLEservice extends Service {

	public static final int MSG_REGISTER_CLIENT = 0;
	public static final int MSG_UNREGISTER_CLIENT = 1;
	public static final int MSG_SEARCH_BLE = 2;
	public static final int MSG_SEARCH_BLE_RESULT = 3;
	public static final int MSG_CONNECT_BLE = 4;
	public static final int MSG_CONNECT_BLE_RESULT = 5;
	public static final int MSG_BREAKCONNECT_BLE = 50;
	public static final int MSG_WRITE_BLE = 6;
	public static final int MSG_WRITE_BLE_RESULT = 7;
	public static final int MSG_OPENT_BLE = 8;// 0
	public static final int MSG_CLOSE_BLE = 9;// 1

	public final static String ADDRESS = "88:33:14:DD:88:92";
	public final static String ADDRESS_ = "88:33:14:DD:8A:1E";
	
	
	private BluetoothManager bluetoothManager;
	private BluetoothAdapter bluetoothAdapter;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CONNECT_BLE:

				break;
			case MSG_OPENT_BLE:
				if (checkBLEstate()) {
					String mac = (String) msg.obj;
					
					if (BluetoothAdapter.checkBluetoothAddress(mac)) {
						System.out.println("MSG_OPENT_BLE"+mac);
						BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(mac);
						bluetoothDevice.connectGatt(getApplicationContext(), false, new SwitchPark(SwitchPark.OPEN));
					}
				} else {
					Toast.makeText(getApplicationContext(), "蓝牙不可用", Toast.LENGTH_SHORT).show();
				}
				break;
			case MSG_CLOSE_BLE:
				if (checkBLEstate()) {
					String mac = (String) msg.obj;
					
					if (BluetoothAdapter.checkBluetoothAddress(mac)) {
						System.out.println("MSG_CLOSE_BLE"+mac);
						BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(mac);
						bluetoothDevice.connectGatt(getApplicationContext(), false,new SwitchPark(SwitchPark.CLOSE));
					}
				} else {
					Toast.makeText(getApplicationContext(), "蓝牙不可用", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	private Messenger messenger = new Messenger(handler);

	@Override
	public void onCreate() {
		super.onCreate();
		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return messenger.getBinder();
	}

	private boolean checkBLEstate() {
		boolean state = false;
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) && bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
			state = true;
		}
		return state;
	}
}
