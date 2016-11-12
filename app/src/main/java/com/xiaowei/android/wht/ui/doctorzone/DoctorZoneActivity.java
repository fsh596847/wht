package com.xiaowei.android.wht.ui.doctorzone;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;

/*
*  医生社区
* */
public class DoctorZoneActivity extends BaseActivity implements View.OnClickListener {

    DoctorTalkFragment fragment_doctor;
    DoctorZoneFragment fragment_history;
    private TextView tv_zone;
    private TextView tv_talk;

    private FragmentManager fragmentManager;
    FragmentTransaction transaction;

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

    }

    @Override
    public void setListener() {
        tv_talk.setOnClickListener(this);
        tv_zone.setOnClickListener(this);
    }

    private void initViews() {
        tv_zone = (TextView) findViewById(R.id.tv_zone);
        tv_talk = (TextView) findViewById(R.id.tv_talk);

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
