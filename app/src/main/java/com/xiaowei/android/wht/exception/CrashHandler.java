package com.xiaowei.android.wht.exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.utis.HlpUtils;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类
 * 来接管程序,并记录 发送错误报告.
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
	/** Debug Log tag*/
	public static final String TAG = "CrashHandler";
	/** 是否开启日志输出,在Debug状态下开启,
	 * 在Release状态下关闭以提示程序性能
	 * */
	public static final boolean DEBUG = false;
	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** CrashHandler实例 */
	private static CrashHandler INSTANCE;
	/** 程序的Context对象 */
	private Context mContext;
	
	
	/** 使用Properties来保存设备的信息和错误堆栈信息*/
	private Properties mDeviceCrashInfo = new Properties();
	//用来存储设备信息和异常信息   
	private Map<String, String> infos = new HashMap<String, String>();  

	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";
	/** 错误报告文件的扩展名 */
	private static final String CRASH_REPORTER_EXTENSION = ".txt";
	
	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {}
	/** 获取CrashHandler实例 ,单例模式*/
	public static CrashHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CrashHandler();
		}
		return INSTANCE;
	}

	static String crFileName;
	
	/**
	 * 初始化,注册Context对象,
	 * 获取系统默认的UncaughtException处理器,
	 * 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
 
	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */

	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			//如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			//Sleep一会后结束程序
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error : ", e);
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息
	 * 发送错误报告等操作均在此完成.
	 * 开发者可以根据自己的情况来自定义异常处理逻辑
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		ex.printStackTrace();
		final String msg = ex.getMessage();

		try{

			//收集设备信息
			collectCrashDeviceInfo(mContext);
			//保存错误报告文件
			String crashFileName = saveCrashInfoToFile(ex);
//			Intent i = new Intent(ExceptionCollector.ActionSendException);
//			i.putExtra(ExceptionCollector.ParameterSendExceptionPackageName, mContext.getPackageName());
//			i.putExtra(ExceptionCollector.ParameterSendExceptionFileName, crashFileName);
//			mContext.sendBroadcast(i);
			if (!Config.debug){
				Intent i = new Intent(mContext,ExceptionCollectorService.class);
				i.putExtra(ExceptionCollector.ParameterSendExceptionPackageName, mContext.getPackageName());
				i.putExtra(ExceptionCollector.ParameterSendExceptionFileName, crashFileName);
				mContext.startService(i);
			}
			//sendCrashReportsToMail(mContext);
		}catch (Exception e){
			
		}
		//使用Toast来显示异常信息
//		new Thread() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				if (msg != null && !"".equals(msg.trim())){
//					Toast t = Toast.makeText(mContext, "出现运行错误.", Toast.LENGTH_LONG);
//					t.show();
//				}
//				Looper.loop();
//			}
//
//		}.start();
		return true;
	}

	private String saveCrashInfoToFile(Throwable ex) {	
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { 
			return null;
		}
		 StringBuffer sb = new StringBuffer();       
		 String fileName = "";
		 for (Map.Entry<String, String> entry : infos.entrySet()) {                                             
		     String key = entry.getKey();                                                                       
		     String value = entry.getValue();                                                                   
		     sb.append(key + "=" + value + "\r\n");                                                               
		 }                                                                                                      
		                                                                                                        
		 Writer writer = new StringWriter();                                                                    
		 PrintWriter printWriter = new PrintWriter(writer);                                                     
		 ex.printStackTrace(printWriter);                                                                       
		 Throwable cause = ex.getCause();                                                                       
		 while (cause != null) {                                                                                
		     cause.printStackTrace(printWriter);                                                                
		     cause = cause.getCause();                                                                          
		 }                                                                                                      
		 printWriter.close();                                                                                   
		 String result = writer.toString();                                                                     
		 sb.append(result);                                                                                     
		 try {                                                                                                  
			 SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyyMMddHHmm", Locale.US);
				fileName = ApplicationTool.getInstance().loginUserId+"-"+ formatter.format(new Date()) + "-"+HlpUtils.getSerialNo(mContext) + CRASH_REPORTER_EXTENSION;                                   
		     //if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {                     
		    	 File file =  new File(Environment.getExternalStorageDirectory(),packageName == null?"crashFiles4hlp":"crashFiles4hlp/"+packageName);
		    	 if (!file.exists()){
		    		 file.mkdirs();
		    	 }
		    	 file = new File(file,fileName);
				 fileName = file.getAbsolutePath();                                                                   
		         FileOutputStream fos = new FileOutputStream(fileName);                                  
		         fos.write(sb.toString().getBytes());                                                           
		         fos.close();                                                                                   
		    // }                                                                                                  
		     return fileName;                                                                                   
		 } catch (Exception e) {                                                                                
		     Log.e(TAG, "an error o" +
		     		"ccured while writing file...", e);                                           
		 }                   
		 Log.d(TAG, "crash file name:"+fileName);
		 return fileName;  
	}
	
	String packageName;
	private void collectCrashDeviceInfo(Context ctx) {
		 try {                                                                                                    
		     PackageManager pm = ctx.getPackageManager();                                                         
		     PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);             
		     if (pi != null) {                                                                                    
		         String versionName = pi.versionName == null ? "null" : pi.versionName;                           
		         String versionCode = pi.versionCode + "";                        
		         packageName = pi.packageName;
		         infos.put("packageName", pi.packageName);                                                       
		         infos.put("versionName", versionName);                                                           
		         infos.put("versionCode", versionCode);
		     }                                                                                                    
		 } catch (NameNotFoundException e) {                                                                      
		     Log.e(TAG, "an error occured when collect package info", e);                                         
		 }                                                                                                        
		 Field[] fields = Build.class.getDeclaredFields();                                                        
		 for (Field field : fields) {                                                                             
		     try {                                                                                                
		         field.setAccessible(true);                                                                       
		         infos.put(field.getName(), field.get(null).toString());                                          
		         Log.d(TAG, field.getName() + " : " + field.get(null));                                           
		     } catch (Exception e) {                                                                              
		         Log.e(TAG, "an error occured when collect crash info", e);                                       
		     }                                                                                                    
		 }                                                                                                        

	}
}
	