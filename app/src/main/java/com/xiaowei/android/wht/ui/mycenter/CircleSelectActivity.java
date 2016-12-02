//package com.xiaowei.android.wht.ui.mycenter;
//
//import android.app.Activity;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import com.xiaowei.android.wht.ApplicationTool;
//import com.xiaowei.android.wht.R;
//import com.xiaowei.android.wht.views.TextFont;
//
///**
// * @author kevin getIntent().getIntExtra("RESULTCODE", -1); 511;//职称 、512;//职务 setResult(RESULTCODE,
// *         new Intent().putExtra("result", "返回选择"));
// */
//public class CircleSelectActivity extends Activity implements OnClickListener {
//
//  private TextFont btnLeft, btnRight;
//  private CircleCompanyFragment fEngineerHome;
//  private CircleOrgnizeFragment fEngineerMy;
//  private FragmentManager manager;
//  private FragmentTransaction transaction;
//
//  private TextFont tvTitle;
//
//  private static final int RESULTCODE_JOB_TITLE = 511;//职称
//  private static final int RESULTCODE_POST_TITLE = 512;//职务
//  private static final int RESULTCODE_CIRCLE_TITLE = 513;//圈子
//  private int RESULTCODE;
//
//  @Override
//  protected void onCreate(Bundle savedInstanceState) {
//    requestWindowFeature(Window.FEATURE_NO_TITLE);
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_circle);
//    ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
//    btnLeft = (TextFont) findViewById(R.id.tv_company);
//    btnRight = (TextFont) findViewById(R.id.tv_organize);
//
//    btnLeft.setOnClickListener(this);
//    btnRight.setOnClickListener(this);
//    manager = getFragmentManager();
//    transaction = manager.beginTransaction();
//    fEngineerHome = new CircleCompanyFragment();
//    //transaction.replace(R.id.frame_main, fEngineerHome);
//    btnLeft.setTextColor(getResources().getColor(R.color.blue));
//    btnRight.setTextColor(getResources().getColor(R.color.color_gray));
//    //hideFragment(transaction);
//    //fEngineerHome = new CircleCompanyFragment();
//    //transaction.add(R.id.fl_engineer_main,fEngineerHome);
//    //transaction.show(fEngineerHome);
//    transaction.commit();
//  }
//
//  /*
//  * 去除（隐藏）所有的Fragment
//  * */
//  private void hideFragment(FragmentTransaction transaction) {
//    if (fEngineerHome != null) {
//      //transaction.hide(fEngineerHome);
//      transaction.remove(fEngineerHome);
//    }
//
//    if (fEngineerMy != null) {
//      //transaction.hide(fEngineerMy);
//      transaction.remove(fEngineerMy);
//    }
//  }
//
//  // Press the back button in mobile phone
//  @Override
//  public void onBackPressed() {
//    super.onBackPressed();
//    overridePendingTransition(0, R.anim.out_right);
//  }
//
//  boolean isDestroy = false;
//
//  @Override
//  public void onStart() {
//    super.onStart();
//    isDestroy = false;
//  }
//
//  @Override
//  public void onDestroy() {
//    super.onDestroy();
//    isDestroy = true;
//    ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
//  }
//
//  @Override public void onClick(View v) {
//    transaction = manager.beginTransaction();
//
//    switch (v.getId()) {
//      case R.id.tv_company:
//        /**
//         * 为了防止重叠，需要点击之前先移除其他Fragment
//         */
//        btnLeft.setTextColor(getResources().getColor(R.color.blue));
//        btnRight.setTextColor(getResources().getColor(R.color.color_gray));
//        hideFragment(transaction);
//        if (fEngineerHome == null) {
//          fEngineerHome = new CircleCompanyFragment();
//        }
//        //transaction.add(R.id.fl_engineer_main,fEngineerHome);
//        //transaction.show( fEngineerHome);
//        transaction.replace(R.id.frame_main, fEngineerHome);
//        transaction.commit();
//
//        break;
//
//      case R.id.tv_organize:
//        btnLeft.setTextColor(getResources().getColor(R.color.color_gray));
//        btnRight.setTextColor(getResources().getColor(R.color.blue));
//        hideFragment(transaction);
//        if (fEngineerMy == null) {
//          fEngineerMy = new CircleOrgnizeFragment();
//        }
//        //transaction.add(R.id.fl_engineer_main,fEngineerMy);
//        //transaction.show( fEngineerMy);
//        transaction.replace(R.id.frame_main, fEngineerMy);
//        transaction.commit();
//        break;
//
//      default:
//        break;
//    }
//  }
//}
