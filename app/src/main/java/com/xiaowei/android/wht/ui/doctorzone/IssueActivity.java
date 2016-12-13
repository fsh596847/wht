package com.xiaowei.android.wht.ui.doctorzone;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.views.AlertDialog;
import com.xiaowei.android.wht.views.Html5WebView;
import com.xiaowei.android.wht.views.TextFont;
import java.io.File;
import java.util.List;

import static com.xiaowei.android.wht.ui.doctorzone.DoctorZoneActivity.INTENT_KEY_TYPE_ISSUE;

/**
 * Created by HIPAA on 2016/11/29. 发布
 */
public class IssueActivity extends BaseActivity implements Html5WebView.WebCall {
  private WebView mWebView;
  private LinearLayout mLayout;
  private String mUrl;
  private TextFont tvTitle;
  public static String INTENT_KEY_CASE = "casetype";
  private String intentKeyValue;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_issue);
  }

  @Override public void init(Bundle savedInstanceState) {
    mLayout = (LinearLayout) findViewById(R.id.web_layout);
    tvTitle = (TextFont) findViewById(R.id.tv_title);
    tvTitle.setText("填写病例");
    mWebView = new Html5WebView(activity);
    String userid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
            .MATCH_PARENT);
    mWebView.setLayoutParams(params);
    mLayout.addView(mWebView);
    intentKeyValue = getIntent().getStringExtra(INTENT_KEY_CASE);
    mUrl = Config.issueCase.replace("{USID}", userid)
        .replace("{TYPE}", intentKeyValue);
    Log.d(IssueActivity.class.getSimpleName(), mUrl);
    ((Html5WebView) mWebView).setCallBack(this);
    mWebView.loadUrl(mUrl);
    //mWebView.loadUrl("file:///android_asset/text2.html");

  }

  @Override public void setListener() {

  }

  public static void getIntent(Context context, String key) {
    Intent intent = new Intent(context, IssueActivity.class);
    intent.putExtra(INTENT_KEY_CASE, key);
    context.startActivity(intent);
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  public void submitClick(View view) {
    showDialog();
  }

  private void submit() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        mWebView.evaluateJavascript("fsubmit()", new ValueCallback<String>() {
          @Override
          public void onReceiveValue(final String value) {
            mWebView.post(new Runnable() {
              @Override
              public void run() {
                Log.e(IssueActivity.class.getSimpleName(), "onReceiveValue value=" + value);
                if (value.equals("1")) {
                  if (getIntent().getStringExtra(INTENT_KEY_CASE).equals(INTENT_KEY_TYPE_ISSUE)) {
                    //IssueDetailActivity.getIntent(activity, INTENT_KEY_TYPE_ISSUE);
                    DoctorZoneActivity.CallIntent(activity,
                        DoctorZoneActivity.INTENT_KEY_TYPE_ISSUE);
                  } else {
                    //IssueDetailActivity.getIntent(activity, INTENT_KEY_TYPE_SHARE);
                    DoctorZoneActivity.CallIntent(activity,
                        DoctorZoneActivity.INTENT_KEY_TYPE_SHARE);
                  }
                  finish();
                }
              }
            });
          }
        });
      } else {
        mWebView.post(new Runnable() {
          @Override
          public void run() {
            mWebView.loadUrl("javascript:fsubmit()");
            if (getIntent().getStringExtra(INTENT_KEY_CASE).equals(INTENT_KEY_TYPE_ISSUE)) {
              //IssueDetailActivity.getIntent(activity, INTENT_KEY_TYPE_ISSUE);
              DoctorZoneActivity.CallIntent(activity, DoctorZoneActivity.INTENT_KEY_TYPE_ISSUE);
            } else {
              //IssueDetailActivity.getIntent(activity, INTENT_KEY_TYPE_SHARE);
              DoctorZoneActivity.CallIntent(activity, DoctorZoneActivity.INTENT_KEY_TYPE_SHARE);
            }
            finish();
          }
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
      Log.d(IssueActivity.class.getSimpleName(), e.getMessage());
    }
  }

  public void backClick(View view) {
    finish();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {

      showDialogFinsih();

      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  public void showDialog() {
    AlertDialog alertDialog = new AlertDialog(activity).builder();
    alertDialog.setTitle("温馨提示");
    alertDialog.setMsg("确认发布病例？");
    alertDialog.setMultiActionTextView(getString(R.string.select_pay_ail), 9, 11);
    alertDialog.setPositiveButton(getString(R.string.text_custom_view_btn_positive),
        new View.OnClickListener() {
          @Override public void onClick(View v) {
            submit();
          }
        });
    alertDialog.setNegativeButton(getString(R.string.text_custom_view_btn_negative)).show();
  }

  public void showDialogFinsih() {
    AlertDialog alertDialog = new AlertDialog(activity).builder();
    alertDialog.setTitle("温馨提示");
    alertDialog.setMsg("确认放弃本次病例编辑？");
    alertDialog.setMultiActionTextView(getString(R.string.select_pay_ail), 9, 11);
    alertDialog.setPositiveButton(getString(R.string.text_custom_view_btn_positive),
        new View.OnClickListener() {
          @Override public void onClick(View v) {
            IssueActivity.this.finish();
          }
        });
    alertDialog.setNegativeButton(getString(R.string.text_custom_view_btn_negative)).show();
  }

  @Override
  protected void onDestroy() {
    if (mWebView != null) {
      mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
      mWebView.clearHistory();

      ((ViewGroup) mWebView.getParent()).removeView(mWebView);
      mWebView.destroy();
      mWebView = null;
    }
    super.onDestroy();
  }

  private static final int FILE_SELECT_CODE = 0;
  private ValueCallback<Uri> mUploadMessage;//回调图片选择，4.4以下
  private ValueCallback<Uri[]> mUploadCallbackAboveL;//回调图片选择，5.0以上
  private static final int REQ_CAMERA = FILE_SELECT_CODE + 1;

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) {
      if (Build.VERSION.SDK_INT >= 21) {//5.0以上版本处理
        mUploadCallbackAboveL.onReceiveValue(null);
        mUploadCallbackAboveL = null;
      } else {
        mUploadMessage.onReceiveValue(null);
        mUploadMessage = null;
      }
      mWebView.requestFocus();
      return;
    }

    switch (requestCode) {
      case FILE_SELECT_CODE: {
        if (Build.VERSION.SDK_INT >= 21) {//5.0以上版本处理
          Uri uri = data.getData();
          Uri[] uris = new Uri[] {uri};
                   /* ClipData clipData = data.getClipData();  //选择多张
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            uris[i]=uri;
                        }
                    }*/
          mUploadCallbackAboveL.onReceiveValue(uris);//回调给js
          mUploadCallbackAboveL = null;
        } else {//4.4以下处理
          Uri uri = data.getData();
          if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(uri);
            mUploadMessage = null;
          }
        }
      }
      break;
      case REQ_CAMERA:
        afterOpenCamera();
        Uri uri = cameraUri;
        if (mUploadCallbackAboveL != null) {
          Uri[] uris = new Uri[1];
          uris[0] = uri;
          mUploadCallbackAboveL.onReceiveValue(uris);
        } else {
          mUploadMessage.onReceiveValue(uri);
        }
        mUploadCallbackAboveL = null;
        mUploadMessage = null;
        break;
    }
  }

  /**
   * 拍照结束后
   */
  private void afterOpenCamera() {
    File f = new File(imagePaths);
    addImageGallery(f);
  }

  /** 解决拍照后在相册中找不到的问题 */
  private void addImageGallery(File file) {
    ContentValues values = new ContentValues();
    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
    getContentResolver().insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
  }

  private void phone(String type) {
    //mUploadMessage = uploadMsg;
    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType(type);
    startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_SELECT_CODE);
  }

  @Override public void fileChose3(ValueCallback<Uri> uploadMsg) {
    mUploadMessage = uploadMsg;
    //phone(uploadMsg,"image/*");
  }

  @Override public void fileChose3(ValueCallback<Uri> uploadMsg, String acceptType) {
    mUploadMessage = uploadMsg;
    //phone(uploadMsg,"*/*");
  }

  @Override public void fileChose4(ValueCallback<Uri> uploadMsg) {
    mUploadMessage = uploadMsg;
    selectImage();
    //Intent i = new Intent(Intent.ACTION_GET_CONTENT);
    //i.addCategory(Intent.CATEGORY_OPENABLE);
    //i.setType("image/*");
    //startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_SELECT_CODE);
  }

  @Override public void fileChose5(ValueCallback<Uri[]> filePathCallback) {
    mUploadCallbackAboveL = filePathCallback;
    selectImage();

    //Intent i = new Intent(Intent.ACTION_GET_CONTENT);
    //i.addCategory(Intent.CATEGORY_OPENABLE);
    //i.setType("*/*");
    //startActivityForResult(
    //    Intent.createChooser(i, "File Browser"),
    //    FILE_SELECT_CODE);
  }

  private void phone5(String type) {
    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType(type);
    startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_SELECT_CODE);
  }

  /**
   * 检查SD卡是否存在
   */
  public final boolean checkSDcard() {
    boolean flag = Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED);
    if (!flag) {
      Toast.makeText(this, "请插入手机存储卡再使用本功能", Toast.LENGTH_SHORT).show();
    }
    return flag;
  }

  private void permission() {
    Acp.getInstance(this).request(new AcpOptions.Builder()
            .setPermissions(Manifest.permission.CAMERA

            )
            .build(),
        new AcpListener() {
          @Override
          public void onGranted() {
            openCarcme();
          }

          @Override
          public void onDenied(List<String> permissions) {

          }
        });
  }

  String compressPath = "";

  protected final void selectImage() {
    if (!checkSDcard()) {
      return;
    }
    String[] selectPicTypeStr = {"照相机", "相册"};
    android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this)
        .setItems(selectPicTypeStr,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,
                  int which) {
                switch (which) {
                  // 相机拍摄
                  case 0:
                    permission();
                    break;
                  // 手机相册
                  case 1:
                    if (Build.VERSION.SDK_INT >= 21) {
                      phone5("*/*");
                    } else {
                      Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                      i.addCategory(Intent.CATEGORY_OPENABLE);
                      i.setType("image/*");
                      startActivityForResult(Intent.createChooser(i, "File Chooser"),
                          FILE_SELECT_CODE);
                    }
                    break;
                  default:
                    break;
                }
                compressPath = Environment
                    .getExternalStorageDirectory()
                    .getPath()
                    + "/wht";
                new File(compressPath).mkdirs();
                compressPath = compressPath + File.separator
                    + "compress.jpg";
              }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialogInterface) {
            if (mUploadCallbackAboveL != null) {
              Uri[] uris = new Uri[1];
              uris[0] = Uri.parse("");
              mUploadCallbackAboveL.onReceiveValue(uris);
              mUploadCallbackAboveL = null;
            } else {
              mUploadMessage.onReceiveValue(Uri.parse(""));
              mUploadMessage = null;
            }
          }
        }).show();
  }

  String imagePaths;
  Uri cameraUri;

  /**
   * 打开照相机
   */
  private void openCarcme() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    imagePaths = Environment.getExternalStorageDirectory().getPath()
        + "/wht/"
        + (System.currentTimeMillis() + ".jpg");
    // 必须确保文件夹路径存在，否则拍照后无法完成回调
    File vFile = new File(imagePaths);
    if (!vFile.exists()) {
      File vDirPath = vFile.getParentFile();
      vDirPath.mkdirs();
    } else {
      if (vFile.exists()) {
        vFile.delete();
      }
    }
    cameraUri = Uri.fromFile(vFile);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
    startActivityForResult(intent, REQ_CAMERA);
  }
}
