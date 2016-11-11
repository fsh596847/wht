package com.xiaowei.android.wht.views;

import java.util.ArrayList;
import java.util.List;

import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.Area;
import com.xiaowei.android.wht.utils.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MyCityAdapter extends BaseAdapter {

	List<Area> list = new ArrayList<Area>();
	List<Area> listItem = new ArrayList<Area>();
	int itemPostion = -1;
	AreaChooseCallBack areaChooseCallBack;
	
	private LayoutInflater mInflater = null;
	private Context context;
	
	public MyCityAdapter(Context context,List<Area> list)
    {
		this.context = context;
        this.mInflater = LayoutInflater.from(context);
        if(list!=null){
        	this.list = list;
        }
    }
	
	public void setList(List<Area> list)
    {
        if(list!=null){
        	itemPostion = -1;
        	this.list = list;
        	notifyDataSetChanged();
        }
    }
	
	public void setListItem(List<Area> listItem, int itemPostion)
    {
        if(list!=null){
        	this.listItem = listItem;
        	this.itemPostion = itemPostion;
        	notifyDataSetChanged();
        }
    }
	
	public void setAreaChooseCallBack(AreaChooseCallBack areaChooseCallBack){
		this.areaChooseCallBack = areaChooseCallBack;
	}
    
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
	        convertView = mInflater.inflate(R.layout.item_area_city_listview, null);
	        holder.tvText = (TextView)convertView.findViewById(R.id.tv_item_area_city_listview);
	        holder.listView = (ListView) convertView.findViewById(R.id.listView_item_area_city);
	        holder.areaAdapter = new MyAreaAdapter(context);
	        holder.listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					areaChooseCallBack.areaChooseCallBack(arg2);
				}
			});
	        convertView.setTag(holder);
	    }else
	    {
	        holder = (ViewHolder)convertView.getTag();
	    }
	    
	    if(getCount()>0)
	    {
	    	String detail = list.get(position).getAreaname();
	    	holder.tvText.setText(detail);
	    	
	    	if(itemPostion == position){
	    		holder.listView.setVisibility(View.VISIBLE);
	    		holder.listView.setAdapter(holder.areaAdapter);
	    		holder.areaAdapter.setList(listItem);
	    		Util.setListViewHeightBasedOnChildren(holder.listView);
	    	}
	    	else{
	    		holder.listView.setVisibility(View.GONE);
	    	}
	    }
	                                                                                                 
	    return convertView;
	}
	
	class ViewHolder
	{
		public TextView tvText;
		public ListView listView;
		public MyAreaAdapter areaAdapter;
	}
	
	public interface AreaChooseCallBack {   
	    void areaChooseCallBack(int position);   
	} 

}
