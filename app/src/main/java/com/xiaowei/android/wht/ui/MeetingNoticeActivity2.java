package com.xiaowei.android.wht.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.Config;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.MeetingNoticeDoctor;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utis.Utils;

public class MeetingNoticeActivity2 extends FragmentActivity {

  private MeetingNoticeDoctor homeImg;
  private ImageView imageView;
  private TextView tvMeetingName1, tvMeetingName2;
  double mny;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    //requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_doctor_meeting_notice2);
    ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
    mny = getIntent().getDoubleExtra("mny", 0);
    homeImg = (MeetingNoticeDoctor) getIntent().getSerializableExtra("data");
    if (homeImg == null) {
      finish();
      return;
    }

    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    RelativeLayout ad_div = (RelativeLayout) findViewById(R.id.rl_doc_meeting_notice_meeting);
    //广告图的大小是16（长）:9（宽），所以做此调整
    LinearLayout.LayoutParams layout =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            9 * dm.widthPixels / 16);
    ad_div.setLayoutParams(layout);
    imageView = (ImageView) findViewById(R.id.ImageView_doc_meeting_notice);
    tvMeetingName1 = (TextView) findViewById(R.id.textView_doc_meeting_notice_name1);
    tvMeetingName2 = (TextView) findViewById(R.id.textView_doc_meeting_notice_name2);
    tvMeetingName1.setText(homeImg.getMeetname());
    tvMeetingName2.setText(homeImg.getMeetname());

    //返回
    findViewById(R.id.iv_doc_meeting_notice_back).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
        //overridePendingTransition(0, R.anim.out_right);
      }
    });
    //欢迎辞  1
    findViewById(R.id.btn_doc_meeting_notice_welcome).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity("欢迎辞", Config.getMeetWelcomeWord);
      }
    });
    //基本信息 2
    findViewById(R.id.btn_doc_meeting_notice_info).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity("基本信息", Config.getMeetBaseInfo);
      }
    });
    //组织结构 3
    findViewById(R.id.btn_doc_meeting_notice_org).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity("组织结构", Config.getMeetOrganizationals);
      }
    });
    //会议内容 4
    findViewById(R.id.btn_doc_meeting_notice_content).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity("会议内容", Config.getMeeting);
      }
    });
    //日程查询 5
    findViewById(R.id.btn_doc_meeting_notice_calendar).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity("日程查询", Config.getMeetSchedulet);
      }
    });
    //讲者主持查询 6
    findViewById(R.id.btn_doc_meeting_notice_presenter).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity("讲者主持查询", Config.getMeetSpeaker);
      }
    });
    //大会PPT视频 7
    findViewById(R.id.btn_doc_meeting_notice_video).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity("大会PPT视频", Config.getMeetResource);
      }
    });
    //关于我们 8
    findViewById(R.id.btn_doc_meeting_notice_us).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity("联系我们", Config.getMeetContact);
      }
    });
    //个人中心 9
    findViewById(R.id.btn_doc_meeting_notice_personage).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        setResult(MeetingApply.RESULTCODE_MeetingApply_ApplyOK,
            new Intent().putExtra("personage", true));
        finish();
        //overridePendingTransition(0, R.anim.out_right);
      }
    });

    //右滑退出
    /*SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_meeting_notice);
    mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

			@Override
			public void onSildingFinish() {
				MeetingNoticeActivity2.this.finish();
			}
		});
		// touchView
		ScrollView view = (ScrollView) findViewById(R.id.scrollView_doc_meeting_notice);
		mSildingFinishLayout.setTouchView(view);*/

    getImageInfo();
  }

  /**
   * 67              7 功能使用权限 登录 1 (1234589)  注册 2(12345789)   验证 3(1-9) 只在67做判断就行了
   */
  private void authority(int auth) {

  }

  protected void startActivity(String title, String url) {
    if (homeImg == null) {
      getImageInfo();
      return;
    }
    SpData sp = new SpData(getApplicationContext());
    String phone = sp.getStringValue(SpData.keyPhoneUser, null);
    String id = sp.getStringValue(SpData.keyId, null);
    startActivityForResult(new Intent(MeetingNoticeActivity2.this, WebHealthActivity.class)
            .putExtra("title", title)
            .putExtra("mny", mny)
            .putExtra("meetid", homeImg.getId())
            .putExtra("url", url + "?meetid=" + homeImg.getId() + "&mobile=" + phone + "&userid=" + id)
        , MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
    overridePendingTransition(R.anim.in_right, 0);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (resultCode) {
      case MeetingApply.RESULTCODE_MeetingApply_ApplyOK:
        //setResult(MeetingApply.RESULTCODE_MeetingApply_ApplyOK);
        finish();
        break;
    }
  }

  private void getImageInfo() {
    reload("正在努力加载……");
    new Thread(new Runnable() {

      @Override
      public void run() {

        try {
          /*String ss = DataService.getMainmeetimg(MeetingNoticeActivity2.this);
					mLog.d("http", "ss："+ss);
					if (!HlpUtils.isEmpty(ss)){
						HttpResult hr = JSON.parseObject(ss,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								homeImg = JSON.parseObject(hr.getData().toString(), HomeImg.class);
								if(homeImg != null){
									byte[] data = DataService.getImage(homeImg.getFilename());
									if(data!=null){ 
										Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap 
										if(!isDestroy && mBitmap != null){
											final BitmapDrawable b = new BitmapDrawable(getResources(), mBitmap);
											runOnUiThread(new Runnable() {
												@SuppressLint("NewApi")
												public void run() {
													imageView.setBackground(b);
													tvMeetingName1.setText(homeImg.getMeetname());
													tvMeetingName2.setText(homeImg.getMeetname());
												}
											});
										}
									}
								}
							}else{
							}
						}else{
						}
					}else{
					}*/
          if (homeImg != null) {
            /*List<MeetingImg> meetingImgs = homeImg.getMeetingImgs();
						String img;
						if(meetingImgs != null && meetingImgs.size()>0){
							img = meetingImgs.get(0).getImg();
						}else{
							img = homeImg.getImgUrl();
						}*/
            byte[] data = DataService.getImage(homeImg.getFilename());
            if (data != null) {
              Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
              if (!isDestroy && mBitmap != null) {
                final BitmapDrawable b = new BitmapDrawable(getResources(), mBitmap);
                runOnUiThread(new Runnable() {
                  @SuppressLint("NewApi")
                  public void run() {
                    imageView.setBackground(b);
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

  private void reload(String text) {
    if (loadingDialog == null) {
      loadingDialog = Utils.createLoadingDialog(MeetingNoticeActivity2.this, text);
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

  // Press the back button in mobile phone
  /*@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.out_right);
	}*/

  @Override
  protected void onDestroy() {
    super.onDestroy();
    isDestroy = true;
    ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
  }

  boolean isDestroy = false;
}
