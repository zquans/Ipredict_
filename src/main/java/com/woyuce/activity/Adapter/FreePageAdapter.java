package com.woyuce.activity.Adapter;

import java.util.List;

import com.woyuce.activity.Bean.FreePage;
import com.woyuce.activity.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FreePageAdapter extends BaseAdapter{

	private List<FreePage> mList;
	private LayoutInflater mInflater;
	
	
	public FreePageAdapter(Context context, List<FreePage> data) {
		mList = data;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.gvitem_freepage, null);
			viewHolder.txtpage = (TextView) convertView.findViewById(R.id.txt_pageno);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txtpage.setBackgroundColor( Color.parseColor(mList.get(position).sub_color));   //   Color.parseColor(localPage.tag)��ɫ�����Ĺؼ�����
		viewHolder.txtpage.setText(mList.get(position).sub_name);
		return convertView;
	}
	
	class ViewHolder{
		public TextView txtpage;
	}
}
