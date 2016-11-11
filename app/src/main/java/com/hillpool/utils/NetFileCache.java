package com.hillpool.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;
/**
 * 网络缓存类
 * 根据URL获取后存在本地，然后再读取
 * @author wudw
 *
 */
public class NetFileCache  {
	/** 单例 */
	private static NetFileCache cache;
	/** 内存缓存 */
	private static HashMap<String, String> memory;
	/** 缓存目录 */
	private static String cacheDir;
	/** 缓存的单位 */
	private static final int MB = 1024 * 1024;
	/** 缓存的大小 */

	/** 获取单例 */
	public static NetFileCache getInstance() {
		if (null == cache) {
			cache = new NetFileCache();
		}
		return cache;
	}

	public static String getLinkFileName(String url){
		url = url.replaceAll(":", "-");
		final String key = url;
		File file = new File(cacheDir + key);
		return file.getAbsolutePath();
	}
	/**
	 * 必须进行初始化
	 * @param context
	 */
	public void init(Context context) {
		if (memory == null){
			memory = new HashMap<String, String>();
			//cacheDir = context.getCacheDir().toString() + File.separator+"file"+File.separator;
			cacheDir = Environment.getExternalStorageDirectory() + File.separator+"file"+File.separator;
			File f = new File(cacheDir);
			if (!f.exists()){
				f.mkdirs();
			}
		}
	}

	/** 获取本地文件名 */
	public String getLocalFilePath(final String key) {
		try {
			if (memory.containsKey(key)) {
				return memory.get(key);
			} else {
				File file = new File(getLinkFileName(key));
				if (file.exists()) {
					memory.put(key, file.getAbsolutePath());
					return file.getAbsolutePath();
				}
			}
		} catch (Exception e) {
			Log.d("halfman", e.getMessage());
		}
		return null;
	}

	public void putFile(final String key, final String filePath) {
		// 尺寸超过50时，清理缓存并放入内存
		if (memory.size() == 200) {
			memory.clear();
		}
		// 放入文件到内存
		memory.put(key, filePath);
		
	}
	
	public void removeFile(final String key) {
		if (memory.containsKey(key)) {
			memory.remove(key);		
		}
	}

	
}
