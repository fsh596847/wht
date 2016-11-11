package com.xiaowei.android.wht.uibase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.utis.HlpUtils;
/**
 * 基本界面的基类
 * @author Administrator
 *
 */
public class BaseActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((ApplicationTool)getApplication()).addActivity(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		commStatusReceiver = new CommStatusReceiver();
//		IntentFilter filter = new IntentFilter(DataNotify.ActionCommunictionException);
//		filter.addAction(DataNotify.ActionConnectionException);
//		filter.addAction(DataNotify.ActionSettingException);
//		registerReceiver(commStatusReceiver, filter);
	}
	@Override
	protected void onPause() {
//		unregisterReceiver(commStatusReceiver);
		super.onPause();
	};
	final int msgConnectFail = 1001;
	final int msgCommunication = 1002;
	final int msgSettingException = 1003;
	Handler netHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case msgConnectFail:
				showNetSettingDialog();
				break;
			case msgCommunication:
				String cs = (String)msg.obj;
//				HlpUtils.showToast(getApplicationContext(), cs==null?"通信异常":cs);
//				showNetError();
				break;
			case msgSettingException:
				String ss = (String)msg.obj;
				HlpUtils.showToast(getApplicationContext(), ss==null?"设置有误":ss);
				break;
			default:
				break;
			}
		};
	};

	protected void showNetError() {
		try{
//			stopCommuniction();
			//通信出错后，弹出提示框，点击后重新
			AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
			builder.setMessage("通信出错").setCancelable(false).
			setTitle("提示信息").setIcon(R.drawable.info).
				setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			}).show();
		}catch (Exception e) {
			
		}
	}
	private void showNetSettingDialog() {
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("需要检查Wifi连接。\n是否检查？").setCancelable(false).
			setTitle("连接失败").setIcon(R.drawable.info).
				setPositiveButton("是", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);
					startActivity(intent);
				}
			}).setNegativeButton("否", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					((ApplicationTool)getApplication()).exitApp();
					finish();
				}
			}).show();
		}catch (Exception e) {
			
		}
	}

}
