package com.xiaowei.android.wht.ui.doctorzone;

import android.os.Bundle;
import android.view.View;

import com.xiaowei.android.wht.R;

/**
 * Created by fsh on 2016/11/12.   病例详情
 */

public class CaseDetailActivity extends BaseActivity {
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_case);
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @Override
    public void setListener() {

    }

    public void backClick(View view) {
        finish();
    }
}
