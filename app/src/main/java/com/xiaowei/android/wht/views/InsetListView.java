package com.xiaowei.android.wht.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class InsetListView extends ListView {

	public InsetListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public InsetListView  (Context context) {
		super(context);
	}

	public  InsetListView  (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	//很重要
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}


	@Override
	public void setOnItemClickListener(
			android.widget.AdapterView.OnItemClickListener listener) {
		super.setOnItemClickListener(listener);
	}

}
