package com.xiaowei.android.wht.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.RegisterInfo;
import com.xiaowei.android.wht.beans.User;
import com.xiaowei.android.wht.beans.Versions;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.ui.patient.PatientMainActivity;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;

public class ChooseWindowActivity extends Activity {

	Versions versions;
	
	@SuppressLint("HandlerLeak")
	Handler h = new Handler(){
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			showLogin();
			break;
		}
		super.handleMessage(msg);
	}};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.window_login_auto);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		versionName = Utils.getVersionName(this);
		if(getIntent().getBooleanExtra("comeGuide", false)){
			h.sendEmptyMessage(0);
		}
		else if(versionName.isEmpty()){
			intersection();
		}
		else{
			//reload("正在努力加载……");
			//检查版本更新
			new Thread() {  
				public void run() {  
					//查询版本信息
					String mobile = new SpData(ChooseWindowActivity.this).getStringValue(SpData.keyPhoneUser, null);
					versions = queryVersions(mobile);
					//closeLoadingDialog();
					//处理结果
					if(versions !=null && isUpdataNewVersion(versionName, versions.getVersionname()) && versions.getDownUrl() != null){
						//有新版本,强制更新
						updateVersions(versions.getDownUrl());
					} else {
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() { 
								//路口
								intersection();
							}  
						});
					}
				}
			}.start(); 
		}
	}
	
	private boolean isUpdataNewVersion(String versionName, String newVersionName){
		try {
			if(versionName == null || newVersionName == null){
				return false;
			}
			String[] version = versionName.split("[.]");
			String[] newVersion = newVersionName.split("[.]");
			if(version.length != newVersion.length){
				return true;
			}
			for (int i = 0; i < newVersion.length; i++) {
				if(Integer.parseInt(version[i]) < Integer.parseInt(newVersion[i])){
					return true;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void intersection() {
		//Handler x = new Handler();
		SpData sp = new SpData(getApplicationContext());
		String clientType = sp.getStringValue(SpData.KeyClientType, null);
		String mobile = sp.getStringValue(SpData.keyPhoneUser, null);
		if (clientType != null && mobile != null){
			//记忆医生登录过
			if(clientType.equals(""+RegisterActivity.clientTypeDoctor)){
				//显示自动登录
				setContentView(R.layout.window_login_auto);
				TextView tvVer = (TextView) findViewById(R.id.tv_login_auto_ver);
				tvVer.setText(versionName+"版本");
				//停留1秒后进入医生端主页
				//x.postDelayed(new runnableInDoctor(), 3000);
				new Thread(new runnableInDoctor()).start();
			}
			//记忆患者登录过
			else if(clientType.equals(""+RegisterActivity.clientTypePatient)){
				//显示自动登录
				setContentView(R.layout.window_login_auto);
				TextView tvVer = (TextView) findViewById(R.id.tv_login_auto_ver);
				tvVer.setText(versionName+"版本");
				//停留1秒后进入患者端主页
				//x.postDelayed(new runnableInPatient(), 3000);
				new Thread(new runnableInPatient()).start();
			}
		}
		//没有登录过,显示选择
		else{
			//showLogin();
			setContentView(R.layout.window_login_auto);
			h.postDelayed(new Runnable() {
				@Override public void run() {
					showLogin();
				}
			}, 3000);
			// ChooseWindowActivity
			//startActivity(new Intent(ChooseWindowActivity.this, GuideActivity.class));
			//finish();
			//h.sendEmptyMessage(0);
		}
	}

	private void showLogin() {
		setContentView(R.layout.window_login_choose_way);

		//医师
		findViewById(R.id.tv_window_login_choose_doctor).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivityForResult(new Intent(ChooseWindowActivity.this,RegisterActivity.class)
				.putExtra("type", RegisterActivity.clientTypeDoctor), RegisterActivity.resultCodeRegisterSucces);

			}
		});

		//患者
		findViewById(R.id.tv_window_login_choose_patient).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivityForResult(new Intent(ChooseWindowActivity.this,RegisterActivity.class)
				.putExtra("type", RegisterActivity.clientTypePatient), RegisterActivity.resultCodeRegisterSucces);

			}
		});
	}

	class runnableInDoctor implements Runnable{

		public void run() {
			SpData sp = new SpData(getApplicationContext());
			boolean isSuccess = register(sp.getStringValue(SpData.keyCode, null), sp.getStringValue(SpData.keyPhoneUser, null)
					, RegisterActivity.clientTypeDoctor);
			if(isSuccess){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startActivity(new Intent(getApplication(),MainDoctorActivity.class));
				ChooseWindowActivity.this.finish();
			}
			else{
				//showLogin();
				h.sendEmptyMessage(0);
			}
		}

	}

	class runnableInPatient implements Runnable{

		public void run() {
			SpData sp = new SpData(getApplicationContext());
			boolean isSuccess = register(sp.getStringValue(SpData.keyCode, null), sp.getStringValue(SpData.keyPhoneUser, null)
					, RegisterActivity.clientTypePatient);
			if(isSuccess){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startActivity(new Intent(getApplication(),PatientMainActivity.class));
				ChooseWindowActivity.this.finish();
			}
			else{
				//showLogin();
				h.sendEmptyMessage(0);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RegisterActivity.resultCodeRegisterSucces:
			finish();
			break;

		case 100:
			
			break;
		}
	}


	boolean isDestroy = false;
	@Override
	public void onStart() {
		super.onStart();
		isDestroy = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isDestroy = true;
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	
	private boolean register(final String mcode, final String mobile, final int clientType){

		try {
			String s = DataService.getRegister(ChooseWindowActivity.this,""+clientType,mcode,mobile);
			mLog.d("http", "登录   s:"+s);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						User user = JSON.parseObject(hr.getData().toString(), User.class);
						SpData sp = new SpData(getApplicationContext());
						sp.setStringValue(SpData.keyId, user.getId());
						queryDoctorNoAudit(mobile, user.getId());
						return true;
					}
				}
			}
		}catch (Exception he) {
			he.printStackTrace();
		}

		return false;
	}
	
	private void queryDoctorNoAudit(final String mobile, final String id){

		try {
			String s = DataService.queryDoctorNoAudit(ChooseWindowActivity.this, mobile, id);
			mLog.d("http", "医生信息  s:"+s);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						RegisterInfo info = JSON.parseObject(hr.getData().toString(), RegisterInfo.class);
						int a = info.getApprovestate();
						mLog.e("http", "KeyApprovestate  choose:"+a);
						new SpData(getApplicationContext())
						.setIntValue(SpData.KeyApprovestate, a);
					}else{
						new SpData(getApplicationContext()).setIntValue(SpData.KeyApprovestate, 0);
					}
				}else{
					new SpData(getApplicationContext()).setIntValue(SpData.KeyApprovestate, 0);
				}
			}else{

			}
		}catch (Exception he) {
			mLog.e("http", "Exception:"+he.toString());
			he.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return 服务器版本信息Versions
	 */
	private Versions queryVersions(String mobile) {
		if(mobile == null){
			return null;
		}
		Versions versions = null;
		try {
			String s = DataService.getVersion(this
					, mobile);
			mLog.d("http", "s:"+s);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if(hr != null){
					if(hr.isSuccess()){
						if(!isDestroy){
							versions = JSON.parseObject(hr.getData().toString(), Versions.class);
						}
					}
				}
			}
		}catch (Exception he) {
			he.printStackTrace();
		}
		
		return versions;
	}

	/**
	 * 下载版本
	 * @param path
	 */
	private void updateVersions(String path){
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && path != null && !path.isEmpty()) {  
			downFile(path);     
		} else {  
			mToast.showToast(ChooseWindowActivity.this, "无法更新版本，请插入SD卡");  
			intersection();
		} 
	}

	private ProgressDialog pBar;
	// 文件分隔符  
	private static final String FILE_SEPARATOR = "/";  
	// 外存sdcard存放路径  
	private static final String FILE_PATH = Environment.getExternalStorageDirectory() + FILE_SEPARATOR +"wht" + FILE_SEPARATOR;  
	// 下载应用存放全路径  
	private static final String FILE_NAME = FILE_PATH + "wht.apk";  

	// 进度条的当前刻度值  
	private int curProgress; 


	/**
	 * 下载新版本
	 * @param spec
	 */
	void downFile(final String spec) {
		handler.sendEmptyMessage(UPDARE_START);
		/*new Thread() {
			public void run() { */       
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(spec);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					int length = (int) entity.getContentLength();   //获取文件大小
                    pBar.setMax(length/1024); //设置进度条的总长度
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(FILE_PATH);
						if(!file.exists()) {  
							file.mkdir();  
						}
						fileOutputStream = new FileOutputStream(new File(FILE_NAME));
						byte[] buf = new byte[1024];   //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一 下就下载完了，看不出progressbar的效果。
						int ch = -1;
						//int process = 0;
						while ((ch = is.read(buf)) != -1) {       
							fileOutputStream.write(buf, 0, ch);
							//process += ch;
							//pBar.setProgress(process);       //这里就是关键的实时更新进度了！
							curProgress += ch;
							handler.sendEmptyMessage(UPDARE_TOKEN); 
						}

					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			/*}*/

		/*}.start();*/
	}  

	void down() {  
		handler.post(new Runnable() {  
			public void run() { 
				pBar.cancel();  
				update();  
			}  
		});  
	}  
	//安装文件，一般固定写法  
	void update() {                      
		File appFile = new File(FILE_NAME);  
		if(!appFile.exists()) {  
			return;  
		}  
		//信鸽推送反注册
		ApplicationTool.getInstance().stopPushService();
		// 跳转到新版本应用安装页面  
		Intent intent = new Intent(Intent.ACTION_VIEW);  
		intent.setDataAndType(Uri.fromFile(
				new File(FILE_NAME)),
				"application/vnd.android.package-archive");  
		startActivity(intent); 
		finish();
	}  

	// 更新应用版本标记  
	private static final int UPDARE_TOKEN = 0x29;  
	private static final int UPDARE_START = 0x30; 
	// 准备安装新版本应用标记  
	private static final int INSTALL_TOKEN = 0x31; 

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {  
		public void handleMessage(Message msg) {  
			// 如果有更新就提示  
			switch (msg.what) {
			case UPDARE_START:
				pBar = new ProgressDialog(ChooseWindowActivity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
				pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pBar.setIcon(R.drawable.ic_launcher);
				pBar.setCancelable(false);
				pBar.setTitle("正在更新:"+versions.getVersionname()+"版本");
				//pBar.setMessage("请稍候...");
				pBar.setProgress(0);
				pBar.show();
				break;
			
			case UPDARE_TOKEN:  
				pBar.setProgress(curProgress / 1024);  
				break;  

			case INSTALL_TOKEN:  
				pBar.cancel();  
				update();  
				break;  
			}
		}
	}; 

	private String versionName;

}
