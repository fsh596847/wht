package com.hillpool.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;  

public class FileRequest<T> extends Request<String> {
	public static final String TAG = "FileRequest";
	private final Listener<String> mListener;  
	private String mUrl;
	private String mKey;
	public FileRequest(int method,String key, String url,Listener<String> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;  
		this.mUrl = url;
		this.mKey = key;
	}
	public FileRequest(String key,String url,Listener<String> listener, ErrorListener errorListener) {
		this(Method.GET,key,url,listener, errorListener);
	}
	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response); 
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		FileOutputStream os = null;
		String fileName = null;
		Boolean writeFileResult = true;
		try{
			fileName = NetFileCache.getLinkFileName(mKey);
			File file = new File(fileName); 
			os = new FileOutputStream(file);
			os.write(response.data);
			NetFileCache.getInstance().putFile(mKey, fileName);
		}catch (Exception e) {
			writeFileResult = false;
			e.printStackTrace();
		}finally{
			if (os != null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (writeFileResult) {
			Log.i(TAG, "下载成功");
			return Response.success(fileName, null);
		}else {
			Log.i(TAG, "下载失败");
			return Response.error(new ParseError(response));
		}
			
	}  

}
