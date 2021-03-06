package com.xiaowei.android.wht.utis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

/**
 * Created by HIPAA on 2016/11/22.
 */

public class ImageUtils {
  // 根据路径获得图片并压缩，返回bitmap用于显示
  public static Bitmap getSmallBitmap(String filePath) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(filePath, options);

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, 480, 800);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;

    return BitmapFactory.decodeFile(filePath, options);
  }

  //计算图片的缩放值
  public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
      int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
      final int heightRatio = Math.round((float) height / (float) reqHeight);
      final int widthRatio = Math.round((float) width / (float) reqWidth);
      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    return inSampleSize;
  }

  //把bitmap转换成String
  //public static String bitmapToString(String filePath) {
  //  Bitmap bm = getSmallBitmap(filePath);
  //  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  //  bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
  //  byte[] b = baos.toByteArray();
  //  Log.d("d", "压缩后的大小=" + b.length);//1.5M的压缩后在100Kb以内，测试得值,压缩后的大小=94486,压缩后的大小=74473
  //  return Base64.encode(b, Base64.DEFAULT);
  //}

  public static <V extends ImageView> void setImageBitmap(V v, Bitmap bitmap) {
    checkNotNull(v, "v cannot be null.");
    v.setImageBitmap(bitmap);
  }

  public static <T> void checkNotNull(T value, String message) {
    if (value == null) {
      if (TextUtils.isEmpty(message)) {
        throw new IllegalArgumentException();
      } else {
        throw new IllegalArgumentException(message);
      }
      //throw new NullPointerException(message);
    }
  }
}
