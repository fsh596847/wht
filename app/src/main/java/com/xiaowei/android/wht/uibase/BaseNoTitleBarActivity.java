package com.xiaowei.android.wht.uibase;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
/**
 * 没有标题栏的基类
 * @author Administrator
 *
 */
public class BaseNoTitleBarActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

}
