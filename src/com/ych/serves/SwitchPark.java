package com.ych.serves;

import java.util.UUID;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

public class SwitchPark extends BluetoothGattCallback {
	private String sw;
	public static final String OPEN = "O";
	public static final String CLOSE = "C";
	private static final UUID RX_CHAR_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
	private static final UUID TX_CHAR_UUID = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
	private static final UUID RX_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");

	public SwitchPark(String sw) {
		super();
		this.sw = sw;
	}

	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
		super.onConnectionStateChange(gatt, status, newState);
		if (status == BluetoothGatt.GATT_SUCCESS) {
			gatt.discoverServices();
			System.out.println("onConnectionStateChange" + sw);
		} else {
			gatt.disconnect();
			gatt.close();
		}
	}

	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		super.onServicesDiscovered(gatt, status);
		if (status == BluetoothGatt.GATT_SUCCESS) {
			BluetoothGattService service = gatt.getService(RX_SERVICE_UUID);
			BluetoothGattCharacteristic RxChar = service.getCharacteristic(RX_CHAR_UUID);
			RxChar.setValue(sw.getBytes());
			gatt.writeCharacteristic(RxChar);
		} else {
			gatt.disconnect();
			gatt.close();
		}
	}

	@Override
	public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		super.onCharacteristicWrite(gatt, characteristic, status);
		System.out.println("onCharacteristicWrite" + sw);
		gatt.disconnect();
		gatt.close();
	}
}
