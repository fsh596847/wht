package com.xiaowei.android.wht.ui.doctorzone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.ui.doctorzone.adapter.PhotoAdapter;
import com.xiaowei.android.wht.ui.doctorzone.adapter.RecyclerItemClickListener;
import com.xiaowei.android.wht.ui.doctorzone.net.ApiService;
import com.xiaowei.android.wht.ui.doctorzone.net.ProgressSubscriber;
import com.xiaowei.android.wht.ui.doctorzone.net.RestClient;
import com.xiaowei.android.wht.ui.doctorzone.net.SubscriberOnNextListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by HIPAA on 2016/11/21.
 */

public class UploadImageActivity extends BaseActivity {

  private RecyclerView mRecyclerView;
  private ArrayList<String> mSelectedPhotos = new ArrayList<>();
  private PhotoAdapter photoAdapter;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_uploadimage);
  }

  @Override public void init(Bundle savedInstanceState) {
    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    photoAdapter = new PhotoAdapter(this, mSelectedPhotos);
    mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
    mRecyclerView.setAdapter(photoAdapter);
  }

  @Override public void setListener() {

    mRecyclerView.addOnItemTouchListener(
        new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
          @Override public void onItemClick(View view, int position) {
            if (mSelectedPhotos.size() > 0) {
              if (position == mSelectedPhotos.size()) {
                PhotoPicker.builder().setPhotoCount(9).setGridColumnCount(3).//
                    setSelected(mSelectedPhotos).start(UploadImageActivity.this);
              } else {
                PhotoPreview.builder().setPhotos(mSelectedPhotos).//
                    setCurrentItem(position).start(UploadImageActivity.this);
              }
            } else {
              PhotoPicker.builder().setPhotoCount(9).setGridColumnCount(3)//
                  .setSelected(mSelectedPhotos).start(UploadImageActivity.this);
            }
          }
        }));
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (data == null) {
      return;
    }
    if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE
        || requestCode == PhotoPreview.REQUEST_CODE)) {

      List<String> photos = null;
      if (data != null) {
        photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
      }
      mSelectedPhotos.clear();
      if (photos != null) {
        mSelectedPhotos.addAll(photos);
      }
      photoAdapter.notifyDataSetChanged();
    }
  }

  public void submitClick(View view) {
    upload();
  }

  /**
   * 上传图片
   */
  private void upload() {

    ApiService service = RestClient.createService(ApiService.class);
    Map<String, RequestBody> map = new HashMap<>();
    MediaType imgMediaType = MediaType.parse("image/*");
    if (mSelectedPhotos.size() > 0) {
      for (int i = 0; i < mSelectedPhotos.size(); i++) {
        File file = new File(mSelectedPhotos.get(i));
        map.put("files" + i + "\"; filename=\"" + file.getName(),
            RequestBody.create(imgMediaType, file));
      }
    }
    if (map.size() > 0) {
      service.uploadImage(map)
          .subscribeOn(Schedulers.io())
          .unsubscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new ProgressSubscriber(new SubscriberOnNextListener() {
            @Override public void onNext(String t) {
              Log.d(UploadImageActivity.class.getSimpleName(), "UploadImageActivity:onNext" + t);
            }

            @Override public void onError(String t) {
              Log.d(UploadImageActivity.class.getSimpleName(), "UploadImageActivity:onError" + t);
            }
          }, this, true, true));
    }
  }

  /**
   * 压缩图片（质量压缩）
   */

  public static File compressImage(Bitmap image) {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
    int options = 100;

    while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
      baos.reset();//重置baos即清空baos
      options -= 10;//每次都减少10
      image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
      long length = baos.toByteArray().length;
    }
    //        long length = baos.toByteArray().length;
    //        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
    //        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片

    File file = new File(Environment.getExternalStorageDirectory() + "/temp.png");
    try {
      FileOutputStream fos = new FileOutputStream(file);
      try {
        fos.write(baos.toByteArray());
        fos.flush();
        fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return file;
  }

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
}
