package com.ych.parkshare;

import com.ych.tool.FileUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CollapseActivity extends Activity {
	private TextView tv_error;
	private Button bt_submit;
	private Button bt_quit;
	private final static int HANDLER_WHAT_READERRORLOG=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collapse);
		setTitle("错误信息");
		tv_error = (TextView) findViewById(R.id.tv_collapse_error);
		bt_submit = (Button) findViewById(R.id.bt_collapse_submit);
		bt_quit = (Button) findViewById(R.id.bt_collapse_quit);
		bt_quit.setOnClickListener(onClickListener);
		bt_submit.setOnClickListener(onClickListener);
		new Thread(runnablereaderrorlog).start();
	}
	private Runnable runnablereaderrorlog=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String info=new String();
			String path=Environment.getExternalStorageDirectory()+"/"+getPackageName()+"/errorlog.txt";
			info= FileUtils.readFile(path, "utf-8").toString();
			Message message=uiHandler.obtainMessage();
			message.what=HANDLER_WHAT_READERRORLOG;
			message.obj=info;
			message.sendToTarget();
		}
	};
	Handler uiHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int what=msg.what;
			if(what==HANDLER_WHAT_READERRORLOG){
				tv_error.setText((String)msg.obj);
			}
			super.handleMessage(msg);
		}
		
	};
	private View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.bt_collapse_submit:

				break;
			case R.id.bt_collapse_quit:
				finish();
				break;
			default:
				break;
			}
		}
	};
}
