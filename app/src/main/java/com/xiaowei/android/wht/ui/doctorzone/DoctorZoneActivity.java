package com.xiaowei.android.wht.ui.doctorzone;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.utils.mToast;
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
                wxShare(1);
            }

            @Override
            public void friend() {
                wxShare(0);
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
        startActivity(CaseDetailActivity.class);
    }

    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     *
     * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wxShare(int flag) {
        if (!ApplicationTool.wxApi.isWXAppInstalled()) {
            //提醒用户没有按照微信
            mToast.showToast(this, "没有安装微信");
            return;
        }
        ApplicationTool.isWxShare = true;
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://fir.im/4d53";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "华佗来了";
        msg.description = "您的私人医生！";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.app_share);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        ApplicationTool.wxApi.sendReq(req);
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
        AssetManager mgr = getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "tvtype.ttf");//根据路径得到Typeface
        tv_zone.setTypeface(tf);
        tv_talk.setTypeface(tf);
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
