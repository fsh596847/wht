package com.xiaowei.android.wht.utils;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.xiaowei.android.wht.SpData;

public class Util {
	
	/**
	 * 内嵌liseview给高度后全部显示
	 * @param listView 内嵌
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {    
		ListAdapter listAdapter = listView.getAdapter();     
		if (listAdapter == null) {    
			// pre-condition    
			return;    
		}    

		int totalHeight = 0;    
		for (int i = 0; i < listAdapter.getCount(); i++) {    
			View listItem = listAdapter.getView(i, null, listView);    
			listItem.measure(0, 0);    
			totalHeight += listItem.getMeasuredHeight();    
		}    

		ViewGroup.LayoutParams params = listView.getLayoutParams();    
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));    
		listView.setLayoutParams(params);    
	} 
	
	/**
	 * 是否注册登录
	 * @return 
	 */
	public static boolean isRegisterSucceds(Context context){
		SpData spData = new SpData(context);
		int va = spData.getIntValue(SpData.KeyApprovestate, -1);
		mLog.e("http", "KeyApprovestate  util:"+va);
		if(va != 1){
			mLog.e("http", "return false:"+va);
			return false;
		}
		mLog.e("http", "return true:"+va);
		return true;
	}

	public static Bitmap rotaingImageView(int angle, Bitmap bitmap)
	{
		if (bitmap == null) {
			return null;
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(angle);

		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
	
	/**
	 * 
	 * @param angle
	 * @param bitmap
	 * @param type 根据宽（1）/高（2）缩放图片
	 * @param value
	 * @return
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap, int type, int value)
	{
		if (bitmap == null) {
			return null;
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		switch (type) {
		case 1:
			matrix.postScale(value/(float)bitmap.getWidth(),value/(float)bitmap.getWidth()); 
			break;

		case 2:
			matrix.postScale(value/(float)bitmap.getHeight(),value/(float)bitmap.getHeight()); 
			break;
		}
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
	
	/**
	 * 
	 * @param bitmap
	 * @param type 根据宽（1）/高（2）缩放图片
	 * @param value
	 * @return
	 */
	public static Bitmap rotaingImageView(Bitmap bitmap, int type, int value)
	{
		if (bitmap == null || value <= 0) {
			return null;
		}

		Matrix matrix = new Matrix();
		switch (type) {
		case 1:
			matrix.postScale(value/(float)bitmap.getWidth(),value/(float)bitmap.getWidth()); 
			break;

		case 2:
			matrix.postScale(value/(float)bitmap.getHeight(),value/(float)bitmap.getHeight()); 
			break;
		}
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static int readPictureDegree(String path)
	{
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt("Orientation", 1);

			switch (orientation) {
			case 6:
				degree = 90;
				break;
			case 3:
				degree = 180;
				break;
			case 8:
				degree = 270;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	@SuppressLint("NewApi")
	public static String getPath(Context context, Uri uri)
	{
		boolean isKitKat = Build.VERSION.SDK_INT >= 19;

		if ((isKitKat) && (DocumentsContract.isDocumentUri(context, uri)))
		{
			if (isExternalStorageDocument(uri)) {
				String docId = DocumentsContract.getDocumentId(uri);
				String[] split = docId.split(":");
				String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

			}
			else
			{
				if (isDownloadsDocument(uri))
				{
					String id = DocumentsContract.getDocumentId(uri);
					Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id).longValue());

					return getDataColumn(context, contentUri, null, null);
				}

				if (isMediaDocument(uri)) {
					String docId = DocumentsContract.getDocumentId(uri);
					String[] split = docId.split(":");
					String type = split[0];

					Uri contentUri = null;
					if ("image".equals(type))
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					else if ("video".equals(type))
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}

					//String selection = "_id=?";
					String[] selectionArgs = { split[1] };

					return getDataColumn(context, contentUri, "_id=?", selectionArgs);
				}
			}
		} else {
			if ("content".equalsIgnoreCase(uri.getScheme()))
			{
				if (isGooglePhotosUri(uri)) {
					return uri.getLastPathSegment();
				}
				return getDataColumn(context, uri, null, null);
			}

			if ("file".equalsIgnoreCase(uri.getScheme())) {
				return uri.getPath();
			}
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs)
	{
		Cursor cursor = null;
		//String column = "_data";
		String[] projection = { "_data" };
		try
		{
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

			if ((cursor != null) && (cursor.moveToFirst())) {
				int index = cursor.getColumnIndexOrThrow("_data");
				String str1 = cursor.getString(index);
				return str1;
			}
		}
		finally
		{
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri)
	{
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri)
	{
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri)
	{
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public static boolean isGooglePhotosUri(Uri uri)
	{
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

}
