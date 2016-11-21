package com.xiaowei.android.wht.ui.doctorzone;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.ui.doctorzone.able.IWxShare;
import com.xiaowei.android.wht.ui.doctorzone.able.WxShare;
import com.xiaowei.android.wht.views.SharePopupwindow;

/*
*  Created by fsh on 2016/11/12.  医生社区
* */
public class DoctorZoneActivity extends BaseActivity implements View.OnClickListener {

    DoctorTalkFragment fragment_doctor;
    DoctorZoneFragment fragment_history;
    private TextView tv_zone;
    private TextView tv_talk;

    private FragmentManager fragmentManager;
    FragmentTransaction transaction;

    private SharePopupwindow popup;
    private LinearLayout viewParent;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_doctor_zone);
        ApplicationTool.getInstance().activitis.add(this);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initViews();
        fragmentManager = this.getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        setTabSelection(0);

        popup = new SharePopupwindow(this);
        popup.setOutsideTouchable(true);
        popup.setCallBack(new SharePopupwindow.CallBack() {

            @Override
            public void group() {
                IWxShare iWxShare = WxShare.getInstance(DoctorZoneActivity.this);
                iWxShare.wxShare(1);
                //wxShare(1);
            }

            @Override
            public void friend() {
                IWxShare iWxShare = WxShare.getInstance(DoctorZoneActivity.this);
                iWxShare.wxShare(0);
                //wxShare(0);
            }

            @Override
            public void dismiss() {
            }
        });

    }

    public void shareClick(View view) {
        popup.showAtLocation(viewParent, Gravity.BOTTOM, 0, 0);
    }

    public void caseDetailClick(View view) {
        startActivity(UploadImageActivity.class);
    }


    @Override
    public void setListener() {
        tv_talk.setOnClickListener(this);
        tv_zone.setOnClickListener(this);
    }

    private void initViews() {
        viewParent = (LinearLayout) findViewById(R.id.lyt_my_invite);
        tv_zone = (TextView) findViewById(R.id.tv_zone);
        tv_talk = (TextView) findViewById(R.id.tv_talk);
        //AssetManager mgr = getAssets();//得到AssetManager
        //Typeface tf = Typeface.createFromAsset(mgr, "tvfont.ttf");//根据路径得到Typeface
        //tv_zone.setTypeface(tf);
        //tv_talk.setTypeface(tf);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_talk:
                setTabSelection(0);
                break;
            case R.id.tv_zone:
                setTabSelection(1);
                break;
            default:
                break;
        }
    }

    private void setTabSelection(int index) {

        clearSelection();

        // hideFragments(transaction);
        switch (index) {
            case 0:

                tv_talk.setTextColor(getResources().getColor(R.color.color_white));
                tv_zone.setTextColor(getResources().getColor(R.color.blue));
                tv_zone.setBackgroundResource(R.drawable.doctor_zone_normal_bg);
                tv_talk.setBackgroundResource(R.drawable.doctor_talk_press_bg);
                if (fragment_doctor == null) {

                    fragment_doctor = new DoctorTalkFragment();
                }
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, fragment_doctor, "fragment_docotorOption");
                transaction.commit();

                break;
            case 1:
                tv_talk.setTextColor(getResources().getColor(R.color.blue));
                tv_zone.setTextColor(getResources().getColor(R.color.color_white));

                tv_talk.setBackgroundResource(R.drawable.doctor_talk_normal_bg);
                tv_zone.setBackgroundResource(R.drawable.doctor_zone_press_bg);
                if (fragment_history == null) {
                    fragment_history = new DoctorZoneFragment();
                }
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, fragment_history, "fragment_historyOption");
                transaction.commit();
                break;
        }
    }

    private void clearSelection() {

        tv_talk.setTextColor(getResources().getColor(R.color.text_color_lowlight));

        tv_zone.setTextColor(getResources().getColor(R.color.text_color_lowlight));
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fragment_doctor != null) {
            transaction.hide(fragment_doctor);
        }
        if (fragment_history != null) {
            transaction.hide(fragment_history);
        }
    }


}
