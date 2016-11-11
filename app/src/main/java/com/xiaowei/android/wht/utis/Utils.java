package com.xiaowei.android.wht.utis;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipaysdk.Base64;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.utils.TFUtil;
import com.xiaowei.android.wht.utils.Util;


public class Utils {
	static public SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.CHINESE);// EEE HH:mm
	static public SimpleDateFormat timeFormatter = new SimpleDateFormat(
			"HH:mm", Locale.CHINESE);// EEE HH:mm
	static public SimpleDateFormat datetimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm", Locale.CHINESE);// EEE
	static public SimpleDateFormat datetimeFormatter4FileName = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.CHINESE);// EEE
	static public SimpleDateFormat dateyearFormatter = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.CHINESE);// EEE HH:mm
	static public SimpleDateFormat dateFormatterYyyyMMdd = new SimpleDateFormat(
			"yyyyMMdd", Locale.CHINESE);// EEE HH:mm
	static public NumberFormat currency = NumberFormat.getCurrencyInstance();
	static public NumberFormat currencyInt = NumberFormat.getInstance();
	
	/**
	 * 图片转化成base64字符串  
	 * @param imgFile //待处理的图片  
	 * @return
	 */
	public static String getImageToBase64Str(String imgFile)  
	{//将图片文件转化为字节数组字符串，并对其进行Base64编码处理  
		if(imgFile == null){
			return null;
		}
		InputStream in = null;  
		byte[] data = null;  
		//读取图片字节数组  
		try   
		{  
			in = new FileInputStream(imgFile);          
			data = new byte[in.available()];  
			in.read(data);  
			in.close();  
		}   
		catch (IOException e)   
		{  
			e.printStackTrace();  
		}  
		//对字节数组Base64编码  
		String s = Base64.encode(data);
		return s;//返回Base64编码过的字节数组字符串  
	}


	/**
	 * 图片选区裁剪
	 * @param activity
	 * @param uri 裁剪后的图片路径
	 * @param olduri 原图
	 * @param requestCode  onActivityResult()
	 */
	public static void cropImageUri(Activity activity,boolean isFromCamera, Uri uri, Uri olduri, int requestCode, int outputX, int outputY)
	  {
	    if (isFromCamera) {
	      int indexColon = olduri.toString().indexOf(":");
	      if (-1 == indexColon) {
	        return;
	      }
	      String pathType = olduri.toString().substring(0, indexColon);
	      if ("content".equals(pathType)) {
	        String[] proj = { "_data" };
	        @SuppressWarnings("deprecation")
			Cursor actualimagecursor = activity.managedQuery(olduri, proj, null, null, null);
	        //int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow("_data");
	        actualimagecursor.moveToFirst();
	        String img_path = Util.getPath(activity, olduri);
	        File file = new File(img_path);
	        olduri = Uri.fromFile(file);
	        if (null == olduri)
	          return;
	      }
	      else if ("file".equals(pathType)) {
	        Boolean isValidatePicturePath = Boolean.valueOf(TFUtil.isValidatePicturePath(olduri.toString()));

	        if (!isValidatePicturePath.booleanValue())
	          return;
	      }
	      else {
	        return;
	      }
	    }
	    Intent intent = new Intent("com.android.camera.action.CROP");
	    intent.setDataAndType(olduri, "image/*");

	    intent.putExtra("crop", "true");
	    if(outputX == outputY){
	    	intent.putExtra("aspectX", 1);
	    	intent.putExtra("aspectY", 1);
	    	
	    	intent.putExtra("outputX", outputX);
	    	intent.putExtra("outputY", outputY);
	    }
	    //intent.putExtra("scale", true);

	    intent.putExtra("output", uri);

	    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	    intent.putExtra("return-data", false);
	    intent.putExtra("noFaceDetection", true);
	    activity.startActivityForResult(intent, requestCode);
	  }
	
	/**
	 * 加密电话号码
	 * 
	 * @param txt
	 * @return
	 */
	static public String decodePhone(String phone) {
		String newPhone = phone.substring(0, 3) + "****"
				+ phone.substring(7, phone.length());
		return newPhone;
	}

	/**
	 * 获取版本号
	 * 
	 * @param txt
	 * @return
	 */
	public static int getVerCode(Activity act) {
		int verCode = -1;
		String packageName = act.getApplicationInfo().packageName;
		try {
			verCode = act.getPackageManager().getPackageInfo(packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
		}
		return verCode;
	}
	
	// 获取当前版本号
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			String packageName = context.getPackageName();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					packageName, 0);
			versionName = packageInfo.versionName;
			if (TextUtils.isEmpty(versionName)) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	static public String getSerialNo(Context context) {
		String ret = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		return ret;
	}

	public static String getFileType(String fileUri) {
		String fileType = fileUri.substring(fileUri.lastIndexOf(".") + 1,
				fileUri.length());
		return fileType;
	}

	public static int Double2Int(Double dd) {
		dd = dd * 1e6;
		int intNum = (int) dd.doubleValue();
		return intNum;
	}

	public static Double Int2Double(int in) {
		Double dd = new Double(in);
		return dd;
	}

	public static String FormatDouble(double dd) {
		DecimalFormat to = new DecimalFormat("0.00");
		return to.format(dd);
	}

	// 手机号码验证
	public static boolean checkPhone(String number) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[6-8]))\\d{8}$");
		Matcher m = p.matcher(number);
		return m.matches();
	}

	public static Bitmap getVideoThumbnail(String videoPath, int width,
			int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		// System.out.println("w" + bitmap.getWidth());
		// System.out.println("h" + bitmap.getHeight());
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 字符串转换到日期，不含时间部分
	 * 
	 * @param txt
	 * @return
	 */
	static public Date str2Date(String txt) {
		Date d = null;
		try {
			if (txt != null && !"".equals(txt.trim())) {
				d = dateFormatter.parse(txt);
			}
		} catch (Exception e) {

		}
		return d;
	}

	/**
	 * 字符串转换到日期，含时间部分
	 * 
	 * @param txt
	 * @return
	 */
	static public Date str2DateTime(String txt) {
		Date d = null;
		try {
			if (txt != null && !"".equals(txt.trim())) {
				d = datetimeFormatter.parse(txt);
			}
		} catch (Exception e) {

		}
		return d;
	}

	/**
	 * copy 文件
	 * 
	 * @param source
	 *            源文件全路径
	 * @param dest
	 *            目标文件路径
	 * @param overWrite
	 *            是否覆盖
	 * @throws IOException
	 */
	public static void copyFile(String source, String dest, boolean overWrite)
			throws IOException {
		if (new File(dest).exists() && !overWrite) {
			throw new IOException("文件已存在");
		}
		byte[] iobuff = new byte[1024];
		int bytes;
		FileInputStream fis = new FileInputStream(source);
		FileOutputStream fos = new FileOutputStream(dest);
		try {
			bytes = fis.read(iobuff, 0, 1024);
			while (bytes != -1) {
				fos.write(iobuff, 0, bytes);
				bytes = fis.read(iobuff, 0, 1024);
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}

	public static void createWatermarkBitmap(String srcFileName,
			String destFileName, String watermark) {
		Bitmap src = BitmapFactory.decodeFile(srcFileName);
		Bitmap dest = createWatermarkBitmap(src, watermark);
		File myCaptureFile = new File(destFileName);
		try {// 将照片存放在预设的文件中
			BufferedOutputStream os = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			dest.compress(Bitmap.CompressFormat.JPEG, 100, os);
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (src != null) {
				src.recycle();
			}
			if (dest != null) {
				dest.recycle();
			}
		}
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
				.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap getSample(String FileName, int maxNumOfPixels) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(FileName, opts);

		opts.inSampleSize = computeSampleSize(opts, -1, maxNumOfPixels);
		opts.inJustDecodeBounds = false;
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeFile(FileName, opts);
		} catch (OutOfMemoryError err) {
		}
		return bmp;
	}

	/**
	 * 
	 * @param src
	 *            原图片
	 * @param watermark
	 *            要打的水印图片
	 * @return Bitmap 打好水印的图片
	 */
	public static Bitmap createWatermarkBitmap(Bitmap src, String watermark) {
		if (src == null) {
			return null;
		}
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();

		// create the new blank bitmap
		Bitmap newb = Bitmap
				.createBitmap(srcWidth, srcHeight, Config.ARGB_8888);// 创建一个新的和src长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src, 0, 0, null);// 在0,0坐标开始画入src
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(20);
		if (watermark != null) {
			cv.drawText(watermark, 10, 50, paint);// 这是画入水印文字，在画文字时，需要指定paint
		}
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		cv = null;
		return newb;
	}

	/**
	 * 根据源文件生成缩略图
	 * 
	 * @param srcFileName
	 * @param destFileName
	 */
	static public void genSmallPic(String srcFileName, String destFileName) {
		Bitmap bm = null;
		BufferedOutputStream bos = null;
		FileOutputStream fout = null;
		File f = null;
		try {
			bm = BitmapFactory.decodeFile(srcFileName);
			if (bm == null) {
				return;
			}
			bm = ThumbnailUtils.extractThumbnail(bm, 100, 100);
			f = new File(destFileName);
			if (f != null && !f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			fout = new FileOutputStream(destFileName);
			bos = new BufferedOutputStream(fout);
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
				if (fout != null) {
					fout.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据源文件生成缩略图
	 * 
	 * @param srcFileName
	 * @param destFileName
	 */
	static public void genSmallPic21(String srcFileName, String destFileName) {
		Bitmap bm = null;
		Options op = new Options();
		op.inSampleSize = 72;
		op.inJustDecodeBounds = false;
		BufferedOutputStream bos = null;
		FileOutputStream fout = null;
		File f = null;
		try {
			bm = BitmapFactory.decodeFile(srcFileName, op);
			if (bm == null) {
				return;
			}
			f = new File(destFileName);
			if (f != null && !f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			fout = new FileOutputStream(destFileName);
			bos = new BufferedOutputStream(fout);
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
				if (fout != null) {
					fout.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeString2File(String fileName, String s) {
		File f = new File(fileName);
		if (f != null) {
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			out.write(s.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static long getFileSize(String fileName) {
		File f = new File(fileName);
		if (f.exists()) {
			return f.length();
		} else {
			return 0;
		}
	}

	public static String readFileContent(String fileName) {
		File f = new File(fileName);
		if (!f.exists()) {
			return null;
		}
		byte[] buf = new byte[(int) f.length()];
		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
			int count = in.read(buf, 0, (int) f.length());
			if (count != (int) f.length()) {
				System.out
						.println("Read content from file is not equale the file length.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new String(buf);
	}

	/**
	 * 打开文件
	 * 
	 * @param file
	 */
	static public Intent openFile(File file) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 获取文件file的MIME类型
		String type = getMIMEType(file);
		// 设置intent的data和Type属性。
		intent.setDataAndType(/* uri */Uri.fromFile(file), type);
		// 跳转
		return intent;
	}

	/**
	 * 根据文件名判断文件是否图片文件
	 * 
	 * @param fileName
	 * @return
	 */
	static public boolean isImage(String fileName) {
		String mimeType = getMIMEType(fileName);
		if (mimeType != null && mimeType.startsWith("image")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据文件名判断文件是否视频文件
	 * 
	 * @param fileName
	 * @return
	 */
	static public boolean isVideo(String fileName) {
		String mimeType = getMIMEType(fileName);
		if (mimeType != null && mimeType.startsWith("video")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据文件名判断文件是否视频文件
	 * 
	 * @param fileName
	 * @return
	 */
	static public boolean isVoice(String fileName) {
		String mimeType = getMIMEType(fileName);
		if (mimeType != null && mimeType.startsWith("audio")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * 
	 * @param file
	 */
	static public String getMIMEType(File file) {
		String fName = file.getName();
		return getMIMEType(fName);
	}

	static public String getMIMEType(String fName) {
		String type = "*/*";
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) { // MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	static final String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
			{ ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".amr", "audio/amr" }, { ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" }, { "", "*/*" } };

	/**
	 * compareStr：对String类型排序
	 * 
	 * @param @return 设定文件
	 * @return String 对象类型
	 * @throws
	 */
	static public int compareStr(String o1, String o2) {
		String s1 = (String) o1;
		String s2 = (String) o2;
		int len1 = s1.length();
		int len2 = s2.length();
		int n = Math.min(len1, len2);
		char v1[] = s1.toCharArray();
		char v2[] = s2.toCharArray();
		int pos = 0;
		while (n-- != 0) {
			char c1 = v1[pos];
			char c2 = v2[pos];

			if (c1 != c2) {
				return c1 - c2;
			}
			pos++;
		}
		return len1 - len2;
	}

	/**
	 * compareInt：对int类型排序
	 * 
	 * @param @return 设定文件
	 * @return String 对象类型
	 * @throws
	 * @since CodingExample　Ver 1.1
	 */
	static public int compareInt(Integer o1, Integer o2) {
		int val1 = o1.intValue();
		int val2 = o2.intValue();
		return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));
	}

	static class CompratorByFileName implements Comparator<File> {
		public int compare(File file1, File file2) {
			return compareStr(file1.getAbsolutePath(), file2.getAbsolutePath());
		}
	}

	static class CompratorByFileSize implements Comparator<File> {
		public int compare(File file1, File file2) {
			long diff = file1.length() - file2.length();
			if (diff > 0)
				return 1;
			else if (diff == 0)
				return 0;
			else
				return -1;
		}
	}

	static class CompratorByFileTime implements Comparator<File> {
		public int compare(File file1, File file2) {
			long diff = file1.lastModified() - file2.lastModified();
			if (diff > 0)
				return 1;
			else if (diff == 0)
				return 0;
			else
				return -1;
		}
	}

	/**
	 * 根据含路径和文件名的字符串获取路径部分
	 * 
	 * @param filePathName
	 * @return
	 */
	static public String getFilePath(String filePathName) {
		if (filePathName == null) {
			return null;
		}
		int pos = filePathName.lastIndexOf("/");
		if (pos > -1) {
			return filePathName.substring(0, pos);
		} else {
			return "/";
		}
	}

	/**
	 * 根据含路径和文件名的字符串获取文件名部分
	 * 
	 * @param filePathName
	 * @return
	 */
	static public String getFileName(String filePathName) {
		if (filePathName == null) {
			return null;
		}
		int pos = filePathName.lastIndexOf("/");
		if (pos > -1 && pos < filePathName.length() - 1) {
			return filePathName.substring(pos + 1);
		} else {
			return filePathName;
		}
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	static public boolean fileExists(String fileName) {
		File f = new File(fileName);
		return f.exists();
	}

	/**
	 * 删除指定文件路径下面的所有文件和文件夹
	 * 
	 * @param file
	 *            文件或文件夹的File对象
	 * @return 删除操作是否成功
	 */
	public static boolean delFiles(File file) {
		boolean flag = false;
		try {
			if (file.exists()) {
				if (file.isDirectory()) {
					String[] contents = file.list();
					for (int i = 0; i < contents.length; i++) {
						File file2X = new File(file.getAbsolutePath() + "/"
								+ contents[i]);
						if (file2X.exists()) {
							if (file2X.isFile()) {
								flag = file2X.delete();
							} else if (file2X.isDirectory()) {
								delFiles(file2X);
							}
						} else {
							throw new RuntimeException("File not exist!");
						}
					}
				}
				flag = file.delete();
				if (!flag) {
					Thread.sleep(1000);
					flag = file.delete();
				}
				if (!flag) {
					Thread.sleep(1000);
					flag = file.delete();
				}
			} else {
				throw new RuntimeException("File not exist!");
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	static public Bitmap LoadBitmapFromFile(String fileName) {
		Bitmap result = null;
		if (fileName == null) {
			return result;
		}
		// File f = new File(fileName);
		// InputStream is = null;

		try {
			// is = new FileInputStream(f);
			boolean hasError = false;
			BitmapFactory.Options opts = null;
			do {
				try {
					if (hasError) {
						if (opts == null) {
							opts = new BitmapFactory.Options();
							opts.inSampleSize = 2;
						} else {
							opts.inSampleSize = 2 * opts.inSampleSize;
						}
					}
					result = BitmapFactory.decodeFile(fileName, opts);
					hasError = false;
				} catch (OutOfMemoryError e) {
					hasError = true;
				}
			} while (hasError);
			/*
			 * int length = (int)f.length(); byte[] buf = new byte[length];
			 * is.read(buf, 0, length); result =
			 * BitmapFactory.decodeByteArray(buf, 0, length);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (is != null){ try { is.close(); } catch (IOException e) {
			 * e.printStackTrace(); } }
			 */
		}
		return result;
	}

	static public long getFolderSize(String fileName) {
		File f = new File(fileName);
		long length = 0l;
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (File af : fs) {
				if (af.isFile()) {
					length += af.length();
				} else {
					length += getFolderSize(af.getAbsolutePath());
				}
			}
		} else {
			length += f.length();
		}
		return length;
	}

	static public String formatSize(long size) {
		String suffix = null;

		if (size >= 1024) {
			suffix = "KiB";
			size /= 1024;
			if (size >= 1024) {
				suffix = "MiB";
				size /= 1024;
			}
		}

		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}

		if (suffix != null)
			resultBuffer.append(suffix);
		return resultBuffer.toString();
	}

	static public void showToast(Context context, String msg) {
		Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}

	static public void showToastLong(Context context, String msg) {
		Toast t = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}

	static public void showDailog(Activity context, String msg) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(msg).setCancelable(true).setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					}).show();
		} catch (Exception e) {

		}
	}

	static public boolean strIsEmpty(String v) {
		return (v == null || "".equals(v.trim()));
	}

	/**
	 * 将字符串列表用冒号串成一个字符串
	 * 
	 * @param list
	 * @return
	 */
	public String List2String(List<String> list, String seperator) {
		if (list == null)
			return null;
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (String s : list) {
			if (i == 0) {
				buf.append(s);
				i++;
			} else {
				buf.append(seperator + s);
			}
		}
		return buf.toString();
	}

	public List<String> string2List(String s, String seperator) {
		if (s == null)
			return null;
		String[] ss = s.split(seperator);
		List<String> list = new ArrayList<String>();
		for (String str : ss) {
			if (str != null && str.trim().length() > 1) {
				list.add(str.trim());
			}
		}
		return list;
	}

	/**
	 * 关闭android的dialog
	 * 
	 * @param dialog
	 */
	static public void closeDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField(
					"mShowing");
			field.setAccessible(true);
			// 设置mShowing值，欺骗android系统
			field.set(dialog, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保持android的dialog
	 * 
	 * @param dialog
	 */
	static public void keepDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField(
					"mShowing");
			field.setAccessible(true);
			// 设置mShowing值，欺骗android系统
			field.set(dialog, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	static public boolean isNetworkAvailable(Context context) {
		boolean active = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// check the networkInfos numbers
		if (cm != null) {
			NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
			for (int i = 0; i < networkInfos.length; i++) {
				if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
					active = true;
				}
			}
		}
		return active;
	}

	// 获取ApiKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	} // 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false

	public static boolean hasBind1(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String flag = sp.getString("bind_flag", "");
		if ("ok".equalsIgnoreCase(flag)) {
			return true;
		}
		return false;
	}

	public static void setBind1(Context context, boolean flag) {
		String flagStr = "not";
		if (flag) {
			flagStr = "ok";
		}
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("bind_flag", flagStr);
		editor.commit();

	}
    //
	public static boolean isRunningForeground(Context context,
			String activityName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		String currenActivityName = cn.getClassName();
		if (!TextUtils.isEmpty(currenActivityName)
				&& currenActivityName.equals(activityName)) {
			return true;
		}

		return false;
	}

	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog createLoadingDialog(Context context, String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		// LinearLayout layout = (LinearLayout)
		// v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView

		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img_load);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_dialog);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);// 设置加载信息

		loadingDialog.setCanceledOnTouchOutside(false);
		// loadingDialog.setCancelable(false);// 不可以用“返回键”取消

		return loadingDialog;
	}
	
	
	/**
	 * 获取图片的旋转角度
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String filePath) {
		int angle = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(filePath);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					-1);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				angle = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				angle = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				angle = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return angle;
	}
	
	/**
	 * 旋转图片
	 * @param angle
	 * @param bitmap
	 * @return
	 */
	 public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
	       //旋转图片 动作   
	       Matrix matrix = new Matrix();
	       matrix.reset();
	       matrix.postRotate(angle);   
	       // 创建新的图片   
	       Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
	               bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
	       return resizedBitmap;  
	   }
	 
	 /**
	  * 判断是否安装了指定程序
	  * @param context
	  * @param packageName
	  * @return
	  */
	public static boolean isAppInstalled(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		for (PackageInfo pkg : installedPackages) {
			if (pkg.versionName == null) {
				continue;
			}		
			if (pkg.packageName.equalsIgnoreCase(packageName)) {
				return true;
			}
		}
		return false;
	}

}
