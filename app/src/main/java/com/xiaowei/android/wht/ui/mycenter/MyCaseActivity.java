package com.xiaowei.android.wht.ui.mycenter;

import android.os.Bundle;
import android.view.View;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.ui.doctorzone.BaseActivity;

/**
 * Created by HIPAA on 2016/12/16. 我的病例
 */

public class MyCaseActivity extends BaseActivity {

  @Override protected void setContentView() {
    setContentView(R.layout.activity_mycase);
  }

  @Override public void init(Bundle savedInstanceState) {

  }

  @Override public void setListener() {

  }

  public void backClick(View view) {
    finish();
  }

  public void shareClick(View view) {
    startActivity(ShareCaseActivity.class);
  }

  public void helpClick(View view) {
    startActivity(HelpCaseActivity.class);
  }

  public void saveClick(View view) {
    startActivity(SaveCaseActivity.class);
  }

  public void MycommentClick(View view) {
    startActivity(MyCommentActivity.class);
  }

  public void pleaseCommentClick(View view) {
    startActivity(InviteCommentActivity.class);
  }

  public void tipClick(View view) {
    startActivity(PayCaseActivity.class);
  }
}
