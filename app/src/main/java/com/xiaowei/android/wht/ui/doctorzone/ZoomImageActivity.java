package com.xiaowei.android.wht.ui.doctorzone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.utis.BitmapUtil;
import com.xiaowei.android.wht.views.DragImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HIPAA on 2016/11/28.   图片缩放
 */

public class ZoomImageActivity extends BaseActivity {
  private int window_width, window_height;// 控件宽度
  private DragImageView dragImageView;// 自定义控件
  private int state_height;// 状态栏的高度
  private String url = "http://121.40.126.229:8082/sbbx/images/wap/phone/speaker/1477541890657.jpg";
  private Bitmap bitmap = null;
  private ViewTreeObserver viewTreeObserver;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_zoomimage);
  }

  @Override public void init(Bundle savedInstanceState) {
    /** 获取可見区域高度 **/
    WindowManager manager = getWindowManager();
    window_width = manager.getDefaultDisplay().getWidth();
    window_height = manager.getDefaultDisplay().getHeight();
    dragImageView = (DragImageView) findViewById(R.id.show);
    Bitmap bmp = BitmapUtil.ReadBitmapById(this, R.drawable.ydy1,
        window_width, window_height);
    // 设置图片
    //Picasso.with(this).load(url).into(dragImageView);
    dragImageView.setmActivity(this);//注入Activity.
    new Thread(new Runnable() {
      @Override public void run() {
        bitmap = returnBitmap(url);
        runOnUiThread(new Runnable() {
          @Override public void run() {
            dragImageView.setImageBitmap(bitmap);
          }
        });
      }
    }).start();
  }

  @Override public void setListener() {
    /** 测量状态栏高度 **/
    viewTreeObserver = dragImageView.getViewTreeObserver();
    viewTreeObserver
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

          @Override
          public void onGlobalLayout() {
            if (state_height == 0) {
              // 获取状况栏高度
              Rect frame = new Rect();
              getWindow().getDecorView()
                  .getWindowVisibleDisplayFrame(frame);
              state_height = frame.top;
              dragImageView.setScreen_H(window_height - state_height);
              dragImageView.setScreen_W(window_width);
            }
          }
        });
  }

  /**
   * 读取本地资源的图片
   */
  public static Bitmap ReadBitmapById(Context context, int resId) {
    BitmapFactory.Options opt = new BitmapFactory.Options();
    opt.inPreferredConfig = Bitmap.Config.RGB_565;
    opt.inPurgeable = true;
    opt.inInputShareable = true;
    // 获取资源图片
    InputStream is = context.getResources().openRawResource(resId);
    return BitmapFactory.decodeStream(is, null, opt);
  }

  private Bitmap returnBitmap(String url) {
    URL fileUrl = null;
    Bitmap bitmap = null;

    try {
      fileUrl = new URL(url);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    try {
      HttpURLConnection conn = (HttpURLConnection) fileUrl
          .openConnection();
      conn.setDoInput(true);
      conn.connect();
      InputStream is = conn.getInputStream();
      bitmap = BitmapFactory.decodeStream(is);
      is.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bitmap;
  }
}
