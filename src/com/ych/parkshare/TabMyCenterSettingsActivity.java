package com.ych.parkshare;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class TabMyCenterSettingsActivity extends Activity {

	private Switch updateSwitch;
	private Switch notifySwitch;
	private Switch shortcutSwitch;
	private Switch bleSwitch;
	private Switch passwordsSwitch;
	
	private BluetoothAdapter bluetoothAdapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycenter_setting);
		
		updateSwitch = (Switch)findViewById(R.id.switch_update);
		notifySwitch = (Switch)findViewById(R.id.switch_notify);
		shortcutSwitch = (Switch)findViewById(R.id.switch_shortcut);
		bleSwitch = (Switch)findViewById(R.id.switch_ble);
		passwordsSwitch = (Switch)findViewById(R.id.switch_password);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		updateSwitch.setOnCheckedChangeListener(new SwitchListener());
		notifySwitch.setOnCheckedChangeListener(new SwitchListener());
		shortcutSwitch.setOnCheckedChangeListener(new SwitchListener());
		bleSwitch.setOnCheckedChangeListener(new SwitchListener());
		passwordsSwitch.setOnCheckedChangeListener(new SwitchListener());
		
	}
	
	class SwitchListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			switch (buttonView.getId()) {
			case R.id.switch_ble:
				if (isChecked) {
					bluetoothAdapter.enable();
					Toast.makeText(TabMyCenterSettingsActivity.this, "蓝牙已打开", Toast.LENGTH_SHORT).show();
				}else {
					bluetoothAdapter.disable();
					Toast.makeText(TabMyCenterSettingsActivity.this, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
		}
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	

}
