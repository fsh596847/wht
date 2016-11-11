package com.xiaowei.android.wht.ui.patient;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaowei.android.wht.R;

public class PopupDialog {
	public interface OnResult{
		public void onDailogReturn(int resultCode,Object data );
	}
	AlertDialog inputDialog;
	@SuppressWarnings("deprecation")
	public void showTextInputDialog(final Activity act,String title,String old,final OnResult onResult ){
		final View contentV = act.getLayoutInflater().inflate(R.layout.dialog_text_input,null);
		AlertDialog.Builder builder = new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT);
		inputDialog = builder.create();
		inputDialog.setView(contentV);
		final EditText et = (EditText)contentV.findViewById(R.id.info_editText);
		et.setText(old==null?"":old);
		final TextView title_textView = (TextView)contentV.findViewById(R.id.title_textView);
		title_textView.setText(title==null?"":title);
		contentV.setBackgroundDrawable(null);
		contentV.findViewById(R.id.yes_textView).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (onResult != null){
					EditText et = (EditText)contentV.findViewById(R.id.info_editText);
					String s = et.getText().toString();
					onResult.onDailogReturn(Activity.RESULT_OK,s);
				}
				InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);  
//				boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开 
//				if (isOpen){
//					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
//				}
				imm.hideSoftInputFromWindow(et.getWindowToken(), 0); //强制隐藏键盘  
				inputDialog.dismiss();
			}
		});
		contentV.findViewById(R.id.cancel_textView).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (onResult != null){
					onResult.onDailogReturn(Activity.RESULT_CANCELED,null);
				}
				InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);  
//				boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开 
//				if (isOpen){
//					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
//				}
				imm.hideSoftInputFromWindow(et.getWindowToken(), 0); //强制隐藏键盘  
				inputDialog.dismiss();
			}
		});
		//透明
		Window window = inputDialog.getWindow(); 
		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.alpha = 0.9f;
		window.setAttributes(lp);
		inputDialog.show();
	}
	AlertDialog msgDialog;
	@SuppressWarnings("deprecation")
	public void showMessageDialog(final Activity act,String msg ){
		final View contentV = act.getLayoutInflater().inflate(R.layout.dialog_message,null);
		AlertDialog.Builder builder = new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT);
		msgDialog = builder.create();
		msgDialog.setView(contentV);
		
		final TextView msg_textView = (TextView)contentV.findViewById(R.id.msg_textView);
		msg_textView.setText(msg);
		contentV.setBackgroundDrawable(null);
		msgDialog.setCancelable(true);
		msgDialog.setCanceledOnTouchOutside(true);
		//透明
		Window window = msgDialog.getWindow(); 
		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.alpha = 0.9f;
		window.setAttributes(lp);
		msgDialog.show();
	}
	AlertDialog radiaoSexDialog;
	@SuppressWarnings("deprecation")
	public void showStringRadioSelectDialog(Activity act,String currentKey,final OnResult onResult ){
		final View contentV = act.getLayoutInflater().inflate(R.layout.dialog_listview4sex,null);
		AlertDialog.Builder builder = new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT);
		radiaoSexDialog = builder.create();
		radiaoSexDialog.setView(contentV);
		contentV.setBackgroundDrawable(null);
		ListView listView = (ListView) contentV.findViewById(R.id.listView);
		StringListAdapter adapter = new StringListAdapter(act);
		int currentIndex = -1;
		if ("0".equals(currentKey) || "男".equals(currentKey)){
			currentIndex = 0;
		}else if ("1".equals(currentKey)){
			currentIndex = 1;
		}
		adapter.setSelectedIndex(currentIndex);
//		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				if (onResult != null){
					onResult.onDailogReturn(Activity.RESULT_OK, position==0?"0":"1");
				}
				if (radiaoSexDialog != null && radiaoSexDialog.isShowing()){
					radiaoSexDialog.dismiss();
				}
			}
		});
		//透明
		Window window = radiaoSexDialog.getWindow(); 
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.height = 100;
//		lp.alpha = 0.9f;
		window.setAttributes(lp);
		radiaoSexDialog.show();
	}
	
	class StringListAdapter extends BaseAdapter{
		List<String> items ;
		int selectedIndex;
		private LayoutInflater mInflater = null;

		public StringListAdapter(Context context) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			items = new ArrayList<String>();
			items.add("男");
			items.add("女");
		}

		public void setSelectedIndex(int selectedIndex){
			this.selectedIndex = selectedIndex;
		}
		@Override
		public int getCount() {
			if (null != items) {
				return items.size();
			} else {
				return 0;
			}
		}


		

		@Override
		public String getItem(int arg0) {
			if (items != null) {
				return items.get(arg0);
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup arg2) {
			ViewHolder h;
			if (null == v) {
				h = new ViewHolder();
				v = mInflater
						.inflate(R.layout.dialog_listview_adapter, null);
				h.textView = (TextView) v
						.findViewById(R.id.textView);
				v.setTag(h);
			} else {
				h = (ViewHolder) v.getTag();
			}
			final String o = items.get(position);
			if (o != null){
				h.textView.setText(o);
			}
			if (position == selectedIndex){
				h.textView.setBackgroundResource(R.color.color_major);
			}else{
				h.textView.setBackgroundResource(R.color.color_transparent);
			}
			return v;
		}
	}
	public static void showDialog(Dialog d){
		if (d != null && !d.isShowing()){
			d.show();
		}
	}
	public static void hideDialog(Dialog d){
		if (d != null && d.isShowing()){
			d.hide();
		}
	}
	class ViewHolder {
		TextView textView;
	}
}
