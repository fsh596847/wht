package com.xiaowei.android.wht.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.Area;
import com.xiaowei.android.wht.beans.DoctorPerson;
import com.xiaowei.android.wht.beans.MeetingNoticeDoctor;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.HttpUtil;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetingApply extends Activity {

  private EditText etName, etHospital/*,etWorkPermit*/, etExecutePermit;
  private TextView tvSex, tvJobTitle, tvPostTitle, tvArea, etAdministrativeOffice;
  private ToggleButton tBtnStay;
  private Button btnApply;

  private int sex = 1;//0；男；1女
  private Area area;

  private static final int RESULTCODE_JOB_TITLE = 511;
  private static final int RESULTCODE_POST_TITLE = 512;//职务
  private static final int RESULTCODE_AREA_CHOOSE = 515;
  public static final int RESULTCODE_MeetingApply_ApplyOK = 517;

  private String meetid;
  private double mny;//报名费

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_meeting_apply);
    ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
    Intent data = getIntent();
    meetid = data.getStringExtra("meetid");
    mny = data.getDoubleExtra("mny", 0);

    initViews();

    initListeners();

    queryInfoData();

    //右滑退出
    SildingFinishLayout mSildingFinishLayout =
        (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_meeting_apply);
    mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

      @Override
      public void onSildingFinish() {
        MeetingApply.this.finish();
      }
    });
    // touchView
    LinearLayout view = (LinearLayout) findViewById(R.id.ll_meeting_apply);
    mSildingFinishLayout.setTouchView(view);
  }

  private void initViews() {
    etName = (EditText) findViewById(R.id.et_meeting_apply_name);
    etHospital = (EditText) findViewById(R.id.et_meeting_apply_hospital);
    etAdministrativeOffice = (TextView) findViewById(R.id.et_meeting_apply_administrative_office);
    etExecutePermit = (EditText) findViewById(R.id.et_meeting_apply_execute_permit);
    tvSex = (TextView) findViewById(R.id.tv_meeting_apply_sex_value);
    tvJobTitle = (TextView) findViewById(R.id.tv_meeting_apply_job_title);
    tvPostTitle = (TextView) findViewById(R.id.tv_meeting_apply_job);
    tvArea = (TextView) findViewById(R.id.tv_meeting_apply_area);
    tBtnStay = (ToggleButton) findViewById(R.id.tbtn_meeting_apply_stay);
    btnApply = (Button) findViewById(R.id.btn_meeting_apply_pay);
    if (mny <= 0) {
      //btnApply.setText("提交");
    }
  }

  private String username;
  private String hospital;
  private String dept;
  private String doctorno;
  private String jobtitle;
  private String duty;

  private void initListeners() {
    //性别切换
    findViewById(R.id.btn_meeting_apply_sex_cut).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        switch (sex) {
          case 0:
            sex = 1;
            tvSex.setText("女");
            break;

          case 1:
            sex = 0;
            tvSex.setText("男");
            break;
        }
      }
    });

    //选择科室
    findViewById(R.id.rl_meeting_apply_administrative_office).setOnClickListener(
        new OnClickListener() {

          @Override
          public void onClick(View v) {

            startActivityForResult(new Intent(MeetingApply.this, SectionOfficeChooseActivity.class),
                513);
            overridePendingTransition(R.anim.in_right, 0);
          }
        });

    //选择地址
    findViewById(R.id.rl_meeting_apply_area).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        startActivityForResult(new Intent(MeetingApply.this, AreaChooseActivity.class),
            RESULTCODE_AREA_CHOOSE);
        overridePendingTransition(R.anim.in_right, 0);
      }
    });

    //选择职称
    findViewById(R.id.rl_meeting_apply_job_title).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        startActivityForResult(new Intent(MeetingApply.this, JobSelectActivity.class)
            .putExtra("RESULTCODE", RESULTCODE_JOB_TITLE), RESULTCODE_JOB_TITLE);
        overridePendingTransition(R.anim.in_right, 0);
      }
    });

    //选择职务
    findViewById(R.id.rl_meeting_apply_job).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        startActivityForResult(new Intent(MeetingApply.this, JobSelectActivity.class)
            .putExtra("RESULTCODE", RESULTCODE_POST_TITLE), RESULTCODE_POST_TITLE);
        overridePendingTransition(R.anim.in_right, 0);
      }
    });

    //返回
    findViewById(R.id.iv_meeting_apply_back).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        finish();
        overridePendingTransition(0, R.anim.out_right);
      }
    });
    //去支付
    btnApply.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        if (area == null) {
          mToast.showToast(MeetingApply.this, "请选择地区");
          return;
        }
        username = etName.getText().toString().trim();
        if (username.isEmpty()) {
          mToast.showToast(MeetingApply.this, "请输入姓名");
          return;
        }
        hospital = etHospital.getText().toString().trim();
        if (hospital.isEmpty()) {
          mToast.showToast(MeetingApply.this, "请输入所属医院");
          return;
        }
        dept = etAdministrativeOffice.getText().toString().trim();
        if (dept.isEmpty()) {
          mToast.showToast(MeetingApply.this, "请选择科室");
          return;
        }
        doctorno = etExecutePermit.getText().toString().trim();
        if (doctorno.length() != 15) {
          mToast.showToast(MeetingApply.this, "请输入15位医生执业证书编码");
          return;
        }
        jobtitle = tvJobTitle.getText().toString().trim();
        if (jobtitle.isEmpty()) {
          mToast.showToast(MeetingApply.this, "请选择职称");
          return;
        }
        duty = tvPostTitle.getText().toString().trim();
        if (dept.isEmpty()) {
          mToast.showToast(MeetingApply.this, "请选择职务");
          return;
        }
        //去支付之前必须重新更新支付金额
        queryMeetingNotice(1);
      }
    });
  }

  // Press the back button in mobile phone
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(0, R.anim.out_right);
  }

  private void initInfo(DoctorPerson info) {
    String name = info.getDoctorname();
    //姓名
    sex = info.getSex();//0；男；1女
    String hospital = info.getHospital();//所属医院
    String administrativeOffice = info.getDetp();//所属科室
    String address = info.getAddress();
    if (address != null && info.getAreaid() != null) {
      area = new Area();
      area.setAreaname(address);
      area.setId(info.getAreaid());
    }
    String job = info.getJobtitle();//职称
    String post = info.getDuty();//职务
    String executePermit = info.getLicensecard();//执行证
    if (name != null) {
      etName.setText(name);
    }
    switch (sex) {
      case 0:
        tvSex.setText("男");
        break;

      case 1:
        tvSex.setText("女");
        break;
    }
    if (hospital != null) {
      etHospital.setText(hospital);
    }
    if (administrativeOffice != null) {
      etAdministrativeOffice.setText(administrativeOffice);
    }
    if (address != null) {
      tvArea.setText(address);
    }
    if (job != null) {
      tvJobTitle.setText(job);
    }
    if (post != null) {
      tvPostTitle.setText(post);
    }
    if (executePermit != null) {
      etExecutePermit.setText(executePermit);
    }
  }

  private void reload(String text) {
    if (loadingDialog == null) {
      loadingDialog = Utils.createLoadingDialog(MeetingApply.this, text);
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
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    isDestroy = true;
    ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (resultCode) {
      case 513://科室
        String office = data.getStringExtra("result");
        if (office != null) {
          etAdministrativeOffice.setText(office);
        }
        break;
      case RESULTCODE_AREA_CHOOSE://地区
        area = (Area) data.getSerializableExtra("Area");
        if (area != null) {
          tvArea.setText(area.getAreaname());
        }
        break;

      case RESULTCODE_JOB_TITLE://
        String job = data.getStringExtra("result");
        if (job != null) {
          tvJobTitle.setText(job);
        }
        break;

      case RESULTCODE_POST_TITLE:
        String post = data.getStringExtra("result");
        if (post != null) {
          tvPostTitle.setText(post);
        }
        break;

      case RESULTCODE_MeetingApply_ApplyOK:
        Toast.makeText(MeetingApply.this, "报名成功", Toast.LENGTH_SHORT).show();
        setResult(RESULTCODE_MeetingApply_ApplyOK);
        finish();
        break;
    }
  }

  private void meetingApply(final String meetid, final String username, final int sex,
      final String hospital
      , final String dept, final String address, final String areaid, final String doctorno
      , final String jobtitle, final String duty, final boolean isstay) {
    mLog.d("http", "isstay:" + isstay);
    reload("正在努力加载……");
    new Thread(new Runnable() {
      @Override
      public void run() {

        try {
          String s = DataService.meetingApply(MeetingApply.this
              , new SpData(MeetingApply.this).getStringValue(SpData.keyPhoneUser, null), meetid
              , username, sex, hospital, dept, address, areaid, doctorno, jobtitle, duty, isstay);
          mLog.d("http", "s：" + s);
          if (!HlpUtils.isEmpty(s)) {
            final HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              if (hr.isSuccess()) {
                if (!isDestroy) {
                  MeetingNoticeDoctor meeting =
                      JSON.parseObject(hr.getData().toString(), MeetingNoticeDoctor.class);
                  if (mny <= 0) {
                    //不需缴费，跳转成功页面
                    runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                        Toast.makeText(MeetingApply.this, "报名成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULTCODE_MeetingApply_ApplyOK);
                        finish();
                        overridePendingTransition(0, R.anim.out_right);
                      }
                    });
                  } else {
                    //去支付
                    Intent it = new Intent(MeetingApply.this, PayActivity.class);
                    it.putExtra("userId",
                        new SpData(MeetingApply.this).getStringValue(SpData.keyId, null));
                    it.putExtra("orderId", meeting.getOrderid());
                    it.putExtra("mny", String.valueOf(mny));
                    it.putExtra("meetid", meetid);
                    startActivityForResult(it, RESULTCODE_MeetingApply_ApplyOK);
                    overridePendingTransition(R.anim.in_right, 0);
                  }
                }
              } else {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(MeetingApply.this, hr.getData().toString(), Toast.LENGTH_SHORT)
                        .show();
                  }
                });
              }
            }
          } else {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(MeetingApply.this, "请检查网络后重试！", Toast.LENGTH_SHORT).show();
              }
            });
          }
        } catch (Exception he) {
          he.printStackTrace();
        }

        closeLoadingDialog();
      }
    }).start();
  }

  /**
   * 加载医生数据
   */
  private void queryInfoData() {
    reload("正在努力加载……");
    new Thread(new Runnable() {
      @Override
      public void run() {

        try {
          String mobile = new SpData(MeetingApply.this).getStringValue(SpData.keyPhoneUser, null);
          Map<String, String> map = new HashMap<String, String>();
          map.put("mobile", mobile);
          String s = HttpUtil.postUrl(MeetingApply.this, Config.queryDoctorPerson, map);
          mLog.d("http", "s:" + s);
          if (!HlpUtils.isEmpty(s)) {
            final HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              if (hr.isSuccess()) {
                if (!isDestroy) {
                  final DoctorPerson doctorPerson =
                      JSON.parseObject(hr.getData().toString(), DoctorPerson.class);
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      initInfo(doctorPerson);
                    }
                  });
                }
              } else {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(MeetingApply.this, hr.getData().toString(), Toast.LENGTH_SHORT)
                        .show();
                  }
                });
              }
            }
          } else {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(MeetingApply.this, "请检查网络后重试！", Toast.LENGTH_SHORT).show();
              }
            });
          }
        } catch (Exception he) {
          he.printStackTrace();
        }

        closeLoadingDialog();
      }
    }).start();
  }

  //分页刷新
  private int p;
  private int page;
  private List<MeetingNoticeDoctor> listMeeting = new ArrayList<>();

  /**
   * @param type 1，正在进行；2，会议预报；3，往期回顾
   */
  private void queryMeetingNotice(final int type) {

    p = 1;
    page = 15;
    listMeeting.clear();

    reload("正在努力加载……");
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          SpData spData = new SpData(MeetingApply.this);
          String s = DataService.queryMeetingNotice(MeetingApply.this, type,
              spData.getStringValue(SpData.keyId, null)
              , spData.getStringValue(SpData.keyPhoneUser, null), p, page);
          mLog.w("http", "s：" + s);
          if (!isDestroy && !HlpUtils.isEmpty(s)) {
            HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              if (hr.isSuccess()) {

                listMeeting.addAll(
                    JSON.parseArray(hr.getData().toString(), MeetingNoticeDoctor.class));
                if (listMeeting != null && listMeeting.size() > 0) {
                  mny = listMeeting.get(0).getMny();
                }
                MeetingApply.this.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    if (mny > 0) {
                      meetingApply(meetid, username, sex, hospital, dept, area.getAreaname(),
                          area.getId(),
                          doctorno
                          , jobtitle, duty, tBtnStay.isChecked());
                    }
                  }
                });
              }
            }
          }
        } catch (Exception he) {
          he.printStackTrace();
        }
        closeLoadingDialog();
      }
    }).start();
  }
}
