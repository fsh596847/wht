package com.xiaowei.android.wht.ui.doctorzone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.ui.doctorzone.adapter.PhotoAdapter;
import com.xiaowei.android.wht.ui.doctorzone.adapter.RecyclerItemClickListener;
import java.util.ArrayList;
import java.util.List;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

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
}
