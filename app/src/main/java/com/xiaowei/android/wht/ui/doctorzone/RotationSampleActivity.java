/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package com.xiaowei.android.wht.ui.doctorzone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xiaowei.android.wht.utis.ImageLoadProxy;
import uk.co.senab.photoview.PhotoView;

public class RotationSampleActivity extends BaseActivity {
  private String mUrl;
  private PhotoView photo;
  private final Handler handler = new Handler();
  private boolean rotating = false;
  private static String INTENT_KEY_URL = "kEY_URL";

  @Override protected void setContentView() {
    photo = new PhotoView(this);
    setContentView(photo);
  }

  public static void getIntent(Context context, String url) {
    Intent intent = new Intent(context, RotationSampleActivity.class);
    intent.putExtra(INTENT_KEY_URL, url);
    context.startActivity(intent);
  }

  @Override public void init(Bundle savedInstanceState) {
    mUrl = getIntent().getStringExtra(INTENT_KEY_URL);
    if (!TextUtils.isEmpty(mUrl)) {
      ImageLoadProxy.displayImage4Detail(mUrl, photo, new SimpleImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {
          // Empty implementation
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
          // Empty implementation
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
          // Empty implementation
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
          // Empty implementation
        }
      });
    }

  }

  @Override public void setListener() {

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(Menu.NONE, 0, Menu.NONE, "Rotate 10° Right");
    menu.add(Menu.NONE, 1, Menu.NONE, "Rotate 10° Left");
    menu.add(Menu.NONE, 2, Menu.NONE, "Toggle automatic rotation");
    menu.add(Menu.NONE, 3, Menu.NONE, "Reset to 0");
    menu.add(Menu.NONE, 4, Menu.NONE, "Reset to 90");
    menu.add(Menu.NONE, 5, Menu.NONE, "Reset to 180");
    menu.add(Menu.NONE, 6, Menu.NONE, "Reset to 270");
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected void onPause() {
    super.onPause();
    handler.removeCallbacksAndMessages(null);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case 0:
        photo.setRotationBy(10);
        return true;
      case 1:
        photo.setRotationBy(-10);
        return true;
      case 2:
        toggleRotation();
        return true;
      case 3:
        photo.setRotationTo(0);
        return true;
      case 4:
        photo.setRotationTo(90);
        return true;
      case 5:
        photo.setRotationTo(180);
        return true;
      case 6:
        photo.setRotationTo(270);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void toggleRotation() {
    if (rotating) {
      handler.removeCallbacksAndMessages(null);
    } else {
      rotateLoop();
    }
    rotating = !rotating;
  }

  private void rotateLoop() {
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        photo.setRotationBy(1);
        rotateLoop();
      }
    }, 15);
  }
}
