package com.xiaowei.android.wht.ui.patient;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.hillpool.LruImageCache;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.model.PatientInfo;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.service.DataService4Patient;
import com.xiaowei.android.wht.ui.ChooseWindowActivity;
import com.xiaowei.android.wht.ui.RegisterActivity;
import com.xiaowei.android.wht.ui.WebAboutUsActivity;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;

public class FragmentPatientMy extends Fragment implements View.OnClickListener {

  //	private DoctorPerson doctorPerson;
  PatientInfo patientInfo;
  private CircularImage head_imageView;//头像
  private TextView name_textView, ver_textView, exitApp_textView, tvMsg;//医生姓名
  ImageView phone_imageView;
  RelativeLayout transRole_div, myInfo_div, myMessage_div, about_div, money_div;

  private RelativeLayout lytDocoor;
  private RelativeLayout lytCase;
  RequestQueue mQueue = null;
  LruImageCache lruImageCache = null;
  ImageLoader imageLoader = null;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.patient_fragment_my, container, false);

    initCache();
    initViews(view);
    initData();
    return view;
  }

  private void initViews(View v) {
    lytCase = (RelativeLayout) v.findViewById(R.id.lyt_case);
    lytDocoor = (RelativeLayout) v.findViewById(R.id.lyt_doctor);
    tvMsg = (TextView) v.findViewById(R.id.tv_patient_my_message);
    head_imageView = (CircularImage) v.findViewById(R.id.headPic_imageView);
    exitApp_textView = (TextView) v.findViewById(R.id.exitApp_textView);
    exitApp_textView.setOnClickListener(this);
    name_textView = (TextView) v.findViewById(R.id.name_textView);
    ver_textView = (TextView) v.findViewById(R.id.ver_textView);
    transRole_div = (RelativeLayout) v.findViewById(R.id.transRole_div);
    about_div = (RelativeLayout) v.findViewById(R.id.about_div);
    money_div = (RelativeLayout) v.findViewById(R.id.money_div);
    transRole_div.setOnClickListener(this);
    about_div.setOnClickListener(this);
    money_div.setOnClickListener(this);
    myInfo_div = (RelativeLayout) v.findViewById(R.id.myInfo_div);
    myMessage_div = (RelativeLayout) v.findViewById(R.id.myMessage_div);
    myInfo_div.setOnClickListener(this);
    myMessage_div.setOnClickListener(this);
    ver_textView.setText("当前版本：" + Utils.getVersionName(getActivity()));

    phone_imageView = (ImageView) v.findViewById(R.id.phone_imageView);
    phone_imageView.setOnClickListener(this);
    lytCase.setOnClickListener(this);
    lytDocoor.setOnClickListener(this);
  }

  private void initData() {
    queryInitData();
  }

  private void initCache() {

    mQueue = Volley.newRequestQueue(getActivity());
    lruImageCache = LruImageCache.instance();
    imageLoader = new ImageLoader(mQueue, lruImageCache);
  }

  ImageListener imageListener = new ImageListener() {

    @Override
    public void onErrorResponse(VolleyError arg0) {
      System.out.print(arg0.getMessage());
    }

    @Override
    public void onResponse(ImageContainer arg0, boolean arg1) {
      Bitmap bmp = arg0.getBitmap();
      if (bmp != null) {
        //				bmp = HlpUtils.getCroppedBitmap(bmp, HlpUtils.dip2px(PatientDoctorInfoActivity.this, 60));
        head_imageView.setImageBitmap(bmp);
      }
    }
  };

  private void loadLogo(ImageView iv, String url, int defaultImageId) {
    if (defaultImageId != 0) {
      iv.setImageResource(defaultImageId);
    }
    imageLoader.get(url, imageListener);
  }

  final int msgQueryOk = 1001;
  final int msgQueryFail = 1002;
  @SuppressLint("HandlerLeak")
  Handler handler = new Handler() {
    public void handleMessage(android.os.Message msg) {
      switch (msg.what) {
        case msgQueryOk:
          displayInfo();
          break;
        case msgQueryFail:
          String s = (String) msg.obj;
          HlpUtils.showToast(getActivity(), s);
          break;
      }
    }
  };

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (resultCode) {
      case 611:
        boolean isModify = data.getBooleanExtra("isModify", false);
        if (isModify) { //个人信息被修改过重新加载
          reload("正在努力加载……");
          new Thread(new Runnable() {
            @Override
            public void run() {

              //查询医生信息
              //						queryDoctorPerson();
              closeLoadingDialog();
            }
          }).start();
        }

        break;

      case 200:
        queryInitData();
        break;
    }
  }

  protected void displayInfo() {
    if (patientInfo != null) {
      name_textView.setText(patientInfo.getUsername());
      if (!HlpUtils.isEmpty(patientInfo.getHeadimg())) {
        loadLogo(head_imageView, patientInfo.getHeadimg(), R.drawable.ic_head);
      }
    }
  }

  /**
   * 加载医生数据
   */
  private void queryInitData() {
    reload("正在努力加载……");
    new Thread(new Runnable() {
      @Override
      public void run() {

        queryMyInfo();
        closeLoadingDialog();
      }
    }).start();
  }

  public void queryNotReadNoctice() {
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          String s = DataService.queryNotReadNoctice(getActivity()
              , new SpData(getActivity()).getStringValue(SpData.keyPhoneUser, null)
              , "PATIENT");
          mLog.d("http", "s:" + s);
          if (!HlpUtils.isEmpty(s)) {
            final HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              final int totalRows = hr.getTotalRows();
              if (totalRows > 0) {
                getActivity().runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    tvMsg.setText("未读消息" + (totalRows > 99 ? "99+" : totalRows + "") + "条");
                  }
                });
              } else {
                getActivity().runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    tvMsg.setText("");
                  }
                });
              }
            }
          }
        } catch (Exception he) {
          he.printStackTrace();
        }
      }
    }).start();
  }

  /**
   * 查询自己的信息
   */
  private void queryMyInfo() {
    try {
      SpData sp = new SpData(getActivity());
      String mobile = sp.getStringValue(SpData.keyPhoneUser, null);
      String id = sp.getStringValue(SpData.keyId, null);

      String s = DataService4Patient.getPatientInfo(getActivity(), id, mobile);
      if (!HlpUtils.isEmpty(s)) {
        final HttpResult hr = JSON.parseObject(s, HttpResult.class);
        if (hr != null && hr.isSuccess()) {
          patientInfo = JSON.parseObject(hr.getData().toString(), PatientInfo.class);
          handler.obtainMessage(msgQueryOk).sendToTarget();
        }
      } else {
      }
    } catch (Exception he) {
      he.printStackTrace();
    }
  }

  private void reload(String text) {
    if (loadingDialog == null) {
      loadingDialog = Utils.createLoadingDialog(getActivity(), text);
    }
    if (!loadingDialog.isShowing()) {
      loadingDialog.show();
    }
  }

  private Dialog loadingDialog = null;

  private void closeLoadingDialog() {
    if (null != loadingDialog) {
      loadingDialog.dismiss();
      loadingDialog = null;
    }
  }

  boolean isDestroy = false;

  @Override
  public void onStart() {
    super.onStart();
    isDestroy = false;
    queryNotReadNoctice();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    isDestroy = true;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.exitApp_textView:
        exitApp();
        break;
      case R.id.transRole_div:
        transRole();
        break;
      case R.id.myInfo_div:
        toMyInfo();
        break;
      case R.id.myMessage_div:
        startActivityForResult(new Intent(getActivity(), PatientMyMessageActivity.class), 10);
        getActivity().overridePendingTransition(R.anim.in_right, 0);
        break;
      case R.id.phone_imageView:
        callService();
        break;
      case R.id.about_div:
        startActivity(new Intent(getActivity(), WebAboutUsActivity.class));
        getActivity().overridePendingTransition(R.anim.in_right, 0);
        break;
      case R.id.money_div:
        startActivity(new Intent(getActivity(), PayDetailsActivity.class));
        getActivity().overridePendingTransition(R.anim.in_right, 0);
        break;
      case R.id.lyt_case:
        startActivity(new Intent(getActivity(), MyCaseActivity.class));
        break;
      case R.id.lyt_doctor:
        startActivity(new Intent(getActivity(), PatientMyDoctorActivity.class));
        break;
    }
  }

  public void myDoctorClick(View view) {
    startActivity(new Intent(getActivity(), PatientMyDoctorActivity.class));
    getActivity().overridePendingTransition(R.anim.in_right, 0);
  }

  public void caseClick(View view) {
    startActivity(new Intent(getActivity(), MyCaseActivity.class));
    getActivity().overridePendingTransition(R.anim.in_right, 0);
  }

  private void callService() {
    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-67681788"));
    startActivity(intent);
  }

  private void toMyInfo() {
    Intent it = new Intent(getActivity(), PatientMyInfoActivity.class);
    it.putExtra("patientInfo", patientInfo);
    startActivityForResult(it, 200);
    getActivity().overridePendingTransition(R.anim.in_right, 0);
  }

  private void transRole() {
    SpData sp = new SpData(getActivity());
    sp.setStringValue(SpData.KeyClientType, "" + RegisterActivity.clientTypeDoctor);
    Intent it = new Intent(getActivity(), ChooseWindowActivity.class);
    startActivity(it);
    getActivity().finish();
  }

  private void exitApp() {
    //信鸽推送反注册
    ApplicationTool.getInstance().stopPushService();
    //PopupDialog p = new PopupDialog();
    Intent it = new Intent(getActivity(), ChooseWindowActivity.class);
    startActivity(it);
    getActivity().finish();
  }
}
