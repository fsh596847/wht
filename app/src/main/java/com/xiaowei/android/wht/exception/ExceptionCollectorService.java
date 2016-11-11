package com.xiaowei.android.wht.exception;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.HttpUtil;


public class ExceptionCollectorService extends Service {
	private static final String THIS_FILE = "ExceptionCollectorService";
	//final String serverUrlPrex="http://121.40.73.173:8080/hlpEC/";
//	final String serverUrlPrex="http://192.168.0.101:8080/hlpEC/";
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
//	class OneException{
//		String packageName;
//		String fileName;
//	}
	static boolean isSending = false;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent==null){
			return super.onStartCommand(intent, flags, startId);
		}
		final String packageName = intent.getStringExtra(ExceptionCollector.ParameterSendExceptionPackageName);
		final String fileName = intent.getStringExtra(ExceptionCollector.ParameterSendExceptionFileName);
		if (!HlpUtils.isEmpty(fileName) && !HlpUtils.isEmpty(fileName)){
			if (!isSending){
				isSending = true;
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						postFile2Server(packageName,fileName);
						isSending = false;
					}
				}).start();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	/**
	 * 发送文件到服务器，同时检查所在目录是否还有其他文件进行上传
	 * @param packageName
	 * @param fileName
	 */
	private void postFile2Server(String packageName,String fileName) {
		sendOneFile2Server(packageName, fileName);
		File file = new File(fileName);
		File[] files = file.getParentFile().listFiles();
		if (files != null && files.length>0){
			for (File f:files){
				if (f.isFile()){
					sendOneFile2Server(packageName, f.getAbsolutePath());
				}
			}
		}
	}
	/**
	 * 发送一个文件到服务器
	 * @param packageName
	 * @param fileName
	 */
	private void sendOneFile2Server(String packageName,String fileName){
		String url= Config.crashFileRecvServer+ "recvFile.jsp?packageName="+packageName;
		Map<String,String> filePaths = new  HashMap<String, String>();
		filePaths.put("crashFile", fileName);
		int count = 0;
		while (count<3){
			try {
				String s = HttpUtil.postMultipartUrl(url, null, filePaths, "UTF-8");
				HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null && hr.success){
					File f = new File(fileName);
					f.delete();
					break;
				}
				try{
					Thread.sleep(1000);
				}catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			count++;
		}
	}
	
	public void onDestroy() {
	};
}
