package com.xiaowei.android.wht.utis;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * 屏幕显示工具类
 *
 * @author JohnWatson
 * @version 1
 * @date 2014-11-6 上午11:33:19
 */
public class DisplayUtil {
  /**
   * 是否是平板设备
   */
  public static boolean isTabletDevice(Context context) {

    boolean xlarge = (context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;

    if (xlarge) {
      DisplayMetrics metrics = new DisplayMetrics();
      WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
      wm.getDefaultDisplay().getMetrics(metrics);

      if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
          || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
          || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
          || metrics.densityDpi == DisplayMetrics.DENSITY_TV
          || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
        return true;
      }
    }

    return false;
  }

  public static float sp2Px(int sp, Context context) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
        context.getResources().getDisplayMetrics());
  }

  public static int sp2Px_Int(int sp, Context context) {
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
        context.getResources().getDisplayMetrics()));
  }

  public static float dp2Px(int dp, Context context) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        context.getResources().getDisplayMetrics());
  }

  public static int dp2Px_Int(int dp, Context context) {
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        context.getResources().getDisplayMetrics()));
  }

  @NonNull public static String getDensityName(Context context) {
    DisplayMetrics dm = context.getResources().getDisplayMetrics();

    float density = dm.density;
    if (density >= 4.0) {
      return "xxxhdpi";
    }
    if (density >= 3.0) {
      return "xxhdpi";
    }
    if (density >= 2.0) {
      return "xhdpi";
    }
    if (density >= 1.5) {
      return "hdpi";
    }
    if (density >= 1.0) {
      return "mdpi";
    }
    return "ldpi";
  }

  @NonNull public static String getDensityNameByDensityDpi(Context context) {
    DisplayMetrics dm = context.getResources().getDisplayMetrics();

    float densityDpi = dm.densityDpi;
    if (densityDpi >= 640) {
      return "xxxhdpi";
    }
    if (densityDpi >= 480) {
      return "xxhdpi";
    }
    if (densityDpi >= 320) {
      return "xhdpi";
    }
    if (densityDpi >= 240) {
      return "hdpi";
    }
    if (densityDpi >= 160) {
      return "mdpi";
    }
    return "ldpi";
  }
}