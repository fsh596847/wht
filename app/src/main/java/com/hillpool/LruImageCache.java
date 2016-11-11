package com.hillpool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class LruImageCache implements ImageCache {

	private static LruCache<String, Bitmap> mMemoryCache;

	private static LruImageCache lruImageCache;

	private LruImageCache() {
		// Get the Max available memory
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
	}

	public static LruImageCache instance() {
		if (lruImageCache == null) {
			lruImageCache = new LruImageCache();
		}
		return lruImageCache;
	}

	@Override
	public Bitmap getBitmap(String arg0) {
		Bitmap bmp = mMemoryCache.get(arg0);
//		try {
//			if (bmp != null){
//				int t = bmp.getWidth();
//				if (t>bmp.getHeight()){
//					t = bmp.getHeight();
//				}
//				bmp = getCroppedBitmap(bmp, t);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return bmp;
	}

	@Override
	public void putBitmap(String arg0, Bitmap arg1) {
		if (getBitmap(arg0) == null) {
			mMemoryCache.put(arg0, arg1);
		}
	}
	/*
	 * 对Bitmap裁剪，使其变成圆形，这步最关键
	 */
	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		if (bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;

		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
				Bitmap.Config.ARGB_8888);
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setColor(Color.parseColor("#BAB399"));

		Canvas c = new Canvas(output);
		c.drawARGB(0, 0, 0, 0);
		c.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
				sbmp.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		c.drawBitmap(sbmp, rect, rect, paint);

		return output;
	}

}
