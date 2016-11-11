package com.xiaowei.android.wht.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.Area;

public class MyAreaAdapter extends BaseAdapter {

	List<Area> list = new ArrayList<Area>();

	private LayoutInflater mInflater = null;

	public MyAreaAdapter(Context context)
	{
		this.mInflater = LayoutInflater.from(context);
	}

	public void setList(List<Area> list)
	{
		if(list!=null){
			this.list = list;
			notifyDataSetChanged();
		}
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
			convertView = mInflater.inflate(R.layout.item_area_city_area_listview, null);
			holder.tvText = (TextView)convertView.findViewById(R.id.tv_item_area_city_area_listview);

			convertView.setTag(holder);
		}else
		{
			holder = (ViewHolder)convertView.getTag();
		}

		if(getCount()>0)
		{
			String detail = list.get(position).getAreaname();
			holder.tvText.setText(detail);
		}

		return convertView;
	}

	class ViewHolder
	{
		public TextView tvText;
	}


}
