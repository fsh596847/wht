package com.xiaowei.android.wht.ui.doctorzone;

import android.MyBaseAdapterHelper;
import android.MyQuickAdapter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.ui.doctorzone.able.IWxShare;
import com.xiaowei.android.wht.ui.doctorzone.able.WxShare;
import com.xiaowei.android.wht.utis.DisplayUtil;
import com.xiaowei.android.wht.views.SharePopupwindow;
import java.util.ArrayList;
import java.util.List;

/*
*  Created by fsh on 2016/11/12.  医生社区
* */
public class DoctorZoneActivity extends BaseActivity implements View.OnClickListener {

  DoctorTalkFragment fragment_doctor;
  DoctorZoneFragment fragment_history;
  private TextView tv_zone;
  private TextView tv_talk;
  private LinearLayout lytCase;

  private FragmentManager fragmentManager;
  FragmentTransaction transaction;

  private SharePopupwindow popup;
  private RelativeLayout viewParent;

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

  public void backClick(View view) {
    finish();
  }

  public static String INTENT_KEY_TYPE_ISSUE = "1";//求助
  public static String INTENT_KEY_TYPE_SHARE = "0";// 分享

  /**
   * 分享
   */
  public void shareClick(View view) {
    //popup.showAtLocation(viewParent, Gravity.BOTTOM, 0, 0);
    IssueActivity.getIntent(activity, INTENT_KEY_TYPE_SHARE);
  }

  /**
   * 发布
   */
  public void issueClick(View view) {

    IssueActivity.getIntent(activity, INTENT_KEY_TYPE_ISSUE);
  }

  public void caseDetailClick(View view) {
    showPopMenu(view);
    //if (lytCase.getVisibility() == View.VISIBLE) {
    //  lytCase.setVisibility(View.GONE);
    //} else {
    //  lytCase.setVisibility(View.VISIBLE);
    //}
  }

  @Override
  public void setListener() {
    tv_talk.setOnClickListener(this);
    tv_zone.setOnClickListener(this);
  }

  private void initViews() {
    viewParent = (RelativeLayout) findViewById(R.id.lyt_my_invite);
    tv_zone = (TextView) findViewById(R.id.tv_zone);
    tv_talk = (TextView) findViewById(R.id.tv_talk);
    lytCase = (LinearLayout) findViewById(R.id.lyt_share);
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

  //=============================POPUWINDOW=====================================================

  private static class PopMenuBean {
    String name;
    String id;

    public PopMenuBean(String name, String id) {
      this.name = name;
      this.id = id;
    }
  }

  private PopupWindow popupWindow;
  private View rootView;
  private List<PopMenuBean> data = new ArrayList<PopMenuBean>() {
    {
      add(new PopMenuBean("分享", INTENT_KEY_TYPE_SHARE));
      add(new PopMenuBean("求助", INTENT_KEY_TYPE_ISSUE));
    }
  };

  private MyQuickAdapter<PopMenuBean> mPopMenuAdapter;
  private ListView mPopMenuListView;

  private void showPopMenu(View view) {
    if (rootView == null) {
      rootView = LayoutInflater.from(getApplicationContext())
          .inflate(R.layout.pop_menu_doctor_dynamic_list_item, null);
    }

    if (mPopMenuListView == null) {
      mPopMenuListView = (ListView) rootView.findViewById(R.id.pop_menu_doctor_dynamic_list_view);
    }

    if (mPopMenuAdapter == null) {
      mPopMenuAdapter = initPopMenuListAdapter();
      mPopMenuListView.setAdapter(mPopMenuAdapter);
      mPopMenuAdapter.clear();
      mPopMenuAdapter.addAll(data);
    }

    mPopMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (data.get(position).id.equals(INTENT_KEY_TYPE_ISSUE)) {
          IssueActivity.getIntent(activity, INTENT_KEY_TYPE_ISSUE);
        } else {
          IssueActivity.getIntent(activity, INTENT_KEY_TYPE_SHARE);
        }
        popupWindow.dismiss();
      }
    });

    if (popupWindow == null) {
      //popupWindow = new PopupWindow(measureContentWidth(mPopMenuAdapter),
      //    WindowManager.LayoutParams.WRAP_CONTENT);
      popupWindow = new PopupWindow(WindowManager.LayoutParams.WRAP_CONTENT,
          WindowManager.LayoutParams.WRAP_CONTENT);
      //popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
      //    ScreenUtils.getScreenHeight(activity) / 2);
    }

    popupWindow.setContentView(rootView);
    popupWindow.setFocusable(true);
    popupWindow.setOutsideTouchable(true);
    popupWindow.setBackgroundDrawable(new ColorDrawable(0x000000));
    popupWindow.setAnimationStyle(R.style.AnimationPopup);

    //View titleView = findViewById(R.id.base_title_layout);
    popupWindow.showAsDropDown(view, -DisplayUtil.dp2Px_Int(50, getApplicationContext()),
        -DisplayUtil.dp2Px_Int(10, getApplicationContext()));
    //popupWindow.showAtLocation(titleView, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0,
    //    titleView.getHeight() + getWindow().getDecorView().getHeight() - rootLayout.getHeight());
  }

  private MyQuickAdapter<PopMenuBean> initPopMenuListAdapter() {
    return new MyQuickAdapter<PopMenuBean>(getApplicationContext(),
        R.layout.adapter_pop_menu_doctor_dynamic_list_item) {
      @Override protected void convert(MyBaseAdapterHelper helper, PopMenuBean item, int position) {
        helper.setText(R.id.adapter_pop_menu_doctor_dynamic_list_item_name_tv, item.name);

        if (position == 0) {
          helper.setCompoundDrawablesWithIntrinsicBounds(
              R.id.adapter_pop_menu_doctor_dynamic_list_item_name_tv,
              R.drawable.doctorzone_share, 0, 0, 0);
        } else {
          helper.setCompoundDrawablesWithIntrinsicBounds(
              R.id.adapter_pop_menu_doctor_dynamic_list_item_name_tv,
              R.drawable.doctorzone_help, 0, 0, 0);
        }
      }
    };
  }
}
