package com.xiaowei.android.wht.utis;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xiaowei.android.wht.BuildConfig;
import com.xiaowei.android.wht.R;

/**
 * Created by HIPAA on 2016/11/29.
 */

public class ImageLoadProxy {
  private static final int MAX_DISK_CACHE = 1024 * 1024 * 50;
  private static final int MAX_MEMORY_CACHE = 1024 * 1024 * 10;

  /**
   * 是否显示日志
   */
  private static boolean isShowLog = false;

  public static ImageLoader imageLoader;

  /**
   * 单例构建
   */
  public static ImageLoader getImageLoader() {

    if (imageLoader == null) {
      synchronized (ImageLoadProxy.class) {
        imageLoader = ImageLoader.getInstance();
      }
    }

    return imageLoader;
  }

  /**
   * 在Application中初始化参数
   */
  public static void initImageLoader(Context context) {
    ImageLoaderConfiguration.Builder build = new ImageLoaderConfiguration.Builder(context);
    build.tasksProcessingOrder(QueueProcessingType.LIFO);
    build.diskCacheSize(MAX_DISK_CACHE);
    build.memoryCacheSize(MAX_MEMORY_CACHE);
    build.memoryCache(new LruMemoryCache(MAX_MEMORY_CACHE));
    if (BuildConfig.DEBUG && isShowLog) {
      build.writeDebugLogs();
    }
    getImageLoader().init(build.build());
  }

  /**
   * 自定义Option
   */
  public static void displayImage(String url, ImageView target, DisplayImageOptions options) {
    imageLoader.displayImage(url, target, options);
  }

  /**
   * 头像专用
   */
  public static void displayHeadIcon(String url, ImageView target) {
    imageLoader.displayImage(url, target, getOptions4Header());
  }

  /**
   * 图片详情页专用
   */
  public static void displayImage4Detail(String url, ImageView target,
      SimpleImageLoadingListener loadingListener) {
    imageLoader.displayImage(url, target, getOption4ExactlyType(), loadingListener);
  }

  /**
   * 图片列表页专用
   */
  public static void displayImageList(String url, ImageView target, int loadingResource,
      SimpleImageLoadingListener loadingListener, ImageLoadingProgressListener progressListener) {
    imageLoader.displayImage(url, target, getOptions4PictureList(loadingResource), loadingListener,
        progressListener);
  }

  /**
   * 自定义加载中图片
   */
  public static void displayImageWithLoadingPicture(String url, ImageView target,
      int loadingResource) {
    imageLoader.displayImage(url, target, getOptions4PictureList(loadingResource));
  }

  /**
   * 当使用WebView加载大图的时候，使用本方法现下载到本地然后再加载
   */
  public static void loadImageFromLocalCache(String url,
      SimpleImageLoadingListener loadingListener) {
    imageLoader.loadImage(url, getOption4ExactlyType(), loadingListener);
  }

  /**
   * 加载头像专用Options，默认加载中、失败和空url为 ic_loading_small
   */
  public static DisplayImageOptions getOptions4Header() {
    return new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .showImageForEmptyUri(R.drawable.zanwutupian)
        .showImageOnFail(R.drawable.zanwutupian)
        .showImageOnLoading(R.drawable.zanwutupian)
        .build();
  }

  /**
   * 设置图片放缩类型为模式EXACTLY，用于图片详情页的缩放
   */
  public static DisplayImageOptions getOption4ExactlyType() {
    return new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .resetViewBeforeLoading(true)
        .considerExifParams(true)
        .imageScaleType(ImageScaleType.EXACTLY)
        .build();
  }

  /**
   * 加载图片列表专用，加载前会重置View {@link com.nostra13.universalimageloader.core.DisplayImageOptions.Builder#resetViewBeforeLoading}
   * = true
   */
  public static DisplayImageOptions getOptions4PictureList(int loadingResource) {
    return new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .resetViewBeforeLoading(true)
        .showImageOnLoading(loadingResource)
        .showImageForEmptyUri(loadingResource)
        .showImageOnFail(loadingResource)
        .build();
  }
}
