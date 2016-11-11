package com.xiaowei.android.wht.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;

/**
 * 
 * @author kevin
 * 
 * setResult(RESULTCODE, new Intent().putExtra("result", "返回开户行"),RESULTCODE);//RESULTCODE = 516;
 */
public class BankSelectActivity extends Activity {

	private TextView tvTitle;
	private ListView listView;
	private MyListViewAdapter adapter;
	
	List<String> list = new ArrayList<String>();
	
	public static final int RESULTCODE_BankSelectActivity = 516;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_title);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		initList();

		initViews();

		initListeners();
		
	}

	private void initList() {
		list.add("中国工商银行");
		list.add("中国农业银行");
		list.add("中国建设银行");
		list.add("中国银行");
		list.add("交通银行");
		list.add("招商银行");
		list.add("中信实业银行");
		list.add("上海浦东发展银行");
		list.add("民生银行");
		list.add("光大银行");
		list.add("广东发展银行");
		list.add("兴业银行");
		list.add("华夏银行");
		list.add("上海银行");
		list.add("北京银行");
		list.add("深圳发展银行");
		list.add("深圳市商业银行");
		list.add("天津市商业银行");
		list.add("广州市商业银行");
		list.add("杭州市商业银行");
		list.add("南京市商业银行");
		list.add("东莞市商业银行");
		list.add("宁波市商业银行");
		list.add("无锡市商业银行");
		list.add("恒丰银行");
		list.add("武汉市商业银行");
		list.add("长沙市商业银行");
		list.add("大连市商业银行");
		list.add("西安市商业银行");
		list.add("重庆市商业银行");
		list.add("济南市商业银行");
		list.add("成都市商业银行");
		list.add("贵阳市商业银行");
		list.add("石家庄市商业银行");
		list.add("昆明市商业银行");
		list.add("烟台市商业银行");
		list.add("哈尔滨市商业银行");
		list.add("郑州市商业银行");
		list.add("乌鲁木齐市商业银行");
		list.add("青岛市商业银行");
		list.add("温州市商业银行");
		list.add("合肥市商业银行");
		list.add("淄博市商业银行");
		list.add("苏州市商业银行");
		list.add("太原市商业银行");
		list.add("绍兴市商业银行");
		list.add("台州市商业银行");
		list.add("浙商银行");
		list.add("临沂市商业银行");
		list.add("鞍山市商业银行");
		list.add("潍坊市商业银行");
	}

	private void initListeners() {
		//返回
		findViewById(R.id.iv_job_title_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
		
		//
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				setResult(RESULTCODE_BankSelectActivity, new Intent().putExtra("result", list.get(arg2)));
				finish();
			}
		});

	}

	private void initViews() {
		tvTitle = (TextView) findViewById(R.id.tv_job_title);
		tvTitle.setText("开户行");
		listView = (ListView) findViewById(R.id.listview_job_title);
		adapter = new MyListViewAdapter(getApplicationContext(), list);
		listView.setAdapter(adapter);
	}

	boolean isDestroy = false;
	@Override
	public void onStart() {
		super.onStart();
		isDestroy = false;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroy = true;
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	
	/*private void queryBank(){
		reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					String s = DataService.queryPostdoc(BankSelectActivity.this);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if(hr != null){
							if(hr.isSuccess()){
								if(!isDestroy){

									com.alibaba.fastjson.JSONArray array = JSON.parseArray(hr.getData().toString());
									for (int i = 0; i < array.size(); i++) {
										list.add(array.getString(i));
									}
									//mLog.d("http", "data:"+JSON.parseArray(hr.getData().toString()).toString());
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											adapter.setList(list);
										}  
									});
								}
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				closeLoadingDialog();

			}
		}).start();
	}*/


	/*private void reload(String text){
		if (loadingDialog == null){
			loadingDialog = Utils.createLoadingDialog(this, text);
		}
		if (!loadingDialog.isShowing()){
			loadingDialog.show();
		}
	}

	private Dialog loadingDialog = null;
	private void closeLoadingDialog() {
		if(null != loadingDialog) {	 
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}*/
	
	private class MyListViewAdapter extends BaseAdapter {
		List<String> list = new ArrayList<String>();
		
		private LayoutInflater mInflater = null;
		
		private MyListViewAdapter(Context context,List<String> list)
	    {
	        this.mInflater = LayoutInflater.from(context);
	        if(list!=null){
	        	this.list = list;
	        }
	    }
		
		/*private void setList(List<String> list)
	    {
	        if(list!=null){
	        	this.list = list;
	        	notifyDataSetChanged();
	        }
	    }*/
	    
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			
			return list.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder;
		    if(convertView == null)
		    {
		        holder = new ViewHolder();
		        convertView = mInflater.inflate(R.layout.item_job_listview, null);
		        holder.tvText = (TextView)convertView.findViewById(R.id.tv_item_job_listview);
		        
		        convertView.setTag(holder);
		    }else
		    {
		        holder = (ViewHolder)convertView.getTag();
		    }
		    
		    if(getCount()>0)
		    {
		    	String detail = list.get(position);
		    	holder.tvText.setText(detail);
		    }
		                                                                                                 
		    return convertView;
		}
		
		class ViewHolder
		{
			public TextView tvText;
		}

	}

}
